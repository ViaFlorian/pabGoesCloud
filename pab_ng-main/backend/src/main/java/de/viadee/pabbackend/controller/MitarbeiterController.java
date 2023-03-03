package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterStundenKonto;
import de.viadee.pabbackend.entities.MitarbeiterUrlaubKonto;
import de.viadee.pabbackend.services.berechnung.MitarbeiterStundenKontoBerechnung;
import de.viadee.pabbackend.services.berechnung.MitarbeiterUrlaubKontoBerechnung;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStundenKontoService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterUrlaubKontoService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.MITARBEITER)
public class MitarbeiterController {

  private final MitarbeiterService mitarbeiterService;
  private final MitarbeiterStundenKontoService mitarbeiterStundenKontoService;
  private final MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung;
  private final MitarbeiterUrlaubKontoService mitarbeiterUrlaubKontoService;
  private final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;

  public MitarbeiterController(final MitarbeiterService mitarbeiterService,
      final MitarbeiterStundenKontoService mitarbeiterStundenKontoService,
      final MitarbeiterStundenKontoBerechnung mitarbeiterStundenKontoBerechnung,
      final MitarbeiterUrlaubKontoService mitarbeiterUrlaubKontoService,
      final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung,
      final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService) {
    this.mitarbeiterService = mitarbeiterService;
    this.mitarbeiterStundenKontoService = mitarbeiterStundenKontoService;
    this.mitarbeiterStundenKontoBerechnung = mitarbeiterStundenKontoBerechnung;
    this.mitarbeiterUrlaubKontoService = mitarbeiterUrlaubKontoService;
    this.mitarbeiterUrlaubKontoBerechnung = mitarbeiterUrlaubKontoBerechnung;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
  }

  @Operation(summary = "Liefert einer Liste aller Mitarbeiter zurück.")
  @GetMapping("/all")
  public ResponseEntity<List<Mitarbeiter>> getAllMitarbeiter() {
    return ResponseEntity.ok(mitarbeiterService.alleMitarbeiter());
  }

  @Operation(summary = "Liefert eine Liste aller Mitarbeiter zurück. Es können Filteroptionen angegeben werden.")
  @GetMapping("/selectOptions")
  public ResponseEntity<List<Mitarbeiter>> getMitarbeiterSelectOptions(
      @RequestParam(defaultValue = "false") boolean aktiveMitarbeiter,
      @RequestParam(defaultValue = "false") boolean inaktiveMitarbeiter,
      @RequestParam(defaultValue = "false") boolean interneMitarbeiter,
      @RequestParam(defaultValue = "false") boolean externeMitarbeiter,
      @RequestParam(defaultValue = "false") boolean beruecksichtigeEintrittsdatum,
      @RequestParam(defaultValue = "false") boolean alleMitarbeiterMitArbeitsnachweis) {
    return ResponseEntity.ok(
        mitarbeiterService.mitarbeiterSelectOptions(aktiveMitarbeiter,
            inaktiveMitarbeiter, interneMitarbeiter, externeMitarbeiter,
            beruecksichtigeEintrittsdatum, alleMitarbeiterMitArbeitsnachweis));
  }

  @Operation(summary = "Liefert eine Liste der Arbeitsstunden für einen Mitarbeiter zurück.")
  @GetMapping("/{id}/stundenkonto")
  public ResponseEntity<List<MitarbeiterStundenKonto>> getMitarbeiterStundenkonto(
      @PathVariable final Long id) {
    return ResponseEntity.ok(
        mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(id));
  }

  @Operation(summary = "Speichert die übergebene Liste von Stunden ab.")
  @PostMapping("/{id}/stundenkonto")
  public ResponseEntity<HttpStatus> speicherMitarbeiterStundenkonto(
      @PathVariable final Long id,
      @RequestBody final List<MitarbeiterStundenKonto> neueStundenKontoListe) {
    mitarbeiterStundenKontoBerechnung.speichereNeueStundenKontoSaetze(neueStundenKontoListe, id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Liefert eine Liste der Arbeitsstunden für einen Mitarbeiter zurück mit neu berechnetem Saldo.")
  @PostMapping("/{id}/stundenkonto/saldo")
  public ResponseEntity<List<MitarbeiterStundenKonto>> getMitarbeiterStundenkontoMitNeuemSaldo(
      @PathVariable final Long id,
      @RequestBody final MitarbeiterStundenKonto[] neueStundenKontoListe) {
    List<MitarbeiterStundenKonto> mitarbeiterStundenKontoListe = mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(
        id);
    Collections.addAll(mitarbeiterStundenKontoListe, neueStundenKontoListe);
    mitarbeiterStundenKontoBerechnung.berechneSalden(mitarbeiterStundenKontoListe);
    return ResponseEntity.ok(mitarbeiterStundenKontoListe);
  }


  @Operation(summary = "Liefert eine Liste der Urlaubsstunden für einen Mitarbeiter zurück.")
  @GetMapping("/{id}/urlaubskonto")
  public ResponseEntity<List<MitarbeiterUrlaubKonto>> getMitarbeiterUrlaubskonto(
      @PathVariable final Long id) {
    return ResponseEntity.ok(
        mitarbeiterUrlaubKontoService.mitarbeiterUrlaubKontoByMitarbeiterId(id));
  }

  @Operation(summary = "Speichert die übergebene Liste von Urlaubstagen ab.")
  @PostMapping("/{id}/urlaubskonto")
  public ResponseEntity<HttpStatus> speicherMitarbeiterUrlkaubskonto(
      @PathVariable final Long id,
      @RequestBody final List<MitarbeiterUrlaubKonto> neueUrlaubsKontoListe) {

    mitarbeiterUrlaubKontoBerechnung.speichereNeueUrlaubKontoSaetze(neueUrlaubsKontoListe, id);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Liefert eine Liste der Urlaubsstunden für einen Mitarbeiter zurück mit neu berechnetem Saldo.")
  @PostMapping("/{id}/urlaubskonto/saldo")
  public ResponseEntity<List<MitarbeiterUrlaubKonto>> getMitarbeiterUrlaubskontoMitNeuemSaldo(
      @PathVariable final Long id,
      @RequestBody final MitarbeiterUrlaubKonto[] neueStundenKontoListe) {
    List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontoListe = mitarbeiterUrlaubKontoService.mitarbeiterUrlaubKontoByMitarbeiterId(
        id);
    Collections.addAll(mitarbeiterUrlaubKontoListe, neueStundenKontoListe);
    mitarbeiterUrlaubKontoBerechnung.berechneSalden(mitarbeiterUrlaubKontoListe);
    return ResponseEntity.ok(mitarbeiterUrlaubKontoListe);
  }
}
