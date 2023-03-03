package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Vertrag")
public class Vertrag {

  @Id
  @Column("ID")
  private Long id;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("ProjektID")
  private Long projektId;

  @Column("Bezeichnung")
  private String bezeichnung;

  @Column("Stundensatz")
  private BigDecimal stundensatz;

  @Column("GueltigAb")
  private LocalDate gueltigAb;

  @Column("GueltigBis")
  private LocalDate gueltigBis;

  @Column("IstAktiv")
  private boolean istAktiv;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @Column("ScribeID")
  private String scribeId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(Long projektId) {
    this.projektId = projektId;
  }

  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

  public BigDecimal getStundensatz() {
    return stundensatz;
  }

  public void setStundensatz(BigDecimal stundensatz) {
    this.stundensatz = stundensatz;
  }

  public LocalDate getGueltigAb() {
    return gueltigAb;
  }

  public void setGueltigAb(LocalDate gueltigAb) {
    this.gueltigAb = gueltigAb;
  }

  public LocalDate getGueltigBis() {
    return gueltigBis;
  }

  public void setGueltigBis(LocalDate gueltigBis) {
    this.gueltigBis = gueltigBis;
  }

  public boolean isIstAktiv() {
    return istAktiv;
  }

  public void setIstAktiv(boolean istAktiv) {
    this.istAktiv = istAktiv;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public String getScribeId() {
    return scribeId;
  }

  public void setScribeId(String scribeId) {
    this.scribeId = scribeId;
  }
}
