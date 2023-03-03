package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.relational.core.mapping.Column;

public class ProjektabrechnungMitarbeiterPair {

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("mitarbeiterID")
  private Long mitarbeiterId;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektabrechnungId() {
    return projektabrechnungId;
  }

  public void setProjektabrechnungId(Long projektabrechnungId) {
    this.projektabrechnungId = projektabrechnungId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }
}
