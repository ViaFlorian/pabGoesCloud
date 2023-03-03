package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004ReisekostenAuslagenPdf implements
    Comparable<ErgebnisB004ReisekostenAuslagenPdf> {

  @Column("Projektnummer")
  private String projektnummer;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("KundeBezeichnung")
  private String kundeBezeichnung;

  @Column("summeBetrag")
  private BigDecimal summeBetrag;

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public String getKundeBezeichnung() {
    return kundeBezeichnung;
  }

  public void setKundeBezeichnung(String kundeBezeichnung) {
    this.kundeBezeichnung = kundeBezeichnung;
  }

  public BigDecimal getSummeBetrag() {
    return summeBetrag;
  }

  public void setSummeBetrag(BigDecimal summeBetrag) {
    this.summeBetrag = summeBetrag;
  }

  @Override
  public int compareTo(ErgebnisB004ReisekostenAuslagenPdf that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }

}
