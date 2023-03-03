package de.viadee.pabbackend.services.excel;

import static de.viadee.pabbackend.enums.ImportFehlerklasse.FEHLER;
import static de.viadee.pabbackend.enums.ImportFehlerklasse.HINWEIS;
import static de.viadee.pabbackend.enums.ImportFehlerklasse.KRITISCH;
import static de.viadee.pabbackend.enums.ImportFehlerklasse.WARNUNG;
import static de.viadee.pabbackend.services.excel.ExcelImport.ANW_SPALTE_ARBEITSZEIT;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEITEN_SPALTE_SUMME_SONDERARBEITSZEIT_INDEX;
import static de.viadee.pabbackend.services.excel.ExcelImport.SUMME_RUFBEREITSCHAFT_INDEX;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Fehlerlog;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus;
import de.viadee.pabbackend.enums.ImportFehlerklasse;
import de.viadee.pabbackend.util.FormatFactory;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExcelImportLogWriter {


    private final DecimalFormat dezimalZahlenFormatter = FormatFactory
        .deutschesDezimalzahlenFormatMitZweiNachkommastellen();

    public static String excelSpaltenName(int index) {
        final StringBuilder sb = new StringBuilder();
        while (index-- > 0) {
            sb.append((char) ('A' + (index % 26)));
            index /= 26;
        }
        return sb.reverse().toString();
    }

    public void schreibeFehlerInDerArbeitsnachweisVerarbeitung(final List<Fehlerlog> fehlerlog,
        final String zelle, final String message) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, "",
            "Fehler in der Arbeitsnachweisverarbeitung: " + message,
            KRITISCH);
    }

    public void schreibeAbrechnungsmonatKonnteNichtErmitteltWerdenFehlermeldung(
        final List<Fehlerlog> fehlerlog,
        final String zelle) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, "",
            "Der Abrechnungsmonat konnte nicht ermittelt werden",
            KRITISCH);
    }

    public void schreibeAbrechnungsmonatLiegtInDerZukunftFehlermeldung(
        final List<Fehlerlog> fehlerlog,
        final String zelle,
        final LocalDate maximalErlaubterMonat) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, "",
            "Abrechnungsmonat liegt in der Zukunft, maximal Abrechnungsmonat "
                + maximalErlaubterMonat.getYear() + "/" + maximalErlaubterMonat.getMonthValue()
                + " erlaubt",
            KRITISCH);
    }

    public void schreibeAbrechnungsmonatIstBereitsAbgeschlossenFehlermeldung(
        final List<Fehlerlog> fehlerlog,
        final String zelle,
        LocalDate abrechnungsmonat) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, "",
            "Der Abrechnungsmonat "
                + abrechnungsmonat.getYear() + "/" + abrechnungsmonat.getMonthValue()
                + " ist bereits abgeschlossen",
            KRITISCH);
    }

    public void schreibeKeinImportAufgrundVonArbeitsnachweisStatus(final List<Fehlerlog> fehlerlog,
        final Arbeitsnachweis arbeitsnachweis) {
        erstelleFehlerlogEintrag(fehlerlog, "", "",
            "Arbeitsnachweis kann nicht importiert werden, es liegt im Abrechnungsmonat "
                + arbeitsnachweis.getDatum()
                + " bereits ein importierter Arbeitsnachweis mit Status \""
                + ArbeitsnachweisBearbeitungsstatus.idToAnzeigeString(arbeitsnachweis.getStatusId())
                + "\" vor",
            KRITISCH);
    }

    public void schreibeKeinenMitarbeiterZurPersonalnummerGefundenFehlermeldung(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "",
            "Kein Mitarbeiter zur angegebenen Personalnummer gefunden",
            KRITISCH);
    }

    public void schreibeMitarbeiterUnterschiedlichFehlermeldung(final List<Fehlerlog> fehlerlog,
        final Mitarbeiter mitarbeiter,
        final Mitarbeiter mitarbeiterInBearbeitung) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Mitarbeiter unterschiedlich: "
                + mitarbeiterInBearbeitung.getFullName() + " <> " + mitarbeiter.getFullName()
                + " (Excel)",
            KRITISCH);
    }

    public void schreibeMitarbeiterHatKeinenStellenfaktorFehlermeldung(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Kein Stellenfaktor für den Abrechnungsmonat administriert.",
            KRITISCH);
    }

    public void schreibeBlattANWFehltFehlermeldung(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Die Datei enthält kein Blatt \"ANW\"",
            KRITISCH);
    }

    public void schreibeSchwererFehlerBeiDerFormelauswertung(final List<Fehlerlog> fehlerlog,
        final String meldung) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Schwerer Fehler in der Formelauswertung: " + meldung,
            KRITISCH);
    }

    public void schreibeSummenZeileInBlattANWKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Summenzeile im Blatt ANW konnte nicht ermittelt werden, Bezeichner \"Summe\" fehlt oder ist nicht in der Spalte C enthalten",
            KRITISCH);
    }

    public void schreibeSummenZeileInBlattSonderzeitenKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Summenzeile im Blatt Sonderzeiten konnte nicht ermittelt werden, Bezeichner \"Summen\" fehlt oder ist nicht in der Spalte A enthalten",
            KRITISCH);
    }

    public void schreibeSummenZeileInBlattSARKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Summenzeile im Blatt SAR konnte nicht ermittelt werden, Bezeichner \"Summen\" fehlt oder ist nicht in der Spalte D enthalten",
            KRITISCH);
    }

    public void schreibeEndeMarkerInBlattAnwKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Der Ende-Marker im Blatt ANW konnte nicht ermittelt werden, Bezeichner \"ENDE\" fehlt",
            KRITISCH);
    }

    public void schreibeAngReisezeitSpalteInBlattANWKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Spalte \"angerechn. Reisezeit\" im Blatt ANW konnte nicht ermittelt werden, Bezeichner fehlt",
            KRITISCH);
    }

    public void schreibeSmartphoneZeileInBlattSARKonnteNichtErmitteltWerden(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Smartphone-Zeile konnte nicht ermittelt werden, Bezeichner \"Smartphone:\" fehlt oder ist nicht in Spalte N enthalten",
            KRITISCH);
    }

    public void schreibeImportFehlerfreiDurchgefuehrt(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Der Import wurde erfolgreich durchgeführt",
            HINWEIS);
    }

    public void schreibeBlattSonderzeitenFehltFehlermeldung(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Datei enthält kein Blatt \"Sonderzeiten\"",
            HINWEIS);
    }

    public void schreibeKeineRufbereitschaftImportiert(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Es wurde keine Rufbereitschaft importiert",
            HINWEIS);
    }

    public void schreibeKeineSonderarbeitszeitImportiert(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Es wurde keine Sonderarbeitszeit importiert",
            HINWEIS);
    }

    public void schreibeKeineAbwesenheitImportiert(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Es wurde keine Abwesenheit importiert",
            HINWEIS);
    }

    public void schreibeKeineBelegeImportiert(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Es wurden keine Belege importiert",
            HINWEIS);
    }

    public void schreibeBlattSARFehltFehlermeldung(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Die Datei enthält kein Blatt \"SAR\"",
            HINWEIS);
    }

    public void schreibeDieDateiKonnteNichtGelesenWerdenFehlermeldung(
        final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Die Datei konnte nicht gelesen werden",
            KRITISCH);
    }

    public void schreibeFormatNichtUnterstuetzt(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Nur OpenXML-Format (*.xlsx) zulässig",
            KRITISCH);
    }

    public void schreibeKeineGueltigeExcelDateiFehlermeldung(final List<Fehlerlog> fehlerlog) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein", "Keine gültige Excel Datei",
            KRITISCH);
    }

    public void schreibePersonalnummerKonnteNichtErmitteltWerdenFehlermeldung(
        final List<Fehlerlog> fehlerlog,
        final String zelle) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, "",
            "Die Personalnummer konnte nicht ermittelt werden",
            KRITISCH);
    }

    public void schreibeArbeitsnachweisTemplateVersionUnterschiedlich(
        final List<Fehlerlog> fehlerlog, final String arbeitsnachweisVersionIst,
        final String arbeitsnachweisVersionSoll, final boolean validierungsModus) {
        erstelleFehlerlogEintrag(fehlerlog, "", "Allgemein",
            "Version des verwendeten Template (" + arbeitsnachweisVersionIst
                + ") ungleich aktueller Template-Version (" + arbeitsnachweisVersionSoll + ")",
            validierungsModus ? WARNUNG : KRITISCH);
    }

    public void schreibeProjektnummerUnbekanntFehlermeldung(final List<Fehlerlog> fehlerlog,
        final String blatt,
        final String zelle, final String projektNummer) {
        if (projektNummer != null) {
            erstelleFehlerlogEintrag(fehlerlog, zelle, blatt,
                "Projektnummer " + projektNummer + " unbekannt", FEHLER);
        } else {
            erstelleFehlerlogEintrag(fehlerlog, zelle, blatt,
                "Keine Projektnummer angegeben", FEHLER);
        }
    }

    public void schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
        final List<Fehlerlog> fehlerlog,
        final String zelle,
        final String blatt) {
        if (!zelle.contains("Zeile") && !zelle.contains("Spalte")) {
            erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Tag in dem Monat nicht vorhanden",
                HINWEIS);
        } else {
            erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Kein Tag angegeben", HINWEIS);
        }
    }

    public void schreibeUhrzeitIstUngueltig(final List<Fehlerlog> fehlerlog,
        final String zelle,
        final String blatt) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Uhrzeit ungültig", FEHLER);
    }

    public void schreibeZeitspanneIstUngueltig(final List<Fehlerlog> fehlerlog,
        final String zelle,
        final String blatt) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Angegebene Zeitspanne ist ungültig",
            FEHLER);
    }

    public void schreibeArbeitsstaetteNichtBefuellt(final List<Fehlerlog> fehlerlog,
        final String zelle,
        final String blatt) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, blatt,
            "Kein Einsatzort bei Abwesenheit angegeben", FEHLER);
    }

    public void schreibeSummeRufbereitschaftUnterschiedlich(final List<Fehlerlog> fehlerlog,
        final BigDecimal summeImportierterStunden,
        final BigDecimal summeRufbereitschaft, final String blatt,
        final Integer sonderzeitenSummenZeileIndex) {
        erstelleFehlerlogEintrag(fehlerlog,
            excelSpaltenName(SUMME_RUFBEREITSCHAFT_INDEX + 1) + (sonderzeitenSummenZeileIndex + 1),

            blatt, "Importierte Rufbereitschaft (" + dezimalZahlenFormatter.format(
                summeImportierterStunden)
                + ") ungleich Summe in Excel Blatt \"Sonderzeiten\" ("
                + dezimalZahlenFormatter.format(summeRufbereitschaft)
                + ")",
            HINWEIS);
    }

    public void schreibeSummeSonderarbeitszeitUnterschiedlich(final List<Fehlerlog> fehlerlog,
        final BigDecimal summeImportierterStunden,
        final BigDecimal summeSonderarbeitszeit, final String blatt,
        final Integer sonderzeitenSummenZeileIndex) {
        erstelleFehlerlogEintrag(fehlerlog,
            excelSpaltenName(SONDERZEITEN_SPALTE_SUMME_SONDERARBEITSZEIT_INDEX + 1)
                + (sonderzeitenSummenZeileIndex + 1),

            blatt, "Importierte Sonderarbeitszeit ("
                + dezimalZahlenFormatter.format(
                summeImportierterStunden != null ? summeImportierterStunden : 0)
                + ") ungleich Summe in Excel Blatt \"Sonderzeiten\" ("
                + dezimalZahlenFormatter.format(
                summeSonderarbeitszeit != null ? summeSonderarbeitszeit : 0)
                + ")",
            HINWEIS);
    }

    public void schreibeSummeProjektstundenUnterschiedlich(final List<Fehlerlog> fehlerlog,
        final BigDecimal summeImportierterStunden,
        final BigDecimal summeProjektstundenInExcel, final String blatt,
        final Integer sonderzeitenSummenZeileIndex) {
        erstelleFehlerlogEintrag(fehlerlog,
            excelSpaltenName(ANW_SPALTE_ARBEITSZEIT + 1)
                + (sonderzeitenSummenZeileIndex + 1),

            blatt, "Summe importierter Projektstunde ("
                + dezimalZahlenFormatter.format(
                summeImportierterStunden != null ? summeImportierterStunden : 0)
                + ") ungleich Summe der Projektstunde in Excel Blatt \"ANW\" ("
                + dezimalZahlenFormatter.format(
                summeProjektstundenInExcel != null ? summeProjektstundenInExcel : 0)
                + ")",
            HINWEIS);
    }

    public void schreibeSarUnterschiedlich(final List<Fehlerlog> fehlerlog,
        final BigDecimal summeImportiert,
        final BigDecimal summeSAR, final String blatt, final Integer index, final String typ) {
        erstelleFehlerlogEintrag(fehlerlog,
            "Spalte " + excelSpaltenName(index + 1),

            blatt, "Importierte Spesen für " + typ + " ("
                + dezimalZahlenFormatter.format(summeImportiert != null ? summeImportiert : 0)
                + ") ungleich Summe in Excel Blatt \"SAR\" ("
                + dezimalZahlenFormatter.format(summeSAR != null ? summeSAR : 0)
                + ")",
            HINWEIS);
    }

    public void schreibeSummeSonderarbeitsUeberschreitetProjektstunden(
        final List<Fehlerlog> fehlerlog,
        final Projektstunde importiereSonderarbeitszeit,
        final Projektstunde projektstunden, final String blatt, final String zelle) {
        erstelleFehlerlogEintrag(fehlerlog,
            zelle,
            blatt, "Sonderarbeitszeit in Excel Blatt Sonderzeiten ("
                + dezimalZahlenFormatter
                .format(importiereSonderarbeitszeit != null
                    && importiereSonderarbeitszeit.getAnzahlStunden() != null
                    ? importiereSonderarbeitszeit.getAnzahlStunden()
                    : 0)
                + ") überschreitet Projektstunde in Excel Blatt ANW ("
                + dezimalZahlenFormatter.format(
                projektstunden != null && projektstunden.getAnzahlStunden() != null
                    ? projektstunden.getAnzahlStunden()
                    : 0)
                + ")",
            HINWEIS);
    }

    public void schreibeEingabeNichtNumerisch(final List<Fehlerlog> fehlerlog, final String zelle,
        final String blatt) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Eingabe nicht numerisch", FEHLER);
    }

    public void schreibeKeineKostenAngegeben(final List<Fehlerlog> fehlerlog, final String zelle,
        final String blatt) {
        erstelleFehlerlogEintrag(fehlerlog, zelle, blatt, "Keine Kosten angegeben", HINWEIS);
    }

    private void erstelleFehlerlogEintrag(final List<Fehlerlog> fehlerlog,
        final String zelleOderZeile,
        final String blatt, final String meldung,
        final ImportFehlerklasse fehlerklasse) {

        final Fehlerlog logEintrag = new Fehlerlog();
        logEintrag.setBlatt(blatt);
        logEintrag.setZelle(zelleOderZeile);
        logEintrag.setFehlerklasse(fehlerklasse);
        logEintrag.setFehlertext(meldung);

        fehlerlog.add(logEintrag);

    }

    public void schreibeUrlaubDarfNurInHalbenGanzenTagenAngegebenWerden(
        final List<Fehlerlog> fehlerlog, final String blatt, final String spalte,
        final BigDecimal stunden) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt,
            "Urlaub soll nur in halben/ganzen Tagen angegeben werden", HINWEIS);
    }

    public void schreibeSpalteEnthaeltProjektstundeOhneProjektangabe(
        final List<Fehlerlog> fehlerlog, final String blatt, final String spalte) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt, "Projektstunden ohne Projektangabe",
            HINWEIS);
    }

    public void schreibeUrlaubUebersteigtArbeitstag(final List<Fehlerlog> fehlerlog,
        final String blatt, final String spalte, final BigDecimal stunden) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt,
            "Urlaubsangabe übersteigt die Stunden des Arbeitstages gemäß Stellenfaktor", HINWEIS);
    }

    public void schreibeStadtUnbekannt(final List<Fehlerlog> fehlerlog, final String blatt,
        final String spalte, String stadtWarnung) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt, stadtWarnung, WARNUNG);
    }

    public void schreibeVerbindungsentgeltGrenzeUeberschritten(final List<Fehlerlog> fehlerlog,
        final String blatt, final String spalte, final BigDecimal verbindungsentgelt,
        final BigDecimal verbindungsentgeltGrenze) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt,
            "Verbindungsentgelt (" + dezimalZahlenFormatter.format(verbindungsentgelt)
                + ") übersteigt Verbindungsentgeltgrenze (" + dezimalZahlenFormatter.format(
                verbindungsentgeltGrenze) + ")", WARNUNG);
    }

    public void schreibeAngerechneteReiszeitUnterschiedlich(final List<Fehlerlog> fehlerlog,
        final String blatt, final String spalte, final BigDecimal angerechneteReisezeitIntern,
        final BigDecimal angerechneteReisezeitExcel) {
        erstelleFehlerlogEintrag(fehlerlog, spalte, blatt,
            "Berechnete angerechnete Reisezeit (" + dezimalZahlenFormatter.format(
                angerechneteReisezeitIntern.doubleValue())
                + ") ungleich angerechneter Reisezeit in Excel Blatt \"ANW\" ("
                + dezimalZahlenFormatter.format(angerechneteReisezeitExcel.doubleValue()) + ")",
            HINWEIS);
    }

}
