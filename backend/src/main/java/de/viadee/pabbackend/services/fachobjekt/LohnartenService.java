package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ArbeitsnachweisLohnartZuordnung;
import de.viadee.pabbackend.repositories.pabdb.ArbeitsnachweisLohnartZuordnungRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class LohnartenService {

  private final ArbeitsnachweisLohnartZuordnungRepository arbeitsnachweisLohnartZuordnungRepository;

  public LohnartenService(
      ArbeitsnachweisLohnartZuordnungRepository arbeitsnachweisLohnartZuordnungRepository) {
    this.arbeitsnachweisLohnartZuordnungRepository = arbeitsnachweisLohnartZuordnungRepository;
  }


  public List<ArbeitsnachweisLohnartZuordnung> lohnartZuordnungenByArbeitsnachweisId(
      Long arbeitsnachweisId) {
    return IterableUtils.toList(
        arbeitsnachweisLohnartZuordnungRepository.findLohnartZuordnungByArbeitsnachweisId(
            arbeitsnachweisId));
  }
}
