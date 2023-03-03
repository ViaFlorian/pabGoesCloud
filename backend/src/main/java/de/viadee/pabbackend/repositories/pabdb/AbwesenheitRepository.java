package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Abwesenheit;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbwesenheitRepository extends CrudRepository<Abwesenheit, Long> {

  Iterable<Abwesenheit> findAbwesenheitenByArbeitsnachweisId(final Long arbeitsnachweisId);

  @Query("""
      SELECT
      	Abwesenheit.*
      FROM
      	Kunde INNER JOIN
      			Projekt ON
      				Kunde.ScribeID = Projekt.KundeID INNER JOIN
      			Abwesenheit ON
      				Abwesenheit.ProjektID = Projekt.ID INNER JOIN
      			Arbeitsnachweis ON
      				Abwesenheit.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      			Mitarbeiter ON
      				Arbeitsnachweis.MitarbeiterID = Mitarbeiter.ID AND
      				Mitarbeiter.ID = :mitarbeiterId

      WHERE
        	TagVon >= ISNULL((
        	        SELECT MAX(gueltigVon) 
        	        FROM DreiMonatsRegel 
        	        WHERE 
        	            DreiMonatsRegel.AutomatischErfasst = 1 AND 
        	            DreiMonatsRegel.KundeID = Kunde.ScribeID AND 
        	            DreiMonatsRegel.Arbeitsstaette = Abwesenheit.Arbeitsstaette AND 
        	            DreiMonatsRegel.MitarbeiterID = MitarbeiterID AND 
        	            MitarbeiterID = :mitarbeiterId AND 
        	            NOT gueltigBis IS NULL AND NOT DATEFROMPARTS(DATEPART(yy, gueltigBis), DATEPART(mm, gueltigBis),1) >= DATEFROMPARTS(:jahr,:monat,1) 
        	        GROUP BY 
        	            DreiMonatsRegel.KundeID, 
        	            DreiMonatsRegel.MitarbeiterID)
        	        , 
        	        DATEADD(yy, -100, GETDATE())
        	)
      """)
  Iterable<Abwesenheit> findDreiMonatsRegelKandidatenByMitarbeiterId(Long mitarbeiterId, int jahr,
      int monat);

  @Query("""
      DELETE FROM Abwesenheit
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
