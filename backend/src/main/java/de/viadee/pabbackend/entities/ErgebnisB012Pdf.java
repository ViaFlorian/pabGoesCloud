package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB012Pdf {

  private String oeBezeichnung;
  private String oeBeschreibung;
  private String oeVerantwortlicherNachname;
  private String oeVerantwortlicherVorname;
  private boolean bereich;
  private BigDecimal anzahlStundenKundenprojekte;
  private BigDecimal anzahlStundenInterneProjekte;
  private BigDecimal leistung;
  private BigDecimal durchschnittlicherStundensatz;
  private BigDecimal kosten;
  private BigDecimal honorarKosten;
  private BigDecimal reiseKosten;
  private Abrechnungsmonat abrechnungsmonat;
  private boolean internesProjekt;
  private boolean jahreswerte;
  private boolean planwerte;
  private boolean planwerteAngefordert;
  private BigDecimal anzahlStundenKundenprojekteAnteil;
  private BigDecimal anzahlStundenInterneProjekteAnteil;
  private BigDecimal planLeistungAnteil;
  private BigDecimal durchschnittlicherStundensatzAnteil;
  private BigDecimal planKostenAnteil;
  private BigDecimal honorarKostenAnteil;
  private BigDecimal reiseKostenAnteil;
  private BigDecimal anzahlStundenKundenprojekteAngestellte;
  private BigDecimal anzahlStundenKundenprojekteExterne;
  private BigDecimal anzahlStundenKundenprojekteStudenten;
  private BigDecimal anzahlStundenInterneProjekteAngestellte;
  private BigDecimal anzahlStundenInterneProjekteExterne;
  private BigDecimal anzahlStundenInterneProjekteStudenten;
  private BigDecimal leistungAngestellte;
  private BigDecimal leistungExterne;
  private BigDecimal leistungStudenten;
  private BigDecimal leistungOhneMitarbeiter;
  private BigDecimal kostenAngestellte;
  private BigDecimal kostenExterne;
  private BigDecimal kostenStudenten;
  private BigDecimal kostenOhneMitarbeiter;

  public BigDecimal getAnzahlStundenKundenprojekteAngestellte() {
    return anzahlStundenKundenprojekteAngestellte;
  }

  public void setAnzahlStundenKundenprojekteAngestellte(
      BigDecimal anzahlStundenKundenprojekteAngestellte) {
    this.anzahlStundenKundenprojekteAngestellte = anzahlStundenKundenprojekteAngestellte;
  }

  public BigDecimal getAnzahlStundenKundenprojekteExterne() {
    return anzahlStundenKundenprojekteExterne;
  }

  public void setAnzahlStundenKundenprojekteExterne(BigDecimal anzahlStundenKundenprojekteExterne) {
    this.anzahlStundenKundenprojekteExterne = anzahlStundenKundenprojekteExterne;
  }

  public BigDecimal getLeistungAngestellte() {
    return leistungAngestellte;
  }

  public void setLeistungAngestellte(BigDecimal leistungAngestellte) {
    this.leistungAngestellte = leistungAngestellte;
  }

  public BigDecimal getLeistungExterne() {
    return leistungExterne;
  }

  public void setLeistungExterne(BigDecimal leistungExterne) {
    this.leistungExterne = leistungExterne;
  }

  public BigDecimal getKostenAngestellte() {
    return kostenAngestellte;
  }

  public void setKostenAngestellte(BigDecimal kostenAngestellte) {
    this.kostenAngestellte = kostenAngestellte;
  }

  public BigDecimal getKostenExterne() {
    return kostenExterne;
  }

  public void setKostenExterne(BigDecimal kostenExterne) {
    this.kostenExterne = kostenExterne;
  }

  public BigDecimal getAnzahlStundenKundenprojekteAnteil() {
    return anzahlStundenKundenprojekteAnteil;
  }

  public void setAnzahlStundenKundenprojekteAnteil(BigDecimal anzahlStundenKundenprojekteAnteil) {
    this.anzahlStundenKundenprojekteAnteil = anzahlStundenKundenprojekteAnteil;
  }

  public BigDecimal getAnzahlStundenInterneProjekteAnteil() {
    return anzahlStundenInterneProjekteAnteil;
  }

  public void setAnzahlStundenInterneProjekteAnteil(BigDecimal anzahlStundenInterneProjekteAnteil) {
    this.anzahlStundenInterneProjekteAnteil = anzahlStundenInterneProjekteAnteil;
  }

  public BigDecimal getPlanLeistungAnteil() {
    return planLeistungAnteil;
  }

  public void setPlanLeistungAnteil(BigDecimal planLeistungAnteil) {
    this.planLeistungAnteil = planLeistungAnteil;
  }

  public BigDecimal getDurchschnittlicherStundensatzAnteil() {
    return durchschnittlicherStundensatzAnteil;
  }

  public void setDurchschnittlicherStundensatzAnteil(
      BigDecimal durchschnittlicherStundensatzAnteil) {
    this.durchschnittlicherStundensatzAnteil = durchschnittlicherStundensatzAnteil;
  }

  public BigDecimal getPlanKostenAnteil() {
    return planKostenAnteil;
  }

  public void setPlanKostenAnteil(BigDecimal planKostenAnteil) {
    this.planKostenAnteil = planKostenAnteil;
  }

  public BigDecimal getHonorarKostenAnteil() {
    return honorarKostenAnteil;
  }

  public void setHonorarKostenAnteil(BigDecimal honorarKostenAnteil) {
    this.honorarKostenAnteil = honorarKostenAnteil;
  }

  public BigDecimal getReiseKostenAnteil() {
    return reiseKostenAnteil;
  }

  public void setReiseKostenAnteil(BigDecimal reiseKostenAnteil) {
    this.reiseKostenAnteil = reiseKostenAnteil;
  }

  public Boolean getPlanwerteAngefordert() {
    return planwerteAngefordert;
  }

  public void setPlanwerteAngefordert(Boolean planwerteAngefordert) {
    this.planwerteAngefordert = planwerteAngefordert;
  }

  public Integer getMonat() {
    return abrechnungsmonat.getMonat();
  }

  public Boolean getPlanwerte() {
    return planwerte;
  }

  public void setPlanwerte(Boolean planwerte) {
    this.planwerte = planwerte;
  }

  public Boolean getBereich() {
    return bereich;
  }

  public void setBereich(Boolean bereich) {
    this.bereich = bereich;
  }

  public String getOeBeschreibung() {
    return oeBeschreibung;
  }

  public void setOeBeschreibung(String oeBeschreibung) {
    this.oeBeschreibung = oeBeschreibung;
  }

  public String getOeBezeichnung() {
    return oeBezeichnung;
  }

  public void setOeBezeichnung(String oeBezeichnung) {
    this.oeBezeichnung = oeBezeichnung;
  }

  public String getOeVerantwortlicher() {
    String nachname = oeVerantwortlicherNachname.trim();
    String vorname = oeVerantwortlicherVorname.trim();
    if (nachname.length() > 0 && vorname.length() > 0) {
      return oeVerantwortlicherNachname + ", " + oeVerantwortlicherVorname;
    } else {
      return oeVerantwortlicherNachname + oeVerantwortlicherVorname;
    }
  }

  public void setOeVerantwortlicherNachname(String oeVerantwortlicherNachname) {
    this.oeVerantwortlicherNachname = oeVerantwortlicherNachname;
  }

  public void setOeVerantwortlicherVorname(String oeVerantwortlicherVorname) {
    this.oeVerantwortlicherVorname = oeVerantwortlicherVorname;
  }

  public BigDecimal getAnzahlStundenKundenprojekte() {
    return anzahlStundenKundenprojekte;
  }

  public void setAnzahlStundenKundenprojekte(BigDecimal anzahlStundenKundenprojekte) {
    this.anzahlStundenKundenprojekte = anzahlStundenKundenprojekte;
  }

  public BigDecimal getAnzahlStundenInterneProjekte() {
    return anzahlStundenInterneProjekte;
  }

  public void setAnzahlStundenInterneProjekte(BigDecimal anzahlStundenInterneProjekte) {
    this.anzahlStundenInterneProjekte = anzahlStundenInterneProjekte;
  }

  public Boolean getJahreswerte() {
    return jahreswerte;
  }

  public void setJahreswerte(Boolean jahreswerte) {
    this.jahreswerte = jahreswerte;
  }

  public Boolean getMonatAbgeschlossen() {
    return abrechnungsmonat.isAbgeschlossen();
  }

  public Boolean getInternesProjekt() {
    return internesProjekt;
  }

  public void setInternesProjekt(Boolean internesProjekt) {
    this.internesProjekt = internesProjekt;
  }

  public String getAbrechnungsmonatForPdf() {

    return abrechnungsmonat.toString();
  }

  public BigDecimal getLeistung() {
    return leistung;
  }

  public void setLeistung(BigDecimal leistung) {
    this.leistung = leistung;
  }

  public BigDecimal getDurchschnittlicherStundensatz() {
    return durchschnittlicherStundensatz;
  }

  public void setDurchschnittlicherStundensatz(BigDecimal durchschnittlicherStundensatz) {
    this.durchschnittlicherStundensatz = durchschnittlicherStundensatz;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getHonorarKosten() {
    return honorarKosten;
  }

  public void setHonorarKosten(BigDecimal honorarKosten) {
    this.honorarKosten = honorarKosten;
  }

  public BigDecimal getReiseKosten() {
    return reiseKosten;
  }

  public void setReiseKosten(BigDecimal reiseKosten) {
    this.reiseKosten = reiseKosten;
  }

  public Abrechnungsmonat getAbrechnungsmonat() {
    return abrechnungsmonat;
  }

  public void setAbrechnungsmonat(Abrechnungsmonat abrechnungsmonat) {
    this.abrechnungsmonat = abrechnungsmonat;
  }

  public BigDecimal getLeistungOhneMitarbeiter() {
    return leistungOhneMitarbeiter;
  }

  public void setLeistungOhneMitarbeiter(BigDecimal leistungOhneMitarbeiter) {
    this.leistungOhneMitarbeiter = leistungOhneMitarbeiter;
  }

  public BigDecimal getKostenOhneMitarbeiter() {
    return kostenOhneMitarbeiter;
  }

  public void setKostenOhneMitarbeiter(BigDecimal kostenOhneMitarbeiter) {
    this.kostenOhneMitarbeiter = kostenOhneMitarbeiter;
  }

  public BigDecimal getAnzahlStundenKundenprojekteStudenten() {
    return anzahlStundenKundenprojekteStudenten;
  }

  public void setAnzahlStundenKundenprojekteStudenten(
      BigDecimal anzahlStundenKundenprojekteStudenten) {
    this.anzahlStundenKundenprojekteStudenten = anzahlStundenKundenprojekteStudenten;
  }

  public BigDecimal getAnzahlStundenInterneProjekteAngestellte() {
    return anzahlStundenInterneProjekteAngestellte;
  }

  public void setAnzahlStundenInterneProjekteAngestellte(
      BigDecimal anzahlStundenInterneProjekteAngestellte) {
    this.anzahlStundenInterneProjekteAngestellte = anzahlStundenInterneProjekteAngestellte;
  }

  public BigDecimal getAnzahlStundenInterneProjekteExterne() {
    return anzahlStundenInterneProjekteExterne;
  }

  public void setAnzahlStundenInterneProjekteExterne(
      BigDecimal anzahlStundenInterneProjekteExterne) {
    this.anzahlStundenInterneProjekteExterne = anzahlStundenInterneProjekteExterne;
  }

  public BigDecimal getAnzahlStundenInterneProjekteStudenten() {
    return anzahlStundenInterneProjekteStudenten;
  }

  public void setAnzahlStundenInterneProjekteStudenten(
      BigDecimal anzahlStundenInterneProjekteStudenten) {
    this.anzahlStundenInterneProjekteStudenten = anzahlStundenInterneProjekteStudenten;
  }

  public BigDecimal getLeistungStudenten() {
    return leistungStudenten;
  }

  public void setLeistungStudenten(BigDecimal leistungStudenten) {
    this.leistungStudenten = leistungStudenten;
  }

  public BigDecimal getKostenStudenten() {
    return kostenStudenten;
  }

  public void setKostenStudenten(BigDecimal kostenStudenten) {
    this.kostenStudenten = kostenStudenten;
  }
}
