package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB004Uebersicht implements Comparable<ErgebnisB004Uebersicht> {

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("SachbearbeiterID")
  private Long sachbearbeiterId;

  @Column("StatusID")
  private Integer statusId;

  @Column("summeNormal")
  private BigDecimal projektstunden;

  @Column("summeReise")
  private BigDecimal reisezeit;

  @Column("summeSonderarbeitszeit")
  private BigDecimal sonderarbeitszeit;

  @Column("summeRufbereitschaft")
  private BigDecimal rufbereitschaft;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisID;

  public Long getArbeitsnachweisID() {
    return arbeitsnachweisID;
  }

  public void setArbeitsnachweisID(Long arbeitsnachweisID) {
    this.arbeitsnachweisID = arbeitsnachweisID;
  }


  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getSachbearbeiterId() {
    return sachbearbeiterId;
  }

  public void setSachbearbeiterId(Long sachbearbeiterId) {
    this.sachbearbeiterId = sachbearbeiterId;
  }

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(Integer statusId) {
    this.statusId = statusId;
  }

  public BigDecimal getProjektstunden() {
    return projektstunden;
  }

  public void setProjektstunden(BigDecimal projektstunden) {
    this.projektstunden = projektstunden;
  }

  public BigDecimal getReisezeit() {
    return reisezeit;
  }

  public void setReisezeit(BigDecimal reisezeit) {
    this.reisezeit = reisezeit;
  }

  public BigDecimal getSonderarbeitszeit() {
    return sonderarbeitszeit;
  }

  public void setSonderarbeitszeit(BigDecimal sonderarbeitszeit) {
    this.sonderarbeitszeit = sonderarbeitszeit;
  }

  public BigDecimal getRufbereitschaft() {
    return rufbereitschaft;
  }

  public void setRufbereitschaft(BigDecimal rufbereitschaft) {
    this.rufbereitschaft = rufbereitschaft;
  }


  @Override
  public int compareTo(ErgebnisB004Uebersicht that) {
    if (this.mitarbeiterId.compareTo(that.mitarbeiterId) < 0) {
      return -1;
    } else if (this.mitarbeiterId.compareTo(that.mitarbeiterId) > 0) {
      return 1;
    }
    return 0;
  }
}
