package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ArbeitsnachweisLohnartZuordnung;
import de.viadee.pabbackend.entities.LohnartberechnungLog;
import de.viadee.pabbackend.repositories.pabdb.ArbeitsnachweisLohnartZuordnungRepository;
import de.viadee.pabbackend.repositories.pabdb.LohnartberechnungLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ArbeitsnachweisLohnartZuordnungService {

  private final ArbeitsnachweisLohnartZuordnungRepository arbeitsnachweisLohnartZuordnungRepository;
  private final LohnartberechnungLogRepository lohnartberechnungLogRepository;


  public ArbeitsnachweisLohnartZuordnungService(
      ArbeitsnachweisLohnartZuordnungRepository arbeitsnachweisLohnartZuordnungRepository,
      LohnartberechnungLogRepository lohnartberechnungLogRepository) {
    this.arbeitsnachweisLohnartZuordnungRepository = arbeitsnachweisLohnartZuordnungRepository;
    this.lohnartberechnungLogRepository = lohnartberechnungLogRepository;
  }

  public void loescheArbeitsnachweisLohnartZuordnungByArbeitsnachweisID(
      final Long arbeitsnachweisId) {
    arbeitsnachweisLohnartZuordnungRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }

  public void loescheLohnartberechnungLogByArbeitsnachweisID(final Long arbeitsnachweisId) {
    lohnartberechnungLogRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }

  public Iterable<ArbeitsnachweisLohnartZuordnung> speichereArbeitsnachweisLohnartZuordnung(final
  List<ArbeitsnachweisLohnartZuordnung> arbeitsnachweisLohnartZuordnung) {
    if (!arbeitsnachweisLohnartZuordnung.isEmpty()) {
      return arbeitsnachweisLohnartZuordnungRepository.saveAll(arbeitsnachweisLohnartZuordnung);
    }
    return null;
  }

  public Iterable<LohnartberechnungLog> speichereLohnartberechnungLog(
      List<LohnartberechnungLog> lohnartberechnungLog) {
    if (!lohnartberechnungLog.isEmpty()) {
      return lohnartberechnungLogRepository.saveAll(lohnartberechnungLog);
    }
    return null;
  }
}
