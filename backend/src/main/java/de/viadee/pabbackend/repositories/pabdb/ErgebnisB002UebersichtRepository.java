package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB002Uebersicht;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB002UebersichtRepository extends
    CrudRepository<ErgebnisB002Uebersicht, Long> {

  @Query("""
      WITH rufbereitschaft AS (
          SELECT
              SUM(pstd.AnzahlStunden) AS AnzahlStunden,
              pstd.ArbeitsnachweisID
          FROM
              Projektstunde pstd
          WHERE
              pstd.ProjektstundeTypID = (
                  select
                      ID
                  from
                      C_ProjektstundeTyp
                  WHERE
                      TextKurz = 'Ruf'
              )
          GROUP BY
              pstd.ArbeitsnachweisID
      ),
      sonderarbeitszeit AS (
          SELECT
              SUM(pstd.AnzahlStunden) AS AnzahlStunden,
              pstd.ArbeitsnachweisID
          FROM
              Projektstunde pstd
          WHERE
              pstd.ProjektstundeTypID = (
                  select
                      ID
                  from
                      C_ProjektstundeTyp
                  WHERE
                      TextKurz = 'Sonder'
              )
          GROUP BY
              pstd.ArbeitsnachweisID
      ),
      beideTypen AS (
          SELECT
              *
          FROM
              rufbereitschaft
          UNION
          ALL
          SELECT
              *
          FROM
              sonderarbeitszeit
      ),
      resultat AS (
          SELECT
              anw.Monat,
              anw.Jahr,
              mitarbeiter.Vorname AS mitarbeitervorname,
              mitarbeiter.Nachname AS mitarbeiternachname,
              mitarbeiter.Kurzname AS mitarbeiterkurzname,
              mitarbeiter.PersonalNr AS mitarbeiterpersonalnummer,
              mitarbeiter.IstAktiv AS mitarbeiteristaktiv,
              sachbearbeiter.Vorname AS sachbearbeitervorname,
              sachbearbeiter.Nachname AS sachbearbeiternachname,
              SUM(beideTypen.AnzahlStunden) AS stunden
          FROM
              beideTypen LEFT JOIN
              Arbeitsnachweis anw ON
                  anw.ID = beideTypen.ArbeitsnachweisID INNER JOIN
              Mitarbeiter mitarbeiter ON
                  anw.MitarbeiterID = mitarbeiter.ID INNER JOIN
              Mitarbeiter sachbearbeiter ON
                  mitarbeiter.SachbearbeiterID = sachbearbeiter.ID
          WHERE
              (anw.Monat = :monat AND anw.Jahr = :jahr)
              AND mitarbeiter.ID = coalesce(:mitarbeiterId, mitarbeiter.ID)
              AND sachbearbeiter.ID = coalesce(:sachbearbeiterId, sachbearbeiter.ID)
              AND sachbearbeiter.ID = coalesce(:sachbearbeiterId, sachbearbeiter.ID)
              AND anw.StatusID >= 20
          GROUP BY
              anw.Jahr, anw.Monat,
              mitarbeiter.Vorname,
              mitarbeiter.Nachname,
              mitarbeiter.Kurzname,
              mitarbeiter.PersonalNr,
              mitarbeiter.IstAktiv,
              sachbearbeiter.Vorname,
              sachbearbeiter.Nachname
      )
      SELECT
          *
      FROM
          resultat
      """)
  List<ErgebnisB002Uebersicht> ladeB002Uebersicht(final int jahr,
      final int monat,
      final Long mitarbeiterId, final Long sachbearbeiterId);

}
