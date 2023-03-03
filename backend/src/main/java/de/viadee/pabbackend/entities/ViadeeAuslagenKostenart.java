package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class ViadeeAuslagenKostenart {

  private Long id;

  private String bezeichnung;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(final String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }
}
