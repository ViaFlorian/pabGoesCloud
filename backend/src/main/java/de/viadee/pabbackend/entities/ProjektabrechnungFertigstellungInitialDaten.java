package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProjektabrechnungFertigstellungInitialDaten {

  private BigDecimal bisherFertigstellung = BigDecimal.ZERO;
  private BigDecimal monatFertigstellung = BigDecimal.ZERO;

  private BigDecimal bisherProjektbudget = BigDecimal.ZERO;
  private BigDecimal monatProjektbudget = BigDecimal.ZERO;

  private BigDecimal bisherLeistungRechnerisch = BigDecimal.ZERO;
  private BigDecimal monatLeistungRechnerisch = BigDecimal.ZERO;

  private final List<String> meldungen = new ArrayList<>();

  public BigDecimal getBisherFertigstellung() {
    return bisherFertigstellung;
  }

  public void setBisherFertigstellung(BigDecimal bisherFertigstellung) {
    this.bisherFertigstellung = bisherFertigstellung;
  }

  public BigDecimal getMonatFertigstellung() {
    return monatFertigstellung;
  }

  public void setMonatFertigstellung(BigDecimal monatFertigstellung) {
    this.monatFertigstellung = monatFertigstellung;
  }

  public BigDecimal getBisherProjektbudget() {
    return bisherProjektbudget;
  }

  public void setBisherProjektbudget(BigDecimal bisherProjektbudget) {
    this.bisherProjektbudget = bisherProjektbudget;
  }

  public BigDecimal getMonatProjektbudget() {
    return monatProjektbudget;
  }

  public void setMonatProjektbudget(BigDecimal monatProjektbudget) {
    this.monatProjektbudget = monatProjektbudget;
  }

  public BigDecimal getBisherLeistungRechnerisch() {
    return bisherLeistungRechnerisch;
  }

  public void setBisherLeistungRechnerisch(BigDecimal bisherLeistungRechnerisch) {
    this.bisherLeistungRechnerisch = bisherLeistungRechnerisch;
  }

  public BigDecimal getMonatLeistungRechnerisch() {
    return monatLeistungRechnerisch;
  }

  public void setMonatLeistungRechnerisch(BigDecimal monatLeistungRechnerisch) {
    this.monatLeistungRechnerisch = monatLeistungRechnerisch;
  }

  public List<String> getMeldungen() {
    return meldungen;
  }

}
