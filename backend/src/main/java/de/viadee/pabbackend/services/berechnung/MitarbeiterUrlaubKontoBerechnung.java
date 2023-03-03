package de.viadee.pabbackend.services.berechnung;

import static java.math.BigDecimal.ZERO;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.entities.MitarbeiterUrlaubKonto;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.entities.SichererSaldoStand;
import de.viadee.pabbackend.enums.UrlaubBuchungstyp;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterUrlaubKontoService;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MitarbeiterUrlaubKontoBerechnung {

  private final MitarbeiterUrlaubKontoService mitarbeiterUrlaubKontoService;

  private final KonstantenService konstantenService;

  private final Zeitrechnung zeitrechnung;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;

  public MitarbeiterUrlaubKontoBerechnung(
      MitarbeiterUrlaubKontoService mitarbeiterUrlaubKontoService,
      KonstantenService konstantenService, Zeitrechnung zeitrechnung,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService) {
    this.mitarbeiterUrlaubKontoService = mitarbeiterUrlaubKontoService;
    this.konstantenService = konstantenService;
    this.zeitrechnung = zeitrechnung;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
  }

  public void berechneSalden(List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontosaetze) {
    LocalDate vormonat = zeitrechnung.getVormonat();
    Collections.sort(mitarbeiterUrlaubKontosaetze);
    SichererSaldoStand sichererSaldo = findeLetztenSicherenMitarbeiterKontoSaldo(vormonat,
        mitarbeiterUrlaubKontosaetze);

    BigDecimal lfdSaldo = sichererSaldo.getSaldo();
    for (MitarbeiterUrlaubKonto kontosatz : mitarbeiterUrlaubKontosaetze) {
      if (kontosatz.getWertstellung().isAfter(sichererSaldo.getWertstellung())) {
        lfdSaldo = lfdSaldo.add(kontosatz.getAnzahlTage());
        kontosatz.setLfdSaldo(lfdSaldo);
        kontosatz.setGeaendert(true);
      }
    }
    Collections.sort(mitarbeiterUrlaubKontosaetze);
  }

  public boolean urlaubAlsHalberOderGanzerTagAngegeben(final BigDecimal stunden,
      final BigDecimal stellenfaktor) {
    BigDecimal angabeUrlaubInStunden = stunden;
    BigDecimal arbeitstagInStundenGemaessStellenfaktor = new BigDecimal(8)
        .multiply(stellenfaktor);
    boolean tagIstHalberOderGanzerTag = angabeUrlaubInStunden
        .divide(arbeitstagInStundenGemaessStellenfaktor, 2, RoundingMode.HALF_UP)
        .remainder(new BigDecimal("0.5"))
        .compareTo(ZERO) == 0;

    return tagIstHalberOderGanzerTag;
  }

  public SichererSaldoStand findeLetztenSicherenMitarbeiterKontoSaldo(LocalDate vormonat,
      final List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontoSaetze) {
    SichererSaldoStand sicheresSaldo = new SichererSaldoStand();

    Collections.sort(mitarbeiterUrlaubKontoSaetze);
    Collections.reverse(mitarbeiterUrlaubKontoSaetze);

    for (MitarbeiterUrlaubKonto kontosatz : mitarbeiterUrlaubKontoSaetze) {
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

  public void loescheMitarbeiterUrlaubKontosaetzeZuArbeitsnachweis(
      final Arbeitsnachweis arbeitsnachweis) {
    List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKonto = mitarbeiterUrlaubKontoService
        .mitarbeiterUrlaubKontoByMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    List<MitarbeiterUrlaubKonto> zuLoeschendeKontosaetze = mitarbeiterUrlaubKonto.stream()
        .filter(kontosatz -> {
          return kontosatz.getWertstellung().getYear() == arbeitsnachweis.getJahr() &&
              kontosatz.getWertstellung().getMonthValue() == arbeitsnachweis.getMonat() &&
              kontosatz.isAutomatisch() &&
              !kontosatz.isEndgueltig();
        }).toList();
    mitarbeiterUrlaubKontoService.loescheMitarbeiterUrlaubKontosaetze(zuLoeschendeKontosaetze);

    mitarbeiterUrlaubKonto = mitarbeiterUrlaubKontoService
        .mitarbeiterUrlaubKontoByMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    berechneSalden(mitarbeiterUrlaubKonto);
    mitarbeiterUrlaubKontoService
        .speichereMitarbeiterUrlaubkontosaetze(mitarbeiterUrlaubKonto.stream().filter(kontosatz -> {
          return kontosatz.isGeaendert();
        }).toList());

  }

  public void ermittleUndSpeichereMitarbeiterUrlaubKontoAenderungen(Arbeitsnachweis arbeitsnachweis,
      BigDecimal anzahlUrlaubProjektstunden) {

    // Löschen alter Sätze
    List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontosaetze = mitarbeiterUrlaubKontoService
        .mitarbeiterUrlaubKontoByMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    List<MitarbeiterUrlaubKonto> zuLoeschendeKontosaetze = new ArrayList<>();
    List<MitarbeiterUrlaubKonto> verbleibendeKontosaetze = new ArrayList<>();
    List<MitarbeiterUrlaubKonto> neueKontoSaetze = new ArrayList<>();

    for (MitarbeiterUrlaubKonto kontosatz : mitarbeiterUrlaubKontosaetze) {
      if (!kontosatz.isEndgueltig() && kontosatz.isAutomatisch()
          && kontosatz.getWertstellung().getMonthValue() == arbeitsnachweis.getMonat()
          && kontosatz.getWertstellung().getYear() == arbeitsnachweis.getJahr()) {
        zuLoeschendeKontosaetze.add(kontosatz);
      } else {
        verbleibendeKontosaetze.add(kontosatz);
      }
    }

    mitarbeiterUrlaubKontoService.loescheMitarbeiterUrlaubKontosaetze(zuLoeschendeKontosaetze);

    // Erstellung neuer Sätze
    LocalDate letzterDesMonats = LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
            1)
        .plusMonths(1).minusDays(1);

    MitarbeiterUrlaubKonto urlaubGenommenKontosatz = new MitarbeiterUrlaubKonto();
    urlaubGenommenKontosatz.setEndgueltig(false);
    urlaubGenommenKontosatz.setAutomatisch(true);
    urlaubGenommenKontosatz.setBemerkung("Arbeitsnachweis " + arbeitsnachweis.getDatum());
    urlaubGenommenKontosatz.setBuchungstypUrlaubId(
        konstantenService.buchungstypUrlaubByBezeichnung("Genommen").getId());
    urlaubGenommenKontosatz.setBuchungsdatum(LocalDateTime.now());
    urlaubGenommenKontosatz.setWertstellung(letzterDesMonats);
    urlaubGenommenKontosatz.setMitarbeiterId(arbeitsnachweis.getMitarbeiterId());
    urlaubGenommenKontosatz.setAnzahlTage(
        berechneGenommeneTageAusStundenGemaessStellenfaktor(anzahlUrlaubProjektstunden,
            arbeitsnachweis));

    neueKontoSaetze.add(urlaubGenommenKontosatz);

    List<MitarbeiterUrlaubKonto> neueUndVerbleibendeKontosatze = new ArrayList<>();
    neueUndVerbleibendeKontosatze.addAll(neueKontoSaetze);
    neueUndVerbleibendeKontosatze.addAll(verbleibendeKontosaetze);

    Collections.sort(neueUndVerbleibendeKontosatze);

    berechneSalden(neueUndVerbleibendeKontosatze);

    mitarbeiterUrlaubKontoService.speichereMitarbeiterUrlaubkontosaetze(neueKontoSaetze);
    mitarbeiterUrlaubKontoService
        .speichereMitarbeiterUrlaubkontosaetze(
            verbleibendeKontosaetze.stream().filter(kontosatz -> {
              return kontosatz.isGeaendert();
            }).toList());

  }

  public BigDecimal berechneGenommeneTageAusStundenGemaessStellenfaktor(
      final BigDecimal anzahlUrlaubProjektstunden, final Arbeitsnachweis arbeitsnachweis) {
    BigDecimal stellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        arbeitsnachweis.getMitarbeiterId(), arbeitsnachweis.getJahr(),
        arbeitsnachweis.getMonat()).getStellenfaktor();
    return berechneGenommeneTageAusStundenGemaessStellenfaktor(anzahlUrlaubProjektstunden,
        stellenfaktor);
  }

  public BigDecimal berechneGenommeneTageAusStundenGemaessStellenfaktor(
      final BigDecimal anzahlUrlaubProjektstunden, final BigDecimal stellenfaktor) {
    return anzahlUrlaubProjektstunden.divide(new BigDecimal("8").multiply(stellenfaktor), 2,
        RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).negate();
  }

  public void bucheKorrekturbuchung(ProjektabrechnungKorrekturbuchung korrekturbuchung
  ) {
    MitarbeiterUrlaubKonto neuerKontoSatz = erstelleNeuenUrlaubKontoSatz(
        korrekturbuchung.getMitarbeiterId(), korrekturbuchung.getAnzahlStundenKosten(),
        korrekturbuchung.getJahr(), korrekturbuchung.getMonat());

    speichereNeueUrlaubKontoSaetze(Collections.singletonList(neuerKontoSatz),
        korrekturbuchung.getMitarbeiterId());


  }

  public void speichereNeueUrlaubKontoSaetze(List<MitarbeiterUrlaubKonto> neueUrlaubKontoSaetze,
      Long mitarbeiterId) {
    neueUrlaubKontoSaetze.forEach(
        neuerUrlaubKontoSatz -> neuerUrlaubKontoSatz.setEndgueltig(true));

    List<MitarbeiterUrlaubKonto> existierendeUrlaubKontoSaetze = mitarbeiterUrlaubKontoService.mitarbeiterUrlaubKontoByMitarbeiterId(
        mitarbeiterId);
    List<MitarbeiterUrlaubKonto> alleUrlaubKontoSatze = new ArrayList<>(
        existierendeUrlaubKontoSaetze);
    alleUrlaubKontoSatze.addAll(neueUrlaubKontoSaetze);
    berechneSalden(alleUrlaubKontoSatze);

    List<MitarbeiterUrlaubKonto> geanderteUrlaubKontoSaetze = alleUrlaubKontoSatze.stream()
        .filter(MitarbeiterUrlaubKonto::isGeaendert).toList();

    mitarbeiterUrlaubKontoService.speichereMitarbeiterUrlaubkontosaetze(
        geanderteUrlaubKontoSaetze);
  }

  private MitarbeiterUrlaubKonto erstelleNeuenUrlaubKontoSatz(Long mitarbeiterId,
      BigDecimal anzahlStunden, int jahr, int monat) {
    MitarbeiterUrlaubKonto neuerKontoSatz = new MitarbeiterUrlaubKonto();
    neuerKontoSatz.setAutomatisch(false);
    neuerKontoSatz.setBemerkung("Korrekturbuchung Projekt");
    neuerKontoSatz.setBuchungstypUrlaubId(
        konstantenService.buchungstypUrlaubByBezeichnung(UrlaubBuchungstyp.GENOMMEN.toString())
            .getId());
    neuerKontoSatz.setBuchungsdatum(LocalDateTime.now());
    neuerKontoSatz.setWertstellung(
        LocalDate.of(jahr, monat, 1).plusMonths(1).minusDays(1));
    neuerKontoSatz.setMitarbeiterId(mitarbeiterId);

    MitarbeiterStellenfaktor mitarbeiterStellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        mitarbeiterId, jahr, monat);

    neuerKontoSatz.setAnzahlTage(
        berechneGenommeneTageAusStundenGemaessStellenfaktor(
            anzahlStunden,
            mitarbeiterStellenfaktor.getStellenfaktor()));

    neuerKontoSatz.setEndgueltig(true);

    return neuerKontoSatz;
  }
}
