package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Skonto;
import de.viadee.pabbackend.repositories.pabdb.SkontoRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class SkontoService {

  private final SkontoRepository skontoRepository;

  public SkontoService(SkontoRepository skontoRepository) {
    this.skontoRepository = skontoRepository;
  }

  public List<Skonto> alleSkontosByProjektId(Long projektIdId) {
    return IterableUtils.toList(skontoRepository.findSkontosByProjektId(projektIdId));
  }

}
