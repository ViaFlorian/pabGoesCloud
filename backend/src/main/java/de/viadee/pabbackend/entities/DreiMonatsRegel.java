package de.viadee.pabbackend.entities;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("DreiMonatsRegel")
public class DreiMonatsRegel implements Comparable<DreiMonatsRegel> {

  @Id
  @Column("ID")
  private Long id;

  @Column("KundeID")
  private String kundeScribeId;

  @Column("Arbeitsstaette")
  private String arbeitsstaette;

  @Column("MitarbeiterID")
  private Long mitarbeiterId;

  @Column("gueltigVon")
  private LocalDate gueltigVon;

  @Column("gueltigBis")
  private LocalDate gueltigBis;

  @Column("AutomatischErfasst")
  private boolean automatischErfasst;

  public boolean isAutomatischErfasst() {
    return automatischErfasst;
  }

  public void setAutomatischErfasst(final boolean automatischErfasst) {
    this.automatischErfasst = automatischErfasst;
  }

  public String getArbeitsstaette() {
    return arbeitsstaette;
  }

  public void setArbeitsstaette(final String arbeitsstaette) {
    this.arbeitsstaette = arbeitsstaette;
  }

  public String getKundeScribeId() {
    return kundeScribeId;
  }

  public void setKundeScribeId(final String kundeId) {
    this.kundeScribeId = kundeId;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(final Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public LocalDate getGueltigVon() {
    return gueltigVon;
  }

  public void setGueltigVon(final LocalDate gueltigVon) {
    this.gueltigVon = gueltigVon;
  }

  public LocalDate getGueltigBis() {
    return gueltigBis;
  }

  public void setGueltigBis(final LocalDate gueltigBis) {
    this.gueltigBis = gueltigBis;
  }

  @Override
  public int compareTo(final DreiMonatsRegel other) {
    return this.getGueltigVon().compareTo(other.gueltigVon);
  }

}
