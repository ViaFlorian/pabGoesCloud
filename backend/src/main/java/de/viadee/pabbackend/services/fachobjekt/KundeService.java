package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Kunde;
import de.viadee.pabbackend.repositories.pabdb.KundeRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class KundeService {

  private final KundeRepository kundeRepository;

  public KundeService(KundeRepository kundeRepository) {
    this.kundeRepository = kundeRepository;
  }

  public Kunde kundeBySribeId(final String scribeId) {
    return kundeRepository.findKundeByScribeId(scribeId);
  }

  public Kunde kundeById(final Long id) {
    return kundeRepository.findById(id).orElse(null);
  }

  public List<Kunde> alleKunden() {
    return IterableUtils.toList(kundeRepository.findAll());
  }
}
