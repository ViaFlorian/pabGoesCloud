package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ProjektabrechnungProjektzeitVormonat {

  @Column("KostensatzVormonat")
  private BigDecimal kostensatzVormonat;

  @Column("StundensatzVormonat")
  private BigDecimal stundensatzVormonat;

  public BigDecimal getKostensatzVormonat() {
    return kostensatzVormonat;
  }

  public void setKostensatzVormonat(final BigDecimal kostensatzVormonat) {
    this.kostensatzVormonat = kostensatzVormonat;
  }

  public BigDecimal getStundensatzVormonat() {
    return stundensatzVormonat;
  }

  public void setStundensatzVormonat(final BigDecimal stundensatzVormonat) {
    this.stundensatzVormonat = stundensatzVormonat;
  }
}
