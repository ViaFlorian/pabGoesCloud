package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("SonstigeProjektkosten")
public class SonstigeProjektkosten {

  @Id
  @Column("ID")
  private Long id;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("Kosten")
  private BigDecimal kosten;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("ProjektID")
  private Long projektId;

  @Column("Bemerkung")
  private String bemerkung;

  @Column("BelegArtID")
  private Long belegartId;

  @Column("KostenartID")
  private Long viadeeAuslagenKostenartId;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDate zuletztGeaendertAm;

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

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(final BigDecimal kosten) {
    this.kosten = kosten;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getBelegartId() {
    return belegartId;
  }

  public void setBelegartId(final Long belegartId) {
    this.belegartId = belegartId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getViadeeAuslagenKostenartId() {
    return viadeeAuslagenKostenartId;
  }

  public void setViadeeAuslagenKostenartId(final Long viadeeAuslagenKostenartId) {
    this.viadeeAuslagenKostenartId = viadeeAuslagenKostenartId;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public LocalDate getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(final LocalDate zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }
}
