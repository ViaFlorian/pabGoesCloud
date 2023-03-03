package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("MitarbeiterStundenKonto")
public class MitarbeiterStundenKonto implements Comparable<MitarbeiterStundenKonto> {

    @Id
    @Column("ID")
    private Long id;

    @Column("MitarbeiterID")
    private Long mitarbeiterId;

    @Column("Wertstellung")
    private LocalDate wertstellung;

    @Column("Buchungsdatum")
    private LocalDateTime buchungsdatum;

    @Column("AnzahlStunden")
    private BigDecimal anzahlStunden;

    @Column("lfdSaldo")
    private BigDecimal lfdSaldo;

    @Column("BuchungstypStundenID")
    private Long buchungstypStundenId;

    @Column("IstAutomatisch")
    private boolean automatisch;

    @Column("IstEndgueltig")
    private boolean endgueltig;

    @LastModifiedBy
    @Column("ZuletztGeaendertVon")
    private String zuletztGeaendertVon;

    @Column("Bemerkung")
    private String bemerkung;

    @Transient
    private boolean geaendert;

    public String getZuletztGeaendertVon() {
        return zuletztGeaendertVon;
    }

    public void setZuletztGeaendertVon(final String zuletztGeaendertVon) {
        this.zuletztGeaendertVon = zuletztGeaendertVon;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getMitarbeiterId() {
        return mitarbeiterId;
    }

    public void setMitarbeiterId(final Long mitarbeiterId) {
        this.mitarbeiterId = mitarbeiterId;
    }

    public LocalDate getWertstellung() {
        return wertstellung;
    }

    public void setWertstellung(LocalDate wertstellung) {
        this.wertstellung = wertstellung;
    }

    public LocalDateTime getBuchungsdatum() {
        return buchungsdatum;
    }

    public void setBuchungsdatum(LocalDateTime buchungsdatum) {
        this.buchungsdatum = buchungsdatum;
    }

    public BigDecimal getAnzahlStunden() {
        return anzahlStunden;
    }

    public void setAnzahlStunden(BigDecimal anzahlStunden) {
        this.anzahlStunden = anzahlStunden;
    }

    public BigDecimal getLfdSaldo() {
        return lfdSaldo;
    }

    public void setLfdSaldo(BigDecimal lfdSaldo) {
        this.lfdSaldo = lfdSaldo;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getBuchungstypStundenId() {
        return buchungstypStundenId;
    }

    public void setBuchungstypStundenId(final Long buchungstypStundenId) {
        this.buchungstypStundenId = buchungstypStundenId;
    }

    public boolean isAutomatisch() {
        return automatisch;
    }

    public void setAutomatisch(boolean automatisch) {
        this.automatisch = automatisch;
    }

    public boolean isEndgueltig() {
        return endgueltig;
    }

    public void setEndgueltig(boolean endgueltig) {
        this.endgueltig = endgueltig;
    }

    public boolean isGeaendert() {
        return geaendert;
    }

    public void setGeaendert(boolean geaendert) {
        this.geaendert = geaendert;
    }

    @Override
    public int compareTo(MitarbeiterStundenKonto that) {
        if (this.wertstellung.compareTo(that.wertstellung) > 0) {
            return -1;
        } else if (this.wertstellung.compareTo(that.wertstellung) < 0) {
            return 1;
        }

        if (this.buchungsdatum.compareTo(that.buchungsdatum) > 0) {
            return -1;
        } else if (this.buchungsdatum.compareTo(that.buchungsdatum) < 0) {
            return 1;
        }
        return 0;
    }

}
