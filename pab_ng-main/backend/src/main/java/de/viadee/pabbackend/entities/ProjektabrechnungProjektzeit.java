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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ProjektabrechnungProjektzeit")
public class ProjektabrechnungProjektzeit {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektabrechnungId")
  private Long projektabrechnungId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("StundenANW")
  private BigDecimal stundenLautArbeitsnachweis;

  @Transient
  private BigDecimal stundenLautArbeitsnachweisOriginal;

  @Column("laufendeNummer")
  private int laufendeNummer;

  @Column("Stundensatz")
  private BigDecimal stundensatz;

  @Transient
  private BigDecimal stundensatzVormonat;

  @Transient
  private BigDecimal stundensatzVertrag;

  @Transient
  private BigDecimal kostensatzVertrag;

  @Column("Kostensatz")
  private BigDecimal kostensatz;

  @Transient
  private BigDecimal kostensatzVormonat;

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

  public BigDecimal getStundenLautArbeitsnachweis() {
    return stundenLautArbeitsnachweis;
  }

  public void setStundenLautArbeitsnachweis(final BigDecimal stundenLautArbeitsnachweis) {
    this.stundenLautArbeitsnachweis = stundenLautArbeitsnachweis;
  }

  public BigDecimal getStundenLautArbeitsnachweisOriginal() {
    return stundenLautArbeitsnachweisOriginal;
  }

  public void setStundenLautArbeitsnachweisOriginal(
      final BigDecimal stundenLautArbeitsnachweisOriginal) {
    this.stundenLautArbeitsnachweisOriginal = stundenLautArbeitsnachweisOriginal;
  }

  public int getLaufendeNummer() {
    return laufendeNummer;
  }

  public void setLaufendeNummer(final int laufendeNummer) {
    this.laufendeNummer = laufendeNummer;
  }

  public BigDecimal getStundensatz() {
    return stundensatz;
  }

  public void setStundensatz(final BigDecimal stundensatz) {
    this.stundensatz = stundensatz;
  }

  public BigDecimal getStundensatzVormonat() {
    return stundensatzVormonat;
  }

  public void setStundensatzVormonat(final BigDecimal stundensatzVormonat) {
    this.stundensatzVormonat = stundensatzVormonat;
  }

  public BigDecimal getStundensatzVertrag() {
    return stundensatzVertrag;
  }

  public void setStundensatzVertrag(final BigDecimal stundensatzVertrag) {
    this.stundensatzVertrag = stundensatzVertrag;
  }

  public BigDecimal getKostensatzVertrag() {
    return kostensatzVertrag;
  }

  public void setKostensatzVertrag(final BigDecimal kostensatzVertrag) {
    this.kostensatzVertrag = kostensatzVertrag;
  }

  public BigDecimal getKostensatz() {
    return kostensatz;
  }

  public void setKostensatz(final BigDecimal kostensatz) {
    this.kostensatz = kostensatz;
  }

  public BigDecimal getKostensatzVormonat() {
    return kostensatzVormonat;
  }

  public void setKostensatzVormonat(final BigDecimal kostensatzVormonat) {
    this.kostensatzVormonat = kostensatzVormonat;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public BigDecimal getLeistung() {
    return (stundenLautArbeitsnachweis == null ? ZERO : stundenLautArbeitsnachweis)
        .multiply(stundensatz == null ? ZERO : stundensatz)
        .setScale(2, RoundingMode.HALF_UP);
  }

  public boolean keineWerteVorhanden() {

    return (stundenLautArbeitsnachweis == null || stundenLautArbeitsnachweis.signum() == 0)
        && (stundenLautArbeitsnachweisOriginal == null
        || stundenLautArbeitsnachweisOriginal.signum() == 0)
        && (stundensatz == null || stundensatz.signum() == 0)
        && (stundensatzVormonat == null || stundensatzVormonat.signum() == 0)
        && (stundensatzVertrag == null || stundensatzVertrag.signum() == 0)
        && (kostensatzVertrag == null || kostensatzVertrag.signum() == 0)
        && (kostensatzVormonat == null || kostensatzVormonat.signum() == 0);
  }
}
