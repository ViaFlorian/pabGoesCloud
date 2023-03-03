package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.BuchungstypUrlaubKonstante;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuchungstypUrlaubRepository extends
    CrudRepository<BuchungstypUrlaubKonstante, Long> {

  @Cacheable("buchungstypUrlaub")
  BuchungstypUrlaubKonstante findFirstByBezeichnung(String bezeichnung);
}
