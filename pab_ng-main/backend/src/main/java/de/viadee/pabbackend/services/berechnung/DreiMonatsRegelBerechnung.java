package de.viadee.pabbackend.services.berechnung;

import static java.util.stream.Collectors.groupingBy;

import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.AbwesenheitGruppierung;
import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.zeitrechnung.Zeitrechnung;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DreiMonatsRegelBerechnung {

  private final ProjektService projektService;

  private final Zeitrechnung zeitrechnung;

  public DreiMonatsRegelBerechnung(final ProjektService projektService,
      final Zeitrechnung zeitrechnung) {
    this.projektService = projektService;
    this.zeitrechnung = zeitrechnung;
  }

  public List<DreiMonatsRegel> berechneDreiMonatsRegeln(final Mitarbeiter mitarbeiter,
      final List<Abwesenheit> bereitsGespeicherteAbwesenheiten,
      final List<Abwesenheit> neueAbwesenheiten,
      final Integer abrechnungsJahr, final Integer abrechnungsmonat) {

    final List<DreiMonatsRegel> berechnungsergebnis = new ArrayList<>();

    final List<Abwesenheit> gesamtMengeZuBetrachtenderAbwesenheiten = new ArrayList<>();

    gesamtMengeZuBetrachtenderAbwesenheiten.addAll(
        bereitsGespeicherteAbwesenheiten == null ? new ArrayList<>()
            : bereitsGespeicherteAbwesenheiten);
    gesamtMengeZuBetrachtenderAbwesenheiten
        .addAll(neueAbwesenheiten == null ? new ArrayList<>() : neueAbwesenheiten);

    Collections.sort(gesamtMengeZuBetrachtenderAbwesenheiten);

    final Map<AbwesenheitGruppierung, List<Abwesenheit>> abwesenheitenNachKalenderwochen = gruppiereAbwesenheitenNachKalenderwoche(
        gesamtMengeZuBetrachtenderAbwesenheiten);

    final Map<AbwesenheitGruppierung, List<Abwesenheit>> kalenderwochenMitMindestensDreiAbwesenheiten = verwerfeKalenderwochenMitWenigerAlsDreiAbwesenheiten(
        abwesenheitenNachKalenderwochen);

    final List<Abwesenheit> startzeitpunkteFuerDreiMonatsRegeln = ermittleJeweilsErsteAbwesenheitProKalenderwoche(
        kalenderwochenMitMindestensDreiAbwesenheiten);

    final Map<Abwesenheit, List<Abwesenheit>> startzeitpunkteSamtNachfolgerAbwesenheiten = ordneStartzeitpunktenNachfolgerZu(
        startzeitpunkteFuerDreiMonatsRegeln, gesamtMengeZuBetrachtenderAbwesenheiten);

    final List<DreiMonatsRegel> dreiMonatsRegelnAufBasisDerStartzeitpunkte = ermittleDreiMonatsRegelnAufBasisDerStartzeitpunkte(
        mitarbeiter, startzeitpunkteSamtNachfolgerAbwesenheiten, abrechnungsJahr, abrechnungsmonat);

    final List<DreiMonatsRegel> ueberschneidungsfreieRegeln = verwerfeUeberschneideneRegeln(
        dreiMonatsRegelnAufBasisDerStartzeitpunkte);

    berechnungsergebnis.addAll(ueberschneidungsfreieRegeln);

    return berechnungsergebnis;

  }

  private List<DreiMonatsRegel> verwerfeUeberschneideneRegeln(
      final List<DreiMonatsRegel> dreiMonatsRegelnAufBasisDerStartzeitpunkte) {
    final List<DreiMonatsRegel> ueberschneidungsfreieRegeln = new ArrayList<>();

    dreiMonatsRegelnAufBasisDerStartzeitpunkte.stream().forEach(regel -> {
      if (!dreiMonatsRegelnAufBasisDerStartzeitpunkte.stream().anyMatch(vergleichsRegel -> {
        return regel.getArbeitsstaette().trim().equals(vergleichsRegel.getArbeitsstaette().trim())
            &&
            ((regel.getGueltigBis() == null && vergleichsRegel.getGueltigBis() == null
                && (regel.getGueltigVon().isAfter(vergleichsRegel.getGueltigVon())) ||

                (regel.getGueltigBis() != null && vergleichsRegel.getGueltigBis() != null &&
                    regel.getGueltigBis().equals(vergleichsRegel.getGueltigBis())
                    && (regel.getGueltigVon().isAfter(vergleichsRegel.getGueltigVon()))))

            );

      })) {
        ueberschneidungsfreieRegeln.add(regel);
      }
    });

    return ueberschneidungsfreieRegeln;
  }

  private List<DreiMonatsRegel> ermittleDreiMonatsRegelnAufBasisDerStartzeitpunkte(
      final Mitarbeiter mitarbeiter,
      final Map<Abwesenheit, List<Abwesenheit>> startzeitpunkteSamtNachfolgerAbwesenheiten,
      final Integer abrechnungsJahr, final Integer abrechnungsmonat) {

    final List<DreiMonatsRegel> ermittelteRegeln = new ArrayList<>();

    for (final Abwesenheit startzeitpunkt : startzeitpunkteSamtNachfolgerAbwesenheiten.keySet()) {

      final Projekt projektDerAbwesenheit = projektService.projektById(
          startzeitpunkt.getProjektId());

      final DreiMonatsRegel regel = new DreiMonatsRegel();
      regel.setArbeitsstaette(startzeitpunkt.getArbeitsstaette().trim());
      regel.setMitarbeiterId(mitarbeiter.getId());
      regel.setGueltigVon(startzeitpunkt.getTagVon());
      regel.setAutomatischErfasst(true);
      regel.setKundeScribeId(projektDerAbwesenheit.getKundeId());
      regel.setGueltigBis(startzeitpunkt.getTagVon().plusDays(28));

      for (final Abwesenheit nachfolgendeAbwensenheit : startzeitpunkteSamtNachfolgerAbwesenheiten
          .get(startzeitpunkt)) {
        if (nachfolgendeAbwensenheit.getTagVon().isBefore(regel.getGueltigBis())
            || nachfolgendeAbwensenheit.getTagVon().isEqual(regel.getGueltigBis())) {
          regel.setGueltigBis(nachfolgendeAbwensenheit.getTagVon().plusDays(28));
        } else {
          break;
        }

      }
      LocalDate aktuellerAbrechnungsmonat = LocalDate.of(abrechnungsJahr, abrechnungsmonat, 1);
      if (regel.getGueltigBis().isAfter(aktuellerAbrechnungsmonat.plusMonths(1))
          || regel.getGueltigBis().isEqual(aktuellerAbrechnungsmonat.plusMonths(1))) {
        regel.setGueltigBis(null);
      }

      ermittelteRegeln.add(regel);

    }

    return ermittelteRegeln;
  }

  private Map<Abwesenheit, List<Abwesenheit>> ordneStartzeitpunktenNachfolgerZu(
      final List<Abwesenheit> startzeitpunkteFuerDreiMonatsRegeln,
      final List<Abwesenheit> gesamtMengeZuBetrachtenderAbwesenheiten) {

    final Map<Abwesenheit, List<Abwesenheit>> startzeitpunkteSamtNachfolgerAbwesenheiten = new HashMap<>();

    startzeitpunkteFuerDreiMonatsRegeln.stream().forEach(startzeitpunkt -> {

      if (startzeitpunkteSamtNachfolgerAbwesenheiten.get(startzeitpunkt) == null) {
        startzeitpunkteSamtNachfolgerAbwesenheiten.put(startzeitpunkt, new ArrayList<>());
      }

      gesamtMengeZuBetrachtenderAbwesenheiten.stream().forEach(zuVergleichendeAbwensenheit -> {
        if (arbeitsstaetteIstGleich(startzeitpunkt, zuVergleichendeAbwensenheit) &&
            kundeIstGleich(startzeitpunkt, zuVergleichendeAbwensenheit) &&
            startzeitpunkt.getTagVon().isBefore(zuVergleichendeAbwensenheit.getTagVon())) {
          startzeitpunkteSamtNachfolgerAbwesenheiten.get(startzeitpunkt)
              .add(zuVergleichendeAbwensenheit);
        }
      });

    });

    return startzeitpunkteSamtNachfolgerAbwesenheiten;
  }

  private boolean arbeitsstaetteIstGleich(final Abwesenheit startzeitpunkt,
      final Abwesenheit zuVergleichendeAbwensenheit) {
    return startzeitpunkt.getArbeitsstaette().trim()
        .equals(zuVergleichendeAbwensenheit.getArbeitsstaette().trim());
  }

  private boolean kundeIstGleich(final Abwesenheit startzeitpunkt,
      final Abwesenheit zuVergleichendeAbwensenheit) {
    final Projekt projektStartzeitpunkt = projektService.projektById(startzeitpunkt.getProjektId());
    final Projekt projektZuVergleichendeAbwesenheit = projektService.projektById(
        zuVergleichendeAbwensenheit.getProjektId());
    return projektStartzeitpunkt.getKundeId()
        .equals(projektZuVergleichendeAbwesenheit.getKundeId());
  }

  private List<Abwesenheit> ermittleJeweilsErsteAbwesenheitProKalenderwoche(
      final Map<AbwesenheitGruppierung, List<Abwesenheit>> kalenderwochenMitMindestensDreiAbwesenheiten) {
    final List<Abwesenheit> ersteAbwesenheiteDerKalenderwochen = new ArrayList<>();

    for (final AbwesenheitGruppierung kalenderwoche : kalenderwochenMitMindestensDreiAbwesenheiten.keySet()) {
      Collections.sort(kalenderwochenMitMindestensDreiAbwesenheiten.get(kalenderwoche));
      ersteAbwesenheiteDerKalenderwochen
          .add(kalenderwochenMitMindestensDreiAbwesenheiten.get(kalenderwoche).get(0));
    }

    return ersteAbwesenheiteDerKalenderwochen;
  }

  private Map<AbwesenheitGruppierung, List<Abwesenheit>> gruppiereAbwesenheitenNachKalenderwoche(
      final List<Abwesenheit> gesamtMengeZuBetrachtenderAbwesenheiten) {
    final Map<AbwesenheitGruppierung, List<Abwesenheit>> abwesenheitenNachKalenderwochen = gesamtMengeZuBetrachtenderAbwesenheiten
        .stream().collect(
            groupingBy(
                abwesenheit -> new AbwesenheitGruppierung(abwesenheit.getArbeitsstaette().trim(),
                    projektService.projektById(abwesenheit.getProjektId()).getKundeId(),
                    zeitrechnung.getKalenderwoche(abwesenheit.getTagVon()),
                    abwesenheit.getTagVon().getYear())));
    return abwesenheitenNachKalenderwochen;
  }

  private Map<AbwesenheitGruppierung, List<Abwesenheit>> verwerfeKalenderwochenMitWenigerAlsDreiAbwesenheiten(
      final Map<AbwesenheitGruppierung, List<Abwesenheit>> abwesenheitenNachKalenderwochen) {
    final Map<AbwesenheitGruppierung, List<Abwesenheit>> kalenderwochenMitMindestensDreiAbwesenheiten = new HashMap<>();

    for (final AbwesenheitGruppierung kalenderwoche : abwesenheitenNachKalenderwochen.keySet()) {
      if (abwesenheitenNachKalenderwochen.get(kalenderwoche).size() >= 3) {
        kalenderwochenMitMindestensDreiAbwesenheiten.put(kalenderwoche,
            abwesenheitenNachKalenderwochen.get(kalenderwoche));
      }
    }

    return kalenderwochenMitMindestensDreiAbwesenheiten;
  }

}
