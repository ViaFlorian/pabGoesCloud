package de.viadee.pabbackend.entities;

import static java.math.BigDecimal.ZERO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ProjektabrechnungReise")
public class ProjektabrechnungReise {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("AngerechneteReisezeit")
  private BigDecimal angerechneteReisezeit;

  @Column("Kostensatz")
  private BigDecimal kostensatz;

  @Column("BelegeLtArbeitsnachweisKosten")
  private BigDecimal belegeLautArbeitsnachweisKosten;

  @Column("BelegeViadeeKosten")
  private BigDecimal belegeViadeeKosten;

  @Column("SpesenKosten")
  private BigDecimal spesenKosten;

  @Column("ZuschlaegeKosten")
  private BigDecimal zuschlaegeKosten;

  @Column("TatsaechlicheReisezeit")
  private BigDecimal tatsaechlicheReisezeit;

  @Column("TatsaechlicheReisezeitInformatorisch")
  private BigDecimal tatsaechlicheReisezeitInformatorisch;

  @Column("Stundensatz")
  private BigDecimal stundensatz;

  @Column("BelegeLtArbeitsnachweisLeistung")
  private BigDecimal belegeLautArbeitsnachweisLeistung;

  @Column("BelegeViadeeLeistung")
  private BigDecimal belegeViadeeLeistung;

  @Column("SpesenLeistung")
  private BigDecimal spesenLeistung;

  @Column("ZuschlaegeLeistung")
  private BigDecimal zuschlaegeLeistung;

  @Column("PauschaleAnzahl")
  private BigDecimal pauschaleAnzahl;

  @Column("PauschaleProTag")
  private BigDecimal pauschaleProTag;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektabrechnungId() {
    return projektabrechnungId;
  }

  public void setProjektabrechnungId(final Long projektabrechnungId) {
    this.projektabrechnungId = projektabrechnungId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public BigDecimal getAngerechneteReisezeit() {
    return angerechneteReisezeit;
  }

  public void setAngerechneteReisezeit(final BigDecimal angerechneteReisezeit) {
    this.angerechneteReisezeit = angerechneteReisezeit;
  }

  public BigDecimal getKostensatz() {
    return kostensatz;
  }

  public void setKostensatz(final BigDecimal kostensatz) {
    this.kostensatz = kostensatz;
  }

  public BigDecimal getBelegeLautArbeitsnachweisKosten() {
    return belegeLautArbeitsnachweisKosten;
  }

  public void setBelegeLautArbeitsnachweisKosten(final BigDecimal belegeLautArbeitsnachweisKosten) {
    this.belegeLautArbeitsnachweisKosten = belegeLautArbeitsnachweisKosten;
  }

  public BigDecimal getBelegeViadeeKosten() {
    return belegeViadeeKosten;
  }

  public void setBelegeViadeeKosten(final BigDecimal belegeViadeeKosten) {
    this.belegeViadeeKosten = belegeViadeeKosten;
  }

  public BigDecimal getSpesenKosten() {
    return spesenKosten;
  }

  public void setSpesenKosten(final BigDecimal spesenKosten) {
    this.spesenKosten = spesenKosten;
  }

  public BigDecimal getZuschlaegeKosten() {
    return zuschlaegeKosten;
  }

  public void setZuschlaegeKosten(final BigDecimal zuschlaegeKosten) {
    this.zuschlaegeKosten = zuschlaegeKosten;
  }

  public BigDecimal getTatsaechlicheReisezeit() {
    return tatsaechlicheReisezeit;
  }

  public void setTatsaechlicheReisezeit(final BigDecimal tatsaechlicheReisezeit) {
    this.tatsaechlicheReisezeit = tatsaechlicheReisezeit;
  }

  public BigDecimal getTatsaechlicheReisezeitInformatorisch() {
    return tatsaechlicheReisezeitInformatorisch;
  }

  public void setTatsaechlicheReisezeitInformatorisch(
      final BigDecimal tatsaechlicheReisezeitInformatorisch) {
    this.tatsaechlicheReisezeitInformatorisch = tatsaechlicheReisezeitInformatorisch;
  }

  public BigDecimal getStundensatz() {
    return stundensatz;
  }

  public void setStundensatz(final BigDecimal stundensatz) {
    this.stundensatz = stundensatz;
  }

  public BigDecimal getBelegeLautArbeitsnachweisLeistung() {
    return belegeLautArbeitsnachweisLeistung;
  }

  public void setBelegeLautArbeitsnachweisLeistung(
      final BigDecimal belegeLautArbeitsnachweisLeistung) {
    this.belegeLautArbeitsnachweisLeistung = belegeLautArbeitsnachweisLeistung;
  }

  public BigDecimal getBelegeViadeeLeistung() {
    return belegeViadeeLeistung;
  }

  public void setBelegeViadeeLeistung(final BigDecimal belegeViadeeLeistung) {
    this.belegeViadeeLeistung = belegeViadeeLeistung;
  }

  public BigDecimal getSpesenLeistung() {
    return spesenLeistung;
  }

  public void setSpesenLeistung(final BigDecimal spesenLeistung) {
    this.spesenLeistung = spesenLeistung;
  }

  public BigDecimal getZuschlaegeLeistung() {
    return zuschlaegeLeistung;
  }

  public void setZuschlaegeLeistung(final BigDecimal zuschlaegeLeistung) {
    this.zuschlaegeLeistung = zuschlaegeLeistung;
  }

  public BigDecimal getPauschaleAnzahl() {
    return pauschaleAnzahl;
  }

  public void setPauschaleAnzahl(final BigDecimal pauschaleAnzahl) {
    this.pauschaleAnzahl = pauschaleAnzahl;
  }

  public BigDecimal getPauschaleProTag() {
    return pauschaleProTag;
  }

  public void setPauschaleProTag(final BigDecimal pauschaleProTag) {
    this.pauschaleProTag = pauschaleProTag;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public BigDecimal getReiseKosten() {
    BigDecimal reiseKosten = ZERO;
    BigDecimal angerechneteReisezeitProdukt =
        angerechneteReisezeit == null || kostensatz == null ? ZERO
            : angerechneteReisezeit.multiply(kostensatz).setScale(2, RoundingMode.HALF_UP);
    reiseKosten = angerechneteReisezeitProdukt.add(
        belegeLautArbeitsnachweisKosten == null ? ZERO : belegeLautArbeitsnachweisKosten);
    reiseKosten = reiseKosten.add(belegeViadeeKosten == null ? ZERO : belegeViadeeKosten);
    reiseKosten = reiseKosten.add(spesenKosten == null ? ZERO : spesenKosten);
    reiseKosten = reiseKosten.add(zuschlaegeKosten == null ? ZERO : zuschlaegeKosten);

    return reiseKosten;
  }

  public BigDecimal getReiseLeistung() {
    BigDecimal reiseLeistung = ZERO;
    BigDecimal tatsaechlicheReisezeitProdukt =
        tatsaechlicheReisezeit == null || stundensatz == null ? ZERO
            : tatsaechlicheReisezeit.multiply(stundensatz).setScale(2, RoundingMode.HALF_UP);
    reiseLeistung = tatsaechlicheReisezeitProdukt.add(
        belegeLautArbeitsnachweisLeistung == null ? ZERO : belegeLautArbeitsnachweisLeistung);
    reiseLeistung = reiseLeistung.add(belegeViadeeLeistung == null ? ZERO : belegeViadeeLeistung);
    reiseLeistung = reiseLeistung.add(spesenLeistung == null ? ZERO : spesenLeistung);
    reiseLeistung = reiseLeistung.add(zuschlaegeLeistung == null ? ZERO : zuschlaegeLeistung);
    BigDecimal pauschaleProdukt = pauschaleAnzahl == null || pauschaleProTag == null ? ZERO
        : pauschaleAnzahl.multiply(pauschaleProTag).setScale(2, RoundingMode.HALF_UP);
    reiseLeistung = reiseLeistung.add(pauschaleProdukt);

    return reiseLeistung;
  }

  public boolean keineWerteVorhanden() {
    return (angerechneteReisezeit == null || angerechneteReisezeit.signum() == 0) &&
        (belegeLautArbeitsnachweisKosten == null || belegeLautArbeitsnachweisKosten.signum() == 0)
        &&
        (belegeViadeeKosten == null || belegeViadeeKosten.signum() == 0) &&
        (spesenKosten == null || spesenKosten.signum() == 0) &&
        (zuschlaegeKosten == null || zuschlaegeKosten.signum() == 0) &&
        (tatsaechlicheReisezeit == null || tatsaechlicheReisezeit.signum() == 0) &&
        (tatsaechlicheReisezeitInformatorisch == null
            || tatsaechlicheReisezeitInformatorisch.signum() == 0) &&
        (stundensatz == null || stundensatz.signum() == 0) &&
        (belegeLautArbeitsnachweisLeistung == null
            || belegeLautArbeitsnachweisLeistung.signum() == 0) &&
        (belegeViadeeLeistung == null || belegeViadeeLeistung.signum() == 0) &&
        (spesenLeistung == null || spesenLeistung.signum() == 0) &&
        (zuschlaegeLeistung == null || zuschlaegeLeistung.signum() == 0) &&
        (pauschaleAnzahl == null || pauschaleAnzahl.signum() == 0) &&
        (pauschaleProTag == null || pauschaleProTag.signum() == 0);
  }


}
