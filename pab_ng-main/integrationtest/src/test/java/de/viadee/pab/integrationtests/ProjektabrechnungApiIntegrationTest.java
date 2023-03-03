package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.ProjektabrechnungControllerApi;
import de.viadee.pab.client.api.model.ProjektAbrechnungsmonat;
import de.viadee.pab.client.api.model.Projektabrechnung;
import de.viadee.pab.client.api.model.ProjektabrechnungBerechneteLeistung;
import de.viadee.pab.client.api.model.ProjektabrechnungKorrekturbuchung;
import de.viadee.pab.client.api.model.ProjektabrechnungKostenLeistung;
import de.viadee.pab.client.api.model.ProjektabrechnungMitarbeiterPair;
import de.viadee.pab.client.api.model.ProjektabrechnungProjektzeit;
import de.viadee.pab.client.api.model.ProjektabrechnungReise;
import de.viadee.pab.client.api.model.ProjektabrechnungSonderarbeit;
import de.viadee.pab.client.api.model.ProjektabrechnungSonstige;
import de.viadee.pab.client.api.model.ProjektabrechnungUebersicht;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjektabrechnungApiIntegrationTest {

  private final Long projektId = 2182L;

  private ProjektabrechnungControllerApi projektabrechnungControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.projektabrechnungControllerApi = new ProjektabrechnungControllerApi(apiClient);
  }

  @Test
  public void testGetProjektabrechnungUebersicht() throws ApiException {
    LocalDate abWann = LocalDate.of(2018, 1, 1);
    LocalDate bisWann = LocalDate.now();

    List<ProjektabrechnungUebersicht> projektabrechnungUebersichts = this.projektabrechnungControllerApi.getProjektabrechnungUebersicht(
        abWann, bisWann);

    Assertions.assertNotNull(projektabrechnungUebersichts);
    Assertions.assertFalse(projektabrechnungUebersichts.isEmpty());
  }

  @Test
  public void testProjektabrechnungUebersichtMitFehlenderAbrechnung() throws ApiException {
    LocalDate abWann = LocalDate.now().minusMonths(1);
    LocalDate bisWann = LocalDate.now();

    List<ProjektabrechnungUebersicht> projektabrechnungUebersichts = this.projektabrechnungControllerApi.projektabrechnungUebersichtMitFehlenderAbrechnung(
        abWann, bisWann);

    Assertions.assertNotNull(projektabrechnungUebersichts);
    Assertions.assertFalse(projektabrechnungUebersichts.isEmpty());
  }

  @Test
  public void testGetProjektabrechnungMitarbeiterPairs() throws ApiException {
    LocalDate abWann = LocalDate.of(2018, 1, 1);
    LocalDate bisWann = LocalDate.now();

    List<ProjektabrechnungMitarbeiterPair> projektabrechnungMitarbeiterPairs = this.projektabrechnungControllerApi.getProjektabrechnungMitarbeiterPairs(
        abWann, bisWann);

    Assertions.assertNotNull(projektabrechnungMitarbeiterPairs);
    Assertions.assertFalse(projektabrechnungMitarbeiterPairs.isEmpty());
  }

  @Test
  public void testGetAlleAbrechnungsmonateByProjektId() throws ApiException {
    List<ProjektAbrechnungsmonat> abrechnungsmonate = this.projektabrechnungControllerApi.getAlleAbrechnungsmonateByProjektId(
        this.projektId);

    Assertions.assertNotNull(abrechnungsmonate);
    Assertions.assertFalse(abrechnungsmonate.isEmpty());
  }

  @Test
  public void testGetProjektabrechnungById() throws ApiException {
    Long projektabrechnungId = 478L;

    Projektabrechnung projektabrechnung = this.projektabrechnungControllerApi.getProjektabrechnungById(
        projektabrechnungId);

    Assertions.assertNotNull(projektabrechnung);
    Assertions.assertEquals(projektabrechnungId, projektabrechnung.getId());
  }

  @Test
  public void testGetKostenLeistungJeMitarbeiter() throws ApiException {
    List<ProjektabrechnungKostenLeistung> kostenLeistungJeMitarbeiter = this.projektabrechnungControllerApi.getKostenLeistungJeMitarbeiter(
        this.projektId);

    Assertions.assertNotNull(kostenLeistungJeMitarbeiter);
    Assertions.assertFalse(kostenLeistungJeMitarbeiter.isEmpty());
  }

  @Test
  public void testGetProjektabrechnungKorrekturbuchungByProjektId() throws ApiException {
    List<ProjektabrechnungKorrekturbuchung> korrekturbuchungen = this.projektabrechnungControllerApi.getProjektabrechnungKorrekturbuchungenByProjektId(
        projektId);

    Assertions.assertNotNull(korrekturbuchungen);
    Assertions.assertFalse(korrekturbuchungen.isEmpty());
  }

  @Test
  public void testGetBerechneteLeistungZuProjektabrechnung() throws ApiException {
    Long projektabrechnungId = 374L;

    List<ProjektabrechnungBerechneteLeistung> projektabrechnungBerechneteLeistungen = this.projektabrechnungControllerApi.getBerechneteLeistungenZuProjektabrechnung(
        projektabrechnungId);

    Assertions.assertNotNull(projektabrechnungBerechneteLeistungen);
    Assertions.assertFalse(projektabrechnungBerechneteLeistungen.isEmpty());
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungBerechneteLeistungen.get(0).getProjektabrechnungId());
  }

  @Test
  public void testGetProjektzeitZuProjektabrechnungFuerMitarbeiter() throws ApiException {
    Long projektabrechnungId = 253L;
    Long mitarbeiterId = 2655L;
    Long projektId = 592L;
    Integer jahr = 2019;
    Integer monat = 10;

    List<ProjektabrechnungProjektzeit> projektabrechnungProjektzeits = this.projektabrechnungControllerApi.getProjektzeitZuProjektabrechnungFuerMitarbeiter(
        projektabrechnungId, mitarbeiterId, projektId, jahr, monat);

    Assertions.assertNotNull(projektabrechnungProjektzeits);
    Assertions.assertFalse(projektabrechnungProjektzeits.isEmpty());
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungProjektzeits.get(0).getProjektabrechnungId());
  }

  @Test
  public void testGetReiseZuProjektabrechnungFuerMitarbeiter() throws ApiException {
    Long projektabrechnungId = 16L;
    Long mitarbeiterId = 89L;

    ProjektabrechnungReise projektabrechnungReise = this.projektabrechnungControllerApi.getReiseZuProjektabrechnungFuerMitarbeiter(
        projektabrechnungId, mitarbeiterId);

    Assertions.assertNotNull(projektabrechnungReise);
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungReise.getProjektabrechnungId());
  }

  @Test
  public void testGetSonderarbeitZuProjektabrechnungFuerMitarbeiter() throws ApiException {
    Long projektabrechnungId = 39L;
    Long mitarbeiterId = 175L;

    ProjektabrechnungSonderarbeit projektabrechnungSonderarbeit = this.projektabrechnungControllerApi.getSonderarbeitZuProjektabrechnungFuerMitarbeiter(
        projektabrechnungId, mitarbeiterId);

    Assertions.assertNotNull(projektabrechnungSonderarbeit);
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungSonderarbeit.getProjektabrechnungId());
  }

  @Test
  public void testGetSonstigeZuProjektabrechnungFuerMitarbeiter() throws ApiException {
    Long projektabrechnungId = 75L;
    Long mitarbeiterId = 165L;

    ProjektabrechnungSonstige projektabrechnungSonstige = this.projektabrechnungControllerApi.getSonstigeZuProjektabrechnungFuerMitarbeiter(
        projektabrechnungId, mitarbeiterId);

    Assertions.assertNotNull(projektabrechnungSonstige);
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungSonstige.getProjektabrechnungId());
  }

  @Test
  public void testGetSonstigeZuProjektabrechnungOhneMitarbeiterbezug() throws ApiException {
    Long projektabrechnungId = 386L;

    ProjektabrechnungSonstige projektabrechnungSonstige = this.projektabrechnungControllerApi.getSonstigeZuProjektabrechnungOhneMitarbeiterbezug(
        projektabrechnungId);

    Assertions.assertNotNull(projektabrechnungSonstige);
    Assertions.assertEquals(projektabrechnungId,
        projektabrechnungSonstige.getProjektabrechnungId());
  }

  @Test
  public void testGetMitarbeiterHatMehrAlsBerechneteLeistung_liefertFalse() throws ApiException {
    Long projektabrechnungId = 872L;
    Long mitarbeiterId = 7520L;

    Boolean mitarbeiterHatMehrAlsBerechneteLeistung = this.projektabrechnungControllerApi.getMitarbeiterHatMehrAlsBerechneteLeistung(
        projektabrechnungId, mitarbeiterId);

    Assertions.assertFalse(mitarbeiterHatMehrAlsBerechneteLeistung);
  }

  @Test
  public void testGetMitarbeiterHatMehrAlsBerechneteLeistung_liefertTrue() throws ApiException {
    Long projektabrechnungId = 633L;
    Long mitarbeiterId = 85L;

    Boolean mitarbeiterHatMehrAlsBerechneteLeistung = this.projektabrechnungControllerApi.getMitarbeiterHatMehrAlsBerechneteLeistung(
        projektabrechnungId, mitarbeiterId);

    Assertions.assertTrue(mitarbeiterHatMehrAlsBerechneteLeistung);
  }

}
