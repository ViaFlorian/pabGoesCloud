package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Beleg")
public final class Beleg {

  @Id
  @Column("ID")
  private Long id;

  @Column("Datum")
  private LocalDate datum;

  @Column("Arbeitsstaette")
  private String arbeitsstaette;

  @Column("km")
  private BigDecimal kilometer;

  @Column("Betrag")
  private BigDecimal betrag;

  @Column("BelegNr")
  private String belegNr;

  @Column("Bemerkung")
  private String bemerkung;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @Column("ProjektID")
  private Long projektId;

  @Column("BelegArtID")
  private Long belegartId;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public LocalDate getDatum() {
    return datum;
  }

  public void setDatum(final LocalDate datum) {
    this.datum = datum;
  }

  public String getArbeitsstaette() {
    return arbeitsstaette;
  }

  public void setArbeitsstaette(final String arbeitsstaette) {
    this.arbeitsstaette = arbeitsstaette;
  }

  public BigDecimal getKilometer() {
    return kilometer;
  }

  public void setKilometer(final BigDecimal kilometer) {
    this.kilometer = kilometer;
  }

  public BigDecimal getBetrag() {
    return betrag;
  }

  public void setBetrag(final BigDecimal betrag) {
    this.betrag = betrag;
  }

  public String getBelegNr() {
    return belegNr;
  }

  public void setBelegNr(final String belegNr) {
    this.belegNr = belegNr;
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
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getBelegartId() {
    return belegartId;
  }

  public void setBelegartId(final Long belegartId) {
    this.belegartId = belegartId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Beleg beleg)) {
      return false;
    }
    return getId().equals(beleg.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
