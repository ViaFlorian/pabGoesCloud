package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Projektabrechnung")
public class Projektabrechnung {

  @Id
  @Column("ID")
  private Long id;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("ProjektID")
  private Long projektId;

  @Column("StatusID")
  private Integer statusId;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @Transient
  private BigDecimal kosten;

  @Transient
  private BigDecimal umsatz;

  @Column("BudgetBetragZurAbrechnung")
  private BigDecimal budgetBetragZurAbrechnung;

  @Column("Fertigstellungsgrad")
  private BigDecimal fertigstellungsgrad;

  @Transient
  private BigDecimal fertigstellungsgradAlt;

  @Transient
  private boolean korrekturVorhanden;

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

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(final Integer statusId) {
    this.statusId = statusId;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(final BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getUmsatz() {
    return umsatz;
  }

  public void setUmsatz(final BigDecimal umsatz) {
    this.umsatz = umsatz;
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

  public BigDecimal getBudgetBetragZurAbrechnung() {
    return budgetBetragZurAbrechnung;
  }

  public void setBudgetBetragZurAbrechnung(final BigDecimal budgetBetragZurAbrechnung) {
    this.budgetBetragZurAbrechnung = budgetBetragZurAbrechnung;
  }

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(final BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public BigDecimal getFertigstellungsgradAlt() {
    return fertigstellungsgradAlt;
  }

  public void setFertigstellungsgradAlt(final BigDecimal fertigstellungsgradAlt) {
    this.fertigstellungsgradAlt = fertigstellungsgradAlt;
  }

  public Boolean getKorrekturVorhanden() {
    return korrekturVorhanden;
  }

  public void setKorrekturVorhanden(final Boolean korrekturVorhanden) {
    this.korrekturVorhanden = korrekturVorhanden;
  }
}
