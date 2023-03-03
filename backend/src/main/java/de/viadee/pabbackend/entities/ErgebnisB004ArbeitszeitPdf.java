package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004ArbeitszeitPdf implements Comparable<ErgebnisB004ArbeitszeitPdf> {

  @Column("Projektnummer")
  private String projektnummer;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("KundeBezeichnung")
  private String kundeBezeichnung;

  @Column("summeNormal")
  private BigDecimal summeNormal;

  @Column("summeReise")
  private BigDecimal summeReise;

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

  public BigDecimal getSummeNormal() {
    return summeNormal;
  }

  public void setSummeNormal(BigDecimal summeNormal) {
    this.summeNormal = summeNormal;
  }

  public BigDecimal getSummeReise() {
    return summeReise;
  }

  public void setSummeReise(BigDecimal summeReise) {
    this.summeReise = summeReise;
  }

  public String getKundeBezeichnung() {
    return kundeBezeichnung;
  }

  public void setKundeBezeichnung(String kundeBezeichnung) {
    this.kundeBezeichnung = kundeBezeichnung;
  }

  @Override
  public int compareTo(ErgebnisB004ArbeitszeitPdf that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }
}
