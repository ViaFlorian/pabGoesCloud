package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.ProjektAbrechnungsmonat;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungBerechneteLeistung;
import de.viadee.pabbackend.entities.ProjektabrechnungFertigstellungInitialDaten;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchungVorgang;
import de.viadee.pabbackend.entities.ProjektabrechnungKostenLeistung;
import de.viadee.pabbackend.entities.ProjektabrechnungMitarbeiterPair;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeit;
import de.viadee.pabbackend.entities.ProjektabrechnungReise;
import de.viadee.pabbackend.entities.ProjektabrechnungSonderarbeit;
import de.viadee.pabbackend.entities.ProjektabrechnungSonstige;
import de.viadee.pabbackend.entities.ProjektabrechnungUebersicht;
import de.viadee.pabbackend.entities.SonstigeProjektkostenSpeichernRequest;
import de.viadee.pabbackend.entities.SonstigeProjektkostenSpeichernResponse;
import de.viadee.pabbackend.services.berechnung.KorrekturbuchungErstellenService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungBerechneteLeistungService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungKorrekturbuchungService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungKorrekturbuchungSpeichernService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungProjektzeitService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungReiseService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungSonderarbeitService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungSonstigeService;
import de.viadee.pabbackend.services.validation.KorrekturbuchungValidator;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.PROJEKTABRECHNUNG)
public class ProjektabrechnungController {

  private final ProjektabrechnungService projektabrechnungService;
  private final ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService;
  private final ProjektabrechnungKorrekturbuchungSpeichernService projektabrechnungKorrekturbuchungSpeichernService;
  private final ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService;
  private final ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService;
  private final ProjektabrechnungReiseService projektabrechnungReiseService;
  private final ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService;
  private final ProjektabrechnungSonstigeService projektabrechnungSonstigeService;
  private final KorrekturbuchungErstellenService korrekturbuchungErstellenService;
  private final KorrekturbuchungValidator korrekturbuchungValidator;


  public ProjektabrechnungController(ProjektabrechnungService projektabrechnungService,
      ProjektabrechnungKorrekturbuchungService projektabrechnungKorrekturbuchungService,
      ProjektabrechnungKorrekturbuchungSpeichernService projektabrechnungKorrekturbuchungSpeichernService,
      ProjektabrechnungBerechneteLeistungService projektabrechnungBerechneteLeistungService,
      ProjektabrechnungProjektzeitService projektabrechnungProjektzeitService,
      ProjektabrechnungReiseService projektabrechnungReiseService,
      ProjektabrechnungSonderarbeitService projektabrechnungSonderarbeitService,
      ProjektabrechnungSonstigeService projektabrechnungSonstigeService,
      KorrekturbuchungErstellenService korrekturbuchungErstellenService,
      KorrekturbuchungValidator korrekturbuchungValidator) {
    this.projektabrechnungService = projektabrechnungService;
    this.projektabrechnungKorrekturbuchungService = projektabrechnungKorrekturbuchungService;
    this.projektabrechnungKorrekturbuchungSpeichernService = projektabrechnungKorrekturbuchungSpeichernService;
    this.projektabrechnungBerechneteLeistungService = projektabrechnungBerechneteLeistungService;
    this.projektabrechnungProjektzeitService = projektabrechnungProjektzeitService;
    this.projektabrechnungReiseService = projektabrechnungReiseService;
    this.projektabrechnungSonderarbeitService = projektabrechnungSonderarbeitService;
    this.projektabrechnungSonstigeService = projektabrechnungSonstigeService;
    this.korrekturbuchungErstellenService = korrekturbuchungErstellenService;
    this.korrekturbuchungValidator = korrekturbuchungValidator;
  }

  @Operation(summary = "Liefert zu einem Datumsbereich eine Übersicht je Projektabrechnung zurück.")
  @GetMapping("/uebersicht")
  public ResponseEntity<List<ProjektabrechnungUebersicht>> getProjektabrechnungUebersicht(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann) {
    return ResponseEntity.ok(
        projektabrechnungService.alleProjektabrechnungenFuerUebersichtGefiltert(
            abWann.getYear(),
            abWann.getMonthValue(),
            bisWann.getYear(),
            bisWann.getMonthValue()
        ));
  }

  @Operation(summary =
      "Liefert zu einem Dateumsbereich eine Übersicht der Projekte zurück, für welche die Abrechnung noch fehlt. "
          + "Liefert eine leere Liste zurück, falls der offene Abrechnungsmonat nicht Teil des Zeitraums ist.")
  @GetMapping("/abrechnungsmonatFehlt")
  public ResponseEntity<List<ProjektabrechnungUebersicht>> projektabrechnungUebersichtMitFehlenderAbrechnung(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann) {
    return ResponseEntity.ok(
        this.projektabrechnungService.fehlendeProjektabrechnungen(abWann, bisWann));
  }

  @Operation(summary = "Liefert zu einem Datumsbereich eine Liste an Pärchen bestehend aus Projektabrechnung-ID und Mitarbeiter-ID zurück.")
  @GetMapping("/projektabrechnungMitarbeiterPairs")
  public ResponseEntity<List<ProjektabrechnungMitarbeiterPair>> getProjektabrechnungMitarbeiterPairs(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann) {
    return ResponseEntity.ok(
        projektabrechnungService.alleProjektabrechnungMitarbeiterPairs(
            abWann.getYear(),
            abWann.getMonthValue(),
            bisWann.getYear(),
            bisWann.getMonthValue()
        ));
  }

  @Operation(summary = "Liefert für ein Projekte alle Abrechnungsmonate mit einer erfassten Projektabrechnung zurück.")
  @GetMapping("/abrechnungsmonateFuerProjekt")
  public ResponseEntity<List<ProjektAbrechnungsmonat>> getAlleAbrechnungsmonateByProjektId(
      @RequestParam Long projektId) {
    List<ProjektAbrechnungsmonat> abrechnungsmonate = this.projektabrechnungService.abrechnungsmonateByProjektId(
        projektId);
    return ResponseEntity.ok(abrechnungsmonate);
  }

  @Operation(summary = "Liefert zu einer ID die zugehörige Projektabrechnung.")
  @GetMapping("/{id}")
  public ResponseEntity<Projektabrechnung> getProjektabrechnungById(@PathVariable Long id) {
    var projaktbarechnung = projektabrechnungService.projektabrechnungById(id);
    return ResponseEntity.ok(projaktbarechnung);
  }

  @Operation(summary = "Liefert zu einem Datumsbereich eine Liste an Pärchen bestehend aus Projektabrechnung-ID und Mitarbeiter-ID zurück.")
  @GetMapping("/{id}/kostenLeistungJeMitarbeiter")
  public ResponseEntity<List<ProjektabrechnungKostenLeistung>> getKostenLeistungJeMitarbeiter(
      @PathVariable Long id) {
    return ResponseEntity.ok(this.projektabrechnungService.getKostenLeistungJeMitarbeiter(id));
  }

  @Operation(summary = "Liefert zu einem Datumsbereich eine Liste an Pärchen bestehend aus Projektabrechnung-ID und Mitarbeiter-ID zurück.")
  @GetMapping("/{id}/mitarbeiterHatMehrAlsBerechneteLeistung")
  public ResponseEntity<Boolean> getMitarbeiterHatMehrAlsBerechneteLeistung(
      @PathVariable Long id, @RequestParam Long mitarbeiterId) {
    return ResponseEntity.ok(
        this.projektabrechnungService.mitarbeiterHatMehrAlsBerechneteLeistung(id, mitarbeiterId));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnung Daten zu berechneten Leistungen.")
  @GetMapping("/{id}/berechneteLeistung")
  public ResponseEntity<List<ProjektabrechnungBerechneteLeistung>> getBerechneteLeistungenZuProjektabrechnung(
      @PathVariable Long id) {
    return ResponseEntity.ok(
        projektabrechnungBerechneteLeistungService.projektabrechnungBerechneteLeistungByProjektabrechnungId(
            id));
  }

  @Operation(summary = "Liefert die zur Fertigstellung eines Festpreis-Projektes initial benötigten Daten.")
  @GetMapping("/{id}/fertigstellung/initial")
  public ResponseEntity<ProjektabrechnungFertigstellungInitialDaten> getProjektabrechnungFertigstellungInitialDaten(
      @PathVariable Long id, @RequestParam BigDecimal fertigstellungsgrad) {
    return ResponseEntity.ok(
        this.projektabrechnungService.getProjektabrechnungFertigstellungInitialDaten(id,
            fertigstellungsgrad));
  }

  @Operation(summary = "Berechnet die neue Fertigstellung eines Festpreis-Projektes und liefert das Ergebnis zurück.")
  @GetMapping("/{id}/fertigstellung/berechne")
  public ResponseEntity<List<ProjektabrechnungBerechneteLeistung>> berechneNeueFertigstellung(
      @PathVariable Long id, @RequestParam BigDecimal monatFertigstellung) {
    return ResponseEntity.ok(
        this.projektabrechnungService.berechneNeueFertigstellung(id, monatFertigstellung));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnung für einen Mitarbeiter die Daten zur Projektzeit.")
  @GetMapping("/{id}/projektzeit")
  public ResponseEntity<List<ProjektabrechnungProjektzeit>> getProjektzeitZuProjektabrechnungFuerMitarbeiter(
      @PathVariable Long id, @RequestParam Long mitarbeiterId, @RequestParam Long projektId,
      @RequestParam Integer jahr, @RequestParam Integer monat) {
    return ResponseEntity.ok(
        projektabrechnungProjektzeitService.projektabrechnungProjektzeitByProjektabrechnungIdUndMitarbeiterId(
            id, mitarbeiterId, projektId, jahr, monat));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnung für einen Mitarbeiter die Daten zur Reise.")
  @GetMapping("/{id}/reise")
  public ResponseEntity<ProjektabrechnungReise> getReiseZuProjektabrechnungFuerMitarbeiter(
      @PathVariable Long id, @RequestParam Long mitarbeiterId) {
    return ResponseEntity.ok(
        projektabrechnungReiseService.projektabrechnungReiseByProjektabrechnungIdUndMitarbeiterId(
            id, mitarbeiterId));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnung für einen Mitarbeiter die Daten zur Sonderarbeit.")
  @GetMapping("/{id}/sonderarbeit")
  public ResponseEntity<ProjektabrechnungSonderarbeit> getSonderarbeitZuProjektabrechnungFuerMitarbeiter(
      @PathVariable Long id, @RequestParam Long mitarbeiterId) {
    return ResponseEntity.ok(
        projektabrechnungSonderarbeitService.projektabrechnungSonderarbeitByProjektabrechnungIdMitarbeiterId(
            id, mitarbeiterId));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnung für einen Mitarbeiter die Daten zu Sonstige.")
  @GetMapping("/{id}/sonstige")
  public ResponseEntity<ProjektabrechnungSonstige> getSonstigeZuProjektabrechnungFuerMitarbeiter(
      @PathVariable Long id, @RequestParam Long mitarbeiterId) {
    return ResponseEntity.ok(
        projektabrechnungSonstigeService.projektabrechnungSonstigeByProjektabrechnungIdMitarbeiterId(
            id, mitarbeiterId));
  }

  @Operation(summary = "Liefert zu einer Projektabrechnungdie Daten zu Sonstige ohne Mitarbeiterbezug.")
  @GetMapping("/{id}/sonstige/ohneMitarbeiterbezug")
  public ResponseEntity<ProjektabrechnungSonstige> getSonstigeZuProjektabrechnungOhneMitarbeiterbezug(
      @PathVariable Long id) {
    return ResponseEntity.ok(
        projektabrechnungSonstigeService.projektabrechnungSonstigeByProjektabrechnungIdOhneMitarbeiterbezug(
            id));
  }

  @Operation(summary = "Speichert die übergebene Liste von aktualisierten und neuen viadee Auslagen ab.")
  @PostMapping("/viadeeAuslage")
  public ResponseEntity<SonstigeProjektkostenSpeichernResponse> speichereViadeeAuslage(
      @RequestBody SonstigeProjektkostenSpeichernRequest sonstigeProjektkostenSpeichernRequest) {
    return ResponseEntity.ok(this.projektabrechnungService.speichereViadeeAuslage(
        sonstigeProjektkostenSpeichernRequest));
  }

  @Operation(summary = "Liefert zu einer ProjektId die zugehörige Projektabrechnung für die Korrekturbuchung.")
  @GetMapping("/korrekturbuchung/{projektId}")
  public ResponseEntity<List<ProjektabrechnungKorrekturbuchung>> getProjektabrechnungKorrekturbuchungenByProjektId(
      @PathVariable Long projektId) {
    return ResponseEntity.ok(
        projektabrechnungKorrekturbuchungService.ladeProjektabrechnungKorrekturbuchungenByProjektId(
            projektId));
  }

  @Operation(summary = "Liefert zu einer GegenbuchungID die zugehörige Gegenbuchung fuer eine Korrekturbuchung.")
  @GetMapping("/korrekturbuchung/gegenbuchung")
  public ResponseEntity<ProjektabrechnungKorrekturbuchung> getProjektabrechnungKorrekturbuchungGegenbuchungByGegenbuchungID(
      @RequestParam String gegenbuchungId,
      @RequestParam Long korrekturbuchungId) {
    return ResponseEntity.ok(
        projektabrechnungKorrekturbuchungService.ladeProjektabrechnungKorrekturbuchungGegenbuchung(
            korrekturbuchungId,
            gegenbuchungId));
  }

  @Operation(summary = "Erstellt die neuen Buchungen und Gegenbuchungen"
      + " für die Korrekturbuchung zum hinzufuegen zur Liste auf Basis der übergebenen Daten.")
  @PostMapping("/korrekturbuchung")
  public ResponseEntity<ProjektabrechnungKorrekturbuchungVorgang> erstelleNeueProjektabrechnungKorrekturbuchung(
      @RequestBody ProjektabrechnungKorrekturbuchungVorgang projektabrechnungKorrekturbuchungVorgang) {
    korrekturbuchungValidator.fuehrePruefungVonKorrekturbuchungVorgangDurch(
        projektabrechnungKorrekturbuchungVorgang);
    return ResponseEntity.ok(
        korrekturbuchungErstellenService.projektabrechnungKorrekturbuchungenErstellen(
            projektabrechnungKorrekturbuchungVorgang));
  }

  @Operation(summary = "Speichert die neuen Korrekturbuchungen.")
  @PostMapping("/korrekturbuchung/speichern")
  public ResponseEntity<List<String>> speichereNeueProjektabrechnungKorrekturbuchung(
      @RequestBody List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchungen) {
    korrekturbuchungValidator.fuehrePruefungVonKorrekturbuchungenDurch(
        projektabrechnungKorrekturbuchungen);
    return ResponseEntity.ok(
        projektabrechnungKorrekturbuchungSpeichernService.speichereKorrekturuchungen(
            projektabrechnungKorrekturbuchungen));

  }
}
