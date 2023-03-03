package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Faktur;
import de.viadee.pabbackend.entities.FakturuebersichtSpeichernRequest;
import de.viadee.pabbackend.repositories.pabdb.FakturRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class FakturService {

  private final FakturRepository fakturRepository;

  public FakturService(FakturRepository fakturRepository) {
    this.fakturRepository = fakturRepository;
  }

  public List<Faktur> alleFakturenByProjektId(Long projektIdId) {
    return IterableUtils.toList(fakturRepository.findFaktursByProjektId(projektIdId));
  }

  public void speicherFakturen(FakturuebersichtSpeichernRequest fakturuebersichtSpeichernRequest) {
    fakturuebersichtSpeichernRequest.getGeloeschteFakturen().forEach(faktur -> {
      fakturRepository.deleteById(faktur.getId());
    });
    fakturuebersichtSpeichernRequest.getAktualisierteFakturen().forEach(fakturRepository::save);
    fakturuebersichtSpeichernRequest.getNeueFakturen().forEach(fakturRepository::save);
  }

}
