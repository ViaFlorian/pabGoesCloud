package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB010 implements Comparable<ErgebnisB010> {

  private Mitarbeiter mitarbeiter;

  private String mitarbeiterTyp;

  private Integer jahr;

  private Integer monat;

  private BigDecimal anspruch;

  private BigDecimal vortrag;

  private BigDecimal genommen;

  private BigDecimal umbuchung;

  private BigDecimal uebertrag;

  private BigDecimal anspruchJahr;

  private BigDecimal vortragJahr;

  private BigDecimal genommenJahr;

  private BigDecimal umbuchungJahr;

  private BigDecimal uebertragJahr;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullname() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
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

  public BigDecimal getAnspruch() {
    return anspruch;
  }

  public void setAnspruch(BigDecimal anspruch) {
    this.anspruch = anspruch;
  }

  public BigDecimal getVortrag() {
    return vortrag;
  }

  public void setVortrag(BigDecimal vortrag) {
    this.vortrag = vortrag;
  }

  public BigDecimal getGenommen() {
    return genommen;
  }

  public void setGenommen(BigDecimal genommen) {
    this.genommen = genommen;
  }

  public BigDecimal getUmbuchung() {
    return umbuchung;
  }

  public void setUmbuchung(BigDecimal umbuchung) {
    this.umbuchung = umbuchung;
  }

  public BigDecimal getUebertrag() {
    return uebertrag;
  }

  public void setUebertrag(BigDecimal uebertrag) {
    this.uebertrag = uebertrag;
  }

  public BigDecimal getAnspruchJahr() {
    return anspruchJahr;
  }

  public void setAnspruchJahr(BigDecimal anspruchJahr) {
    this.anspruchJahr = anspruchJahr;
  }

  public BigDecimal getVortragJahr() {
    return vortragJahr;
  }

  public void setVortragJahr(BigDecimal vortragJahr) {
    this.vortragJahr = vortragJahr;
  }

  public BigDecimal getGenommenJahr() {
    return genommenJahr;
  }

  public void setGenommenJahr(BigDecimal genommenJahr) {
    this.genommenJahr = genommenJahr;
  }

  public BigDecimal getUmbuchungJahr() {
    return umbuchungJahr;
  }

  public void setUmbuchungJahr(BigDecimal umbuchungJahr) {
    this.umbuchungJahr = umbuchungJahr;
  }

  public BigDecimal getUebertragJahr() {
    return uebertragJahr;
  }

  public void setUebertragJahr(BigDecimal uebertragJahr) {
    this.uebertragJahr = uebertragJahr;
  }

  @Override
  public int compareTo(ErgebnisB010 that) {
    if (this.mitarbeiter.compareTo(that.mitarbeiter) < 0) {
      return -1;
    } else if (this.mitarbeiter.compareTo(that.mitarbeiter) > 0) {
      return 1;
    }
    return 0;
  }
}
