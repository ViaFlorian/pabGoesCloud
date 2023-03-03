package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004AbwesenheitPdf;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004AbwesenheitRepository extends
    CrudRepository<ErgebnisB004AbwesenheitPdf, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Projekt.Projektnummer,
      	Projekt.Bezeichnung,
      	CASE    WHEN lower(Kunde.Kurzbezeichnung) = 'viadee'
      	        THEN ''
      	        ELSE Kunde.Bezeichnung
      	END AS KundeBezeichnung,
      	Abwesenheit.UhrzeitVon,
      	Abwesenheit.UhrzeitBis,
      	CASE WHEN DATEDIFF(second, Abwesenheit.UhrzeitVon, Abwesenheit.UhrzeitBis)  % 60 = 59
      	     THEN (DATEDIFF(second, Abwesenheit.UhrzeitVon, Abwesenheit.UhrzeitBis) + 1) / 60
      		 ELSE DATEDIFF(second, Abwesenheit.UhrzeitVon, Abwesenheit.UhrzeitBis) / 60
      	END stunden,
      	CASE WHEN	LEAD(Abwesenheit.UhrzeitVon) OVER (ORDER BY TagVon, UhrzeitVon) = '00:00:00' AND
      				UhrzeitBis = '23:59:59' AND
      				UhrzeitVon <> '00:00:00'
      		 THEN	'a'
      		 ELSE NULL
      	END anreise,
      	CASE WHEN	LAG(Abwesenheit.UhrzeitBis) OVER (ORDER BY TagVon, UhrzeitVon) = '23:59:59' AND
      				UhrzeitVon = '00:00:00' AND
      				UhrzeitBis <> '23:59:59'
      		 THEN	'a'
      		 ELSE NULL
      	END abreise
      FROM
      	Abwesenheit INNER JOIN
      	Projekt ON Abwesenheit.ProjektID = Projekt.ID INNER JOIN
      	Kunde ON Projekt.KundeID = Kunde.ScribeID INNER JOIN
      	Kalender ON Kalender.Datum = Abwesenheit.TagVon
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      )
      SELECT
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung,
      	SUM(CASE	WHEN	stunden = (24 * 60) AND
      					anreise IS NULL AND
      					abreise IS NULL
      			THEN	1
      			ELSE	0
      	END) anzahlTage24,
      	SUM(CASE	WHEN	anreise IS NOT NULL OR
      					abreise IS NOT NULL
      			THEN	1
      			ELSE	0
      	END) anzahlTageAnreiseAbreise,
      	SUM(CASE	WHEN	stunden > (8 * 60) AND
      					stunden < (24 * 60) AND
      					anreise IS NULL AND
      					abreise IS NULL
      			THEN	1
      			ELSE	0
      	END) anzahlTageUeberAcht
      FROM
      	vorselektion
      GROUP BY
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung
      """)
  List<ErgebnisB004AbwesenheitPdf> ladeB004AbwesenheitFuerArbeitsnachweis(Long arbeitsnachweisId);


}
