package de.viadee.pabbackend.entities;

import org.springframework.data.relational.core.mapping.Column;

public class ProjektAbrechnungsmonat {

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("Abgeschlossen")
  private Boolean abgeschlossen;

  public ProjektAbrechnungsmonat(Long projektabrechnungId, Integer jahr, Integer monat,
      Boolean abgeschlossen) {
    this.projektabrechnungId = projektabrechnungId;
    this.jahr = jahr;
    this.monat = monat;
    this.abgeschlossen = abgeschlossen;
  }

  public Long getProjektabrechnungId() {
    return projektabrechnungId;
  }

  public void setProjektabrechnungId(Long projektabrechnungId) {
    this.projektabrechnungId = projektabrechnungId;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(Integer monat) {
    this.monat = monat;
  }

  public Boolean isAbgeschlossen() {
    return abgeschlossen;
  }

  public void setAbgeschlossen(final Boolean abgeschlossen) {
    this.abgeschlossen = abgeschlossen;
  }

}
