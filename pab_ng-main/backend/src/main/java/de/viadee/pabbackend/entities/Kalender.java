package de.viadee.pabbackend.entities;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Kalender")
public class Kalender {

  @Id
  @Column("Datum")
  private LocalDate datum;

  @Column("IstFeiertag")
  private boolean istFeiertag;

  @Column("Wochentag")
  private Integer wochentag;

  @Column("Jahr")
  private Integer jahr;

  @Column("Monat")
  private Integer monat;

  @Column("Tag")
  private Integer tag;

  public Integer getWochentag() {
    return wochentag;
  }

  public void setWochentag(Integer wochentag) {
    this.wochentag = wochentag;
  }

  public LocalDate getDatum() {
    return datum;
  }

  public void setDatum(final LocalDate datum) {
    this.datum = datum;
  }

  public boolean istFeiertag() {
    return istFeiertag;
  }

  public void setIstFeiertag(final boolean istFeiertag) {
    this.istFeiertag = istFeiertag;
  }

}
