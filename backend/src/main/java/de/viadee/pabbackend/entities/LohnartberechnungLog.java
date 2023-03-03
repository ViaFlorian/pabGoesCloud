package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("LohnartenberechnungLog")
public class LohnartberechnungLog {

  @Id
  @Column("ID")
  private Long id;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Column("Konto")
  private String konto;

  @Column("Datum")
  private LocalDate datum;

  @Column("Meldung")
  private String meldung;

  @Column("Wert")
  private BigDecimal wert;

  @Column("Einheit")
  private String einheit;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getEinheit() {
    return einheit;
  }

  public void setEinheit(final String einheit) {
    this.einheit = einheit;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(final Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  public String getKonto() {
    return konto;
  }

  public void setKonto(final String konto) {
    this.konto = konto;
  }

  public LocalDate getDatum() {
    return datum;
  }

  public void setDatum(final LocalDate datum) {
    this.datum = datum;
  }

  public String getMeldung() {
    return meldung;
  }

  public void setMeldung(final String meldung) {
    this.meldung = meldung;
  }

  public BigDecimal getWert() {
    return wert;
  }

  public void setWert(final BigDecimal wert) {
    this.wert = wert;
  }

}
