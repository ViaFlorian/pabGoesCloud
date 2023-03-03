package de.viadee.pabbackend.entities;

public class AuswertungAbwesenheitFuerAbrechnung {

  private Integer ueberAcht;

  private Integer anAb;

  private Integer spesenGanztaegig;

  private Integer fruehstueck;

  private Integer mittagessen;

  private Integer abendessen;

  private Integer zwischenSechsUndZehn;

  private Integer ueberZehn;

  private Integer zuschlaegeGanztaegig;

  public Integer getUeberAcht() {
    return ueberAcht;
  }

  public void setUeberAcht(Integer ueberAcht) {
    this.ueberAcht = ueberAcht;
  }

  public Integer getAnAb() {
    return anAb;
  }

  public void setAnAb(Integer anAb) {
    this.anAb = anAb;
  }

  public Integer getSpesenGanztaegig() {
    return spesenGanztaegig;
  }

  public void setSpesenGanztaegig(Integer spesenGanztaegig) {
    this.spesenGanztaegig = spesenGanztaegig;
  }

  public Integer getFruehstueck() {
    return fruehstueck;
  }

  public void setFruehstueck(Integer fruehstueck) {
    this.fruehstueck = fruehstueck;
  }

  public Integer getMittagessen() {
    return mittagessen;
  }

  public void setMittagessen(final Integer mittagessen) {
    this.mittagessen = mittagessen;
  }

  public Integer getAbendessen() {
    return abendessen;
  }

  public void setAbendessen(final Integer abendessen) {
    this.abendessen = abendessen;
  }

  public Integer getZwischenSechsUndZehn() {
    return zwischenSechsUndZehn;
  }

  public void setZwischenSechsUndZehn(Integer zwischenSechsUndZehn) {
    this.zwischenSechsUndZehn = zwischenSechsUndZehn;
  }

  public Integer getUeberZehn() {
    return ueberZehn;
  }

  public void setUeberZehn(Integer ueberZehn) {
    this.ueberZehn = ueberZehn;
  }

  public Integer getZuschlaegeGanztaegig() {
    return zuschlaegeGanztaegig;
  }

  public void setZuschlaegeGanztaegig(Integer zuschlaegeGanztaegig) {
    this.zuschlaegeGanztaegig = zuschlaegeGanztaegig;
  }
}
