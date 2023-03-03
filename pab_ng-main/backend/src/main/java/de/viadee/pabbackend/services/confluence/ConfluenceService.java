package de.viadee.pabbackend.services.confluence;

import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ConfluenceService {

  private final ParameterService parameterService;

  public ConfluenceService(ParameterService parameterService) {
    this.parameterService = parameterService;
  }

  public void aktualisiereSeite(final long pageId, final String inhalt)
      throws IOException, URISyntaxException, InterruptedException {

    final String baseUrl = parameterService.valueByKey("ConfluenceURL");
    final String username = parameterService.valueByKey("ConfluenceUser");
    final String password = parameterService.valueByKey("ConfluencePassword");
    final String encoding = "utf-8";

    // Aktuelle Seite abfragen
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest pageRequest = HttpRequest.newBuilder()
        .GET()
        .header("Authorization",
            "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
        .uri(new URI(
            getContentRestUrl(pageId, new String[]{"body.storage", "version", "ancestors"}, baseUrl,
                encoding)))
        .build();

    HttpResponse<String> pageResponse = httpClient.send(pageRequest,
        HttpResponse.BodyHandlers.ofString());
    String pageObj = pageResponse.body();

    // Antwort nach JSON wandeln
    JSONObject page = new JSONObject(pageObj);

    // Seite Aktualisieren
    // !! Confluence Storage Format beachten !! (https://confluence.atlassian.com/display/DOC/Confluence+Storage+Format)
    page.getJSONObject("body").getJSONObject("storage").put("value", inhalt);
    int currentVersion = page.getJSONObject("version").getInt("number");
    page.getJSONObject("version").put("number", currentVersion + 1);

    // Update Request senden
    HttpRequest pageUpdateRequest = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(page.toString()))
        .header("Authorization",
            "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
        .uri(new URI(getContentRestUrl(pageId, new String[]{}, baseUrl, encoding))).build();
    HttpResponse<String> pageUpdateResponse = httpClient.send(pageUpdateRequest,
        HttpResponse.BodyHandlers.ofString());
  }

  private String getContentRestUrl(final long pageId, final String[] expansions,
      final String baseUrl, final String encoding) throws UnsupportedEncodingException {
    final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), encoding);
    String contentRestUrl =
        baseUrl + "/rest/api/content/" + pageId + "?expand=" + expand + "&os_auth_Type=basic";
    return contentRestUrl;
  }
}


