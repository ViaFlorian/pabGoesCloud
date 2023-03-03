package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ErgebnisB014Pdf {

  private static final long serialVersionUID = 4409572498916143587L;

  private Long projektId;
  private String projektnummer;
  private String projektBezeichnung;
  private String projekttyp;
  private String kunde;
  private String oeBezeichnung;
  private String oeBeschreibung;
  private String oeVerantwortlicher;
  private String projektVerantwortlicher;
  private String debitorennummer;
  private String projektStart;
  private String projektEnde;

  private BigDecimal fertigstellungsgrad;
  private BigDecimal monatFaktLeistung;
  private BigDecimal monatKosten;
  private BigDecimal monatRechnungssumme;
  private BigDecimal jahrFaktLeistung;
  private BigDecimal jahrKosten;
  private BigDecimal jahrRechnungssumme;
  private BigDecimal gesamtFaktLeistung;
  private BigDecimal gesamtKosten;
  private BigDecimal gesamtBudget;
  private BigDecimal gesamtRechnungssumme;
  private BigDecimal gesamtNichtBudgetrelevant;

  private List<ErgebnisB014Budget> listeBudgets;
  private List<ErgebnisB014Faktur> listeFaktur;
  private List<ErgebnisB014KostenLeistungen> listeKostenLeistungen;

  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(Long projektId) {
    this.projektId = projektId;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getProjektBezeichnung() {
    return projektBezeichnung;
  }

  public void setProjektBezeichnung(String projektBezeichnung) {
    this.projektBezeichnung = projektBezeichnung;
  }

  public String getProjekttyp() {
    return projekttyp;
  }

  public void setProjekttyp(String projekttyp) {
    this.projekttyp = projekttyp;
  }

  public String getKunde() {
    return kunde;
  }

  public void setKunde(String kunde) {
    this.kunde = kunde;
  }

  public String getOeBezeichnung() {
    return oeBezeichnung;
  }

  public void setOeBezeichnung(String oeBezeichnung) {
    this.oeBezeichnung = oeBezeichnung;
  }

  public String getOeBeschreibung() {
    return oeBeschreibung;
  }

  public void setOeBeschreibung(String oeBeschreibung) {
    this.oeBeschreibung = oeBeschreibung;
  }

  public String getOeVerantwortlicher() {
    return oeVerantwortlicher;
  }

  public void setOeVerantwortlicher(String oeVerantwortlicher) {
    this.oeVerantwortlicher = oeVerantwortlicher;
  }

  public String getProjektVerantwortlicher() {
    return projektVerantwortlicher;
  }

  public void setProjektVerantwortlicher(String projektVerantwortlicher) {
    this.projektVerantwortlicher = projektVerantwortlicher;
  }

  public String getDebitorennummer() {
    return debitorennummer;
  }

  public void setDebitorennummer(String debitorennummer) {
    this.debitorennummer = debitorennummer;
  }

  public String getProjektStart() {
    return projektStart;
  }

  public void setProjektStart(String projektStart) {
    this.projektStart = projektStart;
  }

  public String getProjektEnde() {
    return projektEnde;
  }

  public void setProjektEnde(String projektEnde) {
    this.projektEnde = projektEnde;
  }

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public BigDecimal getMonatFaktLeistung() {
    return monatFaktLeistung;
  }

  public void setMonatFaktLeistung(BigDecimal monatFaktLeistung) {
    this.monatFaktLeistung = monatFaktLeistung;
  }

  public BigDecimal getMonatKosten() {
    return monatKosten;
  }

  public void setMonatKosten(BigDecimal monatKosten) {
    this.monatKosten = monatKosten;
  }

  public BigDecimal getMonatRechnungssumme() {
    return monatRechnungssumme;
  }

  public void setMonatRechnungssumme(BigDecimal monatRechnungssumme) {
    this.monatRechnungssumme = monatRechnungssumme;
  }

  public BigDecimal getJahrFaktLeistung() {
    return jahrFaktLeistung;
  }

  public void setJahrFaktLeistung(BigDecimal jahrFaktLeistung) {
    this.jahrFaktLeistung = jahrFaktLeistung;
  }

  public BigDecimal getJahrKosten() {
    return jahrKosten;
  }

  public void setJahrKosten(BigDecimal jahrKosten) {
    this.jahrKosten = jahrKosten;
  }

  public BigDecimal getJahrRechnungssumme() {
    return jahrRechnungssumme;
  }

  public void setJahrRechnungssumme(BigDecimal jahrRechnungssumme) {
    this.jahrRechnungssumme = jahrRechnungssumme;
  }

  public BigDecimal getGesamtFaktLeistung() {
    return gesamtFaktLeistung;
  }

  public void setGesamtFaktLeistung(BigDecimal gesamtFaktLeistung) {
    this.gesamtFaktLeistung = gesamtFaktLeistung;
  }

  public BigDecimal getGesamtKosten() {
    return gesamtKosten;
  }

  public void setGesamtKosten(BigDecimal gesamtKosten) {
    this.gesamtKosten = gesamtKosten;
  }

  public BigDecimal getGesamtBudget() {
    return gesamtBudget;
  }

  public void setGesamtBudget(BigDecimal gesamtBudget) {
    this.gesamtBudget = gesamtBudget;
  }

  public BigDecimal getGesamtRechnungssumme() {
    return gesamtRechnungssumme;
  }

  public void setGesamtRechnungssumme(BigDecimal gesamtRechnungssumme) {
    this.gesamtRechnungssumme = gesamtRechnungssumme;
  }

  public BigDecimal getGesamtNichtBudgetrelevant() {
    return gesamtNichtBudgetrelevant;
  }

  public void setGesamtNichtBudgetrelevant(BigDecimal gesamtNichtBudgetrelevant) {
    this.gesamtNichtBudgetrelevant = gesamtNichtBudgetrelevant;
  }

  public JRBeanCollectionDataSource getListeBudgets() {
    return new JRBeanCollectionDataSource(listeBudgets);
  }

  public void setListeBudgets(List<ErgebnisB014Budget> listeBudgets) {
    this.listeBudgets = listeBudgets;
  }

  public JRBeanCollectionDataSource getListeFaktur() {
    return new JRBeanCollectionDataSource(listeFaktur);
  }

  public void setListeFaktur(List<ErgebnisB014Faktur> listeFaktur) {
    this.listeFaktur = listeFaktur;
  }

  public JRBeanCollectionDataSource getListeKostenLeistungen() {
    return new JRBeanCollectionDataSource(listeKostenLeistungen);
  }

  public void setListeKostenLeistungen(List<ErgebnisB014KostenLeistungen> listeKostenLeistungen) {
    this.listeKostenLeistungen = listeKostenLeistungen;
  }
}
