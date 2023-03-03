package de.viadee.pabbackend.entities;


import de.viadee.pabbackend.enums.ProjektBuchungstyp;
import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;

public class ErgebnisB007Pdf {

  @Column("MitarbeiterName")
  private String mitarbeiterName;
  @Column("MitarbeiterID")
  private Long mitarbeiterID;
  @Column("PersonalNr")
  private Long personalNummer;
  @Column("Kostenart")
  private String kostenart;
  @Column("KostenStunden")
  private BigDecimal kostenStunden;
  @Column("Kostensatz")
  private BigDecimal kostensatz;
  @Column("Kosten")
  private BigDecimal kosten;
  @Column("LeistungStunden")
  private BigDecimal leistungStunden;
  @Column("Stundensatz")
  private BigDecimal stundensatz;
  @Column("BerechneteLeistung")
  private BigDecimal berechneteLeistung;
  @Column("FaktLeistung")
  private BigDecimal faktLeistung;
  @Column("Jahr")
  private Integer jahr;
  @Column("Monat")
  private Integer monat;
  @Column("ReferenzJahr")
  private Integer referenzJahr;
  @Column("ReferenzMonat")
  private Integer referenzMonat;
  @Column("Kurzbezeichnung")
  private String kunde;
  @Column("Projektnummer")
  private String projektnummer;
  @Column("Bezeichnung")
  private String projektBezeichnung;
  @Column("Buchungstyp")
  private String projektBuchungstyp;
  @Column("Projekttyp")
  private String projekttyp;
  @Column("Fertigstellungsgrad")
  private BigDecimal fertigstellungsgrad;
  @Column("Vorlaeufig")
  private Integer vorlaeufig;
  private String kostenartDetails;
  private String einzelmonat;
  private Integer anzahlBuchungstypenInProjektAbrechnungsmonatGruppe;
  private Integer anzahlMonateInProjektGruppe;

  public Integer getAnzahlMonateInProjektGruppe() {
    return anzahlMonateInProjektGruppe;
  }

  public void setAnzahlMonateInProjektGruppe(Integer anzahlMonateInProjektGruppe) {
    this.anzahlMonateInProjektGruppe = anzahlMonateInProjektGruppe;
  }

  public Integer getAnzahlBuchungstypenInProjektAbrechnungsmonatGruppe() {
    return anzahlBuchungstypenInProjektAbrechnungsmonatGruppe;
  }

  public void setAnzahlBuchungstypenInProjektAbrechnungsmonatGruppe(
      Integer anzahlBuchungstypenInProjektAbrechnungsmonatGruppe) {
    this.anzahlBuchungstypenInProjektAbrechnungsmonatGruppe = anzahlBuchungstypenInProjektAbrechnungsmonatGruppe;
  }

  public String getEinzelmonat() {
    return einzelmonat;
  }

  public void setEinzelmonat(String einzelmonat) {
    this.einzelmonat = einzelmonat;
  }

  public String getKostenartDetails() {
    return kostenartDetails;
  }

  public void setKostenartDetails(String kostenartDetails) {
    this.kostenartDetails = kostenartDetails;
  }

  public String getMitarbeiterName() {
    return mitarbeiterName;
  }

  public void setMitarbeiterName(String mitarbeiterName) {
    this.mitarbeiterName = mitarbeiterName;
  }

  public Long getMitarbeiterID() {
    return mitarbeiterID;
  }

  public void setMitarbeiterID(Long mitarbeiterID) {
    this.mitarbeiterID = mitarbeiterID;
  }

  public Long getPersonalNummer() {
    return personalNummer;
  }

  public void setPersonalNummer(Long personalNummer) {
    this.personalNummer = personalNummer;
  }

  public String getKostenart() {
    return kostenart;
  }

  public void setKostenart(String kostenart) {
    this.kostenart = kostenart;
  }

  public BigDecimal getKostenStunden() {
    return kostenStunden;
  }

  public void setKostenStunden(BigDecimal kostenStunden) {
    this.kostenStunden = kostenStunden;
  }

  public BigDecimal getKostensatz() {
    return kostensatz;
  }

  public void setKostensatz(BigDecimal kostensatz) {
    this.kostensatz = kostensatz;
  }

  public BigDecimal getKosten() {
    return kosten;
  }

  public void setKosten(BigDecimal kosten) {
    this.kosten = kosten;
  }

  public BigDecimal getLeistungStunden() {
    return leistungStunden;
  }

  public void setLeistungStunden(BigDecimal leistungStunden) {
    this.leistungStunden = leistungStunden;
  }

  public BigDecimal getStundensatz() {
    return stundensatz;
  }

  public void setStundensatz(BigDecimal stundensatz) {
    this.stundensatz = stundensatz;
  }

  public BigDecimal getBerechneteLeistung() {
    return berechneteLeistung;
  }

  public void setBerechneteLeistung(BigDecimal berechneteLeistung) {
    this.berechneteLeistung = berechneteLeistung;
  }

  public BigDecimal getFaktLeistung() {
    return faktLeistung;
  }

  public void setFaktLeistung(BigDecimal faktLeistung) {
    this.faktLeistung = faktLeistung;
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

  public Integer getReferenzJahr() {
    return referenzJahr;
  }

  public void setReferenzJahr(Integer referenzJahr) {
    this.referenzJahr = referenzJahr;
  }

  public Integer getReferenzMonat() {
    return referenzMonat;
  }

  public void setReferenzMonat(Integer referenzMonat) {
    this.referenzMonat = referenzMonat;
  }

  public String getKunde() {
    return kunde;
  }

  public void setKunde(String kunde) {
    this.kunde = kunde;
  }

  public String getProjektnummer() {
    return projektnummer;
  }

  public void setProjektnummer(String projektnummer) {
    this.projektnummer = projektnummer;
  }

  public String getProjektBezeichnung() {
    return projektBezeichnung;
  }

  public void setProjektBezeichnung(String projektBezeichnung) {
    this.projektBezeichnung = projektBezeichnung;
  }

  public String getProjektBuchungstyp() {
    return projektBuchungstyp;
  }

  public void setProjektBuchungstyp(String projektBuchungstyp) {
    this.projektBuchungstyp = projektBuchungstyp;
  }

  public String getProjekttyp() {
    return projekttyp;
  }

  public void setProjekttyp(String projekttyp) {
    this.projekttyp = projekttyp;
  }

  public BigDecimal getFertigstellungsgrad() {
    return fertigstellungsgrad;
  }

  public void setFertigstellungsgrad(BigDecimal fertigstellungsgrad) {
    this.fertigstellungsgrad = fertigstellungsgrad;
  }

  public Boolean getVorlaeufig() {
    return this.vorlaeufig != 0;
  }

  public void setVorlaeufig(Integer vorlaeufig) {
    this.vorlaeufig = vorlaeufig;
  }

  public String getProjektBuchungstypForPdf() {
    return projektBuchungstyp.equals(ProjektBuchungstyp.KORREKTUR) ? "Korrekturen" : "Buchungen";
  }

  public String getAbrechnungsmonat() {
    String datum;
    if (jahr == null || monat == null) {
      datum = "keine Daten";
    } else {
      datum = jahr.toString().concat("/").concat(String.format("%02d", monat));
    }
    return datum;
  }

  public String getReferenzsMonatString() {
    String datum;
    if (referenzJahr == null || referenzMonat == null) {
      datum = "keine Daten";
    } else {
      datum = referenzJahr.toString().concat("/").concat(String.format("%02d", referenzMonat));
    }
    return datum;
  }

  public String getFertigstellungsgradForPdf() {
    String fertigungsgradString = "";
    if (fertigstellungsgrad != null) {
      fertigungsgradString = " (Fertigstellungsgrad: " + fertigstellungsgrad + "%)";
      fertigungsgradString = fertigungsgradString.replace(".", ",");
    }
    return fertigungsgradString;
  }
}
