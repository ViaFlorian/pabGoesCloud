package de.viadee.pabbackend.entities;

import de.viadee.pabbackend.enums.ImportFehlerklasse;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Fehlerlog")
public class Fehlerlog implements Comparable<Fehlerlog> {

  @Id
  @Column("ID")
  private Long id;

  @Column("Fehlerklasse")
  private ImportFehlerklasse fehlerklasse;

  @Column("Blatt")
  private String blatt;

  @Column("Feld")
  private String zelle;

  @Column("Fehlertext")
  private String fehlertext;

  @Column("Anzahl")
  private BigDecimal anzahl;

  @Column("ArbeitsnachweisID")
  private Long arbeitsnachweisId;

  @Transient
  private Long projektstundeTypId;

  @LastModifiedBy
  @Column("DurchgefuehrtVon")
  private String durchgefuehrtVon;

  public String getDurchgefuehrtVon() {
    return durchgefuehrtVon;
  }

  public void setDurchgefuehrtVon(final String durchgefuehrtVon) {
    this.durchgefuehrtVon = durchgefuehrtVon;
  }

  public Long getProjektstundeTypId() {
    return projektstundeTypId;
  }

  public void setProjektstundeTypId(Long projektstundeTypId) {
    this.projektstundeTypId = projektstundeTypId;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public ImportFehlerklasse getFehlerklasse() {
    return fehlerklasse;
  }

  public void setFehlerklasse(final ImportFehlerklasse fehlerklasse) {
    this.fehlerklasse = fehlerklasse;
  }

  public String getBlatt() {
    return blatt;
  }

  public void setBlatt(final String blatt) {
    this.blatt = blatt;
  }

  public String getZelle() {
    return zelle;
  }

  public void setZelle(final String zelle) {
    this.zelle = zelle;
  }

  public String getFehlertext() {
    return fehlertext;
  }

  public void setFehlertext(final String fehlertext) {
    this.fehlertext = fehlertext;
  }

  public BigDecimal getAnzahl() {
    return anzahl;
  }

  public void setAnzahl(final BigDecimal anzahl) {
    this.anzahl = anzahl;
  }

  public Long getArbeitsnachweisId() {
    return arbeitsnachweisId;
  }

  public void setArbeitsnachweisId(Long arbeitsnachweisId) {
    this.arbeitsnachweisId = arbeitsnachweisId;
  }

  @Override
  public int compareTo(final Fehlerlog anderesFehlerlog) {
    int ergebnis = 0;
    ergebnis = getBlatt().compareTo(anderesFehlerlog.getBlatt());
    if (ergebnis == 0) {
      getFehlerklasse().compareTo(anderesFehlerlog.getFehlerklasse());
    }
    if (ergebnis == 0) {
      getFehlertext().equals(anderesFehlerlog.getFehlertext());
    }
    if (ergebnis == 0) {
      getZelle().equals(anderesFehlerlog.getZelle());
    }
    return 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((blatt == null) ? 0 : blatt.hashCode());
    result = prime * result + ((fehlerklasse == null) ? 0 : fehlerklasse.hashCode());
    result = prime * result + ((fehlertext == null) ? 0 : fehlertext.hashCode());
    result = prime * result + ((zelle == null) ? 0 : zelle.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Fehlerlog other = (Fehlerlog) obj;
    if (blatt == null) {
      if (other.blatt != null) {
        return false;
      }
    } else if (!blatt.equals(other.blatt)) {
      return false;
    }
    if (fehlerklasse != other.fehlerklasse) {
      return false;
    }
    if (fehlertext == null) {
      if (other.fehlertext != null) {
        return false;
      }
    } else if (!fehlertext.equals(other.fehlertext)) {
      return false;
    }
      if (zelle == null) {
          return other.zelle == null;
      } else {
          return zelle.equals(other.zelle);
      }
  }

}
