package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class MonatsabschlussAktion {


  @Id
  @Column("ID")
  private Long id;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("titel")
  private String titel;

  @Column("istErledigt")
  private boolean istErledigt;

  @Column("durchgefuehrtAm")
  private LocalDate durchgefuehrtAm;

  @Column("mitarbeiterID")
  private Long mitarbeiterID;

  @Column("rang")
  private int rang;

  @Column("LodasExport")
  private byte[] lodasExport;

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

  public String getTitel() {
    return titel;
  }

  public void setTitel(String titel) {
    this.titel = titel;
  }

  public boolean isIstErledigt() {
    return istErledigt;
  }

  public void setIstErledigt(boolean istErledigt) {
    this.istErledigt = istErledigt;
  }

  public LocalDate getDurchgefuehrtAm() {
    return durchgefuehrtAm;
  }

  public void setDurchgefuehrtAm(LocalDate durchgefuehrtAm) {
    this.durchgefuehrtAm = durchgefuehrtAm;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterID() {
    return mitarbeiterID;
  }

  public void setMitarbeiterID(Long mitarbeiterID) {
    this.mitarbeiterID = mitarbeiterID;
  }

  public int getRang() {
    return rang;
  }

  public void setRang(int rang) {
    this.rang = rang;
  }

  public byte[] getLodasExport() {
    return lodasExport;
  }

  public void setLodasExport(byte[] lodasExport) {
    this.lodasExport = lodasExport;
  }
}
