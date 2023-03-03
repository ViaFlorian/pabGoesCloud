package de.viadee.pabbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class FakturuebersichtSpeichernRequest {

  private List<Faktur> geloeschteFakturen = new ArrayList<>();
  private List<Faktur> aktualisierteFakturen = new ArrayList<>();
  private List<Faktur> neueFakturen = new ArrayList<>();

  public List<Faktur> getGeloeschteFakturen() {
    return geloeschteFakturen;
  }

  public void setGeloeschteFakturen(
      List<Faktur> geloeschteFakturen) {
    this.geloeschteFakturen = geloeschteFakturen;
  }

  public List<Faktur> getAktualisierteFakturen() {
    return aktualisierteFakturen;
  }

  public void setAktualisierteFakturen(
      List<Faktur> aktualisierteFakturen) {
    this.aktualisierteFakturen = aktualisierteFakturen;
  }

  public List<Faktur> getNeueFakturen() {
    return neueFakturen;
  }

  public void setNeueFakturen(List<Faktur> neueFakturen) {
    this.neueFakturen = neueFakturen;
  }

}
