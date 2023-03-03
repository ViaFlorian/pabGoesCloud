package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.Belegarten.PKW;
import static de.viadee.pabbackend.enums.Lohnarten.DIENSTF_UND_NEBENKOSTEN_FREI;
import static de.viadee.pabbackend.enums.Lohnarten.HANDY_ERSTATTUNG_FREI;
import static de.viadee.pabbackend.enums.Lohnarten.HANDY_ERSTATTUNG_PFL;
import static de.viadee.pabbackend.enums.Lohnarten.JOBTICKET;
import static de.viadee.pabbackend.enums.Lohnarten.RUFBEREITSCHAFT;
import static de.viadee.pabbackend.enums.Lohnarten.UEBERSTUNDEN_GRUNDVERGUETUNG;
import static de.viadee.pabbackend.enums.Lohnarten.VERPFLEGUNG_MEHRAUFWAND_25_PROZENT;
import static de.viadee.pabbackend.enums.Lohnarten.VERPFLEGUNG_MEHRAUFWAND_FREI;
import static de.viadee.pabbackend.enums.Lohnarten.VERPFLEGUNG_MEHRAUFWAND_PFL;
import static de.viadee.pabbackend.enums.Lohnarten.ZUSCHLAG_UEBERSTUNDEN_25_PROZENT_PFL;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.AbstractLohnartZuordnung;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisLohnartZuordnung;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.BetragLohnartZuordnung;
import de.viadee.pabbackend.entities.LohnartKonstante;
import de.viadee.pabbackend.entities.LohnartberechnungErgebnis;
import de.viadee.pabbackend.entities.LohnartberechnungLog;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.SonderzeitenKontoZuweisung;
import de.viadee.pabbackend.entities.StundenLohnartZuordnung;
import de.viadee.pabbackend.enums.Lohnarten;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import de.viadee.pabbackend.util.FormatFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Lohnartenberechnung {

  private final ParameterService parameterService;

  private final KonstantenService konstantenService;

  private final Zeitrechnung zeitrechnung;

  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;

  private final DecimalFormat dezimalZahlenFormatter = FormatFactory.deutschesDezimalzahlenFormatMitZweiNachkommastellen();

  public Lohnartenberechnung(ParameterService parameterService, KonstantenService konstantenService,
      Zeitrechnung zeitrechnung, MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService) {
    this.parameterService = parameterService;
    this.konstantenService = konstantenService;
    this.zeitrechnung = zeitrechnung;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
  }

  public LohnartberechnungErgebnis berechneLohnarten(final Arbeitsnachweis arbeitsnachweis,
      final List<Beleg> belege, final String smartphoneSelektion,
      final List<Abwesenheit> abwesenheiten,
      final List<Projektstunde> sonderarbeitszeiten, final List<Projektstunde> rufbereitschaften,
      final BigDecimal auszahlung) {

    final LohnartberechnungErgebnis ergebnis = new LohnartberechnungErgebnis();
    final List<LohnartberechnungLog> lohnartberechnungLog = new ArrayList<>();
    final List<ArbeitsnachweisLohnartZuordnung> arbeitsnachweisLohnartZuordnung = new ArrayList<>();

    final List<AbstractLohnartZuordnung> belegeMitLohnarten = ordneBelegenLohnartenZu(
        arbeitsnachweis, lohnartberechnungLog, belege,
        smartphoneSelektion);
    final List<AbstractLohnartZuordnung> spesenZuschlaegeMitLohnarten = ordneSpesenZuschlaegeLohnartenZu(
        arbeitsnachweis, lohnartberechnungLog,
        abwesenheiten);
    final List<AbstractLohnartZuordnung> ueberstundenMitLohnarten = ordneUeberstundenLohnartenZu(
        arbeitsnachweis,
        lohnartberechnungLog,
        auszahlung, sonderarbeitszeiten);
    final List<AbstractLohnartZuordnung> sonderarbeitszeitMitLohnarten = ordneSonderarbeitszeitenLohnartenZu(
        arbeitsnachweis, lohnartberechnungLog,
        sonderarbeitszeiten);
    final List<AbstractLohnartZuordnung> rufbereitschaftenMitLohnarten = ordneRufbereitschaftenLohnartenZu(
        arbeitsnachweis, lohnartberechnungLog,
        rufbereitschaften);

    final List<AbstractLohnartZuordnung> alleZuordnungen = new ArrayList<>();
    alleZuordnungen.addAll(belegeMitLohnarten);
    alleZuordnungen.addAll(spesenZuschlaegeMitLohnarten);
    alleZuordnungen.addAll(ueberstundenMitLohnarten);
    alleZuordnungen.addAll(sonderarbeitszeitMitLohnarten);
    alleZuordnungen.addAll(rufbereitschaftenMitLohnarten);

    uebertrageZuordnungenNachErgebnis(arbeitsnachweis, alleZuordnungen,
        arbeitsnachweisLohnartZuordnung);

    ergebnis.setArbeitsnachweisLohnartZuordnung(arbeitsnachweisLohnartZuordnung);
    ergebnis.setLohnartberechnungLog(lohnartberechnungLog);

    return ergebnis;
  }

  private List<AbstractLohnartZuordnung> ordneUeberstundenLohnartenZu(
      final Arbeitsnachweis arbeitsnachweis,
      final List<LohnartberechnungLog> lohnartberechnungLog, final BigDecimal auszahlung,
      final List<Projektstunde> sonderarbeitszeiten) {
    final List<AbstractLohnartZuordnung> ueberstundenLohnarten = new ArrayList<>();
    final AbstractLohnartZuordnung ueberstundenGrundverguetungZuordnung = new StundenLohnartZuordnung();

    ueberstundenGrundverguetungZuordnung.setLohnart(UEBERSTUNDEN_GRUNDVERGUETUNG);
    final BigDecimal summeSonderarbeitszeit = sonderarbeitszeiten.stream()
        .map(Projektstunde::getAnzahlStunden)
        .reduce(ZERO, BigDecimal::add);

    ueberstundenGrundverguetungZuordnung
        .setWert(auszahlung.add(summeSonderarbeitszeit));

    if (ueberstundenGrundverguetungZuordnung.getWert().compareTo(ZERO) != 0) {
      ueberstundenLohnarten.add(ueberstundenGrundverguetungZuordnung);
      lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
          UEBERSTUNDEN_GRUNDVERGUETUNG.getKonto(),
          LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1),
          "Ausbezahlte Überstunden (" + dezimalZahlenFormatter.format(auszahlung)
              + ") inkl. Sonderarbeitszeit (" + dezimalZahlenFormatter.format(
              summeSonderarbeitszeit)
              + ")",
          auszahlung.add(summeSonderarbeitszeit),
          ueberstundenGrundverguetungZuordnung.getEinheit()));
    }

    // Nur bei Stellenfaktor gleich 1.0 wird ein Zuschlag auf die Überstunden bezahlt.
    // Nur bei positiver Auszahlung erfolgt ein Zuschlag.
    BigDecimal stellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
            arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat())
        .getStellenfaktor();
    if (stellenfaktor.compareTo(BigDecimal.ONE) == 0
        && auszahlung.compareTo(ZERO) > 0) {
      final AbstractLohnartZuordnung ueberstundenZuschlagUeber25ProzPflZuordnung = new StundenLohnartZuordnung();
      ueberstundenZuschlagUeber25ProzPflZuordnung.setLohnart(ZUSCHLAG_UEBERSTUNDEN_25_PROZENT_PFL);
      ueberstundenZuschlagUeber25ProzPflZuordnung.setWert(auszahlung);
      if (ueberstundenZuschlagUeber25ProzPflZuordnung.getWert().compareTo(ZERO) != 0) {
        ueberstundenLohnarten.add(ueberstundenZuschlagUeber25ProzPflZuordnung);

        lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
            ZUSCHLAG_UEBERSTUNDEN_25_PROZENT_PFL.getKonto(),
            LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1),
            "Ausbezahlte Überstunden",
            auszahlung,
            ueberstundenZuschlagUeber25ProzPflZuordnung.getEinheit()));

      }

    }

    return ueberstundenLohnarten;
  }

  private void uebertrageZuordnungenNachErgebnis(final Arbeitsnachweis arbeitsnachweis,
      final List<AbstractLohnartZuordnung> alleZuordnungen,
      final List<ArbeitsnachweisLohnartZuordnung> lohnartenberechnungErgebnis) {

    for (final AbstractLohnartZuordnung zuordnung : alleZuordnungen) {

      final LohnartKonstante lohnart = konstantenService.lohnartByKonto(
          zuordnung.getLohnart().getKonto());

      final ArbeitsnachweisLohnartZuordnung arbeitsnachweisLohnartZuordnung = lohnartenberechnungErgebnis.stream()
          .filter(lohnartZuordnung -> {
            return lohnartZuordnung.getLohnartId().equals(lohnart.getId());
          }).findFirst().orElse(new ArbeitsnachweisLohnartZuordnung());

      if (arbeitsnachweisLohnartZuordnung.getLohnartId() == null) {
        arbeitsnachweisLohnartZuordnung.setArbeitsnachweisId(arbeitsnachweis.getId());
        arbeitsnachweisLohnartZuordnung.setLohnartId(lohnart.getId());
        arbeitsnachweisLohnartZuordnung.setBetrag(zuordnung.getWert());
        arbeitsnachweisLohnartZuordnung.setEinheit(zuordnung.getEinheit());
        lohnartenberechnungErgebnis.add(arbeitsnachweisLohnartZuordnung);
      } else {
        arbeitsnachweisLohnartZuordnung
            .setBetrag(arbeitsnachweisLohnartZuordnung.getBetrag().add(zuordnung.getWert()));
      }

    }

  }

  private List<AbstractLohnartZuordnung> ordneRufbereitschaftenLohnartenZu(
      final Arbeitsnachweis arbeitsnachweis,
      final List<LohnartberechnungLog> lohnartberechnungLog,
      final List<Projektstunde> rufbereitschaften) {
    final List<AbstractLohnartZuordnung> rufbereitschaftLohnarten = new ArrayList<>();

    final AbstractLohnartZuordnung rufbereitschaftenLohnartZuordnung = new BetragLohnartZuordnung();
    final BigDecimal rufbereitschaftErstattung = parameterService
        .valueByKey("rufbereitschaftErstattung") == null ? ZERO
        : new BigDecimal(parameterService.valueByKey("rufbereitschaftErstattung"));
    final BigDecimal summeRufbereitschaftStunden = rufbereitschaften.stream()
        .map(Projektstunde::getAnzahlStunden)
        .reduce(ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

    if (summeRufbereitschaftStunden.multiply(rufbereitschaftErstattung).compareTo(ZERO) != 0) {
      rufbereitschaftenLohnartZuordnung.setWert(
          summeRufbereitschaftStunden.multiply(rufbereitschaftErstattung));
      rufbereitschaftenLohnartZuordnung.setLohnart(RUFBEREITSCHAFT);
      rufbereitschaftLohnarten.add(rufbereitschaftenLohnartZuordnung);

      lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
          RUFBEREITSCHAFT.getKonto(),
          LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1),
          "Summe der Rufbereitschat (" + dezimalZahlenFormatter.format(summeRufbereitschaftStunden)
              + " ) multipliziert mit der Erstattung für Rufbereitschaft ("
              + dezimalZahlenFormatter.format(rufbereitschaftErstattung) + ")",
          summeRufbereitschaftStunden.multiply(rufbereitschaftErstattung),
          rufbereitschaftenLohnartZuordnung.getEinheit()));

    }

    return rufbereitschaftLohnarten;

  }

  private List<AbstractLohnartZuordnung> ordneSonderarbeitszeitenLohnartenZu(
      final Arbeitsnachweis arbeitsnachweis, final List<LohnartberechnungLog> lohnartberechnungLog,
      final List<Projektstunde> sonderarbeitszeiten) {

    final List<Projektstunde> sonderarbeitszeitenHeiligAbendUndSilvesterGesplittet = splitteHeiligabendUndSilvester(
        sonderarbeitszeiten);

    final List<AbstractLohnartZuordnung> sonderarbeitszeitenLohnartenZuordnung = new ArrayList<>();
    final List<SonderzeitenKontoZuweisung> sonderzeitenKontoZuweisung = konstantenService
        .sonderzeitenKontoZuweisungen();

    for (final Projektstunde sonderarbeitszeit : sonderarbeitszeitenHeiligAbendUndSilvesterGesplittet) {
      final String tagDerWoche = ermittleTagDerWoche(sonderarbeitszeit);
      final LocalTime sonderarbeitszeitUhrzeitVon = sonderarbeitszeit.getUhrzeitVon();
      final LocalTime sonderarbeitszeitUhrzeitBis = sonderarbeitszeit.getUhrzeitBis();

      for (final SonderzeitenKontoZuweisung sonderzeitenKontoKonfiguration : sonderzeitenKontoZuweisung) {

        if (sonderzeitenKontoKonfiguration.getTagDerWoche().equals(tagDerWoche)) {

          final LocalTime kontoUhrzeitVon = sonderzeitenKontoKonfiguration.getUhrzeitVon();
          final LocalTime kontoUhrzeitBis = sonderzeitenKontoKonfiguration.getUhrzeitBis();

          sucheUeberschneidungenUndErstelleZuweisungssaetze(arbeitsnachweis, lohnartberechnungLog,
              sonderarbeitszeit, sonderarbeitszeitenLohnartenZuordnung,
              sonderarbeitszeitUhrzeitVon,
              sonderarbeitszeitUhrzeitBis, sonderzeitenKontoKonfiguration,
              kontoUhrzeitVon, kontoUhrzeitBis);

        }

      }

    }

    return sonderarbeitszeitenLohnartenZuordnung;
  }

  /**
   * Feiertage sowie Heiligabend und Silvester werden unterschiedlich betrachtet. In der Tabelle
   * SonderzeitenKontoZuweisung sind daher eigene Sätze mit den Kennern HEILIGABEND, FEIERTAG und
   * SILVESTER hinterlegt. Handelt es sich um den 24.12. oder den 31.12. wird HEILIGABEND bzw.
   * SILVESTER zurückgegeben. Ist dies nicht der Fall, wird über die Tabelle Kalender ermittelt, ob
   * es sich um einen Feiertag handelt. In dem Fall wird FEIERTAG und in allen anderen Fällen der
   * Tag der Woche (als Zahl; 1-basiert) zurückgegeben.
   *
   * @param sonderarbeitszeit
   * @return
   */
  private String ermittleTagDerWoche(final Projektstunde sonderarbeitszeit) {
    LocalTime vierzehnUhr = LocalTime.of(14, 0);
    if (sonderarbeitszeit.getTagVon().getDayOfMonth() == 24
        && sonderarbeitszeit.getTagVon().getMonthValue() == 12) {
      if (sonderarbeitszeit.getUhrzeitBis().isBefore(vierzehnUhr)) {
        return String.valueOf(sonderarbeitszeit.getTagVon().getDayOfWeek().getValue());
      }
      return "HEILIGABEND";
    } else if (sonderarbeitszeit.getTagVon().getDayOfMonth() == 31
        && sonderarbeitszeit.getTagVon().getMonthValue() == 12) {
      if (sonderarbeitszeit.getUhrzeitBis().isBefore(vierzehnUhr)) {
        return String.valueOf(sonderarbeitszeit.getTagVon().getDayOfWeek().getValue());
      }
      return "SILVESTER";
    } else if (zeitrechnung.istFeiertag(sonderarbeitszeit.getTagVon())) {
      return "FEIERTAG";
    }
    return String.valueOf(sonderarbeitszeit.getTagVon().getDayOfWeek().getValue());
  }

  /**
   * Heiligabend und Silvester werden ab 14:00 Uhr wie Feiertage behandelt und davor wie normale
   * Arbeitstage, daher werden aus einem Sonderarbeitszeiten-Satz an diesen Tagen zwei Sätze gemacht
   * (00:00 - 13:59 Uhr und 14:00 bis 00:00 Uhr), sofern die Sonderarbeitszeit die Grenze 14:00 Uhr
   * überschneidet.<br>
   * <br>
   * Beispiel:<br> 31.12. 12:00 Uhr bis 17:00 Uhr -> 2 Sätze: 31.12. 12:00 Uhr bis 13:59 Uhr /
   * 31.12. 14:00 Uhr bis 17:00 Uhr
   *
   * @param sonderarbeitszeiten
   * @return
   */
  private List<Projektstunde> splitteHeiligabendUndSilvester(
      final List<Projektstunde> sonderarbeitszeiten) {
    final List<Projektstunde> sonderarbeitszeitenSilvesterUndHeiligabendGesplittet = new ArrayList<>();
    for (final Projektstunde sonderarbeitszeit : sonderarbeitszeiten) {
      if (istHeiligabendOderSilvester(sonderarbeitszeit)) {

        final LocalTime vierzehnUhr = LocalTime.of(14, 0);

        if (vierzehnUhr.isAfter(sonderarbeitszeit.getUhrzeitVon())
            && vierzehnUhr.isBefore(sonderarbeitszeit.getUhrzeitBis())) {

          final Projektstunde stundenVorVierzehnUhr = kopiereSonderarbeitszeit(sonderarbeitszeit);
          final Projektstunde stundenNachVierzehnUhr = kopiereSonderarbeitszeit(sonderarbeitszeit);

          stundenVorVierzehnUhr.setUhrzeitBis(LocalTime.of(13, 59, 59));
          stundenNachVierzehnUhr.setUhrzeitVon(LocalTime.of(14, 0, 0));

          sonderarbeitszeitenSilvesterUndHeiligabendGesplittet.add(stundenVorVierzehnUhr);
          sonderarbeitszeitenSilvesterUndHeiligabendGesplittet.add(stundenNachVierzehnUhr);
        } else {
          sonderarbeitszeitenSilvesterUndHeiligabendGesplittet.add(sonderarbeitszeit);
        }

      } else {
        sonderarbeitszeitenSilvesterUndHeiligabendGesplittet.add(sonderarbeitszeit);
      }
    }
    return sonderarbeitszeitenSilvesterUndHeiligabendGesplittet;
  }

  private Projektstunde kopiereSonderarbeitszeit(Projektstunde sonderarbeitszeit) {
    Projektstunde projektstunden = new Projektstunde();
    projektstunden.setKostensatzIntern(sonderarbeitszeit.getKostensatzIntern());
    projektstunden.setKostensatzExtern(sonderarbeitszeit.getKostensatzExtern());
    projektstunden.setDavonFakturierbar(sonderarbeitszeit.getDavonFakturierbar());
    projektstunden.setTagBis(sonderarbeitszeit.getTagBis());
    projektstunden.setUhrzeitBis(sonderarbeitszeit.getUhrzeitBis());
    projektstunden.setUhrzeitVon(sonderarbeitszeit.getUhrzeitVon());
    projektstunden.setArbeitsnachweisId(sonderarbeitszeit.getArbeitsnachweisId());
    projektstunden.setProjektstundeTypId(sonderarbeitszeit.getProjektstundeTypId());
    projektstunden.setTagVon(sonderarbeitszeit.getTagVon());
    projektstunden.setAnzahlStunden(sonderarbeitszeit.getAnzahlStunden());
    projektstunden.setId(sonderarbeitszeit.getId());
    projektstunden.setBemerkung(sonderarbeitszeit.getBemerkung());
    projektstunden.setZuletztGeaendertVon(sonderarbeitszeit.getZuletztGeaendertVon());
    projektstunden.setZuletztGeaendertAm(sonderarbeitszeit.getZuletztGeaendertAm());
    projektstunden.setProjektId(sonderarbeitszeit.getProjektId());
    projektstunden.setId(sonderarbeitszeit.getId());
    return projektstunden;
  }

  private boolean istHeiligabendOderSilvester(Projektstunde sonderarbeitszeit) {
    return (sonderarbeitszeit.getTagVon().getDayOfMonth() == 24
        || sonderarbeitszeit.getTagVon().getDayOfMonth() == 31)
        && sonderarbeitszeit.getTagVon().getMonthValue() == 12;
  }

  private void sucheUeberschneidungenUndErstelleZuweisungssaetze(
      final Arbeitsnachweis arbeitsnachweis, final List<LohnartberechnungLog> lohnartberechnungLog,
      final Projektstunde sonderarbeitszeit,
      final List<AbstractLohnartZuordnung> sonderarbeitszeitenLohnartenZuordnung,
      final LocalTime sonderarbeitszeitUhrzeitVon, final LocalTime sonderarbeitszeitUhrzeitBis,
      final SonderzeitenKontoZuweisung sonderzeitenKontoKonfiguration,
      final LocalTime kontoUhrzeitVon,
      final LocalTime kontoUhrzeitBis) {

    // Beide Uhrzeiten fallen ins Intervall
    if (((sonderarbeitszeitUhrzeitVon.isAfter(kontoUhrzeitVon)
        && sonderarbeitszeitUhrzeitVon.isBefore(kontoUhrzeitBis))
        || sonderarbeitszeitUhrzeitVon.equals(kontoUhrzeitVon)) &&
        ((sonderarbeitszeitUhrzeitBis.isAfter(kontoUhrzeitVon)
            && sonderarbeitszeitUhrzeitBis.isBefore(kontoUhrzeitBis))
            || sonderarbeitszeitUhrzeitBis.equals(kontoUhrzeitBis))) {

      final BigDecimal anzahlStunden = zeitrechnung.berechneStundendifferenz(
          sonderarbeitszeitUhrzeitVon,
          sonderarbeitszeitUhrzeitBis);
      erstelleLohnartenZuweisungssaetze(arbeitsnachweis, lohnartberechnungLog, sonderarbeitszeit,
          anzahlStunden,
          sonderzeitenKontoKonfiguration,
          sonderarbeitszeitenLohnartenZuordnung);

      // Beide Uhrzeiten überspannen das Intervall
    } else if (sonderarbeitszeitUhrzeitVon.isBefore(kontoUhrzeitVon)
        && sonderarbeitszeitUhrzeitBis.isAfter(kontoUhrzeitBis)) {

      final BigDecimal anzahlStunden = zeitrechnung.berechneStundendifferenz(
          kontoUhrzeitVon,
          kontoUhrzeitBis);
      erstelleLohnartenZuweisungssaetze(arbeitsnachweis, lohnartberechnungLog, sonderarbeitszeit,
          anzahlStunden,
          sonderzeitenKontoKonfiguration,
          sonderarbeitszeitenLohnartenZuordnung);

      // Nur von-Uhrzeit fällt ins Intervall
    } else if (((sonderarbeitszeitUhrzeitVon.isAfter(kontoUhrzeitVon)
        && sonderarbeitszeitUhrzeitVon.isBefore(kontoUhrzeitBis))
        || sonderarbeitszeitUhrzeitVon.equals(kontoUhrzeitVon)) &&
        sonderarbeitszeitUhrzeitBis.isAfter(kontoUhrzeitBis)) {

      final BigDecimal anzahlStunden = zeitrechnung.berechneStundendifferenz(
          sonderarbeitszeitUhrzeitVon,
          kontoUhrzeitBis);
      erstelleLohnartenZuweisungssaetze(arbeitsnachweis, lohnartberechnungLog, sonderarbeitszeit,
          anzahlStunden,
          sonderzeitenKontoKonfiguration,
          sonderarbeitszeitenLohnartenZuordnung);

      // Nur bis-Uhrzeit fällt ins Intervall
    } else if (sonderarbeitszeitUhrzeitVon.isBefore(kontoUhrzeitVon) &&
        ((sonderarbeitszeitUhrzeitBis.isAfter(kontoUhrzeitVon)
            && sonderarbeitszeitUhrzeitBis.isBefore(kontoUhrzeitBis))
            || sonderarbeitszeitUhrzeitBis.equals(kontoUhrzeitBis))) {

      final BigDecimal anzahlStunden = zeitrechnung.berechneStundendifferenz(kontoUhrzeitVon,
          sonderarbeitszeitUhrzeitBis);
      erstelleLohnartenZuweisungssaetze(arbeitsnachweis, lohnartberechnungLog, sonderarbeitszeit,
          anzahlStunden,
          sonderzeitenKontoKonfiguration,
          sonderarbeitszeitenLohnartenZuordnung);

    }
  }

  private void erstelleLohnartenZuweisungssaetze(final Arbeitsnachweis arbeitsnachweis,
      final List<LohnartberechnungLog> lohnartberechnungLog, final Projektstunde sonderarbeitszeit,
      final BigDecimal anzahlStunden,
      final SonderzeitenKontoZuweisung sonderzeitenKontoKonfiguration,
      final List<AbstractLohnartZuordnung> sonderarbeitszeitenLohnartenZuordnung) {

    if (anzahlStunden != null && anzahlStunden.compareTo(ZERO) != 0) {
      if (sonderzeitenKontoKonfiguration.getKontoSteuerfrei() != null) {
        final AbstractLohnartZuordnung wertLohnartZuordnungSteuerfrei = new StundenLohnartZuordnung();
        wertLohnartZuordnungSteuerfrei
            .setLohnart(Lohnarten.get(sonderzeitenKontoKonfiguration.getKontoSteuerfrei()));
        wertLohnartZuordnungSteuerfrei.setWert(anzahlStunden);
        sonderarbeitszeitenLohnartenZuordnung.add(wertLohnartZuordnungSteuerfrei);

        lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
            sonderzeitenKontoKonfiguration.getKontoSteuerfrei(), sonderarbeitszeit.getTagVon(),
            "Sonderarbeitszeit " + getTagBezeichner(sonderarbeitszeit.getTagVon()),
            anzahlStunden, wertLohnartZuordnungSteuerfrei.getEinheit()));

      }
      if (sonderzeitenKontoKonfiguration.getKontoSteuerpflichtig() != null) {
        final AbstractLohnartZuordnung wertLohnartZuordnungSteuerpflichtig = new StundenLohnartZuordnung();
        wertLohnartZuordnungSteuerpflichtig
            .setLohnart(Lohnarten.get(sonderzeitenKontoKonfiguration.getKontoSteuerpflichtig()));
        wertLohnartZuordnungSteuerpflichtig.setWert(anzahlStunden);
        sonderarbeitszeitenLohnartenZuordnung.add(wertLohnartZuordnungSteuerpflichtig);

        lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
            sonderzeitenKontoKonfiguration.getKontoSteuerpflichtig(), sonderarbeitszeit.getTagVon(),
            "Sonderarbeitszeit " + getTagBezeichner(sonderarbeitszeit.getTagVon()),
            anzahlStunden, wertLohnartZuordnungSteuerpflichtig.getEinheit()));

      }
    }

  }

  private List<AbstractLohnartZuordnung> ordneSpesenZuschlaegeLohnartenZu(
      final Arbeitsnachweis arbeitsnachweis, final List<LohnartberechnungLog> lohnartberechnungLog,
      final List<Abwesenheit> relevanteAbwesenheiten) {
    final List<AbstractLohnartZuordnung> spesenZuschlaegeLohnartenZuweisung = new ArrayList<>();

    relevanteAbwesenheiten.stream().forEach(abwesenheit -> {

      BigDecimal konto078 = ZERO;
      BigDecimal konto079 = ZERO;
      BigDecimal konto080 = ZERO;

      if (dreiMonatsRegelIstInKraft(abwesenheit)) {

        konto080 = abwesenheit.getSpesen().add(abwesenheit.getZuschlag());

        final AbstractLohnartZuordnung zuordnung080 = new BetragLohnartZuordnung();
        zuordnung080.setLohnart(VERPFLEGUNG_MEHRAUFWAND_PFL);
        zuordnung080.setWert(konto080);
        spesenZuschlaegeLohnartenZuweisung.add(zuordnung080);

        lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
            VERPFLEGUNG_MEHRAUFWAND_PFL.getKonto(), abwesenheit.getTagVon(),
            "Summe aus Spesen und Zuschlägen; 3-Monatsregel greift",
            konto080, zuordnung080.getEinheit()));

      } else {

        konto078 = konto078.add(abwesenheit.getSpesen());

        if (konto078.compareTo(ZERO) != 0) {
          final AbstractLohnartZuordnung zuordnung078 = new BetragLohnartZuordnung();
          zuordnung078.setLohnart(VERPFLEGUNG_MEHRAUFWAND_FREI);
          zuordnung078.setWert(konto078);
          spesenZuschlaegeLohnartenZuweisung.add(zuordnung078);

          lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
              VERPFLEGUNG_MEHRAUFWAND_FREI.getKonto(), abwesenheit.getTagVon(),
              "Berechnete Spesen",
              konto078, zuordnung078.getEinheit()));

        }

        konto079 = konto078.min(abwesenheit.getZuschlag());

        if (konto079.compareTo(ZERO) != 0) {
          final AbstractLohnartZuordnung zuordnung079 = new BetragLohnartZuordnung();
          zuordnung079.setLohnart(VERPFLEGUNG_MEHRAUFWAND_25_PROZENT);
          zuordnung079.setWert(konto079);
          spesenZuschlaegeLohnartenZuweisung.add(zuordnung079);

          lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
              VERPFLEGUNG_MEHRAUFWAND_25_PROZENT.getKonto(), abwesenheit.getTagVon(),
              "Minimum aus Spesen (" + dezimalZahlenFormatter.format(abwesenheit.getSpesen())
                  + ") und viadee Zuschlägen ("
                  + dezimalZahlenFormatter.format(abwesenheit.getZuschlag()) + ")",
              konto079, zuordnung079.getEinheit()));

        }

        konto080 = abwesenheit.getZuschlag().subtract(konto079);

        if (konto080.compareTo(ZERO) != 0) {
          final AbstractLohnartZuordnung zuordnung080 = new BetragLohnartZuordnung();
          zuordnung080.setLohnart(VERPFLEGUNG_MEHRAUFWAND_PFL);
          zuordnung080.setWert(konto080);
          spesenZuschlaegeLohnartenZuweisung.add(zuordnung080);

          lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
              VERPFLEGUNG_MEHRAUFWAND_PFL.getKonto(), abwesenheit.getTagVon(),
              "Differenz der viadee Zuschläge ("
                  + dezimalZahlenFormatter.format(abwesenheit.getZuschlag())
                  + ") und dem ermittelten Minimum aus Spesen und viadee Zuschlägen ("
                  + dezimalZahlenFormatter.format(konto079) + ")",
              konto080, zuordnung080.getEinheit()));
        }

      }

    });

    return spesenZuschlaegeLohnartenZuweisung;
  }

  private boolean dreiMonatsRegelIstInKraft(final Abwesenheit abwesenheit) {
    return abwesenheit.isDreiMonatsRegelAktiv() != null && abwesenheit.isDreiMonatsRegelAktiv();
  }

  private List<AbstractLohnartZuordnung> ordneBelegenLohnartenZu(
      final Arbeitsnachweis arbeitsnachweis,
      final List<LohnartberechnungLog> lohnartberechnungLog, final List<Beleg> belege,
      final String smartphone) {
    final List<AbstractLohnartZuordnung> belegeMitLohnarten = new ArrayList<>();

    if (smartphone.equals(EIGEN.toString())) {
      final BigDecimal betragFuerBereitstellungDesEigenenGeraets = parameterService
          .valueByKey("bereitstellungEigenesSmartphone") == null ? ZERO
          : new BigDecimal(
              parameterService.valueByKey("bereitstellungEigenesSmartphone"));
      final AbstractLohnartZuordnung zuschlagZuordnung = new BetragLohnartZuordnung();
      zuschlagZuordnung.setWert(betragFuerBereitstellungDesEigenenGeraets);
      zuschlagZuordnung.setLohnart(HANDY_ERSTATTUNG_PFL);
      lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
          HANDY_ERSTATTUNG_PFL.getKonto(),
          LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1),
          "Erstattung für Nutzung des eigenen Gerätes",
          betragFuerBereitstellungDesEigenenGeraets, "€"));
      belegeMitLohnarten.add(zuschlagZuordnung);
    }

    final BelegartKonstante verbindungsentgelt = konstantenService.belegartByTextKurz(
        "Verbindungsentgelt");
    final BelegartKonstante jobticket = konstantenService.belegartByTextKurz(JOBTICKET.toString());
    final BelegartKonstante pkw = konstantenService.belegartByTextKurz(PKW.toString());

    belege.stream().forEach(beleg -> {
      final AbstractLohnartZuordnung zuordnung = new BetragLohnartZuordnung();
      zuordnung.setWert(beleg.getBetrag());

      if (beleg.getBelegartId().equals(verbindungsentgelt.getId())) {

        if (smartphone.equals(EIGEN.toString())) {
          zuordnung.setLohnart(HANDY_ERSTATTUNG_PFL);
          zuordnung.setWert(beleg.getBetrag());

          lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
              HANDY_ERSTATTUNG_PFL.getKonto(), beleg.getDatum(),
              "Verbindungsentgelt",
              beleg.getBetrag(), zuordnung.getEinheit()));

        } else {
          zuordnung.setLohnart(HANDY_ERSTATTUNG_FREI);

          lohnartberechnungLog.add(erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
              HANDY_ERSTATTUNG_FREI.getKonto(), beleg.getDatum(),
              "Verbindungsentgelt; Mitarbeiter/in nutzt Firmen-Gerät oder hat kein Smartphone angegeben",
              beleg.getBetrag(),
              zuordnung.getEinheit()));
        }

      } else if (beleg.getBelegartId().equals(jobticket.getId())) {
        zuordnung.setLohnart(JOBTICKET);

        lohnartberechnungLog.add(
            erstelleLohnartenberechnungLogMeldung(arbeitsnachweis, JOBTICKET.getKonto(),
                beleg.getDatum(), "Jobticket", beleg.getBetrag(), zuordnung.getEinheit()));

      } else {

        zuordnung.setLohnart(DIENSTF_UND_NEBENKOSTEN_FREI);
        if (beleg.getBelegartId().equals(pkw.getId()) && (arbeitsnachweis.getFirmenwagen() != null
            && arbeitsnachweis.getFirmenwagen().equals(Boolean.TRUE))) {
          // Hat ein Mitarbeiter einen Firmenwagen, dürfen die Kilometer nicht erstattet werden
          // Es darf entsprechend auch kein Eintrag im Log erfolgen
        } else {
          lohnartberechnungLog.add(
              erstelleLohnartenberechnungLogMeldung(arbeitsnachweis,
                  DIENSTF_UND_NEBENKOSTEN_FREI.getKonto(),
                  beleg.getDatum(),
                  "Beleg " + konstantenService.belegartByID(beleg.getBelegartId()).getTextKurz(),
                  beleg.getBetrag(),
                  zuordnung.getEinheit()));
        }
      }

      if (beleg.getBelegartId().equals(pkw.getId()) && (arbeitsnachweis.getFirmenwagen() != null
          && arbeitsnachweis.getFirmenwagen().equals(Boolean.TRUE))) {
        // Hat ein Mitarbeiter einen Firmenwagen, dürfen die Kilometer nicht erstattet werden
      } else {
        belegeMitLohnarten.add(zuordnung);
      }
    });

    return belegeMitLohnarten;

  }

  private LohnartberechnungLog erstelleLohnartenberechnungLogMeldung(
      final Arbeitsnachweis arbeitsnachweis,
      final String konto, final LocalDate datum, final String meldung, final BigDecimal wert,
      final String einheit) {
    final LohnartberechnungLog logeintrag = new LohnartberechnungLog();
    logeintrag.setArbeitsnachweisId(arbeitsnachweis.getId());
    logeintrag.setKonto(konto);
    logeintrag.setDatum(datum);
    logeintrag.setMeldung(meldung);
    logeintrag.setWert(wert);
    logeintrag.setEinheit(einheit);
    return logeintrag;

  }

  private String getTagBezeichner(final LocalDate datumVon) {
    if (datumVon.getMonthValue() == 12 && datumVon.getDayOfMonth() == 24) {
      return "Heiligabend";
    }
    if (datumVon.getMonthValue() == 12 && datumVon.getDayOfMonth() == 24) {
      return "Silvester";
    }
    if (!(datumVon.getDayOfWeek().equals(DayOfWeek.SATURDAY) || datumVon.getDayOfWeek()
        .equals(DayOfWeek.SUNDAY))) {
      return "Werktag";
    }

    return datumVon.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
  }

}
