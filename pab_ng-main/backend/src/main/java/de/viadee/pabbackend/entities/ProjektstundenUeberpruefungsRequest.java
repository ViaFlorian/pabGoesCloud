package de.viadee.pabbackend.entities;

import java.util.List;

public class ProjektstundenUeberpruefungsRequest {

  private final Long mitarbeiterId;

  private final Integer jahr;

  private final Integer monat;

  private final List<Projektstunde> projektstunden;

  public ProjektstundenUeberpruefungsRequest(Long mitarbeiterId, Integer jahr, Integer monat,
      List<Projektstunde> projektstunden) {
    this.mitarbeiterId = mitarbeiterId;
    this.jahr = jahr;
    this.monat = monat;
    this.projektstunden = projektstunden;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public Integer getJahr() {
    return jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public List<Projektstunde> getProjektstunden() {
    return projektstunden;
  }
}
