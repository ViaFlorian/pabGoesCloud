package de.viadee.pabbackend.enums;

public enum Reports {

  B002_SUBREPORT_RUFBEREITSCHAFTEN_EXCEL("B002SubreportRufbereitschaftenExcel.jrxml",
      "Rufbereitschaften"),
  B002_SUBREPORT_SONDERARBEITSZEITEN_EXCEL("B002SubreportSonderarbeitszeitenExcel.jrxml",
      "Sonderarbeitszeiten"),
  B002_RUFBEREITSCHAFT_SONDERARBEIT_EXCEL(
      "B002RufbereitschaftSonderarbeitExcel.jrxml",
      "B002",
      new Reports[]{
          Reports.B002_SUBREPORT_RUFBEREITSCHAFTEN_EXCEL,
          Reports.B002_SUBREPORT_SONDERARBEITSZEITEN_EXCEL},
      new String[]{"Rufbereitschaften", "Sonderarbeitszeiten"}),

  B004_SUBREPORT_ARBEITSZEIT("B004SubreportArbeitszeit.jrxml", "Arbeitszeit"),
  B004_SUBREPORT_SONDERARBEITSZEIT("B004SubreportSonderarbeitszeit.jrxml", "Sonderarbeitszeit"),
  B004_SUBREPORT_RUFBEREITSCHAFT("B004SubreportRufbereitschaft.jrxml", "Rufbereitschaft"),
  B004_SUBREPORT_REISEKOSTEN_AUSLAGEN("B004SubreportReisekostenAuslagen.jrxml",
      "ReisekostenAuslagen"),
  B004_SUBREPORT_ABWESENHEIT("B004SubreportAbwesenheit.jrxml", "Abwesenheit"),
  B004_ARBEITSNACHWEIS_PRO_MITARBEITER_UND_ABRECHNUNGSMONAT(
      "B004ArbeitszeitProMitarbeiterUndAbrechnungsmonat.jrxml",
      "B004",
      new Reports[]{Reports.B004_SUBREPORT_ARBEITSZEIT, Reports.B004_SUBREPORT_SONDERARBEITSZEIT,
          Reports.B004_SUBREPORT_RUFBEREITSCHAFT, Reports.B004_SUBREPORT_REISEKOSTEN_AUSLAGEN,
          Reports.B004_SUBREPORT_ABWESENHEIT}),

  B005_LOHNARTEN_PRO_MITARBEITER_UND_ABRECHNUNGSMONAT_EXCEL(
      "B005LohnartenProMitarbeiterUndAbrechnungsmonatExcel.jrxml", "B005"),

  B006_SUBREPORT_PROJEKTSTUNDENSUMMEN("B006SubreportProjektstundensummen.jrxml",
      "Projektstundensummen"),
  B006_SUBREPORT_STUNDENKONTO("B006SubreportStundenkonto.jrxml", "Stundenkonto"),
  B006_SUBREPORT_URLAUBSKONTO("B006SubreportUrlaubskonto.jrxml", "Urlaubskonto"),
  B006_ARBEITSZEIT_STUNDENKONTO_PRO_MITARBEITER_JAHR(
      "B006ArbeitszeitStundenkontoProMitarbeiterJahr.jrxml",
      "B006",
      new Reports[]{Reports.B006_SUBREPORT_PROJEKTSTUNDENSUMMEN,
          Reports.B006_SUBREPORT_STUNDENKONTO, Reports.B006_SUBREPORT_URLAUBSKONTO}),

  B007_PROJEKT_KOSTEN_LEISTUNG_PDF("B007ProjektKostenLeistungPdf.jrxml", "B007"),

  B008_PROJEKT_RECHNUNGSVORLAGE_EXCEL("B008ProjektRechnungsvorlageExcel.jrxml", "B008"),

  B009_UEBERSICHT_MITARBEITER_STUNDENKONTEN_EXCEL(
      "B009UebersichtMitarbeiterStundenkontenExcel.jrxml", "B009"),

  B010_UEBERSICHT_MITARBEITER_URLAUBSKONTEN_EXCEL(
      "B010UebersichtMitarbeiterUrlaubskontenExcel.jrxml", "B010"),

  B011_KENNZAHLEN_OE_MONAT_PDF("B011KennzahlenOEMonatPdf.jrxml", "B011"),

  B012_KENNZAHLEN_GESAMT_PDF("B012KennzahlenGesamtPdf.jrxml", "B012"),

  B013_VERGLEICH_FAKTUR_LEISTUNG_OE_KUNDE_PROJEKT_PDF(
      "B013VergleichFakturLeistungOeKundeProjektPdf.jrxml", "B013"),

  B014_SUBREPORT_BUDGETS("B014SubreportBudgets.jrxml", "BudgetsSubreport", null),
  B014_SUBREPORT_FAKTUR("B014SubreportFaktur.jrxml", "FakturSubreport", null),
  B014_SUBREPORT_KOSTEN_LEISTUNGEN("B014SubreportKostenLeistungen.jrxml",
      "KostenLeistungenSubreport", null),
  B014_PROJEKTUEBERSICHT_PDF("B014ProjektuebersichtPdf.jrxml", "B014",
      new Reports[]{Reports.B014_SUBREPORT_BUDGETS, Reports.B014_SUBREPORT_FAKTUR,
          Reports.B014_SUBREPORT_KOSTEN_LEISTUNGEN}),
  B015_VERTEILUNG_ARBEITSZEIT_AUF_OES("B015VerteilungArbeitszeitAufOEs.jrxml", "B015"),
  B016_VERTEILUNG_ARBEITSZEIT_AUF_OES_FUER_PERSONALVORGESETZTE(
      "B016VerteilungArbeitszeitAufOEsFuerPersonalvorgesetzte.jrxml", "B016");

  private final String jasperReportDatei;

  private final String downloadDateiname;

  private Reports[] subreports;

  private String[] sheetNames;

  private String[] tempSheetNames;

  Reports(final String jasperReportDatei, final String downloadDateiname) {
    this.jasperReportDatei = jasperReportDatei;
    this.downloadDateiname = downloadDateiname;
  }

  Reports(final String jasperReportDatei, final String downloadDateiname,
      final Reports[] subreports) {
    this.jasperReportDatei = jasperReportDatei;
    this.downloadDateiname = downloadDateiname;
    this.subreports = subreports;
  }

  Reports(final String jasperReportDatei, final String downloadDateiname,
      final Reports[] subreports,
      final String[] sheetnames) {
    this.jasperReportDatei = jasperReportDatei;
    this.downloadDateiname = downloadDateiname;
    this.subreports = subreports;
    sheetNames = sheetnames;
  }

  public String getJasperReportDatei() {
    return jasperReportDatei;
  }

  /**
   * Gibt den Dateinamen des Downloads *ohne* Dateierweiterung zurück.
   */
  public String getDownloadDateiname() {
    return downloadDateiname;
  }

  public Reports[] getSubreports() {
    return subreports;
  }

  public String[] getSheetNames() {
    return (tempSheetNames == null) ? sheetNames : tempSheetNames;
  }

  public void setTempSheetNames(final String[] tempSheetNames) {
    this.tempSheetNames = tempSheetNames;
  }
}
