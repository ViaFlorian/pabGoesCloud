package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ArbeitsnachweisLohnartZuordnung")
public class ArbeitsnachweisLohnartZuordnung implements
    Comparable<ArbeitsnachweisLohnartZuordnung> {

  @Id
  @Column("ID")
  private Long id;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Column("LohnartID")
  private Long lohnartId;

  @Column("Betrag")
  private BigDecimal betrag;

  @Column("Einheit")
  private String einheit;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }
  
  @JsonSerialize(using = ToStringSerializer.class)
  public Long getLohnartId() {
    return lohnartId;
  }

  public void setLohnartId(final Long lohnartId) {
    this.lohnartId = lohnartId;
  }

  public BigDecimal getBetrag() {
    return betrag;
  }

  public void setBetrag(final BigDecimal betrag) {
    this.betrag = betrag;
  }

  public String getEinheit() {
    return einheit;
  }

  public void setEinheit(final String einheit) {
    this.einheit = einheit;
  }

  @Override
  public int compareTo(final ArbeitsnachweisLohnartZuordnung other) {
    return getLohnartId().compareTo(other.getLohnartId());
  }

}
