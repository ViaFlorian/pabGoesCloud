package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.KundeControllerApi;
import de.viadee.pab.client.api.model.Kunde;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KundeApiIntegrationTest {

  private KundeControllerApi kundeControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.kundeControllerApi = new KundeControllerApi(apiClient);
  }

  @Test
  public void testGetAllKunden() throws ApiException {
    List<Kunde> kunden = this.kundeControllerApi.getAlleKunden();

    Assertions.assertNotNull(kunden);
    Assertions.assertFalse(kunden.isEmpty());
  }

}
