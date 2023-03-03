package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004SonderarbeitszeitPdf implements
    Comparable<ErgebnisB004SonderarbeitszeitPdf> {

  @Column("Projektnummer")
  private String projektnummer;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("KundeBezeichnung")
  private String kundeBezeichnung;

  @Column("summeSonder")
  private BigDecimal summeSonder;

  @Column("summeWerktag")
  private BigDecimal summeWerktag;

  @Column("summeSamstag")
  private BigDecimal summeSamstag;

  @Column("summeSonntagFeiertag")
  private BigDecimal summeSonntagFeiertag;

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

  public BigDecimal getSummeSonder() {
    return summeSonder;
  }

  public void setSummeSonder(BigDecimal summeSonder) {
    this.summeSonder = summeSonder;
  }

  public BigDecimal getSummeWerktag() {
    return summeWerktag;
  }

  public void setSummeWerktag(BigDecimal summeWerktag) {
    this.summeWerktag = summeWerktag;
  }

  public BigDecimal getSummeSamstag() {
    return summeSamstag;
  }

  public void setSummeSamstag(BigDecimal summeSamstag) {
    this.summeSamstag = summeSamstag;
  }

  public BigDecimal getSummeSonntagFeiertag() {
    return summeSonntagFeiertag;
  }

  public void setSummeSonntagFeiertag(BigDecimal summeSonntagFeiertag) {
    this.summeSonntagFeiertag = summeSonntagFeiertag;
  }

  @Override
  public int compareTo(ErgebnisB004SonderarbeitszeitPdf that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }

}
