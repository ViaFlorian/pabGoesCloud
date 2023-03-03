package de.viadee.pabbackend.entities;

import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("SonderzeitenKontoZuweisung")
public class SonderzeitenKontoZuweisung {

  @Id
  @Column("ID")
  private Long id;

  @Column("TagDerWoche")
  private String tagDerWoche;

  @Column("uhrzeitVon")
  private LocalTime uhrzeitVon;

  @Column("uhrzeitBis")
  private LocalTime uhrzeitBis;

  @Column("zuschlagViadee")
  private Integer zuschlagViadee;

  @Column("davonSteuerfrei")
  private Integer davonSteuerfrei;

  @Column("kontoSteuerfrei")
  private String kontoSteuerfrei;

  @Column("davonSteuerpflichtig")
  private Integer davonSteuerpflichtig;

  @Column("kontoSteuerpflichtig")
  private String kontoSteuerpflichtig;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getTagDerWoche() {
    return tagDerWoche;
  }

  public void setTagDerWoche(final String tagDerWoche) {
    this.tagDerWoche = tagDerWoche;
  }

  public LocalTime getUhrzeitVon() {
    return uhrzeitVon;
  }

  public void setUhrzeitVon(final LocalTime uhrzeitVon) {
    this.uhrzeitVon = uhrzeitVon;
  }

  public LocalTime getUhrzeitBis() {
    return uhrzeitBis;
  }

  public void setUhrzeitBis(final LocalTime uhrzeitBis) {
    this.uhrzeitBis = uhrzeitBis;
  }

  public Integer getZuschlagViadee() {
    return zuschlagViadee;
  }

  public void setZuschlagViadee(final Integer zuschlagViadee) {
    this.zuschlagViadee = zuschlagViadee;
  }

  public Integer getDavonSteuerfrei() {
    return davonSteuerfrei;
  }

  public void setDavonSteuerfrei(final Integer davonSteuerfrei) {
    this.davonSteuerfrei = davonSteuerfrei;
  }

  public String getKontoSteuerfrei() {
    return kontoSteuerfrei;
  }

  public void setKontoSteuerfrei(final String kontoSteuerfrei) {
    this.kontoSteuerfrei = kontoSteuerfrei;
  }

  public Integer getDavonSteuerpflichtig() {
    return davonSteuerpflichtig;
  }

  public void setDavonSteuerpflichtig(final Integer davonSteuerpflichtig) {
    this.davonSteuerpflichtig = davonSteuerpflichtig;
  }

  public String getKontoSteuerpflichtig() {
    return kontoSteuerpflichtig;
  }

  public void setKontoSteuerpflichtig(final String kontoSteuerpflichtig) {
    this.kontoSteuerpflichtig = kontoSteuerpflichtig;
  }

}
