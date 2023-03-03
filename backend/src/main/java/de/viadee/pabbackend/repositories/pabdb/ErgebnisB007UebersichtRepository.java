package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB007Uebersicht;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB007UebersichtRepository extends
    CrudRepository<ErgebnisB007Uebersicht, Long> {

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
              DATEFROMPARTS(Jahr, Monat, 1) BETWEEN
                  DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
              AND (:sachbearbeiterId IS NULL OR Projekt.SachbearbeiterID = :sachbearbeiterId)
              AND (:statusId IS NULL OR Projektabrechnung.StatusID = :statusId)
              AND (:istAktiv IS NULL OR Projekt.IstAktiv = :istAktiv)
              AND (:projektId IS NULL OR Projektabrechnung.ProjektID = :projektId)
              AND (:projekttyp IS NULL OR Projekt.Projekttyp = :projekttyp)
              AND (:kundeScribeId IS NULL OR Projekt.KundeID = :kundeScribeId)
              AND (:oeScribeId IS NULL OR Projekt.OrganisationseinheitID = :oeScribeId)
              AND (NOT Projekt.Projektnummer = '99999' AND NOT Projekt.Projektnummer = '99998')
      ),
      projektzeitKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungProjektzeit.MitarbeiterID,
              'Projektzeiten' AS Kostenart,
              ProjektabrechnungProjektzeit.StundenANW AS KostenStunden,
              ProjektabrechnungProjektzeit.Kostensatz,
              COALESCE(
                  SUM(
                      ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Kostensatz
                  ),
                  0
              ) AS Kosten,
              ProjektabrechnungProjektzeit.StundenANW AS LeistungStunden,
              ProjektabrechnungProjektzeit.Stundensatz,
              COALESCE(
                  SUM(
                      ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Stundensatz
                  ),
                  0
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungProjektzeit ON
                  Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungProjektzeit.MitarbeiterID,
              ProjektabrechnungProjektzeit.StundenANW,
              ProjektabrechnungProjektzeit.Stundensatz,
              ProjektabrechnungProjektzeit.Kostensatz
      ),
      reisezeitKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungReise.MitarbeiterID,
              'Reisezeiten' AS Kostenart,
              ProjektabrechnungReise.AngerechneteReisezeit AS KostenStunden,
              ProjektabrechnungReise.Kostensatz,
              --Kosten
              COALESCE(
                  SUM(
                      ProjektabrechnungReise.AngerechneteReisezeit * ProjektabrechnungReise.Kostensatz
                  ),
                  0
              ) AS Kosten,
              ProjektabrechnungReise.TatsaechlicheReisezeit AS LeistungStunden,
              ProjektabrechnungReise.Stundensatz,
              --Leistung
              COALESCE(
                  SUM(
                      ProjektabrechnungReise.TatsaechlicheReisezeit * ProjektabrechnungReise.Stundensatz
                  ),
                  0
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungReise ON
                  Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungReise.MitarbeiterID,
              ProjektabrechnungReise.AngerechneteReisezeit,
              ProjektabrechnungReise.Kostensatz,
              ProjektabrechnungReise.TatsaechlicheReisezeit,
              ProjektabrechnungReise.Stundensatz
      ),
      reisekostenKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungReise.MitarbeiterID,
              'Reisekosten' AS Kostenart,
              NULL AS KostenStunden,
              NULL AS Kostensatz,
              --Kosten
              SUM(
                  COALESCE(ProjektabrechnungReise.BelegeViadeeKosten, 0) + COALESCE(
                      ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten,
                      0
                  ) + COALESCE(ProjektabrechnungReise.SpesenKosten, 0) + COALESCE(ProjektabrechnungReise.ZuschlaegeKosten, 0)
              ) AS Kosten,
              NULL AS LeistungStunden,
              NULL AS Stundensatz,
              --Leistung
              SUM(
                  COALESCE(ProjektabrechnungReise.BelegeViadeeLeistung, 0) + COALESCE(
                      ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung,
                      0
                  ) + COALESCE(ProjektabrechnungReise.SpesenLeistung, 0) + COALESCE(ProjektabrechnungReise.ZuschlaegeLeistung, 0) + COALESCE(
                      ProjektabrechnungReise.PauschaleAnzahl * ProjektabrechnungReise.PauschaleProTag,
                      0
                  )
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungReise ON
                  Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungReise.MitarbeiterID
      ),
      rufbereitschaftKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonderarbeit.MitarbeiterID,
              'Rufbereitschaften' AS Kostenart,
              NULL AS KostenStunden,
              NULL AS Kostensatz,
              --Kosten
              COALESCE(
                  SUM(
                      -- Rufbereitschaft
                      (
                          ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz
                      )
                  ),
                  0
              ) AS Kosten,
              --Leistung
              NULL AS LeistungStunden,
              NULL AS Stundensatz,
              COALESCE(
                  SUM(
                      -- Rufbereitschaft
                      (
                          ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz + COALESCE(
                              ProjektabrechnungSonderarbeit.RufbereitschaftPauschale,
                              0
                          )
                      )
                  ),
                  0
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungSonderarbeit ON
                  Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonderarbeit.MitarbeiterID
      ),
      sonderarbeitKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonderarbeit.MitarbeiterID,
              'Sonderarbeitszeiten' AS Kostenart,
              NULL AS KostenStunden,
              NULL AS Kostensatz,
              --Kosten
              COALESCE(
                  SUM(
                      -- Sonderarbeit
                      (
                          COALESCE(
                              ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100,
                              0
                          ) * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz + COALESCE(
                              ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50,
                              0
                          ) * 0.5 * ProjektabrechnungSonderarbeit.SonderarbeitKostensatz + COALESCE(
                              ProjektabrechnungSonderarbeit.SonderarbeitPauschale,
                              0
                          )
                      )
                  ),
                  0
              ) AS Kosten,
              --Leistung
              NULL AS LeistungStunden,
              NULL AS Stundensatz,
              COALESCE(
                  SUM(
                      -- Sonderarbeit Leistung Pauschale
                      COALESCE(
                          ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale,
                          0
                      )
                  ),
                  0
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungSonderarbeit ON
                  Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonderarbeit.MitarbeiterID
      ),
      sonstigeKostenLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonstige.MitarbeiterID,
              'Sonstige' AS Kostenart,
              NULL AS KostenStunden,
              NULL AS Kostensatz,
              --Kosten
              COALESCE(
                  SUM(
                      COALESCE(ProjektabrechnungSonstige.AuslagenViadee, 0) + COALESCE(ProjektabrechnungSonstige.PauschaleKosten, 0)
                  ),
                  0
              ) AS Kosten,
              NULL AS LeistungStunden,
              NULL AS Stundensatz,
              --Leistung
              COALESCE(
                  SUM(
                      COALESCE(ProjektabrechnungSonstige.PauschaleLeistungen, 0)
                  ),
                  0
              ) AS BerechneteLeistung,
              NULL AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungSonstige ON
                  Projektabrechnung.ID = ProjektabrechnungSonstige.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungSonstige.MitarbeiterID
      ),
      faktFaehigeLeistung AS (
          SELECT
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungBerechneteLeistung.MitarbeiterID,
              'FaktfaehigeLeistung' AS Kostenart,
              NULL AS KostenStunden,
              NULL AS Kostensatz,
              NULL AS Kosten,
              NULL AS LeistungStunden,
              NULL AS Stundensatz,
              NULL AS BerechneteLeistung,
              COALESCE(
                  SUM(ProjektabrechnungBerechneteLeistung.Leistung),
                  0
              ) AS FaktLeistung,
              'Buchung' AS Buchungstyp
          FROM
              Projektabrechnung
                  INNER JOIN
              ProjektabrechnungBerechneteLeistung ON
                  Projektabrechnung.ID = ProjektabrechnungBerechneteLeistung.ProjektabrechnungID
          GROUP BY
              Projektabrechnung.ProjektID,
              Projektabrechnung.Jahr,
              Projektabrechnung.Monat,
              ProjektabrechnungBerechneteLeistung.MitarbeiterID
      ),
      korrekturen AS (
          SELECT
              ProjektabrechnungKorrekturbuchung.ProjektID,
              ProjektabrechnungKorrekturbuchung.Jahr,
              ProjektabrechnungKorrekturbuchung.Monat,
              ProjektabrechnungKorrekturbuchung.MitarbeiterID,
              C_Kostenart.Bezeichnung AS Kostenart,
              AnzahlStundenKosten AS KostenStunden,
              CASE WHEN AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0
                  THEN NULL
                  ELSE BetragKostensatz
              END AS Kostensatz,
              CASE WHEN AnzahlStundenKosten IS NULL OR AnzahlStundenKosten = 0
                  THEN BetragKostensatz
                  ELSE BetragKostensatz * AnzahlStundenKosten
              END AS Kosten,
              AnzahlStundenLeistung AS LeistungStunden,
              CASE WHEN AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0
                  THEN NULL
                  ELSE BetragStundensatz
              END AS Stundensatz,
              CASE WHEN AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0
                  THEN BetragStundensatz
                  ELSE BetragStundensatz * AnzahlStundenLeistung
              END AS BerechneteLeistung,
              CASE
                  WHEN Projekt.Projekttyp = 'Dienstleistung'
                  THEN CASE WHEN AnzahlStundenLeistung IS NULL OR AnzahlStundenLeistung = 0
                      THEN BetragStundensatz
                      ELSE BetragStundensatz * AnzahlStundenLeistung
                  END
              END AS FaktLeistung,
              'Korrektur' AS Buchungstyp
          FROM
              ProjektabrechnungKorrekturbuchung
                  LEFT OUTER JOIN
              C_Kostenart ON
                  KostenartID = C_Kostenart.ID
                  LEFT OUTER JOIN
              Projekt ON
                  ProjektID = Projekt.ID
      ),
      vereinigt AS (
          SELECT * FROM projektzeitKostenLeistung
          UNION ALL
          SELECT * FROM reisezeitKostenLeistung
          UNION ALL
          SELECT * FROM reisekostenKostenLeistung
          UNION ALL
          SELECT * FROM rufbereitschaftKostenLeistung
          UNION ALL
          SELECT * FROM sonderarbeitKostenLeistung
          UNION ALL
          SELECT * FROM sonstigeKostenLeistung
          UNION ALL
          SELECT * FROM faktFaehigeLeistung
          UNION ALL
          SELECT * FROM korrekturen
      ),
      vereinigtGefiltert AS (
          SELECT vereinigt.*
          FROM
              relevanteProjektMonate
                  INNER JOIN
              vereinigt ON
                  relevanteProjektMonate.ProjektID = vereinigt.ProjektID
                  AND relevanteProjektMonate.Jahr = vereinigt.Jahr
                  AND relevanteProjektMonate.Monat = vereinigt.Monat
                  LEFT OUTER JOIN
              Mitarbeiter ON
                  MitarbeiterID = Mitarbeiter.ID LEFT JOIN
              C_MitarbeiterTyp  on Mitarbeiter.MitarbeiterTypID = C_MitarbeiterTyp.ID

          WHERE
              (vereinigt.Kosten <> 0 OR vereinigt.BerechneteLeistung <> 0 OR vereinigt.FaktLeistung <> 0)
              AND (:mitarbeiterId IS NULL OR vereinigt.MitarbeiterID = :mitarbeiterId)
              AND (:mitarbeiterTyp IS NULL OR C_MitarbeiterTyp.TextLang = :mitarbeiterTyp)
              AND (:buchungstyp IS NULL OR Buchungstyp = :buchungstyp)
              AND vereinigt.Kostenart IN (:kostenart)
      ),
      resultat AS (
          SELECT
              Projekt.Projektnummer,
              Projekt.Bezeichnung,
              Projekt.IstAktiv,
              vereinigtGefiltert.Jahr,
              vereinigtGefiltert.Monat,
              Sum(vereinigtGefiltert.Kosten) AS Kosten,
              Sum(vereinigtGefiltert.BerechneteLeistung) AS Leistung
          FROM
              vereinigtGefiltert
                  LEFT OUTER JOIN
              Projekt ON
                  vereinigtGefiltert.ProjektID = Projekt.ID
          WHERE
              (:abfrageDurchOELeiter = 1 AND
               NOT Projekt.Projektnummer BETWEEN 9000 AND 9999 AND
               NOT Projekt.Projektnummer = 99999 AND
               NOT Projekt.Projektnummer = 99998)
             OR
              (:abfrageDurchOELeiter = 0)
          GROUP BY
              Projekt.ID,
              Projekt.Projektnummer,
              Projekt.IstAktiv,
              Projekt.Bezeichnung,
              vereinigtGefiltert.Jahr,
              vereinigtGefiltert.Monat
      )
      SELECT
          *
      FROM
          resultat
      ORDER BY
          Projektnummer ASC,
          Jahr DESC,
          Monat DESC
                 """)
  List<ErgebnisB007Uebersicht> ladeB007Uebersicht(final int abJahr, final int abMonat,
      final int bisJahr, final int bisMonat, Long sachbearbeiterId, Boolean istAktiv,
      Long statusId,
      String buchungstyp, Long projektId, String kundeScribeId, String oeScribeId,
      Long mitarbeiterId, String projekttyp, String mitarbeiterTyp,
      List<String> kostenart, final int abfrageDurchOELeiter);
}
