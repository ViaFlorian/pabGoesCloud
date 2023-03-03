package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("MitarbeiterStellenfaktor")
public class MitarbeiterStellenfaktor {

  @Id
  @Column("ID")
  private Long id;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("Stellenfaktor")
  private BigDecimal stellenfaktor;

  @Column("GueltigAb")
  private LocalDate gueltigAb;

  @Column("GueltigBis")
  private LocalDate gueltigBis;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public BigDecimal getStellenfaktor() {
    return stellenfaktor;
  }

  public void setStellenfaktor(final BigDecimal stellenfaktor) {
    this.stellenfaktor = stellenfaktor;
  }

  public LocalDate getGueltigAb() {
    return gueltigAb;
  }

  public void setGueltigAb(final LocalDate gueltigAb) {
    this.gueltigAb = gueltigAb;
  }

  public LocalDate getGueltigBis() {
    return gueltigBis;
  }

  public void setGueltigBis(final LocalDate gueltigBis) {
    this.gueltigBis = gueltigBis;
  }
}
