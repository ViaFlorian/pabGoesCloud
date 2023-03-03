package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.MitarbeiterUrlaubKonto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitarbeiterUrlaubKontoRepository extends
    CrudRepository<MitarbeiterUrlaubKonto, Long> {

  Iterable<MitarbeiterUrlaubKonto> findAllByMitarbeiterId(Long mitarbeiterId);
}
