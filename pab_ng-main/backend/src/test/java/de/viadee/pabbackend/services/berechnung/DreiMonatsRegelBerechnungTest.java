package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.SmartphoneBesitzKenner.EIGEN;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.enums.ArbeitsnachweisBearbeitungsstatus;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
public class DreiMonatsRegelBerechnungTest extends MsSqlTestContainersTest {

  @Autowired
  private MitarbeiterRepository mitarbeiterRepository;
  @Autowired
  private ArbeitsnachweisRepository arbeitsnachweisRepository;
  @Autowired
  private DreiMonatsRegelBerechnung dreiMonatsRegelBerechnung;
  @Autowired
  private ProjektService projektService;

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/abrechnung/dreiMonatsRegelTestdaten.sql"})
  public void testBerechneDreiMonatsRegel() {

    final Mitarbeiter mitarbeiter = mitarbeiterRepository.letzterMitarbeiter();

    final Arbeitsnachweis anw01SchlussDerDreiMonatsregelNach28Tagen = new Arbeitsnachweis();

    anw01SchlussDerDreiMonatsregelNach28Tagen.setJahr(2015);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setMonat(1);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setMitarbeiterId(mitarbeiter.getId());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setSmartphoneEigen(EIGEN.value());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setStatusId(
        ArbeitsnachweisBearbeitungsstatus.ERFASST.value());
    anw01SchlussDerDreiMonatsregelNach28Tagen.setStellenfaktor(BigDecimal.ONE);
    anw01SchlussDerDreiMonatsregelNach28Tagen.setZuletztGeaendertVon("Test");
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
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setStatusId(
        ArbeitsnachweisBearbeitungsstatus.ERFASST.value());
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setStellenfaktor(BigDecimal.ONE);
    anw02ZweiProjekteBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertVon("Test");
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
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setStatusId(
        ArbeitsnachweisBearbeitungsstatus.ERFASST.value());
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setStellenfaktor(BigDecimal.ONE);
    anw03ZweiMonateBeimGleichenKundenUeberZweiMonate.setZuletztGeaendertVon("Test");
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
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setStatusId(
        ArbeitsnachweisBearbeitungsstatus.ERFASST.value());
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setStellenfaktor(BigDecimal.ONE);
    anw04ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertVon("Test");
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
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setStatusId(
        ArbeitsnachweisBearbeitungsstatus.ERFASST.value());
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setStellenfaktor(BigDecimal.ONE);
    anw05ArbeitsnachweisMitOffenerDreiMonatsRegel.setZuletztGeaendertVon("Test");
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

    assertTrue(berechneteDreiMonatsRegeln.size() == 3);
    assertTrue(berechneteDreiMonatsRegeln.get(0).getGueltigVon().equals(LocalDate.of(2015, 1, 1)));
    assertTrue(berechneteDreiMonatsRegeln.get(0).getGueltigBis().equals(LocalDate.of(2015, 1, 31)));
    assertTrue(berechneteDreiMonatsRegeln.get(1).getGueltigVon().equals(LocalDate.of(2015, 2, 11)));
    assertTrue(berechneteDreiMonatsRegeln.get(1).getGueltigBis().equals(LocalDate.of(2015, 4, 10)));
    assertTrue(berechneteDreiMonatsRegeln.get(2).getGueltigVon().equals(startDatum));
    assertTrue(berechneteDreiMonatsRegeln.get(2).getGueltigBis() == null);
  }
}
