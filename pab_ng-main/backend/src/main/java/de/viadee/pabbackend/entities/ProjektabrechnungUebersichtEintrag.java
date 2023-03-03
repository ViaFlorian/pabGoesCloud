package de.viadee.pabbackend.entities;


import java.math.BigDecimal;
import java.util.List;

public class ProjektabrechnungUebersichtEintrag {

  private Long projektabrechnungID;

  private Integer bearbeitungsstatus;

  private Abrechnungsmonat abrechnungsmonat;

  private Integer anzahlMitarbeiter;

  private boolean projektIstAktiv;

  private String projektnummer;

  private String projektbezeichnung;

  private String projekttyp;

  private String oeBezeichnung;

  private String kundeBezeichnung;

  private String sachbearbeiterFullName;

  private BigDecimal leistungen;

  private BigDecimal kosten;

  private Integer anzahlKorrekturbuchungen;

  private List<Mitarbeiter> mitarbeiter;

  private boolean bebucht;

  public Long getProjektabrechnungID() {
    return projektabrechnungID;
  }

  public void setProjektabrechnungID(final Long projektabrechnungID) {
    this.projektabrechnungID = projektabrechnungID;
  }

  public Integer getAnzahlKorrekturbuchungen() {
    return anzahlKorrekturbuchungen;
  }

  public void setAnzahlKorrekturbuchungen(Integer anzahlKorrekturbuchungen) {
    this.anzahlKorrekturbuchungen = anzahlKorrekturbuchungen;
  }

  public Abrechnungsmonat getAbrechnungsmonat() {
    return abrechnungsmonat;
  }

  public void setAbrechnungsmonat(final Abrechnungsmonat abrechnungsmonat) {
    this.abrechnungsmonat = abrechnungsmonat;
  }

  public String getAbrechnungsmonatAlsString() {
    return abrechnungsmonat.toString();
  }

  public String getProjektbezeichnung() {
    return projektbezeichnung;
  }

  public void setProjektbezeichnung(final String projektbezeichnung) {
    this.projektbezeichnung = projektbezeichnung;
  }

  public void setProjektIstAktiv(final boolean projektIstAktiv) {
    this.projektIstAktiv = projektIstAktiv;
  }

  public Boolean istAktiv() {
    return projektIstAktiv;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(final String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getProjekttyp() {
    return projekttyp;
  }

  public void setProjekttyp(final String projekttyp) {
    this.projekttyp = projekttyp;
  }

  public String getOeBezeichnung() {
    return oeBezeichnung;
  }

  public void setOeBezeichnung(final String oeBezeichnung) {
    this.oeBezeichnung = oeBezeichnung;
  }

  public String getKundeBezeichnung() {
    return kundeBezeichnung;
  }

  public void setKundeBezeichnung(final String kundeBezeichnung) {
    this.kundeBezeichnung = kundeBezeichnung;
  }

  public String getSachbearbeiterFullName() {
    return sachbearbeiterFullName;
  }

  public void setSachbearbeiterFullName(final String sachbearbeiterFullName) {
    this.sachbearbeiterFullName = sachbearbeiterFullName;
  }

  public BigDecimal getLeistungen() {
    return leistungen;
  }

  public void setLeistungen(final BigDecimal leistungen) {
    this.leistungen = leistungen;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(final BigDecimal kosten) {
    this.kosten = kosten;
  }

  public Integer getBearbeitungsstatus() {
    return bearbeitungsstatus;
  }

  public void setBearbeitungsstatus(final Integer bearbeitungsstatus) {
    this.bearbeitungsstatus = bearbeitungsstatus;
  }

  public List<Mitarbeiter> getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(final List<Mitarbeiter> mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public void setBebucht(final boolean bebucht) {
    this.bebucht = bebucht;
  }


  public Integer getAnzahlMitarbeiter() {
    return anzahlMitarbeiter == null ? 0 : anzahlMitarbeiter;
  }

  public void setAnzahlMitarbeiter(final Integer anzahlMitarbeiter) {
    this.anzahlMitarbeiter = anzahlMitarbeiter;
  }
}
