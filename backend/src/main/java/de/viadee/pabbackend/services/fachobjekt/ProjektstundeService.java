package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Projektstunde;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.repositories.pabdb.ProjektstundeRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjektstundeService {

  private final ProjektstundeRepository projektstundeRepository;
  private final ProjektstundenTypService projektstundenTypService;

  public ProjektstundeService(
      ProjektstundeRepository projektstundeRepository,
      ProjektstundenTypService projektstundenTypService) {
    this.projektstundeRepository = projektstundeRepository;
    this.projektstundenTypService = projektstundenTypService;
  }

  public List<Projektstunde> alleProjektstundenByArbeitsnachweisId(Long arbeitsnachweisId) {
    return IterableUtils.toList(
        projektstundeRepository.findProjektstundenByArbeitsnachweisId(arbeitsnachweisId));
  }

  public List<Projektstunde> alleProjektstundenByArbeitsnachweisIdUndTypTextKurz(
      Long arbeitsnachweisId, String textKurz) {

    ProjektstundeTypKonstante projektstundenTyp =
        projektstundenTypService.ladeProjektstundenTypByTextKurz(textKurz);

    return IterableUtils.toList(
        projektstundeRepository.findProjektstundenByArbeitsnachweisIdAndProjektstundeTypId(
            arbeitsnachweisId, projektstundenTyp.getId()));
  }

  public void loescheProjektstunden(List<Projektstunde> geloeschteProjektstunden) {
    projektstundeRepository.deleteAll(geloeschteProjektstunden);
  }

  public void speichereProjektstunden(List<Projektstunde> aktualisierteProjektstunden) {
    projektstundeRepository.saveAll(aktualisierteProjektstunden);
  }

  public void loescheProjekstundenByArbeitsnachweisUndTypTextKurz(
      Long arbeitsnachweisId, String textKurz) {
    ProjektstundeTypKonstante projektstundenTyp =
        projektstundenTypService.ladeProjektstundenTypByTextKurz(textKurz);
    projektstundeRepository.deleteProjektstundeByArbeitsnachweisIdAndProjektstundeTypId(
        arbeitsnachweisId, projektstundenTyp.getId());
  }

  public void loescheByArbeitsnachweisID(Long arbeitsnachweisId) {
    projektstundeRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }
}
