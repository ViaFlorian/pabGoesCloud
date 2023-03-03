package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Projektstunde;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektstundeRepository extends CrudRepository<Projektstunde, Long> {

  Iterable<Projektstunde> findProjektstundenByArbeitsnachweisId(final Long arbeitsnachweisId);

  Iterable<Projektstunde> findProjektstundenByArbeitsnachweisIdAndProjektstundeTypId(
      final Long arbeitsnachweisId, final Long projektstundeTypId);

  @Query("""
      DELETE FROM Projektstunde
      WHERE ArbeitsnachweisID = :arbeitsnachweisId AND
            ProjektstundeTypID = (select ID from C_ProjektstundeTyp where TextKurz = 'ang_Reise')
      """)
  void deleteBerechneteProjektstunden(Long arbeitsnachweisId);

  @Query("""
      DELETE FROM Projektstunde
      WHERE ArbeitsnachweisID = :arbeitsnachweisId AND
            ProjektstundeTypID = :projektstundeTypId
      """)
  @Modifying
  void deleteProjektstundeByArbeitsnachweisIdAndProjektstundeTypId(Long arbeitsnachweisId,
      Long projektstundeTypId);

  @Query("""
          DELETE FROM Projektstunde
          WHERE ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
