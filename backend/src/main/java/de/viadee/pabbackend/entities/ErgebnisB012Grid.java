package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB012Grid {

  private BigDecimal stunden;

  private Abrechnungsmonat abrechnungsmonat;

  private BigDecimal kosten;

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


  public Abrechnungsmonat getAbrechnungsmonat() {
    return abrechnungsmonat;
  }

  public void setAbrechnungsmonat(Abrechnungsmonat abrechnungsmonat) {
    this.abrechnungsmonat = abrechnungsmonat;
  }

  public String getAbrechnungmonatForGrid() {
    return abrechnungsmonat.toString();
  }

  public BigDecimal getStunden() {
    return stunden;
  }

  public void setStunden(BigDecimal stunden) {
    this.stunden = stunden;
  }
}
