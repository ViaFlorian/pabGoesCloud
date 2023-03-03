package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SichererSaldoStand {

  LocalDate wertstellung;

  BigDecimal saldo;

  public LocalDate getWertstellung() {
    return wertstellung;
  }

  public void setWertstellung(LocalDate wertstellung) {
    this.wertstellung = wertstellung;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }
}
