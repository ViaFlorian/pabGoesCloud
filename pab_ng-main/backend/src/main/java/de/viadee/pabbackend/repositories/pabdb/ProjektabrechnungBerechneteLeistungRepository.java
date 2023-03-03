package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektabrechnungBerechneteLeistung;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungBerechneteLeistungRepository
    extends CrudRepository<ProjektabrechnungBerechneteLeistung, Long> {

  Iterable<ProjektabrechnungBerechneteLeistung> findAllByProjektabrechnungId(
      Long projektabrechnungId);

  void deleteByProjektabrechnungId(Long projektabrechnungId);

  @Query("""
      SELECT
          ProjektabrechnungBerechneteLeistung.*,
          Projektabrechnung.*,
          null as stundensatzVormonat,
          null as kostensatzVormonat
        
        FROM
          ProjektabrechnungBerechneteLeistung INNER JOIN
          Projektabrechnung
            ON
              ProjektabrechnungBerechneteLeistung.ProjektabrechnungID = Projektabrechnung.ID AND
              Projektabrechnung.ProjektID = :projektId
        WHERE
          DATEFROMPARTS(Jahr, Monat, 1) < DATEFROMPARTS(:jahr, :monat , 1)
        """)
  Iterable<ProjektabrechnungBerechneteLeistung> ladeVergangeneLeistungen(Long projektId,
      Integer jahr, Integer monat);
}
