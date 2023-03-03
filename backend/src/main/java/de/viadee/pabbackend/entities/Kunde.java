package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Kunde")
public class Kunde {

  @Id
  @Column("ID")
  private long id;

  @Column("Kurzbezeichnung")
  private String kurzbezeichnung;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("Debitorennummer")
  private String debitorennummer;

  @Column("ScribeID")
  private String scribeId;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getKurzbezeichnung() {
    return kurzbezeichnung;
  }

  public void setKurzbezeichnung(final String kurzbezeichnung) {
    this.kurzbezeichnung = kurzbezeichnung;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(final String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public String getScribeId() {
    return scribeId;
  }

  public void setScribeId(final String scribeId) {
    this.scribeId = scribeId;
  }

  public String getDebitorennummer() {
    return debitorennummer;
  }

  public void setDebitorennummer(final String debitorennummer) {
    this.debitorennummer = debitorennummer;
  }
}
