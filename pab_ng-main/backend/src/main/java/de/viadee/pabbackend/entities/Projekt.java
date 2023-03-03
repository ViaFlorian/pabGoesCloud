package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Projekt")
public class Projekt implements Comparable<Projekt> {

  @Id
  @Column("ID")
  private Long id;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("Projektnummer")
  private String projektnummer;

  @Column("KundeID")
  private String kundeId;

  @Column("IstAktiv")
  private boolean istAktiv;

  @Column("Statuszusatz")
  private String statuszusatz;

  @Column("Projekttyp")
  private String projekttyp;

  @Column("OrganisationseinheitID")
  private String organisationseinheitId;

  @Column("SachbearbeiterID")
  private Long sachbearbeiterId;

  @Column("VerantwortlicherMitarbeiterID")
  private Long verantwortlicherMitarbeiterId;

  @Column("Geschaeftsstelle")
  private String geschaeftsstelle;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(final String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(final String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getKundeId() {
    return kundeId;
  }

  public void setKundeId(final String kundeId) {
    this.kundeId = kundeId;
  }

  public Boolean getIstAktiv() {
    return istAktiv;
  }

  public void setIstAktiv(final Boolean istAktiv) {
    this.istAktiv = istAktiv;
  }

  public String getStatuszusatz() {
    return statuszusatz;
  }

  public void setStatuszusatz(final String statuszusatz) {
    this.statuszusatz = statuszusatz;
  }

  public String getProjekttyp() {
    return projekttyp;
  }

  public void setProjekttyp(final String projekttyp) {
    this.projekttyp = projekttyp;
  }

  public String getOrganisationseinheitId() {
    return organisationseinheitId;
  }

  public void setOrganisationseinheitId(final String organisationseinheitId) {
    this.organisationseinheitId = organisationseinheitId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getSachbearbeiterId() {
    return sachbearbeiterId;
  }

  public void setSachbearbeiterId(final Long sachbearbeiterId) {
    this.sachbearbeiterId = sachbearbeiterId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getVerantwortlicherMitarbeiterId() {
    return verantwortlicherMitarbeiterId;
  }

  public void setVerantwortlicherMitarbeiterId(final Long verantwortlicherMitarbeiterId) {
    this.verantwortlicherMitarbeiterId = verantwortlicherMitarbeiterId;
  }

  public String getGeschaeftsstelle() {
    return geschaeftsstelle;
  }

  public void setGeschaeftsstelle(final String geschaeftsstelle) {
    this.geschaeftsstelle = geschaeftsstelle;
  }

  @Override
  public int compareTo(Projekt that) {
    if (this.projektnummer.compareTo(that.projektnummer) < 0) {
      return -1;
    } else if (this.projektnummer.compareTo(that.projektnummer) > 0) {
      return 1;
    }
    return 0;
  }
}
