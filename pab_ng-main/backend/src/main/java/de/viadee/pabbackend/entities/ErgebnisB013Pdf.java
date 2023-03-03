package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.apache.commons.lang3.NotImplementedException;

public class ErgebnisB013Pdf implements Comparable<ErgebnisB013Pdf> {

  private Organisationseinheit oe;
  private Kunde kunde;
  private Projekt projekt;
  private BigDecimal fertigstellungsgrad;

  private BigDecimal leistungMonat;
  private BigDecimal fakturMonat;
  private BigDecimal leistungJahr;
  private BigDecimal fakturJahr;
  private BigDecimal leistungGesamt;
  private BigDecimal fakturGesamt;

  public String getOrganisationseinheitForPdf() {
    return oe.getBezeichnung();
  }

  public String getOeVerantwortlicherForPdf() {
    throw new NotImplementedException("Hier muss noch der MA Name aufgelöst werden");
//        final Long verantwortlicher = oe.getVerantwortlicherMitarbeiterId();
// FixMe        return verantwortlicher == null ? "" : verantwortlicher.getFullName();
  }

  public void setOrganisationseinheit(final Organisationseinheit oe) {
    this.oe = oe;
  }

  public String getKundeForPdf() {
    return kunde.getKurzbezeichnung();
  }

  public void setKunde(final Kunde kunde) {
    this.kunde = kunde;
  }

  public String getProjektnummer() {
    return projekt.getProjektnummer();
  }

  public String getProjektbezeichnung() {
    return projekt.getBezeichnung();
  }

  public String getProjekttypForPdf() {
    return projekt.getProjekttyp().toString();
  }

  public void setProjekt(final Projekt projekt) {
    this.projekt = projekt;
  }

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(final BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public BigDecimal getLeistungMonat() {
    return leistungMonat;
  }

  public void setLeistungMonat(final BigDecimal leistungMonat) {
    this.leistungMonat = leistungMonat;
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


  @Override
  public int compareTo(ErgebnisB013Pdf that) {
    int firstCompare = this.getOrganisationseinheitForPdf()
        .compareTo(that.getOrganisationseinheitForPdf());
    if (firstCompare != 0) {
      return firstCompare;
    }

    int secondCompare = this.getKundeForPdf().compareTo(that.getKundeForPdf());
    if (secondCompare != 0) {
      return secondCompare;
    }

    int thirdCompare = this.getProjektnummer().compareTo(that.getProjektnummer());
    return thirdCompare;
  }
}
