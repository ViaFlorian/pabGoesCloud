package de.viadee.pabbackend.repositories.pabdb;


import de.viadee.pabbackend.entities.LohnartberechnungLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import de.viadee.pabbackend.entities.LohnartberechnungLog;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LohnartberechnungLogRepository extends CrudRepository<LohnartberechnungLog, Long> {

  Iterable<LohnartberechnungLog> findLohnartberechnungLogByArbeitsnachweisId(
      final Long arbeitsnachweisId);

  @Query("""
      delete
      from
      	LohnartenberechnungLog
      where
      	ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
