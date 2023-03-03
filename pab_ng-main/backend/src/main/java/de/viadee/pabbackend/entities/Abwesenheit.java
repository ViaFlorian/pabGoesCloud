package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Abwesenheit")
public class Abwesenheit implements Comparable<Abwesenheit> {

  @Id
  @Column("ID")
  private Long id;

  @Column("Arbeitsstaette")
  private String arbeitsstaette;

  @Column("TagVon")
  private LocalDate tagVon;

  @Transient
  private LocalDate tagBis;

  @Column("UhrzeitVon")
  private LocalTime uhrzeitVon;

  @Column("UhrzeitBis")
  private LocalTime uhrzeitBis;

  @Column("Spesen")
  private BigDecimal spesen;

  @Column("Zuschlag")
  private BigDecimal zuschlag;

  @Column("Bemerkung")
  private String bemerkung;

  @Column("FruehstueckGenommen")
  private Boolean mitFruehstueck;

  @Column("MittagessenGenommen")
  private Boolean mitMittagessen;

  @Column("AbendessenGenommen")
  private Boolean mitAbendessen;

  @Column("Uebernachtet")
  private Boolean mitUebernachtung;

  @Column("DreiMonatsRegelAktiv")
  private Boolean dreiMonatsRegelAktiv;

  @Column("DreiMonatsRegelUebersteuert")
  private Boolean dreiMonatsRegelUebersteuert;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @Column("ProjektID")
  private Long projektId;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Transient
  private BigDecimal anzahlStundenFuerSpesenberechnung;

  @Transient
  private BigDecimal anzahlStundenFuerZuschlagsberechnung;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getArbeitsstaette() {
    return arbeitsstaette;
  }

  public void setArbeitsstaette(final String arbeitsstaette) {
    this.arbeitsstaette = arbeitsstaette;
  }

  public LocalDate getTagVon() {
    return tagVon;
  }

  public void setTagVon(final LocalDate tagVon) {
    this.tagVon = tagVon;
  }

  public LocalDate getTagBis() {
    return tagBis;
  }

  public void setTagBis(LocalDate tagBis) {
    this.tagBis = tagBis;
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

  public BigDecimal getSpesen() {
    return spesen;
  }

  public void setSpesen(final BigDecimal spesen) {
    this.spesen = spesen;
  }

  public BigDecimal getZuschlag() {
    return zuschlag;
  }

  public void setZuschlag(final BigDecimal zuschlag) {
    this.zuschlag = zuschlag;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public Boolean isMitFruehstueck() {
    return mitFruehstueck != null ? mitFruehstueck : false;
  }

  public void setMitFruehstueck(final boolean mitFruehstueck) {
    this.mitFruehstueck = mitFruehstueck;
  }

  public Boolean isMitMittagessen() {
    return mitMittagessen != null ? mitMittagessen : false;
  }

  public void setMitMittagessen(final boolean mitMittagessen) {
    this.mitMittagessen = mitMittagessen;
  }

  public Boolean isMitAbendessen() {
    return mitAbendessen != null ? mitAbendessen : false;
  }

  public void setMitAbendessen(final boolean mitAbendessen) {
    this.mitAbendessen = mitAbendessen;
  }

  public Boolean isMitUebernachtung() {
    return mitUebernachtung;
  }

  public void setMitUebernachtung(final boolean mitUebernachtung) {
    this.mitUebernachtung = mitUebernachtung;
  }

  public Boolean isDreiMonatsRegelAktiv() {
    return dreiMonatsRegelAktiv != null ? dreiMonatsRegelAktiv : false;
  }

  public void setDreiMonatsRegelAktiv(final boolean dreiMonatsRegelAktiv) {
    this.dreiMonatsRegelAktiv = dreiMonatsRegelAktiv;
  }

  public Boolean isDreiMonatsRegelUebersteuert() {
    return dreiMonatsRegelUebersteuert;
  }

  public void setDreiMonatsRegelUebersteuert(final boolean dreiMonatsRegelUebersteuert) {
    this.dreiMonatsRegelUebersteuert = dreiMonatsRegelUebersteuert;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(final LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  public BigDecimal getAnzahlStundenFuerSpesenberechnung() {
    return anzahlStundenFuerSpesenberechnung;
  }

  public void setAnzahlStundenFuerSpesenberechnung(
      final BigDecimal anzahlStundenFuerSpesenberechnung) {
    this.anzahlStundenFuerSpesenberechnung = anzahlStundenFuerSpesenberechnung;
  }

  public BigDecimal getAnzahlStundenFuerZuschlagsberechnung() {
    return anzahlStundenFuerZuschlagsberechnung;
  }

  public void setAnzahlStundenFuerZuschlagsberechnung(
      final BigDecimal anzahlStundenFuerZuschlagsberechnung) {
    this.anzahlStundenFuerZuschlagsberechnung = anzahlStundenFuerZuschlagsberechnung;
  }


  @Override
  public int compareTo(final Abwesenheit other) {
    return tagVon.compareTo(other.getTagVon());
  }

  @Override
  public Abwesenheit clone() {
    Abwesenheit clone = null;
    try {
      clone = (Abwesenheit) super.clone();
    } catch (CloneNotSupportedException e) {

    }

    return clone;
  }
}
