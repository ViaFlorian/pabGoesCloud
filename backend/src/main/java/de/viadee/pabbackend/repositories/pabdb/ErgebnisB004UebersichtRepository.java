package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004Uebersicht;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004UebersichtRepository extends
    CrudRepository<ErgebnisB004Uebersicht, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Mitarbeiter.ID MitarbeiterID,
      	Mitarbeiter.SachbearbeiterID SachbearbeiterID,
      	Arbeitsnachweis.ID ArbeitsnachweisID,
      	Arbeitsnachweis.StatusID,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdNormal,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'ang_Reise')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdReise,
          CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder')
          		THEN AnzahlStunden
          		ELSE 0
          END stdSonder,
          CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Ruf')
          		THEN AnzahlStunden
          		ELSE 0
          END stdRuf
      FROM
      	Arbeitsnachweis LEFT JOIN
      	Projektstunde ON Projektstunde.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Mitarbeiter ON Mitarbeiter.ID = Arbeitsnachweis.MitarbeiterID
      WHERE
          Mitarbeiter.ID = COALESCE(:mitarbeiterId, Mitarbeiter.ID) AND
          Mitarbeiter.SachbearbeiterID = COALESCE(:sachbearbeiterId, Mitarbeiter.SachbearbeiterID) AND
          Arbeitsnachweis.Jahr = :jahr AND
          Arbeitsnachweis.Monat = :monat AND
          Arbeitsnachweis.StatusID = COALESCE(:statusId, Arbeitsnachweis.StatusID) AND
          Arbeitsnachweis.StatusID >= 40
      )
      SELECT
      	MitarbeiterID,
      	SachbearbeiterID,
      	StatusID,
      	ArbeitsnachweisID,
      	SUM(stdNormal) summeNormal,
      	SUM(stdReise) summeReise,
      	SUM(stdRuf) summeRufbereitschaft,
      	SUM(stdSonder) summeSonderarbeitszeit
      FROM
      	vorselektion
      GROUP BY
      	MitarbeiterID,
          SachbearbeiterID,
          ArbeitsnachweisID,
          StatusID
      """)
  List<ErgebnisB004Uebersicht> ladeB004Uebersicht(final int jahr, final int monat,
      final Integer statusId, final Long mitarbeiterId, final Long sachbearbeiterId);
}
