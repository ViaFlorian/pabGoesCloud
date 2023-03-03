package de.viadee.pabbackend.entities;

import java.time.LocalDate;

public class FehlendeDreiMonatsRegel implements Comparable<FehlendeDreiMonatsRegel> {

  private Long kundeId;

  private Mitarbeiter mitarbeiter;

  private LocalDate tagVon;

  private LocalDate tagBis;

  private LocalDate verfallsdatum;

  public LocalDate getTagBis() {
    return tagBis;
  }

  public void setTagBis(final LocalDate tagBis) {
    this.tagBis = tagBis;
  }

  public Long getKundeId() {
    return kundeId;
  }

  public void setKundeId(final Long kundeId) {
    this.kundeId = kundeId;
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(final Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public LocalDate getTagVon() {
    return tagVon;
  }

  public void setTagVon(final LocalDate tagVon) {
    this.tagVon = tagVon;
  }

  public LocalDate getVerfallsdatum() {
    return verfallsdatum;
  }

  public void setVerfallsdatum(final LocalDate verfallsdatum) {
    this.verfallsdatum = verfallsdatum;
  }

  @Override
  public int compareTo(final FehlendeDreiMonatsRegel other) {
    return this.getTagVon().compareTo(other.getTagVon());
  }

}
