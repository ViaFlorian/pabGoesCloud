package de.viadee.pabbackend.entities;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Neuigkeit")
public class Neuigkeit {

  @Id
  @Column("ID")
  private Long id;

  @Column("Meldung")
  private String meldung;

  @Column("anzeigenVon")
  private LocalDate anzeigenVon;

  @Column("anzeigenBis")
  private LocalDate anzeigenBis;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getMeldung() {
    return meldung;
  }

  public void setMeldung(final String meldung) {
    this.meldung = meldung;
  }

  public LocalDate getAnzeigenVon() {
    return anzeigenVon;
  }

  public void setAnzeigenVon(final LocalDate anzeigenVon) {
    this.anzeigenVon = anzeigenVon;
  }

  public LocalDate getAnzeigenBis() {
    return anzeigenBis;
  }

  public void setAnzeigenBis(final LocalDate anzeigenBis) {
    this.anzeigenBis = anzeigenBis;
  }
}
