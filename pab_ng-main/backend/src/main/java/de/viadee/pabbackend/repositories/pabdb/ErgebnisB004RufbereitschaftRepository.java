package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004RufbereitschaftPdf;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004RufbereitschaftRepository extends
    CrudRepository<ErgebnisB004RufbereitschaftPdf, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Projekt.Projektnummer,
      	Projekt.Bezeichnung,
      	CASE    WHEN lower(Kunde.Kurzbezeichnung) = 'viadee'
      	        THEN ''
      	        ELSE Kunde.Bezeichnung
      	END AS KundeBezeichnung,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Ruf')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdRuf
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
      	SUM(stdRuf) summeRuf
      FROM
      	vorselektion
      GROUP BY
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung
      HAVING SUM(stdRuf) > 0
      """)
  List<ErgebnisB004RufbereitschaftPdf> ladeB004RufbereitschaftFuerArbeitsnachweis(
      Long arbeitsnachweisId);

}
