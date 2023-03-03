package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisBerechnungGesetzlicheSpesen extends
    AbstractSpesenUndZuschlagsberechnungsErgebnis {

  private BigDecimal gesetzlicheSpesen;

  public BigDecimal getGesetzlicheSpesen() {
    return gesetzlicheSpesen;
  }

  public void setGesetzlicheSpesen(final BigDecimal gesetzlicheSpesen) {
    this.gesetzlicheSpesen = gesetzlicheSpesen;
  }

}
