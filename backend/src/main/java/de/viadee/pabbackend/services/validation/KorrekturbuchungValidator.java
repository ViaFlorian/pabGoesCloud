package de.viadee.pabbackend.services.validation;

import de.viadee.pabbackend.entities.KostenartKonstante;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchungVorgang;
import de.viadee.pabbackend.enums.Kostenarten;
import de.viadee.pabbackend.enums.Projektnummern;
import de.viadee.pabbackend.enums.Projekttypen;
import de.viadee.pabbackend.exception.PabValidatorException;
import de.viadee.pabbackend.services.berechnung.MitarbeiterUrlaubKontoBerechnung;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.MonatsabschlussService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KorrekturbuchungValidator {


  private final BigDecimal INVALIDER_STELLENFAKTOR = new BigDecimal("0.00001");

  private final MitarbeiterService mitarbeiterService;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;
  private final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung;
  private final ProjektService projektService;
  private final KonstantenService konstantenService;
  private final MonatsabschlussService monatsabschlussService;

  public KorrekturbuchungValidator(MitarbeiterService mitarbeiterService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung,
      ProjektService projektService,
      KonstantenService konstantenService, MonatsabschlussService monatsabschlussService) {
    this.mitarbeiterService = mitarbeiterService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.mitarbeiterUrlaubKontoBerechnung = mitarbeiterUrlaubKontoBerechnung;
    this.projektService = projektService;
    this.konstantenService = konstantenService;
    this.monatsabschlussService = monatsabschlussService;
  }

  public void fuehrePruefungVonKorrekturbuchungVorgangDurch(
      ProjektabrechnungKorrekturbuchungVorgang korrekturbuchungsvorgang) {
    if (korrekturbuchungsvorgang.getKorrekturbuchung() == null) {
      throw new PabValidatorException("Keine Korrekturbuchung vorhanden.");
    }
    fuehrePruefungVonKorrekturBuchungDurch(korrekturbuchungsvorgang.getKorrekturbuchung());

    if (korrekturbuchungsvorgang.getGegenbuchung().getProjektId() != null) {
      fuehrePruefungVonKorrekturBuchungDurch(korrekturbuchungsvorgang.getGegenbuchung());

    }
  }

  public void fuehrePruefungVonKorrekturbuchungenDurch(
      List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchungen) {
    for (ProjektabrechnungKorrekturbuchung korrekturbuchung : projektabrechnungKorrekturbuchungen) {
      fuehrePruefungVonKorrekturBuchungDurch(korrekturbuchung);
    }
  }

  private void fuehrePruefungVonKorrekturBuchungDurch(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {

    /* -- Einzelfelder Prüfung --
     * Id positiver Long oder null.
     * Projekt existiert.
     * Abrechnungsmonat existiert und ist nicht abgeschlossen.
     * Kostenart aus enums (ohne Skonto).
     * Bemerkung existiert.
     * Wenn Mitarbeiter existiert dann aktiv, wenn Mitarbeiter intern dann Stellenfaktor
     */
    pruefeIdIstValide(korrekturbuchung);
    Projekt projekt = pruefeProjektExistiertUndGebeZurueck(korrekturbuchung);
    pruefeAbrechnungsmonatValide(korrekturbuchung);
    KostenartKonstante kostenartKonstante = pruefeKostenartErlaubt(korrekturbuchung);
    pruefeBemerkungExistiert(korrekturbuchung);
    pruefeMitarbeiterValide(korrekturbuchung);

    /* -- Abhängige Felder Prüfung --
     * Prüfe bei Urlaub, dass Mitarbeiter korrekt.
     * Prüfe bei Kostenart Rabatt, dass Projekttyp Festpreis, Wartung oder Produkt.
     * Prüfe Buchungsbeträge (abhängig required), außer das Projekt ist Rabatt.
     */
    pruefePotentielleUrlaubsbuchung(korrekturbuchung, projekt);
    pruefeProjektKorrektBeiKostenartRabatt(projekt, kostenartKonstante);
    pruefeBuchungsBetraege(korrekturbuchung, kostenartKonstante);

    /* -- Überprüfe, dass Felder leer sind --
     * Wenn Projekt intern keine Leistungsfelder befüllt.
     * Wenn Kostenart Rabatt nur Kostenartbetrag und Bemerkung befüllt.
     * Wenn Kostenart nicht Projektzeiten oder Reisezeiten keine Stundenfelder gesetzt.
     */
    pruefeBeiProjektIntern(korrekturbuchung, projekt);
    pruefeBeiKostenartRabatt(korrekturbuchung, kostenartKonstante);
    pruefeBeiKostenartZeiten(korrekturbuchung, kostenartKonstante);

    /* -- Überprüfe, dass errechnete werte stimmen --
     * Wenn Kostenart = Projektzeiten -> Stundenleistung = Stundenkosten.
     */
    pruefeFelderUebernommenBeiKostenartProjektzeit(korrekturbuchung, kostenartKonstante);

  }

  private void pruefeFelderUebernommenBeiKostenartProjektzeit(
      ProjektabrechnungKorrekturbuchung korrekturbuchung, KostenartKonstante kostenartKonstante) {
    if (!kostenartKonstante.getBezeichnung().equals(Kostenarten.PROJEKTZEITEN.toString())) {
      return;
    }

    if (!korrekturbuchung.getAnzahlStundenLeistung()
        .equals(korrekturbuchung.getAnzahlStundenKosten())) {
      throw new PabValidatorException(
          "Stunden Leistung muss Stunden Kosten entsprechen für Kostenart Projektzeiten.");
    }

  }

  private void pruefeBeiKostenartZeiten(
      ProjektabrechnungKorrekturbuchung korrekturbuchung, KostenartKonstante kostenartKonstante) {
    if (!kostenartKonstante.getBezeichnung().equals(Kostenarten.REISEZEITEN.toString())
        && !kostenartKonstante.getBezeichnung().equals(Kostenarten.PROJEKTZEITEN.toString())) {
      if (korrekturbuchung.getAnzahlStundenLeistung() != null
          || korrekturbuchung.getAnzahlStundenKosten() != null) {
        throw new PabValidatorException(
            "Stunden können nur für Kostenarten Reisezeiten und Projektzeiten eingetragen werden.");
      }
    }
  }

  private void pruefeBeiKostenartRabatt(
      ProjektabrechnungKorrekturbuchung korrekturbuchung, KostenartKonstante kostenart) {
    if (!kostenart.getBezeichnung().equals(Kostenarten.RABATTE.toString())) {
      return;
    }
    if (
        korrekturbuchung.getAnzahlStundenKosten() != null ||
            korrekturbuchung.getBetragKostensatz() != null ||
            korrekturbuchung.getAnzahlStundenLeistung() != null
    ) {
      throw new PabValidatorException(
          "Bei Kostenart Rabatt darf nur der Betrag/Stundensatz und die Bemerkung befüllt werden.");
    }
  }

  private void pruefeBeiProjektIntern(
      ProjektabrechnungKorrekturbuchung korrekturbuchung, Projekt projekt) {
    if (!projekt.getProjekttyp().equals(Projekttypen.INTERN.toString())) {
      return;
    }
    if (korrekturbuchung.getAnzahlStundenLeistung() != null
        || korrekturbuchung.getBetragStundensatz() != null) {
      throw new PabValidatorException(
          "Leistungsfelder dürfen für ein internes Projekt nicht befüllt werden.");
    }
  }

  private void pruefeBuchungsBetraege(
      ProjektabrechnungKorrekturbuchung korrekturbuchung, KostenartKonstante kostenartKonstante) {
    if (kostenartKonstante.getBezeichnung()
        .equals(Kostenarten.RABATTE.toString())
        && korrekturbuchung.getBetragStundensatz() == null) {
      throw new PabValidatorException(
          "BetragStundensatz darf für Kostenart Rabatte nicht leer sein.");
    }

    boolean stundenKostenLeer = korrekturbuchung.getAnzahlStundenKosten() == null;
    boolean betragKostenLeer = korrekturbuchung.getBetragKostensatz() == null;
    boolean stundenLeistungLeer = korrekturbuchung.getAnzahlStundenLeistung() == null;
    boolean betragLeistungLeer = korrekturbuchung.getBetragStundensatz() == null;

    if ((!stundenKostenLeer && betragKostenLeer) || (!stundenLeistungLeer && betragLeistungLeer)) {
      throw new PabValidatorException("Betrag darf nicht leer sein, wenn Stunden befüllt sind.");
    }
    if (betragKostenLeer && betragLeistungLeer) {
      throw new PabValidatorException("Stundensatz oder Kostensatz muss befüllt sein.");
    }
  }

  private void pruefeProjektKorrektBeiKostenartRabatt(Projekt projekt,
      KostenartKonstante kostenartKonstante) {
    if (!kostenartKonstante.getBezeichnung()
        .equals(Kostenarten.RABATTE.toString())) {
      return;
    }

    List<String> erlaubteProjektarten = List.of(
        Projekttypen.FESTPREIS.toString(),
        Projekttypen.WARTUNG.toString(),
        Projekttypen.PRODUKT.toString()
    );

    if (!erlaubteProjektarten.contains(projekt.getProjekttyp())) {
      throw new PabValidatorException(
          "Rabatte sind nur für Projekttypen Festpreis, Wartung oder Produkt erlaubt.");
    }

  }

  private void pruefeBemerkungExistiert(ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getBemerkung() == null || korrekturbuchung.getBemerkung().equals("")) {
      throw new PabValidatorException("Bemerkung ist verpflichtend.");
    }
  }

  private KostenartKonstante pruefeKostenartErlaubt(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {

    List<String> erlaubteKostenarten = List.of(
        Kostenarten.PROJEKTZEITEN.toString(),
        Kostenarten.REISEZEITEN.toString(),
        Kostenarten.REISEKOSTEN.toString(),
        Kostenarten.SONDERARBEITSZEITEN.toString(),
        Kostenarten.RUFBEREITSCHAFTEN.toString(),
        Kostenarten.SONSTIGE.toString(),
        Kostenarten.RABATTE.toString()
    );

    if (korrekturbuchung.getKostenartId() != null) {
      KostenartKonstante kostenart = konstantenService.kostenartByID(
          korrekturbuchung.getKostenartId());
      if (kostenart != null && erlaubteKostenarten.contains(kostenart.getBezeichnung())) {
        return kostenart;
      }
    }
    throw new PabValidatorException("Kostenart ungültig.");
  }

  private Projekt pruefeProjektExistiertUndGebeZurueck(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getProjektId() == null) {
      throw new PabValidatorException("Projekt muss angegeben werden.");
    }
    Projekt projekt = projektService.projektById(korrekturbuchung.getProjektId());
    if (projekt == null) {
      throw new PabValidatorException("Projekt muss angegeben werden.");
    }
    return projekt;
  }

  private void pruefeAbrechnungsmonatValide(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getJahr() == null || korrekturbuchung.getMonat() == null) {
      throw new PabValidatorException("Korrekturbuchung muss Abrechnungsmonat enthalten.");
    }

    if (monatsabschlussService.istAbgeschlossen(korrekturbuchung.getJahr(),
        korrekturbuchung.getMonat())) {
      throw new PabValidatorException("Abrechnungsmonat darf noch nicht abgeschlossen sein.");
    }
  }

  private void pruefeIdIstValide(ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getId() == null) {
      return;
    }
    if (korrekturbuchung.getId() <= 0) {
      throw new PabValidatorException("Korrekturbuchung darf keine negative id haben.");
    }
  }


  private void pruefeMitarbeiterValide(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getMitarbeiterId() == null) {
      return;
    }
    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        korrekturbuchung.getMitarbeiterId());

    //Werfe Fehler, wenn mitarbeiter nicht aktiv ist
    if (!mitarbeiter.getIstAktiv()) {
      throw new PabValidatorException(
          "Mitarbeiter " + mitarbeiter.getFullName() + " ist nicht aktiv.");
    }

    //Werfe Fehler, wenn für internen Mitarbeiter kein Stellenfaktor existiert
    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        korrekturbuchung.getMitarbeiterId(),
        korrekturbuchung.getJahr(),
        korrekturbuchung.getMonat());
    if (mitarbeiter.isIntern() && mitarbeiterStellenfaktor.getStellenfaktor()
        .equals(INVALIDER_STELLENFAKTOR)) {
      throw new PabValidatorException(
          "Für " + mitarbeiter.getFullName() + " ist im Abrechnungsmonat "
              + korrekturbuchung.getJahr() + "/"
              + korrekturbuchung.getMonat() + " kein Stellenfaktor administriert.");
    }

  }

  private void pruefePotentielleUrlaubsbuchung(ProjektabrechnungKorrekturbuchung korrekturbuchung,
      Projekt projekt) {

    //Wenn projekt nicht urlaub keine Prüfung nötig
    if (!projekt.getProjektnummer().equals(Projektnummern.URLAUB.toString())) {
      return;
    }

    if (korrekturbuchung.getMitarbeiterId() == null) {
      throw new PabValidatorException(
          "Nur interne Mitarbeiter können auf das Urlaubskonto buchen.");
    }

    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        korrekturbuchung.getMitarbeiterId());

    if (!mitarbeiter.isIntern()) {
      throw new PabValidatorException(mitarbeiter.getFullName()
          + " ist ein externer Mitarbeiter. Urlaub kann nur für interne Mitarbeiter gebucht werden.");
    }

    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        korrekturbuchung.getMitarbeiterId(),
        korrekturbuchung.getJahr(),
        korrekturbuchung.getMonat());

    if (korrekturbuchung.getAnzahlStundenKosten() != null
        && !mitarbeiterUrlaubKontoBerechnung.urlaubAlsHalberOderGanzerTagAngegeben(
        korrekturbuchung.getAnzahlStundenKosten(),
        mitarbeiterStellenfaktor.getStellenfaktor())) {
      throw new PabValidatorException(
          "Urlaub bei der Teilbuchung nicht als halber/ganzer Tag gemäß Stellenfaktor angegeben.");
    }

  }


}
