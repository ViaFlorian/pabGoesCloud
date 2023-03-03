package de.viadee.pabbackend.services.berichte;

import static de.viadee.pabbackend.enums.Reports.B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL;
import static de.viadee.pabbackend.enums.Reports.B004_ARBEITSNACHWEIS_PRO_MITARBEITER_UND_ABRECHNUNGSMONAT;
import static de.viadee.pabbackend.enums.Reports.B007_PROJEKT_KOSTEN_LEISTUNG_PDF;
import static de.viadee.pabbackend.enums.Reports.B008_PROJEKT_RECHNUNGSVORLAGE_EXCEL;

import de.viadee.pabbackend.entities.Abrechnungsmonat;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ErgebnisB002Excel;
import de.viadee.pabbackend.entities.ErgebnisB004Pdf;
import de.viadee.pabbackend.entities.ErgebnisB004Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB007Pdf;
import de.viadee.pabbackend.entities.ErgebnisB008Excel;
import de.viadee.pabbackend.entities.Kunde;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterQuittungEmailDaten;
import de.viadee.pabbackend.entities.Organisationseinheit;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus;
import de.viadee.pabbackend.enums.Reports;
import de.viadee.pabbackend.exception.PabException;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.BerichtsdatenService;
import de.viadee.pabbackend.services.fachobjekt.KundeService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.OrganisationseinheitService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.jasperreports.JasperService;
import de.viadee.pabbackend.services.mail.MailService;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class BerichtService {

  private static final BigDecimal UMSATZSTEUER =
      BigDecimal.valueOf(19.0);
  private final ArbeitsnachweisService arbeitsnachweisService;
  private final MitarbeiterService mitarbeiterService;
  private final BerichtsdatenService berichtsdatenService;
  private final JasperService jasperService;
  private final MailService mailService;
  private final KundeService kundeService;
  private final OrganisationseinheitService organisationseinheitService;
  private final ProjektService projektService;

  public BerichtService(ArbeitsnachweisService arbeitsnachweisService,
      MitarbeiterService mitarbeiterService, BerichtsdatenService berichtsdatenService,
      JasperService jasperService, MailService mailService, KundeService kundeService,
      OrganisationseinheitService organisationseinheitService,
      ProjektService projektService) {
    this.arbeitsnachweisService = arbeitsnachweisService;
    this.mitarbeiterService = mitarbeiterService;
    this.berichtsdatenService = berichtsdatenService;
    this.jasperService = jasperService;
    this.mailService = mailService;
    this.kundeService = kundeService;
    this.organisationseinheitService = organisationseinheitService;
    this.projektService = projektService;
  }

  public ByteArrayOutputStream erstelleB002ExcelResource(int jahr, int monat,
      Long mitarbeiterId, Long sachbearbeiterId) {

    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    final List<ErgebnisB002Excel> ergebnisseB002Excel = Collections.singletonList(
        berichtsdatenService.ladeBerichtB002AllgemeinExcel(
            jahr, monat, mitarbeiterId, sachbearbeiterId));

    B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL.setTempSheetNames(null);
    final List<String> tmpSheetnames = ermittleAktuelleTabnamenFuerB002Excel(ergebnisseB002Excel);
    if (tmpSheetnames.size() > 0) {
      final String[] subreportsArr = new String[tmpSheetnames.size()];
      B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL.setTempSheetNames(
          tmpSheetnames.toArray(subreportsArr));
    }

    final Map<String, Object> params = erzeugeParameter(
        erstelleFilterStringFuerB002(jahr, monat, mitarbeiterId, sachbearbeiterId));

    jasperService.ladeExcelReport(B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL, ergebnisseB002Excel,
        params, output);

    return output;
  }


  public ByteArrayOutputStream erstelleB004PdfResource(int jahr, int monat,
      Long mitarbeiterId, Long sachbearbeiterId, Integer statusId) {
    List<ErgebnisB004Uebersicht> ergebnisseB004Ueberischt = this.berichtsdatenService.ladeB004Uebersicht(
        jahr, monat, mitarbeiterId, sachbearbeiterId, statusId);

    final List<ErgebnisB004Pdf> ergebnisseB004Pdf = new ArrayList<>();
    for (ErgebnisB004Uebersicht ergebnisB004Uebersicht : ergebnisseB004Ueberischt) {
      Arbeitsnachweis arbeitsnachweis = arbeitsnachweisService.arbeitsnachweisById(
          ergebnisB004Uebersicht.getArbeitsnachweisID());
      Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
          ergebnisB004Uebersicht.getMitarbeiterId());

      berichtsdatenService.ladeB004SubreportsUndBefuelleErgebnisobjekt(ergebnisseB004Pdf,
          mitarbeiter,
          arbeitsnachweis);
    }

    final Map<String, Object> params = erzeugeParameter(
        erstelleFilterStringFuerB004(jahr, monat, mitarbeiterId, sachbearbeiterId,
            statusId));

    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    jasperService.ladePdfReport(B004_ARBEITSNACHWEIS_PRO_MITARBEITER_UND_ABRECHNUNGSMONAT,
        ergebnisseB004Pdf, params, output);

    return output;

  }


  public ByteArrayOutputStream erstelleB004PdfResource(
      Long arbeitsnachweisId) {
    Arbeitsnachweis arbeitsnachweis = arbeitsnachweisService.arbeitsnachweisById(arbeitsnachweisId);
    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        arbeitsnachweis.getMitarbeiterId());

    final List<ErgebnisB004Pdf> ergebnisseB004 = new ArrayList<>();
    berichtsdatenService.ladeB004SubreportsUndBefuelleErgebnisobjekt(ergebnisseB004, mitarbeiter,
        arbeitsnachweis);

    final Map<String, Object> params = erzeugeParameter(
        erstelleFilterStringFuerB004Arbeitsnachweis(arbeitsnachweis, mitarbeiter));

    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    jasperService.ladePdfReport(B004_ARBEITSNACHWEIS_PRO_MITARBEITER_UND_ABRECHNUNGSMONAT,
        ergebnisseB004, params, output);

    return output;

  }


  public void versendeB004(int jahr, int monat, Long mitarbeiterId, Long sachbearbeiterId,
      Integer statusId, MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {

    List<Pair<String, byte[]>> anhaenge = new ArrayList<>();
    String[] emailAdressen = mitarbeiterQuittungEmailDaten.getEmailAdressen();

    try {
      Pair<String, byte[]> mailAnhang = Pair.of(mitarbeiterQuittungEmailDaten.getDateiname(),
          erstelleB004PdfResource(jahr, monat, mitarbeiterId, sachbearbeiterId,
              statusId).toByteArray());
      anhaenge.add(mailAnhang);

      mailService.sendMailWithAttachments(emailAdressen, mitarbeiterQuittungEmailDaten.getBetreff(),
          mitarbeiterQuittungEmailDaten.getNachricht(),
          anhaenge);
    } catch (Exception e) {
      if (e instanceof NullPointerException) {
        throw new PabException("Versand nicht möglich, Emailadresse ungültig.");
      } else {
        throw new PabException(
            "Fehler beim Versand der Email an:\n" + String.join(",", emailAdressen)
                + "\nFehlermeldung: " + e.getMessage());
      }
    }

  }

  public void versendeB004AbrechnungAnMitarbeiter(Long arbeitsnachweisId,
      MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {

    List<Pair<String, byte[]>> anhaenge = new ArrayList<>();
    String[] emailAdressen = mitarbeiterQuittungEmailDaten.getEmailAdressen();

    try {
      Pair<String, byte[]> mailAnhang = Pair.of(mitarbeiterQuittungEmailDaten.getDateiname(),
          erstelleB004PdfResource(arbeitsnachweisId).toByteArray());
      anhaenge.add(mailAnhang);

      mailService.sendMailWithAttachments(emailAdressen, mitarbeiterQuittungEmailDaten.getBetreff(),
          mitarbeiterQuittungEmailDaten.getNachricht(),
          anhaenge);
    } catch (Exception e) {
      if (e instanceof NullPointerException) {
        throw new PabException("Versand nicht möglich, Emailadresse ungültig.");
      } else {
        throw new PabException(
            "Fehler beim Versand der Email an:\n" + String.join(",", emailAdressen)
                + "\nFehlermeldung: " + e.getMessage());
      }
    }

  }

  private Map<String, Object> erzeugeParameter(final String filterString) {

    final ClassPathResource logoDatei = new ClassPathResource(
        "reports/viadee_Logo_rot.png");
    Image image = null;
    try {
      image = ImageIO.read(logoDatei.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    final Map<String, Object> params = new HashMap<>();
    params.put("FilterString", "Filterkriterien - " + filterString);
    params.put("logo", image);
    params.put(JRParameter.REPORT_LOCALE, Locale.GERMAN);
    return params;
  }


  private List<String> ermittleAktuelleTabnamenFuerB002Excel(
      final List<ErgebnisB002Excel> listeErgebnisB002Excel) {
    final List<String> tmpSheetnames = new ArrayList<>();
    final ErgebnisB002Excel ergebnisB002Excel = listeErgebnisB002Excel.get(0);
    if (!ergebnisB002Excel.getListeRufbereitschaftenExcel().isEmpty()) {
      tmpSheetnames.add(Reports.B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL.getSheetNames()[0]);
    }
    if (!ergebnisB002Excel.getListeSonderarbeitszeitenExcel().isEmpty()) {
      tmpSheetnames.add(Reports.B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL.getSheetNames()[1]);
    }
    return tmpSheetnames;
  }

  private String erstelleFilterStringFuerB002(int abJahr, int abMonat, Long mitarbeiterId,
      Long sachbearbeiterId) {
    Mitarbeiter mitarbeiter =
        mitarbeiterId == null ? null : mitarbeiterService.mitarbeiterById(mitarbeiterId);
    Mitarbeiter sachbearbeiter =
        sachbearbeiterId == null ? null : mitarbeiterService.mitarbeiterById(sachbearbeiterId);

    String mitarbeiterName = mitarbeiter == null ? ""
        : ", Mitarbeiter/in:" + mitarbeiter.getFullName();
    String sachbearbeiterName = sachbearbeiter == null ? ""
        : ", Sachbearbeiter/in: " + sachbearbeiter.getFullName();

    return String.format("Zeitraum: %s%s%s",
        new Abrechnungsmonat(abJahr, abMonat, false), mitarbeiterName, sachbearbeiterName);

  }

  private String erstelleFilterStringFuerB004Arbeitsnachweis(final Arbeitsnachweis arbeitsnachweis,
      Mitarbeiter mitarbeiter) {
    String abrechnungsmonatText =
        arbeitsnachweis == null ? "" : "Abrechnungsmonat: " + arbeitsnachweis.getDatum();
    String mitarbeiterName = arbeitsnachweis == null || mitarbeiter == null ? ""
        : ", Mitarbeiter/in:" + mitarbeiter.getFullName();
    return abrechnungsmonatText + mitarbeiterName;
  }


  private String erstelleFilterStringFuerB004(int jahr, int monat, Long mitarbeiterId,
      Long sachbearbeiterId,
      Integer statusId) {
    Mitarbeiter mitarbeiter =
        mitarbeiterId == null ? null : mitarbeiterService.mitarbeiterById(mitarbeiterId);
    Mitarbeiter sachbearbeiter =
        sachbearbeiterId == null ? null : mitarbeiterService.mitarbeiterById(sachbearbeiterId);
    ProjektabrechnungBearbeitungsstatus projektabrechnungBearbeitungsstatus =
        statusId == null ? null : ProjektabrechnungBearbeitungsstatus.fromStatusId(statusId);
    return String.format("Abrechnungsmonat: %d/%d", jahr, monat)
        + (sachbearbeiter == null ? ""
        : ", Sachbearbeiter/in: " + sachbearbeiter.getFullName())
        + (mitarbeiter == null ? ""
        : ", Mitarbeiter/in:" + mitarbeiter.getFullName())
        + (projektabrechnungBearbeitungsstatus == null ? ""
        : ", Status: " + projektabrechnungBearbeitungsstatus);
  }

  public ByteArrayOutputStream erstelleB008ExcelResource(int abJahr, int abMonat, int bisJahr,
      int bisMonat,
      Long projektId,
      Long kundeId, Long sachbearbeiterId, Long organisationseinheitId, boolean istDetailsAktiv,
      boolean istAusgabeInPT) {

    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    final List<ErgebnisB008Excel> ergebnisB008ExcelList = berichtsdatenService.ladeBerichtB008AllgemeinExcel(
        abJahr,
        abMonat, bisJahr, bisMonat, projektId,
        kundeId == null ? null : kundeService.kundeById(kundeId).getScribeId(),
        sachbearbeiterId,
        organisationseinheitId == null ? null
            : organisationseinheitService.organisationseinheitById(organisationseinheitId)
                .getScribeId(), istDetailsAktiv, istAusgabeInPT);

    ergebnisB008ExcelList.forEach(ergebnisB008Excel -> {
      Abrechnungsmonat abrechnungsmonat = new Abrechnungsmonat(ergebnisB008Excel.getJahr(),
          ergebnisB008Excel.getMonat(), false);
      ergebnisB008Excel.setAbrechnungsmonat(abrechnungsmonat);
      Projekt projekt = ergebnisB008Excel.getProjektId() == null ? null
          : projektService.projektById(ergebnisB008Excel.getProjektId());
      ergebnisB008Excel.setProjekt(projekt);
      Mitarbeiter mitarbeiter = ergebnisB008Excel.getMitarbeiterId() == null ? null
          : mitarbeiterService.mitarbeiterById(ergebnisB008Excel.getMitarbeiterId());
      ergebnisB008Excel.setMitarbeiter(mitarbeiter);
    });

    final Map<String, Object> params = erzeugeParameter(
        erstelleFilterStringFuerB008(abJahr, abMonat, bisJahr,
            bisMonat,
            projektId,
            kundeId, sachbearbeiterId, organisationseinheitId));
    params.put("umsatzsteuerSatz", UMSATZSTEUER);
    params.put("istDetailsAktiv", istDetailsAktiv);
    params.put("istAusgabeInPT", istAusgabeInPT);

    jasperService.ladeExcelReport(B008_PROJEKT_RECHNUNGSVORLAGE_EXCEL,
        ergebnisB008ExcelList, params,
        output);

    return output;
  }

  private String erstelleFilterStringFuerB008(int abJahr, int abMonat, int bisJahr,
      int bisMonat,
      Long projektId,
      Long kundeId, Long sachbearbeiterId, Long organisationseinheitId) {

    String abWann = "" + abMonat + "-" + abJahr;
    String bisWann = "" + bisMonat + "-" + bisJahr;
    Projekt projekt = projektId == null ? null : projektService.projektById(projektId);
    Kunde kunde = kundeId == null ? null : kundeService.kundeById(kundeId);
    Mitarbeiter sachbearbeiter =
        sachbearbeiterId == null ? null : mitarbeiterService.mitarbeiterById(sachbearbeiterId);
    Organisationseinheit organisationseinheit = organisationseinheitId == null ? null
        : organisationseinheitService.organisationseinheitById(
            organisationseinheitId);

    String stringBuilder = "Abrechnungsmonate: "
        + abWann
        + " bis "
        + bisWann
        + (projekt == null ? "" : ", Projekt: " + projekt.getProjektnummer())
        + (kunde == null ? "" : ", Kunde: " + kunde.getBezeichnung())
        + (organisationseinheit == null ? "" : ", OE: " + organisationseinheit.getBezeichnung())
        + (sachbearbeiter == null ? "" : ", Sachbearbeiter/in: " + sachbearbeiter.getFullName());

    return stringBuilder;

  }

  public ByteArrayOutputStream erstelleB007ExcelUndPdfResource(int abJahr, int abMonat, int bisJahr,
      int bisMonat,
      Long sachbearbeiterId, Boolean istAktiv, Long statusId, String buchungstyp,
      Long projektId,
      Long kundeId,
      Long organisationseinheitId, Long mitarbeiterId, String projekttyp,
      String mitarbeiterTyp,
      List<String> kostenart, Boolean abfrageDurchOELeiter, Boolean istKostenartdetails,
      Boolean istExcelReport) {

    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final List<ErgebnisB007Pdf> ergebnisseB007 = berichtsdatenService
        .ladeB007Pdf(abJahr, abMonat, bisJahr,
            bisMonat,
            sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
            kundeId == null ? null : kundeService.kundeById(kundeId).getScribeId(),
            organisationseinheitId == null ? null
                : organisationseinheitService.organisationseinheitById(organisationseinheitId)
                    .getScribeId(), mitarbeiterId, projekttyp,
            mitarbeiterTyp,
            kostenart, abfrageDurchOELeiter);

    ergebnisseB007.stream()
        .forEach(satz -> satz.setKostenartDetails(istKostenartdetails ? "true" : "false"));

    ergebnisseB007.stream().forEach(
        satz -> satz.setEinzelmonat(abJahr == bisJahr && abMonat == bisMonat ? "true" : "false"));

    Map<String, Set<String>> projektAbrechnungsmonatGruppe = new HashMap<>();
    for (ErgebnisB007Pdf ergebnisB007 : ergebnisseB007) {
      projektAbrechnungsmonatGruppe.put(
          ergebnisB007.getProjektnummer() + "_" + ergebnisB007.getJahr() + "_"
              + ergebnisB007.getMonat(), new HashSet<>());
    }
    for (String abrechnungsMonatGruppeKey : projektAbrechnungsmonatGruppe.keySet()) {
      for (ErgebnisB007Pdf ergebnisB007Pdf : ergebnisseB007) {
        if ((ergebnisB007Pdf.getProjektnummer() + "_" + ergebnisB007Pdf.getJahr() + "_"
            + ergebnisB007Pdf.getMonat()).equals(abrechnungsMonatGruppeKey)) {
          projektAbrechnungsmonatGruppe.get(abrechnungsMonatGruppeKey)
              .add(ergebnisB007Pdf.getProjektBuchungstypForPdf());
        }
      }
    }
    for (String abrechnungsMonatGruppeKey : projektAbrechnungsmonatGruppe.keySet()) {
      for (ErgebnisB007Pdf ergebnisB007Pdf : ergebnisseB007) {
        if ((ergebnisB007Pdf.getProjektnummer() + "_" + ergebnisB007Pdf.getJahr() + "_"
            + ergebnisB007Pdf.getMonat()).equals(abrechnungsMonatGruppeKey)) {
          ergebnisB007Pdf.setAnzahlBuchungstypenInProjektAbrechnungsmonatGruppe(
              projektAbrechnungsmonatGruppe.get(abrechnungsMonatGruppeKey).size());
        }
      }
    }
    Map<String, Set<String>> projektGruppe = new HashMap<>();
    for (ErgebnisB007Pdf ergebnisB007 : ergebnisseB007) {
      projektGruppe.put(ergebnisB007.getProjektnummer(), new HashSet<>());
    }
    for (String gruppeKey : projektGruppe.keySet()) {
      for (ErgebnisB007Pdf ergebnisB007Pdf : ergebnisseB007) {
        if ((ergebnisB007Pdf.getProjektnummer()).equals(gruppeKey)) {
          projektGruppe.get(gruppeKey)
              .add(ergebnisB007Pdf.getJahr().toString() + ergebnisB007Pdf.getMonat().toString());
        }
      }
    }
    for (String gruppeKey : projektGruppe.keySet()) {
      for (ErgebnisB007Pdf ergebnisB007Pdf : ergebnisseB007) {
        if ((ergebnisB007Pdf.getProjektnummer()).equals(gruppeKey)) {
          ergebnisB007Pdf.setAnzahlMonateInProjektGruppe(projektGruppe.get(gruppeKey).size());
        }
      }
    }

    final Map<String, Object> params = erzeugeParameter(
        erstelleFilterStringAusB007ProjektKostenLeistungenVM(abJahr, abMonat, bisJahr,
            bisMonat,
            sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId,
            kundeId,
            organisationseinheitId, mitarbeiterId, projekttyp,
            mitarbeiterTyp,
            kostenart, abfrageDurchOELeiter, istKostenartdetails));

    boolean berichtIstVorlaeufig = ergebnisseB007.stream().anyMatch(ErgebnisB007Pdf::getVorlaeufig);
    params.put("istVorlaeufig", berichtIstVorlaeufig);

    if (istExcelReport) {
      jasperService.ladeExcelReport(B007_PROJEKT_KOSTEN_LEISTUNG_PDF, ergebnisseB007, params,
          output);
    } else {
      jasperService.ladePdfReport(B007_PROJEKT_KOSTEN_LEISTUNG_PDF, ergebnisseB007, params,
          output);
    }

    return output;
  }

  private String erstelleFilterStringAusB007ProjektKostenLeistungenVM(int abJahr, int abMonat,
      int bisJahr,
      int bisMonat,
      Long sachbearbeiterId, Boolean istAktiv, Long statusId, String buchungstyp,
      Long projektId,
      Long kundeId,
      Long organisationseinheitId, Long mitarbeiterId, String projekttyp,
      String mitarbeiterTyp,
      List<String> kostenart, Boolean abfrageDurchOELeiter, Boolean istKostenartdetails) {

    String abWann = "" + abMonat + "-" + abJahr;
    String bisWann = "" + bisMonat + "-" + bisJahr;
    String istAktivStr = "";
    if (istAktiv != null) {
      istAktivStr = istAktiv ? "Aktiv" : "Inaktiv";
    }

    Projekt projekt = projektId == null ? null : projektService.projektById(projektId);
    Mitarbeiter mitarbeiter =
        mitarbeiterId == null ? null : mitarbeiterService.mitarbeiterById(mitarbeiterId);
    Kunde kunde = kundeId == null ? null : kundeService.kundeById(kundeId);
    Organisationseinheit oe = organisationseinheitId == null ? null
        : organisationseinheitService.organisationseinheitById(
            organisationseinheitId);
    Mitarbeiter sachbearbeiter =
        sachbearbeiterId == null ? null : mitarbeiterService.mitarbeiterById(sachbearbeiterId);
    ProjektabrechnungBearbeitungsstatus status =
        statusId == null ? null : ProjektabrechnungBearbeitungsstatus.fromStatusId(
            statusId.intValue());

    String str = "Zeitraum: ";
    str += abWann;
    str += " bis " + bisWann;
    str += (projekt != null) ? ", Projekt: " + projekt.getProjektnummer() + "; "
        + projekt.getBezeichnung() : "";
    str += (mitarbeiter != null) ? ", Mitarbeiter/in: "
        + mitarbeiter.getMitarbeiterPersonalnummerInklGanzerName() : "";
    str += (kunde != null) ? ", Kunde: " + kunde.getBezeichnung() : "";
    str += (oe != null) ? ", OE: " + oe.getBezeichnung() : "";
    str += (sachbearbeiter != null) ? ", Sachbearbeiter/in: " + sachbearbeiter.getFullName() : "";
    str += (status != null) ? ", Status: "
        + status : "";
    str += (projekttyp != null) ?
        ", Projekttyp: " + projekttyp : "";
    str += (istAktivStr != "") ?
        ", Aktiv/Inaktiv: " + istAktivStr : "";
    str +=
        (buchungstyp != null) ? ", Buchungstyp: " + buchungstyp : "";
    str += (kostenart != null && !kostenart.isEmpty()) ? ", Kostenart: "
        + kostenart.stream()
        .collect(Collectors.joining(", ")) : "";
    str += (istKostenartdetails ? ", Kostenartendetails=J" : "");
    str += (mitarbeiterTyp != null) ?
        ", Mitarbeitergruppe=" + mitarbeiterTyp : "";
    return str;
  }

  public void versendeB007Email(int abJahr, int abMonat, int bisJahr,
      int bisMonat,
      Long sachbearbeiterId, Boolean istAktiv, Long statusId, String buchungstyp,
      Long projektId,
      Long kundeId,
      Long organisationseinheitId, Long mitarbeiterId, String projekttyp,
      String mitarbeiterTyp,
      List<String> kostenart, Boolean abfrageDurchOELeiter, Boolean istKostenartdetails,
      MitarbeiterQuittungEmailDaten mitarbeiterQuittungEmailDaten) {

    List<Pair<String, byte[]>> anhaenge = new ArrayList<>();
    String[] emailAdressen = mitarbeiterQuittungEmailDaten.getEmailAdressen();

    try {
      Pair<String, byte[]> mailAnhang = Pair.of(mitarbeiterQuittungEmailDaten.getDateiname(),
          erstelleB007ExcelUndPdfResource(abJahr, abMonat, bisJahr,
              bisMonat,
              sachbearbeiterId, istAktiv, statusId, buchungstyp,
              projektId,
              kundeId,
              organisationseinheitId, mitarbeiterId, projekttyp,
              mitarbeiterTyp,
              kostenart, abfrageDurchOELeiter, istKostenartdetails, false).toByteArray());
      anhaenge.add(mailAnhang);

      mailService.sendMailWithAttachments(emailAdressen, mitarbeiterQuittungEmailDaten.getBetreff(),
          mitarbeiterQuittungEmailDaten.getNachricht(),
          anhaenge);
    } catch (Exception e) {
      if (e instanceof NullPointerException) {
        throw new PabException("Versand nicht möglich, Emailadresse ungültig.");
      } else {
        throw new PabException(
            "Fehler beim Versand der Email an:\n" + String.join(",", emailAdressen)
                + "\nFehlermeldung: " + e.getMessage());
      }
    }

  }

}
