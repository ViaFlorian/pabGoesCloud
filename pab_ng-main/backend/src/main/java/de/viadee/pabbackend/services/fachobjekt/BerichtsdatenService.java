package de.viadee.pabbackend.services.fachobjekt;

import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.FIRMA;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.KEIN;
import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ErgebnisB002Excel;
import de.viadee.pabbackend.entities.ErgebnisB002RufbereitschaftenExcel;
import de.viadee.pabbackend.entities.ErgebnisB002SonderarbeitszeitenExcel;
import de.viadee.pabbackend.entities.ErgebnisB002Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB004AbwesenheitPdf;
import de.viadee.pabbackend.entities.ErgebnisB004ArbeitszeitPdf;
import de.viadee.pabbackend.entities.ErgebnisB004Pdf;
import de.viadee.pabbackend.entities.ErgebnisB004ReisekostenAuslagenPdf;
import de.viadee.pabbackend.entities.ErgebnisB004RufbereitschaftPdf;
import de.viadee.pabbackend.entities.ErgebnisB004SonderarbeitszeitPdf;
import de.viadee.pabbackend.entities.ErgebnisB004Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB007Pdf;
import de.viadee.pabbackend.entities.ErgebnisB007Uebersicht;
import de.viadee.pabbackend.entities.ErgebnisB008Details;
import de.viadee.pabbackend.entities.ErgebnisB008Excel;
import de.viadee.pabbackend.entities.ErgebnisB008Uebersicht;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB002RufbereitschaftenExcelRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB002SonderarbeitszeitenExcelRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB002UebersichtRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004AbwesenheitRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004ArbeitszeitRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004ReisekostenAuslagenRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004RufbereitschaftRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004SonderarbeitszeitRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB004UebersichtRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB007PdfRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB007UebersichtRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelDetailsBelegeANWRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelDetailsBelegeViadeeRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelDetailsReisezeitenRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelDetailsSpesenRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelDetailsZuschlaegeRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008ExcelRepository;
import de.viadee.pabbackend.repositories.pabdb.ErgebnisB008UebersichtRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BerichtsdatenService {

  private final ErgebnisB004ArbeitszeitRepository ergebnisB004ArbeitszeitRepository;
  private final ErgebnisB004SonderarbeitszeitRepository ergebnisB004SonderarbeitszeitRepository;
  private final ErgebnisB004RufbereitschaftRepository ergebnisB004RufbereitschaftRepository;
  private final ErgebnisB004ReisekostenAuslagenRepository ergebnisB004ReisekostenAuslagenRepository;
  private final ErgebnisB004AbwesenheitRepository ergebnisB004AbwesenheitRepository;
  private final ErgebnisB002UebersichtRepository ergebnisB002UebersichtRepository;
  private final ErgebnisB002RufbereitschaftenExcelRepository ergebnisB002RufbereitschaftenExcelRepository;
  private final ErgebnisB002SonderarbeitszeitenExcelRepository ergebnisB002SonderarbeitszeitenExcelRepository;
  private final ErgebnisB004UebersichtRepository ergebnisB004UebersichtRepository;
  private final ErgebnisB008UebersichtRepository ergebnisB008UebersichtRepository;
  private final ErgebnisB008ExcelRepository ergebnisB008ExcelRepository;
  private final ErgebnisB008ExcelDetailsReisezeitenRepository ergebnisB008ExcelDetailsReisezeitenRepository;
  private final ErgebnisB008ExcelDetailsBelegeANWRepository ergebnisB008ExcelDetailsBelegeANWRepository;
  private final ErgebnisB008ExcelDetailsBelegeViadeeRepository ergebnisB008ExcelDetailsBelegeViadeeRepository;
  private final ErgebnisB008ExcelDetailsSpesenRepository ergebnisB008ExcelDetailsSpesenRepository;
  private final ErgebnisB008ExcelDetailsZuschlaegeRepository ergebnisB008ExcelDetailsZuschlaegeRepository;
  private final ErgebnisB007UebersichtRepository ergebnisB007UebersichtRepository;
  private final ErgebnisB007PdfRepository ergebnisB007PdfRepository;


  public BerichtsdatenService(ErgebnisB004ArbeitszeitRepository ergebnisB004ArbeitszeitRepository,
      ErgebnisB004SonderarbeitszeitRepository ergebnisB004SonderarbeitszeitRepository,
      ErgebnisB004RufbereitschaftRepository ergebnisB004RufbereitschaftRepository,
      ErgebnisB004ReisekostenAuslagenRepository ergebnisB004ReisekostenAuslagenRepository,
      ErgebnisB004AbwesenheitRepository ergebnisB004AbwesenheitRepository,
      ErgebnisB002UebersichtRepository ergebnisB002UebersichtRepository,
      ErgebnisB002RufbereitschaftenExcelRepository ergebnisB002RufbereitschaftenExcelRepository,
      ErgebnisB002SonderarbeitszeitenExcelRepository ergebnisB002SonderarbeitszeitenExcelRepository,
      ErgebnisB004UebersichtRepository ergebnisB004UebersichtRepository,
      ErgebnisB008UebersichtRepository ergebnisB008UebersichtRepository,
      ErgebnisB008ExcelRepository ergebnisB008ExcelRepository,
      ErgebnisB008ExcelDetailsReisezeitenRepository ergebnisB008ExcelDetailsReisezeitenRepository,
      ErgebnisB008ExcelDetailsBelegeANWRepository ergebnisB008ExcelDetailsBelegeANWRepository,
      ErgebnisB008ExcelDetailsBelegeViadeeRepository ergebnisB008ExcelDetailsBelegeViadeeRepository,
      ErgebnisB008ExcelDetailsSpesenRepository ergebnisB008ExcelDetailsSpesenRepository,
      ErgebnisB008ExcelDetailsZuschlaegeRepository ergebnisB008ExcelDetailsZuschlaegeRepository,
      ErgebnisB007UebersichtRepository ergebnisB007UebersichtRepository,
      ErgebnisB007PdfRepository ergebnisB007PdfRepository) {
    this.ergebnisB004ArbeitszeitRepository = ergebnisB004ArbeitszeitRepository;
    this.ergebnisB004SonderarbeitszeitRepository = ergebnisB004SonderarbeitszeitRepository;
    this.ergebnisB004RufbereitschaftRepository = ergebnisB004RufbereitschaftRepository;
    this.ergebnisB004ReisekostenAuslagenRepository = ergebnisB004ReisekostenAuslagenRepository;
    this.ergebnisB004AbwesenheitRepository = ergebnisB004AbwesenheitRepository;
    this.ergebnisB002UebersichtRepository = ergebnisB002UebersichtRepository;
    this.ergebnisB002RufbereitschaftenExcelRepository = ergebnisB002RufbereitschaftenExcelRepository;
    this.ergebnisB002SonderarbeitszeitenExcelRepository = ergebnisB002SonderarbeitszeitenExcelRepository;
    this.ergebnisB004UebersichtRepository = ergebnisB004UebersichtRepository;
    this.ergebnisB008UebersichtRepository = ergebnisB008UebersichtRepository;
    this.ergebnisB008ExcelRepository = ergebnisB008ExcelRepository;
    this.ergebnisB008ExcelDetailsReisezeitenRepository = ergebnisB008ExcelDetailsReisezeitenRepository;
    this.ergebnisB008ExcelDetailsBelegeANWRepository = ergebnisB008ExcelDetailsBelegeANWRepository;
    this.ergebnisB008ExcelDetailsBelegeViadeeRepository = ergebnisB008ExcelDetailsBelegeViadeeRepository;
    this.ergebnisB008ExcelDetailsSpesenRepository = ergebnisB008ExcelDetailsSpesenRepository;
    this.ergebnisB008ExcelDetailsZuschlaegeRepository = ergebnisB008ExcelDetailsZuschlaegeRepository;
    this.ergebnisB007UebersichtRepository = ergebnisB007UebersichtRepository;
    this.ergebnisB007PdfRepository = ergebnisB007PdfRepository;
  }

  public List<ErgebnisB002Uebersicht> ladeB002Uebersicht(
      final int jahr,
      final int monat,
      final Long mitarbeiterId, final Long sachbearbeiterId) {

    return ergebnisB002UebersichtRepository.ladeB002Uebersicht(jahr,
        monat,
        mitarbeiterId, sachbearbeiterId);
  }

  public List<ErgebnisB004Uebersicht> ladeB004Uebersicht(int jahr, int monat, Long mitarbeiterId,
      Long sachbearbeiterId, Integer statusId) {
    return ergebnisB004UebersichtRepository.ladeB004Uebersicht(jahr, monat, statusId, mitarbeiterId,
        sachbearbeiterId);
  }

  public void ladeB004SubreportsUndBefuelleErgebnisobjekt(List<ErgebnisB004Pdf> listeBerichte,
      Mitarbeiter mitarbeiter, Arbeitsnachweis arbeitsnachweis) {
    final List<ErgebnisB004ArbeitszeitPdf> ergebnisB004ArbeitszeitPdf = ergebnisB004ArbeitszeitRepository.ladeB004ArbeitszeitFuerArbeitsnachweis(
        arbeitsnachweis.getId());
    Collections.sort(ergebnisB004ArbeitszeitPdf);
    final List<ErgebnisB004SonderarbeitszeitPdf> ergebnisB004SonderarbeitszeitPdf = ergebnisB004SonderarbeitszeitRepository.ladeB004SonderarbeitszeitFuerArbeitsnachweis(
        arbeitsnachweis.getId());
    Collections.sort(ergebnisB004SonderarbeitszeitPdf);
    final List<ErgebnisB004RufbereitschaftPdf> ergebnisB004RufbereitschaftPdf = ergebnisB004RufbereitschaftRepository.ladeB004RufbereitschaftFuerArbeitsnachweis(
        arbeitsnachweis.getId());
    Collections.sort(ergebnisB004RufbereitschaftPdf);
    final List<ErgebnisB004ReisekostenAuslagenPdf> ergebnisB004ReisekostenAuslagenPdf = ergebnisB004ReisekostenAuslagenRepository.ladeB004ReisekostenAuslagenFuerArbeitsnachweis(
        arbeitsnachweis.getId());
    Collections.sort(ergebnisB004ReisekostenAuslagenPdf);
    final List<ErgebnisB004AbwesenheitPdf> ergebnisB004AbwesenheitPdf = ergebnisB004AbwesenheitRepository.ladeB004AbwesenheitFuerArbeitsnachweis(
        arbeitsnachweis.getId());

    BigDecimal verbindungentgelt = ZERO;
    BigDecimal jobticket = ZERO;
    List<ErgebnisB004ReisekostenAuslagenPdf> reisekostenUndAuslagenOhneVerbingunsentgeldUndJobticket = new ArrayList<>();
    //Fixme: Könnte auch mit strems (filter + groupBy gehen)
    for (ErgebnisB004ReisekostenAuslagenPdf reisekostenUndAuslagen : ergebnisB004ReisekostenAuslagenPdf) {
      if (reisekostenUndAuslagen.getProjektnummer().equalsIgnoreCase("99999")) {
        verbindungentgelt = reisekostenUndAuslagen.getSummeBetrag();
      } else if (reisekostenUndAuslagen.getProjektnummer().equalsIgnoreCase("99998")) {
        jobticket = reisekostenUndAuslagen.getSummeBetrag();
      } else {
        reisekostenUndAuslagenOhneVerbingunsentgeldUndJobticket.add(reisekostenUndAuslagen);
      }
    }

    ErgebnisB004Pdf ergebnisB004Pdf = new ErgebnisB004Pdf();
    ergebnisB004Pdf.setMitarbeiter(mitarbeiter);
    ergebnisB004Pdf.setListeArbeitszeiten(ergebnisB004ArbeitszeitPdf);
    ergebnisB004Pdf.setListeSonderarbeitszeiten(ergebnisB004SonderarbeitszeitPdf);
    ergebnisB004Pdf.setListeRufbereitschaften(ergebnisB004RufbereitschaftPdf);
    ergebnisB004Pdf.setListeReisekostenAuslagen(
        reisekostenUndAuslagenOhneVerbingunsentgeldUndJobticket);
    ergebnisB004Pdf.setListeAbwesenheiten(ergebnisB004AbwesenheitPdf);
    ergebnisB004Pdf.setAuszahlung(arbeitsnachweis.getAuszahlung());
    ergebnisB004Pdf.setSollstunden(arbeitsnachweis.getSollstunden());
    //Fixme: Sollte in util oder in arbeitsnachweis selbst gemacht werden
    ergebnisB004Pdf.setSmartphone(arbeitsnachweis.getSmartphoneEigen() == null ? KEIN.toString()
        : arbeitsnachweis.getSmartphoneEigen() ? EIGEN.toString() : FIRMA.toString());
    ergebnisB004Pdf.setVerbindungsentgelt(verbindungentgelt);
    ergebnisB004Pdf.setJobticket(jobticket);

    listeBerichte.add(ergebnisB004Pdf);
  }

  public ErgebnisB002Excel ladeBerichtB002AllgemeinExcel(int jahr, int monat,
      Long mitarbeiterId, Long sachbearbeiterId) {
    final ErgebnisB002Excel ergebnisB002Excel = new ErgebnisB002Excel();

    List<ErgebnisB002RufbereitschaftenExcel> rufbereitschaften =
        ergebnisB002RufbereitschaftenExcelRepository.ladeB002RufbereitschaftenExcel(
            jahr, monat, mitarbeiterId, sachbearbeiterId);
    //TODO: Do i need sorting
    ergebnisB002Excel.setListeRufbereitschaftenExcel(rufbereitschaften);

    List<ErgebnisB002SonderarbeitszeitenExcel> sonderarbeitszeiten = ergebnisB002SonderarbeitszeitenExcelRepository.ladeB002SonderarbeitszeitenExcel(
        jahr, monat, mitarbeiterId, sachbearbeiterId);
    //TODO: Do i need sorting
    ergebnisB002Excel.setListeSonderarbeitszeitenExcel(sonderarbeitszeiten);

    return ergebnisB002Excel;
  }

  public List<ErgebnisB008Uebersicht> ladeB008Uebersicht(int abJahr, int abMonat, int bisJahr,
      int bisMonat,
      Long projektId,
      String kundeScribeId, Long sachbearbeiterId, String organisationseinheitScribeId) {
    return ergebnisB008UebersichtRepository.ladeB008Uebersicht(abJahr, abMonat, bisJahr, bisMonat,
        projektId,
        kundeScribeId, sachbearbeiterId,
        organisationseinheitScribeId);
  }

  public List<ErgebnisB008Excel> ladeBerichtB008AllgemeinExcel(int abJahr, int abMonat,
      int bisJahr,
      int bisMonat,
      Long projektId,
      String kundeScribeId, Long sachbearbeiterId, String organisationseinheitScribeId,
      boolean istDetailsAktiv,
      boolean istAusgabeInPT) {

    List<ErgebnisB008Excel> ergebnisB008Excel = ergebnisB008ExcelRepository.ladeB008Excel(
        abJahr, abMonat, bisJahr, bisMonat, projektId, kundeScribeId, sachbearbeiterId,
        organisationseinheitScribeId
    );

    if (istAusgabeInPT) {
      b008KonvertiereAusgabeInPt(ergebnisB008Excel);
    }

    if (istDetailsAktiv) {
      ladeB008Details(ergebnisB008Excel);
    }

    return ergebnisB008Excel;

  }

  private void b008KonvertiereAusgabeInPt(List<ErgebnisB008Excel> result) {
    final BigDecimal stundenProTag = BigDecimal.valueOf(8);

    for (ErgebnisB008Excel zeile : result) {
      BigDecimal tage = null, tagessatz = null;
      BigDecimal stunden = zeile.getStundenLeistungen();
      if (stunden != null) {
        tage = stunden.divide(stundenProTag, 2, RoundingMode.HALF_UP);
        zeile.setStundenLeistungen(tage);
      }

      BigDecimal stundensatz = zeile.getStundensatzLeistungen();
      if (stundensatz != null) {
        tagessatz = stundensatz.multiply(stundenProTag);
        zeile.setStundensatzLeistungen(tagessatz);
      }

      if (tage != null && tagessatz != null) {
        BigDecimal honorar = tage.multiply(tagessatz);
        zeile.setHonorarLeistungen(honorar);
      } else {
        zeile.setHonorarLeistungen(null);
      }

      tage = null;
      tagessatz = null;
      stunden = zeile.getStundenKosten();
      if (stunden != null) {
        tage = stunden.divide(stundenProTag, 2, RoundingMode.HALF_UP);
        zeile.setStundenKosten(tage);
      }

      stundensatz = zeile.getStundensatzKosten();
      if (stundensatz != null) {
        tagessatz = stundensatz.multiply(stundenProTag);
        zeile.setStundensatzKosten(tagessatz);
      }

      if (tage != null && tagessatz != null) {
        BigDecimal honorar = tage.multiply(tagessatz);
        zeile.setHonorarKosten(honorar);
      } else {
        zeile.setHonorarKosten(null);
      }
    }
  }

  private void ladeB008Details(List<ErgebnisB008Excel> ergebnisB008Excel) {
    for (ErgebnisB008Excel zeile : ergebnisB008Excel) {
      if (zeile.getKategorie().equals("Dienstreisen")) {
        Long projektId = zeile.getProjektId();
        Integer jahr =
            zeile.getAbrechnungsmonat() == null ? null : zeile.getAbrechnungsmonat().getJahr();
        Integer monat =
            zeile.getAbrechnungsmonat() == null ? null : zeile.getAbrechnungsmonat().getMonat();
        Long mitarbeiterId = zeile.getMitarbeiterId();

        List<ErgebnisB008Details> result;
        switch (zeile.getSubkategorie()) {
          case "Reisezeiten":
            result = ergebnisB008ExcelDetailsReisezeitenRepository.ladeB008ExcelDetailsReisezeiten(
                projektId, jahr, monat, mitarbeiterId);
            break;
          case "Belege lt. Arbeitsnachweis":
            result = ergebnisB008ExcelDetailsBelegeANWRepository.ladeB008ExcelDetailsBelegeANW(
                projektId, jahr, monat, mitarbeiterId);
            break;
          case "Belege (viadee)":
            result = ergebnisB008ExcelDetailsBelegeViadeeRepository.ladeB008ExcelDetailsBelegeViadee(
                projektId, jahr, monat, mitarbeiterId);
            break;
          case "Spesen":
            result = ergebnisB008ExcelDetailsSpesenRepository.ladeB008ExcelDetailsSpesen(projektId,
                jahr, monat, mitarbeiterId);
            break;
          case "Zuschläge":
            result = ergebnisB008ExcelDetailsZuschlaegeRepository.ladeB008ExcelDetailsZuschlaege(
                projektId, jahr, monat, mitarbeiterId);
            break;
          default:
            result = null;
        }
        zeile.setDetails(result);
      }
    }
  }

  public List<ErgebnisB007Uebersicht> ladeB007Uebersicht(int abJahr, int abMonat, int bisJahr,
      int bisMonat, Long sachbearbeiterId, Boolean istAktiv, Long statusId,
      String buchungstyp, Long projektId, String kundeScribeId,
      String organisationseinheitScribeId,
      Long mitarbeiterId, String projekttyp, String mitarbeiterTyp, List<String> kostenart,
      Boolean abfrageDurchOELeiter) {

    return this.ergebnisB007UebersichtRepository.ladeB007Uebersicht(abJahr, abMonat, bisJahr,
        bisMonat,
        sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId, kundeScribeId,
        organisationseinheitScribeId,
        mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter ? 1 : 0);
  }

  public List<ErgebnisB007Pdf> ladeB007Pdf(int abJahr, int abMonat, int bisJahr,
      int bisMonat, Long sachbearbeiterId, Boolean istAktiv, Long statusId,
      String buchungstyp, Long projektId, String kundeScribeId,
      String organisationseinheitScribeId,
      Long mitarbeiterId, String projekttyp, String mitarbeiterTyp,
      List<String> kostenart,
      Boolean abfrageDurchOELeiter) {

    return this.ergebnisB007PdfRepository.ladeB007Pdf(abJahr, abMonat, bisJahr,
        bisMonat,
        sachbearbeiterId, istAktiv, statusId, buchungstyp, projektId, kundeScribeId,
        organisationseinheitScribeId,
        mitarbeiterId, projekttyp, mitarbeiterTyp, kostenart, abfrageDurchOELeiter ? 1 : 0);
  }


}
