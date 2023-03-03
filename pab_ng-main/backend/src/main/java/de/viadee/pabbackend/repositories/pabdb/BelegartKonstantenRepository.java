package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.BelegartKonstante;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BelegartKonstantenRepository extends CrudRepository<BelegartKonstante, Long> {

  @Cacheable("belegart")
  BelegartKonstante findByTextKurz(final String textKurz);

  @Override
  @Cacheable("belegartById")
  Optional<BelegartKonstante> findById(Long id);
}
