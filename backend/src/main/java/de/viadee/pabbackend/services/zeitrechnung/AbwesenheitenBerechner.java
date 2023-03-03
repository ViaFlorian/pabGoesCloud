package de.viadee.pabbackend.services.zeitrechnung;

import de.viadee.pabbackend.entities.Abwesenheit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AbwesenheitenBerechner {

  public boolean istAnreisetag(final List<Abwesenheit> listeAbwesenheiten,
      final Abwesenheit abwesenheit) {
    final boolean hatNachfolgendenTag = listeAbwesenheiten.stream()
        .anyMatch(
            andereAbw -> hatNachfolgendenTag(abwesenheit.getTagVon(), abwesenheit.getUhrzeitBis(),
                andereAbw.getTagVon(),
                andereAbw.getUhrzeitVon()));
    final boolean hatVorhergehendenTag = listeAbwesenheiten.stream()
        .anyMatch(
            andereAbw -> hatVorhergehendenTag(abwesenheit.getTagVon(), abwesenheit.getUhrzeitVon(),
                andereAbw.getTagVon(), andereAbw.getUhrzeitBis()));
    return hatNachfolgendenTag && !hatVorhergehendenTag;
  }

  public boolean istAbreisetag(final List<Abwesenheit> listeAbwesenheiten,
      final Abwesenheit abwesenheit) {
    final boolean hatNachfolgendenTag = listeAbwesenheiten.stream()
        .anyMatch(
            andereAbw -> hatNachfolgendenTag(abwesenheit.getTagVon(), abwesenheit.getUhrzeitBis(),
                andereAbw.getTagVon(), andereAbw.getUhrzeitVon()));
    final boolean hatVorhergehendenTag = listeAbwesenheiten.stream()
        .anyMatch(
            andereAbw -> hatVorhergehendenTag(abwesenheit.getTagVon(), abwesenheit.getUhrzeitVon(),
                andereAbw.getTagVon(), andereAbw.getUhrzeitBis()));
    return hatVorhergehendenTag && !hatNachfolgendenTag;
  }

  public boolean hatNachfolgendenTag(final LocalDate abwesenheitTagVon,
      final LocalTime abwesenheitUhrzeitBis,
      final LocalDate andereAbwesenheitTagVon, final LocalTime andereAbwesenheitUhrzeitVon) {

    boolean andererTagIstDirekterNachfolger = abwesenheitTagVon.plusDays(1)
        .equals(andereAbwesenheitTagVon);
    boolean aktuellerTagEndetUm2359Uhr = (abwesenheitUhrzeitBis.equals(LocalTime.MAX)
        || abwesenheitUhrzeitBis.equals(LocalTime.of(23, 59, 59)));
    boolean andererTagBeginnZurMinimalenUhrzeit = andereAbwesenheitUhrzeitVon.equals(
        LocalTime.of(0, 0, 0));

    final boolean hatNachfolgendenTag =
        andererTagIstDirekterNachfolger && aktuellerTagEndetUm2359Uhr
            && andererTagBeginnZurMinimalenUhrzeit;

    return hatNachfolgendenTag;
  }

  public boolean hatVorhergehendenTag(final LocalDate abwesenheitTagVon,
      final LocalTime abwesenheitUhrzeitVon,
      final LocalDate andereAbwesenheitTagVon, final LocalTime andereAbwesenheitUhrzeitBis) {

    boolean andererTagIstDirekterVorgaenger = abwesenheitTagVon.minusDays(1)
        .equals(andereAbwesenheitTagVon);
    boolean aktuellerTagStartetUm0Uhr = abwesenheitUhrzeitVon.equals(LocalTime.of(0, 0));
    boolean andererTagEndetZurMaximalenUhrzeit = (
        andereAbwesenheitUhrzeitBis.equals(LocalTime.of(23, 59, 59))
            || andereAbwesenheitUhrzeitBis.equals(LocalTime.MAX));

    final boolean hatVorhergehendenTag =
        andererTagIstDirekterVorgaenger && aktuellerTagStartetUm0Uhr
            && andererTagEndetZurMaximalenUhrzeit;

    return hatVorhergehendenTag;
  }

}
