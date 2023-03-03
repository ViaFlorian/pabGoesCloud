package de.viadee.pabbackend.services.dms;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.DmsVerarbeitung;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class DmsService {

  private final ParameterService parameterService;
  private final MitarbeiterService mitarbeiterService;
  private final ArbeitsnachweisService arbeitsnachweisService;
  private String dmsAuthToken;
  private String dmsBaseUrl;
  private String repoId;
  private String dmsSourceCategory;
  private String dmsAbrechnungsmonatPropertyKey;
  private String dmsPersonalNrPropertyKey;
  private String dmsMitarbeiterNamePropertyKey;

  public DmsService(ParameterService parameterService, MitarbeiterService mitarbeiterService,
      ArbeitsnachweisService arbeitsnachweisService) {
    this.parameterService = parameterService;
    this.mitarbeiterService = mitarbeiterService;
    this.arbeitsnachweisService = arbeitsnachweisService;
  }

  @PostConstruct
  public void init() {
    dmsAuthToken = parameterService.valueByKey("dmsAuthToken");
    dmsBaseUrl = parameterService.valueByKey("dmsBaseUrl");
    repoId = parameterService.valueByKey("repoId");
    dmsSourceCategory = parameterService.valueByKey("dmsSourceCategory");
    dmsAbrechnungsmonatPropertyKey = parameterService.valueByKey("dmsAbrechnungsmonatPropertyKey");
    dmsPersonalNrPropertyKey = parameterService.valueByKey("dmsPersonalNrPropertyKey");
    dmsMitarbeiterNamePropertyKey = parameterService.valueByKey("dmsMitarbeiterNamePropertyKey");
  }

  public String erstelleDmsUrlFuerArbeitsnachweis(Long arbeitsnachweisId) {
    Arbeitsnachweis arbeitsnachweis = arbeitsnachweisService.arbeitsnachweisById(arbeitsnachweisId);
    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        arbeitsnachweis.getMitarbeiterId());

    String dmsSuchePersonalNr = parameterService.valueByKey("dmsSuchePersonalNr");
    String dmsSucheAbrechnungsmonat = parameterService.valueByKey("dmsSucheAbrechnungsmonat");
    String dmsSucheMitarbeitername = parameterService.valueByKey("dmsSucheMitarbeitername");
    String dmsSucheObjectDefinitionIds = parameterService.valueByKey(
        "dmsSucheObjectDefinitionIds");
    String dmsBaseUrl = parameterService.valueByKey("dmsBaseUrl");
    String repoId = parameterService.valueByKey("repoId");
    LocalDate monatsanfang = LocalDate.of(
        arbeitsnachweis.getJahr(),
        arbeitsnachweis.getMonat(), 1);
    String abrechnungsmonatSuchWert = monatsanfang.withDayOfMonth(monatsanfang.lengthOfMonth())
        .toString();

    return String.format(
        "%s/dms/r/%s/sr/?objectdefinitionids=[\"%s\"]&properties={\"%s\":[\"%s\"],\"%s\":[\"%s\"],\"%s\":[\"%s\"]}&propertysort=property_last_modified_date&ascending=false&showdetails=true",
        dmsBaseUrl, repoId, dmsSucheObjectDefinitionIds, dmsSuchePersonalNr,
        mitarbeiter.getPersonalNr(), dmsSucheAbrechnungsmonat, abrechnungsmonatSuchWert,
        dmsSucheMitarbeitername, mitarbeiter
            .getFullName());
  }

  public DmsVerarbeitung ladeBelegHoch(final Arbeitsnachweis arbeitsnachweis,
      final Mitarbeiter mitarbeiter,
      final String dateiname, final byte[] datei) {

    final DmsVerarbeitung dmsVerarbeitung = new DmsVerarbeitung();

    try {

      // Chunked Upload URL vom DMS abfragen
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest chunkedUploadRequest = HttpRequest.newBuilder()
          .GET()
          .header("Authorization",
              "Bearer " + dmsAuthToken)
          .uri(new URI(dmsBaseUrl + "/dms/r/" + repoId))
          .build();

      HttpResponse<String> chunkedUploadResponse = httpClient.send(chunkedUploadRequest,
          HttpResponse.BodyHandlers.ofString());
      JSONParser parser = new JSONParser(chunkedUploadResponse.body());
      LinkedHashMap<String, Object> resultJson = parser.parseObject();
      String chunkedUrl = (String) ((LinkedHashMap) ((LinkedHashMap) resultJson.get("_links")).get(
          "chunkedupload")).get("href");

      // Eigentlichen Upload durchführen
      HttpRequest uploadRequest = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofByteArray(datei))
          .header("Authorization",
              "Bearer " + dmsAuthToken)
          .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
          .uri(new URI(dmsBaseUrl + chunkedUrl))
          .build();

      // Upload-Response verarbeiten und Commit-Request vorbereiten
      HttpResponse<String> uploadResponse = httpClient.send(uploadRequest,
          HttpResponse.BodyHandlers.ofString());
      String contentLocationUri = uploadResponse.headers().firstValue("Location").get();
      JSONObject bodyObject = new JSONObject();
      bodyObject.put("filename", dateiname);
      bodyObject.put("sourceCategory", dmsSourceCategory);
      bodyObject.put("sourceId", "/dms/r/" + repoId + "/source");
      bodyObject.put("contentLocationUri", contentLocationUri);
      bodyObject.put("sourceProperties", new JSONObject());

      JSONArray propertiesArray = new JSONArray();
      JSONObject abrechnungsmonatProperty = new JSONObject();
      abrechnungsmonatProperty.put("key", dmsAbrechnungsmonatPropertyKey);
      JSONArray abrechnungsmonatPropertyValuesArray = new JSONArray();
      LocalDate monatsanfang = LocalDate.of(arbeitsnachweis.getJahr(), arbeitsnachweis.getMonat(),
          1);
      abrechnungsmonatPropertyValuesArray.put(
          monatsanfang.withDayOfMonth(monatsanfang.lengthOfMonth()).toString());
      abrechnungsmonatProperty.put("values", abrechnungsmonatPropertyValuesArray);
      JSONObject personalNrProperty = new JSONObject();
      personalNrProperty.put("key", dmsPersonalNrPropertyKey);
      JSONArray personalNrPropertyValuesArray = new JSONArray();
      personalNrPropertyValuesArray.put(mitarbeiter.getPersonalNr());
      personalNrProperty.put("values", personalNrPropertyValuesArray);
      JSONObject nameProperty = new JSONObject();
      nameProperty.put("key", dmsMitarbeiterNamePropertyKey);
      JSONArray namePropertyValuesArray = new JSONArray();
      namePropertyValuesArray.put(mitarbeiter.getFullName());
      nameProperty.put("values", namePropertyValuesArray);

      propertiesArray.put(abrechnungsmonatProperty);
      propertiesArray.put(personalNrProperty);
      propertiesArray.put(nameProperty);

      bodyObject.getJSONObject("sourceProperties").put("properties", propertiesArray);

      // Commit-Request ausführen
      HttpRequest commitRequest = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString(bodyObject.toString()))
          .header("Authorization",
              "Bearer " + dmsAuthToken)
          .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
          .uri(new URI(dmsBaseUrl + "/dms/r/" + repoId + "/o2m"))
          .build();

      HttpResponse<String> commitResponse = httpClient.send(commitRequest,
          HttpResponse.BodyHandlers.ofString());
      if (isSuccessful(commitResponse.statusCode())) {
        dmsVerarbeitung.setFehlerhaft(false);
        dmsVerarbeitung.setMessage("Datei " + dateiname + " erfolgreich ins DMS übertragen");
      } else {
        dmsVerarbeitung.setFehlerhaft(true);
        dmsVerarbeitung.setMessage("Upload von Datei " + dateiname + " NICHT ERFOLGREICH.");
      }

    } catch (URISyntaxException | IOException | InterruptedException |
             ParseException exception) {
      dmsVerarbeitung.setFehlerhaft(true);
      dmsVerarbeitung.setMessage(
          String.format("Upload von Datei " + dateiname + " NICHT ERFOLGREICH. Message %s",
              exception.getMessage()));
    }

    return dmsVerarbeitung;

  }

  private boolean isSuccessful(int statusCode) {
    return statusCode / 100 == 2;
  }
}
