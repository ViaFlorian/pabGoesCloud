package de.viadee.pabbackend.entities;


import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB007Uebersicht {

  private static final long serialVersionUID = 6027175424914837092L;

  @Column("Projektnummer")
  private String projektnummer;
  @Column("IstAktiv")
  private boolean projektIstAktiv;
  @Column("Bezeichnung")
  private String projektbezeichnung;
  @Column("Jahr")
  private Integer jahr;
  @Column("Monat")
  private Integer monat;
  @Column("Kosten")
  private BigDecimal kosten;
  @Column("Leistung")
  private BigDecimal leistungen;

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public boolean isProjektIstAktiv() {
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

  public void setJahr(Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(Integer monat) {
    this.monat = monat;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getLeistungen() {
    return leistungen;
  }

  public void setLeistungen(BigDecimal leistungen) {
    this.leistungen = leistungen;
  }
}
