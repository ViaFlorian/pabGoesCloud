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


@Table("Faktur")
public class Faktur {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektID")
  private Long projektId;

  @Column("ReferenzMonat")
  private int referenzMonat;

  @Column("ReferenzJahr")
  private int referenzJahr;

  @Column("Rechnungsdatum")
  private LocalDateTime rechnungsdatum;

  @Column("BetragNetto")
  private BigDecimal betragNetto;

  @Column("Rechnungsnummer")
  private String rechnungsnummer;

  @Column("NichtBudgetrelevant")
  private BigDecimal nichtBudgetRelevant;

  @Column("AbweichenderRechnungsempfaengerKundeID")
  private String abweichenderRechnungsempfaengerKundeId;

  @Column("Umsatzsteuer")
  private BigDecimal umsatzsteuer;

  @Column("Bemerkung")
  private String bemerkung;

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
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public int getReferenzMonat() {
    return referenzMonat;
  }

  public void setReferenzMonat(final int referenzMonat) {
    this.referenzMonat = referenzMonat;
  }

  public int getReferenzJahr() {
    return referenzJahr;
  }

  public void setReferenzJahr(final int referenzJahr) {
    this.referenzJahr = referenzJahr;
  }

  public LocalDateTime getRechnungsdatum() {
    return rechnungsdatum;
  }

  public void setRechnungsdatum(final LocalDateTime rechnungsdatum) {
    this.rechnungsdatum = rechnungsdatum;
  }

  public BigDecimal getBetragNetto() {
    return betragNetto;
  }

  public void setBetragNetto(final BigDecimal betragNetto) {
    this.betragNetto = betragNetto;
  }

  public String getRechnungsnummer() {
    return rechnungsnummer;
  }

  public void setRechnungsnummer(final String rechnungsnummer) {
    this.rechnungsnummer = rechnungsnummer;
  }

  public BigDecimal getNichtBudgetRelevant() {
    return nichtBudgetRelevant;
  }

  public void setNichtBudgetRelevant(final BigDecimal nichtBudgetRelevant) {
    this.nichtBudgetRelevant = nichtBudgetRelevant;
  }

  public String getAbweichenderRechnungsempfaengerKundeId() {
    return abweichenderRechnungsempfaengerKundeId;
  }

  public void setAbweichenderRechnungsempfaengerKundeId(
      final String abweichenderRechnungsempfaengerKundeId) {
    this.abweichenderRechnungsempfaengerKundeId = abweichenderRechnungsempfaengerKundeId;
  }

  public BigDecimal getUmsatzsteuer() {
    return umsatzsteuer;
  }

  public void setUmsatzsteuer(final BigDecimal umsatzsteuer) {
    this.umsatzsteuer = umsatzsteuer;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public LocalDateTime getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(final LocalDateTime zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }
}
