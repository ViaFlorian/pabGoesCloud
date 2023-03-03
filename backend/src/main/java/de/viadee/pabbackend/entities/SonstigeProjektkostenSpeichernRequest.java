package de.viadee.pabbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class SonstigeProjektkostenSpeichernRequest {


  private List<SonstigeProjektkosten> geloeschteSonstigeProjektkosten = new ArrayList<>();
  private List<SonstigeProjektkosten> aktualisierteSonstigeProjektkosten = new ArrayList<>();
  private List<SonstigeProjektkosten> neueSonstigeProjektkosten = new ArrayList<>();
  private List<SonstigeProjektkosten> listeZumAenderungsvergleich = new ArrayList<>();

  public List<SonstigeProjektkosten> getGeloeschteSonstigeProjektkosten() {
    return geloeschteSonstigeProjektkosten;
  }

  public void setGeloeschteSonstigeProjektkosten(
      List<SonstigeProjektkosten> geloeschteSonstigeProjektkosten) {
    this.geloeschteSonstigeProjektkosten = geloeschteSonstigeProjektkosten;
  }

  public List<SonstigeProjektkosten> getAktualisierteSonstigeProjektkosten() {
    return aktualisierteSonstigeProjektkosten;
  }

  public void setAktualisierteSonstigeProjektkosten(
      List<SonstigeProjektkosten> aktualisierteSonstigeProjektkosten) {
    this.aktualisierteSonstigeProjektkosten = aktualisierteSonstigeProjektkosten;
  }

  public List<SonstigeProjektkosten> getNeueSonstigeProjektkosten() {
    return neueSonstigeProjektkosten;
  }

  public void setNeueSonstigeProjektkosten(
      List<SonstigeProjektkosten> neueSonstigeProjektkosten) {
    this.neueSonstigeProjektkosten = neueSonstigeProjektkosten;
  }

  public List<SonstigeProjektkosten> getListeZumAenderungsvergleich() {
    return listeZumAenderungsvergleich;
  }

  public void setListeZumAenderungsvergleich(
      List<SonstigeProjektkosten> listeZumAenderungsvergleich) {
    this.listeZumAenderungsvergleich = listeZumAenderungsvergleich;
  }
}
