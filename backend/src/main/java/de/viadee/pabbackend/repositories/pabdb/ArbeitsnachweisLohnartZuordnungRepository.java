package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ArbeitsnachweisLohnartZuordnung;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbeitsnachweisLohnartZuordnungRepository extends
    CrudRepository<ArbeitsnachweisLohnartZuordnung, Long> {

  @Query("""
      delete
      from
      	ArbeitsnachweisLohnartZuordnung
      where
      	ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);


  Iterable<ArbeitsnachweisLohnartZuordnung> findLohnartZuordnungByArbeitsnachweisId(
      final Long arbeitsnachweisId);
}
