package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.OrganisationseinheitControllerApi;
import de.viadee.pab.client.api.model.Organisationseinheit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganisationseinheitApiIntegrationTest {

  private OrganisationseinheitControllerApi organisationseinheitControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.organisationseinheitControllerApi = new OrganisationseinheitControllerApi(apiClient);
  }

  @Test
  public void testGetAllOrganisationseinheiten() throws ApiException {
    List<Organisationseinheit> organisationseinheiten = this.organisationseinheitControllerApi.getAlleOrganisationseinheiten();

    Assertions.assertNotNull(organisationseinheiten);
    Assertions.assertFalse(organisationseinheiten.isEmpty());
  }

}
