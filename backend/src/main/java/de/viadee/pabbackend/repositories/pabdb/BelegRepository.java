package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Beleg;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BelegRepository extends CrudRepository<Beleg, Long> {

  Iterable<Beleg> findBelegeByArbeitsnachweisId(final Long arbeitsnachweisId);

  @Query("""
      DELETE FROM BELEG
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
