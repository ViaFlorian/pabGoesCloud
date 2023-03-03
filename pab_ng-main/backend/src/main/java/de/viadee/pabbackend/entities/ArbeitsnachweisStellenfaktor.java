package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ArbeitsnachweisStellenfaktor {

  private Integer monat;

  private BigDecimal stellenfaktor;

  public ArbeitsnachweisStellenfaktor(final Integer monat, final BigDecimal stellenfaktor) {
    this.monat = monat;
    this.stellenfaktor = stellenfaktor;
  }

  public Integer getMonat() {
    return monat;
  }

  public BigDecimal getStellenfaktor() {
    return stellenfaktor;
  }
}
