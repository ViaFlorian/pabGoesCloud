package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004RufbereitschaftPdf implements Comparable<ErgebnisB004RufbereitschaftPdf> {

  @Column("Projektnummer")
  private String projektnummer;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("KundeBezeichnung")
  private String kundeBezeichnung;

  @Column("summeRuf")
  private BigDecimal summeRuf;

  public BigDecimal getSummeRuf() {
    return summeRuf;
  }

  public void setSummeRuf(BigDecimal summeRuf) {
    this.summeRuf = summeRuf;
  }

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

  @Override
  public int compareTo(ErgebnisB004RufbereitschaftPdf that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }

}
