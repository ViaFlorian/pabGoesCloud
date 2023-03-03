package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.Belegarten.PKW;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.FIRMA;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.KEIN;
import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisAbrechnung;
import de.viadee.pabbackend.entities.AuswertungAbwesenheitFuerAbrechnung;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.entities.MitarbeiterStundenKonto;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.SonderarbeitszeitWochentagVerteilung;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStundenKontoService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.zeitrechnung.AbwesenheitenBerechner;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ArbeitsnachweisAbrechnungBerechnungService {

  private final MitarbeiterService mitarbeiterService;
  private final AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung;
  private final Zeitrechnung zeitrechnung;
  private final AbwesenheitenBerechner abwesenheitenBerechner;
  private final ProjektService projektService;
  private final KonstantenService konstantenService;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;
  private final MitarbeiterStundenKontoService mitarbeiterStundenKontoService;
  private final ParameterService parameterService;

  public ArbeitsnachweisAbrechnungBerechnungService(MitarbeiterService mitarbeiterService,
      AngerechneteReisezeitBerechnung angerechneteReisezeitBerechnung, Zeitrechnung zeitrechnung,
      AbwesenheitenBerechner abwesenheitenBerechner, ProjektService projektService,
      KonstantenService konstantenService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      MitarbeiterStundenKontoService mitarbeiterStundenKontoService,
      ParameterService parameterService) {
    this.mitarbeiterService = mitarbeiterService;
    this.angerechneteReisezeitBerechnung = angerechneteReisezeitBerechnung;
    this.zeitrechnung = zeitrechnung;
    this.abwesenheitenBerechner = abwesenheitenBerechner;
    this.projektService = projektService;
    this.konstantenService = konstantenService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.mitarbeiterStundenKontoService = mitarbeiterStundenKontoService;
    this.parameterService = parameterService;
  }


  public ArbeitsnachweisAbrechnung erstelleArbeitsnachweisAbrechnung(
      Arbeitsnachweis arbeitsnachweis,
      List<Projektstunde> projektstundenNormal,
      List<Projektstunde> reisezeit, List<Projektstunde> sonderarbeitszeiten,
      List<Projektstunde> rufbereitschaften, List<Abwesenheit> abwesenheiten,
      List<Beleg> belege) {
    ArbeitsnachweisAbrechnung arbeitsnachweisAbrechnung = new ArbeitsnachweisAbrechnung();

    if (istMitarbeiterExtern(arbeitsnachweis)) {
      return null;
    }

    arbeitsnachweisAbrechnung.setAuszahlung(
        arbeitsnachweis.getAuszahlung() == null ? new BigDecimal("0.00")
            : arbeitsnachweis.getAuszahlung());
    arbeitsnachweisAbrechnung.setSollstunden(
        arbeitsnachweis.getSollstunden() == null ? new BigDecimal("0.00")
            : arbeitsnachweis.getSollstunden());

    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat());
    if (mitarbeiterStellenfaktor.getStellenfaktor().equals(new BigDecimal("0.00001"))) {
      Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
          arbeitsnachweis.getMitarbeiterId());
      arbeitsnachweisAbrechnung.setWarnung(
          "Sollstunden konnten nicht berechnet werden, für den Abrechnungsmonat "
              + arbeitsnachweis.getJahr() + "/" + arbeitsnachweis.getMonat() + " ist für "
              + mitarbeiter.getFullName()
              + " kein Stellenfaktor administriert!");

      arbeitsnachweisAbrechnung.setBerechneteSollstunden(ZERO);
      arbeitsnachweisAbrechnung.setAktuellerStellenfaktor(ZERO);
    } else {
      arbeitsnachweisAbrechnung.setAktuellerStellenfaktor(
          mitarbeiterStellenfaktor.getStellenfaktor());
      arbeitsnachweisAbrechnung.setBerechneteSollstunden(
          zeitrechnung.berechneSollstunden(arbeitsnachweis.getJahr(),
              arbeitsnachweis.getMonat(), mitarbeiterStellenfaktor.getStellenfaktor()));
    }

    BigDecimal summeProjektstunden = projektstundenNormal.stream()
        .map(Projektstunde::getAnzahlStunden).reduce(ZERO, BigDecimal::add);
    BigDecimal summeAngReisezeit = angerechneteReisezeitBerechnung.berechneAngerechneteReisezeit(
        reisezeit).stream().map(Projektstunde::getAnzahlStunden).reduce(ZERO,
        BigDecimal::add);
    arbeitsnachweisAbrechnung.setSummeIstStunden(
        summeProjektstunden.add(summeAngReisezeit).setScale(2, RoundingMode.HALF_UP));

    arbeitsnachweisAbrechnung.setSonderarbeitszeit(
        sonderarbeitszeiten.stream().map(Projektstunde::getAnzahlStunden)
            .reduce(ZERO, BigDecimal::add));

    verteileSonderarbeitszeitenAufWerktagSamstagSonntagFeiertagAufEingabeFelder(sonderarbeitszeiten,
        arbeitsnachweisAbrechnung);

    verteileBelegeAufSummeVerbingunsentgeltJobticket(belege, arbeitsnachweisAbrechnung,
        arbeitsnachweis);

    werteAbwesenheitenAusUndVerteileAuswertungAufAbwesenheitEingabeFelder(abwesenheiten,
        arbeitsnachweisAbrechnung);

    arbeitsnachweisAbrechnung.setRufbereitschaft(
        rufbereitschaften.stream().map(Projektstunde::getAnzahlStunden)
            .reduce(ZERO, BigDecimal::add));

    arbeitsnachweisAbrechnung.setVortrag(getUebertragFuerAbrechnungsmonat(arbeitsnachweis));

    arbeitsnachweisAbrechnung.setSmartphone(
        arbeitsnachweis.getSmartphoneEigen() == null ? KEIN.toString()
            : arbeitsnachweis.getSmartphoneEigen() ? EIGEN.toString() : FIRMA.toString());

    arbeitsnachweisAbrechnung.setFirmenwagen(
        arbeitsnachweis.getFirmenwagen() == null || arbeitsnachweis.getFirmenwagen()
            .equals(Boolean.FALSE) ? "nein" : "ja");

    arbeitsnachweisAbrechnung.setZuschlagSmartphone(
        getBereitstellungEigenesSmartphone(arbeitsnachweis));

    return arbeitsnachweisAbrechnung;
  }

  private boolean istMitarbeiterExtern(Arbeitsnachweis arbeitsnachweis) {
    return !mitarbeiterService.mitarbeiterById(arbeitsnachweis.getMitarbeiterId()).isIntern();
  }

  private void verteileSonderarbeitszeitenAufWerktagSamstagSonntagFeiertagAufEingabeFelder(
      List<Projektstunde> sonderarbeitszeiten,
      ArbeitsnachweisAbrechnung arbeitsnachweisAbrechnung) {

    SonderarbeitszeitWochentagVerteilung verteilung = zeitrechnung.getSonderarbeitszeitWochentagVerteilung(
        sonderarbeitszeiten);

    arbeitsnachweisAbrechnung.setDavonWerktag(verteilung.getWerktag());
    arbeitsnachweisAbrechnung.setDavonSamstag(verteilung.getSamstag());
    arbeitsnachweisAbrechnung.setDavonSonntagFeiertag(verteilung.getSonntagFeiertag());

  }

  private void verteileBelegeAufSummeVerbingunsentgeltJobticket(List<Beleg> relevanteBelege,
      ArbeitsnachweisAbrechnung arbeitsnachweisAbrechnung, final Arbeitsnachweis arbeitsnachweis) {
    BigDecimal summeBeleg = ZERO;
    BigDecimal jobticket = ZERO;
    BigDecimal verbindungentgelt = ZERO;
    BigDecimal kilometerpauschaleFirmenwagen = ZERO;

    final BelegartKonstante pkw = konstantenService.belegartByTextKurz(PKW.toString());

    for (Beleg beleg : relevanteBelege) {
      Projekt projekt = projektService.projektById(beleg.getProjektId());
      if (projekt.getProjektnummer().equalsIgnoreCase("99999")) {
        verbindungentgelt = verbindungentgelt.add(beleg.getBetrag());
      } else if (projekt.getProjektnummer().equalsIgnoreCase("99998")) {
        jobticket = jobticket.add(beleg.getBetrag());
      } else {
        BelegartKonstante belegart = konstantenService.belegartByID(beleg.getBelegartId());
        if (belegart.equals(pkw) && (
            arbeitsnachweis.getFirmenwagen() != null && arbeitsnachweis.getFirmenwagen())) {
          kilometerpauschaleFirmenwagen = kilometerpauschaleFirmenwagen.add(beleg.getBetrag());
        } else {
          summeBeleg = summeBeleg.add(beleg.getBetrag());
        }
      }
    }

    arbeitsnachweisAbrechnung.setKilometerpauschaleFirmenwagen(kilometerpauschaleFirmenwagen);
    arbeitsnachweisAbrechnung.setSummeBelege(summeBeleg);
    arbeitsnachweisAbrechnung.setVerbindungsentgelt(verbindungentgelt);
    arbeitsnachweisAbrechnung.setJobticket(jobticket);

  }

  private void werteAbwesenheitenAusUndVerteileAuswertungAufAbwesenheitEingabeFelder(
      List<Abwesenheit> abwesenheiten, ArbeitsnachweisAbrechnung arbeitsnachweisAbrechnung) {
    //TODO @STG Sehe keinen Grund warum nicht direkt auf ArbeitsnachweisAbrechnung gearbeitet werden kann?
    AuswertungAbwesenheitFuerAbrechnung auswertung = werteAbwesenheitenAus(abwesenheiten);
    if (auswertung != null) {
      arbeitsnachweisAbrechnung.setUeberAcht(auswertung.getUeberAcht());
      arbeitsnachweisAbrechnung.setAnAb(auswertung.getAnAb());
      arbeitsnachweisAbrechnung.setSpesenGanztaegig(auswertung.getSpesenGanztaegig());
      arbeitsnachweisAbrechnung.setFruehstueck(auswertung.getFruehstueck());
      arbeitsnachweisAbrechnung.setMittagessen(auswertung.getMittagessen());
      arbeitsnachweisAbrechnung.setAbendessen(auswertung.getAbendessen());
      arbeitsnachweisAbrechnung.setZwischenSechsUndZehn(auswertung.getZwischenSechsUndZehn());
      arbeitsnachweisAbrechnung.setUeberZehn(auswertung.getUeberZehn());
      arbeitsnachweisAbrechnung.setZuschlaegeGanztaegig(auswertung.getZuschlaegeGanztaegig());
    }
  }

  private AuswertungAbwesenheitFuerAbrechnung werteAbwesenheitenAus(
      List<Abwesenheit> abwesenheiten) {
    AuswertungAbwesenheitFuerAbrechnung auswertung = new AuswertungAbwesenheitFuerAbrechnung();

    Integer ueberAcht = 0;
    Integer anAb = 0;
    Integer spesenGanztaegig = 0;
    Integer fruehstueck = 0;
    Integer mittagessen = 0;
    Integer abendessen = 0;
    Integer zwischenSechsUndZehn = 0;
    Integer ueberZehn = 0;
    Integer zuschlaegeGanztaegig = 0;

    for (Abwesenheit abwesenheit : abwesenheiten) {
      if (abwesenheit.isMitFruehstueck()) {
        fruehstueck++;
      }
      if (abwesenheit.isMitMittagessen()) {
        mittagessen++;
      }
      if (abwesenheit.isMitAbendessen()) {
        abendessen++;
      }
      if (abwesenheit.isMitUebernachtung() ||
          zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
              abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("24")) == 0) {
        zuschlaegeGanztaegig++;
      }

      if (!abwesenheit.isMitUebernachtung() &&
          zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
              abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("6.0")) >= 0 &&
          zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
              abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("10.0")) <= 0) {
        zwischenSechsUndZehn++;
      }
      if (!abwesenheit.isMitUebernachtung() &&
          zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
              abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("10.0")) == 1 &&
          zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
              abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("24.0")) == -1) {
        ueberZehn++;
      }

      if (abwesenheitenBerechner.istAnreisetag(abwesenheiten, abwesenheit)
          || abwesenheitenBerechner.istAbreisetag(abwesenheiten, abwesenheit)) {
        anAb++;
      } else {

        if (zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
            abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("24")) == 0) {
          spesenGanztaegig++;
        }
        if (zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
            abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("8.0")) == 1 &&
            zeitrechnung.berechneStundendifferenz(abwesenheit.getUhrzeitVon(),
                abwesenheit.getUhrzeitBis()).compareTo(new BigDecimal("24.0")) == -1) {
          ueberAcht++;
        }

      }
    }

    auswertung.setZwischenSechsUndZehn(zwischenSechsUndZehn);
    auswertung.setZuschlaegeGanztaegig(zuschlaegeGanztaegig);
    auswertung.setUeberZehn(ueberZehn);
    auswertung.setUeberAcht(ueberAcht);
    auswertung.setSpesenGanztaegig(spesenGanztaegig);
    auswertung.setFruehstueck(fruehstueck);
    auswertung.setMittagessen(mittagessen);
    auswertung.setAbendessen(abendessen);
    auswertung.setAnAb(anAb);

    return auswertung;
  }

  private BigDecimal getUebertragFuerAbrechnungsmonat(Arbeitsnachweis arbeitsnachweis) {
    LocalDate vormonat = LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(), 1)
        .minusMonths(1);
    MitarbeiterStundenKonto aktuellsterKontosatzdesAbrechnungsmonatVormonat = mitarbeiterStundenKontoService.ladeAktuellstenStundenSaldoFuerMitarbeiterAbrechnungsmonat(
        arbeitsnachweis.getMitarbeiterId(), vormonat.getYear(), vormonat.getMonthValue());

    return aktuellsterKontosatzdesAbrechnungsmonatVormonat == null ? ZERO
        : aktuellsterKontosatzdesAbrechnungsmonatVormonat.getLfdSaldo();
  }

  private BigDecimal getBereitstellungEigenesSmartphone(final Arbeitsnachweis arbeitsnachweis) {
    return arbeitsnachweis.getSmartphoneEigen() != null && arbeitsnachweis.getSmartphoneEigen()
        .equals(EIGEN.value()) ? new BigDecimal(
        parameterService.valueByKey("bereitstellungEigenesSmartphone")) : ZERO;
  }
}
