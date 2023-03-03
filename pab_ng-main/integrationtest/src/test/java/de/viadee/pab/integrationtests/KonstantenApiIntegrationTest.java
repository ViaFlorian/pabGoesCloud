package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.KonstantenControllerApi;
import de.viadee.pab.client.api.model.BelegartKonstante;
import de.viadee.pab.client.api.model.BuchungstypStundenKonstante;
import de.viadee.pab.client.api.model.BuchungstypUrlaubKonstante;
import de.viadee.pab.client.api.model.LohnartKonstante;
import de.viadee.pab.client.api.model.MitarbeiterTypKonstante;
import de.viadee.pab.client.api.model.ProjektstundeTypKonstante;
import de.viadee.pab.client.api.model.Stadt;
import de.viadee.pab.client.api.model.ViadeeAuslagenKostenartenKonstante;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KonstantenApiIntegrationTest {

  private KonstantenControllerApi konstantenControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.konstantenControllerApi = new KonstantenControllerApi(apiClient);
  }

  @Test
  public void testGetAlleBelegartenKonstante() throws ApiException {
    List<BelegartKonstante> belegarten = this.konstantenControllerApi.getAllBelegartKonstante();

    Assertions.assertNotNull(belegarten);
    Assertions.assertFalse(belegarten.isEmpty());
  }

  @Test
  public void testGetAlleStaedte() throws ApiException {
    List<Stadt> staedte = this.konstantenControllerApi.getAllStaedte();

    Assertions.assertNotNull(staedte);
    Assertions.assertFalse(staedte.isEmpty());
  }

  @Test
  public void testGetAlleProjektstundeTypKonstanten() throws ApiException {
    List<ProjektstundeTypKonstante> projektstundeTypen = this.konstantenControllerApi.getAllProjektstundeTypKonstante();

    Assertions.assertNotNull(projektstundeTypen);
    Assertions.assertFalse(projektstundeTypen.isEmpty());
  }

  @Test
  public void testGetAlleMitarbeiterTypKonstanten() throws ApiException {
    List<MitarbeiterTypKonstante> mitarbeiterTypen = this.konstantenControllerApi.getAllMitarbeiterTypKonstante();

    Assertions.assertNotNull(mitarbeiterTypen);
    Assertions.assertFalse(mitarbeiterTypen.isEmpty());
  }

  @Test
  public void testGetAllBuchungstypStundenKonstante() throws ApiException {
    List<BuchungstypStundenKonstante> buchungstypStunden = this.konstantenControllerApi.getAllBuchungstypStundenKonstante();

    Assertions.assertNotNull(buchungstypStunden);
    Assertions.assertFalse(buchungstypStunden.isEmpty());
  }

  @Test
  public void testGetAllBuchungstypUrlaubKonstante() throws ApiException {
    List<BuchungstypUrlaubKonstante> buchungstypUrlaubs = this.konstantenControllerApi.getAllBuchungstypUrlaubKonstante();

    Assertions.assertNotNull(buchungstypUrlaubs);
    Assertions.assertFalse(buchungstypUrlaubs.isEmpty());
  }

  @Test
  public void testGetAllViadeeAuslagenKostenart() throws ApiException {
    List<ViadeeAuslagenKostenartenKonstante> viadeeAuslagenKostenarten = this.konstantenControllerApi.getAllViadeeAuslagenKostenartKonstante();

    Assertions.assertNotNull(viadeeAuslagenKostenarten);
    Assertions.assertFalse(viadeeAuslagenKostenarten.isEmpty());
  }

  @Test
  public void testGetAllLohnartKonstanten() throws ApiException {
    List<LohnartKonstante> lohnarten = this.konstantenControllerApi.getAllLohnartKonstante();

    Assertions.assertNotNull(lohnarten);
    Assertions.assertFalse(lohnarten.isEmpty());
  }
}
