package de.viadee.pabbackend.services.fachobjekt;

import static de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus.ERFASST;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.LeistungKumuliert;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.ProjektAbrechnungsmonat;
import de.viadee.pabbackend.entities.ProjektBudget;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungBerechneteLeistung;
import de.viadee.pabbackend.entities.ProjektabrechnungFertigstellungInitialDaten;
import de.viadee.pabbackend.entities.ProjektabrechnungKostenLeistung;
import de.viadee.pabbackend.entities.ProjektabrechnungMitarbeiterPair;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeit;
import de.viadee.pabbackend.entities.ProjektabrechnungReise;
import de.viadee.pabbackend.entities.ProjektabrechnungSonderarbeit;
import de.viadee.pabbackend.entities.ProjektabrechnungSonstige;
import de.viadee.pabbackend.entities.ProjektabrechnungUebersicht;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.entities.SonderarbeitszeitWochentagVerteilung;
import de.viadee.pabbackend.entities.SonstigeProjektkosten;
import de.viadee.pabbackend.entities.SonstigeProjektkostenSpeichernRequest;
import de.viadee.pabbackend.entities.SonstigeProjektkostenSpeichernResponse;
import de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus;
import de.viadee.pabbackend.enums.ProjektstundeTyp;
import de.viadee.pabbackend.enums.ViadeeAuslagenKostenarten;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungRepository;
import de.viadee.pabbackend.services.berechnung.FestpreisBerechnung;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjektabrechnungService {

  private final ProjektabrechnungRepository projektabrechnungRepository;
  private final ProjektService projektService;
  private final MonatsabschlussService monatsabschlussService;
  private final KonstantenService konstantenService;
  private final ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService;
  private final ProjektabrechnungReiseService projektabrechnungReiseService;
  private final ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService;
  private final ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService;
  private final ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService;
  private final ParameterService parameterService;
  private final SonstigeProjektkostenService sonstigeProjektkostenService;
  private final ProjektabrechnungSonstigeService projektabrechnungSonstigeService;
  private final MitarbeiterService mitarbeiterService;
  private final ProjektBudgetService projektBudgetService;
  private final Zeitrechnung zeitrechnung;
  private final FestpreisBerechnung festpreisBerechnung;


  public ProjektabrechnungService(ProjektabrechnungRepository projektabrechnungRepository,
      KonstantenService konstantenService,
      ProjektabrechnungReiseService projektabrechnungReiseService,
      ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService,
      ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService,
      ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService,
      ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService,
      ParameterService parameterService, Zeitrechnung zeitrechnung, ProjektService projektService,
      MonatsabschlussService monatsabschlussService,
      SonstigeProjektkostenService sonstigeProjektkostenService,
      ProjektabrechnungSonstigeService projektabrechnungSonstigeService,
      MitarbeiterService mitarbeiterService, ProjektBudgetService projektBudgetService,
      FestpreisBerechnung festpreisBerechnung) {
    this.projektabrechnungRepository = projektabrechnungRepository;
    this.projektabrechnungProjektzeitService = projektabrechnungProjektzeitService;
    this.projektabrechnungReiseService = projektabrechnungReiseService;
    this.projektabrechnungSonderarbeitService = projektabrechnungSonderarbeitService;
    this.projektabrechnungSonstigeService = projektabrechnungSonstigeService;
    this.projektabrechnungBerechneteLeistungService = projektabrechnungBerechneteLeistungService;
    this.projektabrechnungKorrekturbuchungService = projektabrechnungKorrekturbuchungService;
    this.konstantenService = konstantenService;
    this.parameterService = parameterService;
    this.zeitrechnung = zeitrechnung;
    this.projektService = projektService;
    this.monatsabschlussService = monatsabschlussService;
    this.sonstigeProjektkostenService = sonstigeProjektkostenService;
    this.mitarbeiterService = mitarbeiterService;
    this.projektBudgetService = projektBudgetService;
    this.festpreisBerechnung = festpreisBerechnung;
  }

  public List<ProjektabrechnungUebersicht> alleProjektabrechnungenFuerUebersichtGefiltert(
      final int abJahr,
      final int abMonat,
      final int bisJahr,
      final int bisMonat) {
    return IterableUtils.toList(
        projektabrechnungRepository.findAlleProjektabrechnungenFuerUebersichtGefiltert(
            abJahr,
            abMonat,
            bisJahr,
            bisMonat
        ));
  }

  public List<ProjektabrechnungUebersicht> fehlendeProjektabrechnungen(LocalDate abWann,
      LocalDate bisWann) {
    List<ProjektabrechnungUebersicht> projektabrechnungUebersichts = new ArrayList<>();

    LocalDate abrechnungsmonat = LocalDate.now().minusMonths(1);
    if (abrechnungsmonat.getYear() < abWann.getYear() || (
        abrechnungsmonat.getYear() == abWann.getYear()
            && abrechnungsmonat.getMonthValue() < abWann.getMonthValue())) {
      return projektabrechnungUebersichts;
    }
    if (bisWann.getYear() < abrechnungsmonat.getYear() || (
        bisWann.getYear() == abrechnungsmonat.getYear()
            && bisWann.getMonthValue() < abrechnungsmonat.getMonthValue())) {
      return projektabrechnungUebersichts;
    }

    if (this.monatsabschlussService.istAbgeschlossen(abrechnungsmonat)) {
      return projektabrechnungUebersichts;
    }

    List<Projekt> projekteMitFehlenderAbrechnung = this.projektService.projekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
        abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue());
    projekteMitFehlenderAbrechnung.forEach(projekt -> {
      ProjektabrechnungUebersicht neueProjektabrechnung = new ProjektabrechnungUebersicht();

      neueProjektabrechnung.setJahr(abrechnungsmonat.getYear());
      neueProjektabrechnung.setMonat(abrechnungsmonat.getMonthValue());
      neueProjektabrechnung.setProjektId(projekt.getId());
      neueProjektabrechnung.setKundeId(projekt.getKundeId());
      neueProjektabrechnung.setOrganisationseinheitId(projekt.getOrganisationseinheitId());
      neueProjektabrechnung.setSachbearbeiterId(projekt.getSachbearbeiterId());
      neueProjektabrechnung.setAnzahlMitarbeiter(0);
      neueProjektabrechnung.setAnzahlKorrekturbuchungen(0);
      neueProjektabrechnung.setLeistung(BigDecimal.ZERO);
      neueProjektabrechnung.setKosten(BigDecimal.ZERO);

      projektabrechnungUebersichts.add(neueProjektabrechnung);
    });

    return projektabrechnungUebersichts;
  }

  public List<ProjektabrechnungMitarbeiterPair> alleProjektabrechnungMitarbeiterPairs(
      final int abJahr,
      final int abMonat,
      final int bisJahr,
      final int bisMonat) {
    return IterableUtils.toList(
        projektabrechnungRepository.findALleProjektabrechnungMitarbeiterPairs(
            abJahr,
            abMonat,
            bisJahr,
            bisMonat
        ));
  }

  public List<ProjektabrechnungKostenLeistung> getKostenLeistungJeMitarbeiter(Long id) {
    Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter = new HashMap<>();
    erzeugeKostenLeistungJeMitarbeiterFuerProjektzeit(id, kostenlistenEintragProMitarbeiter);
    erzeugeKostenLeistungJeMitarbeiterFuerReise(id, kostenlistenEintragProMitarbeiter);
    erzeugeKostenLeistungJeMitarbeiterFuerSonderarbeit(id, kostenlistenEintragProMitarbeiter);
    erzeugeKostenLeistungJeMitarbeiterFuerSonstige(id, kostenlistenEintragProMitarbeiter);
    erzeugeKostenLeistungJeMitarbeiterFuerBerechneteLeistung(id, kostenlistenEintragProMitarbeiter);
    ergaenzeOhneMitarbeiterbezug(kostenlistenEintragProMitarbeiter);
    return new ArrayList<>(kostenlistenEintragProMitarbeiter.values());
  }

  private void erzeugeKostenLeistungJeMitarbeiterFuerProjektzeit(Long id,
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    List<ProjektabrechnungProjektzeit> projektabrechnungProjektzeits =
        this.projektabrechnungProjektzeitService.projektabrechnungProjektzeitByProjektabrechnungId(
            id);
    for (ProjektabrechnungProjektzeit projektabrechnungProjektzeit : projektabrechnungProjektzeits) {
      ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
          projektabrechnungProjektzeit.getMitarbeiterId());

      if (kostenLeistungEintrag == null) {
        ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
        if (projektabrechnungProjektzeit.getMitarbeiterId() == null) {
          neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
        } else {
          neuerKostenLeistungEintrag.setMitarbeiterId(
              projektabrechnungProjektzeit.getMitarbeiterId());
        }
        kostenlistenEintragProMitarbeiter.put(projektabrechnungProjektzeit.getMitarbeiterId(),
            neuerKostenLeistungEintrag);
        kostenLeistungEintrag = neuerKostenLeistungEintrag;
      }

      BigDecimal kostenbetragDesAktuellenKostensatzes = (
          projektabrechnungProjektzeit.getStundenLautArbeitsnachweis() == null ? ZERO
              : projektabrechnungProjektzeit.getStundenLautArbeitsnachweis()).multiply(
          projektabrechnungProjektzeit.getKostensatz() == null ? ZERO
              : projektabrechnungProjektzeit.getKostensatz()).setScale(2, RoundingMode.HALF_UP);
      BigDecimal leistungbetragDesAktuellenStundensatzes = (
          projektabrechnungProjektzeit.getStundenLautArbeitsnachweis() == null ? ZERO
              : projektabrechnungProjektzeit.getStundenLautArbeitsnachweis()).multiply(
          projektabrechnungProjektzeit.getStundensatz() == null ? ZERO
              : projektabrechnungProjektzeit.getStundensatz()).setScale(2, RoundingMode.HALF_UP);

      kostenLeistungEintrag.setProjektzeitKosten(
          kostenLeistungEintrag.getProjektzeitKosten().add(
              kostenbetragDesAktuellenKostensatzes));
      kostenLeistungEintrag.setProjektzeitLeistung(
          kostenLeistungEintrag.getProjektzeitLeistung().add(
              leistungbetragDesAktuellenStundensatzes));
    }
  }

  private void erzeugeKostenLeistungJeMitarbeiterFuerReise(Long id,
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    List<ProjektabrechnungReise> projektabrechnungReises = projektabrechnungReiseService.projektabrechnungReiseByProjektabrechnungId(
        id);
    for (ProjektabrechnungReise projektabrechnungReise : projektabrechnungReises) {
      ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
          projektabrechnungReise.getMitarbeiterId());

      if (kostenLeistungEintrag == null) {
        ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
        if (projektabrechnungReise.getMitarbeiterId() == null) {
          neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
        } else {
          neuerKostenLeistungEintrag.setMitarbeiterId(
              projektabrechnungReise.getMitarbeiterId());
        }
        kostenlistenEintragProMitarbeiter.put(projektabrechnungReise.getMitarbeiterId(),
            neuerKostenLeistungEintrag);
        kostenLeistungEintrag = neuerKostenLeistungEintrag;
      }

      kostenLeistungEintrag.setReiseKosten(
          kostenLeistungEintrag.getReiseKosten().add(projektabrechnungReise.getReiseKosten()));
      kostenLeistungEintrag.setReiseLeistung(
          kostenLeistungEintrag.getReiseLeistung().add(projektabrechnungReise.getReiseLeistung()));
    }
  }

  private void erzeugeKostenLeistungJeMitarbeiterFuerSonderarbeit(Long id,
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    List<ProjektabrechnungSonderarbeit> projektabrechnungSonderarbeits = projektabrechnungSonderarbeitService.projektabrechnungSonderarbeitByProjektabrechnungId(
        id);
    for (ProjektabrechnungSonderarbeit projektabrechnungSonderarbeit : projektabrechnungSonderarbeits) {
      ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
          projektabrechnungSonderarbeit.getMitarbeiterId());

      if (kostenLeistungEintrag == null) {
        ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
        if (projektabrechnungSonderarbeit.getMitarbeiterId() == null) {
          neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
        } else {
          neuerKostenLeistungEintrag.setMitarbeiterId(
              projektabrechnungSonderarbeit.getMitarbeiterId());
        }
        kostenlistenEintragProMitarbeiter.put(projektabrechnungSonderarbeit.getMitarbeiterId(),
            neuerKostenLeistungEintrag);
        kostenLeistungEintrag = neuerKostenLeistungEintrag;
      }

      kostenLeistungEintrag.setSonderzeitKosten(
          kostenLeistungEintrag.getSonderzeitKosten()
              .add(projektabrechnungSonderarbeit.getSonderzeitKosten()));
      kostenLeistungEintrag.setSonderzeitLeistung(
          kostenLeistungEintrag.getSonderzeitLeistung()
              .add(projektabrechnungSonderarbeit.getSonderzeitLeistung()));
    }
  }

  private void erzeugeKostenLeistungJeMitarbeiterFuerSonstige(Long id,
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    List<ProjektabrechnungSonstige> projektabrechnungSonstiges = projektabrechnungSonstigeService.projektabrechnungSonstigeByProjektabrechnungId(
        id);
    for (ProjektabrechnungSonstige projektabrechnungSonstige : projektabrechnungSonstiges) {
      ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
          projektabrechnungSonstige.getMitarbeiterId());

      if (kostenLeistungEintrag == null) {
        ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
        if (projektabrechnungSonstige.getMitarbeiterId() == null) {
          neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
        } else {
          neuerKostenLeistungEintrag.setMitarbeiterId(
              projektabrechnungSonstige.getMitarbeiterId());
        }
        kostenlistenEintragProMitarbeiter.put(projektabrechnungSonstige.getMitarbeiterId(),
            neuerKostenLeistungEintrag);
        kostenLeistungEintrag = neuerKostenLeistungEintrag;
      }

      kostenLeistungEintrag.setSonstigeKosten(
          kostenLeistungEintrag.getSonstigeKosten()
              .add(projektabrechnungSonstige.getSonstigeKosten()));
      kostenLeistungEintrag.setSonstigeLeistung(
          kostenLeistungEintrag.getSonstigeLeistung()
              .add(projektabrechnungSonstige.getSonstigeLeistung()));
    }
  }

  private void erzeugeKostenLeistungJeMitarbeiterFuerBerechneteLeistung(Long id,
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    List<ProjektabrechnungBerechneteLeistung> projektabrechnungBerechneteLeistungs = projektabrechnungBerechneteLeistungService.projektabrechnungBerechneteLeistungByProjektabrechnungId(
        id);
    for (ProjektabrechnungBerechneteLeistung projektabrechnungBerechneteLeistung : projektabrechnungBerechneteLeistungs) {
      ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
          projektabrechnungBerechneteLeistung.getMitarbeiterId());

      if (kostenLeistungEintrag == null) {
        ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
        if (projektabrechnungBerechneteLeistung.getMitarbeiterId() == null) {
          neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
        } else {
          neuerKostenLeistungEintrag.setMitarbeiterId(
              projektabrechnungBerechneteLeistung.getMitarbeiterId());
        }
        kostenlistenEintragProMitarbeiter.put(
            projektabrechnungBerechneteLeistung.getMitarbeiterId(),
            neuerKostenLeistungEintrag);
        kostenLeistungEintrag = neuerKostenLeistungEintrag;
      }

      kostenLeistungEintrag.setFakturierfaehigeLeistung(
          kostenLeistungEintrag.getSonstigeLeistung()
              .add(projektabrechnungBerechneteLeistung.getLeistung()));
    }
  }

  private void ergaenzeOhneMitarbeiterbezug(
      Map<Long, ProjektabrechnungKostenLeistung> kostenlistenEintragProMitarbeiter) {
    ProjektabrechnungKostenLeistung kostenLeistungEintrag = kostenlistenEintragProMitarbeiter.get(
        null);

    if (kostenLeistungEintrag != null) {
      return;
    }

    ProjektabrechnungKostenLeistung neuerKostenLeistungEintrag = new ProjektabrechnungKostenLeistung();
    neuerKostenLeistungEintrag.setOhneMitarbeiterBezug(true);
    kostenlistenEintragProMitarbeiter.put(null, neuerKostenLeistungEintrag);
  }

  public Projektabrechnung projektabrechnungById(Long id) {
    Projektabrechnung projektabrechnung = this.projektabrechnungRepository.findById(id).get();

    boolean korrekturVorhanden = this.projektabrechnungKorrekturbuchungService
        .ladeProjektabrechnungKorrekturbuchungenByProjektId(projektabrechnung.getProjektId())
        .stream().anyMatch((element) -> element.getProjektabrechnungId() != null
            && element.getProjektabrechnungId().equals(id));
    projektabrechnung.setKorrekturVorhanden(korrekturVorhanden);

    return projektabrechnung;
  }

  public Projektabrechnung projektabrechnungByProjektIdMonatJahr(Long projektId, Integer monat,
      Integer jahr) {
    return projektabrechnungRepository.projektabrechungByProjektIdMonatJahr(projektId, monat,
        jahr);
  }

  public Projektabrechnung vorhergehendeProjektabrechnungByProjektIdMonatJahr(
      Long projektId,
      Integer monat,
      Integer jahr) {
    Optional<Projektabrechnung> optionalProjektabrechnung = projektabrechnungRepository.vorhergehendeProjektabrechungenByProjektIdMonatJahr(
        projektId, monat, jahr);
    return optionalProjektabrechnung.orElse(null);
  }

  public List<ProjektAbrechnungsmonat> abrechnungsmonateByProjektId(Long projektId) {
    List<ProjektAbrechnungsmonat> abrechnungsmonate = IterableUtils.toList(
        this.projektabrechnungRepository.findProjektAbrechnungsmonateByProjektId(
            projektId));

    return abrechnungsmonate;
  }

  public List<ProjektabrechnungBerechneteLeistung> berechneNeueFertigstellung(
      Long projektabrechnungId,
      BigDecimal monatFertigstellung) {
    Projektabrechnung aktuelleProjektabrechnung = this.projektabrechnungById(projektabrechnungId);
    Projektabrechnung vorherigeProjektabrechnung = this.vorhergehendeProjektabrechnungByProjektIdMonatJahr(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getMonat(),
        aktuelleProjektabrechnung.getJahr()
    );

    BigDecimal bisherFertigstellung =
        vorherigeProjektabrechnung != null ? vorherigeProjektabrechnung.getFertigstellungsgrad()
            : ZERO;

    LocalDate aktuellerStichtag = LocalDate.of(aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat(), 1);
    ProjektBudget aktuellesBudget = this.projektBudgetService.ladeProjektbudgetByProjektIdMitStichtag(
        aktuelleProjektabrechnung.getProjektId(), aktuellerStichtag);
    LocalDate vormonatZumStichtag = aktuellerStichtag.minusMonths(1);
    ProjektBudget bisherigesBudget = vorherigeProjektabrechnung != null
        ? this.projektBudgetService.ladeProjektbudgetByProjektIdMitStichtag(
        vorherigeProjektabrechnung.getProjektId(), vormonatZumStichtag)
        : null;
    BigDecimal bisherBudget =
        bisherigesBudget != null ? bisherigesBudget.getSaldoBerechnet() : ZERO;

    List<LeistungKumuliert> vergangeneLeistungKumuliert = this.ladeVergangeneLeistungenFuerProjekt(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());

    BigDecimal leistungOhneMitarbeiterBezug = this.ladeOhneMitarbeiterbezugVerteilteLeistung(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());

    List<ProjektabrechnungBerechneteLeistung> vergangeneBerechneteLeistungen = this.projektabrechnungBerechneteLeistungService.ladeVergangeneLeistungen(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());

    List<LeistungKumuliert> akutelleLeistungKumumliert = this.getAkutelleLeistungKumumliert(
        aktuelleProjektabrechnung);
    List<LeistungKumuliert> aktuelleKorrekturen = this.projektabrechnungKorrekturbuchungService.ladeAktuelleKorrekturenFuerProjektMonatUndJahr(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());
    akutelleLeistungKumumliert.addAll(aktuelleKorrekturen);

    return this.festpreisBerechnung.berechneFestpreis(
        aktuelleProjektabrechnung,
        bisherBudget,
        aktuellesBudget.getSaldoBerechnet(),
        bisherFertigstellung,
        monatFertigstellung,
        vergangeneLeistungKumuliert,
        leistungOhneMitarbeiterBezug,
        vergangeneBerechneteLeistungen,
        akutelleLeistungKumumliert);
  }

  public ProjektabrechnungFertigstellungInitialDaten getProjektabrechnungFertigstellungInitialDaten(
      Long projektabrechnungId, BigDecimal fertigstellungsgrad) {
    ProjektabrechnungFertigstellungInitialDaten projektabrechnungFertigstellungInitialDaten = new ProjektabrechnungFertigstellungInitialDaten();

    Projektabrechnung aktuelleProjektabrechnung = this.projektabrechnungById(projektabrechnungId);
    Projektabrechnung vorherigeProjektabrechnung = this.vorhergehendeProjektabrechnungByProjektIdMonatJahr(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getMonat(),
        aktuelleProjektabrechnung.getJahr()
    );

    if (vorherigeProjektabrechnung != null && !vorherigeProjektabrechnung.getStatusId()
        .equals(50)) {
      projektabrechnungFertigstellungInitialDaten.getMeldungen().add(
          "Fertigstellungsgrad kann nicht administriert werden, der vorhergehende Monat ist noch nicht abgeschlossen.");
      return projektabrechnungFertigstellungInitialDaten;
    }

    this.setzeBudgetFuerFertigstellungInitialDatenAb(projektabrechnungFertigstellungInitialDaten,
        aktuelleProjektabrechnung, vorherigeProjektabrechnung);
    if (!projektabrechnungFertigstellungInitialDaten.getMeldungen().isEmpty()) {
      return projektabrechnungFertigstellungInitialDaten;
    }
    this.setzeFertigstellungsgradFuerFertigstellungInitialDaten(
        projektabrechnungFertigstellungInitialDaten, aktuelleProjektabrechnung,
        vorherigeProjektabrechnung, fertigstellungsgrad);
    this.setzeLeistungFuerFertigstellungInitialDaten(
        projektabrechnungFertigstellungInitialDaten, aktuelleProjektabrechnung);

    return projektabrechnungFertigstellungInitialDaten;
  }

  private void setzeBudgetFuerFertigstellungInitialDatenAb(
      ProjektabrechnungFertigstellungInitialDaten projektabrechnungFertigstellungInitialDaten,
      Projektabrechnung aktuelleProjektabrechnung, Projektabrechnung vorherigeProjektabrechnung) {
    LocalDate aktuellerStichtag = LocalDate.of(aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat(), 1);
    ProjektBudget aktuellesBudget = this.projektBudgetService.ladeProjektbudgetByProjektIdMitStichtag(
        aktuelleProjektabrechnung.getProjektId(), aktuellerStichtag);
    if (aktuellesBudget == null) {
      projektabrechnungFertigstellungInitialDaten.getMeldungen()
          .add("Es wurde noch kein Projektbudget für das aktuelle Projekt administriert");
      return;
    }

    LocalDate vormonatZumStichtag = aktuellerStichtag.minusMonths(1);
    ProjektBudget bisherigesBudget = vorherigeProjektabrechnung != null
        ? this.projektBudgetService.ladeProjektbudgetByProjektIdMitStichtag(
        vorherigeProjektabrechnung.getProjektId(), vormonatZumStichtag)
        : null;

    projektabrechnungFertigstellungInitialDaten.setBisherProjektbudget(
        bisherigesBudget != null ? bisherigesBudget.getSaldoBerechnet()
            : ZERO);
    projektabrechnungFertigstellungInitialDaten.setMonatProjektbudget(
        aktuellesBudget.getSaldoBerechnet());
  }

  private void setzeFertigstellungsgradFuerFertigstellungInitialDaten(
      ProjektabrechnungFertigstellungInitialDaten projektabrechnungFertigstellungInitialDaten,
      Projektabrechnung aktuelleProjektabrechnung, Projektabrechnung vorherigeProjektabrechnung,
      BigDecimal fertigstellungsgrad) {
    if (vorherigeProjektabrechnung != null
        && vorherigeProjektabrechnung.getFertigstellungsgrad() != null) {
      projektabrechnungFertigstellungInitialDaten.setBisherFertigstellung(
          vorherigeProjektabrechnung.getFertigstellungsgrad());
    } else {
      projektabrechnungFertigstellungInitialDaten.setBisherFertigstellung(ZERO);
    }
    if (aktuelleProjektabrechnung.getFertigstellungsgrad() != null
        && !aktuelleProjektabrechnung.getFertigstellungsgrad().equals(fertigstellungsgrad)) {
      projektabrechnungFertigstellungInitialDaten.setMonatFertigstellung(fertigstellungsgrad);
    } else if (aktuelleProjektabrechnung.getFertigstellungsgrad() != null) {
      projektabrechnungFertigstellungInitialDaten.setMonatFertigstellung(
          aktuelleProjektabrechnung.getFertigstellungsgrad());
    } else {
      projektabrechnungFertigstellungInitialDaten.setMonatFertigstellung(ZERO);
    }
  }

  public void setzeLeistungFuerFertigstellungInitialDaten(
      ProjektabrechnungFertigstellungInitialDaten projektabrechnungFertigstellungInitialDaten,
      Projektabrechnung aktuelleProjektabrechnung) {

    List<LeistungKumuliert> akutelleLeistungKumumliert = this.getAkutelleLeistungKumumliert(
        aktuelleProjektabrechnung);
    List<LeistungKumuliert> aktuelleKorrekturen = this.projektabrechnungKorrekturbuchungService.ladeAktuelleKorrekturenFuerProjektMonatUndJahr(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());
    akutelleLeistungKumumliert.addAll(aktuelleKorrekturen);
    BigDecimal monatLeistungRechnerisch = akutelleLeistungKumumliert.stream()
        .map(LeistungKumuliert::getLeistung).reduce(ZERO, BigDecimal::add);
    projektabrechnungFertigstellungInitialDaten.setMonatLeistungRechnerisch(
        monatLeistungRechnerisch);

    List<LeistungKumuliert> vergangeneLeistungKumuliert = this.ladeVergangeneLeistungenFuerProjekt(
        aktuelleProjektabrechnung.getProjektId(), aktuelleProjektabrechnung.getJahr(),
        aktuelleProjektabrechnung.getMonat());
    BigDecimal bisherLeistungRechnerisch = vergangeneLeistungKumuliert.stream()
        .map(LeistungKumuliert::getLeistung).reduce(ZERO, BigDecimal::add);
    projektabrechnungFertigstellungInitialDaten.setBisherLeistungRechnerisch(
        bisherLeistungRechnerisch);
  }

  private List<LeistungKumuliert> getAkutelleLeistungKumumliert(
      Projektabrechnung aktuelleProjektabrechnung) {
    List<ProjektabrechnungKostenLeistung> aktuelleKostenLeistungJeMitarbeiter = this.getKostenLeistungJeMitarbeiter(
        aktuelleProjektabrechnung.getId());
    return aktuelleKostenLeistungJeMitarbeiter.stream().map(
        element -> {
          BigDecimal leistung = element.getSonstigeLeistung().add(element.getReiseLeistung())
              .add(element.getSonderzeitLeistung()).add(element.getProjektzeitLeistung());
          return new LeistungKumuliert(element.getMitarbeiterId(), leistung);
        }).collect(Collectors.toList());
  }

  public List<LeistungKumuliert> ladeVergangeneLeistungenFuerProjekt(Long projektId,
      Integer jahr, Integer monat) {
    return IterableUtils.toList(
        this.projektabrechnungRepository.ladeVergangeneLeistungenFuerProjekt(
            projektId, jahr, monat));
  }

  public BigDecimal ladeOhneMitarbeiterbezugVerteilteLeistung(Long projektId, Integer jahr,
      Integer monat) {
    BigDecimal ohneMitarbeiterbezugVerteilteLeistung = this.projektabrechnungRepository.ladeOhneMitarbeiterbezugVerteilteLeistung(
        projektId,
        jahr, monat);
    return ohneMitarbeiterbezugVerteilteLeistung != null ? ohneMitarbeiterbezugVerteilteLeistung
        : ZERO;
  }

  public boolean mitarbeiterHatMehrAlsBerechneteLeistung(Long projektabrechnungId,
      Long mitarbeiterId) {
    return this.projektabrechnungRepository.anzahlAnElementenFuerMitarbeiter(projektabrechnungId,
        mitarbeiterId) > 0;
  }

  public Projektabrechnung speichereProjektabrechnung(Projektabrechnung projektabrechnung) {
    return projektabrechnungRepository.save(projektabrechnung);
  }

  public void aktualisiereProjektabrechnungStatus(Long projektabrechnungId,
      Integer projektabrechnungStatusId) {
    projektabrechnungRepository.updateProjektabrechnungStatus(projektabrechnungId,
        projektabrechnungStatusId);
  }

  public void loescheObsoleteProjektabrechnungen(Integer jahr, Integer monat,
      Set<Long> idsVonProjektenMitGeloeschtenProjektstunden) {
    if (idsVonProjektenMitGeloeschtenProjektstunden.size() > 0) {
      projektabrechnungRepository.deleteObsoleteProjektabrechnungen(
          idsVonProjektenMitGeloeschtenProjektstunden, monat, jahr);
    }
  }

  public HashSet<Long> speichereReiseDatenInProjektabrechnung(
      final Arbeitsnachweis arbeitsnachweis,
      final Mitarbeiter mitarbeiter, final List<Beleg> belegeZumAenderungsvergleich,
      final List<Beleg> neueBelege, final List<Beleg> aktualisierteBelege,
      final List<Beleg> geloeschteBelege,
      final List<Abwesenheit> abwesenheitenZumAenderungsvergleich,
      final List<Abwesenheit> neueAbwesenheiten, final List<Abwesenheit> aktualisierteAbwesenheiten,
      final List<Abwesenheit> geloeschteAbwesenheiten,
      final List<Projektstunde> projektstundenZumAenderungsvergleich,
      final List<Projektstunde> projektstundenTatsaechlicheReisezeit,
      final List<Projektstunde> projektstundenAngerechneteReisezeit,
      final List<Projektstunde> neueProjektstunden,
      final List<Projektstunde> aktualisierteProjektstunden,
      final List<Projektstunde> geloeschteProjektstunden) {

    HashSet<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();

    // Aufteilung der Belege nach Projekt
    List<Beleg> unveraenderteBelege = belegeZumAenderungsvergleich.stream()
        .collect(Collectors.toList()); // List muss mutable sein
    unveraenderteBelege.removeAll(neueBelege);
    unveraenderteBelege.removeAll(aktualisierteBelege);

    // Aufteilung der Spesen und Zuschläge nach Projekt
    List<Abwesenheit> unveraenderteAbwesenheiten = abwesenheitenZumAenderungsvergleich.stream()
        .collect(Collectors.toList()); // List muss mutable sein
    unveraenderteAbwesenheiten.removeAll(neueAbwesenheiten);
    unveraenderteAbwesenheiten.removeAll(aktualisierteAbwesenheiten);

    // Projektänderungen (Änderung der Projektnummer) in den geänderten Sätzen erkennen.
    // Wird eine Projektnummer innerhalb eines bestehenden Satzes geändert, muss das ursprüngliche
    // Projekt ermittelt werden, um dieses anzupassen.
    List<Beleg> belegeDerenProjektGeaendertWurde = new ArrayList<>();
    for (Beleg originalBeleg : belegeZumAenderungsvergleich) {
      for (Beleg geaenderterBelege : aktualisierteBelege) {
        if (originalBeleg.getId().longValue() == geaenderterBelege.getId().longValue()
            && !originalBeleg.getProjektId().equals(geaenderterBelege.getProjektId())) {
          belegeDerenProjektGeaendertWurde.add(originalBeleg);
        }
      }
    }
    List<Abwesenheit> abwesenheitenDerenProjektGeaendertWurde = new ArrayList<>();
    for (Abwesenheit originalBeleg : abwesenheitenZumAenderungsvergleich) {
      for (Abwesenheit geaenderteAbwesenheit : aktualisierteAbwesenheiten) {
        if (originalBeleg.getId().longValue() == geaenderteAbwesenheit.getId().longValue()
            && !originalBeleg.getProjektId().equals(geaenderteAbwesenheit.getProjektId())) {
          abwesenheitenDerenProjektGeaendertWurde.add(originalBeleg);
        }
      }
    }
    List<Projektstunde> projektstundenDerenProjektnummerGeaendertWurde = new ArrayList<>();
    for (Projektstunde originaleStunden : projektstundenZumAenderungsvergleich) {
      for (Projektstunde geaenderteStunden : aktualisierteProjektstunden) {
        if (originaleStunden.getId().longValue() == geaenderteStunden.getId().longValue()
            && !originaleStunden.getProjektId().equals(geaenderteStunden.getProjektId())) {
          projektstundenDerenProjektnummerGeaendertWurde.add(originaleStunden);
        }
      }
    }

    ProjektstundeTypKonstante tatsaechlicheReisezeitKonstante = konstantenService.projektstundeTypByTextKurz(
        ProjektstundeTyp.TATSAECHLICHE_REISEZEIT.getValue());

    // ProjektabrechnungReise-Einträge zu nicht mehr vorhandenen Projekten löschen
    Set<Long> projekteDieVonDerLoeschungBetroffenSind = new HashSet<>();
    for (Beleg beleg : belegeDerenProjektGeaendertWurde) {
      projekteDieVonDerLoeschungBetroffenSind.add(beleg.getProjektId());
    }
    for (Beleg beleg : geloeschteBelege) {
      projekteDieVonDerLoeschungBetroffenSind.add(beleg.getProjektId());
    }
    for (Abwesenheit abwesenheit : abwesenheitenDerenProjektGeaendertWurde) {
      projekteDieVonDerLoeschungBetroffenSind.add(abwesenheit.getProjektId());
    }
    for (Abwesenheit abwesenheit : geloeschteAbwesenheiten) {
      projekteDieVonDerLoeschungBetroffenSind.add(abwesenheit.getProjektId());
    }
    for (Projektstunde projektstunden : projektstundenDerenProjektnummerGeaendertWurde) {
      projekteDieVonDerLoeschungBetroffenSind.add(projektstunden.getProjektId());
    }
    for (Projektstunde projektstunden : geloeschteProjektstunden.stream().filter(
        projektstunde -> projektstunde.getProjektstundeTypId()
            .equals(tatsaechlicheReisezeitKonstante.getId())).toList()) {
      projekteDieVonDerLoeschungBetroffenSind.add(projektstunden.getProjektId());
    }

    for (Long projektId : projekteDieVonDerLoeschungBetroffenSind) {
      Projektabrechnung projektabrechnung = projektabrechnungByProjektIdMonatJahr(projektId,
          arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
      if (projektabrechnung != null) {

        boolean projektWeiterhinRelevant = ermittleObProjektWeiterhinRelevantIst(neueBelege,
            aktualisierteBelege, neueAbwesenheiten, aktualisierteAbwesenheiten, projektId);

        if (!projektWeiterhinRelevant) {
          loescheAlleReiseEintraegeZuProjektabrechnungUndMitarbeiter(projektabrechnung.getId(),
              mitarbeiter.getId());
        }

        if (!projektabrechnung.getStatusId()
            .equals(ERFASST.toStatusId())) {
          imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
          aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
              ERFASST);
        }

      }
    }

    // Alle relevanten Projekte identifizieren
    Set<Long> projekteDesArbeitsnachweis = new HashSet<>();
    projekteDesArbeitsnachweis.addAll(
        unveraenderteBelege.stream().map(beleg -> beleg.getProjektId()).toList());
    projekteDesArbeitsnachweis.addAll(
        unveraenderteAbwesenheiten.stream().map(abwesenheit -> abwesenheit.getProjektId())
            .toList());
    projekteDesArbeitsnachweis.addAll(
        projektstundenTatsaechlicheReisezeit.stream().map(stunden -> stunden.getProjektId())
            .toList());
    projekteDesArbeitsnachweis.addAll(
        projektstundenAngerechneteReisezeit.stream().map(stunden -> stunden.getProjektId())
            .toList());

    HashMap<Long, Projektabrechnung> projektabrechnungProProjekt = new HashMap<>();

    // Projektabrechnung-Objekte für Projekte laden/initialisieren
    for (Long projektId : projekteDesArbeitsnachweis) {

      boolean projektWurdeAngepasst = ermittleObProjektStatusZurueckGesetztWerdenMuss(projektId,
          neueBelege, aktualisierteBelege, neueAbwesenheiten, aktualisierteAbwesenheiten,
          neueProjektstunden, aktualisierteProjektstunden, projektstundenAngerechneteReisezeit);

      if (projektWurdeAngepasst) {

        Projektabrechnung projektabrechnung = projektabrechnungByProjektIdMonatJahr(projektId,
            arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
        if (projektabrechnung == null) {
          projektabrechnung = new Projektabrechnung();
          projektabrechnung.setJahr(arbeitsnachweis.getJahr());
          projektabrechnung.setMonat(arbeitsnachweis.getMonat());
          projektabrechnung.setProjektId(projektId);
          projektabrechnung.setStatusId(ERFASST.toStatusId());
          projektabrechnung.setKosten(BigDecimal.ZERO);
          projektabrechnung.setUmsatz(BigDecimal.ZERO);
          projektabrechnung = speichereProjektabrechnung(projektabrechnung);
        } else {
          if (!projektabrechnung.getStatusId()
              .equals(ERFASST.toStatusId())) {
            imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
            projektabrechnung.setStatusId(
                ERFASST.toStatusId());
            aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
                ERFASST.toStatusId());
          }
        }

        projektabrechnungProProjekt.put(projektId, projektabrechnung);

        ProjektabrechnungReise projektabrechnungReise = projektabrechnungReiseService.projektabrechnungReiseByProjektabrechnungIdUndMitarbeiterId(
            projektabrechnung.getId(), mitarbeiter.getId());

        if (projektabrechnungReise == null) {
          projektabrechnungReise = new ProjektabrechnungReise();
          projektabrechnungReise.setMitarbeiterId(mitarbeiter.getId());
          projektabrechnungReise.setProjektabrechnungId(
              projektabrechnungProProjekt.get(projektId).getId());
          projektabrechnungReise.setSpesenLeistung(ZERO);
          projektabrechnungReise.setZuschlaegeLeistung(ZERO);
          projektabrechnungReise.setTatsaechlicheReisezeit(ZERO);
          projektabrechnungReise.setPauschaleAnzahl(ZERO);
          projektabrechnungReise.setPauschaleProTag(ZERO);
          projektabrechnungReise.setStundensatz(ZERO);
          projektabrechnungReise.setBelegeViadeeLeistung(ZERO);
          projektabrechnungReise.setBelegeViadeeKosten(ZERO);
          projektabrechnungReise.setBelegeLautArbeitsnachweisLeistung(ZERO);
          projektabrechnungReise.setKostensatz(mitarbeiter.getKostensatz());
        }

        // ProjektabrechnungReise-Objekte für Projekte laden/initialisieren
        BigDecimal belegeLautArbeitsnachweis = unveraenderteBelege.stream()
            .filter(beleg -> beleg.getProjektId().equals(projektId))
            .map(beleg -> beleg.getBetrag())
            .reduce(ZERO, BigDecimal::add);
        projektabrechnungReise.setBelegeLautArbeitsnachweisKosten(belegeLautArbeitsnachweis);
        BigDecimal spesen = unveraenderteAbwesenheiten.stream()
            .filter(abwesenheit -> abwesenheit.getProjektId().equals(projektId))
            .map(abwesenheit -> abwesenheit.getSpesen()).reduce(ZERO, BigDecimal::add);
        projektabrechnungReise.setSpesenKosten(spesen);
        BigDecimal zuschlaege = unveraenderteAbwesenheiten.stream()
            .filter(abwesenheit -> abwesenheit.getProjektId().equals(projektId))
            .map(abwesenheit -> abwesenheit.getZuschlag()).reduce(ZERO, BigDecimal::add);
        projektabrechnungReise.setZuschlaegeKosten(zuschlaege);
        BigDecimal angerechneteReisezeit = projektstundenAngerechneteReisezeit.stream()
            .filter(stunden -> stunden.getProjektId().equals(projektId))
            .map(stunden -> stunden.getAnzahlStunden()).reduce(ZERO, BigDecimal::add);
        projektabrechnungReise.setAngerechneteReisezeit(angerechneteReisezeit);
        BigDecimal tatsaechlicheReisezeit = projektstundenTatsaechlicheReisezeit.stream()
            .filter(stunden -> stunden.getProjektId().equals(projektId))
            .map(stunden -> stunden.getAnzahlStunden()).reduce(ZERO, BigDecimal::add);
        projektabrechnungReise.setTatsaechlicheReisezeitInformatorisch(tatsaechlicheReisezeit);

        if (projektabrechnungReise.getId() == null) {
          projektabrechnungReiseService.speichereProjektabrechnungReise(projektabrechnungReise);
        } else if (projektabrechnungReise.keineWerteVorhanden()) {
          projektabrechnungReiseService.loescheProjektabrechnungReise(
              projektabrechnungReise.getId());
        } else {
          projektabrechnungReiseService.speichereProjektabrechnungReise(projektabrechnungReise);
        }
      }
    }

    loescheObsoleteProjektabrechnungen(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
        projekteDieVonDerLoeschungBetroffenSind);

    return imStatusZurueckgesetzteProjekte;
  }

  private void aktualisiereProjektabrechnungStatus(Long projektabrechnungId,
      ProjektabrechnungBearbeitungsstatus status) {
    projektabrechnungRepository.updateProjektabrechnungStatus(projektabrechnungId,
        status.toStatusId());
  }

  private void loescheAlleReiseEintraegeZuProjektabrechnungUndMitarbeiter(
      final Long projektabrechnungId, final Long mitarbeiterId) {
    projektabrechnungReiseService.loescheAlleReiseEintraegeZuProjektabrechnungUndMitarbeiter(
        projektabrechnungId,
        mitarbeiterId);
  }

  private boolean ermittleObProjektWeiterhinRelevantIst(final List<Beleg> neueBelege,
      final List<Beleg> aktualisierteBelege, final List<Abwesenheit> neueAbwesenheiten,
      final List<Abwesenheit> aktualisierteAbwesenheiten, final Long projektId) {
    boolean projektWeiterhinRelevant = neueAbwesenheiten.stream()
        .anyMatch(abwesenheit -> abwesenheit.getProjektId().equals(projektId));
    projektWeiterhinRelevant = projektWeiterhinRelevant || aktualisierteAbwesenheiten.stream()
        .anyMatch(abwesenheit -> abwesenheit.getProjektId().equals(projektId));
    projektWeiterhinRelevant = projektWeiterhinRelevant || neueBelege.stream()
        .anyMatch(beleg -> beleg.getProjektId().equals(projektId));
    projektWeiterhinRelevant = projektWeiterhinRelevant || aktualisierteBelege.stream()
        .anyMatch(beleg -> beleg.getProjektId().equals(projektId));
    return projektWeiterhinRelevant;
  }

  private boolean ermittleObProjektStatusZurueckGesetztWerdenMuss(final Long projektId,
      final List<Beleg> neueBelege, final List<Beleg> aktualisierteBelege,
      final List<Abwesenheit> neueAbwesenheiten, final List<Abwesenheit> aktualisierteAbwesenheiten,
      final List<Projektstunde> neueProjektstunden,
      final List<Projektstunde> aktualisierteProjektstunden,
      final List<Projektstunde> projektstundenAngerechneteReisezeit) {
    List<Projektstunde> projektAktualisierteProjektstunden = aktualisierteProjektstunden.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
    List<Projektstunde> projektNeueProjektstunden = neueProjektstunden.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();

    List<Beleg> projektAktualisierteBelege = aktualisierteBelege.stream()
        .filter(beleg -> beleg.getProjektId().equals(projektId)).toList();
    List<Beleg> projektNeueBelege = neueBelege.stream()
        .filter(beleg -> beleg.getProjektId().equals(projektId)).toList();

    List<Abwesenheit> projektAktualisierteAbwesenheiten = aktualisierteAbwesenheiten.stream()
        .filter(abwesenheit -> abwesenheit.getProjektId().equals(projektId)).toList();
    List<Abwesenheit> projektNeueAbwesenheiten = neueAbwesenheiten.stream()
        .filter(abwesenheit -> abwesenheit.getProjektId().equals(projektId)).toList();

    List<Projektstunde> projektAktualisierteAngerechneteReisezeit = projektstundenAngerechneteReisezeit.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();

    return !projektAktualisierteProjektstunden.isEmpty() || !projektNeueProjektstunden.isEmpty()
        || !projektAktualisierteBelege.isEmpty() || !projektNeueBelege.isEmpty()
        || !projektAktualisierteAbwesenheiten.isEmpty() || !projektNeueAbwesenheiten.isEmpty()
        || !projektAktualisierteAngerechneteReisezeit.isEmpty();
  }

  public Set<Long> speichereSonderarbeitDatenInProjektabrechnung(
      final Arbeitsnachweis arbeitsnachweis, final Mitarbeiter mitarbeiter,
      final List<Projektstunde> projektstundenZumAenderungsvergleich,
      final List<Projektstunde> aktualisierteSonderarbeitszeiten,
      final List<Projektstunde> neueSonderarbeitszeiten,
      final List<Projektstunde> geloeschteSonderarbeitszeiten,
      final List<Projektstunde> aktualisierteRufbereitschaften,
      final List<Projektstunde> neueRufbereitschaften,
      final List<Projektstunde> geloeschteRufbereitschaften) {

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();
    HashSet<Long> vonLoeschungBetroffeneProjekte = new HashSet<>();

    ProjektstundeTypKonstante sonder = konstantenService.projektstundeTypByTextKurz(
        ProjektstundeTyp.SONDERARBEITSZEIT.getValue());
    ProjektstundeTypKonstante ruf = konstantenService.projektstundeTypByTextKurz(
        ProjektstundeTyp.RUFBEREITSCHAFT.getValue());
    final List<Projektstunde> unveraenderteSonderarbeitszeiten = projektstundenZumAenderungsvergleich.stream()
        .filter(projektstunde -> projektstunde.getProjektstundeTypId().equals(sonder.getId()))
        .collect(Collectors.toList()); // List muss mutable sein
    unveraenderteSonderarbeitszeiten.removeAll(aktualisierteSonderarbeitszeiten);
    unveraenderteSonderarbeitszeiten.removeAll(geloeschteSonderarbeitszeiten);
    final List<Projektstunde> unveraenderteRufbereitschaften = projektstundenZumAenderungsvergleich.stream()
        .filter(projektstunde -> projektstunde.getProjektstundeTypId().equals(ruf.getId()))
        .collect(Collectors.toList()); // List muss mutable sein
    unveraenderteRufbereitschaften.removeAll(aktualisierteRufbereitschaften);
    unveraenderteRufbereitschaften.removeAll(geloeschteRufbereitschaften);

    // Projektänderungen (Änderung der Projektnummer) in den geänderten Sätzen erkennen.
    // Wird eine Projektnummer innerhalb eines bestehenden Satzes geändert, muss das ursprüngliche
    // Projekt ermittelt werden, um dieses anzupassen.
    List<Projektstunde> sonderarbeitszeitenUndRufbereitschaften = new ArrayList<>();
    sonderarbeitszeitenUndRufbereitschaften.addAll(unveraenderteSonderarbeitszeiten);
    sonderarbeitszeitenUndRufbereitschaften.addAll(unveraenderteRufbereitschaften);
    Set<Long> projekteDieVonDerLoeschungBetroffenSind = new HashSet<>();
    for (Projektstunde originaleStunden : projektstundenZumAenderungsvergleich) {
      for (Projektstunde geaenderteStunden : sonderarbeitszeitenUndRufbereitschaften) {
        if (originaleStunden.getId().longValue() == geaenderteStunden.getId().longValue()
            && !originaleStunden.getProjektId().equals(geaenderteStunden.getProjektId())) {
          projekteDieVonDerLoeschungBetroffenSind.add(originaleStunden.getProjektId());
        }
      }
    }
    for (Projektstunde geloeschteSonderarbeitProjektstunden : geloeschteSonderarbeitszeiten) {
      projekteDieVonDerLoeschungBetroffenSind.add(
          geloeschteSonderarbeitProjektstunden.getProjektId());
    }

    for (Projektstunde geloeschteRufbereitschaftProjektstunden : geloeschteRufbereitschaften) {
      projekteDieVonDerLoeschungBetroffenSind.add(
          geloeschteRufbereitschaftProjektstunden.getProjektId());
    }

    for (Long projektId : projekteDieVonDerLoeschungBetroffenSind) {
      Projektabrechnung projektabrechnung = projektabrechnungByProjektIdMonatJahr(projektId,
          arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
      if (projektabrechnung != null) {

        List<Projektstunde> neueOderGeaenderteStunden = new ArrayList<>();
        neueOderGeaenderteStunden.addAll(neueSonderarbeitszeiten);
        neueOderGeaenderteStunden.addAll(neueRufbereitschaften);
        neueOderGeaenderteStunden.addAll(aktualisierteRufbereitschaften);
        neueOderGeaenderteStunden.addAll(aktualisierteSonderarbeitszeiten);
        boolean projektWeiterhinRelevant = neueOderGeaenderteStunden.stream()
            .anyMatch(stunden -> stunden.getProjektId().equals(projektId));

        if (!projektWeiterhinRelevant) {
          projektabrechnungSonderarbeitService.loescheAlleSonderarbeitEintraegeZuProjektabrechnungUndMitarbeiter(
              projektabrechnung.getId(), arbeitsnachweis.getMitarbeiterId());
        }

        if (!projektabrechnung.getStatusId()
            .equals(ERFASST.toStatusId())) {
          imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
          aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
              ERFASST.toStatusId());
        }
      }
    }

    // Alle relevanten Projekte identifizieren
    Set<Long> projekteDesArbeitsnachweis = new HashSet<>();
    projekteDesArbeitsnachweis.addAll(
        unveraenderteSonderarbeitszeiten.stream().map(stunden -> stunden.getProjektId())
            .toList());
    projekteDesArbeitsnachweis.addAll(
        unveraenderteRufbereitschaften.stream().map(stunden -> stunden.getProjektId()).toList());

    HashMap<Long, Projektabrechnung> projektabrechnungProProjekt = new HashMap<>();

    // Projektabrechnung-Objekte für Projekte laden/initialisieren
    for (Long projektId : projekteDesArbeitsnachweis) {

      boolean projektWurdeAngepasst = ermittleObProjektAngepasstWurde(
          aktualisierteSonderarbeitszeiten, neueSonderarbeitszeiten,
          aktualisierteRufbereitschaften,
          neueRufbereitschaften, projektId);

      if (projektWurdeAngepasst) {

        Projektabrechnung projektabrechnung = projektabrechnungByProjektIdMonatJahr(projektId,
            arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
        if (projektabrechnung == null) {
          projektabrechnung = new Projektabrechnung();
          projektabrechnung.setJahr(arbeitsnachweis.getJahr());
          projektabrechnung.setMonat(arbeitsnachweis.getMonat());
          projektabrechnung.setProjektId(projektId);
          projektabrechnung.setStatusId(ERFASST.toStatusId());
          projektabrechnung.setKosten(BigDecimal.ZERO);
          projektabrechnung.setUmsatz(BigDecimal.ZERO);
          projektabrechnung = speichereProjektabrechnung(projektabrechnung);
        } else {
          if (!projektabrechnung.getStatusId()
              .equals(ERFASST.toStatusId())) {
            imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
            projektabrechnung.setStatusId(
                ERFASST.toStatusId());
            aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
                ERFASST.toStatusId());
          }
        }
        projektabrechnungProProjekt.put(projektId, projektabrechnung);

        ProjektabrechnungSonderarbeit projektabrechnungSonderarbeit = projektabrechnungSonderarbeitService.projektabrechnungSonderarbeitByProjektabrechnungIdMitarbeiterId(
            projektabrechnung.getId(), arbeitsnachweis.getMitarbeiterId());

        if (projektabrechnungSonderarbeit == null) {
          projektabrechnungSonderarbeit = new ProjektabrechnungSonderarbeit();
          projektabrechnungSonderarbeit.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
          projektabrechnungSonderarbeit.setProjektabrechnungId(
              projektabrechnungProProjekt.get(projektId).getId());
          projektabrechnungSonderarbeit.setSonderarbeitPauschale(ZERO);
          projektabrechnungSonderarbeit.setRufbereitschaftLeistungAnzahlStunden(ZERO);
          projektabrechnungSonderarbeit.setRufbereitschaftStundensatz(ZERO);
          projektabrechnungSonderarbeit.setRufbereitschaftLeistungAnzahlStunden(ZERO);
          projektabrechnungSonderarbeit.setRufbereitschaftLeistungPauschale(ZERO);
          projektabrechnungSonderarbeit.setSonderarbeitLeistungPauschale(ZERO);
        }

        // ProjektabrechnungReise-Objekte für Projekte laden/initialisieren
        SonderarbeitszeitWochentagVerteilung verteilung = zeitrechnung.getSonderarbeitszeitWochentagVerteilung(
            unveraenderteSonderarbeitszeiten.stream()
                .filter(stunden -> stunden.getProjektId().equals(projektId)).toList());
        BigDecimal rufbereitschaftAnzahlStunden = unveraenderteRufbereitschaften.stream()
            .filter(rufbereitschaft -> rufbereitschaft.getProjektId().equals(projektId))
            .map(rufbereitschaft -> rufbereitschaft.getAnzahlStunden())
            .reduce(ZERO, BigDecimal::add);
        projektabrechnungSonderarbeit.setSonderarbeitAnzahlStunden100(
            verteilung.getSonntagFeiertag());
        projektabrechnungSonderarbeit.setSonderarbeitAnzahlStunden50(
            verteilung.getSamstag().add(verteilung.getWerktag()));
        projektabrechnungSonderarbeit.setRufbereitschaftKostenAnzahlStunden(
            rufbereitschaftAnzahlStunden.setScale(2, HALF_UP));
        projektabrechnungSonderarbeit.setSonderarbeitKostensatz(mitarbeiter.getKostensatz());
        final BigDecimal rufbereitschaftErstattung =
            parameterService.valueByKey("rufbereitschaftErstattung") == null ? ZERO
                : new BigDecimal(parameterService.valueByKey("rufbereitschaftErstattung"));
        projektabrechnungSonderarbeit.setRufbereitschaftKostensatz(rufbereitschaftErstattung);

        if (projektabrechnungSonderarbeit.getId() == null) {
          projektabrechnungSonderarbeitService.speichereProjektabrechnungSonderarbeit(
              projektabrechnungSonderarbeit);
        } else if (projektabrechnungSonderarbeit.keineWerteVorhanden()) {
          vonLoeschungBetroffeneProjekte.add(projektId);
          projektabrechnungSonderarbeitService.loescheById(projektabrechnungSonderarbeit.getId());
        } else {
          projektabrechnungSonderarbeitService.speichereProjektabrechnungSonderarbeit(
              projektabrechnungSonderarbeit);
        }

      }
    }

    loescheObsoleteProjektabrechnungen(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
        projekteDieVonDerLoeschungBetroffenSind);

    return imStatusZurueckgesetzteProjekte;

  }

  @Transactional("pabDbTransactionManager")
  public SonstigeProjektkostenSpeichernResponse speichereViadeeAuslage(
      SonstigeProjektkostenSpeichernRequest sonstigeProjektkostenSpeichernRequest) {
    SonstigeProjektkostenSpeichernResponse sonstigeProjektkostenSpeichernResponse = new SonstigeProjektkostenSpeichernResponse();
    // Es werden die Original Datensaetze zum Abgleich geholt
    List<SonstigeProjektkosten> listeZumAenderungsvergleich = new ArrayList<>();
    for (SonstigeProjektkosten sonstigeProjektkosten : sonstigeProjektkostenSpeichernRequest.getAktualisierteSonstigeProjektkosten()) {
      SonstigeProjektkosten sonstigeProjektkostenOriginal = sonstigeProjektkostenService.ladeSonstigeProjektkostenById(
          sonstigeProjektkosten.getId());
      if (sonstigeProjektkostenOriginal != null) {
        listeZumAenderungsvergleich.add(sonstigeProjektkostenOriginal);
      }
    }
    sonstigeProjektkostenSpeichernRequest.setListeZumAenderungsvergleich(
        listeZumAenderungsvergleich);

    SonstigeProjektkostenSpeichernRequest aktualisierteSonstigeProjektkostenSpeichernRequest = pruefeAufMitarbeiterWechselUndPasseZwischenspeicherAn(
        sonstigeProjektkostenSpeichernRequest);

    sonstigeProjektkostenService.loescheSonstigeProjektkostenByID(
        aktualisierteSonstigeProjektkostenSpeichernRequest.getGeloeschteSonstigeProjektkosten());
    sonstigeProjektkostenService.aktualisiereSonstigeProjektkostenByID(
        aktualisierteSonstigeProjektkostenSpeichernRequest.getAktualisierteSonstigeProjektkosten());
    sonstigeProjektkostenService.fuegeSonstigeProjektkostenEin(
        aktualisierteSonstigeProjektkostenSpeichernRequest.getNeueSonstigeProjektkosten());

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();

    imStatusZurueckgesetzteProjekte.addAll(
        berechneProjektabrechnungReiseSonstige(
            aktualisierteSonstigeProjektkostenSpeichernRequest.getNeueSonstigeProjektkosten()));
    imStatusZurueckgesetzteProjekte.addAll(
        berechneProjektabrechnungReiseSonstige(
            aktualisierteSonstigeProjektkostenSpeichernRequest.getAktualisierteSonstigeProjektkosten()));
    imStatusZurueckgesetzteProjekte.addAll(
        berechneProjektabrechnungReiseSonstige(
            aktualisierteSonstigeProjektkostenSpeichernRequest.getGeloeschteSonstigeProjektkosten()));
    imStatusZurueckgesetzteProjekte.addAll(
        berechneProjektabrechnungReiseSonstige(listeZumAenderungsvergleich));

    for (SonstigeProjektkosten geloschterSonstigerProjektkostensatz : aktualisierteSonstigeProjektkostenSpeichernRequest.getGeloeschteSonstigeProjektkosten()) {
      Set<Long> betroffeneProjekte = new HashSet<>();
      betroffeneProjekte.add(geloschterSonstigerProjektkostensatz.getProjektId());
      loescheObsoleteProjektabrechnungen(
          geloschterSonstigerProjektkostensatz.getJahr(),
          geloschterSonstigerProjektkostensatz.getMonat(),
          betroffeneProjekte);
    }

    if (!imStatusZurueckgesetzteProjekte.isEmpty()) {
      List<Long> projekteAuflistung = imStatusZurueckgesetzteProjekte.stream().sorted().toList();
      String msg =
          "Die Projektabrechnungen folgender Projekte wurden in den Status \"erfasst\" zurückgesetzt: \n\n"
              + projekteAuflistung;
      sonstigeProjektkostenSpeichernResponse.setZurueckgesetzteProjekte(projekteAuflistung);
      sonstigeProjektkostenSpeichernResponse.setMeldungen(msg);
    }
    return sonstigeProjektkostenSpeichernResponse;
  }

  private boolean ermittleObProjektAngepasstWurde(
      final List<Projektstunde> aktualisierteSonderarbeitszeiten,
      final List<Projektstunde> neueSonderarbeitszeiten,
      final List<Projektstunde> aktualisierteRufbereitschaften,
      final List<Projektstunde> neueRufbereitschaften, final Long projektId) {
    List<Projektstunde> projektAktualisierteSonderarbeit = aktualisierteSonderarbeitszeiten.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
    List<Projektstunde> projektNeueSonderarbeit = neueSonderarbeitszeiten.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
    List<Projektstunde> projektAktualisierteRufbereitschaft = aktualisierteRufbereitschaften.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
    List<Projektstunde> projektNeueRufbereitschaft = neueRufbereitschaften.stream()
        .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();

    return !projektAktualisierteSonderarbeit.isEmpty() || !projektNeueSonderarbeit.isEmpty()
        || !projektAktualisierteRufbereitschaft.isEmpty()
        || !projektNeueRufbereitschaft.isEmpty();
  }

  public void setzeProjektabrechnungenStatusFuerArbeitsnachweisLoeschung(Long arbeitsnachweisId) {
    projektabrechnungRepository.setzeProjektabrechnungenStatusFuerArbeitsnachweisLoeschung(
        arbeitsnachweisId);
  }

  private SonstigeProjektkostenSpeichernRequest pruefeAufMitarbeiterWechselUndPasseZwischenspeicherAn(
      SonstigeProjektkostenSpeichernRequest sonstigeProjektkostenSpeichernRequest) {
    // Wurde der Mitarbeiter an einem Auslagen-Satz geändert, muss der originale Satz gelöscht
    // und ein neuer Satz angelegt werden

    List<SonstigeProjektkosten> mitarbeiterWechselListe = new ArrayList<>();
    for (SonstigeProjektkosten original : sonstigeProjektkostenSpeichernRequest.getListeZumAenderungsvergleich()) {
      Long mitarbeiterIdAusOriginalSatz = original.getMitarbeiterId();
      mitarbeiterWechselListe.addAll(
          sonstigeProjektkostenSpeichernRequest.getAktualisierteSonstigeProjektkosten().stream()
              .filter(sonstigeKosten -> sonstigeKosten.getId().equals(original.getId()) && (
                  (mitarbeiterIdAusOriginalSatz == null
                      && sonstigeKosten.getMitarbeiterId() != null) ||
                      (mitarbeiterIdAusOriginalSatz != null
                          && sonstigeKosten.getMitarbeiterId() == null) ||
                      (!mitarbeiterIdAusOriginalSatz.equals(sonstigeKosten.getMitarbeiterId()))
              )).toList());
    }

    for (SonstigeProjektkosten mitarbeiterIstNeu : mitarbeiterWechselListe) {
      SonstigeProjektkosten neuAnzulegen = new SonstigeProjektkosten();
      neuAnzulegen.setJahr(mitarbeiterIstNeu.getJahr());
      neuAnzulegen.setMonat(mitarbeiterIstNeu.getMonat());
      neuAnzulegen.setBelegartId(mitarbeiterIstNeu.getBelegartId());
      neuAnzulegen.setBemerkung(mitarbeiterIstNeu.getBemerkung());
      neuAnzulegen.setKosten(mitarbeiterIstNeu.getKosten());
      neuAnzulegen.setMitarbeiterId(mitarbeiterIstNeu.getMitarbeiterId());
      neuAnzulegen.setProjektId(mitarbeiterIstNeu.getProjektId());
      neuAnzulegen.setViadeeAuslagenKostenartId(mitarbeiterIstNeu.getViadeeAuslagenKostenartId());

      sonstigeProjektkostenSpeichernRequest.getNeueSonstigeProjektkosten().add(neuAnzulegen);
      sonstigeProjektkostenSpeichernRequest.getAktualisierteSonstigeProjektkosten()
          .remove(mitarbeiterIstNeu);
      sonstigeProjektkostenSpeichernRequest.getGeloeschteSonstigeProjektkosten()
          .add(mitarbeiterIstNeu);
    }

    return sonstigeProjektkostenSpeichernRequest;
  }

  private Set<Long> berechneProjektabrechnungReiseSonstige(
      final List<SonstigeProjektkosten> geaenderteSonstigeProjektkostenList) {

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();

    for (SonstigeProjektkosten geaenderteSonstigeProjektkosten : geaenderteSonstigeProjektkostenList) {
      if (geaenderteSonstigeProjektkosten.getViadeeAuslagenKostenartId()
          == ViadeeAuslagenKostenarten.SONSTIGES.toId()) {
        imStatusZurueckgesetzteProjekte.addAll(
            berechneProjektabrechnungSonstige(geaenderteSonstigeProjektkosten));
      } else if (geaenderteSonstigeProjektkosten.getViadeeAuslagenKostenartId()
          == ViadeeAuslagenKostenarten.REISE.toId()) {
        imStatusZurueckgesetzteProjekte.addAll(
            berechneProjektabrechnungReise(geaenderteSonstigeProjektkosten));
      }
    }

    return imStatusZurueckgesetzteProjekte;
  }

  private Set<Long> berechneProjektabrechnungSonstige(
      SonstigeProjektkosten geaendertesonstigeProjektkosten) {

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();
    Set<Long> vonLoeschungBetroffeneProjekte = new HashSet<>(); // TODO Wird nicht benutzt?

    long mitarbeiterID = geaendertesonstigeProjektkosten.getMitarbeiterId() >= 0
        ? geaendertesonstigeProjektkosten.getMitarbeiterId() : Long.MIN_VALUE;

    ProjektabrechnungSonstige projektabrechnungSonstige = projektabrechnungSonstigeService.projektabrechnungSonstigeByMonatJahrProjektIdUndMitarbeiter(
        geaendertesonstigeProjektkosten.getMonat(),
        geaendertesonstigeProjektkosten.getJahr(),
        mitarbeiterID,
        geaendertesonstigeProjektkosten.getProjektId());

    Projektabrechnung projektabrechnung = projektabrechnungRepository.findAllByProjektIdAndMonatAndJahr(
        geaendertesonstigeProjektkosten.getProjektId(),
        geaendertesonstigeProjektkosten.getMonat(),
        geaendertesonstigeProjektkosten.getJahr());
    if (projektabrechnung == null) {
      projektabrechnung = new Projektabrechnung();
      projektabrechnung.setJahr(geaendertesonstigeProjektkosten.getJahr());
      projektabrechnung.setMonat(geaendertesonstigeProjektkosten.getMonat());
      projektabrechnung.setProjektId(geaendertesonstigeProjektkosten.getProjektId());
      projektabrechnung.setStatusId(ERFASST.toStatusId());
      projektabrechnung.setKosten(ZERO);
      projektabrechnung.setUmsatz(ZERO);
      projektabrechnungRepository.save(projektabrechnung);

      Long neuesProjektId = projektabrechnungRepository.findAllByProjektIdAndMonatAndJahr(
          projektabrechnung.getProjektId(),
          projektabrechnung.getMonat(),
          projektabrechnung.getJahr()).getId();

      projektabrechnung.setId(neuesProjektId);
    } else {
      // Den Status der Projektabrechnung auf Erfasst ändern
      if (!projektabrechnung.getStatusId()
          .equals(ERFASST.toStatusId())) {

        projektabrechnung.setStatusId(ERFASST.toStatusId());
        projektabrechnungRepository.save(projektabrechnung);
        imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
      }
    }
    if (projektabrechnungSonstige == null) {
      projektabrechnungSonstige = new ProjektabrechnungSonstige();
      projektabrechnungSonstige.setPauschaleKosten(ZERO);
      projektabrechnungSonstige.setPauschaleLeistung(ZERO);
      projektabrechnungSonstige.setMitarbeiterId(
          geaendertesonstigeProjektkosten.getMitarbeiterId());
      projektabrechnungSonstige.setProjektabrechnungId(
          projektabrechnung.getId());
    }

    BigDecimal summeAuslagenViadee = ZERO;
    List<SonstigeProjektkosten> projektkostenFuerSummeList = sonstigeProjektkostenService.ladeSonstigeProjektkostenByMonatJahrMitarbeiterProjektID(
        geaendertesonstigeProjektkosten.getMonat(),
        geaendertesonstigeProjektkosten.getJahr(), mitarbeiterID,
        geaendertesonstigeProjektkosten.getProjektId());
    for (SonstigeProjektkosten projektkostenFuerSumme : projektkostenFuerSummeList) {
      if (projektkostenFuerSumme.getViadeeAuslagenKostenartId()
          == ViadeeAuslagenKostenarten.SONSTIGES.toId()) {
        summeAuslagenViadee = summeAuslagenViadee.add(projektkostenFuerSumme.getKosten());
      }
    }

    projektabrechnungSonstige.setViadeeAuslagen(summeAuslagenViadee);

    if (projektabrechnungSonstige.keineWerteVorhanden()) {
      if (projektabrechnungSonstige.getId() != null) {
        projektabrechnungSonstigeService.loescheByProjektabrechnungSonstigeId(
            projektabrechnungSonstige.getId());
      }
    } else {
      projektabrechnungSonstigeService.aktualisiereOderFuegeSonstigeProjektkostenEin(
          projektabrechnungSonstige);
    }

    loescheObsoleteProjektabrechnungen(
        projektabrechnung.getJahr(),
        projektabrechnung.getMonat(),
        vonLoeschungBetroffeneProjekte);

    return imStatusZurueckgesetzteProjekte;
  }

  private Set<Long> berechneProjektabrechnungReise(
      SonstigeProjektkosten geaenderteSonstigeProjektkosten) {

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();

    ProjektabrechnungReise projektabrechnungReise = projektabrechnungReiseService.projektabrechnungReiseByMonatJahrUndMitarbeiterId(
        geaenderteSonstigeProjektkosten.getMonat(),
        geaenderteSonstigeProjektkosten.getJahr(),
        geaenderteSonstigeProjektkosten.getMitarbeiterId(),
        geaenderteSonstigeProjektkosten.getProjektId());

    Projektabrechnung projektabrechnung = projektabrechnungRepository.findAllByProjektIdAndMonatAndJahr(
        geaenderteSonstigeProjektkosten.getProjektId(),
        geaenderteSonstigeProjektkosten.getMonat(),
        geaenderteSonstigeProjektkosten.getJahr());
    if (projektabrechnung == null) {
      projektabrechnung = new Projektabrechnung();
      projektabrechnung.setMonat(geaenderteSonstigeProjektkosten.getMonat());
      projektabrechnung.setJahr(geaenderteSonstigeProjektkosten.getJahr());
      projektabrechnung.setProjektId(geaenderteSonstigeProjektkosten.getProjektId());
      projektabrechnung.setStatusId(ERFASST.toStatusId());
      projektabrechnung.setKosten(ZERO);
      projektabrechnung.setUmsatz(ZERO);

      projektabrechnungRepository.save(projektabrechnung);
      projektabrechnung.setId(projektabrechnungRepository.findAllByProjektIdAndMonatAndJahr(
          projektabrechnung.getProjektId(),
          projektabrechnung.getMonat(),
          projektabrechnung.getJahr()).getId()); // TODO Wenn null? Ist das moeglich?
    } else {
      // Den Status der Projektabrechnung auf Erfasst ändern
      if (!projektabrechnung.getStatusId()
          .equals(ERFASST.toStatusId())) {
        projektabrechnung.setStatusId(ERFASST.toStatusId());
        projektabrechnungRepository.save(projektabrechnung);
        imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
      }
    }

    if (projektabrechnungReise == null) {
      projektabrechnungReise = new ProjektabrechnungReise();
      projektabrechnungReise.setProjektabrechnungId(projektabrechnung.getId());
      projektabrechnungReise.setMitarbeiterId(geaenderteSonstigeProjektkosten.getMitarbeiterId());
      projektabrechnungReise.setZuschlaegeLeistung(ZERO);
      projektabrechnungReise.setZuschlaegeKosten(ZERO);
      projektabrechnungReise.setSpesenLeistung(ZERO);
      projektabrechnungReise.setBelegeLautArbeitsnachweisKosten(ZERO);
      projektabrechnungReise.setBelegeLautArbeitsnachweisLeistung(ZERO);
      projektabrechnungReise.setSpesenKosten(ZERO);
      projektabrechnungReise.setBelegeViadeeLeistung(ZERO);
      projektabrechnungReise.setStundensatz(ZERO);
      projektabrechnungReise.setPauschaleProTag(ZERO);
      projektabrechnungReise.setPauschaleAnzahl(ZERO);
      projektabrechnungReise.setPauschaleProTag(ZERO);
      Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
          geaenderteSonstigeProjektkosten.getMitarbeiterId());
      projektabrechnungReise.setKostensatz(mitarbeiter.getKostensatz());
      projektabrechnungReise.setTatsaechlicheReisezeit(ZERO);
      projektabrechnungReise.setAngerechneteReisezeit(ZERO);
    }

    BigDecimal summeAuslagenViadee = ZERO;
    List<SonstigeProjektkosten> projektkostenFuerSummeList = sonstigeProjektkostenService.ladeSonstigeProjektkostenByMonatJahrMitarbeiterProjektID(
        geaenderteSonstigeProjektkosten.getMonat(),
        geaenderteSonstigeProjektkosten.getJahr(),
        geaenderteSonstigeProjektkosten.getMitarbeiterId(),
        geaenderteSonstigeProjektkosten.getProjektId());
    for (SonstigeProjektkosten projektkostenFuerSumme : projektkostenFuerSummeList) {
      if (projektkostenFuerSumme.getViadeeAuslagenKostenartId()
          == ViadeeAuslagenKostenarten.REISE.toId()) {
        summeAuslagenViadee = summeAuslagenViadee.add(projektkostenFuerSumme.getKosten());
      }
    }

    projektabrechnungReise.setBelegeViadeeKosten(summeAuslagenViadee);

    if (projektabrechnungReise.keineWerteVorhanden()) {
      if (projektabrechnungReise.getId() != null) {
        projektabrechnungReiseService.loescheProjektabrechnungReise(
            projektabrechnungReise.getId());
      }
    } else {
      projektabrechnungReiseService.speichereProjektabrechnungReise(projektabrechnungReise);
    }

    return imStatusZurueckgesetzteProjekte;
  }

  public Projektabrechnung leseOderErstelleUndLeseProjektabrechnungAus(
      Projektabrechnung projektabrechnung) {
    Projektabrechnung projektAusDb = projektabrechnungRepository.findAllByProjektIdAndMonatAndJahr(
        projektabrechnung.getProjektId(),
        projektabrechnung.getMonat(), projektabrechnung.getJahr());
    if (projektAusDb != null) {
      return projektAusDb;
    }
    projektabrechnungRepository.save(projektabrechnung);
    return projektabrechnung;
  }

  public List<Projektabrechnung> getProjekteNichtBereitFuerMonatsabschluss(
      int jahr, int monat) {
    List<Projekt> projekteMitFehlenderProjektabrechnung = projektService.projekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
        jahr, monat);
    List<Projekt> aktiveProjekteMitFehlenderProjektabrechnung = projekteMitFehlenderProjektabrechnung.stream()
        .filter(Projekt::getIstAktiv).collect(Collectors.toList());
    List<Projektabrechnung> nochNichtAbgerechneteProjektabrechnungen = IterableUtils.toList(
        projektabrechnungRepository.findProjektabrechnungenKleinerGleichStatusFuerAbrechnungsmonat(
            ERFASST.toStatusId(),
            jahr, monat));

    List<Projektabrechnung> projekteNichtBereitFuerMonatsabschluss = new ArrayList<>();
    ueberfuehreProjekteNachNichtBereitFuerMonatsabschluss(
        aktiveProjekteMitFehlenderProjektabrechnung, projekteNichtBereitFuerMonatsabschluss);
    projekteNichtBereitFuerMonatsabschluss.addAll(nochNichtAbgerechneteProjektabrechnungen);
    return projekteNichtBereitFuerMonatsabschluss;
  }

  private void ueberfuehreProjekteNachNichtBereitFuerMonatsabschluss(
      final List<Projekt> aktiveProjekteMitFehlenderProjektabrechnung,
      final List<Projektabrechnung> projekteNichtBereitFuerMonatsabschluss) {
    for (Projekt projekt : aktiveProjekteMitFehlenderProjektabrechnung) {
      Projektabrechnung projektabrechnung = new Projektabrechnung();
      final LocalDate vormonat = zeitrechnung.getVormonat();
      projektabrechnung.setProjektId(projekt.getId());
      projektabrechnung.setJahr(vormonat.getYear());
      projektabrechnung.setMonat(vormonat.getMonthValue());
      projekteNichtBereitFuerMonatsabschluss.add(projektabrechnung);
    }
  }
}
