package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.ProjektstundeTyp.NORMAL;
import static de.viadee.pabbackend.enums.ProjektstundeTyp.SONDERARBEITSZEIT;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.services.fachobjekt.MitarbeiterStellenfaktorService;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.fachobjekt.ProjektstundenTypService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ProjektstundenInkonsistenzBerechnung {

  private final ProjektstundenTypService projektstundenTypService;
  private final ProjektService projektService;
  private final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;
  private final MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung;

  public ProjektstundenInkonsistenzBerechnung(ProjektstundenTypService projektstundenTypService,
      ProjektService projektService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService,
      MitarbeiterUrlaubKontoBerechnung mitarbeiterUrlaubKontoBerechnung) {
    this.projektstundenTypService = projektstundenTypService;
    this.projektService = projektService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
    this.mitarbeiterUrlaubKontoBerechnung = mitarbeiterUrlaubKontoBerechnung;
  }

  public String pruefeUndErstelleWarnungFuerSonderarbeitszeitInkosistenz(
      List<Projektstunde> projektstunden) {

    final ProjektstundeTypKonstante normal = projektstundenTypService
        .ladeProjektstundenTypByTextKurz(NORMAL.getValue());
    final ProjektstundeTypKonstante sonder = projektstundenTypService
        .ladeProjektstundenTypByTextKurz(SONDERARBEITSZEIT.getValue());

    Map<TagUndProjekt, Double> summeSonderzeitGruppiertNachTagUndProjekt = gruppiereNachTagUndProjektMitSummeStundenFuerBestimmtenTyp(
        projektstunden, sonder);

    Map<TagUndProjekt, Double> summeNormaleStundenGruppiertNachTagUndProjekt = gruppiereNachTagUndProjektMitSummeStundenFuerBestimmtenTyp(
        projektstunden, normal);

    for (TagUndProjekt tagUndProjekt : summeSonderzeitGruppiertNachTagUndProjekt.keySet()) {
      if (!summeNormaleStundenGruppiertNachTagUndProjekt.containsKey(tagUndProjekt) ||
          summeNormaleStundenGruppiertNachTagUndProjekt.get(tagUndProjekt)
              < summeSonderzeitGruppiertNachTagUndProjekt.get(tagUndProjekt)
      ) {
        return erstelleWarnMeldungWegenInkosistenz(tagUndProjekt.tag, tagUndProjekt.projektId);
      }
    }

    return null;

  }

  public String pruefeUndErstelleWarnungFuerProjektstunden(List<Projektstunde> projektstunden,
      Long mitarbeiterId, Integer jahr, Integer monat) {
    final List<String> meldungen = new ArrayList<>();

    final Long projektId = projektService.projektByProjektnummer("9004").getId();
    final BigDecimal stellenfaktor = mitarbeiterStellenfaktorService.mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
        mitarbeiterId, jahr, monat).getStellenfaktor();

    projektstunden.stream().filter(projektstunde -> projektstunde.getProjektId().equals(projektId))
        .forEach(projektstunde -> {
          if (urlaubUebersteigtArbeitstag(projektstunde.getAnzahlStunden(), stellenfaktor)) {
            meldungen.add(erstelleWarnMeldungWegenUrlaubsStundenZuViel(projektstunde.getTagVon()));
          } else if (!mitarbeiterUrlaubKontoBerechnung.urlaubAlsHalberOderGanzerTagAngegeben(
              projektstunde.getAnzahlStunden(), stellenfaktor)) {
            meldungen.add(erstelleWarnMeldungWegenUrlaubNichtAlsHalberOderGanzerTagAngegeben(
                projektstunde.getTagVon()));

          }
        });

    if (!meldungen.isEmpty()) {
      return meldungen.get(0);
    }

    return null;
  }


  private boolean urlaubUebersteigtArbeitstag(final BigDecimal stunden,
      final BigDecimal stellenfaktor) {
    BigDecimal arbeitstagGemaessStellenfaktor = (new BigDecimal(8)).multiply(stellenfaktor);

    return stunden.compareTo(arbeitstagGemaessStellenfaktor) > 0;

  }

  private Map<TagUndProjekt, Double> gruppiereNachTagUndProjektMitSummeStundenFuerBestimmtenTyp(
      List<Projektstunde> projektstunden, ProjektstundeTypKonstante projektstundenTyp) {
    return projektstunden.stream()
        .filter(std -> std.getProjektstundeTypId().equals(
            projektstundenTyp.getId()))
        .collect(groupingBy(
            sonderstunde -> new TagUndProjekt(sonderstunde.getTagVon(),
                sonderstunde.getProjektId()),
            summingDouble(projektstunde -> projektstunde.getAnzahlStunden().doubleValue())));
  }

  private String erstelleWarnMeldungWegenInkosistenz(LocalDate tagVon, Long projektId) {
    Projekt projekt = projektService.projektById(projektId);

    return String.format(
        "Die Sonderarbeitszeit für den Tag %s und das Projekt %s übersteigt die Projektstunden an diesem Tag",
        tagVon, projekt.getProjektnummer());
  }

  private String erstelleWarnMeldungWegenUrlaubsStundenZuViel(LocalDate tagVon) {
    return String.format(
        "Die Urlaubsangabe für den Tag %s übersteigt die Stunden des Arbeitstages gemäß Stellenfaktor",
        tagVon);
  }


  private String erstelleWarnMeldungWegenUrlaubNichtAlsHalberOderGanzerTagAngegeben(
      LocalDate tagVon) {
    return String.format(
        "Urlaub am Tag %s nicht als halber/ganzer Tag gemäß Stellenfaktor angegeben",
        tagVon);
  }


  record TagUndProjekt(LocalDate tag, Long projektId) {

  }

}
