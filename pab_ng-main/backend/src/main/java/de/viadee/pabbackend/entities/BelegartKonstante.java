package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("C_BelegArt")
public class BelegartKonstante {

  @Id
  @Column("ID")
  private Long id;

  @Column("TextKurz")
  private String textKurz;

  @Column("TextLang")
  private String textLang;

  @Column("Wert")
  private BigDecimal wert;

  @Column("IstDefault")
  private boolean istDefault;

  @Column("SortierNr")
  private Integer sortierNr;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getTextKurz() {
    return textKurz;
  }

  public void setTextKurz(final String textKurz) {
    this.textKurz = textKurz;
  }

  public String getTextLang() {
    return textLang;
  }

  public void setTextLang(final String textLang) {
    this.textLang = textLang;
  }

  public BigDecimal getWert() {
    return wert;
  }

  public void setWert(final BigDecimal wert) {
    this.wert = wert;
  }

  public boolean isIstDefault() {
    return istDefault;
  }

  public void setIstDefault(final boolean istDefault) {
    this.istDefault = istDefault;
  }

  public Integer getSortierNr() {
    return sortierNr;
  }

  public void setSortierNr(final Integer sortierNr) {
    this.sortierNr = sortierNr;
  }
}
