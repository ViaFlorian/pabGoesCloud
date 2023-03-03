package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB016Bericht implements Comparable<ErgebnisB016Bericht> {

  private Mitarbeiter mitarbeiter;

  private String mitarbeiterTyp;

  private BigDecimal stundenBE01;

  private BigDecimal stundenBE02;

  private BigDecimal personalUKS;

  private BigDecimal personalKomm;

  private BigDecimal personalSonst;

  private BigDecimal stundenBE04;

  private BigDecimal stundenBE05;

  private BigDecimal stundenOEIntern;

  private BigDecimal stundenOEExtern;

  private BigDecimal oe11Intern;

  private BigDecimal oe11Extern;

  private BigDecimal oe12Intern;

  private BigDecimal oe12Extern;

  private BigDecimal oe13Intern;

  private BigDecimal oe13Extern;

  private BigDecimal oe14Intern;

  private BigDecimal oe14Extern;

  private BigDecimal oe15Intern;

  private BigDecimal oe15Extern;

  private BigDecimal oe16Intern;

  private BigDecimal oe16Extern;

  private BigDecimal oe17Intern;

  private BigDecimal oe17Extern;

  private BigDecimal oe18Intern;

  private BigDecimal oe18Extern;

  private BigDecimal oe19Intern;

  private BigDecimal oe19Extern;

  private BigDecimal oe20Intern;

  private BigDecimal oe20Extern;

  private BigDecimal oe21Intern;

  private BigDecimal oe21Extern;

  private BigDecimal sollstunden;

  private Mitarbeiter vorgesetzter;

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(final Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterName() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public String getVorgesetzterName() {
    return vorgesetzter == null ? "" : vorgesetzter.getFullName();
  }

  public String getMitarbeiterTyp() {
    return mitarbeiterTyp == null ? "" : mitarbeiterTyp;
  }

  public void setMitarbeiterTyp(final String mitarbeiterTyp) {
    this.mitarbeiterTyp = mitarbeiterTyp;
  }

  public BigDecimal getStundenBE01() {
    return stundenBE01;
  }

  public void setStundenBE01(final BigDecimal stundenBE01) {
    this.stundenBE01 = stundenBE01;
  }

  public BigDecimal getStundenBE02() {
    return stundenBE02;
  }

  public void setStundenBE02(final BigDecimal stundenBE02) {
    this.stundenBE02 = stundenBE02;
  }

  public BigDecimal getPersonalUKS() {
    return personalUKS;
  }

  public void setPersonalUKS(final BigDecimal personalUKS) {
    this.personalUKS = personalUKS;
  }

  public BigDecimal getPersonalKomm() {
    return personalKomm;
  }

  public void setPersonalKomm(final BigDecimal personalKomm) {
    this.personalKomm = personalKomm;
  }

  public BigDecimal getPersonalSonst() {
    return personalSonst;
  }

  public void setPersonalSonst(final BigDecimal personalSonst) {
    this.personalSonst = personalSonst;
  }

  public BigDecimal getStundenBE04() {
    return stundenBE04;
  }

  public void setStundenBE04(final BigDecimal stundenBE04) {
    this.stundenBE04 = stundenBE04;
  }

  public BigDecimal getStundenBE05() {
    return stundenBE05;
  }

  public void setStundenBE05(final BigDecimal stundenBE05) {
    this.stundenBE05 = stundenBE05;
  }

  public BigDecimal getStundenOEIntern() {
    return stundenOEIntern;
  }

  public void setStundenOEIntern(final BigDecimal stundenOEIntern) {
    this.stundenOEIntern = stundenOEIntern;
  }

  public BigDecimal getStundenOEExtern() {
    return stundenOEExtern;
  }

  public void setStundenOEExtern(final BigDecimal stundenOEExtern) {
    this.stundenOEExtern = stundenOEExtern;
  }

  public BigDecimal getOe11Intern() {
    return oe11Intern;
  }

  public void setOe11Intern(final BigDecimal oe11Intern) {
    this.oe11Intern = oe11Intern;
  }

  public BigDecimal getOe11Extern() {
    return oe11Extern;
  }

  public void setOe11Extern(final BigDecimal oe11Extern) {
    this.oe11Extern = oe11Extern;
  }

  public BigDecimal getOe12Intern() {
    return oe12Intern;
  }

  public void setOe12Intern(final BigDecimal oe12Intern) {
    this.oe12Intern = oe12Intern;
  }

  public BigDecimal getOe12Extern() {
    return oe12Extern;
  }

  public void setOe12Extern(final BigDecimal oe12Extern) {
    this.oe12Extern = oe12Extern;
  }

  public BigDecimal getOe13Intern() {
    return oe13Intern;
  }

  public void setOe13Intern(final BigDecimal oe13Intern) {
    this.oe13Intern = oe13Intern;
  }

  public BigDecimal getOe13Extern() {
    return oe13Extern;
  }

  public void setOe13Extern(final BigDecimal oe13Extern) {
    this.oe13Extern = oe13Extern;
  }

  public BigDecimal getOe14Intern() {
    return oe14Intern;
  }

  public void setOe14Intern(final BigDecimal oe14Intern) {
    this.oe14Intern = oe14Intern;
  }

  public BigDecimal getOe14Extern() {
    return oe14Extern;
  }

  public void setOe14Extern(final BigDecimal oe14Extern) {
    this.oe14Extern = oe14Extern;
  }

  public BigDecimal getOe15Intern() {
    return oe15Intern;
  }

  public void setOe15Intern(final BigDecimal oe15Intern) {
    this.oe15Intern = oe15Intern;
  }

  public BigDecimal getOe15Extern() {
    return oe15Extern;
  }

  public void setOe15Extern(final BigDecimal oe15Extern) {
    this.oe15Extern = oe15Extern;
  }

  public BigDecimal getOe16Intern() {
    return oe16Intern;
  }

  public void setOe16Intern(final BigDecimal oe16Intern) {
    this.oe16Intern = oe16Intern;
  }

  public BigDecimal getOe16Extern() {
    return oe16Extern;
  }

  public void setOe16Extern(final BigDecimal oe16Extern) {
    this.oe16Extern = oe16Extern;
  }

  public BigDecimal getOe17Intern() {
    return oe17Intern;
  }

  public void setOe17Intern(final BigDecimal oe17Intern) {
    this.oe17Intern = oe17Intern;
  }

  public BigDecimal getOe17Extern() {
    return oe17Extern;
  }

  public void setOe17Extern(final BigDecimal oe17Extern) {
    this.oe17Extern = oe17Extern;
  }

  public BigDecimal getOe18Intern() {
    return oe18Intern;
  }

  public void setOe18Intern(final BigDecimal oe18Intern) {
    this.oe18Intern = oe18Intern;
  }

  public BigDecimal getOe18Extern() {
    return oe18Extern;
  }

  public void setOe18Extern(final BigDecimal oe18Extern) {
    this.oe18Extern = oe18Extern;
  }

  public BigDecimal getOe19Intern() {
    return oe19Intern;
  }

  public void setOe19Intern(final BigDecimal oe19Intern) {
    this.oe19Intern = oe19Intern;
  }

  public BigDecimal getOe19Extern() {
    return oe19Extern;
  }

  public void setOe19Extern(final BigDecimal oe19Extern) {
    this.oe19Extern = oe19Extern;
  }

  public BigDecimal getOe20Intern() {
    return oe20Intern;
  }

  public void setOe20Intern(final BigDecimal oe20Intern) {
    this.oe20Intern = oe20Intern;
  }

  public BigDecimal getOe20Extern() {
    return oe20Extern;
  }

  public void setOe20Extern(final BigDecimal oe20Extern) {
    this.oe20Extern = oe20Extern;
  }

  public BigDecimal getOe21Intern() {
    return oe21Intern;
  }

  public void setOe21Intern(final BigDecimal oe21Intern) {
    this.oe21Intern = oe21Intern;
  }

  public BigDecimal getOe21Extern() {
    return oe21Extern;
  }

  public void setOe21Extern(final BigDecimal oe21Extern) {
    this.oe21Extern = oe21Extern;
  }

  public BigDecimal getSollstunden() {
    return sollstunden;
  }

  public void setSollstunden(final BigDecimal sollstunden) {
    this.sollstunden = sollstunden;
  }

  public Mitarbeiter getVorgesetzter() {
    return vorgesetzter;
  }

  public void setVorgesetzter(final Mitarbeiter vorgesetzter) {
    this.vorgesetzter = vorgesetzter;
  }

  @Override
  public int compareTo(ErgebnisB016Bericht that) {

    if (this.vorgesetzter != null && that.getVorgesetzter() != null) {
      return this.vorgesetzter.getFullName().compareTo(that.getVorgesetzter().getFullName());
    }

    return 0;
  }
}
