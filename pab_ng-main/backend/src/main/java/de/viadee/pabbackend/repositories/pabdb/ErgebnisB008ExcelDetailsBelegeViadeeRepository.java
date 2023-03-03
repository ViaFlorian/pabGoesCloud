package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Details;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008ExcelDetailsBelegeViadeeRepository extends
    CrudRepository<ErgebnisB008Details, Long> {

  @Query("""
      SELECT
          NULL AS Tag,
          Monat,
          C_BelegArt.TextKurz AS Text,
          SonstigeProjektkosten.Bemerkung,
          NULL AS AnzahlStunden,
          Kosten AS Betrag
      FROM
          SonstigeProjektkosten
              LEFT OUTER JOIN
          C_BelegArt ON SonstigeProjektkosten.BelegArtID = C_BelegArt.ID
              LEFT OUTER JOIN
          C_ViadeeAuslagenKostenart ON SonstigeProjektkosten.KostenartID = C_ViadeeAuslagenKostenart.ID
      WHERE
          C_ViadeeAuslagenKostenart.Bezeichnung = 'Reise'
          AND (:projektId IS NULL OR ProjektID = :projektId)
          AND (:mitarbeiterId IS NULL OR MitarbeiterID = :mitarbeiterId)
          AND (:jahr IS NULL OR SonstigeProjektkosten.Jahr = :jahr)
          AND (:monat IS NULL OR SonstigeProjektkosten.Monat = :monat)
          AND SonstigeProjektkosten.Monat = 9
      ORDER BY
          Jahr ASC,
          Monat ASC
           """)
  List<ErgebnisB008Details> ladeB008ExcelDetailsBelegeViadee(Long projektId, Integer jahr,
      Integer monat, Long mitarbeiterId);
}
