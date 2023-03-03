package de.viadee.pabbackend.entities;

import java.math.BigDecimal;

public class ErgebnisB006UrlaubskontoProMonat {

  private String modifier;

  private String art;

  private BigDecimal januar;

  private BigDecimal februar;

  private BigDecimal maerz;

  private BigDecimal april;

  private BigDecimal mai;

  private BigDecimal juni;

  private BigDecimal juli;

  private BigDecimal august;

  private BigDecimal september;

  private BigDecimal oktober;

  private BigDecimal november;

  private BigDecimal dezember;

  private BigDecimal zeileSumme;

  public BigDecimal getZeileSumme() {

    if (art.equals("Resturlaub") || art.equals("Übertrag")) {
      return null;
    }

    BigDecimal ergebnis = null;
    if (januar != null) {
      ergebnis = januar;
    }
    if (februar != null) {
      ergebnis = ergebnis == null ? februar : ergebnis.add(februar);
    }
    if (maerz != null) {
      ergebnis = ergebnis == null ? maerz : ergebnis.add(maerz);
    }
    if (april != null) {
      ergebnis = ergebnis == null ? april : ergebnis.add(april);
    }
    if (mai != null) {
      ergebnis = ergebnis == null ? mai : ergebnis.add(mai);
    }
    if (juni != null) {
      ergebnis = ergebnis == null ? juni : ergebnis.add(juni);
    }
    if (juli != null) {
      ergebnis = ergebnis == null ? juli : ergebnis.add(juli);
    }
    if (august != null) {
      ergebnis = ergebnis == null ? august : ergebnis.add(august);
    }
    if (september != null) {
      ergebnis = ergebnis == null ? september : ergebnis.add(september);
    }
    if (oktober != null) {
      ergebnis = ergebnis == null ? oktober : ergebnis.add(oktober);
    }
    if (november != null) {
      ergebnis = ergebnis == null ? november : ergebnis.add(november);
    }
    if (dezember != null) {
      ergebnis = ergebnis == null ? dezember : ergebnis.add(dezember);
    }

    return ergebnis;
  }

  public void setZeileSumme(final BigDecimal zeileSumme) {
    this.zeileSumme = zeileSumme;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(final String modifier) {
    this.modifier = modifier;
  }

  public String getArt() {
    return art;
  }

  public void setArt(final String art) {
    this.art = art;
  }

  public BigDecimal getJanuar() {
    return januar;
  }

  public void setJanuar(final BigDecimal januar) {
    this.januar = januar;
  }

  public BigDecimal getFebruar() {
    return februar;
  }

  public void setFebruar(final BigDecimal februar) {
    this.februar = februar;
  }

  public BigDecimal getMaerz() {
    return maerz;
  }

  public void setMaerz(final BigDecimal maerz) {
    this.maerz = maerz;
  }

  public BigDecimal getApril() {
    return april;
  }

  public void setApril(final BigDecimal april) {
    this.april = april;
  }

  public BigDecimal getMai() {
    return mai;
  }

  public void setMai(final BigDecimal mai) {
    this.mai = mai;
  }

  public BigDecimal getJuni() {
    return juni;
  }

  public void setJuni(final BigDecimal juni) {
    this.juni = juni;
  }

  public BigDecimal getJuli() {
    return juli;
  }

  public void setJuli(final BigDecimal juli) {
    this.juli = juli;
  }

  public BigDecimal getAugust() {
    return august;
  }

  public void setAugust(final BigDecimal august) {
    this.august = august;
  }

  public BigDecimal getSeptember() {
    return september;
  }

  public void setSeptember(final BigDecimal september) {
    this.september = september;
  }

  public BigDecimal getOktober() {
    return oktober;
  }

  public void setOktober(final BigDecimal oktober) {
    this.oktober = oktober;
  }

  public BigDecimal getNovember() {
    return november;
  }

  public void setNovember(final BigDecimal november) {
    this.november = november;
  }

  public BigDecimal getDezember() {
    return dezember;
  }

  public void setDezember(final BigDecimal dezember) {
    this.dezember = dezember;
  }
}
