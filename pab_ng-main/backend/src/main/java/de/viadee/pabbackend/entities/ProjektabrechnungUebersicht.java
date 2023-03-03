package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ProjektabrechnungUebersicht implements Comparable<ProjektabrechnungUebersicht> {

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("StatusID")
  private Integer statusId;

  @Column("ProjektID")
  private Long projektId;

  @Column("KundeID")
  private String kundeId;

  @Column("OrganisationseinheitID")
  private String organisationseinheitId;

  @Column("SachbearbeiterID")
  private Long sachbearbeiterId;

  @Column("AnzahlMitarbeiter")
  private Integer anzahlMitarbeiter;

  @Column("AnzahlKorrekturbuchungen")
  private Integer anzahlKorrekturbuchungen;

  @Column("Leistung")
  private BigDecimal leistung;

  @Column("kosten")
  private BigDecimal kosten;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektabrechnungId() {
    return projektabrechnungId;
  }

  public void setProjektabrechnungId(Long projektabrechnungId) {
    this.projektabrechnungId = projektabrechnungId;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(Integer monat) {
    this.monat = monat;
  }

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(Integer statusId) {
    this.statusId = statusId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(Long projektId) {
    this.projektId = projektId;
  }

  public String getKundeId() {
    return kundeId;
  }

  public void setKundeId(String kundeId) {
    this.kundeId = kundeId;
  }

  public String getOrganisationseinheitId() {
    return organisationseinheitId;
  }

  public void setOrganisationseinheitId(String organisationseinheitId) {
    this.organisationseinheitId = organisationseinheitId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getSachbearbeiterId() {
    return sachbearbeiterId;
  }

  public void setSachbearbeiterId(Long sachbearbeiterId) {
    this.sachbearbeiterId = sachbearbeiterId;
  }

  public Integer getAnzahlMitarbeiter() {
    return anzahlMitarbeiter;
  }

  public void setAnzahlMitarbeiter(Integer anzahlMitarbeiter) {
    this.anzahlMitarbeiter = anzahlMitarbeiter;
  }

  public Integer getAnzahlKorrekturbuchungen() {
    return anzahlKorrekturbuchungen;
  }

  public void setAnzahlKorrekturbuchungen(Integer anzahlKorrekturbuchungen) {
    this.anzahlKorrekturbuchungen = anzahlKorrekturbuchungen;
  }

  public BigDecimal getLeistung() {
    return leistung;
  }

  public void setLeistung(BigDecimal leistung) {
    this.leistung = leistung;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  @Override
  public int compareTo(ProjektabrechnungUebersicht zuVergleichendeProjektabrechnung) {
    // Zunächst wird das Jahr der jeweiligen Projektabrechnung verglichen
    int result = 0;

    if (zuVergleichendeProjektabrechnung != null
        && zuVergleichendeProjektabrechnung.getJahr() != null
        && zuVergleichendeProjektabrechnung.getMonat() != null) {
      result = jahr.compareTo(zuVergleichendeProjektabrechnung.jahr);
      // Ist das Jahr gleich, muss der Monat verglichen werden
      if (result == 0) {
        result = monat.compareTo(zuVergleichendeProjektabrechnung.monat);
      }
    } else {
      result = 1;
    }

    return result;
  }
}
