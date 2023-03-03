package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.ArbeitsnachweisControllerApi;
import de.viadee.pab.client.api.model.Abwesenheit;
import de.viadee.pab.client.api.model.Arbeitsnachweis;
import de.viadee.pab.client.api.model.ArbeitsnachweisLohnartZuordnung;
import de.viadee.pab.client.api.model.ArbeitsnachweisUebersicht;
import de.viadee.pab.client.api.model.Beleg;
import de.viadee.pab.client.api.model.DreiMonatsRegel;
import de.viadee.pab.client.api.model.Fehlerlog;
import de.viadee.pab.client.api.model.LohnartberechnungLog;
import de.viadee.pab.client.api.model.MitarbeiterAbrechnungsmonat;
import de.viadee.pab.client.api.model.Projektstunde;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArbeitsnachweisApiIntegrationTest {

  private final Long mitarbeiterId = 165L;
  private final Long arbeitsnachweisId = 1267L;

  private ArbeitsnachweisControllerApi arbeitsnachweisControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.arbeitsnachweisControllerApi = new ArbeitsnachweisControllerApi(apiClient);
  }

  @Test
  public void testGetArbeitsnachweisUebersicht() throws ApiException {
    LocalDate abWann = LocalDate.of(2018, 1, 1);
    LocalDate bisWann = LocalDate.now();

    List<ArbeitsnachweisUebersicht> arbeitsnachweisUebersichts = this.arbeitsnachweisControllerApi.getArbeitsnachweisUebersicht(
        abWann, bisWann);

    Assertions.assertNotNull(arbeitsnachweisUebersichts);
    Assertions.assertFalse(arbeitsnachweisUebersichts.isEmpty());
  }

  @Test
  public void testFehlendeArbeitsnachweiseFuerZeitraum() throws ApiException {
    LocalDate abWann = LocalDate.now().minusMonths(1);
    LocalDate bisWann = LocalDate.now();

    List<ArbeitsnachweisUebersicht> arbeitsnachweisUebersichts = this.arbeitsnachweisControllerApi.fehlendeArbeitsnachweiseFuerZeitraum(
        abWann, bisWann);

    Assertions.assertNotNull(arbeitsnachweisUebersichts);
    Assertions.assertFalse(arbeitsnachweisUebersichts.isEmpty());
  }

  @Test
  public void testGetAlleAbrechnungsmonateByMitarbeiterId() throws ApiException {
    List<MitarbeiterAbrechnungsmonat> abrechnungsmonate = this.arbeitsnachweisControllerApi.getAlleAbrechnungsmonateByMitarbeiterId(
        this.mitarbeiterId);

    Assertions.assertNotNull(abrechnungsmonate);
    Assertions.assertFalse(abrechnungsmonate.isEmpty());
  }

  @Test
  public void testGetArbeitsnachweisById() throws ApiException {
    Arbeitsnachweis arbeitsnachweis = this.arbeitsnachweisControllerApi.getArbeitsnachweisById(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(arbeitsnachweis);
    Assertions.assertEquals(this.arbeitsnachweisId, arbeitsnachweis.getId());
  }

  @Test
  public void testGetAlleProjektstundenByArbeitsnachweisId() throws ApiException {
    List<Projektstunde> projektstunden = this.arbeitsnachweisControllerApi.getAlleProjektstundenByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(projektstunden);
    Assertions.assertFalse(projektstunden.isEmpty());
  }

  @Test
  public void testGetAlleBelegeByArbeitsnachweisId() throws ApiException {
    List<Beleg> belege = this.arbeitsnachweisControllerApi.getAlleBelegeByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(belege);
    Assertions.assertFalse(belege.isEmpty());
  }

  @Test
  public void testGetAlleAbwesenheitenByArbeitsnachweisId() throws ApiException {
    List<Abwesenheit> abwesenheiten = this.arbeitsnachweisControllerApi.getAlleAbwesenheitenByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(abwesenheiten);
    Assertions.assertFalse(abwesenheiten.isEmpty());
  }

  @Test
  public void testGetAlleLohnartzuordnungenByArbeitsnachweisId() throws ApiException {
    List<ArbeitsnachweisLohnartZuordnung> arbeitsnachweisLohnartZuordnungen = this.arbeitsnachweisControllerApi.getAlleLohnartZuordnungenByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(arbeitsnachweisLohnartZuordnungen);
    Assertions.assertFalse(arbeitsnachweisLohnartZuordnungen.isEmpty());
  }

  @Test
  public void testGetAlleLohnartberechnungLogByArbeitsnachweisId() throws ApiException {
    List<LohnartberechnungLog> lohnartberechnungLogs = this.arbeitsnachweisControllerApi.getAlleLohnartberechnungLogByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(lohnartberechnungLogs);
    Assertions.assertFalse(lohnartberechnungLogs.isEmpty());
  }

  @Test
  public void testGetAlleDreiMonatsRegelnByArbeitsnachweisId() throws ApiException {
    List<DreiMonatsRegel> dreiMonatsRegeln = this.arbeitsnachweisControllerApi.getAlleDreiMonatsRegelnByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(dreiMonatsRegeln);
    Assertions.assertFalse(dreiMonatsRegeln.isEmpty());
  }

  @Test
  public void testGetAlleFehlerlogsByArbeitsnachweisId() throws ApiException {
    List<Fehlerlog> fehlerlogs = this.arbeitsnachweisControllerApi.getAlleFehlerlogsByArbeitsnachweisId(
        this.arbeitsnachweisId);

    Assertions.assertNotNull(fehlerlogs);
    Assertions.assertFalse(fehlerlogs.isEmpty());
  }


}
