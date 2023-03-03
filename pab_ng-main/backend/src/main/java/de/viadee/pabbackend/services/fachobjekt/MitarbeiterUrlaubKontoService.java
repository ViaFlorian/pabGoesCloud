package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.MitarbeiterUrlaubKonto;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterUrlaubKontoRepository;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterUrlaubKontoService {

  final MitarbeiterUrlaubKontoRepository mitarbeiterUrlaubKontoRepository;
  final KonstantenService konstantenService;
  final MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService;


  public MitarbeiterUrlaubKontoService(
      MitarbeiterUrlaubKontoRepository mitarbeiterUrlaubKontoRepository,
      KonstantenService konstantenService,
      MitarbeiterStellenfaktorService mitarbeiterStellenfaktorService) {
    this.mitarbeiterUrlaubKontoRepository = mitarbeiterUrlaubKontoRepository;
    this.konstantenService = konstantenService;
    this.mitarbeiterStellenfaktorService = mitarbeiterStellenfaktorService;
  }


  public List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontoByMitarbeiterId(Long mitarbeiterId) {
    List<MitarbeiterUrlaubKonto> mitarbeiterUrlaubKontoList = IterableUtils.toList(
        mitarbeiterUrlaubKontoRepository.findAllByMitarbeiterId(mitarbeiterId));
    Collections.sort(mitarbeiterUrlaubKontoList);
    return mitarbeiterUrlaubKontoList;
  }

  public void loescheMitarbeiterUrlaubKontosaetze(
      List<MitarbeiterUrlaubKonto> zuLoeschendeKontosaetze) {
    if (!zuLoeschendeKontosaetze.isEmpty()) {
      mitarbeiterUrlaubKontoRepository.deleteAll(zuLoeschendeKontosaetze);
    }
  }

  public void speichereMitarbeiterUrlaubkontosaetze(List<MitarbeiterUrlaubKonto> kontosaetze) {
    if (!kontosaetze.isEmpty()) {
      mitarbeiterUrlaubKontoRepository.saveAll(kontosaetze);
    }
  }


}
