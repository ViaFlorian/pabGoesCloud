package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Abrechnungsmonat;
import de.viadee.pabbackend.entities.Arbeitsnachweis;
import de.viadee.pabbackend.entities.ArbeitsnachweisUebersicht;
import de.viadee.pabbackend.entities.MitarbeiterAbrechnungsmonat;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbeitsnachweisRepository extends CrudRepository<Arbeitsnachweis, Long> {

  @Query("""
      WITH
          SummeBelege AS
            (SELECT ArbeitsnachweisID,
                    SUM(Betrag) AS SummeBelege
             FROM Beleg
             GROUP BY ArbeitsnachweisID
            ) ,
          SummeSpesen AS
            (SELECT ArbeitsnachweisID,
                    SUM(Spesen + Zuschlag) AS SummeSpesen
             FROM Abwesenheit
             GROUP BY ArbeitsnachweisID
            ),
          SummeProjektstunden AS
            (SELECT ArbeitsnachweisID,
                    SUM(AnzahlStunden) AS SummeProjektstunden
             FROM Projektstunde
             WHERE (ProjektstundeTypID IN
                    (SELECT ID
                     FROM C_ProjektstundeTyp
                     WHERE TextKurz = 'Normal'
                        OR TextKurz    = 'ang_Reise'
                    ) )
             GROUP BY ArbeitsnachweisID
            )
      SELECT Arbeitsnachweis.Jahr,
           Arbeitsnachweis.Monat,
           Mitarbeiter.SachbearbeiterID,
           COALESCE(SummeProjektstunden.SummeProjektstunden, 0) AS SummeProjektstunden,
           COALESCE(SummeSpesen.SummeSpesen, 0)                 AS SummeSpesen,
           COALESCE(SummeBelege.SummeBelege, 0)                 AS SummeBelege,
           Arbeitsnachweis.StatusID,
           Arbeitsnachweis.ID AS ArbeitsnachweisID,
           Mitarbeiter.ID     AS MitarbeiterID
      FROM Arbeitsnachweis
             LEFT OUTER JOIN SummeProjektstunden
                             ON SummeProjektstunden.ArbeitsnachweisID = Arbeitsnachweis.ID
             LEFT OUTER JOIN SummeSpesen
                             ON SummeSpesen.ArbeitsnachweisID = Arbeitsnachweis.ID
             LEFT OUTER JOIN SummeBelege
                             ON SummeBelege.ArbeitsnachweisID = Arbeitsnachweis.ID
             INNER JOIN Mitarbeiter
                        ON Mitarbeiter.ID = Arbeitsnachweis.MitarbeiterID
      WHERE DATEFROMPARTS(Jahr, Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      """)
  Iterable<ArbeitsnachweisUebersicht> findAlleArbeitsnachweiseFuerUebersichtGefiltert(
      final int abJahr,
      final int abMonat,
      final int bisJahr,
      final int bisMonat);

  @Query("""
      SELECT DISTINCT Arbeitsnachweis.Jahr, Arbeitsnachweis.Monat, 
      CASE WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL 
        THEN 'FALSE'
        ELSE 'TRUE'
      END as istAbgeschlossen
      FROM Arbeitsnachweis 
        LEFT JOIN AbgeschlosseneMonate
          ON
            Arbeitsnachweis.Jahr = AbgeschlosseneMonate.Jahr AND
            Arbeitsnachweis.Monat = AbgeschlosseneMonate.Monat
      """)
  Iterable<Abrechnungsmonat> findAlleAbrechnungsmonate();

  @Query("""
      SELECT DISTINCT Arbeitsnachweis.ID as ArbeitsnachweisID, Arbeitsnachweis.Jahr, Arbeitsnachweis.Monat,
        CASE WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL 
          THEN 'FALSE'
          ELSE 'TRUE'
        END as istAbgeschlossen
      FROM Arbeitsnachweis
          LEFT JOIN AbgeschlosseneMonate
          ON
            Arbeitsnachweis.Jahr = AbgeschlosseneMonate.Jahr AND
            Arbeitsnachweis.Monat = AbgeschlosseneMonate.Monat
      WHERE
          MitarbeiterID = :mitarbeiterId
      """)
  Iterable<MitarbeiterAbrechnungsmonat> findAlleAbrechnungsmonateByMitarbeiterId(
      final Long mitarbeiterId);

  @Query("""
      UPDATE Arbeitsnachweis
           SET
                 StatusID = :statusId
      WHERE
      		ID = :arbeitsnachweisId
      """)
  @Modifying
  void updateStatusId(Long arbeitsnachweisId, int statusId);


  Arbeitsnachweis findByMitarbeiterIdAndJahrAndMonat(Long mitarbeiterId, int jahr, int monat);

  @Query("""
      SELECT
            *
      FROM
            Arbeitsnachweis INNER JOIN
            Mitarbeiter ON
                 Arbeitsnachweis.MitarbeiterID = Mitarbeiter.ID
      WHERE
            StatusID <= :statusID and
            Jahr = :jahr and
            Monat = :monat
      """)
  Iterable<Arbeitsnachweis> alleArbeitsnachweiseKleinerGleichStatusFuerAbrechnungsmonat(
      int statusID, int jahr,
      int monat);

}
