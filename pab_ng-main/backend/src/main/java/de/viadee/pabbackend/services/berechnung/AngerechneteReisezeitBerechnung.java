package de.viadee.pabbackend.services.berechnung;

import static de.viadee.pabbackend.enums.ProjektstundeTyp.ANGERECHNETE_REISEZEIT;

import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.services.fachobjekt.ParameterService;
import de.viadee.pabbackend.services.fachobjekt.ProjektstundenTypService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.stereotype.Service;

@Service
public class AngerechneteReisezeitBerechnung {

  private final ProjektstundenTypService projektstundenTypService;

  private final ParameterService parameterService;

  public AngerechneteReisezeitBerechnung(
      final ProjektstundenTypService projektstundenTypService,
      final ParameterService parameterService) {
    this.projektstundenTypService = projektstundenTypService;
    this.parameterService = parameterService;
  }

  /**
   * Die angerechnete Reisezeit wird wie folgt berechnet:<br>
   * <br>
   * 1. Sortierung nach Stunden und nach Projektnummern aufsteigend <br>
   * <br>
   *
   * <table>
   * <tr>
   * <td>Tag
   * <td>Projektnummer
   * <td>Stunden
   * </tr>
   * <tr>
   * <td>1
   * <td>0003
   * <td>0,5
   * </tr>
   * <tr>
   * <td>1
   * <td>9155
   * <td>1,0<br>
   * </tr>
   * <tr>
   * <td>1
   * <td>0033
   * <td>1,0<br>
   * </tr>
   * <tr>
   * <td>1
   * <td>0007
   * <td>4,0<br>
   * </tr>
   * </table>
   *
   * <br>
   * 2. Verteilung der Reisezeit (bei internen Mitarbeitern abzüglich zumutbare Reisezeit ->
   * Paramter ist abhängig von der Summe der Projektstunde des Tages (siehe DB-Tabelle Parameter).
   * Beispiel 2 zumutbare Stunden sind zu verteilen:
   *
   * <table>
   * <tr>
   * <td>Tag</td>
   * <td>Projektnummer</td>
   * <td>Stunden</td>
   * <td>Berechnung</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>0003</td>
   * <td>0,5</td>
   * <td>Berechneter Wert 0: 0,5 - 2 = -1.5 -> 0; Rest 1,5</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>9155</td>
   * <td>1,0</td>
   * <td>Berechneter Wert 0: 1,0 - 1,5 = -0.5 -> 0; Rest 0,5</td> <br>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>0033</td>
   * <td>1,0</td><br>
   * <td>Berechneter Wert 0,5: 1,0 - 0,5 = 0.5 -> 0.5; Rest 0,0</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>0007</td>
   * <td>4,0</td><br>
   * <td>Berechneter Wert 4,0: 4,0 - 0,0 = 4,0 -> 4,0; Rest 0,0</td>
   * </tr>
   * </table>
   *
   * <br>
   */
  public List<Projektstunde> berechneAngerechneteReisezeit(
      final List<Projektstunde> reisezeitProjektstunde) throws RuntimeException {
    List<Projektstunde> berechneteProjektstunde = new ArrayList<>();
    final List<Projektstunde> tatsaechlicheReisezeitProjektstunde = reisezeitProjektstunde;

    final ProjektstundeTypKonstante angReisezeit =
        projektstundenTypService.ladeProjektstundenTypByTextKurz(ANGERECHNETE_REISEZEIT.getValue());

    sortiereNachStundenUndProjektnummer(tatsaechlicheReisezeitProjektstunde);

    final List<Projektstunde> angerechneteReisezeitProjektstunde = new ArrayList<>();

    final List<Integer> tage =
        tatsaechlicheReisezeitProjektstunde.stream()
            .map(stunden -> stunden.getTagVon().getDayOfMonth())
            .distinct().
            toList();

    for (final Integer tag : tage) {

      BigDecimal zumutbareReisezeit;

      String zumutbareReisezeitParameter = parameterService.valueByKey("Zumutbare_Reisezeit");

      if (zumutbareReisezeitParameter != null) {
        zumutbareReisezeit = new BigDecimal(zumutbareReisezeitParameter);
      } else {
        throw new RuntimeException(
            "Der Parameter \"Zumutbare_Reisezeit\" ist nicht administriert, Reisezeit kann nicht ordentlich berechnet werden");
      }

      final List<Projektstunde> reisezeitProjektstundeDesTages =
          tatsaechlicheReisezeitProjektstunde.stream()
              .filter(stunden -> stunden.getTagVon().getDayOfMonth() == tag.intValue()).
              toList();
      BigDecimal rest = zumutbareReisezeit;
      for (final Projektstunde tatsaechlicheStunden : reisezeitProjektstundeDesTages) {

        BigDecimal angerechneteReisezeitIntern = BigDecimal.ZERO;
        if (rest.compareTo(BigDecimal.ZERO) != 0) {
          final BigDecimal stundenMinusRest =
              tatsaechlicheStunden.getAnzahlStunden().subtract(rest);
          if (stundenMinusRest.signum() > 0) {
            angerechneteReisezeitIntern = stundenMinusRest;
            rest = BigDecimal.ZERO;
          } else {
            rest = stundenMinusRest.multiply(new BigDecimal("-1"));
          }
        } else {
          angerechneteReisezeitIntern = tatsaechlicheStunden.getAnzahlStunden();
        }

        final Projektstunde angerechneteReisezeitStunden = new Projektstunde();
        angerechneteReisezeitStunden.setTagVon(tatsaechlicheStunden.getTagVon());
        angerechneteReisezeitStunden.setProjektstundeTypId(angReisezeit.getId());
        angerechneteReisezeitStunden.setProjektId(tatsaechlicheStunden.getProjektId());
        angerechneteReisezeitStunden.setZuletztGeaendertVon(
            tatsaechlicheStunden.getZuletztGeaendertVon());
        angerechneteReisezeitStunden.setAnzahlStunden(angerechneteReisezeitIntern);
        angerechneteReisezeitProjektstunde.add(angerechneteReisezeitStunden);
      }
    }

    berechneteProjektstunde.addAll(angerechneteReisezeitProjektstunde);

    return berechneteProjektstunde;
  }

  private void sortiereNachStundenUndProjektnummer(
      final List<Projektstunde> tatsaechlicheReisezeitProjektstunde) {
    Collections.sort(
        tatsaechlicheReisezeitProjektstunde,
        (ersteStunden, zweiteStunden) ->
            (new CompareToBuilder())
                .append(ersteStunden.getAnzahlStunden(), zweiteStunden.getAnzahlStunden())
                .append(zweiteStunden.getProjektId(), ersteStunden.getProjektId())
                .toComparison());
  }
}
