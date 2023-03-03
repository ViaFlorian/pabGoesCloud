package de.viadee.pabbackend.services.excel;

import static de.viadee.pabbackend.enums.Belegarten.BAHN;
import static de.viadee.pabbackend.enums.Belegarten.FLUG;
import static de.viadee.pabbackend.enums.Belegarten.HOTEL;
import static de.viadee.pabbackend.enums.Belegarten.OPNV;
import static de.viadee.pabbackend.enums.Belegarten.PARKEN;
import static de.viadee.pabbackend.enums.Belegarten.PKW;
import static de.viadee.pabbackend.enums.Belegarten.SONSTIGES;
import static de.viadee.pabbackend.enums.Belegarten.TAXI;
import static de.viadee.pabbackend.enums.Belegarten.VERBINDUNGSENTGELT;
import static de.viadee.pabbackend.enums.ImportFehlerklasse.KRITISCH;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.NORMAL;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.RUFBEREITSCHAFT;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.SONDERARBEITSZEIT;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.TATSAECHLICHE_REISEZEIT;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.KEIN;
import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.ExcelImportErgebnis;
import de.viadee.pabbackend.entities.Fehlerlog;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.services.berechnung.DreiMonatsRegelBerechnung;
import de.viadee.pabbackend.services.fachobjekt.AbwesenheitService;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.DreiMonatsRegelService;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.MonatsabschlussService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.fachobjekt.ProjektstundeService;
import de.viadee.pabbackend.util.FormatFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelImport {

  public static final int RUFBEREITSCHAFT_SPALTE_TAG_VON = 1;
  public static final int RUFBEREITSCHAFT_SPALTE_UHRZEIT_VON = 2;
  public static final int RUFBEREITSCHAFT_SPALTE_TAG_BIS = 4;
  public static final int RUFBEREITSCHAFT_SPALTE_UHRZEIT_BIS = 5;
  public static final int RUFBEREITSCHAFT_SPALTE_SUMME_STUNDEN = 7;
  public static final int SONDERZEIT_SPALTE_PROJEKT = 9;
  public static final int SONDERZEIT_SPALTE_TAG_VON = 10;
  public static final int SONDERZEIT_SPALTE_UHRZEIT_VON = 11;
  public static final int SONDERZEIT_SPALTE_UHRZEIT_BIS = 12;
  public static final int SONDERZEIT_SPALTE_SUMME_STUNDEN = 15;
  public static final int SONDERZEIT_SPALTE_BEMERKUNG = 20;
  public static final int SPALTE_MIT_TAG_ANGABEN = 0;
  public static final int SPALTE_MIT_RUFBREITSCAHFT_PROJEKTANGABEN = 0;
  public static final int SPALTE_MIT_SONDERZEIT_PROJEKTANGABEN = 9;
  public static final int SAR_SPALTE_TAG = 0;
  public static final int SAR_SPALTE_ARBEITSSTAETTE = 2;
  public static final int SAR_SPALTE_PROJEKT = 3;
  public static final int SAR_SPALTE_UHRZEIT_VON = 4;
  public static final int SAR_SPALTE_UHRZEIT_BIS = 5;
  public static final int SAR_SPALTE_HOTEL = 7;
  public static final int SAR_SPALTE_PKW = 8;
  public static final int SAR_SPALTE_ZUG = 9;
  public static final int SAR_SPALTE_FLUG = 10;
  public static final int SAR_SPALTE_TAXI = 11;
  public static final int SAR_SPALTE_OEPNV = 12;
  public static final int SAR_SPALTE_PARKEN = 13;
  public static final int SAR_SPALTE_NEBENKOSTEN = 14;
  public static final int SAR_SPALTE_NEBENKOSTEN_BETRAG = 15;
  public static final int SAR_SPALTE_BELEGNUMMER = 16;
  public static final int ANW_SUMMEN_TITEL_INDEX = 2;
  public static final int ANW_SPALTE_ARBEITSZEIT = 5;
  private static final String ANGERECHN_REISEZEIT = "angerechn. Reisezeit";
  private static final int STELLENFAKTOR_ZEILE_INDEX = 1;
  private static final int NICHT_FAKTURIERFAEHIG_ZEILE_INDEX = 4;
  private static final int STELLENFAKTOR_SPALTE_INDEX = 19;
  private static final int MAX_SPALTE_SAR = 16;
  private static final int MAX_SPALTE_SONDERZEITEN = 20;
  private static final int ZEILE_MIT_PROJEKTANGABEN = 3;
  private static final int ZEILE_MIT_ENDE_INDEX = 0;
  private static final Integer SONDERZEITEN_START_ZEILE = 4;
  private static final int SONDERZEITEN_SUMMEN_TITEL_INDEX = 0;
  private static final int SMARTPHONE_TITEL_INDEX = 14;
  private static final int SAR_SUMME_TITEL_INDEX = 3;

  private static final int AUSZAHLUNG_INDEX = 5;

  private static final int ANW_BLATT_PROJEKTE_START = 7;
  private static final Integer[] PERSONALNUMMER_INDEX = new Integer[]{1, 12};
  private static final Integer[] ABRECHNUNGSMONAT_INDEX = new Integer[]{0, 12};
  public static Integer SUMME_RUFBEREITSCHAFT_INDEX = 7;
  public static Integer SONDERZEITEN_SPALTE_SUMME_SONDERARBEITSZEIT_INDEX = 15;
  protected static String ANW_SHEET_NAME = "ANW";
  protected static String SAR_SHEET_NAME = "SAR";
  protected static String SONDERZEITEN_SHEET_NAME = "Sonderzeiten";
  private final MitarbeiterService mitarbeiterService;
  private final ProjektService projektService;
  private final ProjektstundeService projektstundenService;
  private final MonatsabschlussService monatsabschlussService;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;
  private final ParameterService parameterService;
  private final KonstantenService konstantenService;
  private final AbwesenheitService abwesenheitService;
  private final DreiMonatsRegelService dreiMonatsRegelService;
  private final DreiMonatsRegelBerechnung dreiMonatsRegelBerechnung;
  private final ArbeitsnachweisService arbeitsnachweisService;
  private final ExcelImportValidator excelImportValidator;
  private final ExcelImportLogWriter excelImportLogWriter;
  private final DecimalFormat dezimalZahlenFormatter =
      FormatFactory.deutschesDezimalzahlenFormatMitZweiNachkommastellen();
  protected Map<CellValue, CellAddress> zelleExcelAdresseBlattANW = new HashMap<>();
  protected Map<CellValue, CellAddress> zelleExcelAdresseBlattSonderzeiten = new HashMap<>();
  protected Map<CellValue, CellAddress> zelleExcelAdresseBlattSAR = new HashMap<>();
  private List<Fehlerlog> fehlerlog;

  public ExcelImport(
      MitarbeiterService mitarbeiterService,
      ProjektService projektService,
      ProjektstundeService projektstundenService,
      MonatsabschlussService monatsabschlussService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      ParameterService parameterService,
      KonstantenService konstantenService,
      AbwesenheitService abwesenheitService,
      DreiMonatsRegelService dreiMonatsRegelService,
      DreiMonatsRegelBerechnung dreiMonatsRegelBerechnung,
      ArbeitsnachweisService arbeitsnachweisService,
      ExcelImportValidator excelImportValidator,
      ExcelImportLogWriter excelImportLogWriter) {
    this.mitarbeiterService = mitarbeiterService;
    this.projektService = projektService;
    this.projektstundenService = projektstundenService;
    this.monatsabschlussService = monatsabschlussService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.parameterService = parameterService;
    this.konstantenService = konstantenService;
    this.abwesenheitService = abwesenheitService;
    this.dreiMonatsRegelService = dreiMonatsRegelService;
    this.dreiMonatsRegelBerechnung = dreiMonatsRegelBerechnung;
    this.arbeitsnachweisService = arbeitsnachweisService;
    this.excelImportValidator = excelImportValidator;
    this.excelImportLogWriter = excelImportLogWriter;
  }

  public static String excelSpaltenName(int index) {
    final StringBuilder sb = new StringBuilder();
    while (index-- > 0) {
      sb.append((char) ('A' + (index % 26)));
      index /= 26;
    }
    return sb.reverse().toString();
  }

  public List<Fehlerlog> validiereArbeitsnachweis(final MultipartFile anwMultipartFile) {
    ExcelImportErgebnis excelImportErgebnis = importiereArbeitsnachweis(anwMultipartFile, null,
        true);
    if (!excelImportErgebnis.getFehlerlog().isEmpty()) {
      return excelImportErgebnis.getFehlerlog();
    }
    return null;
  }

  public synchronized ExcelImportErgebnis importiereArbeitsnachweis(
      final MultipartFile anwMultipartFile,
      final Long mitarbeiterInBearbeitungId,
      final boolean validierungsModus) {

    final ExcelImportErgebnis excelImportErgebnis = new ExcelImportErgebnis();
    final String arbeitsnachweisTemplateVersion =
        parameterService.valueByKey("ArbeitsnachweisTemplateVersion");

    fehlerlog = new ArrayList<>();

    try {

      final List<List<CellValue>> anwBlatt =
          leseExcelDateiBlattANWEin(anwMultipartFile.getInputStream(),
              arbeitsnachweisTemplateVersion,
              validierungsModus);

      final String personalnummer = personalnummer(anwBlatt);

      Mitarbeiter mitarbeiter = null;
      final LocalDate abrechnungsmonat = abrechnungsmonat(anwBlatt);
      Arbeitsnachweis arbeitsnachweis = null;
      if (validierungsModus) {
        mitarbeiter = new Mitarbeiter();
        final BigDecimal stellenfaktor =
            stellenfaktorAusArbeitsnachweis(
                anwBlatt, STELLENFAKTOR_ZEILE_INDEX, STELLENFAKTOR_SPALTE_INDEX);
        arbeitsnachweis = new Arbeitsnachweis();
        arbeitsnachweis.setMonat(abrechnungsmonat.getMonthValue());
        arbeitsnachweis.setJahr(abrechnungsmonat.getYear());
        arbeitsnachweis.setStellenfaktor(stellenfaktor);
      } else {
        mitarbeiter = leseMitarbeiterZuPersonalnummer(personalnummer, mitarbeiterInBearbeitungId);
        arbeitsnachweis = ladeArbeitsnachweis(abrechnungsmonat, mitarbeiter);
      }
      final List<List<CellValue>> sonderzeitenBlatt = leseExcelDateiBlattSonderzeitenEin(
          anwMultipartFile.getInputStream());
      final List<List<CellValue>> sarBlatt = leseExcelDateiBlattSAREin(
          anwMultipartFile.getInputStream());

      final Integer arbeitsnachweisSummenZeileIndex = findeArbeitsnachweisSummenZeile(anwBlatt);
      final Integer anwAngerechneteReisezeitSpalteIndex =
          findeAngerechneteReisezeitSpalte(anwBlatt);
      final Integer sonderzeitenSummenZeileIndex = findeSonderzeitenSummenZeile(sonderzeitenBlatt);
      final Integer endeMarkerIndex = findeEndeMarker(anwBlatt);
      final Integer sarSmartphoneZeileIndex = findeSmartphoneZeile(sarBlatt);
      final Integer sarSummenZeileIndex = findeSARSummeZeile(sarBlatt);

      if (verarbeitungBisherOhneKritischeFehler()) {

        final BigDecimal angerechneteReisezeit =
            angerechneteReisezeit(
                anwBlatt, arbeitsnachweisSummenZeileIndex, anwAngerechneteReisezeitSpalteIndex);

        final List<Projektstunde> importierteProjektstunden =
            importiereProjektstunden(
                anwBlatt,
                abrechnungsmonat,
                arbeitsnachweis,
                anwAngerechneteReisezeitSpalteIndex,
                arbeitsnachweisSummenZeileIndex,
                angerechneteReisezeit);

        final BigDecimal summeRufbereitschaft =
            summeRufbereitschaft(sonderzeitenBlatt, sonderzeitenSummenZeileIndex);

        final List<Projektstunde> importierteRufbereitschaft =
            importiereRufbereitschaft(
                sonderzeitenBlatt,
                sonderzeitenSummenZeileIndex,
                arbeitsnachweis,
                abrechnungsmonat,
                summeRufbereitschaft);

        final List<Projektstunde> importierteReisezeiten =
            importiereReisezeiten(
                anwBlatt,
                anwAngerechneteReisezeitSpalteIndex,
                arbeitsnachweisSummenZeileIndex,
                endeMarkerIndex,
                arbeitsnachweis,
                abrechnungsmonat,
                arbeitsnachweisSummenZeileIndex,
                importierteProjektstunden,
                angerechneteReisezeit);

        final BigDecimal summeSonderarbeitszeit =
            summeSonderarbeitszeit(sonderzeitenBlatt, sonderzeitenSummenZeileIndex);

        final List<Projektstunde> importierteSonderarbeitszeiten =
            importiereSonderarbeitszeiten(
                sonderzeitenBlatt,
                sonderzeitenSummenZeileIndex,
                arbeitsnachweis,
                abrechnungsmonat,
                summeSonderarbeitszeit,
                importierteProjektstunden);

        final List[] abwesenheitenUndBelege =
            importiereAbwesenheitenUndBelege(
                sarBlatt, arbeitsnachweis, sarSmartphoneZeileIndex, sarSummenZeileIndex);

        final List<Abwesenheit> importierteAbwesenheiten = abwesenheitenUndBelege[0];

        final List<Beleg> importierteBelege = abwesenheitenUndBelege[1];

        if (mitarbeiter.isIntern() && !validierungsModus) {
          ermittleDreiMonatsRegelnUndSetzeKennzeichenInAbwesenheit(
              mitarbeiter, abrechnungsmonat, arbeitsnachweis, importierteAbwesenheiten);
        }

        if (!validierungsModus) {
          arbeitsnachweis.setSollstunden(
              leseSollstunden(
                  fehlerlog, arbeitsnachweis, anwBlatt, arbeitsnachweisSummenZeileIndex));
          arbeitsnachweis.setSmartphoneEigen(
              leseSmartphoneEigen(sarBlatt, sarSmartphoneZeileIndex));
          arbeitsnachweis.setAuszahlung(leseAuszahlung(anwBlatt, arbeitsnachweisSummenZeileIndex));
        }
        excelImportErgebnis.setArbeitsnachweis(arbeitsnachweis);
        excelImportErgebnis.setImportierteProjektstunden(importierteProjektstunden);
        excelImportErgebnis.setImportierteRufbereitschaft(importierteRufbereitschaft);
        excelImportErgebnis.setImportierteSonderarbeitszeiten(importierteSonderarbeitszeiten);
        excelImportErgebnis.setImportierteBelege(importierteBelege);
        excelImportErgebnis.setImportierteReisezeiten(importierteReisezeiten);
        excelImportErgebnis.setImportierteAbwesenheiten(importierteAbwesenheiten);

        if (fehlerlog.isEmpty()) {
          excelImportLogWriter.schreibeImportFehlerfreiDurchgefuehrt(fehlerlog);
        }

        for (Fehlerlog logeintrag : fehlerlog) {
          logeintrag.setArbeitsnachweisId(arbeitsnachweis.getId());
        }
      }

    } catch (Exception e) {
      fehlerlog.clear();
      excelImportLogWriter.schreibeFehlerInDerArbeitsnachweisVerarbeitung(
          fehlerlog, "alle", e.getMessage());
    }
    excelImportErgebnis.setFehlerlog(fehlerlog.stream().distinct().collect(Collectors.toList()));

    return excelImportErgebnis;
  }

  private void ermittleDreiMonatsRegelnUndSetzeKennzeichenInAbwesenheit(
      final Mitarbeiter mitarbeiter,
      final LocalDate abrechnungsmonat,
      final Arbeitsnachweis arbeitsnachweis,
      final List<Abwesenheit> importierteAbwesenheiten) {
    final List<Abwesenheit> bereitsGespeicherteAbwesenheiten =
        abwesenheitService.dreiMonatsRegelKandidatenByMitarbeiterId(
            mitarbeiter.getId(), abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue());

    final List<Abwesenheit> gespeicherteAbwesenheitenBereinigtUmEventuellenVorherigenImport =
        bereitsGespeicherteAbwesenheiten.stream()
            .filter(
                abwesenheit ->
                    abwesenheit
                        .getTagVon()
                        .isBefore(
                            LocalDate.of(
                                abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue(), 1))).
            toList();

    final List<DreiMonatsRegel> berechneteDreiMonatsRegeln =
        dreiMonatsRegelBerechnung.berechneDreiMonatsRegeln(
            mitarbeiter,
            gespeicherteAbwesenheitenBereinigtUmEventuellenVorherigenImport,
            importierteAbwesenheiten,
            abrechnungsmonat.getYear(),
            abrechnungsmonat.getMonthValue());
    final List<DreiMonatsRegel> manuellErfassteDreiMonatsRegeln =
        dreiMonatsRegelService.manuelleDreiMonatsRegelnFuerAbrechnungsmonat(
            mitarbeiter.getId(), abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue());
    final List<DreiMonatsRegel> alleRegeln = new ArrayList<>();
    alleRegeln.addAll(berechneteDreiMonatsRegeln);
    alleRegeln.addAll(manuellErfassteDreiMonatsRegeln);
    setzeDreiMonatsRegelKennzeichen(alleRegeln, importierteAbwesenheiten);
    arbeitsnachweis.setBerechneteDreiMonatsRegeln(berechneteDreiMonatsRegeln);
  }

  private void setzeDreiMonatsRegelKennzeichen(
      final List<DreiMonatsRegel> berechneteDreiMonatsRegeln,
      final List<Abwesenheit> importierteAbwesenheiten) {
    importierteAbwesenheiten.stream()
        .forEach(
            abwesenheit -> {
              if (abwesenheitVonDreiMonatsRegelBetroffen(berechneteDreiMonatsRegeln, abwesenheit)) {
                abwesenheit.setDreiMonatsRegelAktiv(true);
              }
            });
  }

  private boolean abwesenheitVonDreiMonatsRegelBetroffen(
      final List<DreiMonatsRegel> berechneteDreiMonatsRegeln, final Abwesenheit abwesenheit) {
    return berechneteDreiMonatsRegeln.stream()
        .anyMatch(
            regel -> {
              return regel.getArbeitsstaette().equals(abwesenheit.getArbeitsstaette())
                  && regel
                  .getKundeScribeId()
                  .equals(projektService.projektById(abwesenheit.getProjektId()).getKundeId())
                  && (regel.getGueltigBis() == null
                  && (abwesenheit
                  .getTagVon()
                  .isAfter(regel.getGueltigVon().plusMonths(3).minusDays(1))));
            });
  }

  private BigDecimal leseSollstunden(
      final List<Fehlerlog> fehlerlog,
      final Arbeitsnachweis arbeitsnachweis,
      final List<List<CellValue>> anwBlatt,
      final Integer arbeitsnachweisSummenZeileIndex) {

    BigDecimal sollstunden = BigDecimal.ZERO;

    if (anwBlatt.get(arbeitsnachweisSummenZeileIndex + 1) != null
        && anwBlatt.get(arbeitsnachweisSummenZeileIndex + 1).get(ANW_SUMMEN_TITEL_INDEX + 3)
        != null) {
      sollstunden =
          BigDecimal.valueOf(
                  anwBlatt
                      .get(arbeitsnachweisSummenZeileIndex + 1)
                      .get(ANW_SUMMEN_TITEL_INDEX + 3)
                      .getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    return sollstunden;
  }

  private BigDecimal leseAuszahlung(
      final List<List<CellValue>> anwBlatt, final Integer arbeitsnachweisSummenZeileIndex) {
    BigDecimal auszahlung = ZERO;
    if (!anwBlatt.isEmpty()) {
      if (anwBlatt.get(arbeitsnachweisSummenZeileIndex + 4) != null
          && anwBlatt.get(arbeitsnachweisSummenZeileIndex + 4).get(AUSZAHLUNG_INDEX) != null) {

        auszahlung =
            BigDecimal.valueOf(anwBlatt
                    .get(arbeitsnachweisSummenZeileIndex + 4)
                    .get(AUSZAHLUNG_INDEX)
                    .getNumberValue())
                .setScale(2, RoundingMode.HALF_UP);
      }
    }

    return auszahlung;
  }

  private Boolean leseSmartphoneEigen(
      final List<List<CellValue>> sarBlatt, final Integer sarSmartphoneZeileIndex) {
    Boolean smartphoneEigen = null;
    if (!sarBlatt.isEmpty()) {
      if (sarBlatt.get(sarSmartphoneZeileIndex) != null
          && sarBlatt.get(sarSmartphoneZeileIndex).get(SMARTPHONE_TITEL_INDEX + 1) != null
          && sarBlatt.get(sarSmartphoneZeileIndex).get(SMARTPHONE_TITEL_INDEX + 1).getStringValue()
          != null) {

        if (sarBlatt
            .get(sarSmartphoneZeileIndex)
            .get(SMARTPHONE_TITEL_INDEX + 1)
            .getStringValue()
            .equalsIgnoreCase("firma")) {
          smartphoneEigen = Boolean.TRUE;
        } else if (sarBlatt
            .get(sarSmartphoneZeileIndex)
            .get(SMARTPHONE_TITEL_INDEX + 1)
            .getStringValue()
            .equalsIgnoreCase("eigen")) {
          smartphoneEigen = Boolean.FALSE;
        }
      }
    }

    return smartphoneEigen;
  }

  private Integer findeSmartphoneZeile(final List<List<CellValue>> sarBlatt) {
    Integer smartphoneZeile = Integer.MAX_VALUE;

    if (verarbeitungBisherOhneKritischeFehler()) {

      for (final List<CellValue> zeile : sarBlatt) {

        if (zeile.get(SMARTPHONE_TITEL_INDEX) != null
            && zeile.get(SMARTPHONE_TITEL_INDEX).getStringValue() != null
            && zeile.get(SMARTPHONE_TITEL_INDEX).getStringValue().equals("Smartphone:")) {
          smartphoneZeile = sarBlatt.indexOf(zeile);
        }
      }

      if (smartphoneZeile.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeSmartphoneZeileInBlattSARKonnteNichtErmitteltWerden(fehlerlog);
      }
    }
    return smartphoneZeile;
  }

  private Integer findeSARSummeZeile(final List<List<CellValue>> sarBlatt) {
    Integer summeZeile = Integer.MAX_VALUE;

    if (verarbeitungBisherOhneKritischeFehler()) {

      for (final List<CellValue> zeile : sarBlatt) {

        if (zeile.get(SAR_SUMME_TITEL_INDEX) != null
            && zeile.get(SAR_SUMME_TITEL_INDEX).getStringValue() != null
            && zeile.get(SAR_SUMME_TITEL_INDEX).getStringValue().equals("Summen")) {
          summeZeile = sarBlatt.indexOf(zeile);
        }
      }

      if (summeZeile.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeSummenZeileInBlattSARKonnteNichtErmitteltWerden(fehlerlog);
      }
    }
    return summeZeile;
  }

  private List<Projektstunde> importiereReisezeiten(
      final List<List<CellValue>> anwBlatt,
      final Integer angerechneteReisezeitSpalteIndex,
      final Integer arbeitsnachweisSummenZeileIndex,
      final Integer endeMarkerIndex,
      final Arbeitsnachweis arbeitsnachweis,
      final LocalDate abrechnungsmonat,
      final Integer summenZeileIndex,
      final List<Projektstunde> importierteProjektstunden,
      final BigDecimal angerechneteReisezeit) {

    final Map<Integer, LocalDate> tage = tage(anwBlatt, abrechnungsmonat, summenZeileIndex);
    final Map<Integer, String> projekte =
        reiseZeitenProjekte(anwBlatt, angerechneteReisezeitSpalteIndex, endeMarkerIndex, tage);

    excelImportValidator.pruefeObReisezeitenOhneProjektangabeAngegebenWurden(
        fehlerlog, tage, projekte, angerechneteReisezeitSpalteIndex, endeMarkerIndex, anwBlatt);

    List<Projektstunde> summierteReisezeitJeTagUndProjekt =
        summiereReisezeitJeTagUndProjekt(tage, projekte, anwBlatt);

    excelImportValidator
        .pruefeObAngerechneteReisezeitExcelMitInternAngerechneterReisezeitUebereinstimmt(
            fehlerlog,
            importierteProjektstunden,
            summierteReisezeitJeTagUndProjekt,
            angerechneteReisezeit,
            angerechneteReisezeitSpalteIndex,
            arbeitsnachweisSummenZeileIndex);

    return summierteReisezeitJeTagUndProjekt;
  }

  private Integer findeAngerechneteReisezeitSpalte(final List<List<CellValue>> anwBlatt) {
    Integer angerechneteReisezeitSpalteIndex = Integer.MAX_VALUE;
    if (verarbeitungBisherOhneKritischeFehler()) {
      if (anwBlatt.get(ZEILE_MIT_PROJEKTANGABEN) != null) {

        for (final CellValue zelle : anwBlatt.get(ZEILE_MIT_PROJEKTANGABEN)) {

          if (zelle != null
              && zelle.getStringValue() != null
              && zelle.getStringValue().equals(ANGERECHN_REISEZEIT)) {
            angerechneteReisezeitSpalteIndex =
                anwBlatt.get(ZEILE_MIT_PROJEKTANGABEN).indexOf(zelle);
          }
        }
      }

      if (angerechneteReisezeitSpalteIndex.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeAngReisezeitSpalteInBlattANWKonnteNichtErmitteltWerden(
            fehlerlog);
      }
    }

    return angerechneteReisezeitSpalteIndex;
  }

  private List[] importiereAbwesenheitenUndBelege(
      final List<List<CellValue>> sarBlatt,
      final Arbeitsnachweis arbeitsnachweis,
      final Integer sarSmartphoneZeileIndex,
      final Integer sarSummenZeileIndex) {
    final List[] abwesenheitenUndBelege = new ArrayList[2];
    final List<Beleg> belege = new ArrayList<>();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();

    final BelegartKonstante hotel = konstantenService.belegartByTextKurz(HOTEL.toString());
    final BelegartKonstante pkw = konstantenService.belegartByTextKurz(PKW.toString());
    final BelegartKonstante bahn = konstantenService.belegartByTextKurz(BAHN.toString());
    final BelegartKonstante flug = konstantenService.belegartByTextKurz(FLUG.toString());
    final BelegartKonstante taxi = konstantenService.belegartByTextKurz(TAXI.toString());
    final BelegartKonstante oepnv = konstantenService.belegartByTextKurz(OPNV.toString());
    final BelegartKonstante parken = konstantenService.belegartByTextKurz(PARKEN.toString());
    final BelegartKonstante sonstiges = konstantenService.belegartByTextKurz(SONSTIGES.toString());
    final BelegartKonstante verbindungsentgelt = konstantenService.belegartByTextKurz(
        VERBINDUNGSENTGELT.toString());

    for (final List<CellValue> zeile : sarBlatt) {

      if (excelImportValidator.sarZeilelIstFehlerfrei(zeile, fehlerlog, sarBlatt)) {

        final CellValue tagZelle = zeile.get(SAR_SPALTE_TAG);

        if (tagZelle != null && tagZelle.getNumberValue() != 0.0d) {

          final int anzahlBefuellterSpalten =
              excelImportValidator.bereinigeZeileUmNullUndLeereWerte(zeile).size();

          // Wurden Eingaben getätigt, gibt es mehr als 2 Werte in der Liste der Zellen
          if (anzahlBefuellterSpalten > 2) {

            final CellValue arbeitsstaetteZelle = zeile.get(SAR_SPALTE_ARBEITSSTAETTE);
            final CellValue projektZelle = zeile.get(SAR_SPALTE_PROJEKT);
            final CellValue hotelZelle = zeile.get(SAR_SPALTE_HOTEL);
            final CellValue pkwZelle = zeile.get(SAR_SPALTE_PKW);
            final CellValue zugZelle = zeile.get(SAR_SPALTE_ZUG);
            final CellValue flugZelle = zeile.get(SAR_SPALTE_FLUG);
            final CellValue taxiZelle = zeile.get(SAR_SPALTE_TAXI);
            final CellValue oepnvZelle = zeile.get(SAR_SPALTE_OEPNV);
            final CellValue parkenZelle = zeile.get(SAR_SPALTE_PARKEN);
            final CellValue nebenkostenTextZelle = zeile.get(SAR_SPALTE_NEBENKOSTEN);
            final CellValue nebenkostenBetragZelle = zeile.get(SAR_SPALTE_NEBENKOSTEN_BETRAG);
            final CellValue belegnummerZelle = zeile.get(SAR_SPALTE_BELEGNUMMER);
            final CellValue uhrzeitVonZelle = zeile.get(ExcelImport.SAR_SPALTE_UHRZEIT_VON);
            final CellValue uhrzeitBisZelle = zeile.get(ExcelImport.SAR_SPALTE_UHRZEIT_BIS);

            final String projektnummerGelesen = leseProjektnummerAusZelle(projektZelle);

            if (excelImportValidator.istGueltigesProjekt(
                projektnummerGelesen, fehlerlog, sarBlatt, zeile, SAR_SHEET_NAME)) {
              if (!nurNebenkostenSindBefuellt(
                  anzahlBefuellterSpalten,
                  projektZelle,
                  nebenkostenTextZelle,
                  nebenkostenBetragZelle,
                  belegnummerZelle)) {

                if (arbeitsstaetteZelle != null) {

                  excelImportValidator.schreibeWarnungFallsArbeitsstaetteUnbekannt(
                      fehlerlog, sarBlatt, zeile, arbeitsstaetteZelle);
                  String arbeitsstaette =
                      arbeitsstaetteZelle.getStringValue() == null
                          ? null
                          : arbeitsstaetteZelle.getStringValue().trim();

                  if (hotelZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(hotel.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(hotelZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }
                  if (pkwZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(pkw.getId());
                    beleg.setKilometer(
                        BigDecimal.valueOf(pkwZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setBetrag(
                        BigDecimal.valueOf(pkwZelle.getNumberValue())
                            .multiply(
                                new BigDecimal(
                                    parameterService
                                        .valueByKey("Kilometerpauschale")
                                        .replaceAll(",", ".")))
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (zugZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(bahn.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(zugZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (flugZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(flug.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(flugZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (taxiZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(taxi.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(taxiZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (oepnvZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(oepnv.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(oepnvZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (parkenZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(parken.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(parkenZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung("autom. importiert");

                    belege.add(beleg);
                  }

                  if (nebenkostenBetragZelle != null) {
                    final Beleg beleg = new Beleg();
                    beleg.setId(ThreadLocalRandom.current().nextLong());
                    beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                    beleg.setArbeitsstaette(arbeitsstaette);
                    beleg.setBelegartId(sonstiges.getId());
                    beleg.setBetrag(
                        BigDecimal.valueOf(nebenkostenBetragZelle.getNumberValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    beleg.setDatum(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    beleg.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    beleg.setBelegNr(
                        belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                    beleg.setBemerkung(
                        nebenkostenTextZelle != null
                            && nebenkostenTextZelle.getStringValue() != null
                            ? nebenkostenTextZelle.getStringValue() + "; autom. importiert"
                            : "autom. importiert");

                    belege.add(beleg);
                  }
                  if (arbeitsstaette != null
                      && uhrzeitBisZelle != null
                      && uhrzeitBisZelle.getStringValue() == null) {
                    final Abwesenheit abwesenheit = new Abwesenheit();

                    final String[] stundeMinuteVon =
                        uhrzeitVonZelle == null
                            ? new String[]{"00", "00"}
                            : dezimalZahlenFormatter
                                .format(uhrzeitVonZelle.getNumberValue())
                                .split(",");
                    final String[] stundeMinuteBis =
                        dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

                    final LocalTime uhrzeitVon = berechneUhrzeit(stundeMinuteVon);
                    final LocalTime uhrzeitBis = berechneUhrzeit(stundeMinuteBis);

                    abwesenheit.setId(ThreadLocalRandom.current().nextLong());
                    abwesenheit.setUhrzeitVon(uhrzeitVon);
                    abwesenheit.setUhrzeitBis(uhrzeitBis);
                    abwesenheit.setArbeitsstaette(arbeitsstaette);
                    abwesenheit.setTagVon(
                        tagZelle != null
                            ? LocalDate.of(
                            arbeitsnachweis.getJahr(),
                            arbeitsnachweis.getMonat(),
                            (int) tagZelle.getNumberValue())
                            : LocalDate.of(
                                arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                    abwesenheit.setTagBis(abwesenheit.getTagVon());

                    abwesenheit.setBemerkung("autom. importiert");
                    abwesenheit.setProjektId(
                        projektService.projektByProjektnummer(projektnummerGelesen).getId());
                    abwesenheit.setZuschlag(ZERO);
                    abwesenheit.setSpesen(ZERO);
                    abwesenheiten.add(abwesenheit);
                  }
                }
              } else {

                final Beleg beleg = new Beleg();
                beleg.setId(ThreadLocalRandom.current().nextLong());
                beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
                beleg.setBelegartId(sonstiges.getId());
                beleg.setBetrag(
                    BigDecimal.valueOf(nebenkostenBetragZelle.getNumberValue())
                        .setScale(2, RoundingMode.HALF_UP));
                beleg.setDatum(
                    tagZelle != null
                        ? LocalDate.of(
                        arbeitsnachweis.getJahr(),
                        arbeitsnachweis.getMonat(),
                        (int) tagZelle.getNumberValue())
                        : LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
                beleg.setProjektId(
                    projektService.projektByProjektnummer(projektnummerGelesen).getId());
                beleg.setBelegNr(belegnummerZelle != null ? belegnummerZelle.getStringValue() : "");
                beleg.setBemerkung(nebenkostenTextZelle.getStringValue() + "; autom. importiert");

                belege.add(beleg);
              }
            }
          }
        }
      }
    }

    if (abwesenheiten.isEmpty() && !sarBlatt.isEmpty()) {
      excelImportLogWriter.schreibeKeineAbwesenheitImportiert(fehlerlog);
    }

    if (!sarBlatt.isEmpty()) {
      if (sarBlatt.get(sarSmartphoneZeileIndex) != null
          && sarBlatt.get(sarSmartphoneZeileIndex + 1).get(SMARTPHONE_TITEL_INDEX + 1) != null
          && sarBlatt
          .get(sarSmartphoneZeileIndex + 1)
          .get(SMARTPHONE_TITEL_INDEX + 1)
          .getNumberValue()
          != 0.0d) {
        final Beleg beleg = new Beleg();
        beleg.setId(ThreadLocalRandom.current().nextLong());
        beleg.setArbeitsnachweisId(arbeitsnachweis.getId());
        beleg.setArbeitsstaette("");
        beleg.setBelegartId(verbindungsentgelt.getId());
        BigDecimal verbindungsentgeltWert =
            BigDecimal.valueOf(
                    sarBlatt
                        .get(sarSmartphoneZeileIndex + 1)
                        .get(SMARTPHONE_TITEL_INDEX + 1)
                        .getNumberValue())
                .setScale(2, RoundingMode.HALF_UP);
        excelImportValidator.schreibeWarnungFallsVerbindungsentgeldUeberErstattungsgrenze(
            fehlerlog, sarBlatt, sarBlatt.get(sarSmartphoneZeileIndex), verbindungsentgeltWert);
        beleg.setBetrag(verbindungsentgeltWert);
        beleg.setDatum(LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1));
        beleg.setProjektId(
            projektService
                .projektByProjektnummer(parameterService.valueByKey("Projekt_Verbindungsentgelte"))
                .getId());
        beleg.setBemerkung("autom. importiert");

        belege.add(beleg);
      }
    }

    if (belege.isEmpty()) {
      if (!sarBlatt.isEmpty()) {
        excelImportLogWriter.schreibeKeineBelegeImportiert(fehlerlog);
      }
    } else if (!sarBlatt.isEmpty()) {
      excelImportValidator.pruefeSARSummen(fehlerlog, belege, sarSummenZeileIndex, sarBlatt);
    }

    abwesenheitenUndBelege[0] = abwesenheiten;
    abwesenheitenUndBelege[1] = belege;
    return abwesenheitenUndBelege;
  }

  private boolean nurNebenkostenSindBefuellt(
      final int anzahlBefuellterSpalten,
      final CellValue projekt,
      final CellValue nebenkostenText,
      final CellValue nebenkostenBetrag,
      final CellValue belegnummer) {
    return (anzahlBefuellterSpalten == 5
        && projekt != null
        && nebenkostenText != null
        && nebenkostenBetrag != null
        && belegnummer == null)
        || (anzahlBefuellterSpalten == 6
        && projekt != null
        && nebenkostenText != null
        && nebenkostenBetrag != null
        && belegnummer != null);
  }

  private Integer findeEndeMarker(final List<List<CellValue>> anwBlatt) {
    Integer endeMarkerIndex = Integer.MAX_VALUE;
    if (verarbeitungBisherOhneKritischeFehler()) {
      if (anwBlatt.get(ZEILE_MIT_ENDE_INDEX) != null) {

        for (final CellValue zelle : anwBlatt.get(ZEILE_MIT_ENDE_INDEX)) {

          if (zelle != null
              && zelle.getStringValue() != null
              && zelle.getStringValue().equals("ENDE")) {
            endeMarkerIndex = anwBlatt.get(ZEILE_MIT_ENDE_INDEX).indexOf(zelle);
          }
        }
      }

      if (endeMarkerIndex.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeEndeMarkerInBlattAnwKonnteNichtErmitteltWerden(fehlerlog);
      }
    }
    return endeMarkerIndex;
  }

  private Integer findeSonderzeitenSummenZeile(final List<List<CellValue>> sonderzeitenBlatt) {
    Integer sonderzeitenSummenZeile = Integer.MAX_VALUE;

    if (verarbeitungBisherOhneKritischeFehler()) {

      for (final List<CellValue> zeile : sonderzeitenBlatt) {

        if (zeile.get(SONDERZEITEN_SUMMEN_TITEL_INDEX) != null
            && zeile.get(SONDERZEITEN_SUMMEN_TITEL_INDEX).getStringValue() != null
            && zeile.get(SONDERZEITEN_SUMMEN_TITEL_INDEX).getStringValue().equals("Summen")) {
          sonderzeitenSummenZeile = sonderzeitenBlatt.indexOf(zeile);
        }
      }

      if (sonderzeitenSummenZeile.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeSummenZeileInBlattSonderzeitenKonnteNichtErmitteltWerden(
            fehlerlog);
      }
    }
    return sonderzeitenSummenZeile;
  }

  private Integer findeArbeitsnachweisSummenZeile(final List<List<CellValue>> anwBlatt) {
    final Integer arbeitsnachweisSummenZeile = Integer.MAX_VALUE;

    if (verarbeitungBisherOhneKritischeFehler()) {

      for (final List<CellValue> zeile : anwBlatt) {

        if (zeile.size() >= ANW_SUMMEN_TITEL_INDEX
            && zeile.get(ANW_SUMMEN_TITEL_INDEX) != null
            && zeile.get(ANW_SUMMEN_TITEL_INDEX).getStringValue() != null
            && zeile.get(ANW_SUMMEN_TITEL_INDEX).getStringValue().equals("Summe")) {
          return anwBlatt.indexOf(zeile);
        }
      }

      if (arbeitsnachweisSummenZeile.equals(Integer.MAX_VALUE)) {
        excelImportLogWriter.schreibeSummenZeileInBlattANWKonnteNichtErmitteltWerden(fehlerlog);
      }
    }
    return arbeitsnachweisSummenZeile;
  }

  private List<Projektstunde> importiereProjektstunden(
      final List<List<CellValue>> anwBlatt,
      final LocalDate abrechnungsmonat,
      final Arbeitsnachweis arbeitsnachweis,
      final Integer anwAngerechneteReisezeitSpalteIndex,
      final Integer arbeitsnachweisSummenZeileIndex,
      final BigDecimal angerechneteReisezeit) {
    final Map<Integer, String> projekte = projekte(anwBlatt, anwAngerechneteReisezeitSpalteIndex);
    final Map<Integer, String> faktuerierfaehigkeitDerProjektstunden =
        leseZeileNichtFakturierfaehig(anwBlatt, anwAngerechneteReisezeitSpalteIndex);
    final Map<Integer, LocalDate> tage =
        tage(anwBlatt, abrechnungsmonat, arbeitsnachweisSummenZeileIndex);
    final List<Projektstunde> projektstunden =
        summiereStundenJeTagUndProjekt(
            tage,
            projekte,
            faktuerierfaehigkeitDerProjektstunden,
            anwBlatt,
            arbeitsnachweis,
            anwAngerechneteReisezeitSpalteIndex);

    excelImportValidator.pruefeProjektstundenSumme(
        fehlerlog,
        anwBlatt,
        arbeitsnachweisSummenZeileIndex,
        projektstunden,
        arbeitsnachweisSummenZeileIndex,
        angerechneteReisezeit);

    return projektstunden;
  }

  private List<Projektstunde> importiereRufbereitschaft(
      final List<List<CellValue>> sonderzeitenBlatt,
      final Integer sonderzeitenSummenZeileIndex,
      final Arbeitsnachweis arbeitsnachweis,
      final LocalDate abrechnungsmonat,
      final BigDecimal summeRufbereitschaft) {

    List<Projektstunde> importierteRufbereitschaft = new ArrayList<>();

    if (!sonderzeitenBlatt.isEmpty()) {

      final Map<Integer, Projekt> projekteRufbereitschaft =
          rufbereitschaftProjekte(sonderzeitenBlatt, sonderzeitenSummenZeileIndex);
      importierteRufbereitschaft =
          rufbereitschaften(
              projekteRufbereitschaft,
              sonderzeitenSummenZeileIndex,
              sonderzeitenBlatt,
              arbeitsnachweis,
              abrechnungsmonat);

      excelImportValidator.pruefeRufbereitschaftSumme(
          fehlerlog,
          importierteRufbereitschaft,
          summeRufbereitschaft,
          sonderzeitenSummenZeileIndex);
    }

    if (importierteRufbereitschaft.isEmpty() && !sonderzeitenBlatt.isEmpty()) {
      excelImportLogWriter.schreibeKeineRufbereitschaftImportiert(fehlerlog);
    }

    return importierteRufbereitschaft;
  }

  private List<Projektstunde> importiereSonderarbeitszeiten(
      final List<List<CellValue>> sonderzeitenBlatt,
      final Integer sonderzeitenSummenZeileIndex,
      final Arbeitsnachweis arbeitsnachweis,
      final LocalDate abrechnungsmonat,
      final BigDecimal summeSonderarbeitszeit,
      final List<Projektstunde> importierteProjektstunden) {
    List<Projektstunde> importiereSonderarbeitszeiten = new ArrayList<>();

    if (!sonderzeitenBlatt.isEmpty()) {

      final Map<Integer, Projekt> projekteSonderarbeitszeit =
          sonderarbeitszeitProjekte(sonderzeitenBlatt, sonderzeitenSummenZeileIndex);
      importiereSonderarbeitszeiten =
          sonderarbeitszeiten(
              projekteSonderarbeitszeit,
              sonderzeitenSummenZeileIndex,
              sonderzeitenBlatt,
              arbeitsnachweis,
              abrechnungsmonat,
              importierteProjektstunden,
              zelleExcelAdresseBlattSonderzeiten);

      excelImportValidator.pruefeSonderarbeitszeitSumme(
          fehlerlog,
          importiereSonderarbeitszeiten,
          summeSonderarbeitszeit,
          sonderzeitenSummenZeileIndex);
    }

    if (importiereSonderarbeitszeiten.isEmpty() && !sonderzeitenBlatt.isEmpty()) {
      excelImportLogWriter.schreibeKeineSonderarbeitszeitImportiert(fehlerlog);
    }

    return importiereSonderarbeitszeiten;
  }

  private List<Projektstunde> sonderarbeitszeiten(
      final Map<Integer, Projekt> projekteSonderarbeitszeit,
      final Integer sonderzeitenSummenZeileIndex,
      final List<List<CellValue>> sonderzeitenBlatt,
      final Arbeitsnachweis arbeitsnachweis,
      final LocalDate abrechnungsmonat,
      final List<Projektstunde> importierteProjektstunden,
      final Map<CellValue, CellAddress> zelleExcelAdresseBlattSonderzeiten2) {
    final List<Projektstunde> sonderzeiten = new ArrayList<>();

    for (final Integer zeilenIndex : projekteSonderarbeitszeit.keySet()) {
      if (zeilenIndex > SONDERZEITEN_START_ZEILE && zeilenIndex < sonderzeitenSummenZeileIndex) {

        final boolean keineFehler =
            excelImportValidator.pruefeSonderarbeitszeitZeile(
                fehlerlog,
                zelleExcelAdresseBlattSonderzeiten,
                sonderzeitenBlatt.get(zeilenIndex),
                abrechnungsmonat,
                sonderzeitenBlatt);

        if (keineFehler) {

          final CellValue tagVonZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(SONDERZEIT_SPALTE_TAG_VON);
          final CellValue uhrzeitVonZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(SONDERZEIT_SPALTE_UHRZEIT_VON);
          final CellValue uhrzeitBisZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(SONDERZEIT_SPALTE_UHRZEIT_BIS);
          final CellValue stundenSummeZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(SONDERZEIT_SPALTE_SUMME_STUNDEN);
          final CellValue bemerkungZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(SONDERZEIT_SPALTE_BEMERKUNG);

          final LocalDate tagVon =
              abrechnungsmonat.plusDays((long) (tagVonZelle.getNumberValue() - 1.0d));

          final String[] stundeMinuteVon =
              dezimalZahlenFormatter.format(uhrzeitVonZelle.getNumberValue()).split(",");
          final String[] stundeMinuteBis =
              dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

          final LocalTime uhrzeitVon = berechneUhrzeit(stundeMinuteVon);

          final LocalTime uhrzeitBis = berechneUhrzeit(stundeMinuteBis);

          final String bemerkung = bemerkungZelle != null ? bemerkungZelle.getStringValue() : null;

          final Projektstunde sonderarbeitszeitStunden =
              erstelleSonderarbeitszeitProjektstunden(
                  projekteSonderarbeitszeit,
                  zeilenIndex,
                  stundenSummeZelle,
                  tagVon,
                  uhrzeitVon,
                  uhrzeitBis,
                  bemerkung);

          excelImportValidator.pruefeObSonderarbeitszeitDieProjektstundenUeberschreitet(
              fehlerlog,
              sonderarbeitszeitStunden,
              importierteProjektstunden,
              stundenSummeZelle,
              zelleExcelAdresseBlattSonderzeiten);

          sonderzeiten.add(sonderarbeitszeitStunden);
        }
      }
    }

    return sonderzeiten;
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

  private Projektstunde erstelleSonderarbeitszeitProjektstunden(
      final Map<Integer, Projekt> projekteSonderarbeitszeit,
      final Integer zeilenIndex,
      final CellValue stundenSummeZelle,
      final LocalDate tagVon,
      final LocalTime uhrzeitVon,
      final LocalTime uhrzeitBis,
      final String bemerkung) {
    final Projektstunde sonderarbeitszeitStunden = new Projektstunde();
    sonderarbeitszeitStunden.setTagVon(tagVon);
    sonderarbeitszeitStunden.setUhrzeitVon(uhrzeitVon);
    sonderarbeitszeitStunden.setUhrzeitBis(uhrzeitBis);
    sonderarbeitszeitStunden.setAnzahlStunden(
        BigDecimal.valueOf(stundenSummeZelle.getNumberValue()).setScale(2, RoundingMode.HALF_UP));
    sonderarbeitszeitStunden.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(SONDERARBEITSZEIT.getValue()).getId());
    sonderarbeitszeitStunden.setProjektId(projekteSonderarbeitszeit.get(zeilenIndex).getId());
    sonderarbeitszeitStunden.setBemerkung(
        bemerkung != null ? bemerkung + "; autom. importiert" : "autom. importiert");
    sonderarbeitszeitStunden.setId(ThreadLocalRandom.current().nextLong());
    sonderarbeitszeitStunden.setZuletztGeaendertAm(LocalDateTime.now());
    return sonderarbeitszeitStunden;
  }

  private List<Projektstunde> rufbereitschaften(
      final Map<Integer, Projekt> projekteRufbereitschaft,
      final Integer sonderzeitenSummenZeileIndex,
      final List<List<CellValue>> sonderzeitenBlatt,
      final Arbeitsnachweis arbeitsnachweis,
      final LocalDate abrechnungsmonat) {

    final List<Projektstunde> rufbereitschaften = new ArrayList<>();

    for (final Integer zeilenIndex : projekteRufbereitschaft.keySet()) {

      if (zeilenIndex > SONDERZEITEN_START_ZEILE && zeilenIndex < sonderzeitenSummenZeileIndex) {

        final boolean keineFehler =
            excelImportValidator.pruefeRufbereitschaftZeile(
                fehlerlog,
                zelleExcelAdresseBlattSonderzeiten,
                sonderzeitenBlatt.get(zeilenIndex),
                abrechnungsmonat,
                sonderzeitenBlatt);

        if (keineFehler) {

          final CellValue tagVonZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(RUFBEREITSCHAFT_SPALTE_TAG_VON);
          final CellValue uhrzeitVonZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(RUFBEREITSCHAFT_SPALTE_UHRZEIT_VON);
          final CellValue tagBisZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(RUFBEREITSCHAFT_SPALTE_TAG_BIS);
          final CellValue uhrzeitBisZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(RUFBEREITSCHAFT_SPALTE_UHRZEIT_BIS);
          final CellValue stundenSummeZelle =
              sonderzeitenBlatt.get(zeilenIndex).get(RUFBEREITSCHAFT_SPALTE_SUMME_STUNDEN);

          final LocalDate tagVon =
              abrechnungsmonat.plusDays((long) (tagVonZelle.getNumberValue() - 1.0d));
          final LocalDate tagBis =
              abrechnungsmonat.plusDays((long) (tagBisZelle.getNumberValue() - 1.0d));

          final String[] stundeMinuteVon =
              dezimalZahlenFormatter.format(uhrzeitVonZelle.getNumberValue()).split(",");
          final String[] stundeMinuteBis =
              dezimalZahlenFormatter.format(uhrzeitBisZelle.getNumberValue()).split(",");

          final LocalTime uhrzeitVon = berechneUhrzeit(stundeMinuteVon);

          final LocalTime uhrzeitBis = berechneUhrzeit(stundeMinuteBis);

          final Projektstunde rufbereitschaftStunden =
              erstelleRufbereitschaftProjektstunden(
                  projekteRufbereitschaft,
                  zeilenIndex,
                  stundenSummeZelle,
                  tagVon,
                  tagBis,
                  uhrzeitVon,
                  uhrzeitBis);
          rufbereitschaften.add(rufbereitschaftStunden);
        }
      }
    }

    return rufbereitschaften;
  }

  private Projektstunde erstelleRufbereitschaftProjektstunden(
      final Map<Integer, Projekt> projekteRufbereitschaft,
      final Integer zeilenIndex,
      final CellValue stundenSummeZelle,
      final LocalDate tagVon,
      final LocalDate tagBis,
      final LocalTime uhrzeitVon,
      final LocalTime uhrzeitBis) {
    final Projektstunde rufbereitschaftStunden = new Projektstunde();
    rufbereitschaftStunden.setTagVon(tagVon);
    rufbereitschaftStunden.setTagBis(tagBis);
    rufbereitschaftStunden.setUhrzeitVon(uhrzeitVon);
    rufbereitschaftStunden.setUhrzeitBis(uhrzeitBis);
    rufbereitschaftStunden.setAnzahlStunden(BigDecimal.valueOf(stundenSummeZelle.getNumberValue()));
    rufbereitschaftStunden.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(RUFBEREITSCHAFT.getValue()).getId());
    rufbereitschaftStunden.setProjektId(projekteRufbereitschaft.get(zeilenIndex).getId());
    rufbereitschaftStunden.setBemerkung("autom. importiert");
    rufbereitschaftStunden.setId(ThreadLocalRandom.current().nextLong());
    rufbereitschaftStunden.setZuletztGeaendertAm(LocalDateTime.now());
    return rufbereitschaftStunden;
  }

  private Arbeitsnachweis ladeArbeitsnachweis(
      final LocalDate abrechnungsmonat, final Mitarbeiter mitarbeiter) {
    Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();

    if (verarbeitungBisherOhneKritischeFehler()) {

      arbeitsnachweis =
          arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
              mitarbeiter.getId(), abrechnungsmonat.getMonthValue(), abrechnungsmonat.getYear());
      if (arbeitsnachweis == null) {

        arbeitsnachweis = new Arbeitsnachweis();
        arbeitsnachweis.setJahr(abrechnungsmonat.getYear());
        arbeitsnachweis.setMonat(abrechnungsmonat.getMonthValue());
        arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
        arbeitsnachweis.setSmartphoneEigen(KEIN.value());

        MitarbeiterStellenfaktor mitarbeiterStellenfaktor =
            mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
                mitarbeiter.getId(), arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat());
        if (mitarbeiterStellenfaktor.getStellenfaktor().equals(new BigDecimal("0.00001"))) {
          excelImportLogWriter.schreibeMitarbeiterHatKeinenStellenfaktorFehlermeldung(fehlerlog);
        }

        arbeitsnachweis.setStellenfaktor(mitarbeiterStellenfaktor.getStellenfaktor());
      }
    }

    excelImportValidator.pruefeObImportMitArbeitsnachweisStatusFortgesetztWerdenDarf(
        arbeitsnachweis, fehlerlog);

    return arbeitsnachweis;
  }

  private Mitarbeiter leseMitarbeiterZuPersonalnummer(
      final String personalnummer, final Long mitarbeiterInBearbeitungId) {
    final Mitarbeiter arbeitsnachweisMitarbeiter =
        mitarbeiterService.mitarbeiterByPersonalnummer(personalnummer);

    if (verarbeitungBisherOhneKritischeFehler()) {

      if (arbeitsnachweisMitarbeiter != null) {

        if (!arbeitsnachweisMitarbeiter.getId().equals(mitarbeiterInBearbeitungId)) {
          Mitarbeiter mitarbeiterInBearbeitung = mitarbeiterService.mitarbeiterById(
              mitarbeiterInBearbeitungId);
          excelImportLogWriter.schreibeMitarbeiterUnterschiedlichFehlermeldung(
              fehlerlog, arbeitsnachweisMitarbeiter, mitarbeiterInBearbeitung);
        }
      } else {
        excelImportLogWriter.schreibeKeinenMitarbeiterZurPersonalnummerGefundenFehlermeldung(
            fehlerlog);
      }
    }
    return arbeitsnachweisMitarbeiter;
  }

  private List<List<CellValue>> leseExcelDateiBlattANWEin(
      final InputStream dateiInputStream, final String arbeitsnachweisTemplateSoll,
      final boolean validierungsModus) {

    final List<List<CellValue>> alleZeilen = new ArrayList<>();

    try (final XSSFWorkbook workbook = new XSSFWorkbook(dateiInputStream)) {

      if (workbook.isMacroEnabled()) {
        excelImportLogWriter.schreibeFormatNichtUnterstuetzt(fehlerlog);
        return null;
      }

      excelImportValidator.pruefeTemplateVersion(
          fehlerlog, arbeitsnachweisTemplateSoll, validierungsModus, workbook);

      if (workbook != null) {
        final XSSFFormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(workbook);
        final XSSFSheet sheet = workbook.getSheet(ANW_SHEET_NAME);
        if (sheet == null) {
          excelImportLogWriter.schreibeBlattANWFehltFehlermeldung(fehlerlog);
          return null;
        } else {
          for (Row row : sheet) {
            final List<CellValue> geleseneZeile = new ArrayList<>();
            for (int i = 0; i < row.getLastCellNum(); i++) {
              final XSSFCell zelle =
                  (XSSFCell) row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
              try {
                final CellValue zellenWert = formulaEvaluator.evaluate(zelle);
                zelleExcelAdresseBlattANW.put(zellenWert, zelle.getAddress());
                geleseneZeile.add(zellenWert);
              } catch (final FormulaParseException fpe) {
                excelImportLogWriter.schreibeSchwererFehlerBeiDerFormelauswertung(
                    fehlerlog, fpe.getMessage());
              }
            }
            alleZeilen.add(geleseneZeile);
          }
        }
      }

    } catch (final NullPointerException npe) {
      excelImportLogWriter.schreibeKeineGueltigeExcelDateiFehlermeldung(fehlerlog);
      return null;
    } catch (final IOException io) {
      excelImportLogWriter.schreibeDieDateiKonnteNichtGelesenWerdenFehlermeldung(fehlerlog);
      return null;
    } catch (final NotOfficeXmlFileException oxmf) {
      excelImportLogWriter.schreibeFormatNichtUnterstuetzt(fehlerlog);
      return null;
    }

    return alleZeilen;
  }

  private List<List<CellValue>> leseExcelDateiBlattSonderzeitenEin(
      final InputStream dateiInputStream) {
    final List<List<CellValue>> alleZeilen = new ArrayList<>();
    if (verarbeitungBisherOhneKritischeFehler()) {

      try (final XSSFWorkbook workbook = new XSSFWorkbook(dateiInputStream)) {
        final XSSFFormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(workbook);
        final XSSFSheet sheet = workbook.getSheet(SONDERZEITEN_SHEET_NAME);
        if (sheet == null) {
          excelImportLogWriter.schreibeBlattSonderzeitenFehltFehlermeldung(fehlerlog);
        } else {
          for (Row row : sheet) {
            final List<CellValue> geleseneZeile = new ArrayList<>();
            for (int i = 0; i < row.getLastCellNum() && i <= MAX_SPALTE_SONDERZEITEN; i++) {
              final XSSFCell zelle =
                  (XSSFCell) row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
              try {
                final CellValue zellenWert = formulaEvaluator.evaluate(zelle);
                zelleExcelAdresseBlattSonderzeiten.put(zellenWert, zelle.getAddress());
                geleseneZeile.add(zellenWert);

              } catch (final FormulaParseException fpe) {
                excelImportLogWriter.schreibeSchwererFehlerBeiDerFormelauswertung(
                    fehlerlog, fpe.getMessage());
              }
            }
            alleZeilen.add(geleseneZeile);
          }
        }
      } catch (final NullPointerException npe) {
        excelImportLogWriter.schreibeKeineGueltigeExcelDateiFehlermeldung(fehlerlog);
      } catch (final IOException io) {
        excelImportLogWriter.schreibeDieDateiKonnteNichtGelesenWerdenFehlermeldung(fehlerlog);
      } catch (final OLE2NotOfficeXmlFileException oe) {
        excelImportLogWriter.schreibeFormatNichtUnterstuetzt(fehlerlog);
        return null;
      }
    }
    return alleZeilen;
  }

  private List<List<CellValue>> leseExcelDateiBlattSAREin(final InputStream dateiInputStream) {

    final List<List<CellValue>> alleZeilen = new ArrayList<>();

    if (verarbeitungBisherOhneKritischeFehler()) {

      try (final XSSFWorkbook workbook = new XSSFWorkbook(dateiInputStream)) {
        final XSSFFormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(workbook);
        final XSSFSheet sheet = workbook.getSheet(SAR_SHEET_NAME);
        if (sheet == null) {
          excelImportLogWriter.schreibeBlattSARFehltFehlermeldung(fehlerlog);
        } else {
          for (Row row : sheet) {
            final List<CellValue> geleseneZeile = new ArrayList<>();
            for (int i = 0; i < row.getLastCellNum() && i <= MAX_SPALTE_SAR; i++) {
              final XSSFCell zelle =
                  (XSSFCell) row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
              try {
                final CellValue zellenWert = formulaEvaluator.evaluate(zelle);
                zelleExcelAdresseBlattSAR.put(zellenWert, zelle.getAddress());
                geleseneZeile.add(zellenWert);
              } catch (final FormulaParseException fpe) {
                excelImportLogWriter.schreibeSchwererFehlerBeiDerFormelauswertung(
                    fehlerlog, fpe.getMessage());
              }
            }
            alleZeilen.add(geleseneZeile);
          }
        }
      } catch (final NullPointerException npe) {
        excelImportLogWriter.schreibeKeineGueltigeExcelDateiFehlermeldung(fehlerlog);
      } catch (final IOException io) {
        excelImportLogWriter.schreibeDieDateiKonnteNichtGelesenWerdenFehlermeldung(fehlerlog);
      } catch (final OLE2NotOfficeXmlFileException oe) {
        excelImportLogWriter.schreibeFormatNichtUnterstuetzt(fehlerlog);
        return null;
      }
    }
    return alleZeilen;
  }

  public String personalnummer(final List<List<CellValue>> arbeitsnachweisBlatt) {
    String personalnummer = null;

    if (verarbeitungBisherOhneKritischeFehler()) {

      if (arbeitsnachweisBlatt.get(PERSONALNUMMER_INDEX[0]) != null
          && arbeitsnachweisBlatt.get(PERSONALNUMMER_INDEX[0]).get(PERSONALNUMMER_INDEX[1])
          != null) {

        final Double doublePersonalnummer =
            arbeitsnachweisBlatt
                .get(PERSONALNUMMER_INDEX[0])
                .get(PERSONALNUMMER_INDEX[1])
                .getNumberValue();
        // Numerische Angabe
        if (doublePersonalnummer > 0.0d) {
          personalnummer = String.valueOf(doublePersonalnummer.intValue());
        } else {
          // Angabe per String mit Hochkomma
          personalnummer =
              arbeitsnachweisBlatt
                  .get(PERSONALNUMMER_INDEX[0])
                  .get(PERSONALNUMMER_INDEX[1])
                  .getStringValue();
        }
      }

      if (personalnummer != null) {
        personalnummer = personalnummer.replaceFirst("^0+(?!$)", "");
      } else {
        excelImportLogWriter.schreibePersonalnummerKonnteNichtErmitteltWerdenFehlermeldung(
            fehlerlog,
            zelleExcelAdresseBlattANW
                .get(arbeitsnachweisBlatt.get(PERSONALNUMMER_INDEX[0]).get(PERSONALNUMMER_INDEX[1]))
                .formatAsString());
      }
    }

    return personalnummer;
  }

  public LocalDate abrechnungsmonat(final List<List<CellValue>> arbeitsnachweisBlatt) {
    LocalDate abrechnungsmonat = null;

    if (verarbeitungBisherOhneKritischeFehler()) {

      if (arbeitsnachweisBlatt.get(ABRECHNUNGSMONAT_INDEX[0]) != null
          && arbeitsnachweisBlatt.get(ABRECHNUNGSMONAT_INDEX[0]).get(ABRECHNUNGSMONAT_INDEX[1])
          != null) {

        abrechnungsmonat =
            wandleExcelDatumInLocalDate(
                arbeitsnachweisBlatt
                    .get(ABRECHNUNGSMONAT_INDEX[0])
                    .get(ABRECHNUNGSMONAT_INDEX[1])
                    .getNumberValue());
      }
      if (abrechnungsmonat != null) {

        final LocalDate aktuellerMonat = LocalDate.now();
        if (abrechnungsmonat.isAfter(aktuellerMonat)) {
          excelImportLogWriter.schreibeAbrechnungsmonatLiegtInDerZukunftFehlermeldung(
              fehlerlog,
              zelleExcelAdresseBlattANW
                  .get(
                      arbeitsnachweisBlatt
                          .get(ABRECHNUNGSMONAT_INDEX[0])
                          .get(ABRECHNUNGSMONAT_INDEX[1]))
                  .formatAsString(),
              aktuellerMonat);
        }
        if (istAbgeschlossen(abrechnungsmonat)) {
          excelImportLogWriter.schreibeAbrechnungsmonatIstBereitsAbgeschlossenFehlermeldung(
              fehlerlog,
              zelleExcelAdresseBlattANW
                  .get(
                      arbeitsnachweisBlatt
                          .get(ABRECHNUNGSMONAT_INDEX[0])
                          .get(ABRECHNUNGSMONAT_INDEX[1]))
                  .formatAsString(),
              abrechnungsmonat);
        }

      } else {
        excelImportLogWriter.schreibeAbrechnungsmonatKonnteNichtErmitteltWerdenFehlermeldung(
            fehlerlog,
            zelleExcelAdresseBlattANW
                .get(
                    arbeitsnachweisBlatt
                        .get(ABRECHNUNGSMONAT_INDEX[0])
                        .get(ABRECHNUNGSMONAT_INDEX[1]))
                .formatAsString());
      }
    }

    return abrechnungsmonat;
  }

  private boolean istAbgeschlossen(final LocalDate abrechnungsmonat) {
    return monatsabschlussService.istAbgeschlossen(abrechnungsmonat);
  }

  public BigDecimal summeRufbereitschaft(
      final List<List<CellValue>> sonderzeitenBlatt, final Integer sonderzeitenSummenZeileIndex) {
    BigDecimal summeRufbereitschaft = BigDecimal.ZERO;

    if (!sonderzeitenBlatt.isEmpty()
        && sonderzeitenBlatt.get(sonderzeitenSummenZeileIndex) != null
        && sonderzeitenBlatt.get(sonderzeitenSummenZeileIndex).get(SUMME_RUFBEREITSCHAFT_INDEX)
        != null) {

      summeRufbereitschaft =
          BigDecimal.valueOf(sonderzeitenBlatt
              .get(sonderzeitenSummenZeileIndex)
              .get(SUMME_RUFBEREITSCHAFT_INDEX)
              .getNumberValue());
    }

    return summeRufbereitschaft;
  }

  public BigDecimal summeSonderarbeitszeit(
      final List<List<CellValue>> sonderzeitenBlatt, final Integer sonderzeitenSummenZeileIndex) {
    BigDecimal summeSonderarbeitszeit = BigDecimal.ZERO;

    if (!sonderzeitenBlatt.isEmpty()
        && sonderzeitenBlatt.get(sonderzeitenSummenZeileIndex) != null
        && sonderzeitenBlatt
        .get(sonderzeitenSummenZeileIndex)
        .get(SONDERZEITEN_SPALTE_SUMME_SONDERARBEITSZEIT_INDEX)
        != null) {

      summeSonderarbeitszeit =
          BigDecimal.valueOf(sonderzeitenBlatt
              .get(sonderzeitenSummenZeileIndex)
              .get(SONDERZEITEN_SPALTE_SUMME_SONDERARBEITSZEIT_INDEX)
              .getNumberValue());
    }

    return summeSonderarbeitszeit;
  }

  public BigDecimal angerechneteReisezeit(
      final List<List<CellValue>> anwBlatt,
      final Integer summenZeileIndex,
      final Integer angerechneteReisezeitSpalteIndex) {
    BigDecimal angerechneteReisezeit = null;

    if (!anwBlatt.isEmpty()
        && anwBlatt.get(summenZeileIndex) != null
        && anwBlatt.get(summenZeileIndex).get(angerechneteReisezeitSpalteIndex) != null) {

      angerechneteReisezeit =
          BigDecimal.valueOf(anwBlatt
              .get(summenZeileIndex)
              .get(angerechneteReisezeitSpalteIndex)
              .getNumberValue());
    }

    return angerechneteReisezeit;
  }

  public BigDecimal stellenfaktorAusArbeitsnachweis(
      final List<List<CellValue>> anwBlatt,
      final Integer stellenfaktorZeileIndex,
      final Integer stellenfaktorSpalteIndex) {
    BigDecimal stellenfaktor = BigDecimal.ONE;

    if (!anwBlatt.isEmpty()
        && anwBlatt.get(stellenfaktorZeileIndex) != null
        && anwBlatt.get(stellenfaktorZeileIndex).get(stellenfaktorSpalteIndex) != null) {

      stellenfaktor =
          BigDecimal.valueOf(anwBlatt
                  .get(stellenfaktorZeileIndex)
                  .get(stellenfaktorSpalteIndex)
                  .getNumberValue())
              .setScale(2, RoundingMode.HALF_UP);
    }

    return stellenfaktor;
  }

  private LocalDate wandleExcelDatumInLocalDate(final double excelDatum) {
    final Date date = DateUtil.getJavaDate(excelDatum);
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public Map<Integer, String> projekte(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final Integer anwAngerechneteReisezeitSpalteIndex) {
    final Map<Integer, String> spalteProjektnummer = new HashMap<>();

    if (arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN) != null) {

      for (final CellValue zelle : arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN)) {
        if (arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN).indexOf(zelle)
            >= ANW_BLATT_PROJEKTE_START
            && arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN).indexOf(zelle)
            < anwAngerechneteReisezeitSpalteIndex) {
          final String zellenWert = leseProjektnummerAusZelle(zelle);
          if (zellenWert != null) {
            String ersteVierStellenDesZelleninhalts = "";
            try {
              ersteVierStellenDesZelleninhalts = zellenWert.substring(0, 4);
              if (istVierstelligeZahl(ersteVierStellenDesZelleninhalts)) {
                final int indexDerZelleInZeile =
                    arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN).indexOf(zelle);
                spalteProjektnummer.put(indexDerZelleInZeile, ersteVierStellenDesZelleninhalts);
              } else {
                excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
                    fehlerlog,
                    ANW_SHEET_NAME,
                    zelleExcelAdresseBlattANW.get(zelle).formatAsString(),
                    "\"" + zellenWert + "\"");
              }
            } catch (final StringIndexOutOfBoundsException oob) {
            }
          }
        }
      }
    }
    return spalteProjektnummer;
  }

  public Map<Integer, String> leseZeileNichtFakturierfaehig(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final Integer anwAngerechneteReisezeitSpalteIndex) {
    final Map<Integer, String> spalteNichtFakturierfaehig = new HashMap<>();

    if (arbeitsnachweisBlatt.get(NICHT_FAKTURIERFAEHIG_ZEILE_INDEX) != null) {

      for (final CellValue zelle : arbeitsnachweisBlatt.get(NICHT_FAKTURIERFAEHIG_ZEILE_INDEX)) {
        if (arbeitsnachweisBlatt.get(NICHT_FAKTURIERFAEHIG_ZEILE_INDEX).indexOf(zelle)
            >= ANW_BLATT_PROJEKTE_START
            && arbeitsnachweisBlatt.get(NICHT_FAKTURIERFAEHIG_ZEILE_INDEX).indexOf(zelle)
            < anwAngerechneteReisezeitSpalteIndex) {
          final String zellenWert = leseNichtFakturierfaehigAusZelle(zelle);
          if (zellenWert.equalsIgnoreCase("n")) {
            try {
              final int indexDerZelleInZeile =
                  arbeitsnachweisBlatt.get(NICHT_FAKTURIERFAEHIG_ZEILE_INDEX).indexOf(zelle);
              spalteNichtFakturierfaehig.put(indexDerZelleInZeile, zellenWert);
            } catch (final StringIndexOutOfBoundsException oob) {
            }
          }
        }
      }
    }
    return spalteNichtFakturierfaehig;
  }

  public Map<Integer, String> reiseZeitenProjekte(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final Integer anwAngerechneteReisezeitSpalteIndex,
      final Integer endeMarkerIndex,
      final Map<Integer, LocalDate> tage) {
    final Map<Integer, String> spalteProjektnummer = new HashMap<>();

    if (arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN) != null) {

      for (final CellValue zelle : arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN)) {
        final String zellenWert = leseProjektnummerAusZelle(zelle);
        if (zellenWert != null
            && zellenWert.length() >= 4
            && istVierstelligeZahl(zellenWert.substring(0, 4))) {
          try {
            final int indexDerZelleInZeile =
                arbeitsnachweisBlatt.get(ZEILE_MIT_PROJEKTANGABEN).indexOf(zelle);
            if (indexDerZelleInZeile > anwAngerechneteReisezeitSpalteIndex
                && indexDerZelleInZeile <= endeMarkerIndex) {
              spalteProjektnummer.put(indexDerZelleInZeile, zellenWert.substring(0, 4));
            }
          } catch (final StringIndexOutOfBoundsException oob) {
          }
        }
      }
    }

    return spalteProjektnummer;
  }

  public Map<Integer, Projekt> rufbereitschaftProjekte(
      final List<List<CellValue>> sonderzeitenBlatt, final Integer sonderzeitenSummenZeileIndex) {
    final Map<Integer, Projekt> zeileProjektnummer = new HashMap<>();

    for (final List<CellValue> zeile : sonderzeitenBlatt) {
      if (zeile.get(SPALTE_MIT_RUFBREITSCAHFT_PROJEKTANGABEN) != null
          && sonderzeitenBlatt.indexOf(zeile) > SONDERZEITEN_START_ZEILE
          && sonderzeitenBlatt.indexOf(zeile) < sonderzeitenSummenZeileIndex) {

        final String zellenWert =
            leseProjektnummerAusZelle(zeile.get(SPALTE_MIT_RUFBREITSCAHFT_PROJEKTANGABEN));

        if (istVierstelligeZahl(zellenWert)) {

          final Projekt projekt = projektService.projektByProjektnummer(zellenWert);
          if (projekt != null) {
            zeileProjektnummer.put(sonderzeitenBlatt.indexOf(zeile), projekt);
          } else {
            excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
                fehlerlog,
                SONDERZEITEN_SHEET_NAME,
                "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1),
                zellenWert);
          }

        } else {
          excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
              fehlerlog,
              SONDERZEITEN_SHEET_NAME,
              "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1),
              zellenWert);
        }
      }
    }

    return zeileProjektnummer;
  }

  private Map<Integer, Projekt> sonderarbeitszeitProjekte(
      final List<List<CellValue>> sonderzeitenBlatt, final Integer sonderzeitenSummenZeileIndex) {
    final Map<Integer, Projekt> zeileProjektnummer = new HashMap<>();

    for (final List<CellValue> zeile : sonderzeitenBlatt) {
      if (zeile.get(SPALTE_MIT_SONDERZEIT_PROJEKTANGABEN) != null
          && sonderzeitenBlatt.indexOf(zeile) > SONDERZEITEN_START_ZEILE
          && sonderzeitenBlatt.indexOf(zeile) < sonderzeitenSummenZeileIndex) {

        final String zellenWert =
            leseProjektnummerAusZelle(zeile.get(SPALTE_MIT_SONDERZEIT_PROJEKTANGABEN));

        if (istVierstelligeZahl(zellenWert)) {

          final Projekt projekt = projektService.projektByProjektnummer(zellenWert);
          if (projekt != null) {
            zeileProjektnummer.put(sonderzeitenBlatt.indexOf(zeile), projekt);
          } else {
            excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
                fehlerlog,
                SONDERZEITEN_SHEET_NAME,
                "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1),
                zellenWert);
          }
        } else {
          excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
              fehlerlog,
              SONDERZEITEN_SHEET_NAME,
              "Zeile " + (sonderzeitenBlatt.indexOf(zeile) + 1),
              zellenWert);
        }
      }
    }

    return zeileProjektnummer;
  }

  private String leseProjektnummerAusZelle(final CellValue cellValue) {
    // Annahme: Zelle mit Projektnummer ist als Zahl formattiert
    String projektnummerGelesen = "";

    if (cellValue != null) {
      projektnummerGelesen = (Integer.valueOf((int) cellValue.getNumberValue()).toString());
      // Ist die Zelle als Text formattiert, konnte keine Projektnummer ermittelt werden.
      // Neue Annahme: Projektnummer ist als Text formattiert
      if (projektnummerGelesen.equals("0")) {

        projektnummerGelesen =
            cellValue.getStringValue() == null ? "" : cellValue.getStringValue().trim();
      }
    }
    return projektnummerGelesen;
  }

  private String leseNichtFakturierfaehigAusZelle(final CellValue cellValue) {
    String nichtFakturierfaehigKennzeichen = "";

    if (cellValue != null) {
      nichtFakturierfaehigKennzeichen =
          cellValue.getStringValue() == null ? "" : cellValue.getStringValue().trim();
    }
    return nichtFakturierfaehigKennzeichen;
  }

  public Map<Integer, LocalDate> tage(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final LocalDate abrechnungsmonat,
      final Integer summenZeileIndex) {
    final Map<Integer, LocalDate> zeileTag = new HashMap<>();

    for (final List<CellValue> zeile : arbeitsnachweisBlatt) {
      if (zeile.get(SPALTE_MIT_TAG_ANGABEN) != null
          && arbeitsnachweisBlatt.indexOf(zeile) < summenZeileIndex) {
        if (zeile.get(SPALTE_MIT_TAG_ANGABEN).getNumberValue() > 0) {
          LocalDate tagDerZeile = null;
          try {
            tagDerZeile =
                LocalDate.of(
                    abrechnungsmonat.getYear(),
                    abrechnungsmonat.getMonthValue(),
                    (int) zeile.get(SPALTE_MIT_TAG_ANGABEN).getNumberValue());
            if (tagDerZeile.getYear() == abrechnungsmonat.getYear()
                && tagDerZeile.getMonth().equals(abrechnungsmonat.getMonth())) {
              zeileTag.put(arbeitsnachweisBlatt.indexOf(zeile), tagDerZeile);
            } else {
              if (excelImportValidator.bereinigeZeileUmNullUndLeereWerte(zeile).size() > 4) {
                excelImportLogWriter.schreibeTagImMonatNichtVorhandenOderUngueltigFehlermeldung(
                    fehlerlog,
                    zelleExcelAdresseBlattANW
                        .get(zeile.get(SPALTE_MIT_TAG_ANGABEN))
                        .formatAsString(),
                    ANW_SHEET_NAME);
              }
            }
          } catch (final DateTimeException dte) {
            // Es gibt Mitarbeiter, die für ihre Projekte Formeln hinterlegen. Somit kann es
            // vorkommen, dass
            // an der obigen Stelle z.B. der 31.06. ausgewertet wird.
          }
        }
      }
    }
    return zeileTag;
  }

  public List<Projektstunde> summiereStundenJeTagUndProjekt(
      final Map<Integer, LocalDate> tage,
      final Map<Integer, String> projekte,
      final Map<Integer, String> fakturierfaehigkeitDerProjekte,
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final Arbeitsnachweis arbeitsnachweis,
      final Integer anwAngerechneteReisezeitSpalteIndex) {
    final List<Projektstunde> summierteStundenJeTagUndProjekt = new ArrayList<>();

    for (final Integer tagIndex : tage.keySet()) {

      final LocalDate tagInBearbeitung = tage.get(tagIndex);

      for (final Integer projektIndex : projekte.keySet()) {

        final String projektNummer = projekte.get(projektIndex);
        final Projekt projekt = projektService.projektByProjektnummer(projektNummer);

        if (projekt != null) {
          if (arbeitsnachweisBlatt.get(tagIndex) != null
              && arbeitsnachweisBlatt.get(tagIndex).get(projektIndex) != null) {

            summiereStundenFuerTagUndProjekt(
                arbeitsnachweisBlatt,
                arbeitsnachweis,
                summierteStundenJeTagUndProjekt,
                fakturierfaehigkeitDerProjekte,
                tagIndex,
                tagInBearbeitung,
                projektIndex,
                projektNummer);
          }
        } else {
          excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
              fehlerlog,
              ANW_SHEET_NAME,
              "Spalte " + excelSpaltenName(projektIndex + 1),
              projektNummer);
        }
      }

      excelImportValidator.pruefeObProjektstundenOhneProjektangabeAngegebenWurden(
          fehlerlog,
          ANW_SHEET_NAME,
          tagIndex,
          anwAngerechneteReisezeitSpalteIndex,
          arbeitsnachweisBlatt.get(tagIndex),
          projekte);
    }
    return summierteStundenJeTagUndProjekt;
  }

  public List<Projektstunde> summiereReisezeitJeTagUndProjekt(
      final Map<Integer, LocalDate> tage,
      final Map<Integer, String> projekte,
      final List<List<CellValue>> arbeitsnachweisBlatt) {
    final List<Projektstunde> summierteStundenJeTagUndProjekt = new ArrayList<>();

    for (final Integer tagIndex : tage.keySet()) {

      final LocalDate tagInBearbeitung = tage.get(tagIndex);

      for (final Integer projektIndex : projekte.keySet()) {

        final String projektNummer = projekte.get(projektIndex);
        final Projekt projekt = projektService.projektByProjektnummer(projektNummer);

        if (projekt != null) {
          if (arbeitsnachweisBlatt.get(tagIndex) != null
              && arbeitsnachweisBlatt.get(tagIndex).get(projektIndex) != null) {

            summiereReisezeitFuerTagUndProjekt(
                arbeitsnachweisBlatt,
                summierteStundenJeTagUndProjekt,
                tagIndex,
                tagInBearbeitung,
                projektIndex,
                projektNummer);
          }
        } else {
          excelImportLogWriter.schreibeProjektnummerUnbekanntFehlermeldung(
              fehlerlog,
              ANW_SHEET_NAME,
              "Spalte " + excelSpaltenName(projektIndex + 1),
              projektNummer);
        }
      }
    }
    return summierteStundenJeTagUndProjekt;
  }

  private void summiereStundenFuerTagUndProjekt(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final Arbeitsnachweis arbeitsnachweis,
      final List<Projektstunde> summierteStundenJeTagUndProjekt,
      final Map<Integer, String> fakturierfaehigkeitDerProjekte,
      final Integer tagIndex,
      final LocalDate tagInBearbeitung,
      final Integer projektIndex,
      final String projektnummer) {

    if (arbeitsnachweisBlatt
        .get(tagIndex)
        .get(projektIndex)
        .getCellType()
        .equals(CellType.NUMERIC)) {
      final double stundenEingabeFuerTagProjekt =
          arbeitsnachweisBlatt.get(tagIndex).get(projektIndex).getNumberValue();
      final Boolean fakturierfaehig =
          fakturierfaehigkeitDerProjekte.get(projektIndex) == null ? Boolean.TRUE : Boolean.FALSE;

      Projektstunde importierteProjektstundenFuerTagProjekt =
          summierteStundenJeTagUndProjekt.stream()
              .filter(
                  stunden ->
                      stunden.getTagVon().isEqual(tagInBearbeitung)
                          && projektService
                          .projektById(stunden.getProjektId())
                          .getProjektnummer()
                          .equals(projektnummer)
                          && stunden.getFakturierfaehig().equals(fakturierfaehig))
              .findFirst()
              .orElse(null);

      if (importierteProjektstundenFuerTagProjekt != null) {
        summierteStundenJeTagUndProjekt.remove(importierteProjektstundenFuerTagProjekt);
        importierteProjektstundenFuerTagProjekt.setAnzahlStunden(
            importierteProjektstundenFuerTagProjekt
                .getAnzahlStunden()
                .add(new BigDecimal(stundenEingabeFuerTagProjekt))
                .setScale(2, RoundingMode.HALF_UP));
        if (importierteProjektstundenFuerTagProjekt.getAnzahlStunden().compareTo(BigDecimal.ZERO)
            != 0) {
          summierteStundenJeTagUndProjekt.add(importierteProjektstundenFuerTagProjekt);
        }
      } else {
        final Projektstunde stunden =
            erstelleNormaleProjektstunden(
                tagInBearbeitung, projektnummer, fakturierfaehig, stundenEingabeFuerTagProjekt);
        if (stunden.getAnzahlStunden().compareTo(BigDecimal.ZERO) != 0) {

          // Urlaub sollte nur in halben bzw. ganzen Tagen gemäß Stellenfaktor angegeben werden.
          // Beispiel - Stellenfaktor 0,80: 3,20 od. 6,40; Stellenfaktor 1,00: 4,00 od. 8,00
          if (projektService
              .projektById(stunden.getProjektId())
              .getProjektnummer()
              .equalsIgnoreCase("9004")) {
            excelImportValidator.pruefeObUrlaubProjektstundenKonsistentSindUndSchreibeGgfMeldung(
                fehlerlog,
                arbeitsnachweis,
                ANW_SHEET_NAME,
                stunden,
                excelSpaltenName(projektIndex + 1) + (tagIndex + 1));
          }

          summierteStundenJeTagUndProjekt.add(stunden);
        }
      }

    } else {
      if (arbeitsnachweisBlatt.get(tagIndex).get(projektIndex).getStringValue() == null
          || !arbeitsnachweisBlatt
          .get(tagIndex)
          .get(projektIndex)
          .getStringValue()
          .trim()
          .isEmpty()) {
        excelImportLogWriter.schreibeEingabeNichtNumerisch(
            fehlerlog,
            zelleExcelAdresseBlattANW
                .get(arbeitsnachweisBlatt.get(tagIndex).get(projektIndex))
                .formatAsString(),
            ANW_SHEET_NAME);
      }
    }
  }

  private void summiereReisezeitFuerTagUndProjekt(
      final List<List<CellValue>> arbeitsnachweisBlatt,
      final List<Projektstunde> summierteReisezeitJeTagUndProjekt,
      final Integer tagIndex,
      final LocalDate tagInBearbeitung,
      final Integer projektIndex,
      final String projektnummer) {

    if (arbeitsnachweisBlatt
        .get(tagIndex)
        .get(projektIndex)
        .getCellType()
        .equals(CellType.NUMERIC)) {
      final double stundenEingabeFuerTagProjekt =
          arbeitsnachweisBlatt.get(tagIndex).get(projektIndex).getNumberValue();

      Projektstunde importierteReisezeitFuerTagProjekt =
          summierteReisezeitJeTagUndProjekt.stream()
              .filter(
                  stunden ->
                      stunden.getTagVon().isEqual(tagInBearbeitung)
                          && projektService
                          .projektById(stunden.getProjektId())
                          .getProjektnummer()
                          .equals(projektnummer))
              .findFirst()
              .orElse(null);

      if (importierteReisezeitFuerTagProjekt != null) {
        summierteReisezeitJeTagUndProjekt.remove(importierteReisezeitFuerTagProjekt);
        importierteReisezeitFuerTagProjekt.setAnzahlStunden(
            importierteReisezeitFuerTagProjekt
                .getAnzahlStunden()
                .add(new BigDecimal(stundenEingabeFuerTagProjekt)));
        summierteReisezeitJeTagUndProjekt.add(importierteReisezeitFuerTagProjekt);
      } else {
        final Projektstunde stunden =
            erstelleReisezeitProjektstunden(
                tagInBearbeitung, projektnummer, stundenEingabeFuerTagProjekt);
        summierteReisezeitJeTagUndProjekt.add(stunden);
        importierteReisezeitFuerTagProjekt = stunden;
      }

    } else {
      excelImportLogWriter.schreibeEingabeNichtNumerisch(
          fehlerlog,
          zelleExcelAdresseBlattANW
              .get(arbeitsnachweisBlatt.get(tagIndex).get(projektIndex))
              .formatAsString(),
          ANW_SHEET_NAME);
    }
  }

  private Projektstunde erstelleNormaleProjektstunden(
      final LocalDate tagInBearbeitung,
      final String projektnummer,
      final Boolean fakturierfaehig,
      final double stundenEingabeFuerTagProjekt) {
    final Projektstunde stunden = new Projektstunde();
    stunden.setTagVon(tagInBearbeitung);
    stunden.setProjektId(projektService.projektByProjektnummer(projektnummer).getId());
    stunden.setAnzahlStunden(
        new BigDecimal(stundenEingabeFuerTagProjekt).setScale(2, RoundingMode.HALF_UP));
    stunden.setBemerkung("autom. importiert");
    stunden.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(NORMAL.getValue()).getId());
    stunden.setId(ThreadLocalRandom.current().nextLong());
    stunden.setFakturierfaehig(fakturierfaehig);
    stunden.setZuletztGeaendertAm(LocalDateTime.now());
    return stunden;
  }

  private Projektstunde erstelleReisezeitProjektstunden(
      final LocalDate tagInBearbeitung,
      final String projektnummer,
      final double reisezeitEingabeFuerTagProjekt) {
    final Projektstunde stunden = new Projektstunde();
    stunden.setTagVon(tagInBearbeitung);
    stunden.setProjektId(projektService.projektByProjektnummer(projektnummer).getId());
    stunden.setAnzahlStunden(new BigDecimal(reisezeitEingabeFuerTagProjekt));
    stunden.setBemerkung("autom. importiert");
    stunden.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(TATSAECHLICHE_REISEZEIT.getValue()).getId());
    stunden.setId(ThreadLocalRandom.current().nextLong());
    stunden.setZuletztGeaendertAm(LocalDateTime.now());
    return stunden;
  }

  private boolean verarbeitungBisherOhneKritischeFehler() {
    return !fehlerlog.stream()
        .anyMatch(
            meldung ->
                meldung.getFehlerklasse() != null && meldung.getFehlerklasse().equals(KRITISCH));
  }

  private boolean istVierstelligeZahl(final String vierstelligerString) {
    return vierstelligerString.matches("^[0-9]{4}$");
  }
}
