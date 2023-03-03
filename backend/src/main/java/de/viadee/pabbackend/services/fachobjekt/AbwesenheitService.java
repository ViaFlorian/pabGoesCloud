package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Abwesenheit;
import de.viadee.pabbackend.entities.ErgebnisBerechnungGesetzlicheSpesen;
import de.viadee.pabbackend.entities.ErgebnisBerechnungViadeeZuschlaege;
import de.viadee.pabbackend.repositories.pabdb.AbwesenheitRepository;
import de.viadee.pabbackend.services.berechnung.SpesenUndZuschlagsberechnung;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AbwesenheitService {

  private final AbwesenheitRepository abwesenheitRepository;
  private final SpesenUndZuschlagsberechnung spesenUndZuschlagsberechnung;

  public AbwesenheitService(AbwesenheitRepository abwesenheitRepository,
      SpesenUndZuschlagsberechnung spesenUndZuschlagsberechnung) {
    this.abwesenheitRepository = abwesenheitRepository;
    this.spesenUndZuschlagsberechnung = spesenUndZuschlagsberechnung;
  }

  public List<Abwesenheit> alleAbwesenheitenByArbeitsnachweisId(Long arbeitsnachweisId) {
    return IterableUtils.toList(
        abwesenheitRepository.findAbwesenheitenByArbeitsnachweisId(arbeitsnachweisId));
  }

  public List<Abwesenheit> dreiMonatsRegelKandidatenByMitarbeiterId(Long id, int jahr, int monat) {
    return IterableUtils.toList(
        abwesenheitRepository.findDreiMonatsRegelKandidatenByMitarbeiterId(id, jahr, monat));
  }

  @Transactional("pabDbTransactionManager")
  public void loescheAbwesenheiten(List<Abwesenheit> geloeschteAbwesenheiten) {
    abwesenheitRepository.deleteAll(geloeschteAbwesenheiten);
  }

  @Transactional("pabDbTransactionManager")
  public Iterable<Abwesenheit> speichereAbwensenheiten(
      List<Abwesenheit> aktualisierteAbwesenheiten) {
    return abwesenheitRepository.saveAll(aktualisierteAbwesenheiten);
  }

  public void berechneSpesenUndZuschlaegeUndUebertrageSieNachAbwesenheiten(
      final List<Abwesenheit> unveraenderteAbwesenheiten,
      final List<Abwesenheit> neueAbwesenheiten,
      final List<Abwesenheit> aktualisierteAbwesenheiten
  ) {

    final List<Abwesenheit> alleRelevantenAbwesenheiten = Stream.of(
        unveraenderteAbwesenheiten,
        neueAbwesenheiten,
        aktualisierteAbwesenheiten
    ).flatMap(list -> list.stream()).toList();

    final List<ErgebnisBerechnungGesetzlicheSpesen> gesetzlicheSpesen = spesenUndZuschlagsberechnung
        .berechneGesetzlicheSpesen(alleRelevantenAbwesenheiten);
    final List<ErgebnisBerechnungViadeeZuschlaege> viadeeZuschlaege = spesenUndZuschlagsberechnung
        .berechneViadeeZuschlaege(alleRelevantenAbwesenheiten);

    gesetzlicheSpesen.stream().forEach(spesen -> {
      spesen.getAbwesenheit().setSpesen(spesen.getGesetzlicheSpesen());
    });
    viadeeZuschlaege.stream().forEach(zuschlag -> {
      zuschlag.getAbwesenheit().setZuschlag(zuschlag.getViadeeZuschlaege());
    });

    aktualisierteAbwesenheiten.addAll(alleRelevantenAbwesenheiten);
    unveraenderteAbwesenheiten.clear();
  }

  public void loescheByArbeitsnachweisID(Long arbeitsnachweisId) {
    abwesenheitRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }
}
