package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ProjektabrechnungSonstige")
public class ProjektabrechnungSonstige {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("Bemerkung")
  private String bemerkung;

  @Column("AuslagenViadee")
  private BigDecimal viadeeAuslagen;

  @Column("PauschaleKosten")
  private BigDecimal pauschaleKosten;

  @Column("PauschaleLeistungen")
  private BigDecimal pauschaleLeistung;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDateTime zuletztGeaendertAm;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektabrechnungId() {
    return projektabrechnungId;
  }

  public void setProjektabrechnungId(final Long projektabrechnungId) {
    this.projektabrechnungId = projektabrechnungId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public BigDecimal getViadeeAuslagen() {
    return viadeeAuslagen;
  }

  public void setViadeeAuslagen(final BigDecimal viadeeAuslagen) {
    this.viadeeAuslagen = viadeeAuslagen;
  }

  public BigDecimal getPauschaleKosten() {
    return pauschaleKosten;
  }

  public void setPauschaleKosten(final BigDecimal pauschaleKosten) {
    this.pauschaleKosten = pauschaleKosten;
  }

  public BigDecimal getPauschaleLeistung() {
    return pauschaleLeistung;
  }

  public void setPauschaleLeistung(final BigDecimal pauschaleLeistung) {
    this.pauschaleLeistung = pauschaleLeistung;
  }

  public BigDecimal getSonstigeKosten() {
    BigDecimal sonstigeKosten = BigDecimal.ZERO;
    sonstigeKosten = sonstigeKosten.add(viadeeAuslagen == null ? BigDecimal.ZERO : viadeeAuslagen);
    sonstigeKosten = sonstigeKosten.add(sonstigeKosten == null ? BigDecimal.ZERO : pauschaleKosten);
    return sonstigeKosten;
  }

  public BigDecimal getSonstigeLeistung() {
    return pauschaleLeistung == null ? BigDecimal.ZERO : pauschaleLeistung;
  }

  public boolean keineWerteVorhanden() {
    return (pauschaleKosten.compareTo(BigDecimal.ZERO) == 0
        && pauschaleLeistung.compareTo(BigDecimal.ZERO) == 0
        && viadeeAuslagen.compareTo(BigDecimal.ZERO) == 0
        && (bemerkung == null || bemerkung.isEmpty()));
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
}
