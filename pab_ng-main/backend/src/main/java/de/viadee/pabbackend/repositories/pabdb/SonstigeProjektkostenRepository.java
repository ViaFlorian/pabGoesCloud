package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.SonstigeProjektkosten;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonstigeProjektkostenRepository extends
    CrudRepository<SonstigeProjektkosten, Long> {

  @Query("""
      SELECT SonstigeProjektkosten.*,
             CASE
                 WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL THEN 0
                 ELSE 1
                 END as IstAbgeschlossen
      FROM SonstigeProjektkosten
               LEFT JOIN
           AbgeschlosseneMonate
           ON
                   AbgeschlosseneMonate.Jahr = SonstigeProjektkosten.Jahr AND
                   AbgeschlosseneMonate.Monat = SonstigeProjektkosten.Monat
      where ProjektID = :projektId
        AND COALESCE(MitarbeiterID, -1) = COALESCE(:mitarbeiterID, -1)
        AND SonstigeProjektkosten.Jahr = :jahr
        AND SonstigeProjektkosten.Monat = :monat
      """)
  List<SonstigeProjektkosten> ladeSonstigeProjektkostenByMonatJahrMitarbeiterProjektID(
      Long projektId, long mitarbeiterID, Integer monat, Integer jahr);
}
