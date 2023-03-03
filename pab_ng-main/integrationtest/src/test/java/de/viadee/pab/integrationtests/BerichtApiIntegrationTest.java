package de.viadee.pab.integrationtests;

import de.viadee.pab.client.api.ApiClient;
import de.viadee.pab.client.api.ApiException;
import de.viadee.pab.client.api.BerichtControllerApi;
import de.viadee.pab.client.api.model.ErgebnisB002Uebersicht;
import de.viadee.pab.client.api.model.ErgebnisB004Uebersicht;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BerichtApiIntegrationTest {

  private final Integer abJahr = 2019;
  private final Integer abMonat = 5;
  private final Long mitarbeiterId = 165L;
  private final Long sachbearbeiterId = 242L;

  private BerichtControllerApi berichtControllerApi;

  @BeforeEach
  public void init() {
    ApiClient apiClient = new ApiClient();
    apiClient.setHost("localhost");
    apiClient.setPort(8080);
    this.berichtControllerApi = new BerichtControllerApi(apiClient);
  }

  @Test
  public void testGetBerichtB002FuerZeitMitarbeiterSachbearbeiter() throws ApiException {
    List<ErgebnisB002Uebersicht> bericht = this.berichtControllerApi.getB002Uebersicht(
        abJahr, abMonat, mitarbeiterId, sachbearbeiterId);

    Assertions.assertNotNull(bericht);
    Assertions.assertFalse(bericht.isEmpty());
  }

  @Test
  public void testGetBerichtB002FuerZeitMitarbeiter() throws ApiException {
    List<ErgebnisB002Uebersicht> bericht = this.berichtControllerApi.getB002Uebersicht(
        abJahr, abMonat, mitarbeiterId, null);

    Assertions.assertNotNull(bericht);
    Assertions.assertFalse(bericht.isEmpty());
  }

  @Test
  public void testGetBerichtB002FuerZeit() throws ApiException {
    List<ErgebnisB002Uebersicht> bericht = this.berichtControllerApi.getB002Uebersicht(
        abJahr, abMonat, null, null);

    Assertions.assertNotNull(bericht);
    Assertions.assertFalse(bericht.isEmpty());
  }

  @Test
  public void testGetBerichtB004Uebersicht() throws ApiException {
    List<ErgebnisB004Uebersicht> bericht = this.berichtControllerApi.getB004Uebersicht(
        abJahr, abMonat, 175L, 2629L, 50);

    Assertions.assertNotNull(bericht);
    Assertions.assertFalse(bericht.isEmpty());
  }


}
