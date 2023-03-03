package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class LeistungKumuliert {

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("Leistung")
  private BigDecimal leistung;

  public LeistungKumuliert(final Long mitarbeiterId, final BigDecimal leistung) {
    this.mitarbeiterId = mitarbeiterId;
    this.leistung = leistung;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public BigDecimal getLeistung() {
    return leistung == null ? BigDecimal.ZERO : leistung;
  }

  public void setLeistung(final BigDecimal leistung) {
    this.leistung = leistung;
  }
}
