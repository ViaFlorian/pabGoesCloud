package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB009 {

  private Mitarbeiter mitarbeiter;

  private String mitarbeiterTyp;

  private Integer jahr;

  private Integer monat;

  private BigDecimal monatVortrag;

  private BigDecimal monatSoll;

  private BigDecimal monatIst;

  private BigDecimal monatKorrektur;

  private BigDecimal monatAuszahlung;

  private BigDecimal monatUebertrag;

  private BigDecimal jahrVortrag;

  private BigDecimal jahrSoll;

  private BigDecimal jahrIst;

  private BigDecimal jahrKorrektur;

  private BigDecimal jahrAuszahlung;

  private BigDecimal jahrUebertrag;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullname() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public BigDecimal getMonatVortrag() {
    return monatVortrag;
  }

  public void setMonatVortrag(final BigDecimal monatVortrag) {
    this.monatVortrag = monatVortrag;
  }

  public BigDecimal getMonatSoll() {
    return monatSoll;
  }

  public void setMonatSoll(final BigDecimal monatSoll) {
    this.monatSoll = monatSoll;
  }

  public BigDecimal getMonatIst() {
    return monatIst;
  }

  public void setMonatIst(final BigDecimal monatIst) {
    this.monatIst = monatIst;
  }

  public BigDecimal getMonatKorrektur() {
    return monatKorrektur;
  }

  public void setMonatKorrektur(final BigDecimal monatKorrektur) {
    this.monatKorrektur = monatKorrektur;
  }

  public BigDecimal getMonatAuszahlung() {
    return monatAuszahlung;
  }

  public void setMonatAuszahlung(final BigDecimal monatAuszahlung) {
    this.monatAuszahlung = monatAuszahlung;
  }

  public BigDecimal getMonatUebertrag() {
    return monatUebertrag;
  }

  public void setMonatUebertrag(final BigDecimal monatUebertrag) {
    this.monatUebertrag = monatUebertrag;
  }

  public BigDecimal getJahrVortrag() {
    return jahrVortrag;
  }

  public void setJahrVortrag(final BigDecimal jahrVortrag) {
    this.jahrVortrag = jahrVortrag;
  }

  public BigDecimal getJahrSoll() {
    return jahrSoll;
  }

  public void setJahrSoll(final BigDecimal jahrSoll) {
    this.jahrSoll = jahrSoll;
  }

  public BigDecimal getJahrIst() {
    return jahrIst;
  }

  public void setJahrIst(final BigDecimal jahrIst) {
    this.jahrIst = jahrIst;
  }

  public BigDecimal getJahrKorrektur() {
    return jahrKorrektur;
  }

  public void setJahrKorrektur(final BigDecimal jahrKorrektur) {
    this.jahrKorrektur = jahrKorrektur;
  }

  public BigDecimal getJahrAuszahlung() {
    return jahrAuszahlung;
  }

  public void setJahrAuszahlung(final BigDecimal jahrAuszahlung) {
    this.jahrAuszahlung = jahrAuszahlung;
  }

  public BigDecimal getJahrUebertrag() {
    return jahrUebertrag;
  }

  public void setJahrUebertrag(final BigDecimal jahrUebertrag) {
    this.jahrUebertrag = jahrUebertrag;
  }

  public String getMitarbeiterTyp() {
    return mitarbeiterTyp;
  }

  public void setMitarbeiterTyp(final String mitarbeiterTyp) {
    this.mitarbeiterTyp = mitarbeiterTyp;
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

  public Integer getPersonalnummer() {
    return mitarbeiter == null || mitarbeiter.getPersonalNr() == null ? null
        : mitarbeiter.getPersonalNr();
  }

  public String getmitarbeiterFullName() {
    return mitarbeiter == null || mitarbeiter.getFullName() == null ? ""
        : mitarbeiter.getFullName();
  }

}
