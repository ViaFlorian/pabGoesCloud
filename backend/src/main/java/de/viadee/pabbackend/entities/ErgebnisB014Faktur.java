package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB014Faktur {

  private String referenzmonat;
  private String rechnungsdatum;
  private String rechnungsnummer;
  private BigDecimal betragNetto;
  private BigDecimal nichtBudgetrelevant;
  private String bemerkung;
  private String rechnungsempfaenger;

  public String getReferenzmonat() {
    return referenzmonat;
  }

  public void setReferenzmonat(String referenzmonat) {
    this.referenzmonat = referenzmonat;
  }

  public String getRechnungsdatum() {
    return rechnungsdatum;
  }

  public void setRechnungsdatum(String rechnungsdatum) {
    this.rechnungsdatum = rechnungsdatum;
  }

  public String getRechnungsnummer() {
    return rechnungsnummer;
  }

  public void setRechnungsnummer(String rechnungsnummer) {
    this.rechnungsnummer = rechnungsnummer;
  }

  public BigDecimal getBetragNetto() {
    return betragNetto;
  }

  public void setBetragNetto(BigDecimal betragNetto) {
    this.betragNetto = betragNetto;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public String getRechnungsempfaenger() {
    return rechnungsempfaenger;
  }

  public void setRechnungsempfaenger(String rechnungsempfaenger) {
    this.rechnungsempfaenger = rechnungsempfaenger;
  }

  public BigDecimal getNichtBudgetrelevant() {
    return nichtBudgetrelevant;
  }

  public void setNichtBudgetrelevant(BigDecimal nichtBudgetrelevant) {
    this.nichtBudgetrelevant = nichtBudgetrelevant;
  }

}
