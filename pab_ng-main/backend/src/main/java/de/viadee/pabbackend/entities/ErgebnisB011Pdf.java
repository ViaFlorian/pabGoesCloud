package de.viadee.pabbackend.entities;


import de.viadee.pabbackend.enums.Projekttyp;
import java.math.BigDecimal;

public class ErgebnisB011Pdf {

  private static final long serialVersionUID = 6027175424914838092L;

  private Integer projektnummer;

  private String projektbezeichnung;

  private Projekttyp projekttyp;

  private BigDecimal fertigstellungsgrad;

  private BigDecimal anzahlStunden;

  private BigDecimal leistung;

  private BigDecimal honorarLeistung;

  private BigDecimal stundenMitStundensatzNull;

  private BigDecimal durchschnittlicherStundensatz;

  private BigDecimal kosten;

  private BigDecimal honorarKosten;

  private BigDecimal reiseKosten;

  private BigDecimal uebrigeKosten;

  private BigDecimal deckungsbeitrag1Euro;

  private BigDecimal deckungsbeitrag1Prozent;

  private BigDecimal planAnzahlStundenJahr;

  private BigDecimal planLeistungJahr;

  private BigDecimal planKostenJahr;

  private BigDecimal planDeckungsbeitrag1Jahr;

  private BigDecimal planAnzahlStundenAnteil;

  private BigDecimal planLeistungAnteil;

  private BigDecimal planKostenAnteil;

  private BigDecimal planDeckungsbeitrag1Anteil;

  private Abrechnungsmonat abrechnungsmonat;

  private boolean internesProjekt;

  private boolean jahreswerte;

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public Boolean getJahreswerte() {
    return jahreswerte;
  }

  public void setJahreswerte(Boolean jahreswerte) {
    this.jahreswerte = jahreswerte;
  }

  public Boolean getMonatAbgeschlossen() {
    return abrechnungsmonat.isAbgeschlossen();
  }

  public Boolean getInternesProjekt() {
    return internesProjekt;
  }

  public void setInternesProjekt(Boolean internesProjekt) {
    this.internesProjekt = internesProjekt;
  }

  public Integer getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(Integer projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getProjektbezeichnung() {
    return projektbezeichnung;
  }

  public void setProjektbezeichnung(String projektbezeichnung) {
    this.projektbezeichnung = projektbezeichnung;
  }

  public Projekttyp getProjekttyp() {
    return projekttyp;
  }

  public void setProjekttyp(Projekttyp projekttyp) {
    this.projekttyp = projekttyp;
  }

  public String getProjekttypForPdf() {
    return projekttyp.toString();
  }

  public String getAbrechnungsmonatForPdf() {

    return abrechnungsmonat.toString();
  }

  public BigDecimal getAnzahlStunden() {
    return anzahlStunden;
  }

  public void setAnzahlStunden(BigDecimal anzahlStunden) {
    this.anzahlStunden = anzahlStunden;
  }

  public BigDecimal getLeistung() {
    return leistung;
  }

  public void setLeistung(BigDecimal leistung) {
    this.leistung = leistung;
  }

  public BigDecimal getHonorarLeistung() {
    return honorarLeistung;
  }

  public void setHonorarLeistung(BigDecimal honorarLeistung) {
    this.honorarLeistung = honorarLeistung;
  }

  public BigDecimal getStundenMitStundensatzNull() {
    return stundenMitStundensatzNull;
  }

  public void setStundenMitStundensatzNull(BigDecimal stundenMitStundensatzNull) {
    this.stundenMitStundensatzNull = stundenMitStundensatzNull;
  }

  public BigDecimal getDurchschnittlicherStundensatz() {
    return durchschnittlicherStundensatz;
  }

  public void setDurchschnittlicherStundensatz(BigDecimal durchschnittlicherStundensatz) {
    this.durchschnittlicherStundensatz = durchschnittlicherStundensatz;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getHonorarKosten() {
    return honorarKosten;
  }

  public void setHonorarKosten(BigDecimal honorarKosten) {
    this.honorarKosten = honorarKosten;
  }

  public BigDecimal getReiseKosten() {
    return reiseKosten;
  }

  public void setReiseKosten(BigDecimal reiseKosten) {
    this.reiseKosten = reiseKosten;
  }

  public BigDecimal getUebrigeKosten() {
    return uebrigeKosten;
  }

  public void setUebrigeKosten(BigDecimal uebrigeKosten) {
    this.uebrigeKosten = uebrigeKosten;
  }

  public BigDecimal getDeckungsbeitrag1Euro() {
    return deckungsbeitrag1Euro;
  }

  public void setDeckungsbeitrag1Euro(BigDecimal deckungsbeitrag1Euro) {
    this.deckungsbeitrag1Euro = deckungsbeitrag1Euro;
  }

  public BigDecimal getDeckungsbeitrag1Prozent() {
    return deckungsbeitrag1Prozent;
  }

  public void setDeckungsbeitrag1Prozent(BigDecimal deckungsbeitrag1Prozent) {
    this.deckungsbeitrag1Prozent = deckungsbeitrag1Prozent;
  }

  public BigDecimal getPlanAnzahlStundenJahr() {
    return planAnzahlStundenJahr;
  }

  public void setPlanAnzahlStundenJahr(BigDecimal planAnzahlStundenJahr) {
    this.planAnzahlStundenJahr = planAnzahlStundenJahr;
  }

  public BigDecimal getPlanLeistungJahr() {
    return planLeistungJahr;
  }

  public void setPlanLeistungJahr(BigDecimal planLeistungJahr) {
    this.planLeistungJahr = planLeistungJahr;
  }

  public BigDecimal getPlanKostenJahr() {
    return planKostenJahr;
  }

  public void setPlanKostenJahr(BigDecimal planKostenJahr) {
    this.planKostenJahr = planKostenJahr;
  }

  public BigDecimal getPlanDeckungsbeitrag1Jahr() {
    return planDeckungsbeitrag1Jahr;
  }

  public void setPlanDeckungsbeitrag1Jahr(BigDecimal planDeckungsbeitrag1Jahr) {
    this.planDeckungsbeitrag1Jahr = planDeckungsbeitrag1Jahr;
  }

  public BigDecimal getPlanAnzahlStundenAnteil() {
    return planAnzahlStundenAnteil;
  }

  public void setPlanAnzahlStundenAnteil(BigDecimal planAnzahlStundenAnteil) {
    this.planAnzahlStundenAnteil = planAnzahlStundenAnteil;
  }

  public BigDecimal getPlanLeistungAnteil() {
    return planLeistungAnteil;
  }

  public void setPlanLeistungAnteil(BigDecimal planLeistungAnteil) {
    this.planLeistungAnteil = planLeistungAnteil;
  }

  public BigDecimal getPlanKostenAnteil() {
    return planKostenAnteil;
  }

  public void setPlanKostenAnteil(BigDecimal planKostenAnteil) {
    this.planKostenAnteil = planKostenAnteil;
  }

  public BigDecimal getPlanDeckungsbeitrag1Anteil() {
    return planDeckungsbeitrag1Anteil;
  }

  public void setPlanDeckungsbeitrag1Anteil(BigDecimal planDeckungsbeitrag1Anteil) {
    this.planDeckungsbeitrag1Anteil = planDeckungsbeitrag1Anteil;
  }


  public Abrechnungsmonat getAbrechnungsmonat() {
    return abrechnungsmonat;
  }

  public void setAbrechnungsmonat(Abrechnungsmonat abrechnungsmonat) {
    this.abrechnungsmonat = abrechnungsmonat;
  }
}
