package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;

public class ProjektabrechnungKostenLeistung {

  private Long mitarbeiterId;

  private BigDecimal projektzeitKosten = BigDecimal.ZERO;
  private BigDecimal projektzeitLeistung = BigDecimal.ZERO;

  private BigDecimal reiseKosten = BigDecimal.ZERO;
  private BigDecimal reiseLeistung = BigDecimal.ZERO;

  private BigDecimal sonderzeitKosten = BigDecimal.ZERO;
  private BigDecimal sonderzeitLeistung = BigDecimal.ZERO;

  private BigDecimal sonstigeKosten = BigDecimal.ZERO;
  private BigDecimal sonstigeLeistung = BigDecimal.ZERO;

  private BigDecimal fakturierfaehigeLeistung = BigDecimal.ZERO;

  private boolean ohneMitarbeiterBezug = false;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public BigDecimal getProjektzeitKosten() {
    return projektzeitKosten;
  }

  public void setProjektzeitKosten(final BigDecimal projektzeitKosten) {
    this.projektzeitKosten = projektzeitKosten;
  }

  public BigDecimal getProjektzeitLeistung() {
    return projektzeitLeistung;
  }

  public void setProjektzeitLeistung(final BigDecimal projektzeitLeistung) {
    this.projektzeitLeistung = projektzeitLeistung;
  }

  public BigDecimal getFakturierfaehigeLeistung() {
    return fakturierfaehigeLeistung;
  }

  public void setFakturierfaehigeLeistung(final BigDecimal fakturierfaehigeLeistung) {
    this.fakturierfaehigeLeistung = fakturierfaehigeLeistung;
  }

  public BigDecimal getReiseKosten() {
    return reiseKosten;
  }

  public void setReiseKosten(final BigDecimal reiseKosten) {
    this.reiseKosten = reiseKosten;
  }

  public BigDecimal getReiseLeistung() {
    return reiseLeistung;
  }

  public void setReiseLeistung(final BigDecimal reiseLeistung) {
    this.reiseLeistung = reiseLeistung;
  }

  public BigDecimal getSonderzeitKosten() {
    return sonderzeitKosten;
  }

  public void setSonderzeitKosten(final BigDecimal sonderzeitKosten) {
    this.sonderzeitKosten = sonderzeitKosten;
  }

  public BigDecimal getSonderzeitLeistung() {
    return sonderzeitLeistung;
  }

  public void setSonderzeitLeistung(final BigDecimal sonderzeitLeistung) {
    this.sonderzeitLeistung = sonderzeitLeistung;
  }

  public BigDecimal getSonstigeKosten() {
    return sonstigeKosten;
  }

  public void setSonstigeKosten(final BigDecimal sonstigeKosten) {
    this.sonstigeKosten = sonstigeKosten;
  }

  public BigDecimal getSonstigeLeistung() {
    return sonstigeLeistung;
  }

  public void setSonstigeLeistung(final BigDecimal sonstigeLeistung) {
    this.sonstigeLeistung = sonstigeLeistung;
  }

  public boolean isOhneMitarbeiterBezug() {
    return ohneMitarbeiterBezug;
  }

  public void setOhneMitarbeiterBezug(boolean ohneMitarbeiterBezug) {
    this.ohneMitarbeiterBezug = ohneMitarbeiterBezug;
  }

}
