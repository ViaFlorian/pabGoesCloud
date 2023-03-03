package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Fehlerlog;
import de.viadee.pabbackend.repositories.pabdb.FehlerlogRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class FehlerlogService {

  private final FehlerlogRepository fehlerlogRepository;


  public FehlerlogService(FehlerlogRepository fehlerlogRepository) {
    this.fehlerlogRepository = fehlerlogRepository;
  }

  public List<Fehlerlog> alleFehlerlogsByArbeitsnachweisId(Long arbeitsnachweisId) {
    return IterableUtils.toList(
        fehlerlogRepository.findFehlerlogByArbeitsnachweisId(arbeitsnachweisId));
  }

  public void loescheFehlerlogByArbeitsnachweisId(Long arbeitsnachweisId) {
    fehlerlogRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }

  public void speichereFehlerlog(List<Fehlerlog> fehlerlog) {
    fehlerlogRepository.saveAll(fehlerlog);
  }
}
