package de.viadee.pabbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class SonstigeProjektkostenSpeichernResponse {

  private List<Long> zurueckgesetzteProjekte = new ArrayList<>();
  private String meldungen;

  public List<Long> getZurueckgesetzteProjekte() {
    return zurueckgesetzteProjekte;
  }

  public void setZurueckgesetzteProjekte(List<Long> zurueckgesetzteProjekte) {
    this.zurueckgesetzteProjekte = zurueckgesetzteProjekte;
  }

  public String getMeldungen() {
    return meldungen;
  }

  public void setMeldungen(String meldungen) {
    this.meldungen = meldungen;
  }
}
