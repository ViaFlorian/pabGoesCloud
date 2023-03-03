package de.viadee.pabbackend.services.berechnung;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.MitarbeiterStundenKonto;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.entities.SichererSaldoStand;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStundenKontoService;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterStundenKontoBerechnung {

  private final MitarbeiterStundenKontoService mitarbeiterStundenKontoService;

  private final KonstantenService konstantenService;

  private final Zeitrechnung zeitrechnung;

  public MitarbeiterStundenKontoBerechnung(
      MitarbeiterStundenKontoService mitarbeiterStundenKontoService,
      KonstantenService konstantenService, Zeitrechnung zeitrechnung) {
    this.mitarbeiterStundenKontoService = mitarbeiterStundenKontoService;
    this.konstantenService = konstantenService;
    this.zeitrechnung = zeitrechnung;
  }

  public void berechneSalden(List<MitarbeiterStundenKonto> mitarbeiterStundenKontosaetze) {
    LocalDate vormonat = zeitrechnung.getVormonat();
    Collections.sort(mitarbeiterStundenKontosaetze);
    SichererSaldoStand sichererSaldo = findeLetztenSicherenMitarbeiterKontoSaldo(vormonat,
        mitarbeiterStundenKontosaetze);

    BigDecimal lfdSaldo = sichererSaldo.getSaldo();
    for (MitarbeiterStundenKonto kontosatz : mitarbeiterStundenKontosaetze) {
      if (kontosatz.getWertstellung().isAfter(sichererSaldo.getWertstellung())
          && kontosatz.getAnzahlStunden() != null) {
        lfdSaldo = lfdSaldo.add(kontosatz.getAnzahlStunden());
        kontosatz.setLfdSaldo(lfdSaldo);
        kontosatz.setGeaendert(true);
      }
    }
    Collections.sort(mitarbeiterStundenKontosaetze);
  }


  public SichererSaldoStand findeLetztenSicherenMitarbeiterKontoSaldo(LocalDate vormonat,
      final List<MitarbeiterStundenKonto> mitarbeiterStundenKontoSaetze) {
    SichererSaldoStand sicheresSaldo = new SichererSaldoStand();

    Collections.sort(mitarbeiterStundenKontoSaetze);
    Collections.reverse(mitarbeiterStundenKontoSaetze);

    for (MitarbeiterStundenKonto kontosatz : mitarbeiterStundenKontoSaetze) {
      if (kontosatz.getWertstellung().isBefore(vormonat) && kontosatz.isEndgueltig()) {
        sicheresSaldo.setWertstellung(kontosatz.getWertstellung());
        sicheresSaldo.setSaldo(kontosatz.getLfdSaldo());
      }
    }

    if (sicheresSaldo.getSaldo() == null) {
      sicheresSaldo.setSaldo(BigDecimal.ZERO);
      sicheresSaldo.setWertstellung(LocalDate.of(1900, 1, 1));
    }

    return sicheresSaldo;
  }

  public void bucheKorrekturbuchung(ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    MitarbeiterStundenKonto neuerKontoSatz = mitarbeiterStundenKontoService.erstelleNeuenStundenKontoSatzAusKorrekturbuchung(
        korrekturbuchung);

    //Wenn kein KontoSatz erzeugt wurde, dann weil keine Buchung auf Stundenkonto durchgeführt werden soll
    if (neuerKontoSatz == null) {
      return;
    }

    speichereNeueStundenKontoSaetze(
        Collections.singletonList(neuerKontoSatz), korrekturbuchung.getMitarbeiterId());


  }

  public void speichereNeueStundenKontoSaetze(List<MitarbeiterStundenKonto> neueStundenKontoSaetze,
      Long mitarbeiterId) {

    neueStundenKontoSaetze.forEach(
        neuerStundenKontoSatz -> neuerStundenKontoSatz.setEndgueltig(true));

    List<MitarbeiterStundenKonto> existierendeStundenKontoSaetze = mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(
        mitarbeiterId);
    List<MitarbeiterStundenKonto> alleStundenKontoSaetze = new ArrayList<>(
        existierendeStundenKontoSaetze);
    alleStundenKontoSaetze.addAll(neueStundenKontoSaetze);

    berechneSalden(alleStundenKontoSaetze);

    List<MitarbeiterStundenKonto> geanderteStundenKontoSaetze = alleStundenKontoSaetze.stream()
        .filter(MitarbeiterStundenKonto::isGeaendert).toList();

    mitarbeiterStundenKontoService.speichereMitarbeiterStundenkontosaetze(
        geanderteStundenKontoSaetze);
  }

  public void ermittleUndSpeichereMitarbeiterStundenKontoAenderungen(
      Arbeitsnachweis arbeitsnachweis, final BigDecimal sonderarbeitszeit,
      final BigDecimal projektstunden) {

    // Löschen alter Sätze
    List<MitarbeiterStundenKonto> mitarbeiterStundenKontosaetze = mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(
        arbeitsnachweis.getMitarbeiterId());
    List<MitarbeiterStundenKonto> zuLoeschendeKontosaetze = new ArrayList<>();
    List<MitarbeiterStundenKonto> verbleibendeKontosaetze = new ArrayList<>();
    List<MitarbeiterStundenKonto> neueKontoSaetze = new ArrayList<>();

    for (MitarbeiterStundenKonto kontosatz : mitarbeiterStundenKontosaetze) {
      if (!kontosatz.isEndgueltig() && kontosatz.isAutomatisch()
          && kontosatz.getWertstellung().getMonthValue() == arbeitsnachweis.getMonat()
          && kontosatz.getWertstellung().getYear() == arbeitsnachweis.getJahr()) {
        zuLoeschendeKontosaetze.add(kontosatz);
      } else {
        verbleibendeKontosaetze.add(kontosatz);
      }
    }

    if (!zuLoeschendeKontosaetze.isEmpty()) {
      mitarbeiterStundenKontoService.loescheMitarbeiterStundenKontosaetze(zuLoeschendeKontosaetze);
    }

    // Erstellung neuer Sätze
    LocalDate ersterDesMonats = LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
        1);
    LocalDate letzterDesMonats = LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
        1).plusMonths(1).minusDays(1);

    MitarbeiterStundenKonto sollstundenKontosatz = new MitarbeiterStundenKonto();
    sollstundenKontosatz.setEndgueltig(false);
    sollstundenKontosatz.setAutomatisch(true);
    sollstundenKontosatz.setBemerkung("Arbeitsnachweis " + arbeitsnachweis.getDatum());
    sollstundenKontosatz.setBuchungstypStundenId(
        konstantenService.buchungstypStundenByBezeichnung("Sollstunden").getId());
    sollstundenKontosatz.setBuchungsdatum(LocalDateTime.now());
    sollstundenKontosatz.setWertstellung(ersterDesMonats);
    sollstundenKontosatz.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    sollstundenKontosatz.setAnzahlStunden(arbeitsnachweis.getSollstunden().negate());

    neueKontoSaetze.add(sollstundenKontosatz);

    MitarbeiterStundenKonto auszahlungKontosatz = new MitarbeiterStundenKonto();
    auszahlungKontosatz.setEndgueltig(false);
    auszahlungKontosatz.setAutomatisch(true);
    auszahlungKontosatz.setBemerkung("Arbeitsnachweis " + arbeitsnachweis.getDatum());
    auszahlungKontosatz.setBuchungstypStundenId(
        konstantenService.buchungstypStundenByBezeichnung("Auszahlung").getId());
    auszahlungKontosatz.setBuchungsdatum(LocalDateTime.now().plusSeconds(1));
    auszahlungKontosatz.setWertstellung(letzterDesMonats);
    auszahlungKontosatz.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    auszahlungKontosatz.setAnzahlStunden(arbeitsnachweis.getAuszahlung().negate());

    if (auszahlungKontosatz.getAnzahlStunden().compareTo(BigDecimal.ZERO) != 0) {
      neueKontoSaetze.add(auszahlungKontosatz);
    }

    MitarbeiterStundenKonto auszahlungSonderarbeitszeitKontosatz = new MitarbeiterStundenKonto();
    auszahlungSonderarbeitszeitKontosatz.setEndgueltig(false);
    auszahlungSonderarbeitszeitKontosatz.setAutomatisch(true);
    auszahlungSonderarbeitszeitKontosatz.setBemerkung(
        "Arbeitsnachweis " + arbeitsnachweis.getDatum());
    auszahlungSonderarbeitszeitKontosatz.setBuchungstypStundenId(
        konstantenService.buchungstypStundenByBezeichnung("Auszahlung Sonderarbeitszeit").getId());
    auszahlungSonderarbeitszeitKontosatz.setBuchungsdatum(LocalDateTime.now().plusSeconds(2));
    auszahlungSonderarbeitszeitKontosatz.setWertstellung(letzterDesMonats);
    auszahlungSonderarbeitszeitKontosatz.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    auszahlungSonderarbeitszeitKontosatz.setAnzahlStunden(sonderarbeitszeit);

    if (auszahlungSonderarbeitszeitKontosatz.getAnzahlStunden().compareTo(BigDecimal.ZERO) != 0) {
      neueKontoSaetze.add(auszahlungSonderarbeitszeitKontosatz);
    }

    MitarbeiterStundenKonto projektstundenKontosatz = new MitarbeiterStundenKonto();
    projektstundenKontosatz.setEndgueltig(false);
    projektstundenKontosatz.setAutomatisch(true);
    projektstundenKontosatz.setBemerkung("Arbeitsnachweis " + arbeitsnachweis.getDatum());
    projektstundenKontosatz.setBuchungstypStundenId(
        konstantenService.buchungstypStundenByBezeichnung("Ist-Stunden").getId());
    projektstundenKontosatz.setBuchungsdatum(LocalDateTime.now().plusSeconds(3));
    projektstundenKontosatz.setWertstellung(letzterDesMonats);
    projektstundenKontosatz.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    projektstundenKontosatz.setAnzahlStunden(projektstunden);

    neueKontoSaetze.add(projektstundenKontosatz);

    List<MitarbeiterStundenKonto> neueUndVerbleibendeKontosatze = new ArrayList<>();
    neueUndVerbleibendeKontosatze.addAll(neueKontoSaetze);
    neueUndVerbleibendeKontosatze.addAll(verbleibendeKontosaetze);

    Collections.sort(neueUndVerbleibendeKontosatze);

    berechneSalden(neueUndVerbleibendeKontosatze);

    mitarbeiterStundenKontoService.speichereMitarbeiterStundenkontosaetze(neueKontoSaetze);

    mitarbeiterStundenKontoService.speichereMitarbeiterStundenkontosaetze(
        verbleibendeKontosaetze.stream().filter(kontosatz -> {
          return kontosatz.isGeaendert();
        }).toList());

  }

  public void loescheMitarbeiterStundenKontosaetzeZuArbeitsnachweis(
      final Arbeitsnachweis arbeitsnachweis) {
    List<MitarbeiterStundenKonto> mitarbeiterStundenKonto = mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(
        arbeitsnachweis.getMitarbeiterId());
    List<MitarbeiterStundenKonto> zuLoeschendeKontosaetze = mitarbeiterStundenKonto.stream()
        .filter(kontosatz -> {
          return kontosatz.getWertstellung().getYear() == arbeitsnachweis.getJahr() &&
              kontosatz.getWertstellung().getMonthValue() == arbeitsnachweis.getMonat() &&
              kontosatz.isAutomatisch() &&
              !kontosatz.isEndgueltig();
        }).toList();
    mitarbeiterStundenKontoService.loescheMitarbeiterStundenKontosaetze(zuLoeschendeKontosaetze);

    mitarbeiterStundenKonto = mitarbeiterStundenKontoService.mitarbeiterStundenKontoByMitarbeiterId(
        arbeitsnachweis.getMitarbeiterId());
    berechneSalden(mitarbeiterStundenKonto);
    mitarbeiterStundenKontoService.speichereMitarbeiterStundenkontosaetze(
        mitarbeiterStundenKonto.stream().filter(kontosatz -> {
          return kontosatz.isGeaendert();
        }).toList());

  }

}
