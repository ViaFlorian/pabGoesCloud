package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.ErgebnisB002Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB004Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB007Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB008Uebersicht;
import de.viadee.pabbackend.entities.MitarbeiterQuittungEmailDaten;
import de.viadee.pabbackend.services.berichte.BerichtService;
import de.viadee.pabbackend.services.fachobjekt.BerichtsdatenService;
import de.viadee.pabbackend.services.mail.MitarbeiterQuittungService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.BERICHT)
public class BerichtController {

  private final BerichtService berichtService;
  private final BerichtsdatenService berichtsdatenService;
  private final MitarbeiterQuittungService mitarbeiterQuittungService;

  public BerichtController(BerichtService berichtService,
      BerichtsdatenService berichtsdatenService,
      MitarbeiterQuittungService mitarbeiterQuittungService) {
    this.berichtService = berichtService;
    this.berichtsdatenService = berichtsdatenService;
    this.mitarbeiterQuittungService = mitarbeiterQuittungService;
  }

  @Operation(summary = "Liefert den Bericht B002 als Übersicht zurück")
  @GetMapping(value = "/b002")
  public ResponseEntity<List<ErgebnisB002Uebersicht>> getB002Uebersicht(
      @RequestParam int jahr, @RequestParam int monat,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) Long sachbearbeiterId) {

    return ResponseEntity.ok(
        berichtsdatenService.ladeB002Uebersicht(jahr, monat, mitarbeiterId, sachbearbeiterId));
  }

  @Operation(summary = "Liefert den Bericht B002 als Excel byte stream zurück")
  @GetMapping(value = "/b002/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getB002AlsExcelStream(
      @RequestParam int jahr, @RequestParam int monat,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) Long sachbearbeiterId) {

    return ResponseEntity.ok(
        berichtService.erstelleB002ExcelResource(jahr, monat, mitarbeiterId, sachbearbeiterId)
            .toByteArray());
  }

  @Operation(summary = "Liefert den Bericht B004 als Übersicht zurück")
  @GetMapping(value = "/b004")
  public ResponseEntity<List<ErgebnisB004Uebersicht>> getB004Uebersicht(
      @RequestParam int jahr, @RequestParam int monat,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Integer statusId) {

    return ResponseEntity.ok(
        berichtsdatenService.ladeB004Uebersicht(jahr, monat, mitarbeiterId, sachbearbeiterId,
            statusId));
  }

  @Operation(summary = "Liefert den Bericht B004 als pdf byte stream zurück")
  @GetMapping(value = "/b004/pdf")
  public ResponseEntity<byte[]> getB004Pdf(
      @RequestParam int jahr, @RequestParam int monat,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Integer statusId) {

    return ResponseEntity.ok(
        berichtService.erstelleB004PdfResource(jahr, monat, mitarbeiterId,
            sachbearbeiterId,
            statusId).toByteArray());
  }

  @Operation(summary = "Sendet B004 als E-mail mit den übergebenen Metadaten")
  @PostMapping("/b004/email")
  public ResponseEntity<HttpStatus> sendeB004Email(
      @RequestParam int jahr, @RequestParam int monat,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Integer statusId,
      @RequestBody MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {
    berichtService.versendeB004(jahr, monat, mitarbeiterId,
        sachbearbeiterId,
        statusId,
        mitarbeiterQuittungEmailDaten);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Liefert den Bericht B004 als pdf byte stream für die übergeben arbeitsnachweis zurück.")
  @GetMapping(value = "/b004/pdf/arbeitsnachweis", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> getB004FuerArbeitsnachweis(
      @RequestParam Long arbeitsnachweisId) {

    return ResponseEntity.ok(
        berichtService.erstelleB004PdfResource(
            arbeitsnachweisId).toByteArray());
  }

  @Operation(summary = "Erstellt zu einer Arbeitsnachweis-ID die email Daten für die Mitarbeiterquittung")
  @GetMapping(value = "/quittungemail")
  public ResponseEntity<MitarbeiterQuittungEmailDaten> getMitarbeiterQuittungEmailDaten(
      @RequestParam Long arbeitsnachweisId) {

    return ResponseEntity.ok(
        mitarbeiterQuittungService.erstelleMitarbeiterQuittungEmailDaten(arbeitsnachweisId));
  }


  @Operation(summary = "Sendet eine E-mail mit den übergebenen Daten für die Abrechnung eines Arbeitsnachweises")
  @PostMapping("/emailarbeitsnachweisabrechnung")
  public ResponseEntity<HttpStatus> sendeAbrechnungsEmail(
      @RequestParam Long arbeitsnachweisId,
      @RequestBody MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {
    berichtService.versendeB004AbrechnungAnMitarbeiter(arbeitsnachweisId,
        mitarbeiterQuittungEmailDaten);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Liefert den Bericht B008 als Übersicht zurück")
  @GetMapping(value = "/b008")
  public ResponseEntity<List<ErgebnisB008Uebersicht>> getB008Uebersicht(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) String kundeScribeId,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) String organisationseinheitScribeId) {
    return ResponseEntity.ok(
        berichtsdatenService.ladeB008Uebersicht(abWann.getYear(), abWann.getMonthValue(),
            bisWann.getYear(),
            bisWann.getMonthValue(), projektId, kundeScribeId, sachbearbeiterId,
            organisationseinheitScribeId));
  }

  @Operation(summary = "Liefert den Bericht B008 als Excel byte stream zurück")
  @GetMapping(value = "/b008/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getB008AlsExcelStream(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) Long kundeId,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Long organisationseinheitId,
      @RequestParam(required = true) boolean istDetailsAktiv,
      @RequestParam(required = true) boolean istAusgabeInPT) {

    return ResponseEntity.ok(
        berichtService.erstelleB008ExcelResource(abWann.getYear(), abWann.getMonthValue(),
                bisWann.getYear(),
                bisWann.getMonthValue(), projektId, kundeId, sachbearbeiterId,
                organisationseinheitId, istDetailsAktiv, istAusgabeInPT)
            .toByteArray());
  }

  @Operation(summary = "Liefert den Bericht B007 als Übersicht zurück")
  @GetMapping(value = "/b007")
  public ResponseEntity<List<ErgebnisB007Uebersicht>> getB007Uebersicht(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Boolean istAktiv,
      @RequestParam(required = false) Long statusId,
      @RequestParam(required = false) String buchungstyp,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) String kundeScribeId,
      @RequestParam(required = false) String organisationseinheitScribeId,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) String projekttyp,
      @RequestParam(required = false) String mitarbeiterTyp,
      @RequestParam(required = false) List<String> kostenart,
      @RequestParam(required = true) Boolean abfrageDurchOELeiter) {

    return ResponseEntity.ok(
        berichtsdatenService.ladeB007Uebersicht(abWann.getYear(), abWann.getMonthValue(),
            bisWann.getYear(), bisWann.getMonthValue(),
            sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
            kundeScribeId, organisationseinheitScribeId,
            mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter));
  }

  @Operation(summary = "Liefert den Bericht B007 als Excel byte stream zurück")
  @GetMapping(value = "/b007/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getB007AlsExcelStream(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Boolean istAktiv,
      @RequestParam(required = false) Long statusId,
      @RequestParam(required = false) String buchungstyp,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) Long kundeId,
      @RequestParam(required = false) Long organisationseinheitId,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) String projekttyp,
      @RequestParam(required = false) String mitarbeiterTyp,
      @RequestParam(required = false) List<String> kostenart,
      @RequestParam(required = true) Boolean abfrageDurchOELeiter,
      @RequestParam(required = true) Boolean istKostenartdetails) {

    return ResponseEntity.ok(
        berichtService.erstelleB007ExcelUndPdfResource(abWann.getYear(), abWann.getMonthValue(),
                bisWann.getYear(), bisWann.getMonthValue(),
                sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
                kundeId, organisationseinheitId,
                mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter,
                istKostenartdetails, true)
            .toByteArray());
  }

  @Operation(summary = "Liefert den Bericht B007 als Pdf byte stream zurück")
  @GetMapping(value = "/b007/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> getB007AlsPdf(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Boolean istAktiv,
      @RequestParam(required = false) Long statusId,
      @RequestParam(required = false) String buchungstyp,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) Long kundeId,
      @RequestParam(required = false) Long organisationseinheitId,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) String projekttyp,
      @RequestParam(required = false) String mitarbeiterTyp,
      @RequestParam(required = false) List<String> kostenart,
      @RequestParam(required = true) Boolean abfrageDurchOELeiter,
      @RequestParam(required = true) Boolean istKostenartdetails) {

    return ResponseEntity.ok(
        berichtService.erstelleB007ExcelUndPdfResource(abWann.getYear(), abWann.getMonthValue(),
                bisWann.getYear(), bisWann.getMonthValue(),
                sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
                kundeId, organisationseinheitId,
                mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter,
                istKostenartdetails, false)
            .toByteArray());
  }

  @Operation(summary = "Sendet B007 als E-mail mit den übergebenen Metadaten")
  @PostMapping("/b007/email")
  public ResponseEntity<HttpStatus> sendeB007Email(
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate abWann,
      @RequestParam(required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate bisWann,
      @RequestParam(required = false) Long sachbearbeiterId,
      @RequestParam(required = false) Boolean istAktiv,
      @RequestParam(required = false) Long statusId,
      @RequestParam(required = false) String buchungstyp,
      @RequestParam(required = false) Long projektId,
      @RequestParam(required = false) Long kundeId,
      @RequestParam(required = false) Long organisationseinheitId,
      @RequestParam(required = false) Long mitarbeiterId,
      @RequestParam(required = false) String projekttyp,
      @RequestParam(required = false) String mitarbeiterTyp,
      @RequestParam(required = false) List<String> kostenart,
      @RequestParam(required = true) Boolean abfrageDurchOELeiter,
      @RequestParam(required = true) Boolean istKostenartdetails,
      @RequestBody MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {

    berichtService.versendeB007Email(abWann.getYear(), abWann.getMonthValue(),
        bisWann.getYear(), bisWann.getMonthValue(),
        sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
        kundeId, organisationseinheitId,
        mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter,
        istKostenartdetails, mitarbeiterQuittungEmailDaten);

    return ResponseEntity.ok().build();
  }


}
