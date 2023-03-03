package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB004Belege {
  //TODO: Delete since probably not used

  private static final long serialVersionUID = 7658654696867005073L;

  String nachname;

  String vorname;

  String projektnummer;

  String bezeichnung;

  String belegart;

  BigDecimal summe;

  public String getNachname() {
    return nachname;
  }

  public void setNachname(final String nachname) {
    this.nachname = nachname;
  }

  public String getVorname() {
    return vorname;
  }

  public void setVorname(final String vorname) {
    this.vorname = vorname;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(final String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(final String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public String getBelegart() {
    return belegart;
  }

  public void setBelegart(final String belegart) {
    this.belegart = belegart;
  }

  public BigDecimal getSumme() {
    return summe;
  }

  public void setSumme(final BigDecimal summe) {
    this.summe = summe;
  }

}
