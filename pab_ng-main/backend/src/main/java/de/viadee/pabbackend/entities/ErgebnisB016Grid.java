package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB016Grid {

  private Mitarbeiter mitarbeiter;

  private BigDecimal stundenOEs;

  private BigDecimal stundenVertrieb;

  private BigDecimal stundenFuE;

  private BigDecimal stundenBd;

  private BigDecimal stundenPersonal;

  private BigDecimal stundenFinanzen;

  private BigDecimal stundenSaldoZumZeitraumEnde;

  private BigDecimal urlaubSaldoZumZeitraumEnde;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(final Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullName() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public BigDecimal getStundenOEs() {
    return stundenOEs;
  }

  public void setStundenOEs(final BigDecimal stundenOEs) {
    this.stundenOEs = stundenOEs;
  }

  public BigDecimal getStundenVertrieb() {
    return stundenVertrieb;
  }

  public void setStundenVertrieb(final BigDecimal stundenVertrieb) {
    this.stundenVertrieb = stundenVertrieb;
  }

  public BigDecimal getStundenFuE() {
    return stundenFuE;
  }

  public void setStundenFuE(final BigDecimal stundenFuE) {
    this.stundenFuE = stundenFuE;
  }

  public BigDecimal getStundenBd() {
    return stundenBd;
  }

  public void setStundenBd(final BigDecimal stundenBd) {
    this.stundenBd = stundenBd;
  }

  public BigDecimal getStundenPersonal() {
    return stundenPersonal;
  }

  public void setStundenPersonal(final BigDecimal stundenPersonal) {
    this.stundenPersonal = stundenPersonal;
  }

  public BigDecimal getStundenFinanzen() {
    return stundenFinanzen;
  }

  public void setStundenFinanzen(final BigDecimal stundenFinanzen) {
    this.stundenFinanzen = stundenFinanzen;
  }

  public BigDecimal getStundenSaldoZumZeitraumEnde() {
    return stundenSaldoZumZeitraumEnde;
  }

  public void setStundenSaldoZumZeitraumEnde(final BigDecimal stundenSaldoZumZeitraumEnde) {
    this.stundenSaldoZumZeitraumEnde = stundenSaldoZumZeitraumEnde;
  }

  public BigDecimal getUrlaubSaldoZumZeitraumEnde() {
    return urlaubSaldoZumZeitraumEnde;
  }

  public void setUrlaubSaldoZumZeitraumEnde(final BigDecimal urlaubSaldoZumZeitraumEnde) {
    this.urlaubSaldoZumZeitraumEnde = urlaubSaldoZumZeitraumEnde;
  }
}
