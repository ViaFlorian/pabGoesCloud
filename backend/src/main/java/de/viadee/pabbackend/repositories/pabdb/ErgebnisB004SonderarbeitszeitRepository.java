package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004SonderarbeitszeitPdf;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004SonderarbeitszeitRepository extends
    CrudRepository<ErgebnisB004SonderarbeitszeitPdf, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Projekt.Projektnummer,
      	Projekt.Bezeichnung,
      	CASE    WHEN lower(Kunde.Kurzbezeichnung) = 'viadee'
      	        THEN ''
      	        ELSE Kunde.Bezeichnung
      	END AS KundeBezeichnung,
      	CASE	WHEN ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder')
      			THEN AnzahlStunden
      			ELSE 0
      	END stdSonder,
      	CASE	WHEN     ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder')
      	             AND Kalender.Wochentag < 6
      				 AND Kalender.IstFeiertag = 0
      			THEN AnzahlStunden
      			ELSE 0
      	END stdWerktag,
      	CASE	WHEN     ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder')
      	             AND Kalender.Wochentag = 6
      				 AND Kalender.IstFeiertag = 0
      			THEN AnzahlStunden
      			ELSE 0
      	END stdSamstag,
      	CASE	WHEN     ProjektstundeTypID = (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder')
      				 AND (Kalender.IstFeiertag = 1 OR Kalender.Wochentag = 7)
      			THEN AnzahlStunden
      			ELSE 0
      	END stdSonntagFeiertag
      FROM
      	Projektstunde INNER JOIN
      	Projekt ON Projektstunde.ProjektID = Projekt.ID INNER JOIN
      	Kunde ON Projekt.KundeID = Kunde.ScribeID INNER JOIN
      	Kalender ON Kalender.Datum = Projektstunde.TagVon
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      )
      SELECT
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung,
      	SUM(stdSonder) summeSonder,
      	SUM(stdWerktag) summeWerktag,
      	SUM(stdSamstag) summeSamstag,
      	SUM(stdSonntagFeiertag) summeSonntagFeiertag
      FROM
      	vorselektion
      GROUP BY
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung
      HAVING SUM(stdSonder) > 0
      """)
  List<ErgebnisB004SonderarbeitszeitPdf> ladeB004SonderarbeitszeitFuerArbeitsnachweis(
      Long arbeitsnachweisId);

}
