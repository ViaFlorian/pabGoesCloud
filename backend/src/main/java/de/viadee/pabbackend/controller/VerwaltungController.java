package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Faktur;
import de.viadee.pabbackend.entities.FakturuebersichtSpeichernRequest;
import de.viadee.pabbackend.entities.MitarbeiterNichtBereitFuerMonatsabschluss;
import de.viadee.pabbackend.entities.MonatsabschlussAktion;
import de.viadee.pabbackend.entities.ProjektBudget;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.Skonto;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.FakturService;
import de.viadee.pabbackend.services.fachobjekt.MonatsabschlussService;
import de.viadee.pabbackend.services.fachobjekt.ProjektBudgetService;
import de.viadee.pabbackend.services.fachobjekt.ProjektabrechnungService;
import de.viadee.pabbackend.services.fachobjekt.SkontoService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.VERWALTUNG)
public class VerwaltungController {

  private final MonatsabschlussService monatsabschlussService;
  private final ArbeitsnachweisService arbeitsnachweisService;
  private final ProjektabrechnungService projektabrechnungService;
  private final FakturService fakturService;
  private final ProjektBudgetService projektBudgetService;
  private final SkontoService skontoService;

  public VerwaltungController(final MonatsabschlussService monatsabschlussService,
      final ArbeitsnachweisService arbeitsnachweisService,
      final ProjektabrechnungService projektabrechnungService,
      final ProjektBudgetService projektBudgetServic,
      final FakturService fakturService,
      final SkontoService skontoService) {
    this.monatsabschlussService = monatsabschlussService;
    this.arbeitsnachweisService = arbeitsnachweisService;
    this.projektabrechnungService = projektabrechnungService;
    this.projektBudgetService = projektBudgetServic;
    this.fakturService = fakturService;
    this.skontoService = skontoService;
  }

  @Operation(summary = "Liefert eine Liste der ausgeführten Aktionen für einen Monat zurück.")
  @GetMapping("/monatsabschluss/aktionen")
  public ResponseEntity<List<MonatsabschlussAktion>> getMonatsabschlussAktion(
      @RequestParam final int jahr,
      @RequestParam final int monat) {
    return ResponseEntity.ok(monatsabschlussService.monatsabschlussAktionen(jahr, monat));
  }

  @Operation(summary = "Liefert eine Liste der Mitarbeiter, die nicht bereit für den Monatsabschluss sind.")
  @GetMapping("/monatsabschluss/mitarbeiter")
  public ResponseEntity<List<MitarbeiterNichtBereitFuerMonatsabschluss>> getMitarbeiterNichtBereitFuerMonatsabschluss(
      @RequestParam final int jahr,
      @RequestParam final int monat) {
    return ResponseEntity.ok(
        arbeitsnachweisService.getMitarbeiterNichtBereitFuerMonatsabschluss(jahr, monat));
  }

  @Operation(summary = "Liefert eine Liste der Projekte, die nicht bereit für den Monatsabschluss sind.")
  @GetMapping("/monatsabschluss/projekte")
  public ResponseEntity<List<Projektabrechnung>> getProjekteNichtBereitFuerMonatsabschluss(
      @RequestParam final int jahr,
      @RequestParam final int monat) {
    return ResponseEntity.ok(
        projektabrechnungService.getProjekteNichtBereitFuerMonatsabschluss(jahr, monat));
  }

  @Operation(summary = "Liefert eine Liste an Projektbudgets zu einem Projekt zurück.")
  @GetMapping("/projektbudget")
  public ResponseEntity<List<ProjektBudget>> getAlleProjektBudgetsByProjektId(
      @RequestParam Long projektId) {
    return ResponseEntity.ok(projektBudgetService.alleProjektBudgetsByProjektId(projektId));
  }

  @Operation(summary = "Liefert eine Liste der Fakturen zu einem Projekt zurück.")
  @GetMapping("/faktur")
  public ResponseEntity<List<Faktur>> getAlleFakturenByProjektId(@RequestParam Long projektId) {
    return ResponseEntity.ok(fakturService.alleFakturenByProjektId(projektId));
  }

  @Operation(summary = "Liefert eine Liste der Skontos zu einem Projekt zurück.")
  @GetMapping("/skonto")
  public ResponseEntity<List<Skonto>> getAlleSkontosByProjektId(@RequestParam Long projektId) {
    return ResponseEntity.ok(skontoService.alleSkontosByProjektId(projektId));
  }

  @Operation(summary = "Prüft und speichert die übergebenen Fakturen")
  @PostMapping("/faktur")
  public ResponseEntity speichereAFakturen(
      @RequestBody FakturuebersichtSpeichernRequest fakturuebersichtSpeichernRequest) {
    fakturService.speicherFakturen(fakturuebersichtSpeichernRequest);
    return ResponseEntity.ok().build();

  }
}
