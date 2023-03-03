package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Skonto")
public class Skonto {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektID")
  private Long projektId;

  @Column("Wertstellung")
  private LocalDate wertstellung;

  @Column("ReferenzMonat")
  private Integer referenzMonat;

  @Column("ReferenzJahr")
  private Integer referenzJahr;

  @Column("BetragNetto")
  private BigDecimal skontoNettoBetrag;

  @Column("Umsatzsteuer")
  private BigDecimal umsatzsteuer;

  @Column("Bemerkung")
  private String bemerkung;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public LocalDate getWertstellung() {
    return wertstellung;
  }

  public void setWertstellung(final LocalDate wertstellung) {
    this.wertstellung = wertstellung;
  }

  public Integer getReferenzMonat() {
    return referenzMonat;
  }

  public void setReferenzMonat(final Integer referenzMonat) {
    this.referenzMonat = referenzMonat;
  }

  public Integer getReferenzJahr() {
    return referenzJahr;
  }

  public void setReferenzJahr(final Integer referenzJahr) {
    this.referenzJahr = referenzJahr;
  }

  public BigDecimal getSkontoNettoBetrag() {
    return skontoNettoBetrag;
  }

  public void setSkontoNettoBetrag(final BigDecimal skontoNettoBetrag) {
    this.skontoNettoBetrag = skontoNettoBetrag;
  }

  public BigDecimal getUmsatzsteuer() {
    return umsatzsteuer;
  }

  public void setUmsatzsteuer(final BigDecimal umsatzsteuer) {
    this.umsatzsteuer = umsatzsteuer;
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
}
