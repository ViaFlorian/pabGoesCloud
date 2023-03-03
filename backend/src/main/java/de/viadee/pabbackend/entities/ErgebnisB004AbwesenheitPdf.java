package de.viadee.pabbackend.entities;

import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004AbwesenheitPdf implements Comparable<ErgebnisB004AbwesenheitPdf> {

  @Column("Projektnummer")
  String projektnummer;

  @Column("Bezeichnung")
  String bezeichnung;

  @Column("KundeBezeichnung")
  String kundeBezeichnung;

  @Column("anzahlTage24")
  Integer anzahlTage24;

  @Column("anzahlTageAnreiseAbreise")
  Integer anzahlTageAnreiseAbreise;

  @Column("anzahlTageUeberAcht")
  Integer anzahlTageUeberAcht;

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

  public Integer getAnzahlTage24() {
    return anzahlTage24;
  }

  public void setAnzahlTage24(Integer anzahlTage24) {
    this.anzahlTage24 = anzahlTage24;
  }

  public Integer getAnzahlTageAnreiseAbreise() {
    return anzahlTageAnreiseAbreise;
  }

  public void setAnzahlTageAnreiseAbreise(Integer anzahlTageAnreiseAbreise) {
    this.anzahlTageAnreiseAbreise = anzahlTageAnreiseAbreise;
  }

  public Integer getAnzahlTageUeberAcht() {
    return anzahlTageUeberAcht;
  }

  public void setAnzahlTageUeberAcht(Integer anzahlTageUeberAcht) {
    this.anzahlTageUeberAcht = anzahlTageUeberAcht;
  }


  @Override
  public int compareTo(ErgebnisB004AbwesenheitPdf that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }
}
