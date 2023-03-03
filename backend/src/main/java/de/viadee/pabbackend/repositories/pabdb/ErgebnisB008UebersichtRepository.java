package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Uebersicht;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008UebersichtRepository extends
    CrudRepository<ErgebnisB008Uebersicht, Long> {

  @Query("""
      WITH
      relevanteProjektMonate AS (
      	SELECT DISTINCT
      		Jahr,
      		Monat,
      		ProjektID
      	FROM
      		Projektabrechnung
      			INNER JOIN
      		Projekt ON ProjektID = Projekt.ID
      	WHERE
      		Projekt.Projekttyp = 'Dienstleistung'  -- Es werden nur Dienstleistungsprojekte berücksichtigt.
      		AND Projektabrechnung.StatusID >= 40   -- Die Projektabrechnung muss mindestens "abgerechnet" sein.
      		AND DATEFROMPARTS(Jahr, Monat, 1) BETWEEN
      			DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      		AND (:projektId IS NULL OR ProjektID = :projektId)
      		AND (:kundeScribeId IS NULL OR Projekt.KundeID = :kundeScribeId)
      		AND (:oeScribeId IS NULL OR Projekt.OrganisationseinheitID = :oeScribeId)
      		AND (:sachbearbeiterId IS NULL OR Projekt.SachbearbeiterID = :sachbearbeiterId)
      ),
      projektzeitKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Kostensatz) AS Kosten,
      		SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Stundensatz) AS Leistungen
      	FROM
      		Projektabrechnung
      			INNER JOIN
      		ProjektabrechnungProjektzeit ON
      			ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      reisezeitKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM(ProjektabrechnungReise.AngerechneteReisezeit * ProjektabrechnungReise.Kostensatz) AS Kosten,
      		SUM(ProjektabrechnungReise.TatsaechlicheReisezeit * ProjektabrechnungReise.Stundensatz) AS Leistungen
      	FROM
      		Projektabrechnung
      			INNER JOIN
      		ProjektabrechnungReise ON
      			ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      reisekostenKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM (
      			CASE WHEN BelegeViadeeKosten IS NULL AND BelegeLtArbeitsnachweisKosten IS NULL AND SpesenKosten IS NULL AND ZuschlaegeKosten IS NULL
      				THEN NULL
      				ELSE COALESCE(ProjektabrechnungReise.BelegeViadeeKosten, 0)
      					+ COALESCE(ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten, 0)
      					+ COALESCE(ProjektabrechnungReise.SpesenKosten, 0)
      					+ COALESCE(ProjektabrechnungReise.ZuschlaegeKosten, 0)
      			END
      		) AS Kosten,
      		SUM(
      			CASE WHEN BelegeViadeeLeistung IS NULL AND BelegeLtArbeitsnachweisLeistung IS NULL AND SpesenLeistung IS NULL AND ZuschlaegeLeistung IS NULL
      					AND (PauschaleAnzahl IS NULL OR PauschaleProTag IS NULL)
      				THEN NULL
      				ELSE COALESCE(ProjektabrechnungReise.BelegeViadeeLeistung, 0)
      					+ COALESCE(ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung, 0)
      					+ COALESCE(ProjektabrechnungReise.SpesenLeistung, 0)
      					+ COALESCE(ProjektabrechnungReise.ZuschlaegeLeistung, 0)
      					+ COALESCE(ProjektabrechnungReise.PauschaleAnzahl * ProjektabrechnungReise.PauschaleProTag, 0)
      			END
      		) AS Leistungen
      	FROM
      		Projektabrechnung
      			INNER JOIN
      		ProjektabrechnungReise ON
      			ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      sonstigeKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM(
      			CASE WHEN AuslagenViadee IS NULL AND PauschaleKosten IS NULL
      				THEN NULL
      				ELSE COALESCE(ProjektabrechnungSonstige.AuslagenViadee, 0)
      					+ COALESCE(ProjektabrechnungSonstige.PauschaleKosten, 0)
      			END
      		) AS Kosten,
      		SUM(ProjektabrechnungSonstige.PauschaleLeistungen) AS Leistungen
      	FROM
      		Projektabrechnung INNER JOIN
      		ProjektabrechnungSonstige ON
      			ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      rufbereitschaftKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM(ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz) AS Kosten,
      		SUM(
      			CASE WHEN (RufbereitschaftLeistungAnzahlStunden IS NULL OR RufbereitschaftStundensatz IS NULL) AND RufbereitschaftPauschale IS NULL
      				THEN NULL
      				ELSE COALESCE(ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz, 0)
      					+ COALESCE(ProjektabrechnungSonderarbeit.RufbereitschaftPauschale, 0)
      			END
      		) AS Leistungen
      	FROM
      		Projektabrechnung INNER JOIN
      		ProjektabrechnungSonderarbeit ON
      			ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      sonderarbeitKostenLeistung AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		SUM(
      			CASE WHEN (SonderarbeitAnzahlStunden100 IS NULL OR SonderarbeitKostensatz IS NULL) AND (SonderarbeitAnzahlStunden50 IS NULL OR SonderarbeitKostensatz IS NULL)
      					AND SonderarbeitPauschale IS NULL
      				THEN NULL
      				ELSE COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100 * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz, 0)
      					+ COALESCE(0.5 * ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50 * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz, 0)
      					+ COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitPauschale, 0)
      			END
      		) AS Kosten,
      		SUM(ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale) as Leistungen
      	FROM
      		Projektabrechnung INNER JOIN
      		ProjektabrechnungSonderarbeit ON
      			ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
      	GROUP BY
      		Jahr,
      		Monat,
      		ProjektID
      ),
      korrektur AS (
      	SELECT
      		Jahr,
      		Monat,
      		ProjektID,
      		CASE
      			WHEN (AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0)
      				THEN BetragKostensatz
      			ELSE
      				BetragKostensatz * AnzahlStundenKosten
      		END AS Kosten,
      		CASE
      			WHEN (AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0)
      				THEN BetragStundensatz
      			ELSE
      				BetragStundensatz * AnzahlStundenLeistung
      		END AS Leistungen
      	FROM
      		ProjektabrechnungKorrekturbuchung
      			LEFT OUTER JOIN
      		C_Kostenart ON KostenartID = C_Kostenart.ID
      			LEFT OUTER JOIN
      		Projekt ON ProjektID = Projekt.ID
      			LEFT OUTER JOIN
      		Kunde ON Projekt.KundeID = Kunde.ScribeID
      			LEFT OUTER JOIN
      		Organisationseinheit ON Projekt.OrganisationseinheitID = Organisationseinheit.ScribeID
      ),
      kostenLeistungenVereinigt AS (
      	SELECT * FROM projektzeitKostenLeistung
      	UNION ALL
      	SELECT * FROM reisezeitKostenLeistung
      	UNION ALL
      	SELECT * FROM reisekostenKostenLeistung
      	UNION ALL
      	SELECT * FROM sonstigeKostenLeistung
      	UNION ALL
      	SELECT * FROM rufbereitschaftKostenLeistung
      	UNION ALL
      	SELECT * FROM sonderarbeitKostenLeistung
      	UNION ALL
      	SELECT * FROM korrektur
      ),
      kostenLeistungenAggregiert AS (
      	SELECT
      		relevanteProjektMonate.ProjektID,
      		Projekt.Projektnummer,
      		Projekt.IstAktiv,
      		Projekt.Bezeichnung AS Projektbezeichnung,
      		relevanteProjektMonate.Jahr,
      		relevanteProjektMonate.Monat,
      		SUM(Kosten) AS Kosten,
      		SUM(Leistungen) AS Leistungen
      	FROM
      		kostenLeistungenVereinigt
      			INNER JOIN
      		relevanteProjektMonate ON
      			kostenLeistungenVereinigt.Jahr = relevanteProjektMonate.Jahr
      				AND kostenLeistungenVereinigt.Monat = relevanteProjektMonate.Monat
      				AND kostenLeistungenVereinigt.ProjektID = relevanteProjektMonate.ProjektID
      			INNER JOIN
      		Projekt ON Projekt.ID = relevanteProjektMonate.ProjektID
      	GROUP BY
      		relevanteProjektMonate.Jahr,
      		relevanteProjektMonate.Monat,
      		relevanteProjektMonate.ProjektID,
      		Projektnummer,
      		Projekt.IstAktiv,
      		Bezeichnung
      )
      	
      SELECT * FROM kostenLeistungenAggregiert
      ORDER BY
      	Projektnummer ASC,
      	ProjektID ASC,
      	Jahr DESC,
      	Monat DESC
           """)
  List<ErgebnisB008Uebersicht> ladeB008Uebersicht(final int abJahr, final int abMonat,
      final int bisJahr, final int bisMonat,
      Long projektId, String kundeScribeId, Long sachbearbeiterId, String oeScribeId);
}
