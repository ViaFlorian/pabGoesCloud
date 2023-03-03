package de.viadee.pabbackend.entities;


import java.math.BigDecimal;

public class ErgebnisB007Grid {

  private static final long serialVersionUID = 6027175424914837092L;

  private String projektnummer;

  private boolean projektIstAktiv;

  private String projektbezeichnung;

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

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public void setProjektIstAktiv(boolean projektIstAktiv) {
    this.projektIstAktiv = projektIstAktiv;
  }

  public String getProjektbezeichnung() {
    return projektbezeichnung;
  }

  public void setProjektbezeichnung(String projektbezeichnung) {
    this.projektbezeichnung = projektbezeichnung;
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
}
