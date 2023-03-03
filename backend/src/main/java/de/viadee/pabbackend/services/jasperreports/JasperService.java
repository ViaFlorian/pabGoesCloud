package de.viadee.pabbackend.services.jasperreports;

import de.viadee.pabbackend.entities.ReportGenerierungErgebnis;
import de.viadee.pabbackend.enums.Reports;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class JasperService {

  public ReportGenerierungErgebnis ladeExcelReport(final Reports jasperReport,
      final Collection<?> daten,
      final Map<String, Object> params,
      final ByteArrayOutputStream outputStream) {

    try {

      final ClassPathResource jrxmlDatei = new ClassPathResource(
          "reports/" + jasperReport.getJasperReportDatei());

      final JasperReport report = JasperCompileManager.compileReport(jrxmlDatei.getInputStream());

      final Map<String, Object> reportParameter = loadSubreportsToParameterlist(jasperReport);
      if (params != null) {
        reportParameter.putAll(params);
      }

      final JasperPrint reportMitDaten = JasperFillManager.fillReport(report, reportParameter,
          new JRBeanCollectionDataSource(daten));
      final JRXlsxExporter excelExporter = new JRXlsxExporter();
      excelExporter.setExporterInput(new SimpleExporterInput(reportMitDaten));
      excelExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

      final SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
      // Keine Seitenumbrüche
      configuration.setOnePagePerSheet(true);
      // Weißen Hintergrund nicht erzwingen
      configuration.setWhitePageBackground(false);
      // Abstände zwischen Elementen sollen NICHT in leere Zellen umgewandelt werden
      configuration.setRemoveEmptySpaceBetweenColumns(true);
      configuration.setRemoveEmptySpaceBetweenRows(true);
      // Formatierung der Zelle anhand des Datentyps
      configuration.setDetectCellType(true);
      // Seitenränder ignorieren um Leerzeilen/-spalten zu vermeiden
      configuration.setIgnorePageMargins(true);

      if (jasperReport.getSheetNames() != null) {
        configuration.setSheetNames(jasperReport.getSheetNames());
      }

      excelExporter.exportReport();

    } catch (JRException | IOException e) {
      return new ReportGenerierungErgebnis(true,
          "\"Es ist ein schwerer Ausnahmefehler bei der Berichterstellung aufgetreten: \" + e.getMessage()");
    }

    return new ReportGenerierungErgebnis(false, null);
  }

  public ReportGenerierungErgebnis ladePdfReport(final Reports jasperReport,
      final Collection<?> daten,
      final Map<String, Object> params,
      final ByteArrayOutputStream outputStream) {

    try {

      final ClassPathResource jrxmlDatei = new ClassPathResource(
          "reports/" + jasperReport.getJasperReportDatei());

      final JasperReport report = JasperCompileManager.compileReport(jrxmlDatei.getInputStream());

      final Map<String, Object> reportParameter = loadSubreportsToParameterlist(jasperReport);
      if (params != null) {
        reportParameter.putAll(params);
      }

      final JasperPrint reportMitDaten = JasperFillManager.fillReport(report, reportParameter,
          new JRBeanCollectionDataSource(daten));

      final JRPdfExporter pdfExporter = new JRPdfExporter();
      pdfExporter.setExporterInput(new SimpleExporterInput(reportMitDaten));
      pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
      pdfExporter.exportReport();

    } catch (JRException | IOException e) {
      return new ReportGenerierungErgebnis(true,
          "\"Es ist ein schwerer Ausnahmefehler bei der Berichterstellung aufgetreten: \" + e.getMessage()");
    }

    return new ReportGenerierungErgebnis(false, null);
  }

  public Map<String, Object> loadSubreportsToParameterlist(final Reports report)
      throws JRException {
    final Map<String, Object> params = new HashMap<>();
    if (report.getSubreports() != null) {
      for (final Reports subreport : report.getSubreports()) {
        try {
          params.put(subreport.getDownloadDateiname(), loadReport(subreport));
        } catch (JRException | IOException e) {
          e.printStackTrace();
        }
      }
    }
    return params;
  }

  public JasperReport loadReport(final Reports report) throws JRException, IOException {
    final ClassPathResource res = new ClassPathResource("reports/" + report.getJasperReportDatei());
    return JasperCompileManager.compileReport(res.getInputStream());
  }

}
