package de.viadee.pabbackend.entities;

import java.util.List;

public class ArbeitsnachweisAbrechnungErstellungsRequest {

  private final Arbeitsnachweis arbeitsnachweis;
  private final List<Projektstunde> projektstundenNormal;
  private final List<Projektstunde> reisezeiten;
  private final List<Projektstunde> sonderarbeitszeiten;
  private final List<Projektstunde> rufbereitschaften;
  private final List<Abwesenheit> abwesenheiten;
  private final List<Beleg> belege;

  public ArbeitsnachweisAbrechnungErstellungsRequest(Arbeitsnachweis arbeitsnachweis,
      List<Projektstunde> projektstundenNormal, List<Projektstunde> reisezeiten,
      List<Projektstunde> sonderarbeitszeiten, List<Projektstunde> rufbereitschaften,
      List<Abwesenheit> abwesenheiten, List<Beleg> belege) {
    this.arbeitsnachweis = arbeitsnachweis;
    this.projektstundenNormal = projektstundenNormal;
    this.reisezeiten = reisezeiten;
    this.sonderarbeitszeiten = sonderarbeitszeiten;
    this.rufbereitschaften = rufbereitschaften;
    this.abwesenheiten = abwesenheiten;
    this.belege = belege;
  }

  public Arbeitsnachweis getArbeitsnachweis() {
    return arbeitsnachweis;
  }

  public List<Projektstunde> getProjektstundenNormal() {
    return projektstundenNormal;
  }

  public List<Projektstunde> getReisezeiten() {
    return reisezeiten;
  }

  public List<Projektstunde> getSonderarbeitszeiten() {
    return sonderarbeitszeiten;
  }

  public List<Projektstunde> getRufbereitschaften() {
    return rufbereitschaften;
  }

  public List<Abwesenheit> getAbwesenheiten() {
    return abwesenheiten;
  }

  public List<Beleg> getBelege() {
    return belege;
  }
}
