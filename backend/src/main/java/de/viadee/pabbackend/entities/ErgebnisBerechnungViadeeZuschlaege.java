package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisBerechnungViadeeZuschlaege extends
    AbstractSpesenUndZuschlagsberechnungsErgebnis {

  private BigDecimal viadeeZuschlaege;

  public BigDecimal getViadeeZuschlaege() {
    return viadeeZuschlaege;
  }

  public void setViadeeZuschlaege(final BigDecimal viadeeZuschlaege) {
    this.viadeeZuschlaege = viadeeZuschlaege;
  }

}
