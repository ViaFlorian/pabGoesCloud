package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Arbeitsnachweis")
public class Arbeitsnachweis implements Comparable<Arbeitsnachweis> {

  @Id
  @Column("ID")
  private Long id;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("StatusID")
  private Integer statusId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("Stellenfaktor")
  private BigDecimal stellenfaktor;

  @Column("Firmenwagen")
  private Boolean firmenwagen;

  @Column("Auszahlung")
  private BigDecimal auszahlung;

  @Column("SmartphoneEigen")
  private Boolean smartphoneEigen;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @Column("Uebertrag")
  private BigDecimal uebertrag;

  @Column("Sollstunden")
  private BigDecimal sollstunden;

  @Transient
  private List<DreiMonatsRegel> berechneteDreiMonatsRegeln;

  @Transient
  private List<Fehlerlog> fehlerlog;

  public List<Fehlerlog> getFehlerlog() {
    return fehlerlog;
  }

  public void setFehlerlog(List<Fehlerlog> fehlerlog) {
    this.fehlerlog = fehlerlog;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(final Integer monat) {
    this.monat = monat;
  }

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(final Integer statusId) {
    this.statusId = statusId;
  }

  public BigDecimal getStellenfaktor() {
    return stellenfaktor;
  }

  public void setStellenfaktor(final BigDecimal stellenfaktor) {
    this.stellenfaktor = stellenfaktor;
  }

  public Boolean getFirmenwagen() {
    return firmenwagen;
  }

  public void setFirmenwagen(final Boolean firmenwagen) {
    this.firmenwagen = firmenwagen;
  }

  public BigDecimal getAuszahlung() {
    return auszahlung;
  }

  public void setAuszahlung(final BigDecimal auszahlung) {
    this.auszahlung = auszahlung;
  }

  public Boolean getSmartphoneEigen() {
    return smartphoneEigen;
  }

  public void setSmartphoneEigen(final Boolean smartphoneEigen) {
    this.smartphoneEigen = smartphoneEigen;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(final LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public BigDecimal getUebertrag() {
    return uebertrag;
  }

  public void setUebertrag(final BigDecimal uebertrag) {
    this.uebertrag = uebertrag;
  }

  public BigDecimal getSollstunden() {
    return sollstunden;
  }

  public void setSollstunden(final BigDecimal sollstunden) {
    this.sollstunden = sollstunden;
  }

  public List<DreiMonatsRegel> getBerechneteDreiMonatsRegeln() {
    return berechneteDreiMonatsRegeln;
  }

  public void setBerechneteDreiMonatsRegeln(List<DreiMonatsRegel> berechneteDreiMonatsRegeln) {
    this.berechneteDreiMonatsRegeln = berechneteDreiMonatsRegeln;
  }

  public String getDatum() {
    String datum;
    if (jahr == null || monat == null) {
      datum = "keine Daten";
    } else {
      datum = jahr.toString().concat("/").concat(String.format("%02d", monat));
    }
    return datum;
  }

  @Override
  public int compareTo(final Arbeitsnachweis zuVergleichenderArbeitsnachweis) {

    // Zunächst wird das Jahr der jeweiligen Arbeitsnachweise verglichen
    int result = 0;

    if (zuVergleichenderArbeitsnachweis != null
        && zuVergleichenderArbeitsnachweis.getJahr() != null
        && zuVergleichenderArbeitsnachweis.getMonat() != null) {
      result = jahr.compareTo(zuVergleichenderArbeitsnachweis.jahr);
      // Ist das Jahr gleich, muss der Monat verglichen werden
      if (result == 0) {
        result = monat.compareTo(zuVergleichenderArbeitsnachweis.monat);
      }
    } else {
      result = 1;
    }

    return result;
  }
}
