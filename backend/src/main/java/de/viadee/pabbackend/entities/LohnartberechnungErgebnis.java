package de.viadee.pabbackend.entities;

import java.util.List;

public class LohnartberechnungErgebnis {

  private List<LohnartberechnungLog> lohnartberechnungLog;

  private List<ArbeitsnachweisLohnartZuordnung> arbeitsnachweisLohnartZuordnung;

  public List<LohnartberechnungLog> getLohnartberechnungLog() {
    return lohnartberechnungLog;
  }

  public void setLohnartberechnungLog(final List<LohnartberechnungLog> lohnartberechnungLog) {
    this.lohnartberechnungLog = lohnartberechnungLog;
  }

  public List<ArbeitsnachweisLohnartZuordnung> getArbeitsnachweisLohnartZuordnung() {
    return arbeitsnachweisLohnartZuordnung;
  }

  public void setArbeitsnachweisLohnartZuordnung(
      final List<ArbeitsnachweisLohnartZuordnung> arbeitsnachweisLohnartZuordnung) {
    this.arbeitsnachweisLohnartZuordnung = arbeitsnachweisLohnartZuordnung;
  }

}
