package de.viadee.pabbackend.services.fachobjekt;


import static de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus.ABGERECHNET;
import static de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus.ERFASST;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.ANGERECHNETE_REISEZEIT;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.NORMAL;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.RUFBEREITSCHAFT;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.SONDERARBEITSZEIT;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.TATSAECHLICHE_REISEZEIT;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.FIRMA;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.KEIN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

import de.viadee.pabbackend.entities.Abrechnungsmonat;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernRequest;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernResponse;
import de.viadee.pabbackend.entities.ArbeitsnachweisUebersicht;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.LohnartberechnungErgebnis;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterAbrechnungsmonat;
import de.viadee.pabbackend.entities.MitarbeiterNichtBereitFuerMonatsabschluss;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeit;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus;
import de.viadee.pabbackend.repositories.pabdb.ArbeitsnachweisRepository;
import de.viadee.pabbackend.services.berechnung.AngerechneteReisezeitBerechnung;
import de.viadee.pabbackend.services.berechnung.Lohnartenberechnung;
import de.viadee.pabbackend.services.berechnung.MitarbeiterStundenKontoBerechnung;
import de.viadee.pabbackend.services.berechnung.MitarbeiterUrlaubKontoBerechnung;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArbeitsnachweisService {

  private final ArbeitsnachweisRepository arbeitsnachweisRepository;
  private final ProjektstundeService projektstundeService;
  private final MitarbeiterService mitarbeiterService;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;
  private final ProjektService projektService;
  private final ProjektabrechnungService projektabrechnungService;
  private final AbwesenheitService abwesenheitService;
  private final BelegService belegService;
  private final ArbeitsnachweisLohnartZuordnungService arbeitsnachweisLohnartZuordnungService;
  private final ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService;
  private final ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService;
  private final ProjektabrechnungReiseService projektabrechnungReiseService;
  private final ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService;
  private final FehlerlogService fehlerlogService;
  private final KonstantenService konstantenService;
  private final AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung;
  private final DreiMonatsRegelService dreiMonatsRegelService;
  private final MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung;
  private final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung;
  private final Lohnartenberechnung lohnartenberechnung;

  public ArbeitsnachweisService(final ArbeitsnachweisRepository arbeitsnachweisRepository,
      ProjektstundeService projektstundeService, final MitarbeiterService mitarbeiterService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      ProjektService projektService, ProjektabrechnungService projektabrechnungService,
      AbwesenheitService abwesenheitService, BelegService belegService,
      ArbeitsnachweisLohnartZuordnungService arbeitsnachweisLohnartZuordnungService,
      final ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService,
      ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService,
      ProjektabrechnungReiseService projektabrechnungReiseService,
      ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService,
      FehlerlogService fehlerlogService, KonstantenService konstantenService,
      AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung,
      DreiMonatsRegelService dreiMonatsRegelService,
      MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung,
      MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung,
      Lohnartenberechnung lohnartenberechnung) {
    this.arbeitsnachweisRepository = arbeitsnachweisRepository;
    this.projektstundeService = projektstundeService;
    this.mitarbeiterService = mitarbeiterService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.projektService = projektService;
    this.projektabrechnungService = projektabrechnungService;
    this.abwesenheitService = abwesenheitService;
    this.belegService = belegService;
    this.arbeitsnachweisLohnartZuordnungService = arbeitsnachweisLohnartZuordnungService;
    this.projektabrechnungBerechneteLeistungService = projektabrechnungBerechneteLeistungService;
    this.projektabrechnungProjektzeitService = projektabrechnungProjektzeitService;
    this.projektabrechnungReiseService = projektabrechnungReiseService;
    this.projektabrechnungSonderarbeitService = projektabrechnungSonderarbeitService;
    this.fehlerlogService = fehlerlogService;
    this.konstantenService = konstantenService;
    this.angerechneteReisezeitBerechnung = angerechneteReisezeitBerechnung;
    this.dreiMonatsRegelService = dreiMonatsRegelService;
    this.mitarbeiterStundenKontoBerechnung = mitarbeiterStundenKontoBerechnung;
    this.mitarbeiterUrlaubKontoBerechnung = mitarbeiterUrlaubKontoBerechnung;
    this.lohnartenberechnung = lohnartenberechnung;
  }

  public List<ArbeitsnachweisUebersicht> alleArbeitsnachweiseFuerUebersichtGefiltert(
      final int abJahr, final int abMonat, final int bisJahr, final int bisMonat) {
    return IterableUtils.toList(
        arbeitsnachweisRepository.findAlleArbeitsnachweiseFuerUebersichtGefiltert(abJahr, abMonat,
            bisJahr, bisMonat));
  }

  public List<ArbeitsnachweisUebersicht> fehlendeArbeitsnachweiseFuerZeitraum(LocalDate abWann,
      LocalDate bisWann) {
    List<ArbeitsnachweisUebersicht> arbeitsnachweisUebersichts = new ArrayList<>();

    LocalDate abrechnungsmonat = LocalDate.now().minusMonths(1);
    if (abrechnungsmonat.getYear() < abWann.getYear() || (
        abrechnungsmonat.getYear() == abWann.getYear()
            && abrechnungsmonat.getMonthValue() < abWann.getMonthValue())) {
      return arbeitsnachweisUebersichts;
    }
    if (bisWann.getYear() < abrechnungsmonat.getYear() || (
        bisWann.getYear() == abrechnungsmonat.getYear()
            && bisWann.getMonthValue() < abrechnungsmonat.getMonthValue())) {
      return arbeitsnachweisUebersichts;
    }

    Iterable<Mitarbeiter> mitarbeiterMitFehlendemArbeitsnachweis = mitarbeiterService.mitarbeiterUndStudentenDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
        abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue());
    mitarbeiterMitFehlendemArbeitsnachweis.forEach(mitarbeiter -> {
      ArbeitsnachweisUebersicht arbeitsnachweisUebersicht = new ArbeitsnachweisUebersicht();
      arbeitsnachweisUebersicht.setJahr(abrechnungsmonat.getYear());
      arbeitsnachweisUebersicht.setMonat(abrechnungsmonat.getMonthValue());
      arbeitsnachweisUebersicht.setMitarbeiterId(mitarbeiter.getId());
      arbeitsnachweisUebersicht.setSachbearbeiterId(mitarbeiter.getSachbearbeiterId());
      arbeitsnachweisUebersicht.setSummeProjektstunden(new BigDecimal("0"));
      arbeitsnachweisUebersicht.setSummeSpesen(new BigDecimal("0"));
      arbeitsnachweisUebersicht.setSummeBelege(new BigDecimal("0"));
      arbeitsnachweisUebersichts.add(arbeitsnachweisUebersicht);
    });
    return arbeitsnachweisUebersichts;
  }

  public List<Abrechnungsmonat> alleAbrechnungsmonate() {
    return IterableUtils.toList(arbeitsnachweisRepository.findAlleAbrechnungsmonate());
  }

  public List<MitarbeiterAbrechnungsmonat> alleMitarbeiterAbrechnungsmonate(Long mitarbeiterId) {
    return IterableUtils.toList(
        arbeitsnachweisRepository.findAlleAbrechnungsmonateByMitarbeiterId(mitarbeiterId));
  }

  public Arbeitsnachweis arbeitsnachweisById(Long id) {
    return arbeitsnachweisRepository.findById(id).get();
  }

  public Arbeitsnachweis arbeitsnachweisByMitarbeiterIDMonatJahr(Long mitarbeiterId, int monat,
      int jahr) {
    return arbeitsnachweisRepository.findByMitarbeiterIdAndJahrAndMonat(mitarbeiterId, jahr, monat);
  }

  public List<DreiMonatsRegel> dreiMonatsRegelnFuerAbrechnungsmonat(Long arbeitsnachweisId) {
    Arbeitsnachweis arbeitsnachweis = this.arbeitsnachweisById(arbeitsnachweisId);
    return this.dreiMonatsRegelService.dreiMonatsRegelnFuerAbrechnungsmonat(
        arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(),
        arbeitsnachweis.getMonat());
  }

  @Transactional("pabDbTransactionManager")
  public ArbeitsnachweisSpeichernResponse speichereArbeitsnachweis(
      ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest) {

    ArbeitsnachweisSpeichernResponse result = new ArbeitsnachweisSpeichernResponse();

    Arbeitsnachweis zuSpeichernderArbeitsnachweis = arbeitsnachweisSpeichernRequest.getArbeitsnachweis();
    final Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        arbeitsnachweisSpeichernRequest.getMitarbeiterId());

    // Zunächst überprüfen, ob überhaupt ein Stellenfaktor für den MA für den Abrechnungsmonat
    // administriert ist
    if (mitarbeiter.isIntern()
        && mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
            mitarbeiter.getId(), zuSpeichernderArbeitsnachweis.getJahr(),
            zuSpeichernderArbeitsnachweis.getMonat()).getStellenfaktor()
        .equals(new BigDecimal("0.00001"))) {
      result.getMeldungen().add("Der Arbeitsnachweis kann nicht gespeichert werden, es ist für "
          + mitarbeiter.getFullName() + " im Abrechnungsmonat "
          + zuSpeichernderArbeitsnachweis.getJahr() + "/" + zuSpeichernderArbeitsnachweis.getMonat()
          + " kein Stellenfaktor administriert!");
    } else {

      Set<Long> zurueckgesetzteFestpreisprojekte = new HashSet<>();

      if (zuSpeichernderArbeitsnachweis.getId() != null) {
        // Handelt es sich um einen vorher bereits gespeicherten ANW, muss sein Status auf ERFASST
        // zurückgesetzt
        // werden
        pruefeObAbgerechneterArbeitsnachweisGeaendertWurdeUndSetzeStatus(
            zuSpeichernderArbeitsnachweis);

        arbeitsnachweisRepository.save(zuSpeichernderArbeitsnachweis);
        List<Projekt> projekteMitProjektstundenImArbeitsnachweisVorSpeichernDerArbeitsnachweisAenderungen = projektService.alleFestpreisProjekteZuDenenArbeitsnachweisProjektstundenHatUndProjektabrechnungExistiert(
            zuSpeichernderArbeitsnachweis.getId());

        // Festpreisprojekte müssen nach jeder ANW-Änderung neu berechnet werden.
        zurueckgesetzteFestpreisprojekte.addAll(
            projekteMitProjektstundenImArbeitsnachweisVorSpeichernDerArbeitsnachweisAenderungen.stream()
                .map(projekt -> projekt.getId()).toList());

      } else {
        // Ist es ein neuer ANW müssen die Default-Werte für Firmenwagen und Status gesetzt werden
        zuSpeichernderArbeitsnachweis.setFirmenwagen(mitarbeiter.getFirmenwagen());
        zuSpeichernderArbeitsnachweis.setStatusId(ERFASST.value());
        zuSpeichernderArbeitsnachweis = arbeitsnachweisRepository.save(
            zuSpeichernderArbeitsnachweis);
      }

      speichereFehlerlog(zuSpeichernderArbeitsnachweis);

      if (zuSpeichernderArbeitsnachweis.getFehlerlog() != null
          && !zuSpeichernderArbeitsnachweis.getFehlerlog().isEmpty()) {
        final Long arbeitsnachweisId = zuSpeichernderArbeitsnachweis.getId();
        zuSpeichernderArbeitsnachweis.getFehlerlog()
            .forEach(fehlerlog -> {
              fehlerlog.setArbeitsnachweisId(arbeitsnachweisId);
              fehlerlog.setId(null);
            });
        fehlerlogService.loescheFehlerlogByArbeitsnachweisId(zuSpeichernderArbeitsnachweis.getId());
        fehlerlogService.speichereFehlerlog(zuSpeichernderArbeitsnachweis.getFehlerlog());
      }

      // Bei der Speicherung von Projektstunden wird geprüft, ob bereits gespeicherte
      // Projektabrechnungen
      // davon betroffen sind (bspw. wenn diese bereits den Status ABGERECHNET haben). Ist das der
      // Fall wird das zurückgemeldet, sodass im Frontend eine Meldung darüber erfolgen kann
      List<Projektstunde> projektstundenInDB = projektstundeService.alleProjektstundenByArbeitsnachweisId(
          zuSpeichernderArbeitsnachweis.getId());
      result.getZurueckgesetzteProjekte().addAll(
          speicherProjektstundenAenderungen(zuSpeichernderArbeitsnachweis, mitarbeiter,
              projektstundenInDB,
              arbeitsnachweisSpeichernRequest.getNeueProjektstunden(),
              arbeitsnachweisSpeichernRequest.getAktualisierteProjektstunden(),
              arbeitsnachweisSpeichernRequest.getGeloeschteProjektstunden()));

      // Nach der Stundenänderung muss noch einmal geprüft werden, welche Festpreisprojekte auf den
      // Status ERFASST zurückgesetzt werden müssen
      List<Projekt> festpreisprojekteMitProjektstundenImArbeitsnachweisNachSpeichernDerArbeitsnachweisAenderungen = projektService.alleFestpreisProjekteZuDenenArbeitsnachweisProjektstundenHatUndProjektabrechnungExistiert(
          zuSpeichernderArbeitsnachweis.getId());
      zurueckgesetzteFestpreisprojekte.addAll(
          festpreisprojekteMitProjektstundenImArbeitsnachweisNachSpeichernDerArbeitsnachweisAenderungen.stream()
              .map(projekt -> projekt.getId()).toList());
      setzeFestpreiseAufErfasstZurueck(zurueckgesetzteFestpreisprojekte,
          zuSpeichernderArbeitsnachweis.getJahr(), zuSpeichernderArbeitsnachweis.getMonat());
      result.getZurueckgesetzteProjekte().addAll(zurueckgesetzteFestpreisprojekte);

      // Im folgenden Schritt werden basierend auf den Abwesenheiten die gesetzlichen Spesen
      // samt der freiwilligen Zuschläge ermittelt und in den Abwesenheiten gespeichert
      List<Abwesenheit> abwesenheitenZumAenderungsvergleich = abwesenheitService.alleAbwesenheitenByArbeitsnachweisId(
          zuSpeichernderArbeitsnachweis.getId());
      speichereReisekostenUndAuslagenAenderungen(zuSpeichernderArbeitsnachweis, mitarbeiter,
          arbeitsnachweisSpeichernRequest.getNeueAbwesenheiten(),
          arbeitsnachweisSpeichernRequest.getAktualisierteAbwesenheiten(),
          arbeitsnachweisSpeichernRequest.getGeloeschteAbwesenheiten());

      // Belege führen nicht zu Berechnungen und werden einfach gespeichert
      List<Beleg> belegeZumAenderungsvergleich = belegService.alleBelegeByArbeitsnachweisId(
          zuSpeichernderArbeitsnachweis.getId());
      speichereBelegAenderungen(zuSpeichernderArbeitsnachweis,
          arbeitsnachweisSpeichernRequest.getNeueBelege(),
          arbeitsnachweisSpeichernRequest.getAktualisierteBelege(),
          arbeitsnachweisSpeichernRequest.getGeloeschteBelege());

      // Die für den Arbeitsnachweis gespeicherten Abwesenheitsdaten müssen in die
      // zugehörige Projektabrechnung übertragen werden
      ProjektstundeTypKonstante angerechneteReisezeit = konstantenService.projektstundeTypByTextKurz(
          ANGERECHNETE_REISEZEIT.getValue());
      ProjektstundeTypKonstante tatsaechlicheReisezeit = konstantenService.projektstundeTypByTextKurz(
          TATSAECHLICHE_REISEZEIT.getValue());
      ProjektstundeTypKonstante sonder = konstantenService.projektstundeTypByTextKurz(
          SONDERARBEITSZEIT.getValue());
      ProjektstundeTypKonstante normal = konstantenService.projektstundeTypByTextKurz(
          NORMAL.getValue());
      ProjektstundeTypKonstante ruf = konstantenService.projektstundeTypByTextKurz(
          RUFBEREITSCHAFT.getValue());
      List<Projektstunde> gespeicherteProjektstunden = projektstundeService.alleProjektstundenByArbeitsnachweisId(
          zuSpeichernderArbeitsnachweis.getId());
      List<Projektstunde> angerechneteReisezeiten = gespeicherteProjektstunden.stream().filter(
          projektstunde -> projektstunde.getProjektstundeTypId()
              .equals(angerechneteReisezeit.getId())).toList();
      List<Projektstunde> tatsaechlicheReisezeiten = gespeicherteProjektstunden.stream().filter(
          projektstunde -> projektstunde.getProjektstundeTypId()
              .equals(tatsaechlicheReisezeit.getId())).toList();
      List<Projektstunde> sonderarbeitszeiten = gespeicherteProjektstunden.stream().filter(
          projektstunde -> projektstunde.getProjektstundeTypId()
              .equals(sonder.getId())).toList();
      List<Projektstunde> rufbereitschaften = gespeicherteProjektstunden.stream().filter(
          projektstunde -> projektstunde.getProjektstundeTypId()
              .equals(ruf.getId())).toList();
      List<Projektstunde> normaleProjektzeiten = gespeicherteProjektstunden.stream().filter(
          projektstunde -> projektstunde.getProjektstundeTypId()
              .equals(normal.getId())).toList();
      result.getZurueckgesetzteProjekte().addAll(
          projektabrechnungService.speichereReiseDatenInProjektabrechnung(
              zuSpeichernderArbeitsnachweis, mitarbeiter, belegeZumAenderungsvergleich,
              arbeitsnachweisSpeichernRequest.getNeueBelege(),
              arbeitsnachweisSpeichernRequest.getAktualisierteBelege(),
              arbeitsnachweisSpeichernRequest.getGeloeschteBelege(),
              abwesenheitenZumAenderungsvergleich,
              arbeitsnachweisSpeichernRequest.getNeueAbwesenheiten(),
              arbeitsnachweisSpeichernRequest.getAktualisierteAbwesenheiten(),
              arbeitsnachweisSpeichernRequest.getGeloeschteAbwesenheiten(),
              projektstundenInDB,
              tatsaechlicheReisezeiten,
              angerechneteReisezeiten,
              arbeitsnachweisSpeichernRequest.getNeueProjektstunden(),
              arbeitsnachweisSpeichernRequest.getAktualisierteProjektstunden(),
              arbeitsnachweisSpeichernRequest.getGeloeschteProjektstunden()));

      // Die für einen Arbeitsnachweis gespeicherten Sonderarbeitdaten müssen in die
      //  zugehörige Projektabrechnung übertragen werden
      result.getZurueckgesetzteProjekte().addAll(
          projektabrechnungService.speichereSonderarbeitDatenInProjektabrechnung(
              zuSpeichernderArbeitsnachweis, mitarbeiter, projektstundenInDB,
              arbeitsnachweisSpeichernRequest.getAktualisierteSonderabeit(),
              arbeitsnachweisSpeichernRequest.getNeueSonderarbeit(),
              arbeitsnachweisSpeichernRequest.getGeloschteSonderarbeit(),
              arbeitsnachweisSpeichernRequest.getAktualisierteRufbereitschaft(),
              arbeitsnachweisSpeichernRequest.getNeueRufbereitschaft(),
              arbeitsnachweisSpeichernRequest.getGeloeschteRufbereitschaft()));

      // Beim Import berechnete Dreimonatsregeln werden gespeichert.
      // FixMe Berechnung findet aktuell im Import statt (zwecks Anzeige), muss jedoch hier wiederholt werden
      speichereDreiMonatsRegelAenderungen(arbeitsnachweisSpeichernRequest, mitarbeiter);

      // Für interne Mitarbeiter müssen die erfassten Stunden im Stunden- und im Urlaubskonto
      // nachgehalten sowie die Lohnartenberechnung durchgeführt werden
      if (mitarbeiter.isIntern()) {

        // Stundenkonto um Überstunden nachzuhalten
        mitarbeiterStundenKontobuchungenDurchfuehren(zuSpeichernderArbeitsnachweis,
            normaleProjektzeiten,
            sonderarbeitszeiten,
            angerechneteReisezeiten
        );

        // Urlaubskonto um Urlaubsanspruch nachzuhalten
        mitarbeiterUrlaubKontobuchungenDurchfuehren(zuSpeichernderArbeitsnachweis,
            normaleProjektzeiten);

        // Lohnarten für die Übertragung ans Lohnbüro
        List<Beleg> gespeicherteBelege = belegService.alleBelegeByArbeitsnachweisId(
            zuSpeichernderArbeitsnachweis.getId());
        List<Abwesenheit> gespeicherteAbwesenheiten = abwesenheitService.alleAbwesenheitenByArbeitsnachweisId(
            zuSpeichernderArbeitsnachweis.getId());
        lohnartenBerechnenUndSpeichern(zuSpeichernderArbeitsnachweis, gespeicherteBelege,
            gespeicherteAbwesenheiten, sonderarbeitszeiten, rufbereitschaften);
      }

      result.setArbeitsnachweis(zuSpeichernderArbeitsnachweis);

    }

    return result;
  }

  private void speichereFehlerlog(final Arbeitsnachweis arbeitsnachweis) {
    if (arbeitsnachweis.getFehlerlog() != null
        && !arbeitsnachweis.getFehlerlog().isEmpty()) {
      final Long arbeitsnachweisId = arbeitsnachweis.getId();
      arbeitsnachweis.getFehlerlog()
          .forEach(fehlerlog -> {
            fehlerlog.setArbeitsnachweisId(arbeitsnachweisId);
            fehlerlog.setId(null);
          });
      fehlerlogService.loescheFehlerlogByArbeitsnachweisId(arbeitsnachweis.getId());
      fehlerlogService.speichereFehlerlog(arbeitsnachweis.getFehlerlog());
    }
  }

  @Transactional("pabDbTransactionManager")
  public void lohnartenBerechnenUndSpeichern(

      final Arbeitsnachweis arbeitsnachweis,
      final List<Beleg> belege,
      final List<Abwesenheit> abwesenheiten,
      final List<Projektstunde> sonderarbeitszeiten,
      final List<Projektstunde> rufbereitschaften) {

    final String smartphoneSelektion =
        arbeitsnachweis.getSmartphoneEigen() == null ? KEIN.toString() :
            arbeitsnachweis.getSmartphoneEigen() ? EIGEN.toString() : FIRMA.toString();
    BigDecimal auszahlung =
        arbeitsnachweis.getAuszahlung() == null ? ZERO : arbeitsnachweis.getAuszahlung();

    final LohnartberechnungErgebnis lohnartenBerechnungsErgebnis = lohnartenberechnung
        .berechneLohnarten(arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten,
            sonderarbeitszeiten, rufbereitschaften, auszahlung);

    arbeitsnachweisLohnartZuordnungService.loescheArbeitsnachweisLohnartZuordnungByArbeitsnachweisID(
        arbeitsnachweis.getId());
    arbeitsnachweisLohnartZuordnungService.loescheLohnartberechnungLogByArbeitsnachweisID(
        arbeitsnachweis.getId());

    arbeitsnachweisLohnartZuordnungService.speichereArbeitsnachweisLohnartZuordnung(
        lohnartenBerechnungsErgebnis.getArbeitsnachweisLohnartZuordnung());

    arbeitsnachweisLohnartZuordnungService.speichereLohnartberechnungLog(
        lohnartenBerechnungsErgebnis.getLohnartberechnungLog());


  }

  private void speichereDreiMonatsRegelAenderungen(
      final ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest,
      final Mitarbeiter mitarbeiter) {

    // Dreimonatsregeln werden nur beim Excel-Import berechnet
    if (arbeitsnachweisSpeichernRequest.getBerechneteDreiMonatsRegeln() != null
        && !arbeitsnachweisSpeichernRequest.getBerechneteDreiMonatsRegeln().isEmpty()) {

      dreiMonatsRegelService.loescheAutomatischErfassteOffeneDreiMonatsRegeln(mitarbeiter.getId());

      dreiMonatsRegelService.loescheKollidierendeDreiMonatsregeln(
          arbeitsnachweisSpeichernRequest.getBerechneteDreiMonatsRegeln());

      dreiMonatsRegelService.speichereDreiMonatsRegeln(
          arbeitsnachweisSpeichernRequest.getBerechneteDreiMonatsRegeln());

    }
  }

  private void mitarbeiterUrlaubKontobuchungenDurchfuehren(
      Arbeitsnachweis zuSpeichernderArbeitsnachweis, List<Projektstunde> normaleProjektstunden) {
    Projekt urlaub = projektService.projektByProjektnummer("9004");
    BigDecimal anzahlUrlaubProjektstunden = normaleProjektstunden.stream()
        .filter(stunden -> stunden.getProjektId().equals(urlaub.getId()))
        .map(stunden -> stunden.getAnzahlStunden()).reduce(ZERO, BigDecimal::add);
    if (anzahlUrlaubProjektstunden.compareTo(ZERO) != 0) {
      mitarbeiterUrlaubKontoBerechnung.ermittleUndSpeichereMitarbeiterUrlaubKontoAenderungen(
          zuSpeichernderArbeitsnachweis, anzahlUrlaubProjektstunden);
    }
  }

  private void mitarbeiterStundenKontobuchungenDurchfuehren(
      Arbeitsnachweis arbeitsnachweis,
      List<Projektstunde> normaleProjektstunden,
      List<Projektstunde> sonderarbeitszeiten,
      List<Projektstunde> angerechneteReisezeiten) {
    BigDecimal sonderarbeitszeitBuchung = sonderarbeitszeiten
        .stream()
        .map(Projektstunde::getAnzahlStunden).reduce(ZERO, BigDecimal::add).negate()
        .setScale(2, HALF_UP);
    BigDecimal projektstundenSumme = normaleProjektstunden
        .stream()
        .map(Projektstunde::getAnzahlStunden).reduce(ZERO, BigDecimal::add).setScale(2, HALF_UP);
    BigDecimal angerechneteReisezeit = angerechneteReisezeiten
        .stream()
        .map(Projektstunde::getAnzahlStunden).reduce(ZERO,
            BigDecimal::add);
    BigDecimal projektstundenBuchung = projektstundenSumme.add(angerechneteReisezeit)
        .setScale(2, HALF_UP);

    mitarbeiterStundenKontoBerechnung.ermittleUndSpeichereMitarbeiterStundenKontoAenderungen(
        arbeitsnachweis, sonderarbeitszeitBuchung, projektstundenBuchung);
  }

  private void pruefeObAbgerechneterArbeitsnachweisGeaendertWurdeUndSetzeStatus(
      Arbeitsnachweis zuSpeichernderArbeitsnachweis) {
    if (zuSpeichernderArbeitsnachweis.getStatusId().equals(ABGERECHNET.value())) {
      zuSpeichernderArbeitsnachweis.setStatusId(ERFASST.value());
    }
  }

  private void setzeFestpreiseAufErfasstZurueck(final Set<Long> zurueckzusetzendeProjekte,
      final Integer jahr, final Integer monat) {

    for (Long projektId : zurueckzusetzendeProjekte) {
      Projektabrechnung projektabrechnung = projektabrechnungService.projektabrechnungByProjektIdMonatJahr(
          projektId, monat, jahr);
      if (projektabrechnung == null) {
        continue;
      }
      projektabrechnung.setStatusId(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
      projektabrechnung.setBudgetBetragZurAbrechnung(null);
      projektabrechnung.setFertigstellungsgrad(null);

      projektabrechnungService.speichereProjektabrechnung(projektabrechnung);

      projektabrechnungBerechneteLeistungService.loescheByProjektabrechnungId(
          projektabrechnung.getId());
    }
  }

  private Set<Long> speicherProjektstundenAenderungen(final Arbeitsnachweis arbeitsnachweis,
      final Mitarbeiter mitarbeiter,
      final List<Projektstunde> projektstundenVorDemSpeichern,
      final List<Projektstunde> neueProjektstunden,
      final List<Projektstunde> aktualisierteProjektstunden,
      final List<Projektstunde> geloeschteProjektstunden) {

    ProjektstundeTypKonstante angerechneteReisezeit = konstantenService.projektstundeTypByTextKurz(
        ANGERECHNETE_REISEZEIT.getValue());
    ProjektstundeTypKonstante tatsaechlicheReisezeit = konstantenService.projektstundeTypByTextKurz(
        TATSAECHLICHE_REISEZEIT.getValue());

    // Vor der tatsächlichen Speicherung müssen eventuell bestehende Projektstundendaten
    // gelesen werden, sodass nach dem Speichern ermittelt werden kann, welche Projektabrechnungen
    // im Status zurückgesetzt wurden
    List<Projektstunde> berechneteProjektundenVorSpeicherungZumAenderungsvergleich = projektstundenVorDemSpeichern.stream()
        .filter(stunden -> stunden.getProjektstundeTypId().equals(angerechneteReisezeit.getId()))
        .toList();
    List<Projektstunde> unveraenderteProjektstunden = projektstundenVorDemSpeichern.stream()
        .filter(stunden -> !stunden.getProjektstundeTypId().equals(angerechneteReisezeit.getId()))
        .collect(Collectors.toList());
    unveraenderteProjektstunden.removeAll(aktualisierteProjektstunden);
    unveraenderteProjektstunden.removeAll(geloeschteProjektstunden);

    // Durch Änderung der Projektstunden ist eine Neuberechnung der angerechneten Reisezeit
    // notwendig,
    List<Projektstunde> alleFuerReisezeitberechnungRelevantenProjektstunden = new ArrayList<>();
    alleFuerReisezeitberechnungRelevantenProjektstunden.addAll(neueProjektstunden);
    alleFuerReisezeitberechnungRelevantenProjektstunden.addAll(aktualisierteProjektstunden);
    alleFuerReisezeitberechnungRelevantenProjektstunden.addAll(unveraenderteProjektstunden);
    final List<Projektstunde> berechneteProjektstunden = angerechneteReisezeitBerechnung.berechneAngerechneteReisezeit(
        alleFuerReisezeitberechnungRelevantenProjektstunden.stream().filter(
                stunden -> stunden.getProjektstundeTypId().equals(tatsaechlicheReisezeit.getId()))
            .collect(Collectors.toList())); // List muss mutable sein
    neueProjektstunden.stream().forEach(projektstunde -> {
      projektstunde.setId(null);
      projektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    });
    aktualisierteProjektstunden.stream()
        .forEach(projektstunde -> projektstunde.setArbeitsnachweisId(arbeitsnachweis.getId()));
    berechneteProjektstunden.stream().forEach(projektstunden -> {
      projektstunden.setArbeitsnachweisId(arbeitsnachweis.getId());
    });

    // Persistieren der Daten in der Datenbank
    if (!geloeschteProjektstunden.isEmpty()) {
      projektstundeService.loescheProjektstunden(geloeschteProjektstunden);
    }
    if (!aktualisierteProjektstunden.isEmpty()) {
      projektstundeService.speichereProjektstunden(aktualisierteProjektstunden);
    }
    if (!neueProjektstunden.isEmpty()) {
      projektstundeService.speichereProjektstunden(neueProjektstunden);
    }
    projektstundeService.loescheProjekstundenByArbeitsnachweisUndTypTextKurz(
        arbeitsnachweis.getId(), ANGERECHNETE_REISEZEIT.getValue());

    // Nur berechnete Stunden speichern, wenn Anzahl Stunden <> 0 ist
    List<Projektstunde> zuSpeicherndeBerechneteProjektstunden = berechneteProjektstunden.stream()
        .filter(stunde -> stunde.getAnzahlStunden().compareTo(ZERO) == 1).toList();
    if (!zuSpeicherndeBerechneteProjektstunden.isEmpty()) {
      projektstundeService.speichereProjektstunden(zuSpeicherndeBerechneteProjektstunden);
    }

    // Die oben gesammelten Informationen über den vorherigen Datenbestand und die durchgeführten
    // Änderungen
    // dienen als Basis für das, was folglich in die Projektabrechnungen der betroffenen Projekte
    // übertragen
    // werden muss
    return speicherProjektstundenInProjektabrechnung(arbeitsnachweis, mitarbeiter,
        projektstundenVorDemSpeichern,
        berechneteProjektundenVorSpeicherungZumAenderungsvergleich, berechneteProjektstunden,
        aktualisierteProjektstunden, geloeschteProjektstunden, neueProjektstunden,
        unveraenderteProjektstunden);
  }

  private Set<Long> speicherProjektstundenInProjektabrechnung(final Arbeitsnachweis arbeitsnachweis,
      final Mitarbeiter mitarbeiter,
      final List<Projektstunde> projektstundenListeZumAenderungsvergleich,
      final List<Projektstunde> berechneteProjektstundenZumAenderungsvergleich,
      final List<Projektstunde> berechneteProjektstunden,
      final List<Projektstunde> aktualisierteProjektstunden,
      final List<Projektstunde> geloeschteProjektstunden,
      final List<Projektstunde> neueProjektstunden, final List<Projektstunde> projektstundenListe) {
    final ProjektstundeTypKonstante normaleProjektstunden = konstantenService.projektstundeTypByTextKurz(
        NORMAL.getValue());

    Set<Long> imStatusZurueckgesetzteProjekte = new HashSet<>();

    // Projektänderungen (Änderung der Projektnummer) in den geänderten Sätzen erkennen.
    // Wird eine Projektnummer innerhalb eines bestehenden Satzes geändert, muss das ursprüngliche
    // Projekt ermittelt werden, um dieses anzupassen.
    List<Projektstunde> projektstundenDerenProjektnummerGeaendertWurde = new ArrayList<>();
    for (Projektstunde originaleStunden : projektstundenListeZumAenderungsvergleich) {
      for (Projektstunde geaenderteStunden : aktualisierteProjektstunden) {
        if (Objects.equals(originaleStunden.getId(), geaenderteStunden.getId())
            && !originaleStunden.getProjektId().equals(geaenderteStunden.getProjektId())) {
          projektstundenDerenProjektnummerGeaendertWurde.add(originaleStunden);
        }
      }
    }

    // Neuberechnungen der angerechneten Reisezeit führt dazu, dass der Status der Projektabrechnung
    // zurückgesetzt werden muss.
    List<Projektstunde> geaenderteAngerechneteReisezeiten = new ArrayList<>();
    for (Projektstunde alteBerechnung : berechneteProjektstundenZumAenderungsvergleich) {
      for (Projektstunde aktuelleBerechnung : berechneteProjektstunden) {
        if (alteBerechnung.getProjektId().equals(aktuelleBerechnung.getProjektId())) {
          if (!alteBerechnung.getAnzahlStunden().equals(aktuelleBerechnung.getAnzahlStunden())) {
            geaenderteAngerechneteReisezeiten.add(aktuelleBerechnung);
          }
        }
      }
    }
    for (Projektstunde projektstunden : geaenderteAngerechneteReisezeiten) {
      Projektabrechnung projektabrechnung = projektabrechnungService.projektabrechnungByProjektIdMonatJahr(
          projektstunden.getProjektId(), arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
      if (projektabrechnung != null) {
        if (!projektabrechnung.getStatusId()
            .equals(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId())) {
          imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
          projektabrechnungService.aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
              ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
        }
      }
    }

    // Nicht mehr vorhandene Projektstunden löschen
    Map<Long, BigDecimal> geloeschteProjektstundenSummen = new HashMap<>();
    List<Projektstunde> projekteDieVonLoeschungOderAenderungBetroffenSind = new ArrayList<>();
    projekteDieVonLoeschungOderAenderungBetroffenSind.addAll(geloeschteProjektstunden.stream()
        .filter(projektstunden -> projektstunden.getProjektstundeTypId()
            .equals(normaleProjektstunden.getId())).toList());
    projekteDieVonLoeschungOderAenderungBetroffenSind.addAll(
        projektstundenDerenProjektnummerGeaendertWurde);
    for (Projektstunde stunden : projekteDieVonLoeschungOderAenderungBetroffenSind) {
      geloeschteProjektstundenSummen.merge(stunden.getProjektId(), stunden.getAnzahlStunden(),
          BigDecimal::add);
    }
    for (Long projektId : geloeschteProjektstundenSummen.keySet()) {
      Projektabrechnung projektabrechnung = projektabrechnungService.projektabrechnungByProjektIdMonatJahr(
          projektId, arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
      if (projektabrechnung != null) {

        List<Projektstunde> neueOderGeaenderteStunden = new ArrayList<>();
        neueOderGeaenderteStunden.addAll(neueProjektstunden);
        neueOderGeaenderteStunden.addAll(aktualisierteProjektstunden);
        boolean projektWeiterhinRelevant = neueOderGeaenderteStunden.stream()
            .anyMatch(stunden -> stunden.getProjektId().equals(projektId));

        if (!projektWeiterhinRelevant) {
          projektabrechnungProjektzeitService.loescheAlleProjektzeitEintraegeZuProjektabrechnungUndMitarbeiter(
              projektabrechnung.getId(), mitarbeiter.getId());
        }

        if (!projektabrechnung.getStatusId()
            .equals(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId())) {
          imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
          projektabrechnungService.aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
              ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
        }
      }
    }

    // Geänderte und neue Projektstunden in Projektabrechnung übernehmen
    List<Projektstunde> alleRelevantenProjektstunden = new ArrayList<>();
    alleRelevantenProjektstunden.addAll(projektstundenListe.stream().filter(
        projektstunden -> projektstunden.getProjektstundeTypId()
            .equals(normaleProjektstunden.getId())).toList());
    alleRelevantenProjektstunden.addAll(aktualisierteProjektstunden.stream().filter(
        projektstunden -> projektstunden.getProjektstundeTypId()
            .equals(normaleProjektstunden.getId())).toList());
    alleRelevantenProjektstunden.addAll(neueProjektstunden.stream().filter(
        projektstunden -> projektstunden.getProjektstundeTypId()
            .equals(normaleProjektstunden.getId())).toList());

    Map<Long, BigDecimal> projektStundenSummen = new HashMap<>();
    for (Projektstunde stunden : alleRelevantenProjektstunden) {
      projektStundenSummen.merge(stunden.getProjektId(), stunden.getAnzahlStunden(),
          BigDecimal::add);
    }

    for (Long projektId : projektStundenSummen.keySet()) {

      List<Projektstunde> projektAktualisierteProjektstunden = aktualisierteProjektstunden.stream()
          .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
      List<Projektstunde> projektGeloeschteProjektstunden = geloeschteProjektstunden.stream()
          .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();
      List<Projektstunde> projektNeueProjektstunden = neueProjektstunden.stream()
          .filter(stunden -> stunden.getProjektId().equals(projektId)).toList();

      boolean projektWurdeAngepasst = !projektAktualisierteProjektstunden.isEmpty()
          || !projektGeloeschteProjektstunden.isEmpty() || !projektNeueProjektstunden.isEmpty();

      Projektabrechnung projektabrechnung = projektabrechnungService.projektabrechnungByProjektIdMonatJahr(
          projektId, arbeitsnachweis.getMonat(), arbeitsnachweis.getJahr());
      if (projektabrechnung == null) {
        projektabrechnung = new Projektabrechnung();
        projektabrechnung.setJahr(arbeitsnachweis.getJahr());
        projektabrechnung.setMonat(arbeitsnachweis.getMonat());
        projektabrechnung.setProjektId(projektId);
        projektabrechnung.setStatusId(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
        projektabrechnung.setKosten(ZERO);
        projektabrechnung.setUmsatz(ZERO);
        projektabrechnung = projektabrechnungService.speichereProjektabrechnung(projektabrechnung);
      } else {
        if (projektWurdeAngepasst) {
          if (!projektabrechnung.getStatusId()
              .equals(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId())) {
            imStatusZurueckgesetzteProjekte.add(projektabrechnung.getProjektId());
            projektabrechnung.setStatusId(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
            projektabrechnungService.aktualisiereProjektabrechnungStatus(projektabrechnung.getId(),
                ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
          }
        }
      }

      if (projektWurdeAngepasst) {

        List<ProjektabrechnungProjektzeit> projektzeiten = projektabrechnungProjektzeitService.projektabrechnungProjektzeitByProjektabrechnungIdUndMitarbeiterId(
            projektabrechnung.getId(), mitarbeiter.getId(), projektId, arbeitsnachweis.getJahr(),
            arbeitsnachweis.getMonat());

        if (projektzeiten.size() > 1) {
          projektabrechnungProjektzeitService.loescheAlleProjektzeitEintraegeZuProjektabrechnungUndMitarbeiter(
              projektabrechnung.getId(), mitarbeiter.getId());
        }
        ProjektabrechnungProjektzeit projektabrechnungProjektzeit = null;
        if (projektzeiten.size() == 1) {
          projektabrechnungProjektzeit = projektzeiten.get(0);
        } else {
          projektabrechnungProjektzeit = new ProjektabrechnungProjektzeit();
          projektabrechnungProjektzeit.setProjektabrechnungId(projektabrechnung.getId());
          projektabrechnungProjektzeit.setMitarbeiterId(mitarbeiter.getId());
          projektabrechnungProjektzeit.setLaufendeNummer(1);
          projektabrechnungProjektzeit.setStundensatz(ZERO);
          projektabrechnungProjektzeit.setKostensatzVormonat(ZERO);
          projektabrechnungProjektzeit.setStundensatzVormonat(ZERO);
          projektabrechnungProjektzeit.setKostensatzVertrag(ZERO);
        }

        BigDecimal summeFakturierfaehig = alleRelevantenProjektstunden.stream().filter(
                stunden -> stunden.getProjektId().equals(projektId) && stunden.isFakturierfaehig())
            .map(Projektstunde::getAnzahlStunden).reduce(ZERO, BigDecimal::add);

        BigDecimal summeNichtFakturierfaehig = alleRelevantenProjektstunden.stream().filter(
                stunden -> stunden.getProjektId().equals(projektId) && stunden.isNichtFakturierfaehig())
            .map(Projektstunde::getAnzahlStunden).reduce(ZERO, BigDecimal::add);

        if (summeFakturierfaehig.compareTo(ZERO) != 0) {
          projektabrechnungProjektzeit.setStundenLautArbeitsnachweis(summeFakturierfaehig);
        } else {
          projektabrechnungProjektzeit.setStundenLautArbeitsnachweis(summeNichtFakturierfaehig);
        }

        projektabrechnungProjektzeit.setKostensatz(
            mitarbeiter.getKostensatz() == null ? ZERO : mitarbeiter.getKostensatz());

        if (projektabrechnungProjektzeit.getId() == null) {
          projektabrechnungProjektzeitService.speichereProjektabrechnungProjektzeit(
              projektabrechnungProjektzeit);
        } else if (projektabrechnungProjektzeit.keineWerteVorhanden()) {
          projektabrechnungProjektzeitService.loescheProjektabrechnungProjektzeit(
              projektabrechnungProjektzeit.getId());
        } else {
          projektabrechnungProjektzeitService.speichereProjektabrechnungProjektzeit(
              projektabrechnungProjektzeit);
        }

        // Fall fakturierfähiger Stunden und nicht fakturierfähiger Stunden
        if (summeFakturierfaehig.compareTo(ZERO) != 0
            && summeNichtFakturierfaehig.compareTo(ZERO) != 0) {

          projektabrechnungProjektzeit = new ProjektabrechnungProjektzeit();
          projektabrechnungProjektzeit.setProjektabrechnungId(projektabrechnung.getId());
          projektabrechnungProjektzeit.setMitarbeiterId(mitarbeiter.getId());
          projektabrechnungProjektzeit.setLaufendeNummer(2);
          projektabrechnungProjektzeit.setStundensatz(ZERO);
          projektabrechnungProjektzeit.setKostensatzVormonat(ZERO);
          projektabrechnungProjektzeit.setStundensatzVormonat(ZERO);
          projektabrechnungProjektzeit.setKostensatzVertrag(ZERO);
          projektabrechnungProjektzeit.setKostensatz(
              mitarbeiter.getKostensatz() == null ? ZERO : mitarbeiter.getKostensatz());
          projektabrechnungProjektzeit.setStundenLautArbeitsnachweis(summeNichtFakturierfaehig);

          projektabrechnungProjektzeitService.speichereProjektabrechnungProjektzeit(
              projektabrechnungProjektzeit);
        }
      }
    }

    projektabrechnungService.loescheObsoleteProjektabrechnungen(arbeitsnachweis.getJahr(),
        arbeitsnachweis.getMonat(), geloeschteProjektstundenSummen.keySet());

    return imStatusZurueckgesetzteProjekte;
  }

  @Transactional("pabDbTransactionManager")
  public void speichereReisekostenUndAuslagenAenderungen(final Arbeitsnachweis arbeitsnachweis,
      final Mitarbeiter mitarbeiter, final List<Abwesenheit> neueAbwesenheiten,
      final List<Abwesenheit> aktualisierteAbwesenheiten,
      final List<Abwesenheit> geloeschteAbwesenheiten) {

    // Für interne Mitarbeiter müssen Spesen und Zuschläge auf Basis der angegebenen
    // Abwesenheiten ermittelt werden
    if (mitarbeiter.isIntern()) {
      // Neben den neuen und aktualisierten Abwesenheiten sind dafür auch die aus einer früheren Bearbeitung
      // bereits gespeicherten und durch die aktuelle Bearbeitung unveränderten Abwesenheiten relevant
      List<Abwesenheit> unveraenderteAbwesenheiten = abwesenheitService.alleAbwesenheitenByArbeitsnachweisId(
          arbeitsnachweis.getId());
      unveraenderteAbwesenheiten.removeAll(aktualisierteAbwesenheiten);
      unveraenderteAbwesenheiten.removeAll(geloeschteAbwesenheiten);
      abwesenheitService.berechneSpesenUndZuschlaegeUndUebertrageSieNachAbwesenheiten(
          unveraenderteAbwesenheiten, neueAbwesenheiten, aktualisierteAbwesenheiten);
    }

    neueAbwesenheiten.stream().forEach(abwesenheit -> {
      abwesenheit.setId(null);
      abwesenheit.setArbeitsnachweisId(arbeitsnachweis.getId());
      if (abwesenheit.getSpesen() == null) {
        abwesenheit.setSpesen(ZERO);
      }
      if (abwesenheit.getZuschlag() == null) {
        abwesenheit.setZuschlag(ZERO);
      }
    });

    aktualisierteAbwesenheiten.stream().forEach(abwesenheit -> {
      abwesenheit.setArbeitsnachweisId(arbeitsnachweis.getId());
      if (abwesenheit.getSpesen() == null) {
        abwesenheit.setSpesen(ZERO);
      }
      if (abwesenheit.getZuschlag() == null) {
        abwesenheit.setZuschlag(ZERO);
      }
    });

    if (!geloeschteAbwesenheiten.isEmpty()) {
      abwesenheitService.loescheAbwesenheiten(geloeschteAbwesenheiten);
    }
    if (!aktualisierteAbwesenheiten.isEmpty()) {
      abwesenheitService.speichereAbwensenheiten(aktualisierteAbwesenheiten);
    }
    if (!neueAbwesenheiten.isEmpty()) {
      abwesenheitService.speichereAbwensenheiten(neueAbwesenheiten);
    }


  }

  @Transactional("pabDbTransactionManager")
  public void speichereBelegAenderungen(final Arbeitsnachweis arbeitsnachweis,
      final List<Beleg> neueBelege, final List<Beleg> aktualisierteBelege,
      final List<Beleg> geloeschteBelege) {

    neueBelege.stream().forEach(beleg -> {
      beleg.setId(null);
      beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
    });
    aktualisierteBelege.stream().forEach(beleg -> {
      beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
    });

    if (!geloeschteBelege.isEmpty()) {
      belegService.loescheBelege(geloeschteBelege);
    }
    if (!aktualisierteBelege.isEmpty()) {
      belegService.speichereBelege(aktualisierteBelege);
    }
    if (!neueBelege.isEmpty()) {
      belegService.speichereBelege(neueBelege);
    }
  }

  @Transactional("pabDbTransactionManager")
  public Set<Long> loescheArbeitsnachweis(Long id) {
    Set<Long> vonDerLoeschungBetroffeneProjekte = new HashSet<>();
    Optional<Arbeitsnachweis> arbeitsnachweis = arbeitsnachweisRepository.findById(id);

    if (arbeitsnachweis.isPresent()) {
      Arbeitsnachweis zuLoeschenderArbeitsnachweis = arbeitsnachweis.get();
      List<Projekt> festpreisProjekteMitProjektstundenImArbeitsnachweis = projektService.alleFestpreisProjekteZuDenenArbeitsnachweisProjektstundenHatUndProjektabrechnungExistiert(
          zuLoeschenderArbeitsnachweis.getId());
      Integer jahr = zuLoeschenderArbeitsnachweis.getJahr();
      Integer monat = zuLoeschenderArbeitsnachweis.getMonat();

      mitarbeiterStundenKontoBerechnung.loescheMitarbeiterStundenKontosaetzeZuArbeitsnachweis(
          zuLoeschenderArbeitsnachweis);
      mitarbeiterUrlaubKontoBerechnung.loescheMitarbeiterUrlaubKontosaetzeZuArbeitsnachweis(
          zuLoeschenderArbeitsnachweis);

      List<Projekt> alleProjekteZuArbeitsnachweis = projektService.alleProjekteZuArbeitsnachweis(
          zuLoeschenderArbeitsnachweis.getId());
      vonDerLoeschungBetroffeneProjekte = new HashSet<>(
          alleProjekteZuArbeitsnachweis.stream().map(Projekt::getId).toList());
      projektabrechnungService.setzeProjektabrechnungenStatusFuerArbeitsnachweisLoeschung(
          zuLoeschenderArbeitsnachweis.getId());

      projektabrechnungProjektzeitService.loescheProjektabrechnungProjektzeitenFuerArbeitsnachweis(
          zuLoeschenderArbeitsnachweis.getId());
      projektabrechnungReiseService.loescheProjektabrechnungReiseFuerArbeitsnachweis(
          zuLoeschenderArbeitsnachweis.getId());
      projektabrechnungSonderarbeitService.loescheProjektabrechnungSonderarbeitFuerArbeitsnachweis(
          zuLoeschenderArbeitsnachweis.getId());
      fehlerlogService.loescheFehlerlogByArbeitsnachweisId(zuLoeschenderArbeitsnachweis.getId());
      abwesenheitService.loescheByArbeitsnachweisID(zuLoeschenderArbeitsnachweis.getId());
      belegService.loescheByArbeitsnachweisID(zuLoeschenderArbeitsnachweis.getId());
      projektstundeService.loescheByArbeitsnachweisID(zuLoeschenderArbeitsnachweis.getId());
      arbeitsnachweisLohnartZuordnungService.loescheArbeitsnachweisLohnartZuordnungByArbeitsnachweisID(
          zuLoeschenderArbeitsnachweis.getId());
      arbeitsnachweisLohnartZuordnungService.loescheLohnartberechnungLogByArbeitsnachweisID(
          zuLoeschenderArbeitsnachweis.getId());
      arbeitsnachweisRepository.deleteById(zuLoeschenderArbeitsnachweis.getId());

      projektabrechnungService.loescheObsoleteProjektabrechnungen(
          jahr, monat,
          vonDerLoeschungBetroffeneProjekte);

      setzeFestpreiseAufErfasstZurueck(
          new HashSet<>(
              festpreisProjekteMitProjektstundenImArbeitsnachweis.stream().map(Projekt::getId)
                  .toList()), jahr, monat);
      vonDerLoeschungBetroffeneProjekte.addAll(
          festpreisProjekteMitProjektstundenImArbeitsnachweis.stream().map(Projekt::getId)
              .toList());

      // FixMe muss im Frontend angezeigt werden
//      if (!vonDerLoeschungBetroffeneProjekte.isEmpty()) {
//        String projekteAuflistung = vonDerLoeschungBetroffeneProjekte.stream()
//            .map(Projekt::getProjektnummer).sorted().collect(Collectors.joining(", "));
//        notification.warnung(
//            "Die Projektabrechnungen des Abrechnungsmonat " + event.getArbeitsnachweis().getDatum()
//                + " folgender Projekte wurden in den Status \"erfasst\" zurückgesetzt bzw. gelöscht sofern keine weiteren Daten vorlagen: \n\n"
//                + projekteAuflistung);
//      }

    }

    return vonDerLoeschungBetroffeneProjekte;
  }

  public void rechneArbeitsnachweisAb(Long id) {
    //FixMe: Prüfungen wann durchgeführt werden darf
    this.arbeitsnachweisRepository.updateStatusId(id, ABGERECHNET.value());
  }

  public List<Arbeitsnachweis> alleArbeitsnachweiseKleinerGleichStatusFuerAbrechnungsmonat(
      int toStatusId, int jahr, int monat) {
    return IterableUtils.toList(
        this.arbeitsnachweisRepository.alleArbeitsnachweiseKleinerGleichStatusFuerAbrechnungsmonat(
            toStatusId, jahr, monat));
  }

  public List<Arbeitsnachweis> alleArbeitsnachweise() {
    return IterableUtils.toList(this.arbeitsnachweisRepository.findAll());
  }

  public List<MitarbeiterNichtBereitFuerMonatsabschluss> getMitarbeiterNichtBereitFuerMonatsabschluss(
      int jahr, int monat) {
    List<Mitarbeiter> interneMitarbeiterMitFehlendemArbeitsnachweis = mitarbeiterService.mitarbeiterUndStudentenDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
        jahr, monat);
    List<Mitarbeiter> externeMitarbeiterMitFehlendemArbeitsnachweis = mitarbeiterService.externeMitarbeiterDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
        jahr, monat);
    List<Mitarbeiter> alleRelevantenMitarbeiter = new ArrayList<>();
    alleRelevantenMitarbeiter.addAll(interneMitarbeiterMitFehlendemArbeitsnachweis);
    alleRelevantenMitarbeiter.addAll(externeMitarbeiterMitFehlendemArbeitsnachweis);
    List<Arbeitsnachweis> nochNichtAbgerechneteArbeitsnachweise = alleArbeitsnachweiseKleinerGleichStatusFuerAbrechnungsmonat(
        ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId(),
        jahr, monat);

    List<MitarbeiterNichtBereitFuerMonatsabschluss> mitarbeiterNichtBereitFuerMonatsabschlusses = new ArrayList<>();
    ueberfuehreMitarbeiterNachNichtBereitFuerMonatsabschluss(alleRelevantenMitarbeiter,
        mitarbeiterNichtBereitFuerMonatsabschlusses);
    ueberfuehreArbeitsnachweisNachNichtBereitFuerMonatsabschluss(
        nochNichtAbgerechneteArbeitsnachweise,
        mitarbeiterNichtBereitFuerMonatsabschlusses);
    return mitarbeiterNichtBereitFuerMonatsabschlusses;
  }

  private void ueberfuehreMitarbeiterNachNichtBereitFuerMonatsabschluss(
      final List<Mitarbeiter> mitarbeiterMitFehlendemArbeitsnachweis,
      final List<MitarbeiterNichtBereitFuerMonatsabschluss> mitarbeiterNichtBereitFuerMonatsabschlusses) {
    for (Mitarbeiter mitarbeiter : mitarbeiterMitFehlendemArbeitsnachweis) {
      MitarbeiterNichtBereitFuerMonatsabschluss anzufuehrenderMitarbeiter = new MitarbeiterNichtBereitFuerMonatsabschluss();
      anzufuehrenderMitarbeiter.setMitarbeiterId(mitarbeiter.getId());
      mitarbeiterNichtBereitFuerMonatsabschlusses.add(anzufuehrenderMitarbeiter);
    }
  }

  private void ueberfuehreArbeitsnachweisNachNichtBereitFuerMonatsabschluss(
      final List<Arbeitsnachweis> nochNichtAbgerechneteArbeitsnachweise,
      final List<MitarbeiterNichtBereitFuerMonatsabschluss> mitarbeiterNichtBereitFuerMonatsabschlusses) {
    for (Arbeitsnachweis arbeitsnachweis : nochNichtAbgerechneteArbeitsnachweise) {
      MitarbeiterNichtBereitFuerMonatsabschluss aufzufuehrendeArbeitsnachweis = new MitarbeiterNichtBereitFuerMonatsabschluss();
      aufzufuehrendeArbeitsnachweis.setArbeitsnachweisId(arbeitsnachweis.getId());
      aufzufuehrendeArbeitsnachweis.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
      aufzufuehrendeArbeitsnachweis.setStatusId(arbeitsnachweis.getStatusId());
      mitarbeiterNichtBereitFuerMonatsabschlusses.add(aufzufuehrendeArbeitsnachweis);
    }
  }
}
