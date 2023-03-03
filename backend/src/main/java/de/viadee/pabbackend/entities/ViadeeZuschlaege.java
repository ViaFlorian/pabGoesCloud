package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("C_ViadeeZuschlaege")
public class ViadeeZuschlaege {

  @Id
  @Column("ID")
  private Long id;

  @Column("StundenAbwesendVon")
  private BigDecimal stundenAbwesendVon;

  @Column("vonInklusive")
  private boolean vonInklusive;

  @Column("StundenAbwesendBis")
  private BigDecimal stundenAbwesendBis;

  @Column("bisInklusive")
  private boolean bisInklusive;

  @Column("Betrag")
  private BigDecimal betrag;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public BigDecimal getStundenAbwesendVon() {
    return stundenAbwesendVon;
  }

  public void setStundenAbwesendVon(final BigDecimal stundenAbwesendVon) {
    this.stundenAbwesendVon = stundenAbwesendVon;
  }

  public boolean isVonInklusive() {
    return vonInklusive;
  }

  public void setVonInklusive(final boolean vonInklusive) {
    this.vonInklusive = vonInklusive;
  }

  public BigDecimal getStundenAbwesendBis() {
    return stundenAbwesendBis;
  }

  public void setStundenAbwesendBis(final BigDecimal stundenAbwesendBis) {
    this.stundenAbwesendBis = stundenAbwesendBis;
  }

  public boolean isBisInklusive() {
    return bisInklusive;
  }

  public void setBisInklusive(final boolean bisInklusive) {
    this.bisInklusive = bisInklusive;
  }

  public BigDecimal getBetrag() {
    return betrag;
  }

  public void setBetrag(final BigDecimal betrag) {
    this.betrag = betrag;
  }

}
