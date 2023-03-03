package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.DreiMonatsRegel;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DreiMonatsRegelRepository extends CrudRepository<DreiMonatsRegel, Long> {

  @Query("""
      SELECT
      	*
      FROM
      	DreiMonatsRegel
      WHERE
      	(gueltigBis IS NULL OR
      	DATEFROMPARTS(:jahr, :monat, 1) between DATEFROMPARTS(DATEPART(yy, gueltigVon), DATEPART(mm, gueltigVon), 1) AND gueltigBis) AND
      	AutomatischErfasst = 0 AND
      	MitarbeiterID = :mitarbeiterId
      """)
  Iterable<DreiMonatsRegel> findManuelleDreiMonatsRegelnFuerAbrechnungsmonat(Long mitarbeiterId,
      int jahr, int monat);

    @Query("""
      SELECT
      	*
      FROM
      	DreiMonatsRegel
      WHERE
      	(gueltigBis IS NULL OR
      	DATEFROMPARTS(:jahr, :monat, 1) between DATEFROMPARTS(DATEPART(yy, gueltigVon), DATEPART(mm, gueltigVon), 1) AND gueltigBis) AND
      	MitarbeiterID = :mitarbeiterId
      """)
    Iterable<DreiMonatsRegel> findDreiMonatsRegelnFuerAbrechnungsmonat(Long mitarbeiterId, int jahr,
        int monat);



    @Query("""
      DELETE
      FROM
      	DreiMonatsRegel
      WHERE
      	AutomatischErfasst = 1 AND
      	GueltigBis IS NULL AND
      	MitarbeiterID = :mitarbeiterId
      """)
  @Modifying
  void deleteAutomatischErfassteRegelnByMitarbeiterId(Long mitarbeiterId);

  @Query("""
      DELETE
      FROM
      	DreiMonatsRegel
      WHERE
      	Arbeitsstaette = :arbeitsstaette AND
      	KundeID = :kundeId AND
      	MitarbeiterID = :mitarbeiterId AND
         	(:gueltigVon between gueltigVon and gueltigBis OR
         	 :gueltigVon <= gueltigVon) AND
      	(COALESCE(:gueltigBis, gueltigBis) between gueltigVon and gueltigBis OR
      	 COALESCE(:gueltigBis, gueltigBis) >= gueltigBis OR
      	 gueltigBis IS NULL)
      """)
  @Modifying
  void deleteKollidierendeRegeln(String arbeitsstaette, String kundeId, Long mitarbeiterId,
      LocalDate gueltigVon, LocalDate gueltigBis);
}
