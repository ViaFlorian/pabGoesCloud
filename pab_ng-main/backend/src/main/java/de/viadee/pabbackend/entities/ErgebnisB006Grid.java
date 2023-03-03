package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB006Grid implements Comparable<ErgebnisB006Grid> {

  private Mitarbeiter mitarbeiter;

  private BigDecimal summeStunden;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullname() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public BigDecimal getSummeStunden() {
    return summeStunden;
  }

  public void setSummeStunden(final BigDecimal summeStunden) {
    this.summeStunden = summeStunden;
  }

  @Override
  public int compareTo(ErgebnisB006Grid that) {
    if (this.mitarbeiter.compareTo(that.mitarbeiter) < 0) {
      return -1;
    } else if (this.mitarbeiter.compareTo(that.mitarbeiter) > 0) {
      return 1;
    }
    return 0;
  }
}
