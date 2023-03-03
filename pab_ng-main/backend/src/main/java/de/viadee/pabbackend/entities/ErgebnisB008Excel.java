package de.viadee.pabbackend.entities;


import de.viadee.pabbackend.util.FormatFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB008Excel {

  @Transient
  private Projekt projekt;
  @Transient
  private Abrechnungsmonat abrechnungsmonat;
  @Transient
  private Mitarbeiter mitarbeiter;
  @Transient
  private List<ErgebnisB008Details> details;
  @Column("ProjektID")
  private Long projektId;
  @Column("Jahr")
  private Integer jahr;
  @Column("Monat")
  private Integer monat;
  @Column("Kategorie")
  private String kategorie;
  @Column("MitarbeiterID")
  private Long mitarbeiterId;
  @Column("Subkategorie")
  private String subkategorie;
  @Column("StundenLeistungen")
  private BigDecimal stundenLeistungen;
  @Column("StundensatzLeistungen")
  private BigDecimal stundensatzLeistungen;
  @Column("HonorarLeistungen")
  private BigDecimal honorarLeistungen;
  @Column("NebenkostenLeistungen")
  private BigDecimal nebenkostenLeistungen;
  @Column("StundenKosten")
  private BigDecimal stundenKosten;
  @Column("StundensatzKosten")
  private BigDecimal stundensatzKosten;
  @Column("HonorarKosten")
  private BigDecimal honorarKosten;
  @Column("NebenkostenKosten")
  private BigDecimal nebenkostenKosten;

  public Projekt getProjekt() {
    return projekt;
  }

  public void setProjekt(Projekt projekt) {
    this.projekt = projekt;
  }

  public String getProjektnummer() {
    return projekt.getProjektnummer();
  }

  public String getProjektbezeichnung() {
    return projekt.getBezeichnung();
  }

  public Abrechnungsmonat getAbrechnungsmonat() {
    return abrechnungsmonat;
  }

  public void setAbrechnungsmonat(Abrechnungsmonat abrechnungsmonat) {
    this.abrechnungsmonat = abrechnungsmonat;
  }

  public String getAbrechnungsmonatBezeichnung() {
    LocalDate date = LocalDate.of(abrechnungsmonat.getJahr(), abrechnungsmonat.getMonat(), 1);
    return date.format(FormatFactory.deutscherMonatsnameUndJahrFormatter());
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterBezeichnung() {
    if (mitarbeiter == null) {
      return null;
    }
    String sb = (mitarbeiter.getAnrede() == null ? "" : mitarbeiter.getAnrede() + " ")
        + (mitarbeiter.getTitel() == null ? "" : mitarbeiter.getTitel() + " ")
        + (mitarbeiter.getNachname() == null ? "" : mitarbeiter.getNachname());
    return sb;
  }

  public Long getProjektId() {
    return projektId;
  }

  public void setProjektId(Long projektId) {
    this.projektId = projektId;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getMonat() {
    return monat;
  }

  public void setMonat(Integer monat) {
    this.monat = monat;
  }

  public String getKategorie() {
    return kategorie;
  }

  public void setKategorie(String kategorie) {
    this.kategorie = kategorie;
  }

  public Long getMitarbeiterId() {
    return mitarbeiterId;
  }

  public void setMitarbeiterId(Long mitarbeiterId) {
    this.mitarbeiterId = mitarbeiterId;
  }

  public String getSubkategorie() {
    return subkategorie;
  }

  public void setSubkategorie(String subkategorie) {
    this.subkategorie = subkategorie;
  }

  public BigDecimal getStundenLeistungen() {
    return stundenLeistungen;
  }

  public void setStundenLeistungen(BigDecimal stundenLeistungen) {
    this.stundenLeistungen = stundenLeistungen;
  }

  public BigDecimal getStundensatzLeistungen() {
    return stundensatzLeistungen;
  }

  public void setStundensatzLeistungen(BigDecimal stundensatzLeistungen) {
    this.stundensatzLeistungen = stundensatzLeistungen;
  }

  public BigDecimal getHonorarLeistungen() {
    return honorarLeistungen;
  }

  public void setHonorarLeistungen(BigDecimal honorarLeistungen) {
    this.honorarLeistungen = honorarLeistungen;
  }

  public BigDecimal getNebenkostenLeistungen() {
    return nebenkostenLeistungen;
  }

  public void setNebenkostenLeistungen(BigDecimal nebenkostenLeistungen) {
    this.nebenkostenLeistungen = nebenkostenLeistungen;
  }

  public BigDecimal getStundenKosten() {
    return stundenKosten;
  }

  public void setStundenKosten(BigDecimal stundenKosten) {
    this.stundenKosten = stundenKosten;
  }

  public BigDecimal getStundensatzKosten() {
    return stundensatzKosten;
  }

  public void setStundensatzKosten(BigDecimal stundensatzKosten) {
    this.stundensatzKosten = stundensatzKosten;
  }

  public BigDecimal getHonorarKosten() {
    return honorarKosten;
  }

  public void setHonorarKosten(BigDecimal honorarKosten) {
    this.honorarKosten = honorarKosten;
  }

  public BigDecimal getNebenkostenKosten() {
    return nebenkostenKosten;
  }

  public void setNebenkostenKosten(BigDecimal nebenkostenKosten) {
    this.nebenkostenKosten = nebenkostenKosten;
  }

  public String getDetailsText() {
    if (details == null) {
      return null;
    }
    List<String> zeilen = details.stream()
        .map(ErgebnisB008Details::getDetails)
        .collect(Collectors.toList());
    return String.join("\n", zeilen);
  }

  public void setDetails(final List<ErgebnisB008Details> details) {
    this.details = details;
  }
}
