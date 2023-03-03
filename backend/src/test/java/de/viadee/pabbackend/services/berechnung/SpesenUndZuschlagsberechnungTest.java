package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus.ERFASST;
import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.ErgebnisBerechnungGesetzlicheSpesen;
import de.viadee.pabbackend.entities.ErgebnisBerechnungViadeeZuschlaege;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.repositories.pabdb.ArbeitsnachweisRepository;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterRepository;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
class SpesenUndZuschlagsberechnungTest extends MsSqlTestContainersTest {

  @Autowired
  private SpesenUndZuschlagsberechnung spesenUndZuschlagsberechnung;

  @Autowired
  private ArbeitsnachweisRepository arbeitsnachweisRepository;

  @Autowired
  private MitarbeiterRepository mitarbeiterRepository;

  @Autowired
  private DreiMonatsRegelBerechnung dreiMonatsRegelBerechnung;

  @Autowired
  private ProjektService projektService;

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testGesetzlicheSpesen() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit tag1Anreise = new Abwesenheit();
    final Abwesenheit tag2AbreiseMitFruehstueck = new Abwesenheit();
    final Abwesenheit tag10Anreise = new Abwesenheit();
    final Abwesenheit tag11GanztaegigMitFruehstueck = new Abwesenheit();
    final Abwesenheit tag12GanztaegigOhneFruehstueck = new Abwesenheit();
    final Abwesenheit tag13AbreiseOhneFruehstueck = new Abwesenheit();
    final Abwesenheit tag14Anreise = new Abwesenheit();
    final Abwesenheit tag15AbreiseMitFruehstueck = new Abwesenheit();
    final Abwesenheit tag15WeitereAbwesenheitUeberAchtStunden = new Abwesenheit();
    final Abwesenheit tag20AbwesenheitUnterAchtStunden = new Abwesenheit();
    final Abwesenheit tag21AbwesenheitUeberAchtStundenOhneUebernachtung = new Abwesenheit();

    tag1Anreise.setTagVon(LocalDate.of(2018, 5, 1));
    tag1Anreise.setUhrzeitVon(LocalTime.of(20, 0));
    tag1Anreise.setUhrzeitBis(LocalTime.MAX);
    tag1Anreise.setMitUebernachtung(true);
    tag1Anreise.setMitFruehstueck(false);
    tag1Anreise.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag2AbreiseMitFruehstueck.setTagVon(LocalDate.of(2018, 5, 2));
    tag2AbreiseMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag2AbreiseMitFruehstueck.setUhrzeitBis(LocalTime.of(11, 0));
    tag2AbreiseMitFruehstueck.setMitUebernachtung(false);
    tag2AbreiseMitFruehstueck.setMitFruehstueck(true);
    tag2AbreiseMitFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag10Anreise.setTagVon(LocalDate.of(2018, 5, 10));
    tag10Anreise.setUhrzeitVon(LocalTime.of(22, 45));
    tag10Anreise.setUhrzeitBis(LocalTime.MAX);
    tag10Anreise.setMitUebernachtung(true);
    tag10Anreise.setMitFruehstueck(false);
    tag10Anreise.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag11GanztaegigMitFruehstueck.setTagVon(LocalDate.of(2018, 5, 11));
    tag11GanztaegigMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag11GanztaegigMitFruehstueck.setUhrzeitBis(LocalTime.MAX);
    tag11GanztaegigMitFruehstueck.setMitUebernachtung(true);
    tag11GanztaegigMitFruehstueck.setMitFruehstueck(true);
    tag11GanztaegigMitFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag12GanztaegigOhneFruehstueck.setTagVon(LocalDate.of(2018, 5, 12));
    tag12GanztaegigOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag12GanztaegigOhneFruehstueck.setUhrzeitBis(LocalTime.MAX);
    tag12GanztaegigOhneFruehstueck.setMitUebernachtung(true);
    tag12GanztaegigOhneFruehstueck.setMitFruehstueck(false);
    tag12GanztaegigOhneFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag13AbreiseOhneFruehstueck.setTagVon(LocalDate.of(2018, 5, 13));
    tag13AbreiseOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag13AbreiseOhneFruehstueck.setUhrzeitBis(LocalTime.of(12, 0));
    tag13AbreiseOhneFruehstueck.setMitUebernachtung(false);
    tag13AbreiseOhneFruehstueck.setMitFruehstueck(false);
    tag13AbreiseOhneFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag14Anreise.setTagVon(LocalDate.of(2018, 5, 14));
    tag14Anreise.setUhrzeitVon(LocalTime.of(18, 15));
    tag14Anreise.setUhrzeitBis(LocalTime.MAX);
    tag14Anreise.setMitUebernachtung(true);
    tag14Anreise.setMitFruehstueck(false);
    tag14Anreise.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag15AbreiseMitFruehstueck.setTagVon(LocalDate.of(2018, 5, 15));
    tag15AbreiseMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag15AbreiseMitFruehstueck.setUhrzeitBis(LocalTime.of(7, 0));
    tag15AbreiseMitFruehstueck.setMitUebernachtung(false);
    tag15AbreiseMitFruehstueck.setMitFruehstueck(true);
    tag15AbreiseMitFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag15WeitereAbwesenheitUeberAchtStunden.setTagVon(LocalDate.of(2018, 5, 15));
    tag15WeitereAbwesenheitUeberAchtStunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag15WeitereAbwesenheitUeberAchtStunden.setUhrzeitBis(LocalTime.of(18, 0));
    tag15WeitereAbwesenheitUeberAchtStunden.setMitUebernachtung(false);
    tag15WeitereAbwesenheitUeberAchtStunden.setMitFruehstueck(false);
    tag15WeitereAbwesenheitUeberAchtStunden.setProjektId(
        projektService.projektByProjektnummer("1809").getId());

    tag20AbwesenheitUnterAchtStunden.setTagVon(LocalDate.of(2018, 5, 20));
    tag20AbwesenheitUnterAchtStunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag20AbwesenheitUnterAchtStunden.setUhrzeitBis(LocalTime.of(13, 0));
    tag20AbwesenheitUnterAchtStunden.setMitUebernachtung(false);
    tag20AbwesenheitUnterAchtStunden.setMitFruehstueck(false);
    tag20AbwesenheitUnterAchtStunden.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setTagVon(LocalDate.of(2018, 5, 21));
    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setUhrzeitVon(LocalTime.of(8, 0));
    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setUhrzeitBis(LocalTime.of(23, 30));
    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setMitUebernachtung(false);
    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setMitFruehstueck(false);
    tag21AbwesenheitUeberAchtStundenOhneUebernachtung.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(tag1Anreise);
    abwesenheiten.add(tag2AbreiseMitFruehstueck);
    abwesenheiten.add(tag10Anreise);
    abwesenheiten.add(tag11GanztaegigMitFruehstueck);
    abwesenheiten.add(tag12GanztaegigOhneFruehstueck);
    abwesenheiten.add(tag13AbreiseOhneFruehstueck);
    abwesenheiten.add(tag14Anreise);
    abwesenheiten.add(tag15AbreiseMitFruehstueck);
    abwesenheiten.add(tag15WeitereAbwesenheitUeberAchtStunden);
    abwesenheiten.add(tag20AbwesenheitUnterAchtStunden);
    abwesenheiten.add(tag21AbwesenheitUeberAchtStundenOhneUebernachtung);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(10, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("4.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(1).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && gesetzlicheSpesen.get(1).getGesetzlicheSpesen().compareTo(new BigDecimal("7.20"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(2).getAnzahlStunden().compareTo(new BigDecimal("1.25")) == 0
            && gesetzlicheSpesen.get(2).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(3).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(3).getGesetzlicheSpesen().compareTo(new BigDecimal("19.20"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(4).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(4).getGesetzlicheSpesen().compareTo(new BigDecimal("24.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(5).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && gesetzlicheSpesen.get(5).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(6).getAnzahlStunden().compareTo(new BigDecimal("5.75")) == 0
            && gesetzlicheSpesen.get(6).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(7).getAnzahlStunden().compareTo(new BigDecimal("17.00")) == 0
            && gesetzlicheSpesen.get(7).getGesetzlicheSpesen().compareTo(new BigDecimal("7.20"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(8).getAnzahlStunden().compareTo(new BigDecimal("5.00")) == 0
            && gesetzlicheSpesen.get(8).getGesetzlicheSpesen().compareTo(new BigDecimal("0.00"))
            == 0);
    assertTrue(
        gesetzlicheSpesen.get(9).getAnzahlStunden().compareTo(new BigDecimal("15.50")) == 0
            && gesetzlicheSpesen.get(9).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testGesetzlicheSpesenMitMehrfachenAbwesenheitenAmSelbenTag() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitEins = new Abwesenheit();
    final Abwesenheit abwesenheitZwei = new Abwesenheit();
    final Abwesenheit abwesenheitDrei = new Abwesenheit();

    abwesenheitEins.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitEins.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitEins.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitEins.setMitUebernachtung(false);
    abwesenheitEins.setMitFruehstueck(false);
    abwesenheitEins.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitZwei.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitZwei.setUhrzeitVon(LocalTime.of(13, 0));
    abwesenheitZwei.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitZwei.setMitUebernachtung(false);
    abwesenheitZwei.setMitFruehstueck(false);
    abwesenheitZwei.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitDrei.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDrei.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitDrei.setUhrzeitBis(LocalTime.of(22, 0));
    abwesenheitDrei.setMitUebernachtung(false);
    abwesenheitDrei.setMitFruehstueck(false);
    abwesenheitDrei.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitEins);
    abwesenheiten.add(abwesenheitZwei);
    abwesenheiten.add(abwesenheitDrei);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testGesetzlicheSpesenMitMehrfachenAbwesenheitenAmSelbenTagSeminarUndNormal() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(13, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(20, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitSeminar);
    abwesenheiten.add(abwesenheitNormal);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(gesetzlicheSpesen.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void testGesetzlicheSpesenMitMehrfachenAbwesenheitenAmSelbenTagDreimonatsregelUndNormal() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(21, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setDreiMonatsRegelAktiv(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitDreimonatsregel);
    abwesenheiten.add(abwesenheitNormal);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(gesetzlicheSpesen.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void testGesetzlicheSpesenMitMehrfachenAbwesenheitenAmSelbenTagDreimonatsregelUndSeminar() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(21, 0));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setDreiMonatsRegelAktiv(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheiten.add(abwesenheitDreimonatsregel);
    abwesenheiten.add(abwesenheitSeminar);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(gesetzlicheSpesen.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void
  testGesetzlicheSpesenMitMehrfachenAbwesenheitenAmSelbenTagNormalUndDreimonatsregelUndSeminar() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(12, 30));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(15, 30));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(16, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(20, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    abwesenheiten.add(abwesenheitNormal);
    abwesenheiten.add(abwesenheitSeminar);
    abwesenheiten.add(abwesenheitDreimonatsregel);

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("12.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(gesetzlicheSpesen.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testGesetzlicheSpesenFruestueckMittagessenAbendessen() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheit = new Abwesenheit();

    abwesenheit.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheit.setUhrzeitVon(LocalTime.of(0, 0));
    abwesenheit.setUhrzeitBis(LocalTime.MAX);
    abwesenheit.setMitFruehstueck(true);
    abwesenheit.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheit);

    List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen =
        spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("19.20"))
            == 0);

    abwesenheit.setMitMittagessen(true);

    gesetzlicheSpesen = spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("9.60"))
            == 0);

    abwesenheit.setMitAbendessen(true);

    gesetzlicheSpesen = spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("0.00"))
            == 0);

    abwesenheit.setMitFruehstueck(false);

    gesetzlicheSpesen = spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("4.80"))
            == 0);

    abwesenheit.setMitFruehstueck(true);
    abwesenheit.setMitMittagessen(false);

    gesetzlicheSpesen = spesenUndZuschlagsberechnung.berechneGesetzlicheSpesen(abwesenheiten);

    assertEquals(1, gesetzlicheSpesen.size());
    assertTrue(
        gesetzlicheSpesen.get(0).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && gesetzlicheSpesen.get(0).getGesetzlicheSpesen().compareTo(new BigDecimal("9.60"))
            == 0);
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testBerechneViadeeZuschlaege() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit tag1AnreiseSeminar = new Abwesenheit();
    final Abwesenheit tag2AbreiseMitFruehstueckSeminar = new Abwesenheit();
    final Abwesenheit tag10Anreise = new Abwesenheit();
    final Abwesenheit tag11GanztaegigMitFruehstueck = new Abwesenheit();
    final Abwesenheit tag12GanztaegigOhneFruehstueck = new Abwesenheit();
    final Abwesenheit tag13AbreiseOhneFruehstueck = new Abwesenheit();
    final Abwesenheit tag14Anreise = new Abwesenheit();
    final Abwesenheit tag15AbreiseMitFruehstueck = new Abwesenheit();
    final Abwesenheit tag15WeitereAbwesenheitUeberAchtStunden = new Abwesenheit();
    final Abwesenheit tag20AbwesenheitUnterSechsStunden = new Abwesenheit();
    final Abwesenheit tag21AbwesenheitUeber6Unter10Stunden = new Abwesenheit();
    final Abwesenheit tag23AbwesenheitUeber10Stunden = new Abwesenheit();
    final Abwesenheit tag24MitFruehstueck = new Abwesenheit();

    tag1AnreiseSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    tag1AnreiseSeminar.setUhrzeitVon(LocalTime.of(20, 0));
    tag1AnreiseSeminar.setUhrzeitBis(LocalTime.MAX);
    tag1AnreiseSeminar.setMitUebernachtung(true);
    tag1AnreiseSeminar.setMitFruehstueck(false);
    tag1AnreiseSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    tag2AbreiseMitFruehstueckSeminar.setTagVon(LocalDate.of(2018, 5, 2));
    tag2AbreiseMitFruehstueckSeminar.setUhrzeitVon(LocalTime.of(0, 0));
    tag2AbreiseMitFruehstueckSeminar.setUhrzeitBis(LocalTime.of(11, 0));
    tag2AbreiseMitFruehstueckSeminar.setMitUebernachtung(false);
    tag2AbreiseMitFruehstueckSeminar.setMitFruehstueck(true);
    tag2AbreiseMitFruehstueckSeminar.setProjektId(
        projektService.projektByProjektnummer("9002").getId());

    tag10Anreise.setTagVon(LocalDate.of(2018, 5, 10));
    tag10Anreise.setUhrzeitVon(LocalTime.of(22, 45));
    tag10Anreise.setUhrzeitBis(LocalTime.MAX);
    tag10Anreise.setMitUebernachtung(true);
    tag10Anreise.setMitFruehstueck(false);
    tag10Anreise.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag11GanztaegigMitFruehstueck.setTagVon(LocalDate.of(2018, 5, 11));
    tag11GanztaegigMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag11GanztaegigMitFruehstueck.setUhrzeitBis(LocalTime.MAX);
    tag11GanztaegigMitFruehstueck.setMitUebernachtung(true);
    tag11GanztaegigMitFruehstueck.setMitFruehstueck(true);
    tag11GanztaegigMitFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag12GanztaegigOhneFruehstueck.setTagVon(LocalDate.of(2018, 5, 12));
    tag12GanztaegigOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag12GanztaegigOhneFruehstueck.setUhrzeitBis(LocalTime.MAX);
    tag12GanztaegigOhneFruehstueck.setMitUebernachtung(true);
    tag12GanztaegigOhneFruehstueck.setMitFruehstueck(false);
    tag12GanztaegigOhneFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag13AbreiseOhneFruehstueck.setTagVon(LocalDate.of(2018, 5, 13));
    tag13AbreiseOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag13AbreiseOhneFruehstueck.setUhrzeitBis(LocalTime.of(12, 0));
    tag13AbreiseOhneFruehstueck.setMitUebernachtung(false);
    tag13AbreiseOhneFruehstueck.setMitFruehstueck(false);
    tag13AbreiseOhneFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag14Anreise.setTagVon(LocalDate.of(2018, 5, 14));
    tag14Anreise.setUhrzeitVon(LocalTime.of(18, 15));
    tag14Anreise.setUhrzeitBis(LocalTime.MAX);
    tag14Anreise.setMitUebernachtung(true);
    tag14Anreise.setMitFruehstueck(false);
    tag14Anreise.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag15AbreiseMitFruehstueck.setTagVon(LocalDate.of(2018, 5, 15));
    tag15AbreiseMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag15AbreiseMitFruehstueck.setUhrzeitBis(LocalTime.of(7, 0));
    tag15AbreiseMitFruehstueck.setMitUebernachtung(false);
    tag15AbreiseMitFruehstueck.setMitFruehstueck(true);
    tag15AbreiseMitFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    tag15WeitereAbwesenheitUeberAchtStunden.setTagVon(LocalDate.of(2018, 5, 15));
    tag15WeitereAbwesenheitUeberAchtStunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag15WeitereAbwesenheitUeberAchtStunden.setUhrzeitBis(LocalTime.of(18, 0));
    tag15WeitereAbwesenheitUeberAchtStunden.setMitUebernachtung(false);
    tag15WeitereAbwesenheitUeberAchtStunden.setMitFruehstueck(false);
    tag15WeitereAbwesenheitUeberAchtStunden.setProjektId(
        projektService.projektByProjektnummer("1809").getId());

    tag20AbwesenheitUnterSechsStunden.setTagVon(LocalDate.of(2018, 5, 20));
    tag20AbwesenheitUnterSechsStunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag20AbwesenheitUnterSechsStunden.setUhrzeitBis(LocalTime.of(13, 0));
    tag20AbwesenheitUnterSechsStunden.setMitUebernachtung(false);
    tag20AbwesenheitUnterSechsStunden.setMitFruehstueck(false);
    tag20AbwesenheitUnterSechsStunden.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag21AbwesenheitUeber6Unter10Stunden.setTagVon(LocalDate.of(2018, 5, 21));
    tag21AbwesenheitUeber6Unter10Stunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag21AbwesenheitUeber6Unter10Stunden.setUhrzeitBis(LocalTime.of(16, 45));
    tag21AbwesenheitUeber6Unter10Stunden.setMitUebernachtung(false);
    tag21AbwesenheitUeber6Unter10Stunden.setMitFruehstueck(false);
    tag21AbwesenheitUeber6Unter10Stunden.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag23AbwesenheitUeber10Stunden.setTagVon(LocalDate.of(2018, 5, 23));
    tag23AbwesenheitUeber10Stunden.setUhrzeitVon(LocalTime.of(8, 0));
    tag23AbwesenheitUeber10Stunden.setUhrzeitBis(LocalTime.of(18, 15));
    tag23AbwesenheitUeber10Stunden.setMitUebernachtung(false);
    tag23AbwesenheitUeber10Stunden.setMitFruehstueck(false);
    tag23AbwesenheitUeber10Stunden.setProjektId(
        projektService.projektByProjektnummer("9155").getId());

    tag24MitFruehstueck.setTagVon(LocalDate.of(2018, 5, 24));
    tag24MitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    tag24MitFruehstueck.setUhrzeitBis(LocalTime.of(7, 0));
    tag24MitFruehstueck.setMitUebernachtung(false);
    tag24MitFruehstueck.setMitFruehstueck(true);
    tag24MitFruehstueck.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(tag1AnreiseSeminar);
    abwesenheiten.add(tag2AbreiseMitFruehstueckSeminar);
    abwesenheiten.add(tag10Anreise);
    abwesenheiten.add(tag11GanztaegigMitFruehstueck);
    abwesenheiten.add(tag12GanztaegigOhneFruehstueck);
    abwesenheiten.add(tag13AbreiseOhneFruehstueck);
    abwesenheiten.add(tag14Anreise);
    abwesenheiten.add(tag15AbreiseMitFruehstueck);
    abwesenheiten.add(tag15WeitereAbwesenheitUeberAchtStunden);
    abwesenheiten.add(tag20AbwesenheitUnterSechsStunden);
    abwesenheiten.add(tag21AbwesenheitUeber6Unter10Stunden);
    abwesenheiten.add(tag23AbwesenheitUeber10Stunden);
    abwesenheiten.add(tag24MitFruehstueck);

    final List<ErgebnisBerechnungViadeeZuschlaege> zuschlaege =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(12, zuschlaege.size());
    assertTrue(
        zuschlaege.get(0).getAnzahlStunden().compareTo(new BigDecimal("4.00")) == 0
            && zuschlaege.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("30.00")) == 0);
    assertTrue(
        zuschlaege.get(1).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && zuschlaege.get(1).getViadeeZuschlaege().compareTo(new BigDecimal("30.80")) == 0);
    assertTrue(
        zuschlaege.get(2).getAnzahlStunden().compareTo(new BigDecimal("1.25")) == 0
            && zuschlaege.get(2).getViadeeZuschlaege().compareTo(new BigDecimal("30.00")) == 0);
    assertTrue(
        zuschlaege.get(3).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && zuschlaege.get(3).getViadeeZuschlaege().compareTo(new BigDecimal("34.80")) == 0);
    assertTrue(
        zuschlaege.get(4).getAnzahlStunden().compareTo(new BigDecimal("24.00")) == 0
            && zuschlaege.get(4).getViadeeZuschlaege().compareTo(new BigDecimal("30.00")) == 0);
    assertTrue(
        zuschlaege.get(5).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && zuschlaege.get(5).getViadeeZuschlaege().compareTo(new BigDecimal("26.00")) == 0);
    assertTrue(
        zuschlaege.get(6).getAnzahlStunden().compareTo(new BigDecimal("5.75")) == 0
            && zuschlaege.get(6).getViadeeZuschlaege().compareTo(new BigDecimal("30.00")) == 0);
    assertTrue(
        zuschlaege.get(7).getAnzahlStunden().compareTo(new BigDecimal("17.00")) == 0
            && zuschlaege.get(7).getViadeeZuschlaege().compareTo(new BigDecimal("30.80")) == 0);
    assertTrue(
        zuschlaege.get(8).getAnzahlStunden().compareTo(new BigDecimal("5.00")) == 0
            && zuschlaege.get(8).getViadeeZuschlaege().compareTo(new BigDecimal("0.00")) == 0);
    assertTrue(
        zuschlaege.get(9).getAnzahlStunden().compareTo(new BigDecimal("8.75")) == 0
            && zuschlaege.get(9).getViadeeZuschlaege().compareTo(new BigDecimal("11.00")) == 0);
    assertTrue(
        zuschlaege.get(10).getAnzahlStunden().compareTo(new BigDecimal("10.25")) == 0
            && zuschlaege.get(10).getViadeeZuschlaege().compareTo(new BigDecimal("26.00")) == 0);
    assertTrue(
        zuschlaege.get(11).getAnzahlStunden().compareTo(new BigDecimal("7.00")) == 0
            && zuschlaege.get(11).getViadeeZuschlaege().compareTo(new BigDecimal("15.80")) == 0);
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testViadeeZuschlaegeMitMehrfachenAbwesenheitenAmSelbenTag() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitEins = new Abwesenheit();
    final Abwesenheit abwesenheitZwei = new Abwesenheit();
    final Abwesenheit abwesenheitDrei = new Abwesenheit();

    abwesenheitEins.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitEins.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitEins.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitEins.setMitUebernachtung(false);
    abwesenheitEins.setMitFruehstueck(false);
    abwesenheitEins.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitZwei.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitZwei.setUhrzeitVon(LocalTime.of(13, 0));
    abwesenheitZwei.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitZwei.setMitUebernachtung(false);
    abwesenheitZwei.setMitFruehstueck(false);
    abwesenheitZwei.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitDrei.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDrei.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitDrei.setUhrzeitBis(LocalTime.of(22, 0));
    abwesenheitDrei.setMitUebernachtung(false);
    abwesenheitDrei.setMitFruehstueck(false);
    abwesenheitDrei.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitEins);
    abwesenheiten.add(abwesenheitZwei);
    abwesenheiten.add(abwesenheitDrei);

    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlaege =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(1, viadeeZuschlaege.size());
    assertTrue(
        viadeeZuschlaege.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && viadeeZuschlaege.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("26.00"))
            == 0);
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeTestdaten.sql"})
  void testViadeeZuschlaegeMitMehrfachenAbwesenheitenAmSelbenTagSeminarUndNormal() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(13, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(20, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitSeminar);
    abwesenheiten.add(abwesenheitNormal);

    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlaege =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(1, viadeeZuschlaege.size());
    assertTrue(
        viadeeZuschlaege.get(0).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && viadeeZuschlaege.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("26.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(viadeeZuschlaege.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void testViadeeZuschlaegeMitMehrfachenAbwesenheitenAmSelbenTagDreimonatsregelUndNormal() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(21, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setDreiMonatsRegelAktiv(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheiten.add(abwesenheitDreimonatsregel);
    abwesenheiten.add(abwesenheitNormal);

    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlage =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(1, viadeeZuschlage.size());
    assertTrue(
        viadeeZuschlage.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && viadeeZuschlage.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("26.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(viadeeZuschlage.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void testViadeeZuschlaegeMitMehrfachenAbwesenheitenAmSelbenTagDreimonatsregelUndSeminar() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(17, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(18, 0));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(21, 0));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setDreiMonatsRegelAktiv(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheiten.add(abwesenheitDreimonatsregel);
    abwesenheiten.add(abwesenheitSeminar);

    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlaege =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(1, viadeeZuschlaege.size());
    assertTrue(
        viadeeZuschlaege.get(0).getAnzahlStunden().compareTo(new BigDecimal("12.00")) == 0
            && viadeeZuschlaege.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("26.00"))
            == 0);
    assertTrue(
        projektService
            .projektById(viadeeZuschlaege.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void
  testViadeeZuschlaegeMitMehrfachenAbwesenheitenAmSelbenTagNormalUndDreimonatsregelUndSeminar() {

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();
    final Abwesenheit abwesenheitNormal = new Abwesenheit();
    final Abwesenheit abwesenheitSeminar = new Abwesenheit();
    final Abwesenheit abwesenheitDreimonatsregel = new Abwesenheit();

    abwesenheitNormal.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitNormal.setUhrzeitVon(LocalTime.of(8, 0));
    abwesenheitNormal.setUhrzeitBis(LocalTime.of(12, 0));
    abwesenheitNormal.setMitUebernachtung(false);
    abwesenheitNormal.setMitFruehstueck(false);
    abwesenheitNormal.setProjektId(projektService.projektByProjektnummer("9155").getId());

    abwesenheitSeminar.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitSeminar.setUhrzeitVon(LocalTime.of(12, 30));
    abwesenheitSeminar.setUhrzeitBis(LocalTime.of(15, 30));
    abwesenheitSeminar.setMitUebernachtung(false);
    abwesenheitSeminar.setMitFruehstueck(false);
    abwesenheitSeminar.setProjektId(projektService.projektByProjektnummer("9002").getId());

    abwesenheitDreimonatsregel.setTagVon(LocalDate.of(2018, 5, 1));
    abwesenheitDreimonatsregel.setUhrzeitVon(LocalTime.of(16, 0));
    abwesenheitDreimonatsregel.setUhrzeitBis(LocalTime.of(20, 0));
    abwesenheitDreimonatsregel.setMitUebernachtung(false);
    abwesenheitDreimonatsregel.setMitFruehstueck(false);
    abwesenheitDreimonatsregel.setDreiMonatsRegelAktiv(true);
    abwesenheitDreimonatsregel.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    abwesenheiten.add(abwesenheitNormal);
    abwesenheiten.add(abwesenheitSeminar);
    abwesenheiten.add(abwesenheitDreimonatsregel);

    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlaege =
        spesenUndZuschlagsberechnung.berechneViadeeZuschlaege(abwesenheiten);

    assertEquals(1, viadeeZuschlaege.size());
    assertTrue(
        viadeeZuschlaege.get(0).getAnzahlStunden().compareTo(new BigDecimal("11.00")) == 0
            && viadeeZuschlaege.get(0).getViadeeZuschlaege().compareTo(new BigDecimal("26.00"))
            == 0);

    assertTrue(
        projektService
            .projektById(viadeeZuschlaege.get(0).getAbwesenheit().getProjektId())
            .getProjektnummer()
            .equals("9155"));
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(
      scripts = {
          "file:src/test/resources/testdaten/services/abrechnung/spesenUndZuschlaegeMitDreiMonatsregelTestdaten.sql"
      })
  void testBerechneDreiMonatsRegel() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();

    final Arbeitsnachweis anw01SchlussDerDreiMonatsregelNach28Tagen = new Arbeitsnachweis();

    anw01SchlussDerDreiMonatsregelNach28Tagen.setJahr(2015);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setMonat(1);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setMitarbeiterId(mitarbeiter.getId());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setSmartphoneEigen(EIGEN.value());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setStatusId(ERFASST.value());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setStellenfaktor(ONE);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setZuletztGeaendertVon("TsT");
    anw01SchlussDerDreiMonatsregelNach28Tagen.setZuletztGeaendertAm(LocalDateTime.now());

    arbeitsnachweisRepository.save(anw01SchlussDerDreiMonatsregelNach28Tagen);

    final List<Abwesenheit> abwesenheiten = new ArrayList<>();

    final Abwesenheit anw01Tag1Anreise = new Abwesenheit();
    final Abwesenheit anw01Tag2GanztaegigMitFruehstueck = new Abwesenheit();
    final Abwesenheit anw01Tag3AbreiseOhneFruehstueck = new Abwesenheit();

    anw01Tag1Anreise.setArbeitsstaette("Köln");
    anw01Tag1Anreise.setTagVon(LocalDate.of(2015, 1, 1));
    anw01Tag1Anreise.setUhrzeitVon(LocalTime.of(20, 0));
    anw01Tag1Anreise.setUhrzeitBis(LocalTime.MAX);
    anw01Tag1Anreise.setMitUebernachtung(true);
    anw01Tag1Anreise.setMitFruehstueck(false);
    anw01Tag1Anreise.setArbeitsnachweisId(anw01SchlussDerDreiMonatsregelNach28Tagen.getId());
    anw01Tag1Anreise.setProjektId(projektService.projektByProjektnummer("999999").getId());

    anw01Tag2GanztaegigMitFruehstueck.setArbeitsstaette("Köln");
    anw01Tag2GanztaegigMitFruehstueck.setTagVon(LocalDate.of(2015, 1, 2));
    anw01Tag2GanztaegigMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw01Tag2GanztaegigMitFruehstueck.setUhrzeitBis(LocalTime.MAX);
    anw01Tag2GanztaegigMitFruehstueck.setMitUebernachtung(true);
    anw01Tag2GanztaegigMitFruehstueck.setMitFruehstueck(true);
    anw01Tag2GanztaegigMitFruehstueck.setArbeitsnachweisId(
        anw01SchlussDerDreiMonatsregelNach28Tagen.getId());
    anw01Tag2GanztaegigMitFruehstueck.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    anw01Tag3AbreiseOhneFruehstueck.setArbeitsstaette("Köln");
    anw01Tag3AbreiseOhneFruehstueck.setTagVon(LocalDate.of(2015, 1, 3));
    anw01Tag3AbreiseOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw01Tag3AbreiseOhneFruehstueck.setUhrzeitBis(LocalTime.of(11, 0));
    anw01Tag3AbreiseOhneFruehstueck.setMitUebernachtung(false);
    anw01Tag3AbreiseOhneFruehstueck.setMitFruehstueck(false);
    anw01Tag3AbreiseOhneFruehstueck.setArbeitsnachweisId(
        anw01SchlussDerDreiMonatsregelNach28Tagen.getId());
    anw01Tag3AbreiseOhneFruehstueck.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    final List<Abwesenheit> abwesenheitenAnw01 = new ArrayList<>();
    abwesenheitenAnw01.add(anw01Tag1Anreise);
    abwesenheitenAnw01.add(anw01Tag2GanztaegigMitFruehstueck);
    abwesenheitenAnw01.add(anw01Tag3AbreiseOhneFruehstueck);

    abwesenheiten.addAll(abwesenheitenAnw01);

    final Arbeitsnachweis anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate =
        new Arbeitsnachweis();

    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setJahr(2015);
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setMonat(2);
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setMitarbeiterId(mitarbeiter.getId());
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setSmartphoneEigen(EIGEN.value());
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setStatusId(ERFASST.value());
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setStellenfaktor(ONE);
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertVon("TsT");
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertAm(LocalDateTime.now());

    arbeitsnachweisRepository.save(
        anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate);

    final Abwesenheit anw02Tag11Anreise = new Abwesenheit();
    final Abwesenheit anw02Tag12GanztaegigMitFruehstueck = new Abwesenheit();
    final Abwesenheit anw02Tag13AbreiseOhneFruehstueck = new Abwesenheit();

    anw02Tag11Anreise.setArbeitsstaette("Köln");
    anw02Tag11Anreise.setTagVon(LocalDate.of(2015, 2, 11));
    anw02Tag11Anreise.setUhrzeitVon(LocalTime.of(20, 0));
    anw02Tag11Anreise.setUhrzeitBis(LocalTime.MAX);
    anw02Tag11Anreise.setMitUebernachtung(true);
    anw02Tag11Anreise.setMitFruehstueck(false);
    anw02Tag11Anreise.setArbeitsnachweisId(
        anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.getId());
    anw02Tag11Anreise.setProjektId(projektService.projektByProjektnummer("9999991").getId());

    anw02Tag12GanztaegigMitFruehstueck.setArbeitsstaette("Köln");
    anw02Tag12GanztaegigMitFruehstueck.setTagVon(LocalDate.of(2015, 2, 12));
    anw02Tag12GanztaegigMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw02Tag12GanztaegigMitFruehstueck.setUhrzeitBis(LocalTime.MAX);
    anw02Tag12GanztaegigMitFruehstueck.setMitUebernachtung(true);
    anw02Tag12GanztaegigMitFruehstueck.setMitFruehstueck(true);
    anw02Tag12GanztaegigMitFruehstueck.setArbeitsnachweisId(
        anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.getId());
    anw02Tag12GanztaegigMitFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9999992").getId());

    anw02Tag13AbreiseOhneFruehstueck.setArbeitsstaette("Köln");
    anw02Tag13AbreiseOhneFruehstueck.setTagVon(LocalDate.of(2015, 2, 13));
    anw02Tag13AbreiseOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw02Tag13AbreiseOhneFruehstueck.setUhrzeitBis(LocalTime.of(11, 0));
    anw02Tag13AbreiseOhneFruehstueck.setMitUebernachtung(false);
    anw02Tag13AbreiseOhneFruehstueck.setMitFruehstueck(false);
    anw02Tag13AbreiseOhneFruehstueck.setArbeitsnachweisId(
        anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.getId());
    anw02Tag13AbreiseOhneFruehstueck.setProjektId(
        projektService.projektByProjektnummer("9999991").getId());

    final List<Abwesenheit> abwesenheitenAnw02 = new ArrayList<>();
    abwesenheitenAnw02.add(anw02Tag11Anreise);
    abwesenheitenAnw02.add(anw02Tag12GanztaegigMitFruehstueck);
    abwesenheitenAnw02.add(anw02Tag13AbreiseOhneFruehstueck);

    abwesenheiten.addAll(abwesenheitenAnw02);

    final Arbeitsnachweis anw03ZweiMonateBeimGleichenKundenUeberZweiMonate = new Arbeitsnachweis();

    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setJahr(2015);
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setMonat(3);
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setMitarbeiterId(mitarbeiter.getId());
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setSmartphoneEigen(EIGEN.value());
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setStatusId(ERFASST.value());
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setStellenfaktor(ONE);
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertVon("TsT");
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertAm(LocalDateTime.now());

    arbeitsnachweisRepository.save(
        anw03ZweiMonateBeimGleichenKundenUeberZweiMonate);

    final Abwesenheit anw03Tag11 = new Abwesenheit();

    anw03Tag11.setArbeitsstaette("Köln");
    anw03Tag11.setTagVon(LocalDate.of(2015, 3, 13));
    anw03Tag11.setUhrzeitVon(LocalTime.of(07, 0));
    anw03Tag11.setUhrzeitBis(LocalTime.of(20, 0));
    anw03Tag11.setMitUebernachtung(false);
    anw03Tag11.setMitFruehstueck(false);
    anw03Tag11.setArbeitsnachweisId(anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.getId());
    anw03Tag11.setProjektId(projektService.projektByProjektnummer("9999992").getId());

    final List<Abwesenheit> abwesenheitenAnw03 = new ArrayList<>();
    abwesenheitenAnw03.add(anw03Tag11);

    abwesenheiten.addAll(abwesenheitenAnw03);

    LocalDate startDatum =
        LocalDate.of(
            LocalDate.now().minusMonths(1).getYear(),
            LocalDate.now().minusMonths(1).getMonthValue(),
            20);
    while (startDatum.getDayOfWeek().ordinal() >= 5) {
      startDatum = startDatum.plusDays(1);
    }

    final Arbeitsnachweis anw04ArbeitsnachweisMitOffenerDreiMonatsRegel = new Arbeitsnachweis();

    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setJahr(startDatum.getYear());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setMonat(startDatum.getMonthValue());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setMitarbeiterId(mitarbeiter.getId());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setSmartphoneEigen(EIGEN.value());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setStatusId(ERFASST.value());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setStellenfaktor(ONE);
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertVon("TsT");
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertAm(LocalDateTime.now());

    arbeitsnachweisRepository.save(
        anw04ArbeitsnachweisMitOffenerDreiMonatsRegel);

    final Abwesenheit anw04Tag1Anreise = new Abwesenheit();
    final Abwesenheit anw04Tag2GanztaegigMitFruehstueck = new Abwesenheit();
    final Abwesenheit anw04Tag3AbreiseOhneFruehstueck = new Abwesenheit();

    anw04Tag1Anreise.setArbeitsstaette("Köln");
    anw04Tag1Anreise.setTagVon(startDatum);
    anw04Tag1Anreise.setUhrzeitVon(LocalTime.of(20, 0));
    anw04Tag1Anreise.setUhrzeitBis(LocalTime.MAX);
    anw04Tag1Anreise.setMitUebernachtung(true);
    anw04Tag1Anreise.setMitFruehstueck(false);
    anw04Tag1Anreise.setArbeitsnachweisId(anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw04Tag1Anreise.setProjektId(projektService.projektByProjektnummer("999999").getId());

    anw04Tag2GanztaegigMitFruehstueck.setArbeitsstaette("Köln");
    anw04Tag2GanztaegigMitFruehstueck.setTagVon(startDatum.plusDays(1));
    anw04Tag2GanztaegigMitFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw04Tag2GanztaegigMitFruehstueck.setUhrzeitBis(LocalTime.MAX);
    anw04Tag2GanztaegigMitFruehstueck.setMitUebernachtung(true);
    anw04Tag2GanztaegigMitFruehstueck.setMitFruehstueck(true);
    anw04Tag2GanztaegigMitFruehstueck.setArbeitsnachweisId(
        anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw04Tag2GanztaegigMitFruehstueck.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    anw04Tag3AbreiseOhneFruehstueck.setArbeitsstaette("Köln");
    anw04Tag3AbreiseOhneFruehstueck.setTagVon(startDatum.plusDays(2));
    anw04Tag3AbreiseOhneFruehstueck.setUhrzeitVon(LocalTime.of(0, 0));
    anw04Tag3AbreiseOhneFruehstueck.setUhrzeitBis(LocalTime.of(11, 0));
    anw04Tag3AbreiseOhneFruehstueck.setMitUebernachtung(false);
    anw04Tag3AbreiseOhneFruehstueck.setMitFruehstueck(false);
    anw04Tag3AbreiseOhneFruehstueck.setArbeitsnachweisId(
        anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw04Tag3AbreiseOhneFruehstueck.setProjektId(
        projektService.projektByProjektnummer("999999").getId());

    final List<Abwesenheit> abwesenheitenAnw04 = new ArrayList<>();
    abwesenheitenAnw04.add(anw04Tag1Anreise);
    abwesenheitenAnw04.add(anw04Tag2GanztaegigMitFruehstueck);
    abwesenheitenAnw04.add(anw04Tag3AbreiseOhneFruehstueck);

    abwesenheiten.addAll(abwesenheitenAnw04);

    final Arbeitsnachweis anw05ArbeitsnachweisMitOffenerDreiMonatsRegel = new Arbeitsnachweis();

    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setJahr(startDatum.plusDays(28).getYear());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setMonat(startDatum.plusDays(28).getMonthValue());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setMitarbeiterId(mitarbeiter.getId());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setSmartphoneEigen(EIGEN.value());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setStatusId(ERFASST.value());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setStellenfaktor(ONE);
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertVon("TsT");
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertAm(LocalDateTime.now());

    arbeitsnachweisRepository.save(
        anw05ArbeitsnachweisMitOffenerDreiMonatsRegel);

    final Abwesenheit anw05Tag1 = new Abwesenheit();
    final Abwesenheit anw05Tag2 = new Abwesenheit();

    anw05Tag1.setArbeitsstaette("Köln");
    anw05Tag1.setTagVon(startDatum.plusDays(28));
    anw05Tag1.setUhrzeitVon(LocalTime.of(7, 0));
    anw05Tag1.setUhrzeitBis(LocalTime.of(19, 0));
    anw05Tag1.setMitUebernachtung(false);
    anw05Tag1.setMitFruehstueck(false);
    anw05Tag1.setArbeitsnachweisId(anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw05Tag1.setProjektId(projektService.projektByProjektnummer("999999").getId());

    anw05Tag2.setArbeitsstaette("Köln");
    anw05Tag2.setTagVon(startDatum.plusDays(31));
    anw05Tag2.setUhrzeitVon(LocalTime.of(7, 0));
    anw05Tag2.setUhrzeitBis(LocalTime.of(19, 0));
    anw05Tag2.setMitUebernachtung(false);
    anw05Tag2.setMitFruehstueck(false);
    anw05Tag2.setArbeitsnachweisId(anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw05Tag2.setProjektId(projektService.projektByProjektnummer("999999").getId());

    final Abwesenheit anw05Tag3 = new Abwesenheit();
    final Abwesenheit anw05Tag4 = new Abwesenheit();

    anw05Tag3.setArbeitsstaette("Köln");
    anw05Tag3.setTagVon(startDatum.plusDays(12));
    anw05Tag3.setUhrzeitVon(LocalTime.of(7, 0));
    anw05Tag3.setUhrzeitBis(LocalTime.of(19, 0));
    anw05Tag3.setMitUebernachtung(false);
    anw05Tag3.setMitFruehstueck(false);
    anw05Tag3.setArbeitsnachweisId(anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw05Tag3.setProjektId(projektService.projektByProjektnummer("9999991").getId());

    anw05Tag4.setArbeitsstaette("Köln");
    anw05Tag4.setTagVon(startDatum.plusDays(13));
    anw05Tag4.setUhrzeitVon(LocalTime.of(7, 0));
    anw05Tag4.setUhrzeitBis(LocalTime.of(19, 0));
    anw05Tag4.setMitUebernachtung(false);
    anw05Tag4.setMitFruehstueck(false);
    anw05Tag4.setArbeitsnachweisId(anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getId());
    anw05Tag4.setProjektId(projektService.projektByProjektnummer("9999991").getId());

    final List<Abwesenheit> abwesenheitenAnw05 = new ArrayList<>();
    abwesenheitenAnw05.add(anw05Tag1);
    abwesenheitenAnw05.add(anw05Tag2);
    abwesenheitenAnw05.add(anw05Tag3);
    abwesenheitenAnw05.add(anw05Tag4);

    abwesenheiten.addAll(abwesenheitenAnw05);

    final List<DreiMonatsRegel> berechneteDreiMonatsRegeln =
        dreiMonatsRegelBerechnung.berechneDreiMonatsRegeln(
            mitarbeiter,
            new ArrayList<>(),
            abwesenheiten,
            anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getJahr(),
            anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.getMonat());

    Collections.sort(berechneteDreiMonatsRegeln);

    assertEquals(3, berechneteDreiMonatsRegeln.size());
    assertEquals(berechneteDreiMonatsRegeln.get(0).getGueltigVon(), LocalDate.of(2015, 1, 1));
    assertEquals(berechneteDreiMonatsRegeln.get(0).getGueltigBis(), LocalDate.of(2015, 1, 31));
    assertEquals(berechneteDreiMonatsRegeln.get(1).getGueltigVon(), LocalDate.of(2015, 2, 11));
    assertEquals(berechneteDreiMonatsRegeln.get(1).getGueltigBis(), LocalDate.of(2015, 4, 10));
    assertEquals(berechneteDreiMonatsRegeln.get(2).getGueltigVon(), startDatum);
    assertNull(berechneteDreiMonatsRegeln.get(2).getGueltigBis());
  }
}
