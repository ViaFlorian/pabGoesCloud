package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Mitarbeiter")
public class Mitarbeiter implements Comparable<Mitarbeiter> {

  @Id
  @Column("ID")
  private Long id;

  @Column("PersonalNr")
  private Integer personalNr;

  @Column("Anrede")
  private String anrede;

  @Column("Titel")
  private String titel;

  @Column("Nachname")
  private String nachname;

  @Column("Vorname")
  private String vorname;

  @Column("Kurzname")
  private String kurzname;

  @Column("Geschaeftsstelle")
  private String geschaeftsstelle;

  @Column("IstIntern")
  private boolean istIntern;

  @Column("IstAktiv")
  private boolean istAktiv;

  @Column("SachbearbeiterID")
  private Long sachbearbeiterId;

  @Column("MitarbeiterTypID")
  private Long mitarbeiterTypId;

  @Column("eMail")
  private String email;

  @Column("Kostensatz")
  private BigDecimal kostensatz;

  @Column("Firmenwagen")
  private Boolean firmenwagen;

  public Boolean getFirmenwagen() {
    return firmenwagen;
  }

  public void setFirmenwagen(final Boolean firmenwagen) {
    this.firmenwagen = firmenwagen;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Integer getPersonalNr() {
    return personalNr;
  }

  public void setPersonalNr(final Integer personalNr) {
    this.personalNr = personalNr;
  }

  public String getAnrede() {
    return anrede;
  }

  public void setAnrede(String anrede) {
    this.anrede = anrede;
  }

  public String getTitel() {
    return titel;
  }

  public void setTitel(String titel) {
    this.titel = titel;
  }

  public String getNachname() {
    return nachname;
  }

  public void setNachname(final String nachname) {
    this.nachname = nachname;
  }

  public String getFullName() {
    return nachname + ", " + vorname;
  }

  public String getMitarbeiterPersonalnummerInklGanzerName() {
    return (personalNr == null) ? getFullName() : personalNr + "; " + getFullName();
  }

  public String getVorname() {
    return vorname;
  }

  public void setVorname(final String vorname) {
    this.vorname = vorname;
  }

  public String getKurzname() {
    return kurzname;
  }

  public void setKurzname(final String kurzname) {
    this.kurzname = kurzname;
  }

  public String getGeschaeftsstelle() {
    return geschaeftsstelle;
  }

  public void setGeschaeftsstelle(final String geschaeftsstelle) {
    this.geschaeftsstelle = geschaeftsstelle;
  }

  public boolean isIntern() {
    return istIntern;
  }

  public void setIstIntern(final boolean istIntern) {
    this.istIntern = istIntern;
  }

  public boolean getIstAktiv() {
    return istAktiv;
  }

  public void setIstAktiv(final boolean istAktiv) {
    this.istAktiv = istAktiv;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getSachbearbeiterId() {
    return sachbearbeiterId;
  }

  public void setSachbearbeiterId(final Long sachbearbeiterId) {
    this.sachbearbeiterId = sachbearbeiterId;
  }

  public Long getMitarbeiterTypId() {
    return mitarbeiterTypId;
  }

  public void setMitarbeiterTypId(final Long mitarbeiterTypId) {
    this.mitarbeiterTypId = mitarbeiterTypId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public BigDecimal getKostensatz() {
    return kostensatz;
  }

  public void setKostensatz(final BigDecimal kostensatz) {
    this.kostensatz = kostensatz;
  }

  @Override
  public int compareTo(Mitarbeiter that) {
    if (this.personalNr == null) {
      return 1;
    }
    if (this.personalNr.compareTo(that.personalNr) < 0) {
      return -1;
    } else if (this.personalNr.compareTo(that.personalNr) > 0) {
      return 1;
    }
    return 0;
  }
}
