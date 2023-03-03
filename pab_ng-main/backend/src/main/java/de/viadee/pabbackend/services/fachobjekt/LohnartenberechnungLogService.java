package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.LohnartberechnungLog;
import de.viadee.pabbackend.repositories.pabdb.LohnartberechnungLogRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class LohnartenberechnungLogService {

  private final LohnartberechnungLogRepository lohnartberechnungLogRepository;


  public LohnartenberechnungLogService(
      LohnartberechnungLogRepository lohnartberechnungLogRepository) {
    this.lohnartberechnungLogRepository = lohnartberechnungLogRepository;
  }

  public List<LohnartberechnungLog> lohnartenberechnungLogByArbeitsnachweisId(
      Long arbeitsnachweisId) {
    return IterableUtils.toList(
        lohnartberechnungLogRepository.findLohnartberechnungLogByArbeitsnachweisId(
            arbeitsnachweisId));
  }
}
