package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Fehlerlog;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FehlerlogRepository extends CrudRepository<Fehlerlog, Long> {

  @Query("""
          DELETE FROM Fehlerlog
          WHERE ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);

  Iterable<Fehlerlog> findFehlerlogByArbeitsnachweisId(final long arbeitsnachweisId);


}
