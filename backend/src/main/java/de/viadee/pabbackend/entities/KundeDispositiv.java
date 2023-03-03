package de.viadee.pabbackend.entities;

public class KundeDispositiv {

  private long id;

  private String bezeichnung;

  private Integer debitorennummer;

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

  public Integer getDebitorennummer() {
    return debitorennummer;
  }

  public void setDebitorennummer(final Integer debitorennummer) {
    this.debitorennummer = debitorennummer;
  }
}
