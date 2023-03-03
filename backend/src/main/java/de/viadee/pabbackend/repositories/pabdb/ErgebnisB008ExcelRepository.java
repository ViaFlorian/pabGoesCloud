package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB008Excel;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB008ExcelRepository extends
    CrudRepository<ErgebnisB008Excel, Long> {

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
                 projektzeitenLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Geleistete Zeit' AS Kategorie,
                         1 AS KategorieSortierung,
                         MitarbeiterID,
                         NULL AS Subkategorie,
                         NULL AS SubkategorieSortierung,
                         SUM(ProjektabrechnungProjektzeit.StundenANW) AS StundenLeistungen,
                         ProjektabrechnungProjektzeit.Stundensatz AS StundensatzLeistungen,
                         SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Stundensatz) AS HonorarLeistungen,
                         NULL AS NebenkostenLeistungen,
                         SUM(ProjektabrechnungProjektzeit.StundenANW) AS StundenKosten,
                         ProjektabrechnungProjektzeit.Kostensatz AS StundensatzKosten,
                         SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Kostensatz) AS HonorarKosten,
                         NULL AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungProjektzeit ON
                             ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID,
                         ProjektabrechnungProjektzeit.Stundensatz,
                         ProjektabrechnungProjektzeit.Kostensatz
                 ),
                 reiseZeitenLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Reisezeiten' AS Subkategorie,
                         1 AS SubkategorieSortierung,
                         SUM(ProjektabrechnungReise.TatsaechlicheReisezeit) AS StundenLeistungen,
                         ProjektabrechnungReise.Stundensatz AS StundensatzLeistungen,
                         SUM(ProjektabrechnungReise.TatsaechlicheReisezeit * ProjektabrechnungReise.Stundensatz) AS HonorarLeistungen,
                         NULL AS NebenkostenLeistungen,
                         SUM(ProjektabrechnungReise.AngerechneteReisezeit) AS StundenKosten,
                         ProjektabrechnungReise.Kostensatz AS StundensatzKosten,
                         SUM(ProjektabrechnungReise.AngerechneteReisezeit * ProjektabrechnungReise.Kostensatz) AS HonorarKosten,
                         NULL AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID,
                         ProjektabrechnungReise.Stundensatz,
                         ProjektabrechnungReise.Kostensatz
                 ),
                 reiseBelegeAnwLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Belege lt. Arbeitsnachweis' AS Subkategorie,
                         2 AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         SUM(ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten) AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 reiseBelegeViadeeLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Belege (viadee)' AS Subkategorie,
                         3 AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungReise.BelegeViadeeLeistung) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         SUM(ProjektabrechnungReise.BelegeViadeeKosten) AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 reiseSpesenLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Spesen' AS Subkategorie,
                         4 AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungReise.SpesenLeistung) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         SUM(ProjektabrechnungReise.SpesenKosten) AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 reiseZuschlaegeLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Zuschläge' AS Subkategorie,
                         5 AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungReise.ZuschlaegeLeistung) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         SUM(ProjektabrechnungReise.ZuschlaegeKosten) AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 reisePauschaleLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Dienstreisen' AS Kategorie,
                         2 AS KategorieSortierung,
                         MitarbeiterID,
                         'Pauschale' AS Subkategorie,
                         6 AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungReise.PauschaleAnzahl * ProjektabrechnungReise.PauschaleProTag) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         NULL AS NebenkostenKosten
                     FROM
                         Projektabrechnung
                             INNER JOIN
                         ProjektabrechnungReise ON
                             ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 sonstigeLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Sonstiges' AS Kategorie,
                         5 AS KategorieSortierung,
                         MitarbeiterID,
                         NULL AS Subkategorie,
                         NULL AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungSonstige.PauschaleLeistungen) AS NebenkostenLeistungen,
                         NULL AS StundenKosten,
                         NULL AS StundensatzKosten,
                         NULL AS HonorarKosten,
                         SUM(COALESCE(ProjektabrechnungSonstige.AuslagenViadee + ProjektabrechnungSonstige.PauschaleKosten,
                             ProjektabrechnungSonstige.AuslagenViadee,
                             ProjektabrechnungSonstige.PauschaleKosten)) AS NebenkostenKosten
                     FROM
                         Projektabrechnung INNER JOIN
                         ProjektabrechnungSonstige ON
                             ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID
                 ),
                 rufbereitschaftenLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Rufbereitschaften' AS Kategorie,
                         3 AS KategorieSortierung,
                         MitarbeiterID,
                         NULL AS Subkategorie,
                         NULL AS SubkategorieSortierung,
                         SUM(ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden) AS StundenLeistungen,
                         ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz AS StundensatzLeistungen,
                         SUM(ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz) AS HonorarLeistungen,
                         SUM(ProjektabrechnungSonderarbeit.RufbereitschaftPauschale) AS NebenkostenLeistungen,
                         SUM(ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden) AS StundenKosten,
                         ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz AS StundensatzKosten,
                         SUM(ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz) AS HonorarKosten,
                         NULL AS NebenkostenKosten
                     FROM
                         Projektabrechnung INNER JOIN
                         ProjektabrechnungSonderarbeit ON
                             ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID,
                         ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz,
                         ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz
                 ),
                 sonderarbeitLK AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         'Sonderarbeitszeiten' AS Kategorie,
                         4 AS KategorieSortierung,
                         MitarbeiterID,
                         NULL AS Subkategorie,
                         NULL AS SubkategorieSortierung,
                         NULL AS StundenLeistungen,
                         NULL AS StundensatzLeistungen,
                         NULL AS HonorarLeistungen,
                         SUM(ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale) AS NebenkostenLeistungen,
                         NULL AS StundenKosten, -- Die Sonderarbeitszeit-Stunden werden nicht ausgegeben.
                         NULL AS StundensatzKosten, -- Der Sonderarbeitszeit-Kostensatz wird nicht ausgegeben.
                         SUM(
                             CASE WHEN ((SonderarbeitAnzahlStunden100 IS NULL AND SonderarbeitAnzahlStunden50 IS NULL) OR SonderarbeitKostensatz IS NULL)
                                 THEN NULL
                                 ELSE COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100 * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz, 0)
                                     + COALESCE(0.5 * ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50 * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz, 0)
                             END
                         ) AS HonorarKosten,
                         SUM(ProjektabrechnungSonderarbeit.SonderarbeitPauschale) AS NebenkostenKosten
                     FROM
                         Projektabrechnung INNER JOIN
                         ProjektabrechnungSonderarbeit ON
                             ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         MitarbeiterID,
                         ProjektabrechnungSonderarbeit.SonderarbeitKostensatz
                 ),
                 korrektur AS (
                     SELECT
                         ProjektID,
                         Jahr,
                         Monat,
                         CASE
                             WHEN C_Kostenart.Bezeichnung = 'Projektzeiten' THEN 'Geleistete Zeit'
                             WHEN C_Kostenart.Bezeichnung = 'Reisezeiten' THEN 'Dienstreisen'
                             WHEN C_Kostenart.Bezeichnung = 'Reisekosten' THEN 'Dienstreisen'
                             WHEN C_Kostenart.Bezeichnung = 'Rufbereitschaften' THEN 'Rufbereitschaften'
                             WHEN C_Kostenart.Bezeichnung = 'Sonderarbeitszeiten' THEN 'Sonderarbeitszeiten'
                             WHEN C_Kostenart.Bezeichnung = 'Sonstige' THEN 'Sonstiges'
                             WHEN C_Kostenart.Bezeichnung = 'Rabatte' THEN 'Rabatte'
                             WHEN C_Kostenart.Bezeichnung = 'Skonto' THEN 'Skonto'
                             ELSE 'Sonstiges'
                         END AS Kategorie,
                         CASE
                             WHEN C_Kostenart.Bezeichnung = 'Projektzeiten' THEN 1
                             WHEN C_Kostenart.Bezeichnung = 'Reisezeiten' THEN 2
                             WHEN C_Kostenart.Bezeichnung = 'Reisekosten' THEN 2
                             WHEN C_Kostenart.Bezeichnung = 'Rufbereitschaften' THEN 3
                             WHEN C_Kostenart.Bezeichnung = 'Sonderarbeitszeiten' THEN 4
                             WHEN C_Kostenart.Bezeichnung = 'Sonstige' THEN 5
                             WHEN C_Kostenart.Bezeichnung = 'Rabatte' THEN 6
                             WHEN C_Kostenart.Bezeichnung = 'Skonto' THEN 7
                             ELSE 5
                         END AS KategorieSortierung,
                         ProjektabrechnungKorrekturbuchung.MitarbeiterID,
                         CASE
                             WHEN C_Kostenart.Bezeichnung = 'Reisezeiten' THEN 'Reisezeiten'
                             WHEN C_Kostenart.Bezeichnung = 'Reisekosten' THEN 'Pauschale'
                         END AS Subkategorie,
                         CASE
                             WHEN C_Kostenart.Bezeichnung = 'Reisezeiten' THEN 1
                             WHEN C_Kostenart.Bezeichnung = 'Reisekosten' THEN 6
                         END AS SubkategorieSortierung,
                         SUM(AnzahlStundenLeistung) AS StundenLeistungen,
                         CASE WHEN (AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0)
                             THEN NULL
                             ELSE BetragStundensatz
                         END AS StundensatzLeistungen,
                         SUM(CASE WHEN (AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0)
                             THEN NULL
                             ELSE BetragStundensatz * AnzahlStundenLeistung
                         END) AS HonorarLeistungen,
                         SUM(CASE WHEN (AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0)
                             THEN BetragStundensatz
                             ELSE NULL
                         END) AS NebenkostenLeistungen,
                         SUM(AnzahlStundenKosten) AS StundenKosten,
                         CASE WHEN (AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0)
                             THEN NULL
                             ELSE BetragKostensatz
                         END AS StundensatzKosten,
                         SUM(CASE WHEN (AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0)
                             THEN NULL
                             ELSE BetragKostensatz * AnzahlStundenKosten
                         END) AS HonorarKosten,
                         SUM(CASE WHEN (AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0)
                             THEN BetragKostensatz
                             ELSE NULL
                         END) AS NebenkostenKosten
                     FROM
                         ProjektabrechnungKorrekturbuchung
                             LEFT OUTER JOIN
                         C_Kostenart ON KostenartID = C_Kostenart.ID
                     GROUP BY
                         ProjektID,
                         Jahr,
                         Monat,
                         C_Kostenart.Bezeichnung,
                         MitarbeiterID,
                         AnzahlStundenLeistung,
                         BetragStundensatz,
                         AnzahlStundenKosten,
                         BetragKostensatz
                 ),
                 leistungenKostenVereinigt AS (
                     SELECT * FROM projektzeitenLK
                         UNION ALL
                     SELECT * FROM reiseZeitenLK
                         UNION ALL
                     SELECT * FROM reiseBelegeAnwLK
                         UNION ALL
                     SELECT * FROM reiseBelegeViadeeLK
                         UNION ALL
                     SELECT * FROM reiseSpesenLK
                         UNION ALL
                     SELECT * FROM reiseZuschlaegeLK
                         UNION ALL
                     SELECT * FROM reisePauschaleLK
                         UNION ALL
                     SELECT * FROM rufbereitschaftenLK
                         UNION ALL
                     SELECT * FROM sonderarbeitLK
                         UNION ALL
                     SELECT * FROM sonstigeLK
                         UNION ALL
                     SELECT * FROM korrektur
                 ),
                 leistungenKostenAggregiert AS (
                     SELECT
                         relevanteProjektMonate.ProjektID,
                         Projekt.Projektnummer,
                         Projekt.Bezeichnung AS Projektbezeichnung,
                         relevanteProjektMonate.Jahr,
                         relevanteProjektMonate.Monat,
                         Kategorie,
                         KategorieSortierung,
                         MitarbeiterID,
                         Mitarbeiter.Anrede,
                         Mitarbeiter.Nachname,
                         Subkategorie,
                         SubkategorieSortierung,
                         SUM(StundenLeistungen) AS StundenLeistungen,
                         StundensatzLeistungen,
                         SUM(HonorarLeistungen) AS HonorarLeistungen,
                         SUM(NebenkostenLeistungen) AS NebenkostenLeistungen,
                         SUM(StundenKosten) AS StundenKosten,
                         StundensatzKosten,
                         SUM(HonorarKosten) AS HonorarKosten,
                         SUM(NebenkostenKosten) AS NebenkostenKosten
                     FROM
                         relevanteProjektMonate
                             INNER JOIN
                         leistungenKostenVereinigt ON
                                 leistungenKostenVereinigt.Jahr = relevanteProjektMonate.Jahr
                                 AND leistungenKostenVereinigt.Monat = relevanteProjektMonate.Monat
                                 AND leistungenKostenVereinigt.ProjektID = relevanteProjektMonate.ProjektID
                             INNER JOIN
                         Projekt ON Projekt.ID = relevanteProjektMonate.ProjektID
                             LEFT OUTER JOIN
                         Mitarbeiter ON Mitarbeiter.ID = MitarbeiterID
                     WHERE
                         StundenLeistungen <> 0 OR NebenkostenLeistungen <> 0
                         OR StundenKosten <> 0 OR NebenkostenKosten <> 0
                     GROUP BY
                         relevanteProjektMonate.ProjektID,
                         Projektnummer,
                         Projekt.Bezeichnung,
                         relevanteProjektMonate.Jahr,
                         relevanteProjektMonate.Monat,
                         Kategorie,
                         KategorieSortierung,
                         MitarbeiterID,
                         Mitarbeiter.Anrede,
                         Mitarbeiter.Nachname,
                         Subkategorie,
                         SubkategorieSortierung,
                         StundensatzLeistungen,
                         StundensatzKosten
                 )
                 
                 SELECT * FROM leistungenKostenAggregiert
                 ORDER BY
                     Projektnummer ASC,
                     ProjektID ASC,
                     Jahr DESC,
                     Monat DESC,
                     KategorieSortierung ASC,
                     Nachname ASC,
                     Anrede ASC,
                     MitarbeiterID ASC,
                     SubkategorieSortierung ASC
           """)
  List<ErgebnisB008Excel> ladeB008Excel(final int abJahr, final int abMonat,
      final int bisJahr, final int bisMonat,
      Long projektId, String kundeScribeId, Long sachbearbeiterId, String oeScribeId);
}
