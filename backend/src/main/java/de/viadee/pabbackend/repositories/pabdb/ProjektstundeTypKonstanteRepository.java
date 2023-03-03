package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektstundeTypKonstanteRepository extends
    CrudRepository<ProjektstundeTypKonstante, Long> {


  @Query("""
      SELECT *
      FROM C_ProjektstundeTyp
      WHERE TextKurz = :textKurz
      """)
  @Cacheable("projektstundeTypKonstante")
  ProjektstundeTypKonstante findByTextKurz(final String textKurz);
}
