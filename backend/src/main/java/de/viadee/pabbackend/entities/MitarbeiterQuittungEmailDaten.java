package de.viadee.pabbackend.entities;

public class MitarbeiterQuittungEmailDaten {

  private final String[] emailAdressen;
  private final String betreff;
  private final String nachricht;
  private final String dateiname;

  public MitarbeiterQuittungEmailDaten(String dateiname, String[] emailAdressen, String nachricht,
      String betreff) {
    this.dateiname = dateiname;
    this.emailAdressen = emailAdressen;
    this.nachricht = nachricht;
    this.betreff = betreff;
  }

  public String[] getEmailAdressen() {
    return emailAdressen;
  }

  public String getBetreff() {
    return betreff;
  }

  public String getNachricht() {
    return nachricht;
  }

  public String getDateiname() {
    return dateiname;
  }
}
