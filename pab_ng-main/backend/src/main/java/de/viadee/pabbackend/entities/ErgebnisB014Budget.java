package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB014Budget {

  private String wertstellung;
  private BigDecimal betrag;
  private String bemerkung;

  public String getWertstellung() {
    return wertstellung;
  }

  public void setWertstellung(String wertstellung) {
    this.wertstellung = wertstellung;
  }

  public BigDecimal getBetrag() {
    return betrag;
  }

  public void setBetrag(BigDecimal betrag) {
    this.betrag = betrag;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(String bemerkung) {
    this.bemerkung = bemerkung;
  }
}
