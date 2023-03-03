package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.ProjektControllerApi;
import de.viadee.pab.client.api.model.Projekt;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjektApiIntegrationTest {

  private ProjektControllerApi projektControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.projektControllerApi = new ProjektControllerApi(apiClient);
  }

  @Test
  public void testGetAllProjekte() throws ApiException {
    List<Projekt> projekte = this.projektControllerApi.getAlleProjekte();

    Assertions.assertNotNull(projekte);
    Assertions.assertFalse(projekte.isEmpty());
  }

}
