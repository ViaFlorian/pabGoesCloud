package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.BuchungstypStundenKonstante;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuchungstypStundenRepository extends
    CrudRepository<BuchungstypStundenKonstante, Long> {

  @Cacheable("buchungstypStunden")
  BuchungstypStundenKonstante findFirstByBezeichnung(String bezeichnung);
}
