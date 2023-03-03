package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.KostenartKonstante;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KostenartKonstanteRepository extends CrudRepository<KostenartKonstante, Long> {

  @Cacheable("kostenart")
  KostenartKonstante findFirstByBezeichnung(String bezeichnung);
}
