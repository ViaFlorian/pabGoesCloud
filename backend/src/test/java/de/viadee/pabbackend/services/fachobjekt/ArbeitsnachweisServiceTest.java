package de.viadee.pabbackend.services.fachobjekt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernRequest;
import de.viadee.pabbackend.entities.ArbeitsnachweisSpeichernResponse;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.enums.Belegarten;
import de.viadee.pabbackend.enums.ProjektabrechnungBearbeitungsstatus;
import de.viadee.pabbackend.enums.ProjektstundeTyp;
import de.viadee.pabbackend.repositories.pabdb.BelegartKonstantenRepository;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterRepository;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterStellenfaktorRepository;
import de.viadee.pabbackend.repositories.pabdb.ProjektRepository;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungRepository;
import de.viadee.pabbackend.repositories.pabdb.ProjektstundeRepository;
import de.viadee.pabbackend.repositories.pabdb.ProjektstundeTypKonstanteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@EnableJdbcAuditing
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
public class ArbeitsnachweisServiceTest extends MsSqlTestContainersTest {

  @Autowired
  private ArbeitsnachweisService arbeitsnachweisService;

  @Autowired
  private MitarbeiterRepository mitarbeiterRepository;

  @Autowired
  private ProjektstundeRepository projektstundeRepository;

  @Autowired
  private MitarbeiterStellenfaktorRepository mitarbeiterStellenfaktorRepository;

  @Autowired
  private ProjektRepository projektRepository;

  @Autowired
  private ProjektstundeTypKonstanteRepository projektstundeTypKonstanteRepository;

  @Autowired
  private BelegartKonstantenRepository belegartKonstantenRepository;

  @Autowired
  private ProjektabrechnungRepository projektabrechnungRepository;

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testArbeitsnachweisSpeichernMitarbeiterHatKeinenStellenfaktor() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(1);
    arbeitsnachweis.setSollstunden(new BigDecimal("168.00"));
    arbeitsnachweis.setAuszahlung(new BigDecimal("1.00"));
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setZuletztGeaendertAm(LocalDateTime.now());
    arbeitsnachweis.setZuletztGeaendertVon("Test");

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(
        "Der Arbeitsnachweis kann nicht gespeichert werden, es ist für Test, Test im Abrechnungsmonat 2022/1 kein Stellenfaktor administriert!",
        response.getMeldungen().get(0));

    arbeitsnachweis.setMonat(12);

    response = arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testLeerenArbeitsnachweisSpeichern() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);
    arbeitsnachweis.setSollstunden(new BigDecimal("168.00"));
    arbeitsnachweis.setAuszahlung(new BigDecimal("1.00"));
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setZuletztGeaendertAm(LocalDateTime.now());
    arbeitsnachweis.setZuletztGeaendertVon("Test");

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testArbeitsnachweisSpeichernLoescheAlleProjektstunden() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    final List<Projektstunde> projektstunden =
        IterableUtils.toList(
            projektstundeRepository.findProjektstundenByArbeitsnachweisId(arbeitsnachweis.getId()));

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(projektstunden);
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testArbeitsnachweisSpeichernAktualisiereAlleProjektstunden() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    final List<Projektstunde> projektstunden =
        IterableUtils.toList(
            projektstundeRepository.findProjektstundenByArbeitsnachweisId(arbeitsnachweis.getId()));

    // Stunden anpassen
    projektstunden.stream()
        .forEach(projektstunde -> projektstunde.setAnzahlStunden(new BigDecimal(10)));

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(projektstunden);

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testArbeitsnachweisSpeichernNeueProjektstunden() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    Projekt projekt = projektRepository.findByProjektnummer("999999");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projekt.getId());
    neueProjektstunden.add(neueProjektstunde);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void testArbeitsnachweisSpeichernAktualisierteUndGeloeschteProjektstunden() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    Projekt projekt = projektRepository.findByProjektnummer("999999");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projekt.getId());
    neueProjektstunden.add(neueProjektstunde);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());

    List<Projektstunde> projektstundenDesArbeitsnachweis =
        IterableUtils.toList(
            projektstundeRepository.findProjektstundenByArbeitsnachweisId(arbeitsnachweis.getId()));
    List<Projektstunde> aktualisieren = List.of(projektstundenDesArbeitsnachweis.get(0));
    aktualisieren.get(0).setAnzahlStunden(new BigDecimal(5));
    List<Projektstunde> loeschen =
        projektstundenDesArbeitsnachweis.subList(1, projektstundenDesArbeitsnachweis.size() - 1);

    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(loeschen);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(aktualisieren);
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());

    response = arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void
  testArbeitsnachweisSpeichernAktualisierteUndGeloeschteProjektstundenBeiAbgerechnetenProjektabrechnungen() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    Projekt projekt1 = projektRepository.findByProjektnummer("999999");
    Projekt projekt2 = projektRepository.findByProjektnummer("9999991");
    Projekt projekt3 = projektRepository.findByProjektnummer("9999992");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projekt1.getId());
    neueProjektstunden.add(neueProjektstunde);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());

    List<Projektstunde> projektstundenDesArbeitsnachweis =
        IterableUtils.toList(
            projektstundeRepository.findProjektstundenByArbeitsnachweisId(arbeitsnachweis.getId()));
    List<Projektstunde> aktualisieren = List.of(projektstundenDesArbeitsnachweis.get(0));
    aktualisieren.get(0).setAnzahlStunden(new BigDecimal(5));
    List<Projektstunde> loeschen =
        projektstundenDesArbeitsnachweis.subList(1, projektstundenDesArbeitsnachweis.size() - 1);

    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(loeschen);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(aktualisieren);
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(new ArrayList<>());

    Projektabrechnung projektabrechnung1 =
        projektabrechnungRepository.projektabrechungByProjektIdMonatJahr(
            projekt1.getId(), 12, 2022);
    Projektabrechnung projektabrechnung2 =
        projektabrechnungRepository.projektabrechungByProjektIdMonatJahr(
            projekt2.getId(), 12, 2022);
    Projektabrechnung projektabrechnung3 =
        projektabrechnungRepository.projektabrechungByProjektIdMonatJahr(
            projekt3.getId(), 12, 2022);

    projektabrechnungRepository.updateProjektabrechnungStatus(
        projektabrechnung1.getId(), ProjektabrechnungBearbeitungsstatus.ABGERECHNET.toStatusId());
    projektabrechnungRepository.updateProjektabrechnungStatus(
        projektabrechnung2.getId(), ProjektabrechnungBearbeitungsstatus.ABGERECHNET.toStatusId());
    projektabrechnungRepository.updateProjektabrechnungStatus(
        projektabrechnung3.getId(), ProjektabrechnungBearbeitungsstatus.ABGERECHNET.toStatusId());

    response = arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(3, response.getZurueckgesetzteProjekte().size());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void
  testArbeitsnachweisSpeichernMitReisezeit() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis =
        arbeitsnachweisService.arbeitsnachweisByMitarbeiterIDMonatJahr(
            mitarbeiter.getId(), 12, 2022);

    Projekt projekt1 = projektRepository.findByProjektnummer("999999");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());
    ProjektstundeTypKonstante tatsaechlicheReise =
        projektstundeTypKonstanteRepository.findByTextKurz(
            ProjektstundeTyp.TATSAECHLICHE_REISEZEIT.getValue());
    BelegartKonstante pkw = belegartKonstantenRepository.findByTextKurz(Belegarten.PKW.toString());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projekt1.getId());
    neueProjektstunden.add(neueProjektstunde);

    Projektstunde neueProjektstundeReise = new Projektstunde();
    neueProjektstundeReise.setProjektstundeTypId(tatsaechlicheReise.getId());
    neueProjektstundeReise.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstundeReise.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstundeReise.setAnzahlStunden(new BigDecimal(4));
    neueProjektstundeReise.setFakturierfaehig(true);
    neueProjektstundeReise.setProjektId(projekt1.getId());
    neueProjektstunden.add(neueProjektstundeReise);

    List<Beleg> neueBelege = new ArrayList<>();
    Beleg belegPKW = new Beleg();
    belegPKW.setProjektId(projekt1.getId());
    belegPKW.setArbeitsnachweisId(arbeitsnachweis.getId());
    belegPKW.setBelegartId(pkw.getId());
    belegPKW.setKilometer(new BigDecimal("100.00"));
    belegPKW.setBetrag(new BigDecimal("30.00"));
    belegPKW.setDatum(LocalDate.of(2022, 12, 1));
    neueBelege.add(belegPKW);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueBelege(neueBelege);

    List<DreiMonatsRegel> dreiMonatsRegeln = new ArrayList<>();
    DreiMonatsRegel dreiMonatsRegel = new DreiMonatsRegel();
    dreiMonatsRegel.setArbeitsstaette("Köln");
    dreiMonatsRegel.setAutomatischErfasst(true);
    dreiMonatsRegel.setGueltigVon(LocalDate.of(2022, 1, 1));
    dreiMonatsRegel.setGueltigBis(LocalDate.of(2023, 2, 1));
    dreiMonatsRegel.setKundeScribeId("TestScribeIDKunde1");
    dreiMonatsRegel.setMitarbeiterId(mitarbeiter.getId());
    dreiMonatsRegeln.add(dreiMonatsRegel);

    arbeitsnachweisSpeichernRequest.setBerechneteDreiMonatsRegeln(dreiMonatsRegeln);

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void
  testArbeitsnachweisSpeichernMitUrlaub() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();

    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);
    arbeitsnachweis.setSollstunden(new BigDecimal("168.00"));
    arbeitsnachweis.setAuszahlung(new BigDecimal("1.00"));
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setZuletztGeaendertAm(LocalDateTime.now());
    arbeitsnachweis.setZuletztGeaendertVon("Test");

    Projekt projektNormal = projektRepository.findByProjektnummer("999999");
    Projekt projektUrlaub = projektRepository.findByProjektnummer("9004");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());
    BelegartKonstante pkw = belegartKonstantenRepository.findByTextKurz(Belegarten.PKW.toString());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projektNormal.getId());
    neueProjektstunden.add(neueProjektstunde);

    Projektstunde neueProjektstundeUrlaub = new Projektstunde();
    neueProjektstundeUrlaub.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstundeUrlaub.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstundeUrlaub.setTagVon(LocalDate.of(2022, 12, 2));
    neueProjektstundeUrlaub.setAnzahlStunden(new BigDecimal(8));
    neueProjektstundeUrlaub.setFakturierfaehig(true);
    neueProjektstundeUrlaub.setProjektId(projektUrlaub.getId());
    neueProjektstunden.add(neueProjektstundeUrlaub);

    List<Beleg> neueBelege = new ArrayList<>();
    Beleg belegPKW = new Beleg();
    belegPKW.setProjektId(projektNormal.getId());
    belegPKW.setArbeitsnachweisId(arbeitsnachweis.getId());
    belegPKW.setBelegartId(pkw.getId());
    belegPKW.setKilometer(new BigDecimal("100.00"));
    belegPKW.setBetrag(new BigDecimal("30.00"));
    belegPKW.setDatum(LocalDate.of(2022, 12, 1));
    neueBelege.add(belegPKW);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueBelege(neueBelege);

    List<DreiMonatsRegel> dreiMonatsRegeln = new ArrayList<>();
    DreiMonatsRegel dreiMonatsRegel = new DreiMonatsRegel();
    dreiMonatsRegel.setArbeitsstaette("Köln");
    dreiMonatsRegel.setAutomatischErfasst(true);
    dreiMonatsRegel.setGueltigVon(LocalDate.of(2022, 1, 1));
    dreiMonatsRegel.setGueltigBis(LocalDate.of(2023, 2, 1));
    dreiMonatsRegel.setKundeScribeId("TestScribeIDKunde1");
    dreiMonatsRegel.setMitarbeiterId(mitarbeiter.getId());
    dreiMonatsRegeln.add(dreiMonatsRegel);

    arbeitsnachweisSpeichernRequest.setBerechneteDreiMonatsRegeln(dreiMonatsRegeln);

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);
    assertEquals(new ArrayList<>(), response.getZurueckgesetzteProjekte());
    assertEquals(new ArrayList<>(), response.getMeldungen());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisSpeichernTestdaten.sql"})
  public void
  testArbeitsnachweisLoeschen() {
    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();

    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);
    arbeitsnachweis.setSollstunden(new BigDecimal("168.00"));
    arbeitsnachweis.setAuszahlung(new BigDecimal("1.00"));
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setZuletztGeaendertAm(LocalDateTime.now());
    arbeitsnachweis.setZuletztGeaendertVon("Test");

    Projekt projektNormal = projektRepository.findByProjektnummer("999999");
    Projekt projektUrlaub = projektRepository.findByProjektnummer("9004");
    ProjektstundeTypKonstante normaleProjektstunde =
        projektstundeTypKonstanteRepository.findByTextKurz(ProjektstundeTyp.NORMAL.getValue());
    BelegartKonstante pkw = belegartKonstantenRepository.findByTextKurz(Belegarten.PKW.toString());

    List<Projektstunde> neueProjektstunden = new ArrayList<>();
    Projektstunde neueProjektstunde = new Projektstunde();
    neueProjektstunde.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstunde.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstunde.setTagVon(LocalDate.of(2022, 12, 1));
    neueProjektstunde.setAnzahlStunden(new BigDecimal(10));
    neueProjektstunde.setFakturierfaehig(true);
    neueProjektstunde.setProjektId(projektNormal.getId());
    neueProjektstunden.add(neueProjektstunde);

    Projektstunde neueProjektstundeUrlaub = new Projektstunde();
    neueProjektstundeUrlaub.setProjektstundeTypId(normaleProjektstunde.getId());
    neueProjektstundeUrlaub.setArbeitsnachweisId(arbeitsnachweis.getId());
    neueProjektstundeUrlaub.setTagVon(LocalDate.of(2022, 12, 2));
    neueProjektstundeUrlaub.setAnzahlStunden(new BigDecimal(8));
    neueProjektstundeUrlaub.setFakturierfaehig(true);
    neueProjektstundeUrlaub.setProjektId(projektUrlaub.getId());
    neueProjektstunden.add(neueProjektstundeUrlaub);

    List<Beleg> neueBelege = new ArrayList<>();
    Beleg belegPKW = new Beleg();
    belegPKW.setProjektId(projektNormal.getId());
    belegPKW.setArbeitsnachweisId(arbeitsnachweis.getId());
    belegPKW.setBelegartId(pkw.getId());
    belegPKW.setKilometer(new BigDecimal("100.00"));
    belegPKW.setBetrag(new BigDecimal("30.00"));
    belegPKW.setDatum(LocalDate.of(2022, 12, 1));
    neueBelege.add(belegPKW);

    ArbeitsnachweisSpeichernRequest arbeitsnachweisSpeichernRequest =
        new ArbeitsnachweisSpeichernRequest();
    arbeitsnachweisSpeichernRequest.setArbeitsnachweis(arbeitsnachweis);
    arbeitsnachweisSpeichernRequest.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweisSpeichernRequest.setGeloeschteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueProjektstunden(neueProjektstunden);
    arbeitsnachweisSpeichernRequest.setAktualisierteProjektstunden(new ArrayList<>());
    arbeitsnachweisSpeichernRequest.setNeueBelege(neueBelege);

    List<DreiMonatsRegel> dreiMonatsRegeln = new ArrayList<>();
    DreiMonatsRegel dreiMonatsRegel = new DreiMonatsRegel();
    dreiMonatsRegel.setArbeitsstaette("Köln");
    dreiMonatsRegel.setAutomatischErfasst(true);
    dreiMonatsRegel.setGueltigVon(LocalDate.of(2022, 1, 1));
    dreiMonatsRegel.setGueltigBis(LocalDate.of(2023, 2, 1));
    dreiMonatsRegel.setKundeScribeId("TestScribeIDKunde1");
    dreiMonatsRegel.setMitarbeiterId(mitarbeiter.getId());
    dreiMonatsRegeln.add(dreiMonatsRegel);

    arbeitsnachweisSpeichernRequest.setBerechneteDreiMonatsRegeln(dreiMonatsRegeln);

    ArbeitsnachweisSpeichernResponse response =
        arbeitsnachweisService.speichereArbeitsnachweis(arbeitsnachweisSpeichernRequest);

    arbeitsnachweisService.loescheArbeitsnachweis(response.getArbeitsnachweis().getId());

  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisFehlendTestdaten.sql"})
  public void testFehlendeArbeitsnachweiseFuerZeitraum_ZeitraumNachAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now();
    LocalDate bisWann = LocalDate.now();

    List fehlendeArbeitsnachweise = arbeitsnachweisService.fehlendeArbeitsnachweiseFuerZeitraum(
        abWann, bisWann);

    assertTrue(fehlendeArbeitsnachweise.isEmpty());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisFehlendTestdaten.sql"})
  public void testFehlendeArbeitsnachweiseFuerZeitraum_ZeitraumVorAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(3);
    LocalDate bisWann = LocalDate.now().minusMonths(2);

    List fehlendeArbeitsnachweise = arbeitsnachweisService.fehlendeArbeitsnachweiseFuerZeitraum(
        abWann, bisWann);

    assertTrue(fehlendeArbeitsnachweise.isEmpty());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisFehlendTestdaten.sql"})
  public void testFehlendeArbeitsnachweiseFuerZeitraum_ZeitraumEntsprichtAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(1);
    LocalDate bisWann = LocalDate.now().minusMonths(1);

    List fehlendeArbeitsnachweise = arbeitsnachweisService.fehlendeArbeitsnachweiseFuerZeitraum(
        abWann, bisWann);

    assertFalse(fehlendeArbeitsnachweise.isEmpty());
    assertEquals(1, fehlendeArbeitsnachweise.size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/arbeitsnachweisFehlendTestdaten.sql"})
  public void testFehlendeArbeitsnachweiseFuerZeitraum_ZeitraumEnthaeltAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(2);
    LocalDate bisWann = LocalDate.now();

    List fehlendeArbeitsnachweise = arbeitsnachweisService.fehlendeArbeitsnachweiseFuerZeitraum(
        abWann, bisWann);

    assertFalse(fehlendeArbeitsnachweise.isEmpty());
    assertEquals(1, fehlendeArbeitsnachweise.size());
  }

  @TestConfiguration
  @Profile("test")
  static class TestConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
      return () -> Optional.ofNullable("test");
    }
  }
}
