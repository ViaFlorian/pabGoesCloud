package de.viadee.pabbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class ArbeitsnachweisSpeichernRequest {

  private Arbeitsnachweis arbeitsnachweis;
  private Long mitarbeiterId;
  private List<Projektstunde> geloeschteProjektstunden = new ArrayList<>();
  private List<Projektstunde> aktualisierteProjektstunden = new ArrayList<>();
  private List<Projektstunde> neueProjektstunden = new ArrayList<>();

  private List<Projektstunde> geloschteSonderarbeit = new ArrayList<>();

  private List<Projektstunde> aktualisierteSonderabeit = new ArrayList<>();

  private List<Projektstunde> neueSonderarbeit = new ArrayList<>();

  private List<Projektstunde> geloeschteRufbereitschaft = new ArrayList<>();

  private List<Projektstunde> aktualisierteRufbereitschaft = new ArrayList<>();

  private List<Projektstunde> neueRufbereitschaft = new ArrayList<>();

  private List<Abwesenheit> neueAbwesenheiten = new ArrayList<>();
  private List<Abwesenheit> aktualisierteAbwesenheiten = new ArrayList<>();
  private List<Abwesenheit> geloeschteAbwesenheiten = new ArrayList<>();

  private List<Beleg> neueBelege = new ArrayList<>();

  private List<Beleg> aktualisierteBelege = new ArrayList<>();

  private List<Beleg> geloeschteBelege = new ArrayList<>();

  private List<DreiMonatsRegel> berechneteDreiMonatsRegeln = new ArrayList<>();

  public Arbeitsnachweis getArbeitsnachweis() {
    return arbeitsnachweis;
  }

  public void setArbeitsnachweis(Arbeitsnachweis arbeitsnachweis) {
    this.arbeitsnachweis = arbeitsnachweis;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public List<Projektstunde> getGeloeschteProjektstunden() {
    return geloeschteProjektstunden;
  }

  public void setGeloeschteProjektstunden(List<Projektstunde> geloeschteProjektstunden) {
    this.geloeschteProjektstunden = geloeschteProjektstunden;
  }

  public List<Projektstunde> getAktualisierteProjektstunden() {
    return aktualisierteProjektstunden;
  }

  public void setAktualisierteProjektstunden(List<Projektstunde> aktualisierteProjektstunden) {
    this.aktualisierteProjektstunden = aktualisierteProjektstunden;
  }

  public List<Projektstunde> getNeueProjektstunden() {
    return neueProjektstunden;
  }

  public void setNeueProjektstunden(List<Projektstunde> neueProjektstunden) {
    this.neueProjektstunden = neueProjektstunden;
  }

  public List<Abwesenheit> getNeueAbwesenheiten() {
    return neueAbwesenheiten;
  }

  public void setNeueAbwesenheiten(List<Abwesenheit> neueAbwesenheiten) {
    this.neueAbwesenheiten = neueAbwesenheiten;
  }

  public List<Abwesenheit> getAktualisierteAbwesenheiten() {
    return aktualisierteAbwesenheiten;
  }

  public void setAktualisierteAbwesenheiten(List<Abwesenheit> aktualisierteAbwesenheiten) {
    this.aktualisierteAbwesenheiten = aktualisierteAbwesenheiten;
  }

  public List<Abwesenheit> getGeloeschteAbwesenheiten() {
    return geloeschteAbwesenheiten;
  }

  public void setGeloeschteAbwesenheiten(List<Abwesenheit> geloeschteAbwesenheiten) {
    this.geloeschteAbwesenheiten = geloeschteAbwesenheiten;
  }

  public List<Projektstunde> getGeloschteSonderarbeit() {
    return geloschteSonderarbeit;
  }

  public void setGeloschteSonderarbeit(
      List<Projektstunde> geloschteSonderarbeit) {
    this.geloschteSonderarbeit = geloschteSonderarbeit;
  }

  public List<Projektstunde> getAktualisierteSonderabeit() {
    return aktualisierteSonderabeit;
  }

  public void setAktualisierteSonderabeit(
      List<Projektstunde> aktualisierteSonderabeit) {
    this.aktualisierteSonderabeit = aktualisierteSonderabeit;
  }

  public List<Projektstunde> getNeueSonderarbeit() {
    return neueSonderarbeit;
  }

  public void setNeueSonderarbeit(
      List<Projektstunde> neueSonderarbeit) {
    this.neueSonderarbeit = neueSonderarbeit;
  }

  public List<Projektstunde> getGeloeschteRufbereitschaft() {
    return geloeschteRufbereitschaft;
  }

  public void setGeloeschteRufbereitschaft(
      List<Projektstunde> geloeschteRufbereitschaft) {
    this.geloeschteRufbereitschaft = geloeschteRufbereitschaft;
  }

  public List<Projektstunde> getAktualisierteRufbereitschaft() {
    return aktualisierteRufbereitschaft;
  }

  public void setAktualisierteRufbereitschaft(
      List<Projektstunde> aktualisierteRufbereitschaft) {
    this.aktualisierteRufbereitschaft = aktualisierteRufbereitschaft;
  }

  public List<Projektstunde> getNeueRufbereitschaft() {
    return neueRufbereitschaft;
  }

  public void setNeueRufbereitschaft(
      List<Projektstunde> neueRufbereitschaft) {
    this.neueRufbereitschaft = neueRufbereitschaft;
  }

  public List<Beleg> getNeueBelege() {
    return neueBelege;
  }

  public void setNeueBelege(List<Beleg> neueBelege) {
    this.neueBelege = neueBelege;
  }

  public List<Beleg> getAktualisierteBelege() {
    return aktualisierteBelege;
  }

  public void setAktualisierteBelege(List<Beleg> aktualisierteBelege) {
    this.aktualisierteBelege = aktualisierteBelege;
  }

  public List<Beleg> getGeloeschteBelege() {
    return geloeschteBelege;
  }

  public void setGeloeschteBelege(List<Beleg> geloeschteBelege) {
    this.geloeschteBelege = geloeschteBelege;
  }

  public List<DreiMonatsRegel> getBerechneteDreiMonatsRegeln() {
    return berechneteDreiMonatsRegeln;
  }

  public void setBerechneteDreiMonatsRegeln(
      List<DreiMonatsRegel> berechneteDreiMonatsRegeln) {
    this.berechneteDreiMonatsRegeln = berechneteDreiMonatsRegeln;
  }
}
