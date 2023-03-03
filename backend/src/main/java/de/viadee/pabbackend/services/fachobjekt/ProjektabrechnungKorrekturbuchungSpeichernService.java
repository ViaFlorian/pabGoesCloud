package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.enums.Kostenarten;
import de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus;
import de.viadee.pabbackend.enums.Projektnummern;
import de.viadee.pabbackend.enums.Projekttyp;
import de.viadee.pabbackend.services.berechnung.MitarbeiterStundenKontoBerechnung;
import de.viadee.pabbackend.services.berechnung.MitarbeiterUrlaubKontoBerechnung;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjektabrechnungKorrekturbuchungSpeichernService {

  private final ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService;
  private final KonstantenService konstantenService;
  private final ProjektService projektService;
  private final ProjektabrechnungService projektabrechnungService;
  private final ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService;
  private final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung;
  private final MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung;
  private final MitarbeiterService mitarbeiterService;
  private final Zeitrechnung zeitrechnung;


  public ProjektabrechnungKorrekturbuchungSpeichernService(
      ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService,
      KonstantenService konstantenService, ProjektService projektService,
      ProjektabrechnungService projektabrechnungService,
      ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService,
      MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung,
      MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung,
      MitarbeiterService mitarbeiterService, Zeitrechnung zeitrechnung) {
    this.projektabrechnungKorrekturbuchungService = projektabrechnungKorrekturbuchungService;
    this.konstantenService = konstantenService;
    this.projektService = projektService;
    this.projektabrechnungService = projektabrechnungService;
    this.projektabrechnungBerechneteLeistungService = projektabrechnungBerechneteLeistungService;
    this.mitarbeiterUrlaubKontoBerechnung = mitarbeiterUrlaubKontoBerechnung;
    this.mitarbeiterStundenKontoBerechnung = mitarbeiterStundenKontoBerechnung;
    this.mitarbeiterService = mitarbeiterService;
    this.zeitrechnung = zeitrechnung;
  }

  @Transactional("pabDbTransactionManager")
  public List<String> speichereKorrekturuchungen(
      List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchungen) {
    Long projektzeitenKostenartId = konstantenService.kostenartByBezeichung(
        Kostenarten.PROJEKTZEITEN.toString()).getId();
    Long reisezeitenKostenartId = konstantenService.kostenartByBezeichung(
        Kostenarten.REISEZEITEN.toString()).getId();
    Set<String> meldungen = new LinkedHashSet<>();

    ergaenzeKorrekturbuchungenUndErstelleProjektabrechnungWennNoetig(
        projektabrechnungKorrekturbuchungen);
    meldungen.add("Korrekturbuchung wurden gespeichert");

    for (ProjektabrechnungKorrekturbuchung buchung : projektabrechnungKorrekturbuchungen) {
      if (istBuchungStundenTypUndMitarbeiterIntern(buchung, projektzeitenKostenartId,
          reisezeitenKostenartId)) {
        Projekt projekt = projektService.projektById(buchung.getProjektId());
        String mitarbeiterVollerName = erstelleVollenNamenVonMitarbeiter(
            buchung.getMitarbeiterId());
        if (projekt.getProjektnummer().equals(Projektnummern.URLAUB.toString())) {
          mitarbeiterUrlaubKontoBerechnung.bucheKorrekturbuchung(buchung);

          meldungen.add(
              "Das Urlaubskonto von " + mitarbeiterVollerName
                  + " wurde ebenfalls korrigiert.");
        }
        mitarbeiterStundenKontoBerechnung.bucheKorrekturbuchung(buchung);
        meldungen.add("Das Stundenkonto von " + mitarbeiterVollerName
            + " wurde ebenfalls korrigiert.");
      }
    }

    setzeBetroffeneProjektabrechnungenZurueck(projektabrechnungKorrekturbuchungen, meldungen);

    return meldungen.stream().toList();

  }

  private void setzeBetroffeneProjektabrechnungenZurueck(
      final List<ProjektabrechnungKorrekturbuchung> korrekturbuchungen, Set<String> meldungen) {
    LocalDate vormonat = zeitrechnung.getVormonat();
    Set<Projekt> zurueckgesetzteProjekte = new HashSet<>();

    for (ProjektabrechnungKorrekturbuchung buchung : korrekturbuchungen) {

      if (buchung.getJahr() != vormonat.getYear()
          || buchung.getMonat() != vormonat.getMonthValue()) {
        continue;
      }

      Projekt projekt = projektService.projektById(buchung.getProjektId());

      if (zurueckgesetzteProjekte.stream()
          .noneMatch(zurueckgesetztesProjekt -> zurueckgesetztesProjekt.equals(projekt))) {

        Projektabrechnung projektabrechnung = projektabrechnungService.projektabrechnungByProjektIdMonatJahr(
            projekt.getId(), vormonat.getMonthValue(), vormonat.getYear());
        if (projektabrechnung != null) {
          if (projekt.getProjekttyp().equals(Projekttyp.FESTPREIS.toString())
              || projekt.getProjekttyp()
              .equals(Projekttyp.WARTUNG.toString()) || projekt.getProjekttyp()
              .equals(Projekttyp.PRODUKT.toString())) {
            meldungen.add(
                "Der Fertigstellungsgrad vom Projekt " + projekt.getProjektnummer()
                    + " wurde zurückgesetzt.");
            projektabrechnung.setFertigstellungsgrad(null);
          }
          ProjektabrechnungBearbeitungsstatus alterStatus = ProjektabrechnungBearbeitungsstatus.fromStatusId(
              projektabrechnung.getStatusId());
          projektabrechnung.setBudgetBetragZurAbrechnung(null);
          projektabrechnung.setStatusId(ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());
          projektabrechnungService.speichereProjektabrechnung(projektabrechnung);
          projektabrechnungBerechneteLeistungService.loescheByProjektabrechnungId(
              projektabrechnung.getId());
          if (alterStatus != null && alterStatus.equals(
              ProjektabrechnungBearbeitungsstatus.ABGERECHNET)) {
            meldungen.add(
                "Der Status der Projektabrechnung des Projekts " + projekt.getProjektnummer()
                    + " wurde auf 'erfasst' zurückgesetzt. Erneute Abrechnung notwendig!");
          }
        }
      }

      zurueckgesetzteProjekte.add(projekt);
    }
  }


  private String erstelleVollenNamenVonMitarbeiter(Long mitarbeiterId) {
    if (mitarbeiterId == null) {
      return "";
    }
    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(mitarbeiterId);
    return mitarbeiter.getFullName();
  }

  private void ergaenzeKorrekturbuchungenUndErstelleProjektabrechnungWennNoetig(
      List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchungen) {
    for (ProjektabrechnungKorrekturbuchung projektabrechnungKorrekturbuchung : projektabrechnungKorrekturbuchungen) {
      if (projektabrechnungKorrekturbuchung.getProjektabrechnungId() != null) {
        continue;
      }
      Projektabrechnung projektabrechnungsDaten = new Projektabrechnung();
      projektabrechnungsDaten.setJahr(projektabrechnungKorrekturbuchung.getJahr());
      projektabrechnungsDaten.setMonat(projektabrechnungKorrekturbuchung.getMonat());
      projektabrechnungsDaten.setProjektId(projektabrechnungKorrekturbuchung.getProjektId());
      projektabrechnungsDaten.setStatusId(
          ProjektabrechnungBearbeitungsstatus.ERFASST.toStatusId());

      Projektabrechnung projektabrechnungErstellt = projektabrechnungService.leseOderErstelleUndLeseProjektabrechnungAus(
          projektabrechnungsDaten);

      projektabrechnungKorrekturbuchung.setProjektabrechnungId(projektabrechnungErstellt.getId());

    }
    projektabrechnungKorrekturbuchungService.speichereProjektabrechnungKorrekturbuchungen(
        projektabrechnungKorrekturbuchungen);

  }

  private boolean istBuchungStundenTypUndMitarbeiterIntern(
      ProjektabrechnungKorrekturbuchung buchung, Long projektzeitenKostenartId,
      Long reisezeitenKostenartId) {

    if (buchung.getMitarbeiterId() == null) {
      return false;
    }

    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(buchung.getMitarbeiterId());
    return (buchung.getKostenartId().equals(projektzeitenKostenartId)
        || (buchung.getKostenartId().equals(reisezeitenKostenartId)))
        && mitarbeiter != null && mitarbeiter.isIntern()
        && buchung.getAnzahlStundenKosten() != null;
  }

}
