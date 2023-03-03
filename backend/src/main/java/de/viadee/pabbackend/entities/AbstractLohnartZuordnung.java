package de.viadee.pabbackend.entities;

import de.viadee.pabbackend.enums.Lohnarten;
import java.math.BigDecimal;

public abstract class AbstractLohnartZuordnung {

  private Lohnarten lohnart;

  private BigDecimal wert;

  public Lohnarten getLohnart() {
    return lohnart;
  }

  public void setLohnart(final Lohnarten lohnart) {
    this.lohnart = lohnart;
  }

  public BigDecimal getWert() {
    return wert;
  }

  public void setWert(final BigDecimal wert) {
    this.wert = wert;
  }

  public abstract String getEinheit();

}
