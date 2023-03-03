package de.viadee.pabbackend.entities;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ProjektabrechnungKorrekturbuchung")
public class ProjektabrechnungKorrekturbuchung {

  @Id
  @Column("ID")
  private Long id;

  @Column("ProjektabrechnungID")
  private Long projektabrechnungId;

  @Column("GegenbuchungID")
  private String gegenbuchungID;

  @Column("istKorrekturbuchung")
  @ReadOnlyProperty
  private Boolean istKorrekturbuchung;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("ReferenzJahr")
  private Integer referenzJahr;

  @Column("ReferenzMonat")
  private Integer referenzMonat;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("KostenartID")
  private Long kostenartId;

  @Column("ProjektId")
  private Long projektId;

  @Column("AnzahlStundenKosten")
  private BigDecimal anzahlStundenKosten;

  @Column("BetragKostensatz")
  private BigDecimal betragKostensatz;

  @Column("AnzahlStundenLeistung")
  private BigDecimal anzahlStundenLeistung;

  @Column("BetragStundensatz")
  private BigDecimal betragStundensatz;

  @Column("Bemerkung")
  private String bemerkung;

  @LastModifiedBy
  @Column("ZuletztGeaendertVon")
  private String zuletztGeaendertVon;

  @LastModifiedDate
  @Column("ZuletztGeaendertAm")
  private LocalDate zuletztGeaendertAm;

  @Transient
  private BigDecimal stundendifferenzGegenbuchung;

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
  public String getGegenbuchungID() {
    return gegenbuchungID;
  }

  public void setGegenbuchungID(final String gegenbuchungID) {
    this.gegenbuchungID = gegenbuchungID;
  }

  public Boolean getIstKorrekturbuchung() {
    return istKorrekturbuchung;
  }

  public void setIstKorrekturbuchung(final Boolean istKorrekturbuchung) {
    this.istKorrekturbuchung = istKorrekturbuchung;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(final Integer monat) {
    this.monat = monat;
  }

  public Integer getReferenzJahr() {
    return referenzJahr;
  }

  public void setReferenzJahr(final Integer referenzJahr) {
    this.referenzJahr = referenzJahr;
  }

  public Integer getReferenzMonat() {
    return referenzMonat;
  }

  public void setReferenzMonat(final Integer referenzMonat) {
    this.referenzMonat = referenzMonat;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getKostenartId() {
    return kostenartId;
  }

  public void setKostenartId(final Long kostenartId) {
    this.kostenartId = kostenartId;
  }

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(final Long projektId) {
    this.projektId = projektId;
  }

  public BigDecimal getAnzahlStundenKosten() {
    return anzahlStundenKosten;
  }

  public void setAnzahlStundenKosten(final BigDecimal anzahlStundenKosten) {
    this.anzahlStundenKosten = anzahlStundenKosten;
  }

  public BigDecimal getBetragKostensatz() {
    return betragKostensatz;
  }

  public void setBetragKostensatz(final BigDecimal betragKostensatz) {
    this.betragKostensatz = betragKostensatz;
  }

  public BigDecimal getAnzahlStundenLeistung() {
    return anzahlStundenLeistung;
  }

  public void setAnzahlStundenLeistung(final BigDecimal anzahlStundenLeistung) {
    this.anzahlStundenLeistung = anzahlStundenLeistung;
  }

  public BigDecimal getBetragStundensatz() {
    return betragStundensatz;
  }

  public void setBetragStundensatz(final BigDecimal betragStundensatz) {
    this.betragStundensatz = betragStundensatz;
  }

  public String getBemerkung() {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung) {
    this.bemerkung = bemerkung;
  }

  public String getZuletztGeaendertVon() {
    return zuletztGeaendertVon;
  }

  public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
    this.zuletztGeaendertVon = zuletztGeaendertVon;
  }

  public LocalDate getZuletztGeaendertAm() {
    return zuletztGeaendertAm;
  }

  public void setZuletztGeaendertAm(final LocalDate zuletztGeaendertAm) {
    this.zuletztGeaendertAm = zuletztGeaendertAm;
  }

  public BigDecimal getStundendifferenzGegenbuchung() {
    return stundendifferenzGegenbuchung;
  }

  public void setStundendifferenzGegenbuchung(final BigDecimal stundendifferenzGegenbuchung) {
    this.stundendifferenzGegenbuchung = stundendifferenzGegenbuchung;
  }

  public BigDecimal getKosten() {
    if (anzahlStundenKosten == null && betragKostensatz == null) {
      return null;
    } else {
      return berechneProdukt(getAnzahlStundenKosten(), getBetragKostensatz());
    }
  }

  public BigDecimal getLeistung() {
    if (anzahlStundenLeistung == null && betragStundensatz == null) {
      return null;
    } else {
      return berechneProdukt(getAnzahlStundenLeistung(), getBetragStundensatz());
    }
  }

  private BigDecimal berechneProdukt(final BigDecimal zahl1, final BigDecimal zahl2) {
    BigDecimal faktor1 =
        zahl1 == null || zahl1.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : zahl1;
    BigDecimal faktor2 =
        zahl2 == null || zahl2.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : zahl2;
    return faktor1.multiply(faktor2);
  }


}
