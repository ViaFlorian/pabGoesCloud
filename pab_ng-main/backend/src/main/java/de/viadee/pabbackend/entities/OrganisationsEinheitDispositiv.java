package de.viadee.pabbackend.entities;

public class OrganisationsEinheitDispositiv {

  private static final long serialVersionUID = -3861293190538595797L;
  private Long id;
  private String bezeichnung;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(final String name) {
    bezeichnung = name;
  }

}
