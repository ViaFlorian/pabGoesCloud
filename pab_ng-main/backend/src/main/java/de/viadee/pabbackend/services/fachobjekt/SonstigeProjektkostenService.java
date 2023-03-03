package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.SonstigeProjektkosten;
import de.viadee.pabbackend.repositories.pabdb.SonstigeProjektkostenRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class SonstigeProjektkostenService {

  private final SonstigeProjektkostenRepository sonstigeProjektkostenRepository;

  public SonstigeProjektkostenService(
      final SonstigeProjektkostenRepository sonstigeProjektkostenRepository) {
    this.sonstigeProjektkostenRepository = sonstigeProjektkostenRepository;
  }

  public List<SonstigeProjektkosten> ladeAlleSonstigeProjektkosten() {
    return IterableUtils.toList(this.sonstigeProjektkostenRepository.findAll());
  }

  public SonstigeProjektkosten ladeSonstigeProjektkostenById(Long projektkostenId) {
    return this.sonstigeProjektkostenRepository.findById(projektkostenId).orElse(null);
  }

  public void loescheSonstigeProjektkostenByID(
      List<SonstigeProjektkosten> geloeschteSonstigeProjektkosten) {
    if (geloeschteSonstigeProjektkosten.size() < 1) {
      return;
    }
    this.sonstigeProjektkostenRepository.deleteAll(geloeschteSonstigeProjektkosten);
  }

  public void aktualisiereSonstigeProjektkostenByID(
      List<SonstigeProjektkosten> aktualisierteSonstigeProjektkosten) {
    if (aktualisierteSonstigeProjektkosten.size() < 1) {
      return;
    }
    this.sonstigeProjektkostenRepository.saveAll(aktualisierteSonstigeProjektkosten);
  }

  public void fuegeSonstigeProjektkostenEin(
      List<SonstigeProjektkosten> neueSonstigeProjektkosten) {
    if (neueSonstigeProjektkosten.size() < 1) {
      return;
    }
    this.sonstigeProjektkostenRepository.saveAll(neueSonstigeProjektkosten);
  }

  List<SonstigeProjektkosten> ladeSonstigeProjektkostenByMonatJahrMitarbeiterProjektID(
      Integer monat,
      Integer jahr, long mitarbeiterID, Long projektId) {
    return this.sonstigeProjektkostenRepository.ladeSonstigeProjektkostenByMonatJahrMitarbeiterProjektID(
        projektId, mitarbeiterID, monat, jahr);
  }
}
