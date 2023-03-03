package de.viadee.pabbackend.entities;

import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB002Uebersicht {

  private Integer jahr;
  private Integer monat;
  @Column("mitarbeiterpersonalnummer")
  private Integer mitarbeiterPersonalnummer;
  @Column("mitarbeiteristaktiv")
  private boolean mitarbeiterIstAktiv;
  @Column("mitarbeitervorname")
  private String mitarbeiterVorname;
  @Column("mitarbeiternachname")
  private String mitarbeiterNachname;
  @Column("mitarbeiterkurzname")
  private String mitarbeiterKurzname;
  @Column("sachbearbeitervorname")
  private String sachbearbeiterVorname;
  @Column("sachbearbeiternachname")
  private String sachbearbeiterNachname;
  
  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(final Integer monat) {
    this.monat = monat;
  }

  public Integer getMitarbeiterPersonalnummer() {
    return mitarbeiterPersonalnummer;
  }

  public void setMitarbeiterPersonalnummer(final Integer mitarbeiterPersonalnummer) {
    this.mitarbeiterPersonalnummer = mitarbeiterPersonalnummer;
  }

  public boolean isMitarbeiterIstAktiv() {
    return mitarbeiterIstAktiv;
  }

  public void setMitarbeiterIstAktiv(boolean mitarbeiterIstAktiv) {
    this.mitarbeiterIstAktiv = mitarbeiterIstAktiv;
  }

  public String getMitarbeiterVorname() {
    return mitarbeiterVorname;
  }

  public void setMitarbeiterVorname(final String mitarbeiterVorname) {
    this.mitarbeiterVorname = mitarbeiterVorname;
  }

  public String getMitarbeiterNachname() {
    return mitarbeiterNachname;
  }

  public void setMitarbeiterNachname(final String mitarbeiterNachname) {
    this.mitarbeiterNachname = mitarbeiterNachname;
  }

  public String getMitarbeiterKurzname() {
    return mitarbeiterKurzname;
  }

  public void setMitarbeiterKurzname(final String mitarbeiterKurzname) {
    this.mitarbeiterKurzname = mitarbeiterKurzname;
  }

  public String getSachbearbeiterVorname() {
    return sachbearbeiterVorname;
  }

  public void setSachbearbeiterVorname(final String sachbearbeiterVorname) {
    this.sachbearbeiterVorname = sachbearbeiterVorname;
  }

  public String getSachbearbeiterNachname() {
    return sachbearbeiterNachname;
  }

  public void setSachbearbeiterNachname(final String sachbearbeiterNachname) {
    this.sachbearbeiterNachname = sachbearbeiterNachname;
  }
}
