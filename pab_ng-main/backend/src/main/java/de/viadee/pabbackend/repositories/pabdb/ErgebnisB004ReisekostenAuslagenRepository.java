package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB004ReisekostenAuslagenPdf;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB004ReisekostenAuslagenRepository extends
    CrudRepository<ErgebnisB004ReisekostenAuslagenPdf, Long> {

  @Query("""
      WITH vorselektion AS (
      SELECT
      	Projekt.Projektnummer,
          Projekt.Bezeichnung,
      	CASE    WHEN lower(Kunde.Kurzbezeichnung) = 'viadee'
      	        THEN ''
      	        ELSE Kunde.Bezeichnung
      	END AS KundeBezeichnung,
      	Beleg.Betrag
      FROM
      	Beleg INNER JOIN
      	Projekt ON Beleg.ProjektID = Projekt.ID INNER JOIN
      	Kunde ON Projekt.KundeID = Kunde.ScribeID INNER JOIN
      	Kalender ON Kalender.Datum = Beleg.Datum INNER JOIN
      	C_BelegArt ON C_BelegArt.ID = Beleg.BelegArtID
      WHERE ArbeitsnachweisID = :arbeitsnachweisId
      )
      SELECT
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung,
      	SUM(Betrag) summeBetrag
      FROM
      	vorselektion
      GROUP BY
      	Projektnummer,
      	Bezeichnung,
      	KundeBezeichnung
      HAVING SUM(Betrag) > 0
      """)
  List<ErgebnisB004ReisekostenAuslagenPdf> ladeB004ReisekostenAuslagenFuerArbeitsnachweis(
      Long arbeitsnachweisId);

}
