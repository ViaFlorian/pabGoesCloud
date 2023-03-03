package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Details;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008ExcelDetailsBelegeANWRepository extends
    CrudRepository<ErgebnisB008Details, Long> {

  @Query("""
      SELECT
          Datum,
          MitarbeiterID,
          DAY(Datum) AS Tag,
          MONTH(Datum) AS Monat,
          C_BelegArt.TextKurz AS Text,
          NULL AS AnzahlStunden,
          SUM(Betrag) AS Betrag
      FROM
          Beleg
              INNER JOIN
          Arbeitsnachweis ON ArbeitsnachweisID = Arbeitsnachweis.ID
              INNER JOIN
          C_BelegArt ON BelegArtID = C_BelegArt.ID
      WHERE
          (:projektId is NULL OR Beleg.ProjektID = :projektId)
          AND (:jahr IS NULL OR Arbeitsnachweis.Jahr = :jahr)
          AND (:monat IS NULL OR Arbeitsnachweis.Monat = :monat)
          AND (:mitarbeiterId IS NULL OR Arbeitsnachweis.MitarbeiterID = :mitarbeiterId)
      GROUP BY
          Datum,
          MitarbeiterID,
          C_BelegArt.TextKurz
      ORDER BY
          Datum ASC
           """)
  List<ErgebnisB008Details> ladeB008ExcelDetailsBelegeANW(Long projektId, Integer jahr,
      Integer monat, Long mitarbeiterId);
}
