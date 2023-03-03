package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Details;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008ExcelDetailsSpesenRepository extends
    CrudRepository<ErgebnisB008Details, Long> {

  @Query("""
      SELECT
          ab.TagVon AS Datum,
          DAY(ab.TagVon) AS Tag,
          MONTH(ab.TagVon) AS Monat,
          NULL AS Text,
          NULL AS AnzahlStunden,
          SUM(ab.Spesen) AS Betrag
      FROM
          Abwesenheit ab
              INNER JOIN
          Arbeitsnachweis anw ON ab.ArbeitsnachweisID = anw.ID
      WHERE
          (:projektId IS NULL OR ab.ProjektID = :projektId)
          AND (:jahr IS NULL OR anw.Jahr = :jahr)
          AND (:monat IS NULL OR anw.Monat = :monat)
          AND (:mitarbeiterId IS NULL OR anw.MitarbeiterID = :mitarbeiterId)
      GROUP BY
          ab.TagVon
      ORDER BY
          Datum ASC
           """)
  List<ErgebnisB008Details> ladeB008ExcelDetailsSpesen(Long projektId, Integer jahr,
      Integer monat, Long mitarbeiterId);
}
