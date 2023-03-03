package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ArbeitsnachweisUebersicht implements Comparable<ArbeitsnachweisUebersicht> {

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("StatusID")
  private Integer statusId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("SachbearbeiterID")
  private Long sachbearbeiterId;

  @Column("SummeProjektstunden")
  private BigDecimal summeProjektstunden;

  @Column("SummeSpesen")
  private BigDecimal summeSpesen;

  @Column("SummeBelege")
  private BigDecimal summeBelege;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
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

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(final Integer statusId) {
    this.statusId = statusId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getSachbearbeiterId() {
    return sachbearbeiterId;
  }

  public void setSachbearbeiterId(final Long sachbearbeiterId) {
    this.sachbearbeiterId = sachbearbeiterId;
  }

  public BigDecimal getSummeProjektstunden() {
    return summeProjektstunden;
  }

  public void setSummeProjektstunden(final BigDecimal summeProjektstunden) {
    this.summeProjektstunden = summeProjektstunden;
  }

  public BigDecimal getSummeSpesen() {
    return summeSpesen;
  }

  public void setSummeSpesen(final BigDecimal summeSpesen) {
    this.summeSpesen = summeSpesen;
  }

  public BigDecimal getSummeBelege() {
    return summeBelege;
  }

  public void setSummeBelege(final BigDecimal summeBelege) {
    this.summeBelege = summeBelege;
  }

  @Override
  public int compareTo(final ArbeitsnachweisUebersicht zuVergleichenderArbeitsnachweis) {

    // Zunächst wird das Jahr der jeweiligen Arbeitsnachweise verglichen
    int result = 0;

    if (zuVergleichenderArbeitsnachweis != null && zuVergleichenderArbeitsnachweis.getJahr() != null
        && zuVergleichenderArbeitsnachweis.getMonat() != null) {
      result = jahr.compareTo(zuVergleichenderArbeitsnachweis.jahr);
      // Ist das Jahr gleich, muss der Monat verglichen werden
      if (result == 0) {
        result = monat.compareTo(zuVergleichenderArbeitsnachweis.monat);
      }
    } else {
      result = 1;
    }

    return result;
  }

}
