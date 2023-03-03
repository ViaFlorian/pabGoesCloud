package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004ArbeitszeitPdf;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004ArbeitszeitRepository extends
    CrudRepository<ErgebnisB004ArbeitszeitPdf, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Projekt.Projektnummer,
      	Projekt.Bezeichnung,
      	CASE    WHEN lower(Kunde.Kurzbezeichnung) = 'viadee'
      	        THEN ''
      	        ELSE Kunde.Bezeichnung
      	END AS KundeBezeichnung,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdNormal,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'ang_Reise')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdReise
      FROM
      	Projektstunde INNER JOIN
      	Projekt ON Projektstunde.ProjektID = Projekt.ID INNER JOIN
          Kunde ON Projekt.KundeID = Kunde.ScribeID
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      )
      SELECT
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung,
      	SUM(stdNormal) summeNormal,
      	SUM(stdReise) summeReise
      FROM
      	vorselektion
      GROUP BY
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung
      """)
  List<ErgebnisB004ArbeitszeitPdf> ladeB004ArbeitszeitFuerArbeitsnachweis(Long arbeitsnachweisId);

}
