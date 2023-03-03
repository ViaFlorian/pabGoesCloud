package de.viadee.pabbackend.entities;

public class ProjektNichtBereitFuerMonatsabschluss {


  private Projektabrechnung projektabrechnung;

  public Projektabrechnung getProjektabrechnung() {
    return projektabrechnung;
  }

  public void setProjektabrechnung(final Projektabrechnung projektabrechnung) {
    this.projektabrechnung = projektabrechnung;
  }

  public Long getProjektabrechnungID() {
    return projektabrechnung == null ? null : projektabrechnung.getId();
  }
}
