package de.viadee.pabbackend.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB002SonderarbeitszeitenExcel {

  @Column("Monat")
  private int monat;

  @Column("Jahr")
  private int jahr;

  @Column("PersonalNr")
  private String mitarbeiterpersonalnummer;

  @Column("Nachname")
  private String mitarbeitername;

  @Column("Vorname")
  private String mitarbeitervorname;

  @Column("projektnummer")
  private String projektnummer;

  @Column("projektbezeichnung")
  private String projektbezeichnung;

  @Column("TagVon")
  private LocalDate tagVon;

  @Column("UhrzeitVon")
  private LocalTime uhrzeitVon;

  @Column("UhrzeitBis")
  private LocalTime uhrzeitBis;

  @Column("IstFeiertag")
  private boolean feiertag;

  public Double getUhrzeitVonAlsDouble() {
    Double minutes = null;
    Double hours = null;
    if (uhrzeitVon.equals(LocalTime.of(23, 59, 59))) {
      minutes = 0.0d;
      hours = 24.0d;
    } else {
      minutes = Double.valueOf(uhrzeitVon.getMinute()) / 100;
      hours = Double.valueOf(uhrzeitVon.getHour());
    }

    return hours + minutes;
  }

  public Double getUhrzeitBisAlsDouble() {

    Double minutes = null;
    Double hours = null;
    if (uhrzeitBis.equals(LocalTime.of(23, 59, 59))) {
      minutes = 0.0d;
      hours = 24.0d;
    } else {
      minutes = Double.valueOf(uhrzeitBis.getMinute()) / 100;
      hours = Double.valueOf(uhrzeitBis.getHour());
    }

    return hours + minutes;
  }

  public String getMitarbeiterFullname() {
    return mitarbeitername + ", " + mitarbeitervorname;
  }

  public String getAbrechnungsmonat() {
    return jahr + "/" + String.format("%02d", monat);
  }

  public int getMonat() {
    return monat;
  }

  public void setMonat(final int monat) {
    this.monat = monat;
  }

  public int getJahr() {
    return jahr;
  }

  public void setJahr(final int jahr) {
    this.jahr = jahr;
  }

  public String getMitarbeiterpersonalnummer() {
    return mitarbeiterpersonalnummer;
  }

  public void setMitarbeiterpersonalnummer(final String mitarbeiterpersonalnummer) {
    this.mitarbeiterpersonalnummer = mitarbeiterpersonalnummer;
  }

  public String getMitarbeitername() {
    return mitarbeitername;
  }

  public void setMitarbeitername(final String mitarbeitername) {
    this.mitarbeitername = mitarbeitername;
  }

  public String getMitarbeitervorname() {
    return mitarbeitervorname;
  }

  public void setMitarbeitervorname(final String mitarbeitervorname) {
    this.mitarbeitervorname = mitarbeitervorname;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(final String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getProjektbezeichnung() {
    return projektbezeichnung;
  }

  public void setProjektbezeichnung(final String projektbezeichnung) {
    this.projektbezeichnung = projektbezeichnung;
  }

  public LocalDate getTagVon() {
    return tagVon;
  }

  public void setTagVon(final LocalDate tagVon) {
    this.tagVon = tagVon;
  }

  public LocalTime getUhrzeitVon() {
    return uhrzeitVon;
  }

  public void setUhrzeitVon(final LocalTime uhrzeitVon) {
    this.uhrzeitVon = uhrzeitVon;
  }

  public LocalTime getUhrzeitBis() {
    return uhrzeitBis;
  }

  public void setUhrzeitBis(final LocalTime uhrzeitBis) {
    this.uhrzeitBis = uhrzeitBis;
  }

  public boolean isFeiertag() {
    return feiertag;
  }

  public void setFeiertag(final boolean feiertag) {
    this.feiertag = feiertag;
  }

}
