package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("C_MitarbeiterTyp")
public class MitarbeiterTypKonstante {

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
  private Boolean istDefault;

  @Column("SortierNr")
  private Integer sortierNr;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTextKurz() {
    return textKurz;
  }

  public void setTextKurz(String textKurz) {
    this.textKurz = textKurz;
  }

  public String getTextLang() {
    return textLang;
  }

  public void setTextLang(String textLang) {
    this.textLang = textLang;
  }

  public BigDecimal getWert() {
    return wert;
  }

  public void setWert(BigDecimal wert) {
    this.wert = wert;
  }

  public Boolean isIstDefault() {
    return istDefault;
  }

  public void setIstDefault(Boolean istDefault) {
    this.istDefault = istDefault;
  }

  public Integer getSortierNr() {
    return sortierNr;
  }

  public void setSortierNr(Integer sortierNr) {
    this.sortierNr = sortierNr;
  }

}
