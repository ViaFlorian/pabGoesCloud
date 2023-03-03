package de.viadee.pabbackend.entities;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

public class ErgebnisB008Uebersicht {

  @Column("Projektnummer")
  private String projektnummer;
  @Column("IstAktiv")
  private boolean projektIstAktiv;
  @Column("Projektbezeichnung")
  private String projektbezeichnung;
  @Column("Jahr")
  private Integer jahr;
  @Column("Monat")
  private Integer monat;
  @Column("Kosten")
  private BigDecimal kosten;
  @Column("Leistungen")
  private BigDecimal leistungen;

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(final BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getLeistungen() {
    return leistungen;
  }

  public void setLeistungen(final BigDecimal leistungen) {
    this.leistungen = leistungen;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public boolean getProjektIstAktiv() {
    return projektIstAktiv;
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

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(final Integer monat) {
    this.monat = monat;
  }

}
