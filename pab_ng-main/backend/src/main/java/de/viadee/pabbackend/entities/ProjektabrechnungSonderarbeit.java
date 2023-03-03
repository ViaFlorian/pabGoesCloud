package de.viadee.pabbackend.entities;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ProjektabrechnungSonderarbeit")
public class ProjektabrechnungSonderarbeit {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("RufbereitschaftKostenAnzahlStunden")
  private BigDecimal rufbereitschaftKostenAnzahlStunden;

  @Column("RufbereitschaftKostensatz")
  private BigDecimal rufbereitschaftKostensatz;

  @Column("RufbereitschaftLeistungAnzahlStunden")
  private BigDecimal rufbereitschaftLeistungAnzahlStunden;

  @Column("RufbereitschaftStundensatz")
  private BigDecimal rufbereitschaftStundensatz;

  @Column("SonderarbeitLeistungPauschale")
  private BigDecimal rufbereitschaftLeistungPauschale;

  @Column("SonderarbeitAnzahlStunden50")
  private BigDecimal sonderarbeitAnzahlStunden50;

  @Column("SonderarbeitAnzahlStunden100")
  private BigDecimal sonderarbeitAnzahlStunden100;

  @Column("SonderarbeitKostensatz")
  private BigDecimal sonderarbeitKostensatz;

  @Column("SonderarbeitPauschale")
  private BigDecimal sonderarbeitPauschale;

  @Column("SonderarbeitLeistungPauschale")
  private BigDecimal sonderarbeitLeistungPauschale;

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

  public BigDecimal getRufbereitschaftKostenAnzahlStunden() {
    return rufbereitschaftKostenAnzahlStunden;
  }

  public void setRufbereitschaftKostenAnzahlStunden(
      final BigDecimal rufbereitschaftKostenAnzahlStunden) {
    this.rufbereitschaftKostenAnzahlStunden = rufbereitschaftKostenAnzahlStunden;
  }

  public BigDecimal getRufbereitschaftKostensatz() {
    return rufbereitschaftKostensatz;
  }

  public void setRufbereitschaftKostensatz(final BigDecimal rufbereitschaftKostensatz) {
    this.rufbereitschaftKostensatz = rufbereitschaftKostensatz;
  }

  public BigDecimal getRufbereitschaftLeistungAnzahlStunden() {
    return rufbereitschaftLeistungAnzahlStunden;
  }

  public void setRufbereitschaftLeistungAnzahlStunden(
      final BigDecimal rufbereitschaftLeistungAnzahlStunden) {
    this.rufbereitschaftLeistungAnzahlStunden = rufbereitschaftLeistungAnzahlStunden;
  }

  public BigDecimal getRufbereitschaftStundensatz() {
    return rufbereitschaftStundensatz;
  }

  public void setRufbereitschaftStundensatz(final BigDecimal rufbereitschaftStundensatz) {
    this.rufbereitschaftStundensatz = rufbereitschaftStundensatz;
  }

  public BigDecimal getRufbereitschaftLeistungPauschale() {
    return rufbereitschaftLeistungPauschale;
  }

  public void setRufbereitschaftLeistungPauschale(
      final BigDecimal rufbereitschaftLeistungPauschale) {
    this.rufbereitschaftLeistungPauschale = rufbereitschaftLeistungPauschale;
  }

  public BigDecimal getSonderarbeitAnzahlStunden50() {
    return sonderarbeitAnzahlStunden50;
  }

  public void setSonderarbeitAnzahlStunden50(final BigDecimal sonderarbeitAnzahlStunden50) {
    this.sonderarbeitAnzahlStunden50 = sonderarbeitAnzahlStunden50;
  }

  public BigDecimal getSonderarbeitAnzahlStunden100() {
    return sonderarbeitAnzahlStunden100;
  }

  public void setSonderarbeitAnzahlStunden100(final BigDecimal sonderarbeitAnzahlStunden100) {
    this.sonderarbeitAnzahlStunden100 = sonderarbeitAnzahlStunden100;
  }

  public BigDecimal getSonderarbeitKostensatz() {
    return sonderarbeitKostensatz;
  }

  public void setSonderarbeitKostensatz(final BigDecimal sonderarbeitKostensatz) {
    this.sonderarbeitKostensatz = sonderarbeitKostensatz;
  }

  public BigDecimal getSonderarbeitPauschale() {
    return sonderarbeitPauschale;
  }

  public void setSonderarbeitPauschale(final BigDecimal sonderarbeitPauschale) {
    this.sonderarbeitPauschale = sonderarbeitPauschale;
  }

  public BigDecimal getSonderarbeitLeistungPauschale() {
    return sonderarbeitLeistungPauschale;
  }

  public void setSonderarbeitLeistungPauschale(final BigDecimal sonderarbeitLeistungPauschale) {
    this.sonderarbeitLeistungPauschale = sonderarbeitLeistungPauschale;
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

  public BigDecimal getSonderzeitKosten() {

    BigDecimal anzahlStundenRB =
        rufbereitschaftKostenAnzahlStunden == null ? ZERO : rufbereitschaftKostenAnzahlStunden;
    BigDecimal kostensatzRB = rufbereitschaftKostensatz == null ? ZERO : rufbereitschaftKostensatz;
    BigDecimal rufbereitschaftKosten = anzahlStundenRB.multiply(kostensatzRB).setScale(2, HALF_UP);

    BigDecimal stunden50 = sonderarbeitAnzahlStunden50 == null ? ZERO : sonderarbeitAnzahlStunden50;
    BigDecimal stunden100 =
        sonderarbeitAnzahlStunden100 == null ? ZERO : sonderarbeitAnzahlStunden100;
    BigDecimal kostensatz = sonderarbeitKostensatz == null ? ZERO : sonderarbeitKostensatz;
    BigDecimal pauschale = sonderarbeitPauschale == null ? ZERO : sonderarbeitPauschale;
    BigDecimal sonderarbeitKosten = pauschale.add(
        kostensatz.multiply(stunden50.multiply(new BigDecimal(0.5)).add(stunden100)));

    return sonderarbeitKosten.add(rufbereitschaftKosten).setScale(2, HALF_UP);

  }

  public BigDecimal getSonderzeitLeistung() {
    BigDecimal anzahlStundenRB =
        rufbereitschaftLeistungAnzahlStunden == null ? ZERO : rufbereitschaftLeistungAnzahlStunden;
    BigDecimal stundensatz = rufbereitschaftStundensatz == null ? ZERO : rufbereitschaftStundensatz;
    BigDecimal pauschale =
        rufbereitschaftLeistungPauschale == null ? ZERO : rufbereitschaftLeistungPauschale;
    BigDecimal rufbereitschaftLeistung = anzahlStundenRB.multiply(stundensatz).add(pauschale);

    BigDecimal sonderzeitPauschale =
        sonderarbeitLeistungPauschale == null ? ZERO : sonderarbeitLeistungPauschale;

    return rufbereitschaftLeistung.add(sonderzeitPauschale).setScale(2, HALF_UP);
  }

  public boolean keineWerteVorhanden() {
    return (rufbereitschaftKostenAnzahlStunden == null
        || rufbereitschaftKostenAnzahlStunden.signum() == 0) &&
        (rufbereitschaftLeistungAnzahlStunden == null
            || rufbereitschaftLeistungAnzahlStunden.signum() == 0) &&
        (rufbereitschaftStundensatz == null || rufbereitschaftStundensatz.signum() == 0) &&
        (rufbereitschaftLeistungPauschale == null || rufbereitschaftLeistungPauschale.signum() == 0)
        &&
        (sonderarbeitAnzahlStunden50 == null || sonderarbeitAnzahlStunden50.signum() == 0) &&
        (sonderarbeitAnzahlStunden100 == null || sonderarbeitAnzahlStunden100.signum() == 0) &&
        (sonderarbeitKostensatz == null || sonderarbeitKostensatz.signum() == 0) &&
        (sonderarbeitPauschale == null || sonderarbeitPauschale.signum() == 0) &&
        (sonderarbeitLeistungPauschale == null || sonderarbeitLeistungPauschale.signum() == 0);
  }

}
