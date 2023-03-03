package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Vertrag;
import java.math.BigDecimal;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VertragRepository extends CrudRepository<Vertrag, Long> {

  @Query("""
      WITH filterMitarbeiterUndProjekt AS (
      SELECT
      	*
      FROM Vertrag
      WHERE
      	(
      	-- Gültigkeit 1. Fall
      	(IstAktiv = 1 AND
      	 GueltigAb IS NULL AND
      	 GueltigBis IS NULL)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) BETWEEN GueltigAb AND GueltigBis)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) > GueltigAb AND
      	 GueltigBis IS NULL AND
      	 IstAktiv = 1)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) < GueltigBis AND
      	 GueltigAb IS NULL AND
      	 IstAktiv = 1)
      	) AND
      	ProjektID = :projektId AND
      	MitarbeiterID = :mitarbeiterId
      ),
      filterProjekt AS (
      SELECT
      	*
      FROM Vertrag
      WHERE
      	(
      	-- Gültigkeit 1. Fall
      	(IstAktiv = 1 AND
      	 GueltigAb IS NULL AND
      	 GueltigBis IS NULL)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) BETWEEN GueltigAb AND GueltigBis)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) > GueltigAb AND
      	 GueltigBis IS NULL AND
      	 IstAktiv = 1)
      	 OR
      	-- Parameter Jahr und Monat
      	(DATEFROMPARTS(:jahr,:monat,1) < GueltigBis AND
      	 GueltigAb IS NULL AND
      	 IstAktiv = 1)
      	) AND
      	ProjektID = :projektId AND
      	MitarbeiterID IS NULL AND NOT EXISTS
      	(SELECT 'x' FROM filterMitarbeiterUndProjekt WHERE MitarbeiterID = :mitarbeiterId)
      )
      SELECT MAX(Stundensatz) as Stundensatz
      FROM (
      SELECT *
      FROM
      	filterMitarbeiterUndProjekt
      UNION ALL
      SELECT *
      FROM
      	filterProjekt
      ) zusammenfuehrung  
      """)
  BigDecimal leseMaximalenGueltigenStundensatzZuMitarbeiterUndOderProjekt(
      final Long mitarbeiterId, final Long projektId, final Integer jahr, final Integer monat);
}
