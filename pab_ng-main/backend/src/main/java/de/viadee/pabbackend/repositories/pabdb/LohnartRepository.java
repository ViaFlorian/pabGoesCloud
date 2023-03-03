package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.LohnartKonstante;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LohnartRepository extends CrudRepository<LohnartKonstante, Long> {

  @Cacheable("lohnart")
  LohnartKonstante findFirstByKonto(String konto);

}
