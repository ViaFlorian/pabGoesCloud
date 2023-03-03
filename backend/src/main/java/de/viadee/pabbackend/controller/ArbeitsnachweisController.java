package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Abrechnungsmonat;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisAbrechnung;
import de.viadee.pabbackend.entities.ArbeitsnachweisAbrechnungErstellungsRequest;
import de.viadee.pabbackend.entities.ArbeitsnachweisLohnartZuordnung;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernRequest;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernResponse;
import de.viadee.pabbackend.entities.ArbeitsnachweisUebersicht;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.DmsVerarbeitung;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.ExcelImportErgebnis;
import de.viadee.pabbackend.entities.Fehlerlog;
import de.viadee.pabbackend.entities.LohnartberechnungLog;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterAbrechnungsmonat;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundenUeberpruefungsRequest;
import de.viadee.pabbackend.services.berechnung.AngerechneteReisezeitBerechnung;
import de.viadee.pabbackend.services.berechnung.ArbeitsnachweisAbrechnungBerechnungService;
import de.viadee.pabbackend.services.berechnung.ProjektstundenInkonsistenzBerechnung;
import de.viadee.pabbackend.services.dms.DmsService;
import de.viadee.pabbackend.services.excel.ExcelImport;
import de.viadee.pabbackend.services.fachobjekt.AbwesenheitService;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.BelegService;
import de.viadee.pabbackend.services.fachobjekt.FehlerlogService;
import de.viadee.pabbackend.services.fachobjekt.LohnartenService;
import de.viadee.pabbackend.services.fachobjekt.LohnartenberechnungLogService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektstundeService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(BackendPfade.ARBEITSNACHWEIS)
public class ArbeitsnachweisController {

  private final ArbeitsnachweisService arbeitsnachweisService;
  private final MitarbeiterService mitarbeiterService;
  private final ProjektstundeService projektstundeService;
  private final BelegService belegService;
  private final ArbeitsnachweisAbrechnungBerechnungService arbeitsnachweisAbrechnungBerechnungService;
  private final AbwesenheitService abwesenheitService;
  private final LohnartenService lohnartenService;
  private final LohnartenberechnungLogService lohnartenberechnungLogService;
  private final AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung;
  private final DmsService dmsService;
  private final ProjektstundenInkonsistenzBerechnung projektstundenInkonsistenzBerechnung;
  private final FehlerlogService fehlerlogService;
  private final ExcelImport excelImportService;

  public ArbeitsnachweisController(final ArbeitsnachweisService arbeitsnachweisService,
      MitarbeiterService mitarbeiterService, ProjektstundeService projektstundeService,
      BelegService belegService,
      ArbeitsnachweisAbrechnungBerechnungService arbeitsnachweisAbrechnungBerechnungService,
      AbwesenheitService abwesenheitService, DmsService dmsService,
      LohnartenService lohnartenService,
      LohnartenberechnungLogService lohnartenberechnungLogService,
      AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung,
      FehlerlogService fehlerlogService, ExcelImport excelImportService,
      ProjektstundenInkonsistenzBerechnung projektstundenInkonsistenzBerechnung) {
    this.arbeitsnachweisService = arbeitsnachweisService;
    this.mitarbeiterService = mitarbeiterService;
    this.projektstundeService = projektstundeService;
    this.belegService = belegService;
    this.arbeitsnachweisAbrechnungBerechnungService = arbeitsnachweisAbrechnungBerechnungService;
    this.abwesenheitService = abwesenheitService;
    this.dmsService = dmsService;
    this.lohnartenService = lohnartenService;
    this.lohnartenberechnungLogService = lohnartenberechnungLogService;
    this.angerechneteReisezeitBerechnung = angerechneteReisezeitBerechnung;
    this.projektstundenInkonsistenzBerechnung = projektstundenInkonsistenzBerechnung;
    this.fehlerlogService = fehlerlogService;
    this.excelImportService = excelImportService;
  }

  @Operation(summary = "Liefert alle Arbeitsnachweis zurück.")
  @GetMapping("/")
  public ResponseEntity<List<Arbeitsnachweis>> getArbeitsnachweisUebersicht() {
    return ResponseEntity.ok(arbeitsnachweisService.alleArbeitsnachweise());
  }

  @Operation(summary = "Liefert zu einem Dateumsbereich eine Übersicht je Arbeitsnachweis zurück.")
  @GetMapping("/uebersicht")
  public ResponseEntity<List<ArbeitsnachweisUebersicht>> getArbeitsnachweisUebersicht(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann) {
    return ResponseEntity.ok(arbeitsnachweisService.alleArbeitsnachweiseFuerUebersichtGefiltert(
        abWann.getYear(),
        abWann.getMonthValue(),
        bisWann.getYear(),
        bisWann.getMonthValue()
    ));
  }

  @Operation(summary =
      "Liefert zu einem Dateumsbereich eine Übersicht aller fehlenden Arbeitsnachweise zurück. "
          + "Liefert eine leere Liste zurück, falls der offene Abrechnungsmonat nicht Teil des Zeitraums ist.")
  @GetMapping("/fehlendeArbeitsnachweise")
  public ResponseEntity<List<ArbeitsnachweisUebersicht>> fehlendeArbeitsnachweiseFuerZeitraum(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann) {
    return ResponseEntity.ok(
        arbeitsnachweisService.fehlendeArbeitsnachweiseFuerZeitraum(abWann, bisWann));
  }

  @Operation(summary = "Liefert alle Abrechnungsmonnate zurück.")
  @GetMapping("/abrechnungsmonate")
  public ResponseEntity<List<Abrechnungsmonat>> getAlleAbrechnungsmonate() {
    List<Abrechnungsmonat> abrechnungsmonats = arbeitsnachweisService.alleAbrechnungsmonate();
    return ResponseEntity.ok(abrechnungsmonats);
  }

  @Operation(summary = "Liefert für einen Mitarbeiter alle Abrechnungsmonate mit einem erfassten Arbeitsnachweis zurück.")
  @GetMapping("/abrechnungsmonateFuerMitarbeiter")
  public ResponseEntity<List<MitarbeiterAbrechnungsmonat>> getAlleAbrechnungsmonateByMitarbeiterId(
      @RequestParam Long mitarbeiterId) {
    List<MitarbeiterAbrechnungsmonat> mitarbeiterAbrechnungsmonats = arbeitsnachweisService.alleMitarbeiterAbrechnungsmonate(
        mitarbeiterId);
    return ResponseEntity.ok(mitarbeiterAbrechnungsmonats);
  }

  @Operation(summary = "Liefert zu einer ID den zugehörigen Arbeitsnachweis.")
  @GetMapping("/{id}")
  public ResponseEntity<Arbeitsnachweis> getArbeitsnachweisById(@PathVariable Long id) {
    return ResponseEntity.ok(arbeitsnachweisService.arbeitsnachweisById(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID die zugehörigen Projektstunden.")
  @GetMapping("/{id}/projektstunden")
  public ResponseEntity<List<Projektstunde>> getAlleProjektstundenByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(projektstundeService.alleProjektstundenByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID die zugehörigen Belege.")
  @GetMapping("/{id}/belege")
  public ResponseEntity<List<Beleg>> getAlleBelegeByArbeitsnachweisId(@PathVariable Long id) {
    return ResponseEntity.ok(belegService.alleBelegeByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID alle Abwesenheiten.")
  @GetMapping("/{id}/abwesenheiten")
  public ResponseEntity<List<Abwesenheit>> getAlleAbwesenheitenByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(abwesenheitService.alleAbwesenheitenByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID alle LohnartZuordnungen zurück.")
  @GetMapping("/{id}/lohnartzuordnungen")
  public ResponseEntity<List<ArbeitsnachweisLohnartZuordnung>> getAlleLohnartZuordnungenByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(
        lohnartenService.lohnartZuordnungenByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID alle LohnartberechnungLog.")
  @GetMapping("/{id}/lohnartberechnunglog")
  public ResponseEntity<List<LohnartberechnungLog>> getAlleLohnartberechnungLogByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(
        lohnartenberechnungLogService.lohnartenberechnungLogByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID alle dreiMonatsRegeln.")
  @GetMapping("/{id}/dreimonatsregeln")
  public ResponseEntity<List<DreiMonatsRegel>> getAlleDreiMonatsRegelnByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(this.arbeitsnachweisService.dreiMonatsRegelnFuerAbrechnungsmonat(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID die zugehörigen Fehlerlogs.")
  @GetMapping("/{id}/fehlerlogs")
  public ResponseEntity<List<Fehlerlog>> getAlleFehlerlogsByArbeitsnachweisId(
      @PathVariable Long id) {
    return ResponseEntity.ok(fehlerlogService.alleFehlerlogsByArbeitsnachweisId(id));
  }

  @Operation(summary = "Liefert zu einer Arbeitsnachweis-ID den Link zu den Dokumenten im DMS.")
  @GetMapping(value = "/{id}/dmsurl")
  public ResponseEntity<String> getDmsUrlFuerBelege(
      @PathVariable Long id) {
    return ResponseEntity.ok(Json.pretty(dmsService.erstelleDmsUrlFuerArbeitsnachweis(id)));
  }

  @Operation(summary = "Setzt den Arbeitsnachweis auf abgerechnet")
  @PatchMapping("/{id}/abrechnen")
  public ResponseEntity<HttpStatus> rechneArbeitsnachweisAb(@PathVariable Long id) {
    this.arbeitsnachweisService.rechneArbeitsnachweisAb(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Erstellt und liefert zu übergebenen Daten die Abrechnung.")
  @PostMapping("/abrechnung")
  public ResponseEntity<ArbeitsnachweisAbrechnung> erstelleAbrechnung(
      @RequestBody ArbeitsnachweisAbrechnungErstellungsRequest
          arbeitsnachweisAbrechnungErstellungsRequest
  ) {
    return ResponseEntity.ok(
        arbeitsnachweisAbrechnungBerechnungService.erstelleArbeitsnachweisAbrechnung(
            arbeitsnachweisAbrechnungErstellungsRequest.getArbeitsnachweis(),
            arbeitsnachweisAbrechnungErstellungsRequest.getProjektstundenNormal(),
            arbeitsnachweisAbrechnungErstellungsRequest.getReisezeiten(),
            arbeitsnachweisAbrechnungErstellungsRequest.getSonderarbeitszeiten(),
            arbeitsnachweisAbrechnungErstellungsRequest.getRufbereitschaften(),
            arbeitsnachweisAbrechnungErstellungsRequest.getAbwesenheiten(),
            arbeitsnachweisAbrechnungErstellungsRequest.getBelege()
        ));
  }


  @Operation(summary = "Erstellt und liefert zu übergebenen Reisezeit die angerechnete Reisezeit")
  @PostMapping("/reisezeitberechnen")
  public ResponseEntity<List<Projektstunde>> berechneAngerechneteReisezeit(
      @RequestBody List<Projektstunde> tatsaechlicheReisezeitProjektstunde
  ) {
    return ResponseEntity.ok(angerechneteReisezeitBerechnung.berechneAngerechneteReisezeit(
        tatsaechlicheReisezeitProjektstunde));
  }

  @Operation(summary = "Überprüft ob die Sonderzeiten der übergeben Liste inkosistent mit den normalen Projektstunden der Liste sind")
  @PostMapping("/sonderzeitinkonsistenz")
  public ResponseEntity<String> pruefeSonderzeitenAufInkosistenzenMitProjektstunden(
      @RequestBody List<Projektstunde> projektstunden
  ) {
    return ResponseEntity.ok(Json.pretty(
        projektstundenInkonsistenzBerechnung.pruefeUndErstelleWarnungFuerSonderarbeitszeitInkosistenz(
            projektstunden)));
  }

  @Operation(summary = "Überprüft ob die Projektstunden der übergeben Liste Unregelmäßigkeiten enthalten. Derzeitige Prüfung umfasst die Kontrolle der Arbeitsstunden")
  @PostMapping("/projektstundenInkonsistenzen")
  public ResponseEntity<String> pruefeProjektstundenUndErstelleWarnungen(
      @RequestBody ProjektstundenUeberpruefungsRequest projektstundenUeberpruefungsRequest
  ) {
    return ResponseEntity.ok(Json.pretty(
        projektstundenInkonsistenzBerechnung.pruefeUndErstelleWarnungFuerProjektstunden(
            projektstundenUeberpruefungsRequest.getProjektstunden(),
            projektstundenUeberpruefungsRequest.getMitarbeiterId(),
            projektstundenUeberpruefungsRequest.getJahr(),
            projektstundenUeberpruefungsRequest.getMonat())));
  }

  @Operation(summary = "Verarbeitet eine ANW-Excel Datei und liefert das Import-Ergebnis zurück.")
  @PostMapping(path = "/anwimport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ExcelImportErgebnis> verarbeiteAnwImport(
      @RequestPart("anwFile") MultipartFile arbeitsnachweisDatei,
      @RequestParam Long mitarbeiterId
  ) {

    return ResponseEntity.ok(excelImportService.importiereArbeitsnachweis(
        arbeitsnachweisDatei, mitarbeiterId, false));
  }

  @Operation(summary = "Nimmt eine Datei entgegen, um sie ins DMS zu übertragen. Gibt ein Import-Ergenis zurück.")
  @PostMapping(path = "/{id}/belegUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<DmsVerarbeitung> verarbeiteBelegImport(
      @RequestPart("belegFile") MultipartFile belegDatei,
      @RequestPart("dateiname") String dateiname,
      @PathVariable Long id
  ) {
    final Arbeitsnachweis arbeitsnachweis = arbeitsnachweisService.arbeitsnachweisById(id);
    final Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        arbeitsnachweis.getMitarbeiterId());
    DmsVerarbeitung dmsVerarbeitung;

    try {
      dmsVerarbeitung = dmsService.ladeBelegHoch(arbeitsnachweis, mitarbeiter,
          dateiname, belegDatei.getBytes());
    } catch (IOException e) {
      dmsVerarbeitung = new DmsVerarbeitung();
      dmsVerarbeitung.setFehlerhaft(true);
      dmsVerarbeitung.setMessage(e.getMessage());
    }

    return ResponseEntity.ok(dmsVerarbeitung);
  }

  @Operation(summary = "Validiert eine ANW-Excel Datei und liefert die Daten zurück.")
  @PostMapping(path = "/anwvalidieren", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<Fehlerlog>> validiereAnwImport(
      @RequestPart("anwFile") MultipartFile importierteANW
  ) {

    return ResponseEntity.ok(excelImportService.validiereArbeitsnachweis(
        importierteANW));
  }

  @Operation(summary = "Prüft und speichert die übergebenen Arbeitsnachweisdaten")
  @PostMapping("/")
  public ResponseEntity<ArbeitsnachweisSpeichernResponse> speichereArbeitsnachweis(
      @RequestBody ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest) {
    return ResponseEntity.ok(
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest));
  }

  @Operation(summary = "Löscht die Arbeitsnachweisdaten zur übergebenen ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<Set<Long>> loescheArbeitsnachweis(@PathVariable Long id) {
    return ResponseEntity.ok(arbeitsnachweisService.loescheArbeitsnachweis(id));
  }

}
