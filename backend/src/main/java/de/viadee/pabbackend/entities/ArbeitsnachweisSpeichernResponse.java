package de.viadee.pabbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class ArbeitsnachweisSpeichernResponse {

  private final List<Long> zurueckgesetzteProjekte = new ArrayList<>();
  private final List<String> meldungen = new ArrayList<>();

  private Arbeitsnachweis arbeitsnachweis;

  public List<Long> getZurueckgesetzteProjekte() {
    return zurueckgesetzteProjekte;
  }

  public List<String> getMeldungen() {
    return meldungen;
  }

  public Arbeitsnachweis getArbeitsnachweis() {
    return arbeitsnachweis;
  }

  public void setArbeitsnachweis(Arbeitsnachweis arbeitsnachweis) {
    this.arbeitsnachweis = arbeitsnachweis;
  }
}
