package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ErgebnisB004Pdf {

  private static final long serialVersionUID = -6761538436991033386L;

  private List<ErgebnisB004ArbeitszeitPdf> listeArbeitszeiten;

  private List<ErgebnisB004SonderarbeitszeitPdf> listeSonderarbeitszeiten;

  private List<ErgebnisB004RufbereitschaftPdf> listeRufbereitschaften;

  private List<ErgebnisB004ReisekostenAuslagenPdf> listeReisekostenAuslagen;

  private List<ErgebnisB004AbwesenheitPdf> listeAbwesenheiten;

  private Mitarbeiter mitarbeiter;

  private BigDecimal sollstunden;

  private BigDecimal auszahlung;

  private String smartphone;

  private BigDecimal verbindungsentgelt;

  private BigDecimal jobticket;

  public BigDecimal getVerbindungsentgelt() {
    return verbindungsentgelt;
  }

  public void setVerbindungsentgelt(BigDecimal verbindungsentgelt) {
    this.verbindungsentgelt = verbindungsentgelt;
  }

  public BigDecimal getJobticket() {
    return jobticket;
  }

  public void setJobticket(BigDecimal jobticket) {
    this.jobticket = jobticket;
  }

  public Integer getMitarbeiterPersonalnummer() {
    return mitarbeiter == null ? 0 : mitarbeiter.getPersonalNr();
  }

  public String getMitarbeiterFullName() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public List<ErgebnisB004ArbeitszeitPdf> getListeArbeitszeiten() {
    return listeArbeitszeiten;
  }

  public void setListeArbeitszeiten(final List<ErgebnisB004ArbeitszeitPdf> listeArbeitszeiten) {
    this.listeArbeitszeiten = listeArbeitszeiten;
  }

  public List<ErgebnisB004SonderarbeitszeitPdf> getListeSonderarbeitszeiten() {
    return listeSonderarbeitszeiten;
  }

  public void setListeSonderarbeitszeiten(
      List<ErgebnisB004SonderarbeitszeitPdf> listeSonderarbeitszeiten) {
    this.listeSonderarbeitszeiten = listeSonderarbeitszeiten;
  }

  public List<ErgebnisB004RufbereitschaftPdf> getListeRufbereitschaften() {
    return listeRufbereitschaften;
  }

  public void setListeRufbereitschaften(
      List<ErgebnisB004RufbereitschaftPdf> listeRufbereitschaften) {
    this.listeRufbereitschaften = listeRufbereitschaften;
  }

  public List<ErgebnisB004ReisekostenAuslagenPdf> getListeReisekostenAuslagen() {
    return listeReisekostenAuslagen;
  }

  public void setListeReisekostenAuslagen(
      List<ErgebnisB004ReisekostenAuslagenPdf> listeReisekostenAuslagen) {
    this.listeReisekostenAuslagen = listeReisekostenAuslagen;
  }

  public List<ErgebnisB004AbwesenheitPdf> getListeAbwesenheiten() {
    return listeAbwesenheiten;
  }

  public void setListeAbwesenheiten(List<ErgebnisB004AbwesenheitPdf> listeAbwesenheiten) {
    this.listeAbwesenheiten = listeAbwesenheiten;
  }

  public BigDecimal getSollstunden() {
    return sollstunden;
  }

  public void setSollstunden(BigDecimal sollstunden) {
    this.sollstunden = sollstunden;
  }

  public BigDecimal getAuszahlung() {
    return auszahlung;
  }

  public void setAuszahlung(BigDecimal auszahlung) {
    this.auszahlung = auszahlung;
  }

  public String getSmartphone() {
    return smartphone;
  }

  public void setSmartphone(String smartphone) {
    this.smartphone = smartphone;
  }

  public JRBeanCollectionDataSource getListeArbeitszeitenJR() {
    return new JRBeanCollectionDataSource(listeArbeitszeiten);
  }

  public JRBeanCollectionDataSource getListeSonderarbeitszeitenJR() {
    return new JRBeanCollectionDataSource(listeSonderarbeitszeiten);
  }

  public JRBeanCollectionDataSource getListeRufbereitschaftenJR() {
    return new JRBeanCollectionDataSource(listeRufbereitschaften);
  }


  public JRBeanCollectionDataSource getListeReisekostenAuslagenJR() {
    return new JRBeanCollectionDataSource(listeReisekostenAuslagen);
  }

  public JRBeanCollectionDataSource getListeAbwesenheitenJR() {
    return new JRBeanCollectionDataSource(listeAbwesenheiten);
  }

}
