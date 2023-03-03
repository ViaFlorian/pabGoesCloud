package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitarbeiterStellenfaktorRepository
    extends CrudRepository<MitarbeiterStellenfaktor, Long> {

  @Query("""
      SELECT TOP 1 * FROM MitarbeiterStellenfaktor
      WHERE
              MitarbeiterID = :mitarbeiterId AND
          DATEFROMPARTS(:jahr, :monat, 1) BETWEEN DATEFROMPARTS(DATEPART(year, GueltigAb),
                                                                DATEPART(month, GueltigAb),
                                                                1)
              AND
              COALESCE(
                      DATEFROMPARTS(DATEPART(year, GueltigBis),
                                    DATEPART(month, GueltigBis),
                                    1),
                      DATEFROMPARTS(DATEPART(year, SYSDATETIME()),
                                    DATEPART(month, SYSDATETIME()),
                                    1))
      ORDER BY GueltigAb DESC
      """)
  MitarbeiterStellenfaktor findFirstByMitarbeiterIdAndGueltigAbLessThanEqual(
      Long mitarbeiterId, Integer jahr, Integer monat);
}
