package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class SonderarbeitszeitWochentagVerteilung {

  private BigDecimal werktag;

  private BigDecimal samstag;

  private BigDecimal sonntagFeiertag;

  public BigDecimal getWerktag() {
    return werktag;
  }

  public void setWerktag(final BigDecimal werktag) {
    this.werktag = werktag;
  }

  public BigDecimal getSamstag() {
    return samstag;
  }

  public void setSamstag(final BigDecimal samstag) {
    this.samstag = samstag;
  }

  public BigDecimal getSonntagFeiertag() {
    return sonntagFeiertag;
  }

  public void setSonntagFeiertag(final BigDecimal sonntagFeiertag) {
    this.sonntagFeiertag = sonntagFeiertag;
  }
}
