package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.MitarbeiterControllerApi;
import de.viadee.pab.client.api.model.Mitarbeiter;
import de.viadee.pab.client.api.model.MitarbeiterStundenKonto;
import de.viadee.pab.client.api.model.MitarbeiterUrlaubKonto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MitarbeiterApiIntegrationTest {

  private final Long mitarbeiterId = 165L;

  private MitarbeiterControllerApi mitarbeiterControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.mitarbeiterControllerApi = new MitarbeiterControllerApi(apiClient);
  }

  @Test
  public void testGetAllMitarbeiter() throws ApiException {
    List<Mitarbeiter> mitarbeiters = this.mitarbeiterControllerApi.getAllMitarbeiter();

    Assertions.assertNotNull(mitarbeiters);
    Assertions.assertFalse(mitarbeiters.isEmpty());
  }

  @Test
  public void testGetMitarbeiterSelectOptions() throws ApiException {
    List<Mitarbeiter> mitarbeiters = this.mitarbeiterControllerApi.getMitarbeiterSelectOptions(true,
        false, true, true, true, true);

    Assertions.assertNotNull(mitarbeiters);
    Assertions.assertFalse(mitarbeiters.isEmpty());
  }

  @Test
  public void testGetMitarbeiterUrlaubsuonto() throws ApiException {
    List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontos = this.mitarbeiterControllerApi.getMitarbeiterUrlaubskonto(
        this.mitarbeiterId);

    Assertions.assertNotNull(mitarbeiterUrlaubKontos);
    Assertions.assertFalse(mitarbeiterUrlaubKontos.isEmpty());
  }

  @Test
  public void testGetMitarbeiterStundenkonto() throws ApiException {
    List<MitarbeiterStundenKonto> mitarbeiterStundenKontos = this.mitarbeiterControllerApi.getMitarbeiterStundenkonto(
        this.mitarbeiterId);

    Assertions.assertNotNull(mitarbeiterStundenKontos);
    Assertions.assertFalse(mitarbeiterStundenKontos.isEmpty());
  }
}
