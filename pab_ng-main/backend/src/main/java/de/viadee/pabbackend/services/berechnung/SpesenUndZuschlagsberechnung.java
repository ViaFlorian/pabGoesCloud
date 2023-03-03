package de.viadee.pabbackend.services.berechnung;

import de.viadee.pabbackend.entities.AbstractSpesenUndZuschlagsberechnungsErgebnis;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.ErgebnisBerechnungGesetzlicheSpesen;
import de.viadee.pabbackend.entities.ErgebnisBerechnungViadeeZuschlaege;
import de.viadee.pabbackend.entities.GesetzlicheSpesenKonstante;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.ViadeeZuschlaege;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.zeitrechnung.AbwesenheitenBerechner;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpesenUndZuschlagsberechnung {

  private final ParameterService parameterService;

  private final KonstantenService konstantenService;

  private final Zeitrechnung zeitrechnung;

  private final AbwesenheitenBerechner abwesenheitenBerechner;

  private final ProjektService projektService;

  public SpesenUndZuschlagsberechnung(ParameterService parameterService,
      KonstantenService konstantenService, Zeitrechnung zeitrechnung,
      AbwesenheitenBerechner abwesenheitenBerechner, ProjektService projektService) {
    this.parameterService = parameterService;
    this.konstantenService = konstantenService;
    this.zeitrechnung = zeitrechnung;
    this.abwesenheitenBerechner = abwesenheitenBerechner;
    this.projektService = projektService;
  }

  public List<ErgebnisBerechnungGesetzlicheSpesen> berechneGesetzlicheSpesen(
      final List<Abwesenheit> abwesenheiten) {

    final List<ErgebnisBerechnungGesetzlicheSpesen> berechnungsErgebnisListe = new ArrayList<>();
    final List<GesetzlicheSpesenKonstante> gesetzlicheSpesenKonfiguration = konstantenService.gesetzlicheSpesen();

    abwesenheiten.stream().forEach(abwesenheit -> {

      final ErgebnisBerechnungGesetzlicheSpesen spesenFuerAbwesenheitstag = findeBestehendesOderErstelleNeuesSpesenErgebnisobjekt(
          berechnungsErgebnisListe, abwesenheit);

      ermittleUndSetzeAnzahlStundenFuerSpesenAusAbwesenheit(abwesenheit, spesenFuerAbwesenheitstag);

      setzeFruehstueckFlag(abwesenheit, spesenFuerAbwesenheitstag);

      setzeMittagessenFlag(abwesenheit, spesenFuerAbwesenheitstag);

      setzeAbendessenFlag(abwesenheit, spesenFuerAbwesenheitstag);

      setzeAnOderAbreiseFlag(abwesenheiten, abwesenheit, spesenFuerAbwesenheitstag);

      final BigDecimal spesenEntsprechendDerAbwesenheitsstunden = ermittleSpesenEntsprechendDerAbwesenheitsstunden(
          spesenFuerAbwesenheitstag.getAnzahlStunden(),
          spesenFuerAbwesenheitstag.istAnreiseOderAbreiseTag(), gesetzlicheSpesenKonfiguration);

      spesenFuerAbwesenheitstag.setGesetzlicheSpesen(
          spesenEntsprechendDerAbwesenheitsstunden.subtract(abzugFuerFruehstuckBeiUebernachtung(
                  spesenFuerAbwesenheitstag.istMitFruehstueck()))
              .subtract(abzugFuerMittagessen(spesenFuerAbwesenheitstag.istMitMittagessen()))
              .subtract(abzugFuerAbendessen(spesenFuerAbwesenheitstag.istMitAbendessen())));

      if (spesenFuerAbwesenheitstag.getGesetzlicheSpesen().compareTo(BigDecimal.ZERO) == -1) {
        spesenFuerAbwesenheitstag.setGesetzlicheSpesen(BigDecimal.ZERO);
      }

      ordneErgebnisAbwesenheitZu(spesenFuerAbwesenheitstag, abwesenheit);

    });

    return berechnungsErgebnisListe;

  }

  private void ordneErgebnisAbwesenheitZu(
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnis,
      final Abwesenheit neueAbwesenheit) {
    List<Abwesenheit> vorhandeneAbwesenheiten = ermittleVorhandeneAbwesenheiten(ergebnis,
        neueAbwesenheit);
    List<Abwesenheit> seminare = ermittleSeminare(vorhandeneAbwesenheiten);
    List<Abwesenheit> abwesenheitenMitDreimonatsregeln = ermittleAbwesenheitenMitDreimonatsregeln(
        vorhandeneAbwesenheiten);

    // Es ist für einen Abwesenheitstag noch kein Abwesenheit-Objekt hinterlegt
    if (vorhandeneAbwesenheiten.size() == 1) {
      ergebnis.setAbwesenheit(neueAbwesenheit);
      // Beide Abwesenheiten sind Seminare
    } else if (seminare.size() == 2) {
      weiseErgebnisObjektAbwesenheitMitHoechsterStundenzahlZu(ergebnis, seminare);
      // Für beide Abwesenheiten trifft die Dreimonatsregel zu
    } else if (abwesenheitenMitDreimonatsregeln.size() == 2) {
      weiseErgebnisObjektAbwesenheitMitHoechsterStundenzahlZu(ergebnis,
          abwesenheitenMitDreimonatsregeln);
      // Eine Abwesenheit ist ein Seminar, für die andere gilt eine Dreimonatsregel
    } else if (seminare.size() == 1 && abwesenheitenMitDreimonatsregeln.size() == 1) {
      ergebnis.setAbwesenheit(abwesenheitenMitDreimonatsregeln.get(0));
      // Eine Abwesenheit ist ein Seminar, die andere ist eine normale projektbedingte Abwesenheit,
      // für die die Dreimonatsregel nicht greift
    } else if (seminare.size() == 1 && abwesenheitenMitDreimonatsregeln.size() == 0) {
      List<Abwesenheit> zuSetzendeAbwesenheiten = new ArrayList<>(vorhandeneAbwesenheiten);
      zuSetzendeAbwesenheiten.removeAll(seminare);
      ergebnis.setAbwesenheit(zuSetzendeAbwesenheiten.get(0));
      // Bei einer Abwesenheit greift die Dreimonatsregel, die andere Abwesenheit ist eine normale
      // projektbedingte Abwesenheit
    } else if (seminare.size() == 0 && abwesenheitenMitDreimonatsregeln.size() == 1) {
      List<Abwesenheit> zuSetzendeAbwesenheiten = new ArrayList<>(vorhandeneAbwesenheiten);
      zuSetzendeAbwesenheiten.removeAll(abwesenheitenMitDreimonatsregeln);
      ergebnis.setAbwesenheit(zuSetzendeAbwesenheiten.get(0));
      // Beide Abwesenheiten sind normale projektbedingte Abwesenheiten
    } else {
      weiseErgebnisObjektAbwesenheitMitHoechsterStundenzahlZu(ergebnis, vorhandeneAbwesenheiten);
    }

  }

  private void weiseErgebnisObjektAbwesenheitMitHoechsterStundenzahlZu(
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnis,
      final List<Abwesenheit> abwesenheiten) {
    if (anzahlStunden(abwesenheiten.get(0)).compareTo(anzahlStunden(abwesenheiten.get(1))) < 0) {
      ergebnis.setAbwesenheit(abwesenheiten.get(1));
    } else {
      ergebnis.setAbwesenheit(abwesenheiten.get(0));
    }
  }

  private List<Abwesenheit> ermittleAbwesenheitenMitDreimonatsregeln(
      final List<Abwesenheit> vorhandeneAbwesenheiten) {
    List<Abwesenheit> abwesenheitenMitDreimonatsregeln = new ArrayList<>();
    for (Abwesenheit abwesenheit : vorhandeneAbwesenheiten) {
      if (abwesenheit.isDreiMonatsRegelAktiv()) {
        abwesenheitenMitDreimonatsregeln.add(abwesenheit);
      }
    }
    return abwesenheitenMitDreimonatsregeln;
  }

  private List<Abwesenheit> ermittleSeminare(final List<Abwesenheit> vorhandeneAbwesenheiten) {
    List<Abwesenheit> seminare = new ArrayList<>();
    for (Abwesenheit abwesenheit : vorhandeneAbwesenheiten) {
      if (istSeminar(projektService.projektById(abwesenheit.getProjektId()))) {
        seminare.add(abwesenheit);
      }
    }
    return seminare;
  }

  private List<Abwesenheit> ermittleVorhandeneAbwesenheiten(
      final AbstractSpesenUndZuschlagsberechnungsErgebnis spesenFuerAbwesenheitstag,
      final Abwesenheit neueAbwesenheit) {
    List<Abwesenheit> vorhandeneAbwesenheiten = new ArrayList<>();
    if (spesenFuerAbwesenheitstag.getAbwesenheit() != null) {
      vorhandeneAbwesenheiten.add(spesenFuerAbwesenheitstag.getAbwesenheit());
    }
    if (neueAbwesenheit != null) {
      vorhandeneAbwesenheiten.add(neueAbwesenheit);
    }
    return vorhandeneAbwesenheiten;

  }

  private boolean istSeminar(final Projekt projekt) {
    return projekt.getProjektnummer().equals("9002");
  }

  public List<ErgebnisBerechnungViadeeZuschlaege> berechneViadeeZuschlaege(
      final List<Abwesenheit> abwesenheiten) {
    final List<ErgebnisBerechnungViadeeZuschlaege> berechnungsErgebnisListe = new ArrayList<>();
    final List<ViadeeZuschlaege> viadeeZuschlaegeKonfiguration = konstantenService.viadeeZuschlaege();

    abwesenheiten.stream().forEach(abwesenheit -> {

      final ErgebnisBerechnungViadeeZuschlaege viadeeZuschlagFuerAbwesenheitstag = findeBestehendesOderErstelleNeuesZuschlagErgebnisObjekt(
          berechnungsErgebnisListe, abwesenheit);

      ermittleUndSetzeAnzahlStundenFuerZuschlaegeAusAbwesenheit(abwesenheit,
          viadeeZuschlagFuerAbwesenheitstag);

      setzeFruehstueckFlag(abwesenheit, viadeeZuschlagFuerAbwesenheitstag);

      final BigDecimal zuschlaegeEntsprechendDerAbwesenheitsstunden = ermittleZuschlaegeEntsprechendDerAbwesenheitsstunden(
          viadeeZuschlagFuerAbwesenheitstag.getAnzahlStunden(), abwesenheit.isMitUebernachtung(),
          viadeeZuschlaegeKonfiguration);

      viadeeZuschlagFuerAbwesenheitstag.setViadeeZuschlaege(
          zuschlaegeEntsprechendDerAbwesenheitsstunden
              .add(zuschlagFuerFruehstuckBeiUebernachtung(
                  viadeeZuschlagFuerAbwesenheitstag.istMitFruehstueck())));

      ordneErgebnisAbwesenheitZu(viadeeZuschlagFuerAbwesenheitstag, abwesenheit);
    });

    return berechnungsErgebnisListe;

  }

  private void ermittleUndSetzeAnzahlStundenFuerZuschlaegeAusAbwesenheit(
      final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnisObjekt) {

    final BigDecimal anzahlStunden = ergebnisObjekt.getAnzahlStunden() == null ? BigDecimal.ZERO
        : ergebnisObjekt.getAnzahlStunden();

    ergebnisObjekt.setAnzahlStunden(anzahlStunden.add(
        zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
            abwesenheit.getUhrzeitBis())));

  }

  private void ermittleUndSetzeAnzahlStundenFuerSpesenAusAbwesenheit(final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnisObjekt) {

    final BigDecimal anzahlStunden = ergebnisObjekt.getAnzahlStunden() == null ? BigDecimal.ZERO
        : ergebnisObjekt.getAnzahlStunden();
    ergebnisObjekt
        .setAnzahlStunden(
            anzahlStunden.add(zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
                abwesenheit.getUhrzeitBis())));
  }

  private void setzeAnOderAbreiseFlag(final List<Abwesenheit> abwesenheiten,
      final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis eintragZumTagDerAbwesenheit) {
    final boolean istAnreiseOderAbreisetag = eintragZumTagDerAbwesenheit.istAnreiseOderAbreiseTag();
    eintragZumTagDerAbwesenheit.setIstAnreiseOderAbreiseTag(
        istAnreiseOderAbreisetag || abwesenheitenBerechner.istAnreisetag(abwesenheiten, abwesenheit)
            || abwesenheitenBerechner.istAbreisetag(abwesenheiten, abwesenheit));
  }

  private void setzeFruehstueckFlag(final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnisObjekt) {
    final boolean istMitFruehstueck = ergebnisObjekt.istMitFruehstueck();
    ergebnisObjekt.setIstMitFruehstueck(istMitFruehstueck || abwesenheit.isMitFruehstueck());
  }

  private void setzeMittagessenFlag(final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnisObjekt) {
    final boolean istMitMittagessen = ergebnisObjekt.istMitMittagessen();
    ergebnisObjekt.setIstMitMittagessen(istMitMittagessen || abwesenheit.isMitMittagessen());
  }

  private void setzeAbendessenFlag(final Abwesenheit abwesenheit,
      final AbstractSpesenUndZuschlagsberechnungsErgebnis ergebnisObjekt) {
    final boolean istMitAbendessen = ergebnisObjekt.istMitAbendessen();
    ergebnisObjekt.setIstMitAbendessen(istMitAbendessen || abwesenheit.isMitAbendessen());
  }

  private ErgebnisBerechnungGesetzlicheSpesen findeBestehendesOderErstelleNeuesSpesenErgebnisobjekt(
      final List<ErgebnisBerechnungGesetzlicheSpesen> berechnungsErgebnis,
      final Abwesenheit abwesenheit) {
    ErgebnisBerechnungGesetzlicheSpesen eintragZumTagDerAbwesenheit = berechnungsErgebnis
        .stream()
        .filter(andereAbw -> andereAbw.getTag()
            .equals(abwesenheit.getTagVon()))
        .findFirst().orElse(null);
    if (eintragZumTagDerAbwesenheit == null) {
      eintragZumTagDerAbwesenheit = new ErgebnisBerechnungGesetzlicheSpesen();
      eintragZumTagDerAbwesenheit.setTag(abwesenheit.getTagVon());
      berechnungsErgebnis.add(eintragZumTagDerAbwesenheit);
    }
    return eintragZumTagDerAbwesenheit;
  }

  private ErgebnisBerechnungViadeeZuschlaege findeBestehendesOderErstelleNeuesZuschlagErgebnisObjekt(
      final List<ErgebnisBerechnungViadeeZuschlaege> berechnungsErgebnis,
      final Abwesenheit abwesenheit) {
    ErgebnisBerechnungViadeeZuschlaege eintragZumTagDerAbwesenheit = berechnungsErgebnis
        .stream()
        .filter(andereAbw -> andereAbw.getTag()
            .equals(abwesenheit.getTagVon()))
        .findFirst().orElse(null);
    if (eintragZumTagDerAbwesenheit == null) {
      eintragZumTagDerAbwesenheit = new ErgebnisBerechnungViadeeZuschlaege();
      eintragZumTagDerAbwesenheit.setTag(abwesenheit.getTagVon());
      berechnungsErgebnis.add(eintragZumTagDerAbwesenheit);
    }
    return eintragZumTagDerAbwesenheit;
  }

  private BigDecimal abzugFuerFruehstuckBeiUebernachtung(final boolean istMitFruehstueck) {
    BigDecimal abzug = BigDecimal.ZERO;
    final String abzugFuerFruehstuck = parameterService.valueByKey(
        "abzugFuerFruehstuckBeiUebernachtung");
    if (abzugFuerFruehstuck != null) {
      if (istMitFruehstueck) {
        abzug = new BigDecimal(abzugFuerFruehstuck);
      }
    }
    return abzug;
  }

  private BigDecimal abzugFuerMittagessen(final boolean istMitMittagessen) {
    BigDecimal abzug = BigDecimal.ZERO;
    final String abzugFuerMittagessen = parameterService.valueByKey("abzugFuerMittagessen");
    if (abzugFuerMittagessen != null) {
      if (istMitMittagessen) {
        abzug = new BigDecimal(abzugFuerMittagessen);
      }
    }
    return abzug;
  }

  private BigDecimal abzugFuerAbendessen(final boolean istMitAbendessen) {
    BigDecimal abzug = BigDecimal.ZERO;
    final String abzugFuerAbendessen = parameterService.valueByKey("abzugFuerAbendessen");
    if (abzugFuerAbendessen != null) {
      if (istMitAbendessen) {
        abzug = new BigDecimal(abzugFuerAbendessen);
      }
    }
    return abzug;
  }

  private BigDecimal zuschlagFuerFruehstuckBeiUebernachtung(final boolean istMitFruehstueck) {
    BigDecimal zuschlag = BigDecimal.ZERO;
    if (istMitFruehstueck) {
      final String zuschlagFuerFruehstuck = parameterService.valueByKey(
          "zuschlagFuerFruehstuckBeiUebernachtung");
      if (zuschlagFuerFruehstuck != null) {
        zuschlag = new BigDecimal(zuschlagFuerFruehstuck);
      }
    }
    return zuschlag;
  }

  private BigDecimal ermittleSpesenEntsprechendDerAbwesenheitsstunden(
      final BigDecimal anzahlStunden,
      final boolean istAnreiseOderAbreisetag,
      final List<GesetzlicheSpesenKonstante> gesetzlicheSpesen) {
    BigDecimal spesen = BigDecimal.ZERO;
    final BigDecimal pauschaleFuerAnreiseAbreiseTag = parameterService
        .valueByKey("anreiseAbreisePauschale") == null ? BigDecimal.ZERO
        : new BigDecimal(parameterService.valueByKey("anreiseAbreisePauschale"));

    for (final GesetzlicheSpesenKonstante regelung : gesetzlicheSpesen) {
      if (regelung.isVonInklusive() && regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) >= 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) <= 0) {
          spesen = spesen.add(regelung.getBetrag());
        }
      } else if (regelung.isVonInklusive() && !regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) >= 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) < 0) {
          spesen = spesen.add(regelung.getBetrag());
        }
      } else if (!regelung.isVonInklusive() && regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) > 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) <= 0) {
          spesen = spesen.add(regelung.getBetrag());
        }
      } else if (!regelung.isVonInklusive() && !regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) > 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) < 0) {
          spesen = spesen.add(regelung.getBetrag());
        }
      }
    }

    if (istAnreiseOderAbreisetag && anzahlStunden.compareTo(new BigDecimal("24.00")) < 0) {
      spesen = pauschaleFuerAnreiseAbreiseTag;
    }

    return spesen;
  }

  private BigDecimal ermittleZuschlaegeEntsprechendDerAbwesenheitsstunden(
      final BigDecimal anzahlStunden,
      final boolean istMitUebernachtung, final List<ViadeeZuschlaege> viadeeZuschlaege) {
    BigDecimal zuschlag = BigDecimal.ZERO;
    final BigDecimal pauschaleFuerUebernachtung = parameterService
        .valueByKey("uebernachtungPauschale") == null ? BigDecimal.ZERO
        : new BigDecimal(parameterService.valueByKey("uebernachtungPauschale"));

    for (final ViadeeZuschlaege regelung : viadeeZuschlaege) {
      if (regelung.isVonInklusive() && regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) >= 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) <= 0) {
          zuschlag = zuschlag.add(regelung.getBetrag());
        }
      } else if (regelung.isVonInklusive() && !regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) >= 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) < 0) {
          zuschlag = zuschlag.add(regelung.getBetrag());
        }
      } else if (!regelung.isVonInklusive() && regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) > 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) <= 0) {
          zuschlag = zuschlag.add(regelung.getBetrag());
        }
      } else if (!regelung.isVonInklusive() && !regelung.isBisInklusive()) {
        if (anzahlStunden.compareTo(regelung.getStundenAbwesendVon()) > 0
            && anzahlStunden.compareTo(regelung.getStundenAbwesendBis()) < 0) {
          zuschlag = zuschlag.add(regelung.getBetrag());
        }
      }
    }

    if (istMitUebernachtung) {
      zuschlag = pauschaleFuerUebernachtung;
    }

    return zuschlag;
  }

  private Abwesenheit kopiereAbwesenheit(final Abwesenheit zuKopierendeAbwesenheit) {
    final Abwesenheit abwesenheit = new Abwesenheit();
    abwesenheit.setArbeitsnachweisId(zuKopierendeAbwesenheit.getArbeitsnachweisId());
    abwesenheit.setArbeitsstaette(zuKopierendeAbwesenheit.getArbeitsstaette());
    abwesenheit.setBemerkung(zuKopierendeAbwesenheit.getBemerkung());
    abwesenheit.setId(zuKopierendeAbwesenheit.getId());
    abwesenheit.setMitFruehstueck(zuKopierendeAbwesenheit.isMitFruehstueck());
    abwesenheit.setMitMittagessen(zuKopierendeAbwesenheit.isMitMittagessen());
    abwesenheit.setMitAbendessen(zuKopierendeAbwesenheit.isMitAbendessen());
    abwesenheit.setMitUebernachtung(zuKopierendeAbwesenheit.isMitUebernachtung());
    abwesenheit.setDreiMonatsRegelAktiv(zuKopierendeAbwesenheit.isDreiMonatsRegelAktiv());
    abwesenheit.setDreiMonatsRegelUebersteuert(
        zuKopierendeAbwesenheit.isDreiMonatsRegelUebersteuert());
    abwesenheit.setProjektId(zuKopierendeAbwesenheit.getProjektId());
    abwesenheit.setSpesen(zuKopierendeAbwesenheit.getSpesen());
    abwesenheit.setTagBis(zuKopierendeAbwesenheit.getTagBis());
    abwesenheit.setTagVon(zuKopierendeAbwesenheit.getTagVon());
    abwesenheit.setUhrzeitBis(zuKopierendeAbwesenheit.getUhrzeitVon());
    abwesenheit.setUhrzeitVon(zuKopierendeAbwesenheit.getUhrzeitVon());
    abwesenheit.setZuletztGeaendertAm(zuKopierendeAbwesenheit.getZuletztGeaendertAm());
    abwesenheit.setZuletztGeaendertVon(zuKopierendeAbwesenheit.getZuletztGeaendertVon());
    abwesenheit.setZuschlag(zuKopierendeAbwesenheit.getZuschlag());
    return abwesenheit;
  }

  private BigDecimal anzahlStunden(final Abwesenheit abwesenheit) {
    return zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
        abwesenheit.getUhrzeitBis());
  }

}
