package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Organisationseinheit")
public class Organisationseinheit {

  @Id
  @Column("ID")
  private Long id;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("ScribeID")
  private String scribeId;

  @Column("VerantwortlicherMitarbeiterID")
  private Long verantwortlicherMitarbeiterId;

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

  public String getScribeId() {
    return scribeId;
  }

  public void setScribeId(final String scribeId) {
    this.scribeId = scribeId;
  }

  public Long getVerantwortlicherMitarbeiterId() {
    return verantwortlicherMitarbeiterId;
  }

  public void setVerantwortlicherMitarbeiterId(final Long verantwortlicherMitarbeiterId) {
    this.verantwortlicherMitarbeiterId = verantwortlicherMitarbeiterId;
  }
}
