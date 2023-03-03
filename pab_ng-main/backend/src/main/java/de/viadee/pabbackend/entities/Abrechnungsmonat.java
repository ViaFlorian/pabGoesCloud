package de.viadee.pabbackend.entities;

import java.util.Objects;
import org.springframework.data.relational.core.mapping.Column;

public class Abrechnungsmonat implements Comparable<Abrechnungsmonat> {

  @Column("Jahr")
  private final Integer jahr;

  @Column("Monat")
  private final Integer monat;

  @Column("istAbgeschlossen")
  private Boolean abgeschlossen;

  public Abrechnungsmonat(Integer jahr, Integer monat, Boolean abgeschlossen) {
    this.jahr = jahr;
    this.monat = monat;
    this.abgeschlossen = abgeschlossen;
  }

  public Integer getJahr() {
    return jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public Boolean isAbgeschlossen() {
    return abgeschlossen;
  }

  public void setAbgeschlossen(final Boolean abgeschlossen) {
    this.abgeschlossen = abgeschlossen;
  }

  @Override
  public String toString() {
    return jahr.toString().concat("/").concat(String.format("%02d", monat));
  }

  @Override
  public int compareTo(Abrechnungsmonat that) {
    if (this.jahr.compareTo(that.jahr) > 0) {
      return -1;
    } else if (this.jahr.compareTo(that.jahr) < 0) {
      return 1;
    }

    if (this.monat.compareTo(that.monat) > 0) {
      return -1;
    } else if (this.monat.compareTo(that.monat) < 0) {
      return 1;
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Abrechnungsmonat that = (Abrechnungsmonat) o;
    return Objects.equals(jahr, that.jahr) &&
        Objects.equals(monat, that.monat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), jahr, monat);
  }
}
