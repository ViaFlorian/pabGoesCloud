package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ArbeitsnachweisAbrechnung {

  private BigDecimal summeIstStunden;

  private BigDecimal auszahlung;

  private BigDecimal sollstunden;

  private BigDecimal vortrag;

  private BigDecimal berechneteSollstunden;

  private BigDecimal aktuellerStellenfaktor;

  private Integer ueberAcht;

  private Integer anAb;

  private Integer spesenGanztaegig;

  private Integer fruehstueck;

  private Integer mittagessen;

  private Integer abendessen;

  private Integer zwischenSechsUndZehn;

  private Integer ueberZehn;

  private Integer zuschlaegeGanztaegig;

  private BigDecimal sonderarbeitszeit;

  private BigDecimal davonWerktag;

  private BigDecimal davonSamstag;

  private BigDecimal davonSonntagFeiertag;

  private BigDecimal rufbereitschaft;

  private BigDecimal summeBelege;

  private String smartphone;

  private String firmenwagen;

  private BigDecimal kilometerpauschaleFirmenwagen;

  private BigDecimal zuschlagSmartphone;

  private BigDecimal verbindungsentgelt;

  private BigDecimal jobticket;

  private String warnung;

  public BigDecimal getSummeIstStunden() {
    return summeIstStunden;
  }

  public void setSummeIstStunden(BigDecimal summeIstStunden) {
    this.summeIstStunden = summeIstStunden;
  }

  public BigDecimal getAuszahlung() {
    return auszahlung;
  }

  public void setAuszahlung(BigDecimal auszahlung) {
    this.auszahlung = auszahlung;
  }

  public BigDecimal getSollstunden() {
    return sollstunden;
  }

  public void setSollstunden(BigDecimal sollstunden) {
    this.sollstunden = sollstunden;
  }

  public BigDecimal getVortrag() {
    return vortrag;
  }

  public void setVortrag(BigDecimal vortrag) {
    this.vortrag = vortrag;
  }

  public BigDecimal getBerechneteSollstunden() {
    return berechneteSollstunden;
  }

  public void setBerechneteSollstunden(BigDecimal berechneteSollstunden) {
    this.berechneteSollstunden = berechneteSollstunden;
  }

  public BigDecimal getAktuellerStellenfaktor() {
    return aktuellerStellenfaktor;
  }

  public void setAktuellerStellenfaktor(BigDecimal aktuellerStellenfaktor) {
    this.aktuellerStellenfaktor = aktuellerStellenfaktor;
  }

  public Integer getUeberAcht() {
    return ueberAcht;
  }

  public void setUeberAcht(Integer ueberAcht) {
    this.ueberAcht = ueberAcht;
  }

  public Integer getAnAb() {
    return anAb;
  }

  public void setAnAb(Integer anAb) {
    this.anAb = anAb;
  }

  public Integer getSpesenGanztaegig() {
    return spesenGanztaegig;
  }

  public void setSpesenGanztaegig(Integer spesenGanztaegig) {
    this.spesenGanztaegig = spesenGanztaegig;
  }

  public Integer getFruehstueck() {
    return fruehstueck;
  }

  public void setFruehstueck(Integer fruehstueck) {
    this.fruehstueck = fruehstueck;
  }

  public Integer getMittagessen() {
    return mittagessen;
  }

  public void setMittagessen(Integer mittagessen) {
    this.mittagessen = mittagessen;
  }

  public Integer getAbendessen() {
    return abendessen;
  }

  public void setAbendessen(Integer abendessen) {
    this.abendessen = abendessen;
  }

  public Integer getZwischenSechsUndZehn() {
    return zwischenSechsUndZehn;
  }

  public void setZwischenSechsUndZehn(Integer zwischenSechsUndZehn) {
    this.zwischenSechsUndZehn = zwischenSechsUndZehn;
  }

  public Integer getUeberZehn() {
    return ueberZehn;
  }

  public void setUeberZehn(Integer ueberZehn) {
    this.ueberZehn = ueberZehn;
  }

  public Integer getZuschlaegeGanztaegig() {
    return zuschlaegeGanztaegig;
  }

  public void setZuschlaegeGanztaegig(Integer zuschlaegeGanztaegig) {
    this.zuschlaegeGanztaegig = zuschlaegeGanztaegig;
  }

  public BigDecimal getSonderarbeitszeit() {
    return sonderarbeitszeit;
  }

  public void setSonderarbeitszeit(BigDecimal sonderarbeitszeit) {
    this.sonderarbeitszeit = sonderarbeitszeit;
  }

  public BigDecimal getDavonWerktag() {
    return davonWerktag;
  }

  public void setDavonWerktag(BigDecimal davonWerktag) {
    this.davonWerktag = davonWerktag;
  }

  public BigDecimal getDavonSamstag() {
    return davonSamstag;
  }

  public void setDavonSamstag(BigDecimal davonSamstag) {
    this.davonSamstag = davonSamstag;
  }

  public BigDecimal getDavonSonntagFeiertag() {
    return davonSonntagFeiertag;
  }

  public void setDavonSonntagFeiertag(BigDecimal davonSonntagFeiertag) {
    this.davonSonntagFeiertag = davonSonntagFeiertag;
  }

  public BigDecimal getRufbereitschaft() {
    return rufbereitschaft;
  }

  public void setRufbereitschaft(BigDecimal rufbereitschaft) {
    this.rufbereitschaft = rufbereitschaft;
  }

  public BigDecimal getSummeBelege() {
    return summeBelege;
  }

  public void setSummeBelege(BigDecimal summeBelege) {
    this.summeBelege = summeBelege;
  }

  public String getSmartphone() {
    return smartphone;
  }

  public void setSmartphone(String smartphone) {
    this.smartphone = smartphone;
  }

  public String getFirmenwagen() {
    return firmenwagen;
  }

  public void setFirmenwagen(String firmenwagen) {
    this.firmenwagen = firmenwagen;
  }

  public BigDecimal getKilometerpauschaleFirmenwagen() {
    return kilometerpauschaleFirmenwagen;
  }

  public void setKilometerpauschaleFirmenwagen(BigDecimal kilometerpauschaleFirmenwagen) {
    this.kilometerpauschaleFirmenwagen = kilometerpauschaleFirmenwagen;
  }

  public BigDecimal getZuschlagSmartphone() {
    return zuschlagSmartphone;
  }

  public void setZuschlagSmartphone(BigDecimal zuschlagSmartphone) {
    this.zuschlagSmartphone = zuschlagSmartphone;
  }

  public BigDecimal getVerbindungsentgelt() {
    return verbindungsentgelt;
  }

  public void setVerbindungsentgelt(BigDecimal verbindungsentgelt) {
    this.verbindungsentgelt = verbindungsentgelt;
  }

  public BigDecimal getJobticket() {
    return jobticket;
  }

  public void setJobticket(BigDecimal jobticket) {
    this.jobticket = jobticket;
  }

  public String getWarnung() {
    return warnung;
  }

  public void setWarnung(String warnung) {
    this.warnung = warnung;
  }

  public BigDecimal getUebertragVorAuszahlung() {
    BigDecimal uebertragVorAuszahlung = BigDecimal.ZERO;
    if (vortrag != null) {
      uebertragVorAuszahlung = vortrag;
    }
    if (summeIstStunden != null) {
      uebertragVorAuszahlung = uebertragVorAuszahlung.add(summeIstStunden);
    }
    if (sollstunden != null) {
      uebertragVorAuszahlung = uebertragVorAuszahlung.subtract(sollstunden);
    }
    return uebertragVorAuszahlung.setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal getUebertragNachAuszahlung() {
    BigDecimal uebertragNachAuszahlung = getUebertragVorAuszahlung();
    if (sonderarbeitszeit != null) {
      uebertragNachAuszahlung = uebertragNachAuszahlung.subtract(sonderarbeitszeit);
    }
    if (auszahlung != null) {
      uebertragNachAuszahlung = uebertragNachAuszahlung.subtract(auszahlung);
    }

    return uebertragNachAuszahlung.setScale(2, RoundingMode.HALF_UP);
  }
}
