package de.viadee.pabbackend.entities;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Projektbudget")
public class ProjektBudget {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektID")
  private Long projektId;

  @Column("Wertstellung")
  private LocalDate wertstellung;

  @Column("Buchungsdatum")
  private LocalDateTime buchungsdatum;

  @Column("BudgetBetrag")
  private BigDecimal budgetBetrag;

  @Column("Bemerkung")
  private String bemerkung;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @Transient
  private BigDecimal saldoBerechnet;

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

  public LocalDateTime getBuchungsdatum() {
    return buchungsdatum;
  }

  public void setBuchungsdatum(final LocalDateTime buchungsdatum) {
    this.buchungsdatum = buchungsdatum;
  }

  public BigDecimal getBudgetBetrag() {
    return budgetBetrag;
  }

  public void setBudgetBetrag(final BigDecimal budgetBetrag) {
    this.budgetBetrag = budgetBetrag;
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

  public BigDecimal getSaldoBerechnet() {
    return saldoBerechnet;
  }

  public void setSaldoBerechnet(final BigDecimal saldoBerechnet) {
    this.saldoBerechnet = saldoBerechnet;
  }
}
