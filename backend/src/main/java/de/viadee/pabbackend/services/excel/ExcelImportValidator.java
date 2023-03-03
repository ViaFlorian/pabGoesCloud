package de.viadee.pabbackend.services.excel;

import static de.viadee.pabbackend.services.excel.ExcelImport.ANW_SHEET_NAME;
import static de.viadee.pabbackend.services.excel.ExcelImport.ANW_SUMMEN_TITEL_INDEX;
import static de.viadee.pabbackend.services.excel.ExcelImport.RUFBEREITSCHAFT_SPALTE_SUMME_STUNDEN;
import static de.viadee.pabbackend.services.excel.ExcelImport.RUFBEREITSCHAFT_SPALTE_TAG_BIS;
import static de.viadee.pabbackend.services.excel.ExcelImport.RUFBEREITSCHAFT_SPALTE_TAG_VON;
import static de.viadee.pabbackend.services.excel.ExcelImport.RUFBEREITSCHAFT_SPALTE_UHRZEIT_BIS;
import static de.viadee.pabbackend.services.excel.ExcelImport.RUFBEREITSCHAFT_SPALTE_UHRZEIT_VON;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SHEET_NAME;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_ARBEITSSTAETTE;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_BELEGNUMMER;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_FLUG;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_HOTEL;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_NEBENKOSTEN_BETRAG;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_OEPNV;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_PARKEN;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_PKW;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_TAXI;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_UHRZEIT_BIS;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_UHRZEIT_VON;
import static de.viadee.pabbackend.services.excel.ExcelImport.SAR_SPALTE_ZUG;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEITEN_SHEET_NAME;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEIT_SPALTE_PROJEKT;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEIT_SPALTE_TAG_VON;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEIT_SPALTE_UHRZEIT_BIS;
import static de.viadee.pabbackend.services.excel.ExcelImport.SONDERZEIT_SPALTE_UHRZEIT_VON;
import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.Fehlerlog;
import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.Stadt;
import de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus;
import de.viadee.pabbackend.services.berechnung.AngerechneteReisezeitBerechnung;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.fachobjekt.ProjektstundeService;
import de.viadee.pabbackend.util.FormatFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.springframework.stereotype.Service;

@Service
public class ExcelImportValidator {

  private final ProjektService projektService;

  private final ParameterService parameterService;

  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;

  private final KonstantenService konstantenService;

  private final ExcelImportLogWriter excelImportLogWriter;

  private final AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung;

  private final DecimalFormat dezimalZahlenFormatter =
      FormatFactory.deutschesDezimalzahlenFormatMitZweiNachkommastellen();

  public ExcelImportValidator(
      ProjektService projektService,
      ParameterService parameterService,
      ProjektstundeService projektstundenService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      KonstantenService konstantenService,
      ExcelImportLogWriter excelImportLogWriter,
      AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung) {
    this.projektService = projektService;
    this.parameterService = parameterService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.konstantenService = konstantenService;
    this.excelImportLogWriter = excelImportLogWriter;
    this.angerechneteReisezeitBerechnung = angerechneteReisezeitBerechnung;
  }

  public boolean pruefeSonderarbeitszeitZeile(
      final List<Fehlerlog> fehlerlog,
      final Map<CellValue, CellAddress> zelleExcelAdresseBlattSonderzeiten,
      final List<CellValue> zeile,
      final LocalDate abrechnungsmonat,
      final List<List<CellValue>> sonderzeitenBlatt) {
    boolean keineFehler = true;

    if (bereinigeZeileUmNullUndLeereWerte(zeile).size() > 0) {

      final CellValue projektZelle = zeile.get(SONDERZEIT_SPALTE_PROJEKT);
      final CellValue tagVonZelle = zeile.get(SONDERZEIT_SPALTE_TAG_VON);
      final CellValue uhrzeitVonZelle = zeile.get(SONDERZEIT_SPALTE_UHRZEIT_VON);
      final CellValue uhrzeitBisZelle = zeile.get(SONDERZEIT_SPALTE_UHRZEIT_BIS);

      projektZelle.getStringValue();

      if (tagVonZelle == null) {
        keineFehler = false;
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null && tagVonZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            tagVonZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitVonZelle == null) {
        keineFehler = false;
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitVonZelle != null && uhrzeitVonZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            uhrzeitVonZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }

      if (uhrzeitVonZelle != null) {
        final String[] stundeMinuteVon =
            dezimalZahlenFormatter.format(uhrzeitVonZelle.getNumberValue()).split(",");

        if (berechneUhrzeit(stundeMinuteVon) == null) {
          keineFehler = false;
          final String zellenAdresse =
              uhrzeitVonZelle != null
                  ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString()
                  : "";
          excelImportLogWriter.schreibeUhrzeitIstUngueltig(
              fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
        }
      }

      if (uhrzeitBisZelle == null) {
        keineFehler = false;
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitBisZelle != null && uhrzeitBisZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            uhrzeitBisZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }

      if (uhrzeitBisZelle != null) {

        final String[] stundeMinuteBis =
            dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

        if (berechneUhrzeit(stundeMinuteBis) == null) {
          keineFehler = false;
          final String zellenAdresse =
              uhrzeitBisZelle != null
                  ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString()
                  : "";
          excelImportLogWriter.schreibeUhrzeitIstUngueltig(
              fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
        }
      }

      if (tagVonZelle != null && tagVonZelle.getNumberValue() == 0.0d) {
        keineFehler = false;
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog,
            zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString(),
            SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitVonZelle != null
          && uhrzeitBisZelle != null
          && uhrzeitVonZelle.getNumberValue() > uhrzeitBisZelle.getNumberValue()) {
        keineFehler = false;
        final List<String> betroffeneFelder = new ArrayList<>();
        betroffeneFelder.add(
            zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString());
        betroffeneFelder.add(
            zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString());
        Collections.sort(betroffeneFelder);
        excelImportLogWriter.schreibeZeitspanneIstUngueltig(
            fehlerlog, betroffeneFelder.toString(), SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null && tagVonZelle.getNumberValue() > 0.0d) {
        final LocalDate tagVonDatum =
            abrechnungsmonat.plusDays((long) (tagVonZelle.getNumberValue() - 1.0d));
        boolean datumGehoertInDenMonat = true;
        if (tagVonDatum.getYear() == abrechnungsmonat.getYear()
            && tagVonDatum.getMonthValue() != abrechnungsmonat.getMonthValue()) {
          datumGehoertInDenMonat = false;
        } else if (tagVonDatum.getYear() != abrechnungsmonat.getYear()) {
          datumGehoertInDenMonat = false;
        }
        if (!datumGehoertInDenMonat) {
          keineFehler = false;
          excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
              fehlerlog,
              zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString(),
              SONDERZEITEN_SHEET_NAME);
        }
      }
    }

    return keineFehler;
  }

  public boolean pruefeRufbereitschaftZeile(
      final List<Fehlerlog> fehlerlog,
      final Map<CellValue, CellAddress> zelleExcelAdresseBlattSonderzeiten,
      final List<CellValue> zeile,
      final LocalDate abrechnungsmonat,
      final List<List<CellValue>> sonderzeitenBlatt) {
    boolean keineFehler = true;

    if (bereinigeZeileUmNullUndLeereWerte(zeile).size() > 0) {

      final CellValue tagVonZelle = zeile.get(RUFBEREITSCHAFT_SPALTE_TAG_VON);
      final CellValue uhrzeitVonZelle = zeile.get(RUFBEREITSCHAFT_SPALTE_UHRZEIT_VON);
      final CellValue tagBisZelle = zeile.get(RUFBEREITSCHAFT_SPALTE_TAG_BIS);
      final CellValue uhrzeitBisZelle = zeile.get(RUFBEREITSCHAFT_SPALTE_UHRZEIT_BIS);
      zeile.get(RUFBEREITSCHAFT_SPALTE_SUMME_STUNDEN);

      if (tagVonZelle == null) {
        keineFehler = false;
        zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString();
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null && tagVonZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            tagVonZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }
      if (tagBisZelle == null) {
        keineFehler = false;
        zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString();
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (tagBisZelle != null && tagBisZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            tagBisZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitVonZelle == null) {
        keineFehler = false;
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitVonZelle != null && uhrzeitVonZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            uhrzeitVonZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }

      if (uhrzeitVonZelle != null) {
        final String[] stundeMinuteVon =
            dezimalZahlenFormatter.format(uhrzeitVonZelle.getNumberValue()).split(",");

        if (berechneUhrzeit(stundeMinuteVon) == null) {
          keineFehler = false;
          final String zellenAdresse =
              uhrzeitVonZelle != null
                  ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString()
                  : "";
          excelImportLogWriter.schreibeUhrzeitIstUngueltig(
              fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
        }
      }

      if (uhrzeitBisZelle == null) {
        keineFehler = false;
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1), SONDERZEITEN_SHEET_NAME);
      }
      if (uhrzeitBisZelle != null && uhrzeitBisZelle.getStringValue() != null) {
        keineFehler = false;
        final String zellenAdresse =
            uhrzeitBisZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeUhrzeitIstUngueltig(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }

      if (uhrzeitBisZelle != null) {
        final String[] stundeMinuteBis =
            dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

        if (berechneUhrzeit(stundeMinuteBis) == null) {
          keineFehler = false;
          final String zellenAdresse =
              uhrzeitBisZelle != null
                  ? zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString()
                  : "";
          excelImportLogWriter.schreibeUhrzeitIstUngueltig(
              fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
        }
      }

      if (tagVonZelle != null && tagVonZelle.getNumberValue() == 0.0d) {
        keineFehler = false;
        final String zellenAdresse =
            tagVonZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }
      if (tagBisZelle != null && tagBisZelle.getNumberValue() == 0.0d) {
        keineFehler = false;
        final String zellenAdresse =
            tagBisZelle != null
                ? zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString()
                : "";
        excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
            fehlerlog, zellenAdresse, SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null
          && tagBisZelle != null
          && tagVonZelle.getNumberValue() > tagBisZelle.getNumberValue()) {
        keineFehler = false;
        final List<String> betroffeneFelder = new ArrayList<>();
        betroffeneFelder.add(zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString());
        betroffeneFelder.add(zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString());
        Collections.sort(betroffeneFelder);
        excelImportLogWriter.schreibeZeitspanneIstUngueltig(
            fehlerlog, betroffeneFelder.toString(), SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null
          && tagBisZelle != null
          && uhrzeitVonZelle != null
          && uhrzeitBisZelle != null
          && tagVonZelle.getNumberValue() == tagBisZelle.getNumberValue()
          && uhrzeitVonZelle.getNumberValue() > uhrzeitBisZelle.getNumberValue()) {
        keineFehler = false;
        final List<String> betroffeneFelder = new ArrayList<>();
        betroffeneFelder.add(zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString());
        betroffeneFelder.add(zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString());
        betroffeneFelder.add(
            zelleExcelAdresseBlattSonderzeiten.get(uhrzeitVonZelle).formatAsString());
        betroffeneFelder.add(
            zelleExcelAdresseBlattSonderzeiten.get(uhrzeitBisZelle).formatAsString());
        Collections.sort(betroffeneFelder);
        excelImportLogWriter.schreibeZeitspanneIstUngueltig(
            fehlerlog, betroffeneFelder.toString(), SONDERZEITEN_SHEET_NAME);
      }
      if (tagVonZelle != null && tagVonZelle.getNumberValue() > 0.0d) {
        final LocalDate tagVonDatum =
            abrechnungsmonat.plusDays((long) (tagVonZelle.getNumberValue() - 1.0d));
        boolean datumGehoertInDenMonat = true;
        if (tagVonDatum.getYear() == abrechnungsmonat.getYear()
            && tagVonDatum.getMonthValue() != abrechnungsmonat.getMonthValue()) {
          datumGehoertInDenMonat = false;
        } else if (tagVonDatum.getYear() != abrechnungsmonat.getYear()) {
          datumGehoertInDenMonat = false;
        }
        if (!datumGehoertInDenMonat) {
          keineFehler = false;
          excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
              fehlerlog,
              zelleExcelAdresseBlattSonderzeiten.get(tagVonZelle).formatAsString(),
              SONDERZEITEN_SHEET_NAME);
        }
      }

      if (tagBisZelle != null && tagBisZelle.getNumberValue() > 0.0d) {
        final LocalDate tagBisDatum =
            abrechnungsmonat.plusDays((long) (tagBisZelle.getNumberValue() - 1.0d));
        boolean datumGehoertInDenMonat = true;
        if (tagBisDatum.getYear() == abrechnungsmonat.getYear()
            && tagBisDatum.getMonthValue() != abrechnungsmonat.getMonthValue()) {
          datumGehoertInDenMonat = false;
        } else if (tagBisDatum.getYear() != abrechnungsmonat.getYear()) {
          datumGehoertInDenMonat = false;
        }
        if (!datumGehoertInDenMonat) {
          keineFehler = false;
          excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
              fehlerlog,
              zelleExcelAdresseBlattSonderzeiten.get(tagBisZelle).formatAsString(),
              SONDERZEITEN_SHEET_NAME);
        }
      }
    }

    return keineFehler;
  }

  public void pruefeRufbereitschaftSumme(
      final List<Fehlerlog> fehlerlog,
      final List<Projektstunde> importierteRufbereitschaft,
      final BigDecimal summeRufbereitschaft,
      final Integer sonderzeitenSummenZeileIndex) {
    BigDecimal summeImportierterStunden = ZERO;
    summeImportierterStunden =
        importierteRufbereitschaft.stream()
            .filter(stunden -> stunden.getAnzahlStunden() != null)
            .map(stunden -> stunden.getAnzahlStunden())
            .reduce(ZERO, BigDecimal::add);
    if (summeImportierterStunden
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(summeRufbereitschaft.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSummeRufbereitschaftUnterschiedlich(
          fehlerlog,
          summeImportierterStunden,
          summeRufbereitschaft,
          SONDERZEITEN_SHEET_NAME,
          sonderzeitenSummenZeileIndex);
    }
  }

  public void pruefeSonderarbeitszeitSumme(
      final List<Fehlerlog> fehlerlog,
      final List<Projektstunde> importierteSonderarbeitszeit,
      final BigDecimal summeSonderarbeitszeit,
      final Integer sonderzeitenSummenZeileIndex) {
    BigDecimal summeImportierterStunden = ZERO;
    summeImportierterStunden =
        importierteSonderarbeitszeit.stream()
            .filter(stunden -> stunden.getAnzahlStunden() != null)
            .map(stunden -> stunden.getAnzahlStunden())
            .reduce(ZERO, BigDecimal::add);
    if (summeImportierterStunden
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(summeSonderarbeitszeit.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSummeSonderarbeitszeitUnterschiedlich(
          fehlerlog,
          summeImportierterStunden,
          summeSonderarbeitszeit,
          SONDERZEITEN_SHEET_NAME,
          sonderzeitenSummenZeileIndex);
    }
  }

  public void pruefeSARSummen(
      final List<Fehlerlog> fehlerlog,
      final List<Beleg> importierteBelege,
      final Integer sarSummeZeileIndex,
      final List<List<CellValue>> sarBlatt) {

    final BigDecimal importierteHotelSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Hotel"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarHotelSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_HOTEL) != null) {
      sarHotelSumme =
          BigDecimal.valueOf(
                  sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_HOTEL).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }
    if (importierteHotelSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarHotelSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog,
          importierteHotelSumme,
          sarHotelSumme,
          SAR_SHEET_NAME,
          SAR_SPALTE_HOTEL,
          "Hotel");
    }

    final BigDecimal importiertePKWSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("PKW"))
            .map(Beleg::getKilometer)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarPKWSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_PKW) != null) {
      sarPKWSumme =
          BigDecimal.valueOf(sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_PKW).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importiertePKWSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarPKWSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog, importiertePKWSumme, sarPKWSumme, SAR_SHEET_NAME, SAR_SPALTE_PKW, "PKW");
    }

    final BigDecimal importierteBahnSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Bahn"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarBahnSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_ZUG) != null) {
      sarBahnSumme =
          BigDecimal.valueOf(sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_ZUG).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importierteBahnSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarBahnSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog, importierteBahnSumme, sarBahnSumme, SAR_SHEET_NAME, SAR_SPALTE_ZUG, "Bahn");
    }

    final BigDecimal importierteFlugSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Flug"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarFlugSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_FLUG) != null) {
      sarFlugSumme =
          BigDecimal.valueOf(sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_FLUG).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importierteFlugSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarFlugSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog, importierteFlugSumme, sarFlugSumme, SAR_SHEET_NAME, SAR_SPALTE_FLUG, "Flug");
    }

    final BigDecimal importierteTaxiSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Taxi"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarTaxiSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_TAXI) != null) {
      sarTaxiSumme =
          BigDecimal.valueOf(sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_TAXI).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importierteTaxiSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarTaxiSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog, importierteTaxiSumme, sarTaxiSumme, SAR_SHEET_NAME, SAR_SPALTE_TAXI, "Taxi");
    }

    final BigDecimal importierteOepnvSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("ÖPNV"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarOepnvSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_OEPNV) != null) {
      sarOepnvSumme =
          BigDecimal.valueOf(
                  sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_OEPNV).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importierteOepnvSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarOepnvSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog,
          importierteOepnvSumme,
          sarOepnvSumme,
          SAR_SHEET_NAME,
          SAR_SPALTE_OEPNV,
          "ÖPNV");
    }

    final BigDecimal importierteParkenSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Parken"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarParkenSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_PARKEN) != null) {
      sarParkenSumme =
          BigDecimal.valueOf(
                  sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_PARKEN).getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    if (importierteParkenSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarParkenSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog,
          importierteParkenSumme,
          sarParkenSumme,
          SAR_SHEET_NAME,
          SAR_SPALTE_PARKEN,
          "Parken");
    }

    final BigDecimal importierteNebenkostenSumme =
        importierteBelege.stream()
            .filter(
                beleg ->
                    konstantenService
                        .belegartByID(beleg.getBelegartId())
                        .getTextKurz()
                        .equals("Sonstiges"))
            .map(Beleg::getBetrag)
            .reduce(ZERO, BigDecimal::add);

    BigDecimal sarNebenkostenSumme = ZERO;
    if (sarBlatt.get(sarSummeZeileIndex) != null
        && sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_NEBENKOSTEN_BETRAG) != null) {
      sarNebenkostenSumme =
          BigDecimal.valueOf(
              sarBlatt.get(sarSummeZeileIndex).get(SAR_SPALTE_NEBENKOSTEN_BETRAG).getNumberValue());
    }

    if (importierteNebenkostenSumme
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(sarNebenkostenSumme.setScale(2, RoundingMode.HALF_UP))
        != 0) {
      excelImportLogWriter.schreibeSarUnterschiedlich(
          fehlerlog,
          importierteNebenkostenSumme,
          sarNebenkostenSumme,
          SAR_SHEET_NAME,
          SAR_SPALTE_NEBENKOSTEN_BETRAG,
          "Nebenkosten");
    }
  }

  public boolean sarZeilelIstFehlerfrei(
      final List<CellValue> zeile,
      final List<Fehlerlog> fehlerlog,
      final List<List<CellValue>> sarBlatt) {

    boolean keineFehler = true;

    final CellValue tagZelle = zeile.get(ExcelImport.SAR_SPALTE_TAG);

    if (tagZelle != null && tagZelle.getNumberValue() != 0.0d) {

      // Wurden Eingaben getätigt, gibt es mehr als 2 Werte in der Liste der Zellen
      if (bereinigeZeileUmNullUndLeereWerte(zeile).size() > 2) {

        final CellValue arbeitstaetteZelle = zeile.get(SAR_SPALTE_ARBEITSSTAETTE);
        final CellValue uhrzeitVonZelle = zeile.get(SAR_SPALTE_UHRZEIT_VON);
        final CellValue uhrzeitBisZelle = zeile.get(SAR_SPALTE_UHRZEIT_BIS);
        final CellValue hotelZelle = zeile.get(SAR_SPALTE_HOTEL);
        final CellValue pkwZelle = zeile.get(SAR_SPALTE_PKW);
        final CellValue zugZelle = zeile.get(SAR_SPALTE_ZUG);
        final CellValue flugZelle = zeile.get(SAR_SPALTE_FLUG);
        final CellValue taxiZelle = zeile.get(SAR_SPALTE_TAXI);
        final CellValue oepnvZelle = zeile.get(SAR_SPALTE_OEPNV);
        final CellValue parkenZelle = zeile.get(SAR_SPALTE_PARKEN);
        final CellValue nebenkostenBetragZelle = zeile.get(SAR_SPALTE_NEBENKOSTEN_BETRAG);
        zeile.get(SAR_SPALTE_BELEGNUMMER);

        if ((uhrzeitBisZelle == null && uhrzeitVonZelle != null)
            || (uhrzeitVonZelle != null && uhrzeitVonZelle.getStringValue() != null)
            || (uhrzeitBisZelle != null && uhrzeitBisZelle.getStringValue() != null)) {

          keineFehler = false;

          excelImportLogWriter.schreibeUhrzeitIstUngueltig(
              fehlerlog, "Zeile " + (sarBlatt.indexOf(zeile) + 1), SAR_SHEET_NAME);
        }

        if (uhrzeitVonZelle != null) {
          final String[] stundeMinuteVon =
              dezimalZahlenFormatter.format(uhrzeitVonZelle.getNumberValue()).split(",");

          if (berechneUhrzeit(stundeMinuteVon) == null) {
            keineFehler = false;

            excelImportLogWriter.schreibeUhrzeitIstUngueltig(
                fehlerlog, "Zeile " + (sarBlatt.indexOf(zeile) + 1), SAR_SHEET_NAME);
          }
        }

        if (uhrzeitBisZelle != null) {
          final String[] stundeMinuteBis =
              dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

          if (berechneUhrzeit(stundeMinuteBis) == null) {
            keineFehler = false;

            excelImportLogWriter.schreibeUhrzeitIstUngueltig(
                fehlerlog, "Zeile " + (sarBlatt.indexOf(zeile) + 1), SAR_SHEET_NAME);
          }
        }

        if (arbeitstaetteZelle == null
            && (uhrzeitVonZelle != null
            || uhrzeitBisZelle != null
            || pkwZelle != null
            || zugZelle != null
            || flugZelle != null
            || oepnvZelle != null
            || parkenZelle != null
            || hotelZelle != null
            || taxiZelle != null)) {
          keineFehler = false;
          excelImportLogWriter.schreibeArbeitsstaetteNichtBefuellt(
              fehlerlog, "Zeile " + (sarBlatt.indexOf(zeile) + 1), SAR_SHEET_NAME);
        }

        if (hotelZelle == null
            && pkwZelle == null
            && zugZelle == null
            && flugZelle == null
            && taxiZelle == null
            && oepnvZelle == null
            && parkenZelle == null
            && nebenkostenBetragZelle == null) {
          excelImportLogWriter.schreibeKeineKostenAngegeben(
              fehlerlog, "Zeile " + (sarBlatt.indexOf(zeile) + 1), SAR_SHEET_NAME);
        }
      }
    }
    return keineFehler;
  }

  public List<CellValue> bereinigeZeileUmNullUndLeereWerte(final List<CellValue> zeile) {
    final List<CellValue> bereinigteListe = new ArrayList<>();
    bereinigteListe.addAll(zeile);
    bereinigteListe.removeIf(Objects::isNull);
    bereinigteListe.removeIf(
        wert -> wert.getStringValue() != null && wert.getStringValue().equals(""));
    return bereinigteListe;
  }

  public boolean istVierstelligeZahl(final String vierstelligerString) {
    return vierstelligerString.matches("^[0-9]{4}$");
  }

  public boolean istGueltigesProjekt(
      final String projektnummerGelesen,
      final List<Fehlerlog> fehlerlog,
      final List<List<CellValue>> blatt,
      final List<CellValue> zeile,
      final String blattName) {
    boolean keinFehler = true;
    if (projektnummerGelesen != null && istVierstelligeZahl(projektnummerGelesen)) {
      final Projekt projekt = projektService.projektByProjektnummer(projektnummerGelesen);
      if (projekt == null) {
        keinFehler = false;
        excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
            fehlerlog, blattName, "Zeile " + (blatt.indexOf(zeile) + 1), projektnummerGelesen);
      }
    } else {
      keinFehler = false;
      excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
          fehlerlog, blattName, "Zeile " + (blatt.indexOf(zeile) + 1), projektnummerGelesen);
    }
    return keinFehler;
  }

  public void pruefeProjektstundenSumme(
      final List<Fehlerlog> fehlerlog,
      final List<List<CellValue>> anwBlatt,
      final Integer arbeitsnachweisSummenZeileIndex,
      final List<Projektstunde> projektstunden,
      final Integer projektstundenSummenZeileIndex,
      final BigDecimal angerechneteReisezeit) {

    BigDecimal summeStundenInAnwBlatt = ZERO;

    if (anwBlatt.get(arbeitsnachweisSummenZeileIndex) != null
        && anwBlatt.get(arbeitsnachweisSummenZeileIndex).get(ANW_SUMMEN_TITEL_INDEX + 3) != null) {
      summeStundenInAnwBlatt =
          BigDecimal.valueOf(
                  anwBlatt
                      .get(arbeitsnachweisSummenZeileIndex)
                      .get(ANW_SUMMEN_TITEL_INDEX + 3)
                      .getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    final BigDecimal summeImportierterProjektstunden =
        projektstunden.stream()
            .map(stunden -> stunden.getAnzahlStunden())
            .reduce(ZERO, BigDecimal::add);

    final BigDecimal summeStundenInAnwBlattOhneAngerechneteReisezeit =
        summeStundenInAnwBlatt.subtract(angerechneteReisezeit);

    if (summeImportierterProjektstunden
        .setScale(2, RoundingMode.HALF_UP)
        .compareTo(
            summeStundenInAnwBlattOhneAngerechneteReisezeit.setScale(2, RoundingMode.HALF_UP))
        != 0) {

      excelImportLogWriter.schreibeSummeProjektstundenUnterschiedlich(
          fehlerlog,
          summeImportierterProjektstunden,
          summeStundenInAnwBlattOhneAngerechneteReisezeit,
          ANW_SHEET_NAME,
          projektstundenSummenZeileIndex);
    }
  }

  public void pruefeObSonderarbeitszeitDieProjektstundenUeberschreitet(
      final List<Fehlerlog> fehlerlog,
      final Projektstunde importiereSonderarbeitszeit,
      final List<Projektstunde> importierteProjektstunden,
      final CellValue stundenZelle,
      final Map<CellValue, CellAddress> zelleExcelAdresseBlattSonderzeiten) {

    Projektstunde projektstunde = null;
    for (final Projektstunde stunden : importierteProjektstunden) {
      if (stunden.getTagVon().equals(importiereSonderarbeitszeit.getTagVon())) {
        projektstunde = stunden;
        break;
      }
    }
    if (projektstunde == null
        || importiereSonderarbeitszeit
        .getAnzahlStunden()
        .compareTo(projektstunde.getAnzahlStunden())
        == 1) {
      excelImportLogWriter.schreibeSummeSonderarbeitsUeberschreitetProjektstunden(
          fehlerlog,
          importiereSonderarbeitszeit,
          projektstunde,
          SONDERZEITEN_SHEET_NAME,
          zelleExcelAdresseBlattSonderzeiten.get(stundenZelle).formatAsString());
    }
  }

  private LocalTime berechneUhrzeit(final String[] stundeMinute) {
    LocalTime uhrzeit = null;
    final Integer stunde = Integer.parseInt(stundeMinute[0]);
    final Integer minute = Integer.parseInt(stundeMinute[1]);
    if (stunde.equals(24) && minute.equals(0)) {
      uhrzeit = LocalTime.MAX;
    } else {
      try {
        uhrzeit = LocalTime.of(stunde, minute);
      } catch (final DateTimeException dte) {
      }
    }
    return uhrzeit;
  }

  public void pruefeObImportMitArbeitsnachweisStatusFortgesetztWerdenDarf(
      final Arbeitsnachweis arbeitsnachweis, final List<Fehlerlog> fehlerlog) {
    if (arbeitsnachweis.getStatusId() != null) {
      if (!arbeitsnachweis.getStatusId()
          .equals(ArbeitsnachweisBearbeitungsstatus.ERFASST.value())) {
        excelImportLogWriter.schreibeKeinImportAufgrundVonArbeitsnachweisStatus(
            fehlerlog, arbeitsnachweis);
      }
    }
  }

  public void pruefeObUrlaubProjektstundenKonsistentSindUndSchreibeGgfMeldung(
      final List<Fehlerlog> fehlerlog,
      final Arbeitsnachweis arbeitsnachweis,
      final String blattName,
      final Projektstunde projektstunden,
      final String spalte) {

    if (urlaubUebersteigtArbeitstag(projektstunden.getAnzahlStunden(), arbeitsnachweis)) {
      excelImportLogWriter.schreibeUrlaubUebersteigtArbeitstag(
          fehlerlog, blattName, spalte, projektstunden.getAnzahlStunden());
    } else {
      if (!urlaubAlsHalberOderGanzerTagAngegeben(
          projektstunden.getAnzahlStunden(), arbeitsnachweis)) {
        excelImportLogWriter.schreibeUrlaubDarfNurInHalbenGanzenTagenAngegebenWerden(
            fehlerlog, blattName, spalte, projektstunden.getAnzahlStunden());
      }
    }
  }

  private boolean urlaubAlsHalberOderGanzerTagAngegeben(
      final BigDecimal stunden, final Arbeitsnachweis arbeitsnachweis) {
    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat());
    BigDecimal angabeUrlaubInStunden = stunden;
    BigDecimal arbeitstagInStundenGemaessStellenfaktor =
        new BigDecimal(8).multiply(mitarbeiterStellenfaktor.getStellenfaktor());
    boolean tagIstHalberOderGanzerTag =
        angabeUrlaubInStunden
            .divide(arbeitstagInStundenGemaessStellenfaktor, 2, RoundingMode.HALF_UP)
            .remainder(new BigDecimal("0.5"))
            .compareTo(ZERO)
            == 0;

    return tagIstHalberOderGanzerTag;
  }

  private boolean urlaubUebersteigtArbeitstag(
      final BigDecimal stunden, final Arbeitsnachweis arbeitsnachweis) {
    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat());
    BigDecimal arbeitstagGemaessStellenfaktor = (new BigDecimal(8)).multiply(
        mitarbeiterStellenfaktor.getStellenfaktor());

    boolean urlaubUebersteigtArbeitstag = stunden.compareTo(arbeitstagGemaessStellenfaktor) > 0;

    return urlaubUebersteigtArbeitstag;
  }

  public void pruefeObProjektstundenOhneProjektangabeAngegebenWurden(
      final List<Fehlerlog> fehlerlog,
      String blattName,
      final Integer tagIndex,
      final Integer angerechneteReisezeitSpalte,
      final List<CellValue> tagZeile,
      final Map<Integer, String> projekte) {
    for (int spaltenIndex = 7; spaltenIndex < tagZeile.size(); spaltenIndex++) {
      if (spaltenIndex != angerechneteReisezeitSpalte.intValue()
          && tagZeile.get(spaltenIndex) != null) {
        if (!projekte.containsKey(spaltenIndex)
            && !(spaltenIndex > angerechneteReisezeitSpalte.intValue())) {
          excelImportLogWriter.schreibeSpalteEnthaeltProjektstundeOhneProjektangabe(
              fehlerlog, blattName, "Spalte " + ExcelImport.excelSpaltenName(spaltenIndex + 1));
        }
      }
    }
  }

  public void pruefeObReisezeitenOhneProjektangabeAngegebenWurden(
      final List<Fehlerlog> fehlerlog,
      final Map<Integer, LocalDate> tage,
      final Map<Integer, String> projekte,
      final Integer anwAngerechneteReisezeitSpalteIndex,
      final Integer endeMarkerIndex,
      final List<List<CellValue>> anwBlatt) {

    for (Integer tagIndex : tage.keySet()) {
      for (int i = anwAngerechneteReisezeitSpalteIndex + 1; i <= endeMarkerIndex; i++) {
        if (anwBlatt.get(tagIndex).get(i) != null
            && anwBlatt.get(tagIndex).get(i).getNumberValue() != 0.0d) {
          if (projekte.get(i) == null) {
            excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
                fehlerlog, ANW_SHEET_NAME, "Spalte " + ExcelImport.excelSpaltenName(i + 1), null);
          }
        }
      }
    }
  }

  public void schreibeWarnungFallsArbeitsstaetteUnbekannt(
      final List<Fehlerlog> fehlerlog,
      final List<List<CellValue>> blatt,
      final List<CellValue> zeile,
      final CellValue arbeitstaetteZelle) {

    Stadt stadt = konstantenService.stadtByName(arbeitstaetteZelle.getStringValue().trim());

    if (stadt == null) {

      List<Stadt> alternativeStaedte =
          konstantenService.stadtLikeName(arbeitstaetteZelle.getStringValue().trim());
      String warnungUeberNichtBekanntenOrt =
          "Einsatzort \"" + arbeitstaetteZelle.getStringValue().trim() + "\" unbekannt";
      if (alternativeStaedte != null && !alternativeStaedte.isEmpty()) {
        warnungUeberNichtBekanntenOrt = warnungUeberNichtBekanntenOrt + "; ähnlich: ";

        for (Stadt alternativeStadt : alternativeStaedte) {
          String aufzulistendeStadt =
              alternativeStaedte.indexOf(alternativeStadt) == 0
                  ? alternativeStadt.getName()
                  : ", " + alternativeStadt.getName();
          warnungUeberNichtBekanntenOrt = warnungUeberNichtBekanntenOrt + aufzulistendeStadt;
        }
      }

      excelImportLogWriter.schreibeStadtUnbekannt(
          fehlerlog,
          SAR_SHEET_NAME,
          "Zeile " + (blatt.indexOf(zeile) + 1),
          warnungUeberNichtBekanntenOrt);
    }
  }

  public void schreibeWarnungFallsVerbindungsentgeldUeberErstattungsgrenze(
      final List<Fehlerlog> fehlerlog,
      final List<List<CellValue>> blatt,
      final List<CellValue> zeile,
      final BigDecimal verbindungsentgelt) {

    BigDecimal verbindungsentgeltGrenze =
        parameterService.valueByKey("verbindungsentgeltGrenze") == null
            ? ZERO
            : new BigDecimal(parameterService.valueByKey("verbindungsentgeltGrenze"));
    if (verbindungsentgelt.compareTo(verbindungsentgeltGrenze) > 0) {
      excelImportLogWriter.schreibeVerbindungsentgeltGrenzeUeberschritten(
          fehlerlog,
          SAR_SHEET_NAME,
          "Zeile " + (blatt.indexOf(zeile) + 1),
          verbindungsentgelt,
          verbindungsentgeltGrenze);
    }
  }

  public void pruefeObAngerechneteReisezeitExcelMitInternAngerechneterReisezeitUebereinstimmt(
      final List<Fehlerlog> fehlerlog,
      final List<Projektstunde> importierteProjektstunden,
      final List<Projektstunde> summierteReisezeitJeTagUndProjekt,
      final BigDecimal angerechneteReisezeit,
      final Integer angerechneteReisezeitSpalteIndex,
      final Integer arbeitsnachweisSummenZeileIndex) {

    try {
      List<Projektstunde> angerechneteReisezeitImportiert =
          angerechneteReisezeitBerechnung.berechneAngerechneteReisezeit(
              summierteReisezeitJeTagUndProjekt);
      BigDecimal summeImportierteAngerechneteReisezeit =
          angerechneteReisezeitImportiert.stream()
              .filter(stunden -> stunden.getAnzahlStunden() != null)
              .map(stunden -> stunden.getAnzahlStunden())
              .reduce(ZERO, BigDecimal::add);
      if (angerechneteReisezeit.compareTo(summeImportierteAngerechneteReisezeit) != 0) {
        excelImportLogWriter.schreibeAngerechneteReiszeitUnterschiedlich(
            fehlerlog,
            ANW_SHEET_NAME,
            ExcelImport.excelSpaltenName(angerechneteReisezeitSpalteIndex + 1)
                + (arbeitsnachweisSummenZeileIndex + 1),
            summeImportierteAngerechneteReisezeit,
            angerechneteReisezeit);
      }
    } catch (RuntimeException re) {
      excelImportLogWriter.schreibeFehlerInDerArbeitsnachweisVerarbeitung(
          fehlerlog, ANW_SHEET_NAME, re.getMessage());
    }
  }

  public void pruefeTemplateVersion(
      final List<Fehlerlog> fehlerlog,
      final String arbeitsnachweisTemplateSoll,
      final boolean validierungsModus,
      final XSSFWorkbook workbook) {
    POIXMLProperties.CustomProperties workbookProperties =
        workbook.getProperties().getCustomProperties();
    CTProperty propertyAnwVersion = workbookProperties.getProperty("ANW Version");
    String arbeitsnachweisTemplateIst =
        propertyAnwVersion == null ? "" : propertyAnwVersion.getLpwstr();
    if (!arbeitsnachweisTemplateIst.equals(arbeitsnachweisTemplateSoll)) {
      excelImportLogWriter.schreibeArbeitsnachweisTemplateVersionUnterschiedlich(
          fehlerlog, arbeitsnachweisTemplateIst, arbeitsnachweisTemplateSoll, validierungsModus);
    }
  }
}
