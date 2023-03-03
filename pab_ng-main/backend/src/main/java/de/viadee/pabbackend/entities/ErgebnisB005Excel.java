package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB005Excel {

  private static final long serialVersionUID = -1773329433792139680L;

  private String personalnummer;

  private BigDecimal wert;

  private Integer bearbeitungsschluessel;

  private String kostenstelle;

  private Integer lohnart;

  private String lohnfaktor;

  private String bemerkung;

  private String mitarbeiterFullname;

  private String lohnartBezeichnung;

  private BigDecimal eur;

  private BigDecimal std;

  public String getPersonalnummer() {
    return personalnummer;
  }

  public void setPersonalnummer(String personalnummer) {
    this.personalnummer = personalnummer;
  }

  public BigDecimal getWert() {
    return wert;
  }

  public void setWert(BigDecimal wert) {
    this.wert = wert;
  }

  public Integer getBearbeitungsschluessel() {
    return bearbeitungsschluessel;
  }

  public void setBearbeitungsschluessel(Integer bearbeitungsschluessel) {
    this.bearbeitungsschluessel = bearbeitungsschluessel;
  }

  public String getKostenstelle() {
    return kostenstelle;
  }

  public void setKostenstelle(String kostenstelle) {
    this.kostenstelle = kostenstelle;
  }

  public Integer getLohnart() {
    return lohnart;
  }

  public void setLohnart(Integer lohnart) {
    this.lohnart = lohnart;
  }

  public String getLohnfaktor() {
    return lohnfaktor;
  }

  public void setLohnfaktor(String lohnfaktor) {
    this.lohnfaktor = lohnfaktor;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public String getMitarbeiterFullname() {
    return mitarbeiterFullname;
  }

  public void setMitarbeiterFullname(String mitarbeiterFullname) {
    this.mitarbeiterFullname = mitarbeiterFullname;
  }

  public String getLohnartBezeichnung() {
    return lohnartBezeichnung;
  }

  public void setLohnartBezeichnung(String lohnartBezeichnung) {
    this.lohnartBezeichnung = lohnartBezeichnung;
  }

  public BigDecimal getEur() {
    return eur;
  }

  public void setEur(BigDecimal eur) {
    this.eur = eur;
  }

  public BigDecimal getStd() {
    return std;
  }

  public void setStd(BigDecimal std) {
    this.std = std;
  }


}
