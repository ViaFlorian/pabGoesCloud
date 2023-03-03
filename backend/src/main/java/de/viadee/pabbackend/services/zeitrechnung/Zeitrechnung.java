package de.viadee.pabbackend.services.zeitrechnung;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

import de.viadee.pabbackend.entities.Kalender;
import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.SonderarbeitszeitWochentagVerteilung;
import de.viadee.pabbackend.services.fachobjekt.KalenderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Zeitrechnung {

  @Autowired
  private KalenderService kalenderService;

  public BigDecimal berechneStundendifferenz(final LocalTime uhrzeitVon,
      final LocalTime uhrzeitBis) {
    BigDecimal differenz = BigDecimal.ZERO;
    final LocalTime von = uhrzeitVon;
    LocalTime bis = uhrzeitBis;
    if (bis.equals(LocalTime.of(23, 59, 59))) {
      bis = LocalTime.MAX;
    }
    Long minuten = null;
    if (von != null && bis != null) {
      Long sekunden = von.until(bis, ChronoUnit.SECONDS);
      if (sekunden % 100 == 99) {
        sekunden = sekunden + 1;
      }
      minuten = sekunden / 60;
    }
    if (minuten != null && minuten > 0) {
      final double stunden = minuten.doubleValue() / 60;
      differenz = new BigDecimal(stunden).setScale(2, HALF_UP);
    }

    return differenz;
  }

  public LocalDate getAktuellerMonat() {
    int aktuellerMonat = LocalDate.now().getMonthValue();
    int aktuellesJahr = LocalDate.now().getYear();
    return LocalDate.of(aktuellesJahr, aktuellerMonat, 1);
  }

  public LocalDate getVormonat() {
    int aktuellerMonat = LocalDate.now().getMonthValue();
    int aktuellesJahr = LocalDate.now().getYear();
    return LocalDate.of(aktuellesJahr, aktuellerMonat, 1).minusMonths(1);
  }

  public boolean istFeiertag(final LocalDate datum) {
    return kalenderService.tagIstFeiertag(datum);
  }

  public boolean istHeiligabend(final LocalDate datum) {
    return datum.getMonthValue() == 12 && datum.getDayOfMonth() == 24;
  }

  public boolean istSilvester(final LocalDate datum) {
    return datum.getMonthValue() == 12 && datum.getDayOfMonth() == 31;
  }

  public int getKalenderwoche(final LocalDate datum) {
    return datum.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
  }

  public BigDecimal berechneSollstunden(Integer jahr, Integer monat, BigDecimal stellenfaktor) {
    BigDecimal sollstunden = BigDecimal.ZERO;

    List<Kalender> tageDesMonats = kalenderService.ladeAlleTageDesMonats(jahr, monat);

    int anzahlWerktage = tageDesMonats.stream().filter(tag -> {
      return tag.getWochentag() < 6 && !tag.istFeiertag();
    }).toList().size();
    sollstunden = new BigDecimal(anzahlWerktage).multiply(stellenfaktor).multiply(new BigDecimal(8))
        .setScale(2, HALF_UP);

    return sollstunden;
  }

  public SonderarbeitszeitWochentagVerteilung getSonderarbeitszeitWochentagVerteilung(
      final List<Projektstunde> sonderarbeitszeiten) {
    SonderarbeitszeitWochentagVerteilung verteilung = new SonderarbeitszeitWochentagVerteilung();
    verteilung.setWerktag(ZERO);
    verteilung.setSamstag(ZERO);
    verteilung.setSonntagFeiertag(ZERO);
    for (Projektstunde sonderarbeitszeit : sonderarbeitszeiten) {
      Kalender tag = kalenderService.leseKalenderByDatum(sonderarbeitszeit.getTagVon());
      if (tag != null) {
        if (!tag.istFeiertag() && tag.getWochentag() < 6) {
          verteilung.setWerktag(verteilung.getWerktag().add(sonderarbeitszeit.getAnzahlStunden())
              .setScale(2, HALF_UP));
        } else if (!tag.istFeiertag() && tag.getWochentag() == 6) {
          verteilung.setSamstag(verteilung.getSamstag().add(sonderarbeitszeit.getAnzahlStunden())
              .setScale(2, HALF_UP));
        } else if (tag.istFeiertag() || tag.getWochentag() == 7) {
          verteilung.setSonntagFeiertag(
              verteilung.getSonntagFeiertag().add(sonderarbeitszeit.getAnzahlStunden())
                  .setScale(2, HALF_UP));
        }
      }
    }
    return verteilung;
  }

  public String aktuelleDatumLesbar() {
    LocalDate localDate = LocalDate.now();//For reference
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return localDate.format(formatter);
  }
}
