package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Details;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008ExcelDetailsReisezeitenRepository extends
    CrudRepository<ErgebnisB008Details, Long> {

  @Query("""
      SELECT
          TagVon,
          DAY(TagVon) AS Tag,
          MONTH(TagVon) AS Monat,
          NULL AS Text,
          SUM(AnzahlStunden) AS AnzahlStunden,
          NULL AS Betrag
      FROM
          Projektstunde
              INNER JOIN
          Arbeitsnachweis ON ArbeitsnachweisID = Arbeitsnachweis.ID
      WHERE
          ProjektstundeTypID = (
              SELECT
                  ID
              FROM
                  C_ProjektstundeTyp
              WHERE
                  TextKurz = 'tat_Reise'
          )
          AND (:projektId IS NULL OR Projektstunde.ProjektID = :projektId)
          AND (:jahr IS NULL OR Arbeitsnachweis.Jahr = :jahr)
          AND (:monat IS NULL OR Arbeitsnachweis.Monat = :monat)
          AND (:mitarbeiterId IS NULL OR MitarbeiterID = :mitarbeiterId)
      GROUP BY
          TagVon
      ORDER BY
          TagVon ASC
           """)
  List<ErgebnisB008Details> ladeB008ExcelDetailsReisezeiten(Long projektId, Integer jahr,
      Integer monat, Long mitarbeiterId);
}
