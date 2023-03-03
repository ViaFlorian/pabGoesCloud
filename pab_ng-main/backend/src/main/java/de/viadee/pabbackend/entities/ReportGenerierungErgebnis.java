package de.viadee.pabbackend.entities;

public class ReportGenerierungErgebnis {

  private boolean fehlerAufgetreten;

  private String meldung;

  public ReportGenerierungErgebnis(boolean fehlerAufgetreten, String meldung) {
    this.fehlerAufgetreten = fehlerAufgetreten;
    this.meldung = meldung;
  }

  public boolean isFehlerAufgetreten() {
    return fehlerAufgetreten;
  }

  public String getMeldung() {
    return meldung;
  }
}
