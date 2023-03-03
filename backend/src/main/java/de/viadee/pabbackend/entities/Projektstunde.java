package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Projektstunde")
public class Projektstunde {

  @Id
  @Column("ID")
  private Long id;

  @Column("TagVon")
  private LocalDate tagVon;

  @Column("UhrzeitVon")
  private LocalTime uhrzeitVon;

  @Column("TagBis")
  private LocalDate tagBis;

  @Column("UhrzeitBis")
  private LocalTime uhrzeitBis;

  @Column("ProjektID")
  private Long projektId;

  @Column("AnzahlStunden")
  private BigDecimal anzahlStunden;

  @Column("Fakturierfaehig")
  private Boolean fakturierfaehig;

  @Column("davonFakturierbar")
  private BigDecimal davonFakturierbar;

  @Column("KostensatzIntern")
  private BigDecimal kostensatzIntern;

  @Column("KostensatzExtern")
  private BigDecimal kostensatzExtern;

  @Column("Bemerkung")
  private String bemerkung;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @Column("ProjektstundeTypID")
  private Long projektstundeTypId;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
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

  public LocalDate getTagBis() {
    return tagBis;
  }

  public void setTagBis(final LocalDate tagBis) {
    this.tagBis = tagBis;
  }

  public LocalTime getUhrzeitBis() {
    return uhrzeitBis;
  }

  public void setUhrzeitBis(final LocalTime uhrzeitBis) {
    this.uhrzeitBis = uhrzeitBis;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public BigDecimal getAnzahlStunden() {
    return anzahlStunden;
  }

  public void setAnzahlStunden(final BigDecimal anzahlStunden) {
    this.anzahlStunden = anzahlStunden;
  }

  public Boolean getFakturierfaehig() {
    return fakturierfaehig;
  }

  public void setFakturierfaehig(final Boolean fakturierfaehig) {
    this.fakturierfaehig = fakturierfaehig;
  }

  public BigDecimal getDavonFakturierbar() {
    return davonFakturierbar;
  }

  public void setDavonFakturierbar(final BigDecimal davonFakturierbar) {
    this.davonFakturierbar = davonFakturierbar;
  }

  public BigDecimal getKostensatzIntern() {
    return kostensatzIntern;
  }

  public void setKostensatzIntern(final BigDecimal kostensatzIntern) {
    this.kostensatzIntern = kostensatzIntern;
  }

  public BigDecimal getKostensatzExtern() {
    return kostensatzExtern;
  }

  public void setKostensatzExtern(final BigDecimal kostensatzExtern) {
    this.kostensatzExtern = kostensatzExtern;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
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
  public Long getProjektstundeTypId() {
    return projektstundeTypId;
  }

  public void setProjektstundeTypId(final Long projektstundeTypId) {
    this.projektstundeTypId = projektstundeTypId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  public Boolean isFakturierfaehig() {
    return fakturierfaehig == null || fakturierfaehig.equals(Boolean.TRUE)
        ? Boolean.TRUE
        : Boolean.FALSE;
  }

  public Boolean isNichtFakturierfaehig() {
    return fakturierfaehig == null || fakturierfaehig.equals(Boolean.TRUE)
        ? Boolean.FALSE
        : Boolean.TRUE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Projektstunde)) {
      return false;
    }
    Projektstunde that = (Projektstunde) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
