package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.Belegarten.BAHN;
import static de.viadee.pabbackend.enums.Belegarten.FLUG;
import static de.viadee.pabbackend.enums.Belegarten.JOBTICKET;
import static de.viadee.pabbackend.enums.Belegarten.OPNV;
import static de.viadee.pabbackend.enums.Belegarten.PARKEN;
import static de.viadee.pabbackend.enums.Belegarten.PKW;
import static de.viadee.pabbackend.enums.Belegarten.TAXI;
import static de.viadee.pabbackend.enums.Belegarten.VERBINDUNGSENTGELT;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.FIRMA;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.KEIN;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.Kunde;
import de.viadee.pabbackend.entities.LohnartberechnungErgebnis;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.enums.ProjektstundeTyp;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterRepository;
import de.viadee.pabbackend.services.fachobjekt.DreiMonatsRegelService;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import de.viadee.pabbackend.services.fachobjekt.KundeService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
class LohnartenberechnungTest extends MsSqlTestContainersTest {

  @Autowired
  private Lohnartenberechnung lohnartenberechnung;
  @Autowired
  private KonstantenService konstantenService;
  @Autowired
  private MitarbeiterRepository mitarbeiterRepository;
  @Autowired
  private KundeService kundeService;
  @Autowired
  private ProjektService projektService;
  @Autowired
  private DreiMonatsRegelService dreiMonatsRegelService;
  @Autowired
  private Zeitrechnung zeitrechnung;

  @Test
  void testLeereLohnartenzuweisung() {

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    assertTrue(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().isEmpty());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHandyErstattung() {

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(LocalDate.now().getYear());
    arbeitsnachweis.setMonat(LocalDate.now().getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Beleg verbindungsentgelt = new Beleg();
    verbindungsentgelt.setBelegartId(
        konstantenService.belegartByTextKurz(VERBINDUNGSENTGELT.toString()).getId());
    verbindungsentgelt.setBetrag(new BigDecimal("22.99"));
    belege.add(verbindungsentgelt);

    String smartphoneSelektion = KEIN.toString();

    LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("091", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("22.99")));

    smartphoneSelektion = EIGEN.toString();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("092", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("42.99")));

    smartphoneSelektion = FIRMA.toString();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("091", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("22.99")));

    belege.clear();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testJobticket() {

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Beleg jobticket = new Beleg();
    jobticket.setBelegartId(konstantenService.belegartByTextKurz(JOBTICKET.toString()).getId());
    jobticket.setBetrag(new BigDecimal("70.00"));
    belege.add(jobticket);

    LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("9057", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("70.00")));

    belege.clear();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testDienstUndNebenkostenFrei() {

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Beleg jobticket = new Beleg();
    jobticket.setBelegartId(konstantenService.belegartByTextKurz(JOBTICKET.toString()).getId());
    jobticket.setBetrag(new BigDecimal("70.00"));
    belege.add(jobticket);

    final Beleg verbindungsentgelt = new Beleg();
    verbindungsentgelt.setBelegartId(
        konstantenService.belegartByTextKurz(VERBINDUNGSENTGELT.toString()).getId());
    verbindungsentgelt.setBetrag(new BigDecimal("22.99"));
    belege.add(verbindungsentgelt);

    final Beleg pkw = new Beleg();
    pkw.setBelegartId(konstantenService.belegartByTextKurz(PKW.toString()).getId());
    pkw.setBetrag(new BigDecimal("100.00"));
    belege.add(pkw);

    final Beleg bahn = new Beleg();
    bahn.setBelegartId(konstantenService.belegartByTextKurz(BAHN.toString()).getId());
    bahn.setBetrag(new BigDecimal("19.24"));
    belege.add(bahn);

    final Beleg parken = new Beleg();
    parken.setBelegartId(konstantenService.belegartByTextKurz(PARKEN.toString()).getId());
    parken.setBetrag(new BigDecimal("6.50"));
    belege.add(parken);

    final Beleg flug = new Beleg();
    flug.setBelegartId(konstantenService.belegartByTextKurz(FLUG.toString()).getId());
    flug.setBetrag(new BigDecimal("157.86"));
    belege.add(flug);

    final Beleg oepnv = new Beleg();
    oepnv.setBelegartId(konstantenService.belegartByTextKurz(OPNV.toString()).getId());
    oepnv.setBetrag(new BigDecimal("11.00"));
    belege.add(oepnv);

    final Beleg taxi = new Beleg();
    taxi.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    taxi.setBetrag(new BigDecimal("27.00"));
    belege.add(taxi);

    final Beleg hotel = new Beleg();
    hotel.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    hotel.setBetrag(new BigDecimal("123.00"));
    belege.add(hotel);

    final Beleg sonstiges = new Beleg();
    sonstiges.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    sonstiges.setBetrag(new BigDecimal("7.20"));
    belege.add(sonstiges);

    LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung(),
        Collections.reverseOrder());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("091", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("22.99")));
    assertEquals("9978", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("451.80")));
    assertEquals("9057", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("70.00")));

    belege.clear();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testDienstUndNebenkostenFreiMitFirmenwagen() {

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setFirmenwagen(Boolean.TRUE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Beleg jobticket = new Beleg();
    jobticket.setBelegartId(konstantenService.belegartByTextKurz(JOBTICKET.toString()).getId());
    jobticket.setBetrag(new BigDecimal("70.00"));
    belege.add(jobticket);

    final Beleg verbindungsentgelt = new Beleg();
    verbindungsentgelt.setBelegartId(
        konstantenService.belegartByTextKurz(VERBINDUNGSENTGELT.toString()).getId());
    verbindungsentgelt.setBetrag(new BigDecimal("22.99"));
    belege.add(verbindungsentgelt);

    final Beleg pkw = new Beleg();
    pkw.setBelegartId(konstantenService.belegartByTextKurz(PKW.toString()).getId());
    pkw.setBetrag(new BigDecimal("100.00"));
    belege.add(pkw);

    final Beleg bahn = new Beleg();
    bahn.setBelegartId(konstantenService.belegartByTextKurz(BAHN.toString()).getId());
    bahn.setBetrag(new BigDecimal("19.24"));
    belege.add(bahn);

    final Beleg parken = new Beleg();
    parken.setBelegartId(konstantenService.belegartByTextKurz(PARKEN.toString()).getId());
    parken.setBetrag(new BigDecimal("6.50"));
    belege.add(parken);

    final Beleg flug = new Beleg();
    flug.setBelegartId(konstantenService.belegartByTextKurz(FLUG.toString()).getId());
    flug.setBetrag(new BigDecimal("157.86"));
    belege.add(flug);

    final Beleg oepnv = new Beleg();
    oepnv.setBelegartId(konstantenService.belegartByTextKurz(OPNV.toString()).getId());
    oepnv.setBetrag(new BigDecimal("11.00"));
    belege.add(oepnv);

    final Beleg taxi = new Beleg();
    taxi.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    taxi.setBetrag(new BigDecimal("27.00"));
    belege.add(taxi);

    final Beleg hotel = new Beleg();
    hotel.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    hotel.setBetrag(new BigDecimal("123.00"));
    belege.add(hotel);

    final Beleg sonstiges = new Beleg();
    sonstiges.setBelegartId(konstantenService.belegartByTextKurz(TAXI.toString()).getId());
    sonstiges.setBetrag(new BigDecimal("7.20"));
    belege.add(sonstiges);

    LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("9057", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("70.00")));
    assertEquals("9978", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("351.80")));
    assertEquals("091", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("22.99")));

    belege.clear();

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSpesenUndZuschlaegeOhneDreiMonatsRegel() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Abwesenheit abwesenheit = new Abwesenheit();
    abwesenheit.setArbeitsnachweisId(arbeitsnachweis.getId());
    abwesenheit.setProjektId(projektService.projektByProjektnummer("999999").getId());
    abwesenheit.setSpesen(new BigDecimal("180.00"));
    abwesenheit.setZuschlag(new BigDecimal("220.00"));

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    abwesenheiten.add(abwesenheit);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("9979", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag(),
        new BigDecimal("180.00"));
    assertEquals("079", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag(),
        new BigDecimal("180.00"));
    assertEquals("080", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag(),
        new BigDecimal("40.00"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/dreiMonatsRegelTestdaten.sql"})
  void testSpesenUndZuschlaegeMitDreiMonatsRegel() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();
    final Kunde kunde = kundeService.kundeBySribeId("TestScribeIDKunde1");

    final DreiMonatsRegel regel = new DreiMonatsRegel();
    regel.setMitarbeiterId(mitarbeiter.getId());
    regel.setKundeScribeId(kunde.getScribeId());
    regel.setArbeitsstaette("viadee GS");
    regel.setGueltigVon(LocalDate.now().minusMonths(4));

    final List<DreiMonatsRegel> regeln = new ArrayList<>();
    regeln.add(regel);

    dreiMonatsRegelService.speichereDreiMonatsRegeln(regeln);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setMitarbeiterId(mitarbeiter.getId());
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(2022);
    arbeitsnachweis.setMonat(12);

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Abwesenheit abwesenheitMitDreiTageRegel = new Abwesenheit();
    abwesenheitMitDreiTageRegel.setTagVon(LocalDate.now());
    abwesenheitMitDreiTageRegel.setArbeitsnachweisId(arbeitsnachweis.getId());
    abwesenheitMitDreiTageRegel.setDreiMonatsRegelAktiv(true);
    abwesenheitMitDreiTageRegel.setArbeitsstaette("viadee GS");
    abwesenheitMitDreiTageRegel.setProjektId(
        projektService.projektByProjektnummer("999999").getId());
    abwesenheitMitDreiTageRegel.setSpesen(new BigDecimal("180.00"));
    abwesenheitMitDreiTageRegel.setZuschlag(new BigDecimal("220.00"));

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    abwesenheiten.add(abwesenheitMitDreiTageRegel);

    LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("080", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag(),
        new BigDecimal("400.00"));

    final Abwesenheit weitereAbwesenheit = new Abwesenheit();
    weitereAbwesenheit.setTagVon(LocalDate.now());
    weitereAbwesenheit.setArbeitsnachweisId(arbeitsnachweis.getId());
    weitereAbwesenheit.setProjektId(projektService.projektByProjektnummer("9999991").getId());
    weitereAbwesenheit.setSpesen(new BigDecimal("100.00"));
    weitereAbwesenheit.setZuschlag(new BigDecimal("130.00"));

    abwesenheiten.add(weitereAbwesenheit);

    lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(arbeitsnachweis, belege,
        smartphoneSelektion, abwesenheiten, sonderarbeitszeiten, rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("9979", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag(),
        new BigDecimal("100.00"));
    assertEquals("079", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag(),
        new BigDecimal("100.00"));
    assertEquals("080", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag(),
        new BigDecimal("430.00"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testUeberstundenGrundverguetungMitVollerStelle() {

    // Nächsten Samstag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 5 && !(zeitrechnung.istHeiligabend(startDatum)
        || zeitrechnung.istSilvester(startDatum)) || zeitrechnung.istFeiertag(startDatum)
        || zeitrechnung.istHeiligabend(startDatum.plusDays(1)) || zeitrechnung.istSilvester(
        startDatum.plusDays(1)) || zeitrechnung.istFeiertag(startDatum.plusDays(1))
        || zeitrechnung.istHeiligabend(startDatum.plusDays(2)) || zeitrechnung.istSilvester(
        startDatum.plusDays(2)) || zeitrechnung.istFeiertag(startDatum.plusDays(2)))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());
    arbeitsnachweis.setMitarbeiterId(mitarbeiterRepository.letzterMitarbeiter().getId());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = new BigDecimal("17.00");

    final Projektstunde sonderarbeitszeitSamstag = new Projektstunde();
    sonderarbeitszeitSamstag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSamstag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitSamstag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitSamstag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitSamstag.setTagVon(startDatum);

    final Projektstunde sonderarbeitszeitMontag = new Projektstunde();
    sonderarbeitszeitMontag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitMontag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitMontag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitMontag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitMontag.setTagVon(startDatum.plusDays(2));

    sonderarbeitszeiten.add(sonderarbeitszeitSamstag);
    sonderarbeitszeiten.add(sonderarbeitszeitMontag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag(),
        new BigDecimal("25.00"));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag(),
        new BigDecimal("8.00"));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag(),
        new BigDecimal("25.00"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testUeberstundenGrundverguetungMitTeilzeitStelle() {

    // Nächsten Samstag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 5 && !(zeitrechnung.istHeiligabend(startDatum)
        || zeitrechnung.istSilvester(startDatum)) || zeitrechnung.istFeiertag(startDatum)
        || zeitrechnung.istHeiligabend(startDatum.plusDays(1)) || zeitrechnung.istSilvester(
        startDatum.plusDays(1)) || zeitrechnung.istFeiertag(startDatum.plusDays(1))
        || zeitrechnung.istHeiligabend(startDatum.plusDays(2)) || zeitrechnung.istSilvester(
        startDatum.plusDays(2)) || zeitrechnung.istFeiertag(startDatum.plusDays(2)))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(new BigDecimal("0.8"));
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = new BigDecimal("17.00");

    final Projektstunde sonderarbeitszeitSamstag = new Projektstunde();
    sonderarbeitszeitSamstag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSamstag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitSamstag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitSamstag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitSamstag.setTagVon(startDatum);

    final Projektstunde sonderarbeitszeitMontag = new Projektstunde();
    sonderarbeitszeitMontag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitMontag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitMontag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitMontag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitMontag.setTagVon(startDatum.plusDays(2));

    sonderarbeitszeiten.add(sonderarbeitszeitSamstag);
    sonderarbeitszeiten.add(sonderarbeitszeitMontag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag(),
        new BigDecimal("25.00"));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag(),
        new BigDecimal("8.00"));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag(),
        new BigDecimal("8.00"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testWerktag6Bis20UhrSonderzeiten() {

    // Werktag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() > 5 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31)))
        || zeitrechnung.istFeiertag(startDatum)) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitWerktag = new Projektstunde();
    sonderarbeitszeitWerktag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitWerktag.setAnzahlStunden(new BigDecimal("14.00"));
    sonderarbeitszeitWerktag.setUhrzeitVon(LocalTime.of(6, 00));
    sonderarbeitszeitWerktag.setUhrzeitBis(LocalTime.of(19, 59, 59));
    sonderarbeitszeitWerktag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitWerktag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("14.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testWerktag20Bis24UhrSonderzeiten() {
    // Werktag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() > 5 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitWerktag = new Projektstunde();
    sonderarbeitszeitWerktag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitWerktag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitWerktag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitWerktag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitWerktag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitWerktag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("4.0")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("4.0")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("4.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSamstag6Bis20UhrSonderzeiten() {

    // Samstag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 5 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSamstag = new Projektstunde();
    sonderarbeitszeitSamstag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSamstag.setAnzahlStunden(new BigDecimal("14.00"));
    sonderarbeitszeitSamstag.setUhrzeitVon(LocalTime.of(6, 00));
    sonderarbeitszeitSamstag.setUhrzeitBis(LocalTime.of(19, 59, 59));
    sonderarbeitszeitSamstag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitSamstag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(2, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("14.0")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("14.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSamstag20Bis24UhrSonderzeiten() {

    // Samstag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);

    while ((startDatum.getDayOfWeek().ordinal() != 5 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSamstag = new Projektstunde();
    sonderarbeitszeitSamstag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSamstag.setAnzahlStunden(new BigDecimal("4.00"));
    sonderarbeitszeitSamstag.setUhrzeitVon(LocalTime.of(20, 00));
    sonderarbeitszeitSamstag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitSamstag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitSamstag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("4.0")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("4.0")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("4.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSamstagGanztaegigSonderzeiten() {

    // Samstag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 5 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSamstag = new Projektstunde();
    sonderarbeitszeitSamstag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSamstag.setAnzahlStunden(new BigDecimal("24.00"));
    sonderarbeitszeitSamstag.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSamstag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitSamstag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitSamstag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("14.0")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("24.0")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("10.0")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("10.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSonntagGanztaegigSonderzeiten() {

    // Sonntag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 6 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSonntag = new Projektstunde();
    sonderarbeitszeitSonntag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSonntag.setAnzahlStunden(new BigDecimal("24.00"));
    sonderarbeitszeitSonntag.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSonntag.setUhrzeitBis(LocalTime.of(23, 59, 59));
    sonderarbeitszeitSonntag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitSonntag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("24.0")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("24.0")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("24.0")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSonntagSonderzeiten() {

    // Sonntag finden
    LocalDate startDatum = LocalDate.of(2019, 12, 1);
    while ((startDatum.getDayOfWeek().ordinal() != 6 && !(startDatum.getMonthValue() == 12 && (
        startDatum.getDayOfMonth() == 24 || startDatum.getDayOfMonth() == 31))
        || zeitrechnung.istFeiertag(startDatum))) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(startDatum.getYear());
    arbeitsnachweis.setMonat(startDatum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSonntag = new Projektstunde();
    sonderarbeitszeitSonntag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSonntag.setAnzahlStunden(new BigDecimal("6.75"));
    sonderarbeitszeitSonntag.setUhrzeitVon(LocalTime.of(10, 0));
    sonderarbeitszeitSonntag.setUhrzeitBis(LocalTime.of(16, 45));
    sonderarbeitszeitSonntag.setTagVon(startDatum);

    sonderarbeitszeiten.add(sonderarbeitszeitSonntag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("6.75")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.75")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.75")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testFeiertagSonderzeiten() {

    // Feiertag 01.01.2050 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2050, 1, 1);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitFeiertag = new Projektstunde();
    sonderarbeitszeitFeiertag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitFeiertag.setAnzahlStunden(new BigDecimal("6.75"));
    sonderarbeitszeitFeiertag.setUhrzeitVon(LocalTime.of(10, 0));
    sonderarbeitszeitFeiertag.setUhrzeitBis(LocalTime.of(16, 45));
    sonderarbeitszeitFeiertag.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitFeiertag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(2, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("6.75")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.75")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testFeiertagGanztaegigSonderzeiten() {

    // Feiertag 01.01.2050 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2050, 1, 1);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitFeiertag = new Projektstunde();
    sonderarbeitszeitFeiertag.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitFeiertag.setAnzahlStunden(new BigDecimal("24.00"));
    sonderarbeitszeitFeiertag.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitFeiertag.setUhrzeitBis(LocalTime.of(23, 59, 59, 999999999));
    sonderarbeitszeitFeiertag.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitFeiertag);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(2, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("24.00")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("24.00")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendNachVierzehnUhrSonderzeiten() {

    // 24.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(14, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(22, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(2, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.5")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendWerktagVorVierzehnUhrSonderzeiten() {

    // 24.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendWerktagUeberVierzehnUhrHinausSonderzeiten() {

    // 24.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendSamstagVorVierzehnUhrSonderzeiten() {

    // 24.12.2022 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2022, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("2.50")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("6.00")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendSamstagUeberVierzehnUhrHinausSonderzeiten() {

    // 24.12.2022 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2022, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(5, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.00")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(4).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(4).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendSonntagVorVierzehnUhrSonderzeiten() {

    // 24.12.2023 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2023, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("8.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testHeiligabendSonntagUeberVierzehnUhrHinausSonderzeiten() {

    // 24.12.2023 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2023, 12, 24);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitHeiligabend = new Projektstunde();
    sonderarbeitszeitHeiligabend.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitHeiligabend.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitHeiligabend.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitHeiligabend.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitHeiligabend.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitHeiligabend);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("14.00")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("14.00")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterNachVierzehnUhrSonderzeiten() {

    // 31.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(14, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(22, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(2, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.5")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterWerktagVorVierzehnUhrSonderzeiten() {

    // 31.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterWerktagUeberVierzehnUhrHinausSonderzeiten() {

    // 31.12.2019 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2019, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterSamstagVorVierzehnUhrSonderzeiten() {

    // 31.12.2022 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2022, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("2.50")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("6.00")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterSamstagUeberVierzehnUhrHinausSonderzeiten() {

    // 31.12.2022 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2022, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(5, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.00")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("014", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("016", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("6.00")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(4).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(4).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterSonntagVorVierzehnUhrSonderzeiten() {

    // 31.12.2023 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2023, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("8.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(8, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(3, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("8.50")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("8.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testSilvesterSonntagUeberVierzehnUhrHinausSonderzeiten() {

    // 31.12.2023 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2023, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde sonderarbeitszeitSilvester = new Projektstunde();
    sonderarbeitszeitSilvester.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.SONDERARBEITSZEIT.getValue())
            .getId());
    sonderarbeitszeitSilvester.setAnzahlStunden(new BigDecimal("16.50"));
    sonderarbeitszeitSilvester.setUhrzeitVon(LocalTime.of(0, 0));
    sonderarbeitszeitSilvester.setUhrzeitBis(LocalTime.of(16, 30));
    sonderarbeitszeitSilvester.setTagVon(datum);

    sonderarbeitszeiten.add(sonderarbeitszeitSilvester);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(4, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("008", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("14.00")));
    assertEquals("009", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(1).getBetrag()
        .compareTo(new BigDecimal("14.00")));
    assertEquals("012", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(2).getBetrag()
        .compareTo(new BigDecimal("16.50")));
    assertEquals("107", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(3).getBetrag()
        .compareTo(new BigDecimal("2.50")));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/lohnartenberechnungTestdaten.sql"})
  void testRufbereitschaftLohnart() {

    // 31.12.2023 ist in den Testdaten hinterlegt
    LocalDate datum = LocalDate.of(2023, 12, 31);

    final Arbeitsnachweis arbeitsnachweis = new Arbeitsnachweis();
    arbeitsnachweis.setStellenfaktor(ONE);
    arbeitsnachweis.setJahr(datum.getYear());
    arbeitsnachweis.setMonat(datum.getMonthValue());

    final List<Beleg> belege = new ArrayList<>();
    final String smartphoneSelektion = KEIN.toString();
    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final List<Projektstunde> sonderarbeitszeiten = new ArrayList<>();
    final List<Projektstunde> rufbereitschaften = new ArrayList<>();
    final BigDecimal auszahlung = ZERO;

    final Projektstunde rufbereitschaft = new Projektstunde();
    rufbereitschaft.setProjektstundeTypId(
        konstantenService.projektstundeTypByTextKurz(ProjektstundeTyp.RUFBEREITSCHAFT.getValue())
            .getId());
    rufbereitschaft.setAnzahlStunden(new BigDecimal("10.00"));
    rufbereitschaft.setUhrzeitVon(LocalTime.of(0, 0));
    rufbereitschaft.setUhrzeitBis(LocalTime.of(10, 0));
    rufbereitschaft.setTagVon(datum);
    rufbereitschaft.setTagBis(datum);

    rufbereitschaften.add(rufbereitschaft);

    final LohnartberechnungErgebnis lohnartenZuweisung = lohnartenberechnung.berechneLohnarten(
        arbeitsnachweis, belege, smartphoneSelektion, abwesenheiten, sonderarbeitszeiten,
        rufbereitschaften, auszahlung);

    Collections.sort(lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung());

    assertEquals(1, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().size());
    assertEquals("032", konstantenService.lohnartById(
        lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getLohnartId()).getKonto());
    assertEquals(0, lohnartenZuweisung.getArbeitsnachweisLohnartZuordnung().get(0).getBetrag()
        .compareTo(new BigDecimal("150.00")));
  }
}
