package de.viadee.pabbackend.entities;

public class ProjektabrechnungKorrekturbuchungVorgang {

  private ProjektabrechnungKorrekturbuchung korrekturbuchung;
  private ProjektabrechnungKorrekturbuchung gegenbuchung;

  public ProjektabrechnungKorrekturbuchungVorgang(
      ProjektabrechnungKorrekturbuchung korrekturbuchungNeu,
      ProjektabrechnungKorrekturbuchung gegenbuchungNeu) {
    this.korrekturbuchung = korrekturbuchungNeu;
    this.gegenbuchung = gegenbuchungNeu;
  }

  public ProjektabrechnungKorrekturbuchung getKorrekturbuchung() {
    return korrekturbuchung;
  }

  public void setKorrekturbuchung(ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    this.korrekturbuchung = korrekturbuchung;
  }

  public ProjektabrechnungKorrekturbuchung getGegenbuchung() {
    return gegenbuchung;
  }

  public void setGegenbuchung(
      ProjektabrechnungKorrekturbuchung gegenbuchung) {
    this.gegenbuchung = gegenbuchung;
  }
}
