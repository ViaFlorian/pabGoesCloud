package de.viadee.pabbackend.services.mail;

import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.MitarbeiterQuittungEmailDaten;
import de.viadee.pabbackend.services.fachobjekt.ArbeitsnachweisService;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterService;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterQuittungService {

  private final ArbeitsnachweisService arbeitsnachweisService;
  private final MitarbeiterService mitarbeiterService;
  private final ParameterService parameterService;

  public MitarbeiterQuittungService(ArbeitsnachweisService arbeitsnachweisService,
      MitarbeiterService mitarbeiterService, ParameterService parameterService) {
    this.arbeitsnachweisService = arbeitsnachweisService;
    this.mitarbeiterService = mitarbeiterService;
    this.parameterService = parameterService;
  }

  public MitarbeiterQuittungEmailDaten erstelleMitarbeiterQuittungEmailDaten(
      Long arbeitsnachweisId) {
    Arbeitsnachweis arbeitsnachweis = arbeitsnachweisService.arbeitsnachweisById(arbeitsnachweisId);
    Mitarbeiter mitarbeiter = mitarbeiterService.mitarbeiterById(
        arbeitsnachweis.getMitarbeiterId());

    String dateiname = String.format(
        "RueckmeldungANW_%s_%d_%d.pdf", mitarbeiter.getKurzname(), arbeitsnachweis.getJahr(),
        arbeitsnachweis.getMonat());

    String betreff = parameterService.valueByKey("emailSubjectAnwFreigabe")
        + arbeitsnachweis.getDatum();
    String[] mitarbeiterEmails = new String[]{mitarbeiter.getEmail()};
    String nachricht = parameterService.valueByKey("emailBodyAnwFreigabe");

    return new MitarbeiterQuittungEmailDaten(dateiname, mitarbeiterEmails, nachricht, betreff);
  }

}
