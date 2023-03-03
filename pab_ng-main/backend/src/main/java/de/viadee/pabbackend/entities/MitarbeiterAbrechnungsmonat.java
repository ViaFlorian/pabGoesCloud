package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.relational.core.mapping.Column;

public class MitarbeiterAbrechnungsmonat {

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("istAbgeschlossen")
  private Boolean abgeschlossen;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(Integer monat) {
    this.monat = monat;
  }

  public Boolean isAbgeschlossen() {
    return abgeschlossen;
  }

  public void setAbgeschlossen(final Boolean abgeschlossen) {
    this.abgeschlossen = abgeschlossen;
  }
}
