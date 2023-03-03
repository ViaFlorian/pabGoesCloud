package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("C_Lohnart")
public class LohnartKonstante implements Comparable<LohnartKonstante> {

  @Id
  @Column("ID")
  private Long id;

  @Column("Konto")
  private String konto;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column
  private Integer bearbeitungsschluessel;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKonto() {
    return konto;
  }

  public void setKonto(String konto) {
    this.konto = konto;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public Integer getBearbeitungsschluessel() {
    return bearbeitungsschluessel;
  }

  public void setBearbeitungsschluessel(Integer bearbeitungsschluessel) {
    this.bearbeitungsschluessel = bearbeitungsschluessel;
  }

  @Override
  public int compareTo(LohnartKonstante that) {
    if (this.konto.compareTo(that.konto) < 0) {
      return -1;
    } else if (this.konto.compareTo(that.konto) > 0) {
      return 1;
    }
    return 0;
  }
}
