package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB014Grid {

  private String projektnummer;
  private boolean projektIstAktiv;
  private String projektbezeichnung;
  private BigDecimal kosten;
  private BigDecimal berechneteLeistung;
  private BigDecimal budget;
  private BigDecimal rechnungssumme;


  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public void setProjektIstAktiv(boolean projektIstAktiv) {
    this.projektIstAktiv = projektIstAktiv;
  }

  public String getProjektbezeichnung() {
    return projektbezeichnung;
  }

  public void setProjektbezeichnung(String projektbezeichnung) {
    this.projektbezeichnung = projektbezeichnung;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getBerechneteLeistung() {
    return berechneteLeistung;
  }

  public void setBerechneteLeistung(BigDecimal berechneteLeistung) {
    this.berechneteLeistung = berechneteLeistung;
  }

  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal budget) {
    this.budget = budget;
  }

  public BigDecimal getRechnungssumme() {
    return rechnungssumme;
  }

  public void setRechnungssumme(BigDecimal rechnungssumme) {
    this.rechnungssumme = rechnungssumme;
  }
}
