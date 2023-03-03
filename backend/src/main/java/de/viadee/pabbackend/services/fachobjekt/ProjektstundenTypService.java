package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.repositories.pabdb.ProjektstundeTypKonstanteRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjektstundenTypService {

  private final ProjektstundeTypKonstanteRepository projektstundeTypKonstanteRepository;

  public ProjektstundenTypService(
      final ProjektstundeTypKonstanteRepository projektstundeTypKonstanteRepository) {
    this.projektstundeTypKonstanteRepository = projektstundeTypKonstanteRepository;
  }

  public ProjektstundeTypKonstante ladeProjektstundenTypByTextKurz(String textKurz) {
    return this.projektstundeTypKonstanteRepository.findByTextKurz(textKurz);
  }
}
