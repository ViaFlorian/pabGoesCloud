package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB013Grid {

  private Organisationseinheit oe;
  private Kunde kunde;
  private BigDecimal faktfLeistungMonat;
  private BigDecimal fakturMonat;
  private BigDecimal leistungJahr;
  private BigDecimal fakturJahr;
  private BigDecimal leistungGesamt;
  private BigDecimal fakturGesamt;

  public Organisationseinheit getOrganisationseinheit() {
    return oe;
  }

  public void setOrganisationseinheit(final Organisationseinheit oe) {
    this.oe = oe;
  }

  public String getOeForGrid() {
    return oe.getBezeichnung();
  }

  public Kunde getKunde() {
    return kunde;
  }

  public void setKunde(final Kunde kunde) {
    this.kunde = kunde;
  }

  public String getKundeForGrid() {
    return kunde.getBezeichnung();
  }

  public BigDecimal getFaktfLeistungMonat() {
    return faktfLeistungMonat;
  }

  public void setFaktfLeistungMonat(final BigDecimal faktfLeistungMonat) {
    this.faktfLeistungMonat = faktfLeistungMonat;
  }

  public BigDecimal getFakturMonat() {
    return fakturMonat;
  }

  public void setFakturMonat(final BigDecimal fakturMonat) {
    this.fakturMonat = fakturMonat;
  }

  public BigDecimal getLeistungJahr() {
    return leistungJahr;
  }

  public void setLeistungJahr(final BigDecimal leistungJahr) {
    this.leistungJahr = leistungJahr;
  }

  public BigDecimal getFakturJahr() {
    return fakturJahr;
  }

  public void setFakturJahr(final BigDecimal fakturJahr) {
    this.fakturJahr = fakturJahr;
  }

  public BigDecimal getLeistungGesamt() {
    return leistungGesamt;
  }

  public void setLeistungGesamt(final BigDecimal leistungGesamt) {
    this.leistungGesamt = leistungGesamt;
  }

  public BigDecimal getFakturGesamt() {
    return fakturGesamt;
  }

  public void setFakturGesamt(final BigDecimal fakturGesamt) {
    this.fakturGesamt = fakturGesamt;
  }
}
