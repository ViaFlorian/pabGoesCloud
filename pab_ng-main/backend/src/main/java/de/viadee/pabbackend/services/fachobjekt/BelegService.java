package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Beleg;
import de.viadee.pabbackend.repositories.pabdb.BelegRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class BelegService {

  private final BelegRepository belegRepository;

  public BelegService(BelegRepository belegRepository) {
    this.belegRepository = belegRepository;
  }

  public List<Beleg> alleBelegeByArbeitsnachweisId(Long arbeitsnachweisId) {
    return IterableUtils.toList(belegRepository.findBelegeByArbeitsnachweisId(arbeitsnachweisId));
  }

  public void loescheBelege(List<Beleg> belege) {
    belegRepository.deleteAll(belege);
  }

  public Iterable<Beleg> speichereBelege(List<Beleg> aktualisierteBelege) {
    return belegRepository.saveAll(aktualisierteBelege);
  }

  public void loescheByArbeitsnachweisID(Long arbeitsnachweisId) {
    belegRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }
}
