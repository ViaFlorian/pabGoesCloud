package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB014KostenLeistungen {

  private String monat;
  private BigDecimal fertigstellungsgrad;
  private BigDecimal kostenStunden;
  private BigDecimal kosten;
  private BigDecimal leistungStunden;
  private BigDecimal stdsatz0Stunden;
  private BigDecimal rechnerischeLeistung;
  private BigDecimal faktfaehigeLeistung;


  public String getMonat() {
    return monat;
  }

  public void setMonat(String monat) {
    this.monat = monat;
  }

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public BigDecimal getKostenStunden() {
    return kostenStunden;
  }

  public void setKostenStunden(BigDecimal kostenStunden) {
    this.kostenStunden = kostenStunden;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getLeistungStunden() {
    return leistungStunden;
  }

  public void setLeistungStunden(BigDecimal leistungStunden) {
    this.leistungStunden = leistungStunden;
  }

  public BigDecimal getStdsatz0Stunden() {
    return stdsatz0Stunden;
  }

  public void setStdsatz0Stunden(BigDecimal stdsatz0Stunden) {
    this.stdsatz0Stunden = stdsatz0Stunden;
  }

  public BigDecimal getRechnerischeLeistung() {
    return rechnerischeLeistung;
  }

  public void setRechnerischeLeistung(BigDecimal rechnerischeLeistung) {
    this.rechnerischeLeistung = rechnerischeLeistung;
  }

  public BigDecimal getFaktfaehigeLeistung() {
    return faktfaehigeLeistung;
  }

  public void setFaktfaehigeLeistung(BigDecimal faktfaehigeLeistung) {
    this.faktfaehigeLeistung = faktfaehigeLeistung;
  }
}
