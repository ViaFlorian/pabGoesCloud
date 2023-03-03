package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Mitarbeiter;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitarbeiterRepository extends CrudRepository<Mitarbeiter, Long> {

  @Query("""
         SELECT *
         FROM Mitarbeiter
         WHERE
         NOT EXISTS
         	(
         		SELECT 1
         		FROM Arbeitsnachweis
         		WHERE
         			Arbeitsnachweis.Jahr = :jahr AND
         			Arbeitsnachweis.Monat = :monat AND
         			Arbeitsnachweis.MitarbeiterID = Mitarbeiter.ID
         	) AND
         	(
         	  Mitarbeiter.IstAktiv = 1
         	) AND
         	(
         		(
         		  Mitarbeiter.IstIntern = 1 AND
         		  Mitarbeiter.MitarbeiterTypID IN (SELECT ID FROM C_MitarbeiterTyp WHERE TextKurz = 'Angestellter' OR TextKurz = 'Studi/Prakti')
         	  ) OR 
         	  (
         		  Mitarbeiter.IstIntern = 0
         	  )
         	) AND
          NOT (
            DATEFROMPARTS(:jahr, :monat, 1) BETWEEN COALESCE(Mitarbeiter.PauseVon, SYSDATETIME()) AND COALESCE(Mitarbeiter.PauseBis, SYSDATETIME()) AND 
            DATEADD(day, -1, DATEADD(month, 1, DATEFROMPARTS(:jahr, :monat, 1))) BETWEEN COALESCE(Mitarbeiter.PauseVon, SYSDATETIME()) AND COALESCE(Mitarbeiter.PauseBis, SYSDATETIME())
          ) AND 
          DATEFROMPARTS(:jahr, :monat, 1) BETWEEN DATEFROMPARTS(DATEPART(yyyy, Mitarbeiter.Eintrittsdatum), DATEPART(mm, Mitarbeiter.Eintrittsdatum),1)  AND Mitarbeiter.Austrittsdatum
      """)
  Iterable<Mitarbeiter> mitarbeiterUndStudentenDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
      final int jahr, final int monat);

  @Query("""
        SELECT *
        FROM Mitarbeiter
        WHERE
          ((:aktiveMitarbeiter = 'true' AND IstAktiv = 1) OR (:inaktiveMitarbeiter = 'true' AND IstAktiv = 0)) AND
          ((:interneMitarbeiter = 'true' AND IstIntern = 1) OR (:externeMitarbeiter = 'true' AND IstIntern = 0)) AND
          (
            :beruecksichtigeEintrittsdatum = 'false' OR
            (
              :beruecksichtigeEintrittsdatum = 'true' AND
              DATEFROMPARTS(DATEPART(yyyy,SYSDATETIME()), DATEPART(mm, SYSDATETIME()), 1) >= Mitarbeiter.Eintrittsdatum
            )
          ) OR
          (
            :alleMitarbeiterMitArbeitsnachweis = 'true' AND Mitarbeiter.ID IN
            (
              SELECT
                MitarbeiterID
              FROM
                Arbeitsnachweis
                )
          )         
      """)
  Iterable<Mitarbeiter> mitarbeiterSelectOptions(boolean aktiveMitarbeiter,
      boolean inaktiveMitarbeiter, boolean interneMitarbeiter, boolean externeMitarbeiter,
      boolean beruecksichtigeEintrittsdatum, boolean alleMitarbeiterMitArbeitsnachweis);

  @Query("""
      SELECT TOP(1) * FROM Mitarbeiter ORDER BY ID DESC
      """)
  Mitarbeiter letzterMitarbeiter();

  Optional<Mitarbeiter> findByPersonalNr(String personalnummer);

  @Query("""
      SELECT
               ma.*
             , C_MitarbeiterTyp.TextKurz
             , SB.Vorname  AS SBVorname
             , SB.Nachname AS SBNachname
      FROM
               Mitarbeiter ma  INNER JOIN
               C_MitarbeiterTyp ON
                 C_MitarbeiterTyp.ID = ma.MitarbeiterTypID LEFT JOIN
               Mitarbeiter SB
                ON
                  SB.ID = ma.SachbearbeiterID
      WHERE
      NOT EXISTS
             (
              SELECT
                  1
                 FROM
                  Arbeitsnachweis anw
                 WHERE\s
                  anw.Jahr = :jahr AND
                     anw.Monat =  :monat AND
                     anw.MitarbeiterID = ma.ID
            ) AND
             (
                ma.IstAktiv      = 1 AND
                 ma.IstIntern     = 0
             )
             AND
             NOT (DATEFROMPARTS(:jahr, :monat, 1) BETWEEN COALESCE(ma.PauseVon, SYSDATETIME()) AND COALESCE(ma.PauseBis, SYSDATETIME())
              AND
                  DATEADD(day, -1, DATEADD(month, 1, DATEFROMPARTS(:jahr, :monat, 1))) BETWEEN COALESCE(ma.PauseVon, SYSDATETIME()) AND COALESCE(ma.PauseBis, SYSDATETIME()))
             AND DATEFROMPARTS(:jahr, :monat, 1) BETWEEN DATEFROMPARTS(DATEPART(yyyy, ma.Eintrittsdatum), DATEPART(mm, ma.Eintrittsdatum),1)  AND ma.Austrittsdatum

      """)
  Iterable<Mitarbeiter> externeMitarbeiterDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(int jahr,
      int monat);
}
