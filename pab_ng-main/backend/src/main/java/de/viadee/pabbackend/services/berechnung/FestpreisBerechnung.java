package de.viadee.pabbackend.services.berechnung;

import de.viadee.pabbackend.entities.LeistungKumuliert;
import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungBerechneteLeistung;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

@Service
public class FestpreisBerechnung {

    public List<ProjektabrechnungBerechneteLeistung> berechneFestpreis(
            final Projektabrechnung projektabrechnung,
            final BigDecimal budgetAlt,
            final BigDecimal aktuellesBudget,
            final BigDecimal fertigstellungAlt,
            final BigDecimal fertigstellungNeu,
            final List<LeistungKumuliert> vergangeneLeistungKumuliert,
            final BigDecimal ohneMitarbeiterbezugVerteilteLeistung,
            final List<ProjektabrechnungBerechneteLeistung> vergangeneBerechneteLeistung,
            final List<LeistungKumuliert> aktuelleLeistungKumuliert) {

        BigDecimal leistungGesamtLautFertigstellung =
                fertigstellungNeu
                        .divide(new BigDecimal("100.00"), 13, HALF_UP)
                        .multiply(aktuellesBudget)
                        .setScale(2, HALF_UP);
        BigDecimal bisherigeLeistung =
                fertigstellungAlt == null
                        ? ZERO
                        : fertigstellungAlt
                        .divide(new BigDecimal("100.00"), 13, HALF_UP)
                        .multiply(budgetAlt)
                        .setScale(2, HALF_UP);
        BigDecimal leistungGebucht = leistungGesamtLautFertigstellung.subtract(bisherigeLeistung);
        // Die gesamte gebuchte Leistung muss bereinigt werden:
        // Es kann insbesondere bei Produkten der Fall sein, dass monatelang Projekte ohne
        // Mitarbeiterbeteiligung
        // abgerechnet werden. Sobald ein Mitarbeiter
        BigDecimal gesamtLeistungGebucht =
                bisherigeLeistung.add(leistungGebucht).subtract(ohneMitarbeiterbezugVerteilteLeistung);

        // Alle Leistungen aus aktuellen Projektzeiten und vergangenen Berechnungen für
        // Gesamtbetrachtung zusammenfassen
        Map<Long, BigDecimal> alleRechnerischenLeistungenJeMitarbeiter = new HashMap<>();
        for (LeistungKumuliert aktuelleLeistungDesMitarbeiters : aktuelleLeistungKumuliert) {
            if (aktuelleLeistungDesMitarbeiters.getLeistung().compareTo(ZERO) > 0) {
                if (alleRechnerischenLeistungenJeMitarbeiter.get(
                        aktuelleLeistungDesMitarbeiters.getMitarbeiterId())
                        == null) {
                    alleRechnerischenLeistungenJeMitarbeiter.put(
                            aktuelleLeistungDesMitarbeiters.getMitarbeiterId(),
                            aktuelleLeistungDesMitarbeiters.getLeistung());
                } else {
                    BigDecimal aktualisierteAktuelleRechnerischeLeistung =
                            alleRechnerischenLeistungenJeMitarbeiter
                                    .get(aktuelleLeistungDesMitarbeiters.getMitarbeiterId())
                                    .add(aktuelleLeistungDesMitarbeiters.getLeistung())
                                    .setScale(13, HALF_UP);
                    alleRechnerischenLeistungenJeMitarbeiter.put(
                            aktuelleLeistungDesMitarbeiters.getMitarbeiterId(),
                            aktualisierteAktuelleRechnerischeLeistung);
                }
            }
        }

        for (LeistungKumuliert vergangeneLeistungDesMitarbeiters : vergangeneLeistungKumuliert) {
            if (vergangeneLeistungDesMitarbeiters.getLeistung().compareTo(ZERO) > 0) {
                if (alleRechnerischenLeistungenJeMitarbeiter.get(
                        vergangeneLeistungDesMitarbeiters.getMitarbeiterId())
                        == null) {
                    alleRechnerischenLeistungenJeMitarbeiter.put(
                            vergangeneLeistungDesMitarbeiters.getMitarbeiterId(),
                            vergangeneLeistungDesMitarbeiters.getLeistung());
                } else {
                    BigDecimal aktualisierteAktuelleRechnerischeLeistung =
                            alleRechnerischenLeistungenJeMitarbeiter
                                    .get(vergangeneLeistungDesMitarbeiters.getMitarbeiterId())
                                    .add(vergangeneLeistungDesMitarbeiters.getLeistung())
                                    .setScale(13, HALF_UP);
                    alleRechnerischenLeistungenJeMitarbeiter.put(
                            vergangeneLeistungDesMitarbeiters.getMitarbeiterId(),
                            aktualisierteAktuelleRechnerischeLeistung);
                }
            }
        }

        BigDecimal rechnerischeLeistungGesamt = ZERO;

        for (Long mitarbeiterId : alleRechnerischenLeistungenJeMitarbeiter.keySet()) {
            BigDecimal rechnerischeMitarbeiterLeistung =
                    alleRechnerischenLeistungenJeMitarbeiter.get(mitarbeiterId);
            rechnerischeLeistungGesamt = rechnerischeLeistungGesamt.add(rechnerischeMitarbeiterLeistung);
        }

        Map<Long, BigDecimal> anteilRechnerischeLeistungProMitarbeiterKumuliert = new HashMap<>();
        for (Long mitarbeiterId : alleRechnerischenLeistungenJeMitarbeiter.keySet()) {
            BigDecimal mitarbeiterLeistungKumuliert =
                    alleRechnerischenLeistungenJeMitarbeiter.get(mitarbeiterId);
            BigDecimal anteilRechnerischeLeistung = ZERO;
            try {
                anteilRechnerischeLeistung =
                        mitarbeiterLeistungKumuliert.divide(rechnerischeLeistungGesamt, 13, HALF_UP);
            } catch (ArithmeticException ae) {

            }
            anteilRechnerischeLeistungProMitarbeiterKumuliert.put(
                    mitarbeiterId, anteilRechnerischeLeistung);
        }

        // Nur dann, wenn es mindestens einen Mitarbeiter gibt, der einen rechnerischen Anteil > 0
        // hat, darf nach Anteilen verteilt werden. Ansonsten muss alles auf den Pseudo-Mitarbeiter
        // gebucht werden
        int anzahlMitarbeiterMitEinemAnteilUngleich0 = 0;
        for (Long mitarbeiterId : anteilRechnerischeLeistungProMitarbeiterKumuliert.keySet()) {
            if (anteilRechnerischeLeistungProMitarbeiterKumuliert.get(mitarbeiterId).compareTo(ZERO)
                    > 0) {
                anzahlMitarbeiterMitEinemAnteilUngleich0++;
            }
        }

        List<ProjektabrechnungBerechneteLeistung> aktuelleBerechneteLeistungen = new ArrayList<>();

        if (anzahlMitarbeiterMitEinemAnteilUngleich0 > 0) {

            HashMap<Long, BigDecimal> leistungGebuchtJeMitarbeiter = new HashMap<>();
            for (Long mitarbeiterId : anteilRechnerischeLeistungProMitarbeiterKumuliert.keySet()) {
                BigDecimal anteilKumuliertFuerMitarbeiter =
                        anteilRechnerischeLeistungProMitarbeiterKumuliert.get(mitarbeiterId);
                BigDecimal leistungMitarbeiterAnGesamtleistung =
                        anteilKumuliertFuerMitarbeiter.multiply(gesamtLeistungGebucht).setScale(2, HALF_UP);
                BigDecimal bisherVerbuchteLeistungFuerMitarbeiter =
                        vergangeneBerechneteLeistung.stream()
                                .filter(vergangeneProjektzeit -> {
                                    if (mitarbeiterId == null && vergangeneProjektzeit.getMitarbeiterId() == null) {
                                        return true;
                                    }
                                    if (mitarbeiterId == null || vergangeneProjektzeit.getMitarbeiterId() == null) {
                                        return false;
                                    }
                                    return vergangeneProjektzeit.getMitarbeiterId().equals(mitarbeiterId);
                                })
                                .map(vergangeneProjektzeit -> vergangeneProjektzeit.getLeistung())
                                .reduce(ZERO, BigDecimal::add);
                BigDecimal verbuchteLeistungMonat =
                        leistungMitarbeiterAnGesamtleistung.subtract(bisherVerbuchteLeistungFuerMitarbeiter);
                leistungGebuchtJeMitarbeiter.put(
                        mitarbeiterId, verbuchteLeistungMonat.setScale(2, HALF_UP));
            }

            for (Long mitarbeiterId : leistungGebuchtJeMitarbeiter.keySet()) {
                ProjektabrechnungBerechneteLeistung projektabrechnungBerechneteLeistung =
                        new ProjektabrechnungBerechneteLeistung();

                projektabrechnungBerechneteLeistung.setMitarbeiterId(mitarbeiterId);
                projektabrechnungBerechneteLeistung.setProjektabrechnungId(projektabrechnung.getId());
                projektabrechnungBerechneteLeistung.setLeistung(
                        leistungGebuchtJeMitarbeiter.get(mitarbeiterId));
                aktuelleBerechneteLeistungen.add(projektabrechnungBerechneteLeistung);
            }

        } else {
            ProjektabrechnungBerechneteLeistung projektabrechnungBerechneteLeistung =
                    new ProjektabrechnungBerechneteLeistung();
            Mitarbeiter mitarbeiter = new Mitarbeiter();
            mitarbeiter.setId(Long.MIN_VALUE);
            projektabrechnungBerechneteLeistung.setMitarbeiterId(mitarbeiter.getId());
            projektabrechnungBerechneteLeistung.setProjektabrechnungId(projektabrechnung.getId());
            projektabrechnungBerechneteLeistung.setLeistung(leistungGebucht);
            aktuelleBerechneteLeistungen.add(projektabrechnungBerechneteLeistung);
        }

        // Aus den einzelnen Rundungen kann sich eine Rundungsdifferenz ergeben, diese wird dem MA mit
        // dem höchsten Betrag zugeordnet
        BigDecimal leistungVerteilt =
                aktuelleBerechneteLeistungen.stream()
                        .map(leistung -> leistung.getLeistung())
                        .reduce(ZERO, BigDecimal::add);
        BigDecimal rundungsDifferenz = leistungGebucht.setScale(2, HALF_UP).subtract(leistungVerteilt);

        // Rundungsdifferenz aktueller Monat
        ProjektabrechnungBerechneteLeistung hoechsteLeistung = null;
        if (rundungsDifferenz.compareTo(ZERO) != 0) {
            for (ProjektabrechnungBerechneteLeistung leistung : aktuelleBerechneteLeistungen) {
                if (hoechsteLeistung == null
                        || leistung.getLeistung().compareTo(hoechsteLeistung.getLeistung()) > 0) {
                    hoechsteLeistung = leistung;
                }
            }
            hoechsteLeistung.setLeistung(hoechsteLeistung.getLeistung().add(rundungsDifferenz));
        }

        BigDecimal leistungVerteiltBereinigtUmRundungsdifferenzInAktuellerVerteilung =
                aktuelleBerechneteLeistungen.stream()
                        .map(leistung -> leistung.getLeistung())
                        .reduce(ZERO, BigDecimal::add)
                        .setScale(2, HALF_UP);

        // Rundungsdifferenz bei Sonderfall "Fertigstellungsgrad = 100%
        if (fertigstellungNeu.compareTo(new BigDecimal("100")) == 0) {

            BigDecimal summeVergangenerBerechneterLeistung =
                    vergangeneBerechneteLeistung.stream()
                            .map(leistung -> leistung.getLeistung())
                            .reduce(ZERO, BigDecimal::add)
                            .setScale(2, HALF_UP);

            BigDecimal rundungsdifferenzGesamt =
                    aktuellesBudget
                            .subtract(summeVergangenerBerechneterLeistung)
                            .subtract(leistungVerteiltBereinigtUmRundungsdifferenzInAktuellerVerteilung);

            if (rundungsdifferenzGesamt.compareTo(ZERO) != 0 && hoechsteLeistung != null) {
                hoechsteLeistung.setLeistung(hoechsteLeistung.getLeistung().add(rundungsdifferenzGesamt));
            }
        }

        return aktuelleBerechneteLeistungen;
    }
}
