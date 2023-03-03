package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class AbstractSpesenUndZuschlagsberechnungsErgebnis {

  private LocalDate tag;

  private BigDecimal anzahlStunden;

  private boolean istMitFruehstueck;

  private boolean istMitMittagessen;

  private boolean istMitAbendessen;

  private boolean istAnreiseOderAbreiseTag;

  private Abwesenheit abwesenheit;

  public BigDecimal getAnzahlStunden() {
    return anzahlStunden;
  }

  public void setAnzahlStunden(final BigDecimal anzahlStunden) {
    this.anzahlStunden = anzahlStunden;
  }

  public boolean istAnreiseOderAbreiseTag() {
    return istAnreiseOderAbreiseTag;
  }

  public void setIstAnreiseOderAbreiseTag(final boolean istAnreiseOderAbreiseTag) {
    this.istAnreiseOderAbreiseTag = istAnreiseOderAbreiseTag;
  }

  public boolean istMitFruehstueck() {
    return istMitFruehstueck;
  }

  public void setIstMitFruehstueck(final boolean istMitFruehstueck) {
    this.istMitFruehstueck = istMitFruehstueck;
  }

  public boolean istMitMittagessen() {
    return istMitMittagessen;
  }

  public void setIstMitMittagessen(final boolean istMitMittagessen) {
    this.istMitMittagessen = istMitMittagessen;
  }

  public boolean istMitAbendessen() {
    return istMitAbendessen;
  }

  public void setIstMitAbendessen(final boolean istMitAbendessen) {
    this.istMitAbendessen = istMitAbendessen;
  }

  public Abwesenheit getAbwesenheit() {
    return abwesenheit;
  }

  public void setAbwesenheit(final Abwesenheit abwesenheit) {
    this.abwesenheit = abwesenheit;
  }

  public LocalDate getTag() {
    return tag;
  }

  public void setTag(final LocalDate tag) {
    this.tag = tag;
  }

}
