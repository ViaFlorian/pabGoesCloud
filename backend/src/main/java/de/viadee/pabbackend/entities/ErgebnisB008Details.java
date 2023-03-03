package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB008Details {

  @Column("Tag")
  private Integer tag;
  @Column("Monat")
  private Integer monat;
  @Column("Text")
  private String text;
  @Column("AnzahlStunden")
  private BigDecimal stunden;
  @Column("Betrag")
  private BigDecimal betrag;

  public Integer getTag() {
    return tag;
  }

  public void setTag(final Integer tag) {
    this.tag = tag;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(final Integer monat) {
    this.monat = monat;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public BigDecimal getStunden() {
    return stunden;
  }

  public void setStunden(final BigDecimal stunden) {
    this.stunden = stunden;
  }

  public BigDecimal getBetrag() {
    return betrag;
  }

  public void setBetrag(BigDecimal betrag) {
    this.betrag = betrag;
  }

  public String getDetails() {
    NumberFormat num = NumberFormat.getInstance(Locale.GERMAN);
    num.setMaximumFractionDigits(2);
    num.setMinimumFractionDigits(0);
    num.setGroupingUsed(false);

    StringBuilder sb = new StringBuilder();

    if (tag != null && monat != null) {
      sb.append(String.format("%02d.%02d.: ", tag, monat));
    }

    if (text != null) {
      sb.append(text + " ");
    }

    if (stunden != null) {
      String stundenString = num.format(stunden);
      sb.append(String.format("%sh", stundenString));
    }

    num.setMinimumFractionDigits(2);
    if (betrag != null) {
      String betragString = num.format(betrag);
      sb.append(String.format("%s€", betragString));
    }

    return sb.length() > 0 ? sb.toString() : null;
  }
}
