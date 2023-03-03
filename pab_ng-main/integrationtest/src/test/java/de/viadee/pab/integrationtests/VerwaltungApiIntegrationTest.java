package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.VerwaltungControllerApi;
import de.viadee.pab.client.api.model.Faktur;
import de.viadee.pab.client.api.model.ProjektBudget;
import de.viadee.pab.client.api.model.Skonto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VerwaltungApiIntegrationTest {

  private final static Long projektId = 592L;

  private VerwaltungControllerApi verwaltungControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.verwaltungControllerApi = new VerwaltungControllerApi(apiClient);
  }

  @Test
  public void testGetAlleProjektBudgetsByProjektId() throws ApiException {
    List<ProjektBudget> projektBudgets = this.verwaltungControllerApi.getAlleProjektBudgetsByProjektId(
        projektId);

    Assertions.assertNotNull(projektBudgets);
    Assertions.assertFalse(projektBudgets.isEmpty());
  }

  @Test
  public void testGetAlleFakturenByProjektId() throws ApiException {
    List<Faktur> fakturen = this.verwaltungControllerApi.getAlleFakturenByProjektId(projektId);

    Assertions.assertNotNull(fakturen);
    Assertions.assertFalse(fakturen.isEmpty());
  }

  @Test
  public void testGetAlleSkontosByProjektId() throws ApiException {
    List<Skonto> skontos = this.verwaltungControllerApi.getAlleSkontosByProjektId(projektId);

    Assertions.assertNotNull(skontos);
    Assertions.assertFalse(skontos.isEmpty());
  }
}

