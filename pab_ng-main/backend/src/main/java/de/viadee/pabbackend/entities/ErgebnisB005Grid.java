package de.viadee.pabbackend.entities;


import java.math.BigDecimal;

public class ErgebnisB005Grid implements Comparable<ErgebnisB005Grid> {

  private Mitarbeiter mitarbeiter;

  private LohnartKonstante lohnartKonstante;

  private BigDecimal eurBetrag;

  private BigDecimal stdBetrag;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullname() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public String getLohnartKonto() {
    return lohnartKonstante == null ? "" : lohnartKonstante.getKonto();
  }

  public String getLohnartBezeichnung() {
    return lohnartKonstante == null ? "" : lohnartKonstante.getBezeichnung();
  }

  public LohnartKonstante getLohnart() {
    return lohnartKonstante;
  }

  public void setLohnart(LohnartKonstante lohnartKonstante) {
    this.lohnartKonstante = lohnartKonstante;
  }

  public BigDecimal getEurBetrag() {
    return eurBetrag;
  }

  public void setEurBetrag(BigDecimal eurBetrag) {
    this.eurBetrag = eurBetrag;
  }

  public BigDecimal getStdBetrag() {
    return stdBetrag;
  }

  public void setStdBetrag(BigDecimal stdBetrag) {
    this.stdBetrag = stdBetrag;
  }


  @Override
  public int compareTo(ErgebnisB005Grid that) {
    if (this.mitarbeiter.compareTo(that.mitarbeiter) < 0) {
      return -1;
    } else if (this.mitarbeiter.compareTo(that.mitarbeiter) > 0) {
      return 1;
    }

    if (this.lohnartKonstante.compareTo(that.lohnartKonstante) < 0) {
      return -1;
    } else if (this.lohnartKonstante.compareTo(that.lohnartKonstante) > 0) {
      return 1;
    }
    return 0;
  }
}
