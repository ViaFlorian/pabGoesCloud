package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ProjektabrechnungLeistungen {

  private Mitarbeiter mitarbeiter;

  private BigDecimal projektzeit;

  private BigDecimal reisezeit;

  private BigDecimal reisekosten;

  private BigDecimal rufbereitschaft;

  private BigDecimal sonderarbeitszeit;

  private BigDecimal sonstigeProjektkosten;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(final Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public BigDecimal getProjektzeit() {
    return projektzeit;
  }

  public void setProjektzeit(final BigDecimal projektzeit) {
    this.projektzeit = projektzeit;
  }

  public BigDecimal getReisezeit() {
    return reisezeit;
  }

  public void setReisezeit(final BigDecimal reisezeit) {
    this.reisezeit = reisezeit;
  }

  public BigDecimal getReisekosten() {
    return reisekosten;
  }

  public void setReisekosten(final BigDecimal reisekosten) {
    this.reisekosten = reisekosten;
  }

  public BigDecimal getRufbereitschaft() {
    return rufbereitschaft;
  }

  public void setRufbereitschaft(final BigDecimal rufbereitschaft) {
    this.rufbereitschaft = rufbereitschaft;
  }

  public BigDecimal getSonderarbeitszeit() {
    return sonderarbeitszeit;
  }

  public void setSonderarbeitszeit(final BigDecimal sonderarbeitszeit) {
    this.sonderarbeitszeit = sonderarbeitszeit;
  }

  public BigDecimal getSonstigeProjektkosten() {
    return sonstigeProjektkosten;
  }

  public void setSonstigeProjektkosten(final BigDecimal sonstigeProjektkosten) {
    this.sonstigeProjektkosten = sonstigeProjektkosten;
  }

  public Integer getPersonalnummerForGrid() {
    return mitarbeiter == null ? null : mitarbeiter.getPersonalNr();
  }

  public String getMitarbeiterFullnameForGrid() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }
}
