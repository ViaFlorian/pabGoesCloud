package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.LeistungKumuliert;
import de.viadee.pabbackend.entities.ProjektAbrechnungsmonat;
import de.viadee.pabbackend.entities.Projektabrechnung;
import de.viadee.pabbackend.entities.ProjektabrechnungMitarbeiterPair;
import de.viadee.pabbackend.entities.ProjektabrechnungUebersicht;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
public interface ProjektabrechnungRepository extends CrudRepository<Projektabrechnung, Long> {

  @Query("""
      WITH projektzeitKostenLeistung as
               (
                   SELECT Projektabrechnung.ID as ProjektabrechnungID,
                          COALESCE(SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Kostensatz),
                                   0)          as Kosten,
                          COALESCE(SUM(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Stundensatz),
                                   0)          as Leistung
                   FROM Projektabrechnung
                            INNER JOIN
                        ProjektabrechnungProjektzeit ON
                                    Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID AND
                                    DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                   Group by Projektabrechnung.ID
               ),
           reiseKostenLeistung as
               (
                   SELECT Projektabrechnung.ID as ProjektabrechnungID,
                          --Kosten
                          COALESCE(SUM(
                                               (ProjektabrechnungReise.AngerechneteReisezeit *
                                                ProjektabrechnungReise.Kostensatz) +
                                               COALESCE(ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten, 0) +
                                               COALESCE(ProjektabrechnungReise.BelegeViadeeKosten, 0) +
                                               COALESCE(ProjektabrechnungReise.SpesenKosten, 0) +
                                               COALESCE(ProjektabrechnungReise.ZuschlaegeKosten, 0)
                                       )
                              , 0)             AS Kosten,
                          --Leistung
                          COALESCE(SUM(
                                               (ProjektabrechnungReise.TatsaechlicheReisezeit *
                                                ProjektabrechnungReise.Stundensatz) +
                                               COALESCE(ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung, 0) +
                                               COALESCE(ProjektabrechnungReise.BelegeViadeeLeistung, 0) +
                                               COALESCE(ProjektabrechnungReise.SpesenLeistung, 0) +
                                               COALESCE(ProjektabrechnungReise.ZuschlaegeLeistung, 0) +
                                               (ProjektabrechnungReise.PauschaleAnzahl *
                                                ProjektabrechnungReise.PauschaleProTag)
                                       )
                              , 0)             AS Leistung
                   FROM Projektabrechnung
                            INNER JOIN
                        ProjektabrechnungReise ON
                                    Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID AND
                                    DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                   Group by Projektabrechnung.ID
               ),
           sonderarbeitKostenLeistung as
               (
                   SELECT Projektabrechnung.ID as ProjektabrechnungID,
                          --Kosten
                          COALESCE(SUM(
                                       -- Rufbereitschaft
                                               (ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden *
                                                ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz)
                                               +
                                               -- Sonderarbeit
                                               ((ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50 * 0.5 +
                                                 COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100, 0)) *
                                                ProjektabrechnungSonderarbeit.SonderarbeitKostensatz +
                                                COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitPauschale, 0))
                                       )
                              , 0)             AS Kosten,
                          --Leistung
                          COALESCE(SUM(

                                       -- Rufbereitschaft
                                               (ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden *
                                                ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz +
                                                COALESCE(ProjektabrechnungSonderarbeit.RufbereitschaftPauschale, 0))
                                               +
                                               -- Sonderarbeit Leistung Pauschale
                                               COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale, 0)
                                       )
                              , 0)             AS Leistung
                   FROM Projektabrechnung
                            INNER JOIN
                        ProjektabrechnungSonderarbeit ON
                                    Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID AND
                                    DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                   Group by Projektabrechnung.ID
               ),
           sonstigeKostenLeistung as
               (
                   SELECT Projektabrechnung.ID as ProjektabrechnungID,
                          --Kosten
                          COALESCE(SUM(
                                               COALESCE(ProjektabrechnungSonstige.AuslagenViadee, 0) +
                                               COALESCE(ProjektabrechnungSonstige.PauschaleKosten, 0)
                                       )
                              , 0)             AS Kosten,
                          --Leistung
                          COALESCE(SUM(
                                           COALESCE(ProjektabrechnungSonstige.PauschaleLeistungen, 0)
                                       )
                              , 0)             AS Leistung
                   FROM Projektabrechnung
                            INNER JOIN
                        ProjektabrechnungSonstige ON
                                    Projektabrechnung.ID = ProjektabrechnungSonstige.ProjektabrechnungID
                                AND
                                    DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                   Group by Projektabrechnung.ID, Projektabrechnung.Jahr, Projektabrechnung.Monat
               ),
           korrekturbuchung as
               (
                   SELECT DISTINCT Projektabrechnung.ProjektID as ProjektID,  COUNT(*) as anzahl, Projektabrechnung.Jahr as Jahr, Projektabrechnung.Monat as Monat
                   FROM Projektabrechnung
                            INNER JOIN
                        ProjektabrechnungKorrekturbuchung ON
                                    Projektabrechnung.ProjektID = ProjektabrechnungKorrekturbuchung.ProjektID
                                AND
                                    Projektabrechnung.Jahr = ProjektabrechnungKorrekturbuchung.Jahr
                                AND
                                    Projektabrechnung.Monat = ProjektabrechnungKorrekturbuchung.Monat
                                AND
                                    DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)

                   WHERE ProjektabrechnungKorrekturbuchung.Jahr = Projektabrechnung.Jahr and ProjektabrechnungKorrekturbuchung.Monat = Projektabrechnung.Monat
                   Group by Projektabrechnung.ProjektID, Projektabrechnung.Jahr, Projektabrechnung.Monat
               ),
           mitarbeiterZahl as
               (
                   SELECT ID as ProjektabrechnungID, COUNT(*) as anzahl FROM
                       (
                           SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungProjektzeit.MitarbeiterID
                           FROM Projektabrechnung
                                    INNER JOIN
                                ProjektabrechnungProjektzeit ON
                                            Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID AND
                                            DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                           UNION
                           SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungReise.MitarbeiterID
                           FROM Projektabrechnung
                                    INNER JOIN
                                ProjektabrechnungReise ON
                                            Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID AND
                                            DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                           UNION
                           SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungSonderarbeit.MitarbeiterID
                           FROM Projektabrechnung
                                    INNER JOIN
                                ProjektabrechnungSonderarbeit ON
                                            Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID AND
                                            DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                           UNION
                           SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungSonderarbeit.MitarbeiterID
                           FROM Projektabrechnung
                                    INNER JOIN
                                ProjektabrechnungSonderarbeit ON
                                            Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID AND
                                            DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
                       ) mitarbeiterBuchungen
                   GROUP BY ID
               )

      SELECT DISTINCT Projektabrechnung.ID       as ProjektabrechnungID,
                      mitarbeiterZahl.anzahl     as AnzahlMitarbeiter,
                      korrekturbuchung.anzahl    as AnzahlKorrekturbuchungen,
                      Projektabrechnung.Jahr     as Jahr,
                      Projektabrechnung.Monat    as Monat,
                      Projekt.ID                 as ProjektID,
                      Projekt.KundeID            as KundeID,
                      Projekt.SachbearbeiterID   as SachbearbeiterID,
                      (
                              COALESCE(projektzeitKostenLeistung.Leistung, 0) +
                              COALESCE(reiseKostenLeistung.Leistung, 0) +
                              COALESCE(sonderarbeitKostenLeistung.Leistung, 0) +
                              COALESCE(sonstigeKostenLeistung.Leistung, 0)
                          )                      as Leistung,
                      (
                              COALESCE(projektzeitKostenLeistung.Kosten, 0) +
                              COALESCE(reiseKostenLeistung.Kosten, 0) +
                              COALESCE(sonderarbeitKostenLeistung.Kosten, 0) +
                              COALESCE(sonstigeKostenLeistung.Kosten, 0)
                          )                      as Kosten,
                      Projektabrechnung.StatusID as StatusID,
                      Projekt.OrganisationseinheitID as OrganisationseinheitID
      FROM Projektabrechnung
               INNER JOIN
           Projekt ON
                   Projektabrechnung.ProjektID = Projekt.ID
               LEFT JOIN
           projektzeitKostenLeistung ON
                   projektzeitKostenLeistung.ProjektabrechnungID = Projektabrechnung.ID
               LEFT JOIN
           reiseKostenLeistung ON
                   reiseKostenLeistung.ProjektabrechnungID = Projektabrechnung.ID
               LEFT JOIN
           sonderarbeitKostenLeistung ON
                   sonderarbeitKostenLeistung.ProjektabrechnungID = Projektabrechnung.ID
               LEFT JOIN
           sonstigeKostenLeistung ON
                   sonstigeKostenLeistung.ProjektabrechnungID = Projektabrechnung.ID
               LEFT JOIN
           mitarbeiterZahl ON
                   mitarbeiterZahl.ProjektabrechnungID = Projektabrechnung.ID
               LEFT JOIN
          korrekturbuchung ON
                  korrekturbuchung.ProjektID = Projektabrechnung.ProjektID
                  AND
                  korrekturbuchung.Jahr = Projektabrechnung.Jahr
                  AND
                  korrekturbuchung.Monat = Projektabrechnung.Monat
      WHERE DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
        AND (NOT Projekt.Projektnummer = '99999' AND NOT Projekt.Projektnummer = '99998')
                        """)
  Iterable<ProjektabrechnungUebersicht> findAlleProjektabrechnungenFuerUebersichtGefiltert(
      final int abJahr, final int abMonat, final int bisJahr, final int bisMonat);

  @Query("""
      SELECT DISTINCT Projektabrechnung.ID as ProjektabrechnungID, ProjektabrechnungProjektzeit.MitarbeiterID
      FROM Projektabrechnung
               INNER JOIN
           ProjektabrechnungProjektzeit ON
                       Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID AND
                       DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      UNION
      SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungReise.MitarbeiterID
      FROM Projektabrechnung
               INNER JOIN
           ProjektabrechnungReise ON
                       Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID AND
                       DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      UNION
      SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungSonderarbeit.MitarbeiterID
      FROM Projektabrechnung
               INNER JOIN
           ProjektabrechnungSonderarbeit ON
                       Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID AND
                       DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      UNION
      SELECT DISTINCT Projektabrechnung.ID, ProjektabrechnungSonderarbeit.MitarbeiterID
      FROM Projektabrechnung
               INNER JOIN
           ProjektabrechnungSonderarbeit ON
                       Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID AND
                       DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) BETWEEN DATEFROMPARTS(:abJahr, :abMonat, 1) AND DATEFROMPARTS(:bisJahr, :bisMonat, 1)
      """)
  Iterable<ProjektabrechnungMitarbeiterPair> findALleProjektabrechnungMitarbeiterPairs(
      final int abJahr, final int abMonat, final int bisJahr, final int bisMonat);

  @Query("""
        WITH korrektur as
                 (
                     SELECT
                         CASE
                             WHEN COUNT(*) > 0 THEN 1
                             ELSE 0
                             END AS KorrekturVorhanden,
                         ProjektabrechnungKorrekturbuchung.ProjektID,
                         ProjektabrechnungKorrekturbuchung.Monat,
                         ProjektabrechnungKorrekturbuchung.Jahr
        
                     FROM ProjektabrechnungKorrekturbuchung
        
                     GROUP BY ProjektabrechnungKorrekturbuchung.ProjektID, ProjektabrechnungKorrekturbuchung.Monat, ProjektabrechnungKorrekturbuchung.Jahr
                 )
        
        SELECT
            Projektabrechnung.*,
            CASE WHEN korrektur.KorrekturVorhanden IS NULL THEN 0
                 ELSE korrektur.KorrekturVorhanden
                END as KorrekturVorhanden,
            CASE WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL THEN 0
                 ELSE 1
                END as istAbgeschlossen
        FROM
            Projektabrechnung LEFT OUTER JOIN
            AbgeschlosseneMonate ON
                        Projektabrechnung.Monat = AbgeschlosseneMonate.Monat AND
                        Projektabrechnung.Jahr = AbgeschlosseneMonate.Jahr
                              LEFT JOIN
            korrektur ON
                        korrektur.ProjektID = Projektabrechnung.ProjektID AND
                        korrektur.Monat = Projektabrechnung.Monat AND
                        korrektur.Jahr = Projektabrechnung.Jahr
        WHERE
               Projektabrechnung.ProjektID = :projektId AND
               Projektabrechnung.Jahr = :jahr AND
               Projektabrechnung.Monat = :monat
      """)
  Projektabrechnung projektabrechungByProjektIdMonatJahr(Long projektId, Integer monat,
      Integer jahr);

  @Query("""
      SELECT TOP 1
      	*
      FROM
      	Projektabrechnung
      WHERE
      	ProjektID = :projektId AND
      	DATEFROMPARTS(Jahr, Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
      ORDER BY Projektabrechnung.Jahr DESC, Projektabrechnung.Monat DESC
      """)
  Optional<Projektabrechnung> vorhergehendeProjektabrechungenByProjektIdMonatJahr(Long projektId,
      Integer monat, Integer jahr);

  @Query("""
      UPDATE Projektabrechnung
         SET StatusID = :statusId
      WHERE
          ID = :projektabrechnungId
       """)
  @Modifying
  void updateProjektabrechnungStatus(Long projektabrechnungId, Integer statusId);

  @Query("""
      DELETE FROM
      Projektabrechnung
      WHERE ID IN (
      SELECT ID FROM
      Projektabrechnung
      WHERE
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungProjektzeit WHERE ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID) AND
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungReise WHERE ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID) AND
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungSonderarbeit WHERE ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID) AND
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungSonstige WHERE ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID) AND
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungBerechneteLeistung WHERE ProjektabrechnungBerechneteLeistung.ProjektabrechnungID = Projektabrechnung.ID) AND
         NOT EXISTS (SELECT 'X' FROM ProjektabrechnungKorrekturbuchung WHERE ProjektabrechnungKorrekturbuchung.ProjektabrechnungID = Projektabrechnung.ID)
      ) AND
      Projektabrechnung.ProjektID IN (:idsVonProjektenMitGeloeschtenProjektstunden) AND
      Projektabrechnung.Monat = :monat AND
      Projektabrechnung.Jahr = :jahr AND
      Projektabrechnung.StatusID < 40
      """)
  @Modifying
  void deleteObsoleteProjektabrechnungen(
      Collection<Long> idsVonProjektenMitGeloeschtenProjektstunden, Integer monat, Integer jahr);

  Projektabrechnung findAllByProjektIdAndMonatAndJahr(final Long projektId, final Integer monat,
      final Integer jahr);

  Iterable<Projektabrechnung> findProjektabrechnungByProjektId(Long projektId);

  @Query("""
        SELECT P.ID AS ProjektabrechnungID , P.Jahr , P.Monat,
               CASE WHEN AM.Jahr IS NULL THEN 'false' ELSE 'true' END AS Abgeschlossen
        from Projektabrechnung P LEFT JOIN AbgeschlosseneMonate AM on P.Jahr = AM.Jahr AND P.Monat = AM.Monat
        WHERE P.ProjektID = :projektId  
      """)
  Iterable<ProjektAbrechnungsmonat> findProjektAbrechnungsmonateByProjektId(Long projektId);

  @Query("""
      WITH
      	zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte AS
      (
      SELECT
      	DISTINCT ProjektID, Arbeitsnachweis.MitarbeiterID, Arbeitsnachweis.Monat, Arbeitsnachweis.Jahr
      FROM
      	Arbeitsnachweis INNER JOIN
      	Projektstunde ON
      		Projektstunde.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Projektstunde.ProjektID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId
      UNION
      SELECT
      	DISTINCT ProjektID, Arbeitsnachweis.MitarbeiterID, Arbeitsnachweis.Monat, Arbeitsnachweis.Jahr
      FROM
      	Arbeitsnachweis INNER JOIN
      	Beleg ON
      		Beleg.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Beleg.ProjektID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId
      UNION
      SELECT
      	DISTINCT ProjektID, Arbeitsnachweis.MitarbeiterID, Arbeitsnachweis.Monat, Arbeitsnachweis.Jahr
      FROM
      	Arbeitsnachweis INNER JOIN
      	Abwesenheit ON
      		Abwesenheit.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Abwesenheit.ProjektID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId

      )

      UPDATE
          Projektabrechnung
      SET
      	StatusID = 10
      WHERE
          ID IN (
                  SELECT
                      Projektabrechnung.ID
                  FROM
                      Projektabrechnung INNER JOIN
                      zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte ON
                          zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.ProjektID = Projektabrechnung.ProjektID AND
                          Projektabrechnung.Monat = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Monat AND
                          Projektabrechnung.Jahr = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Jahr
      		)
      """)
  @Modifying
  void setzeProjektabrechnungenStatusFuerArbeitsnachweisLoeschung(Long arbeitsnachweisId);

  @Query("""
      WITH AnzahlElementeFuerMitarbeiter AS (
          SELECT
              COUNT(ProjektabrechnungID) as AnzahlElemente
          FROM
              ProjektabrechnungProjektzeit
          WHERE
                  ProjektabrechnungID = :projektabrechnungId AND MitarbeiterID = :mitarbeiterId
          UNION ALL
          SELECT
              COUNT(ProjektabrechnungID) as AnzahlElemente
          FROM
              ProjektabrechnungReise
          WHERE
                  ProjektabrechnungID = :projektabrechnungId AND MitarbeiterID = :mitarbeiterId
          UNION ALL
          SELECT
              COUNT(ProjektabrechnungID) as AnzahlElemente
          FROM
              ProjektabrechnungSonderarbeit
          WHERE
                  ProjektabrechnungID = :projektabrechnungId AND MitarbeiterID = :mitarbeiterId
          UNION ALL
          SELECT
              COUNT(ProjektabrechnungID) as AnzahlElemente
          FROM
              ProjektabrechnungSonstige
          WHERE
                  ProjektabrechnungID = :projektabrechnungId AND MitarbeiterID = :mitarbeiterId
      )
          
      SELECT
          SUM(AnzahlElemente)
      FROM
          AnzahlElementeFuerMitarbeiter
        """)
  Integer anzahlAnElementenFuerMitarbeiter(Long projektabrechnungId, Long mitarbeiterId);

  @Query("""
        WITH projektzeitLeistung as
            (
                SELECT
                      Projektabrechnung.ID  as ProjektabrechnungID
        			, ProjektabrechnungProjektzeit.MitarbeiterID as MitarbeiterID
                    , COALESCE(ProjektabrechnungProjektzeit.StundenANW * ProjektabrechnungProjektzeit.Stundensatz, 0) as Leistung
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungProjektzeit
                    ON
                                Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , reisezeitLeistung as
            (
                SELECT
                     Projektabrechnung.ID as ProjektabrechnungID
        			, ProjektabrechnungReise.MitarbeiterID as MitarbeiterID
                    , COALESCE(ProjektabrechnungReise.TatsaechlicheReisezeit * ProjektabrechnungReise.Stundensatz, 0) AS Leistung                                                                 
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungReise
                    ON
                                Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , reisekostenLeistung as
            (
                SELECT
                    Projektabrechnung.ID as ProjektabrechnungID
                  , ProjektabrechnungReise.MitarbeiterID as MitarbeiterID
        		  ,     COALESCE(ProjektabrechnungReise.BelegeViadeeLeistung, 0) +
        		        COALESCE(ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung, 0) +
        				COALESCE(ProjektabrechnungReise.SpesenLeistung, 0) +
        				COALESCE(ProjektabrechnungReise.ZuschlaegeLeistung, 0) +
        				COALESCE(ProjektabrechnungReise.PauschaleAnzahl * ProjektabrechnungReise.PauschaleProTag,0) 
        			AS Leistung                                                                                                                                                                                                                                                                                                                      
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungReise
                    ON
                                Projektabrechnung.ID = ProjektabrechnungReise.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , rufbereitschaftLeistung as
            (
                SELECT
                    Projektabrechnung.ID as ProjektabrechnungID
                  , ProjektabrechnungSonderarbeit.MitarbeiterID as MitarbeiterID     
        		  , COALESCE(
        			-- Rufbereitschaft
        			(ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden * ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz +
        			COALESCE(ProjektabrechnungSonderarbeit.RufbereitschaftPauschale, 0)
        			) , 0)
        			AS Leistung
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungSonderarbeit
                    ON
                                Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , sonderarbeitLeistung as
            (
                SELECT
                    Projektabrechnung.ID as ProjektabrechnungID
        			, ProjektabrechnungSonderarbeit.MitarbeiterID as MitarbeiterID 
        			, COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale, 0)  AS Leistung
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungSonderarbeit
                    ON
                                Projektabrechnung.ID = ProjektabrechnungSonderarbeit.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , sonstigeLeistung as
            (
                SELECT
                    Projektabrechnung.ID as ProjektabrechnungID
        		  , ProjektabrechnungSonstige.MitarbeiterID as MitarbeiterID 
        		  , COALESCE(COALESCE(ProjektabrechnungSonstige.PauschaleLeistungen, 0) , 0) AS Leistung
                FROM
                    Projektabrechnung
                        INNER JOIN
                    ProjektabrechnungSonstige
                    ON
                                Projektabrechnung.ID = ProjektabrechnungSonstige.ProjektabrechnungID
                            AND DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
        					AND Projektabrechnung.ProjektID = :projektId
            )
           , Buchungen as
            (
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			projektzeitLeistung
        	   UNION ALL
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			reisezeitLeistung
        	   UNION ALL
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			reisekostenLeistung
        	   UNION ALL
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			rufbereitschaftLeistung
        	   UNION ALL
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			sonderarbeitLeistung
        	   UNION ALL
        	   SELECT MitarbeiterID, Leistung
        	   FROM
        			sonstigeLeistung
            ),
        
            Korrekturbuchungen AS(
                SELECT
                    Korrekturbuchung.MitarbeiterID
                  , Korrekturbuchung.Leistung as Leistung
                FROM
                    (
                        SELECT
                            MitarbeiterID
                             , null as ProjektabrechnungID
                             , CASE
                                   WHEN (AnzahlStundenKosten = 0 OR AnzahlStundenKosten IS NULL)
                                       THEN BetragKostensatz
                                   WHEN (AnzahlStundenKosten <> 0 OR AnzahlStundenKosten IS NOT NULL)
                                       THEN BetragKostensatz*AnzahlStundenKosten
                            END AS Kosten
                             , CASE
                                   WHEN (AnzahlStundenLeistung = 0 OR AnzahlStundenLeistung IS NULL)
                                       THEN BetragStundensatz
                                   WHEN (AnzahlStundenLeistung <> 0 OR AnzahlStundenLeistung IS NOT NULL)
                                       THEN BetragStundensatz*AnzahlStundenLeistung
                            END        AS Leistung
                             , Jahr
                             , Monat
                        FROM
                            ProjektabrechnungKorrekturbuchung
                                INNER JOIN
                            C_Kostenart
                            ON
                                    KostenartID = C_Kostenart.ID
                        WHERE
                            DATEFROMPARTS(Jahr, Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
                          AND ProjektabrechnungKorrekturbuchung.ProjektID = :projektId
                    )
                        as Korrekturbuchung
            )
        
        
        SELECT
        	 MitarbeiterID,
        	 SUM(Leistung) AS Leistung
        FROM
        	(
        	 SELECT * FROM
        	 Buchungen
        	 UNION ALL
        	 SELECT * FROM
        	 Korrekturbuchungen
        	) alleBuchungen
        GROUP BY
        	MitarbeiterID
      """)
  Iterable<LeistungKumuliert> ladeVergangeneLeistungenFuerProjekt(Long projektId,
      Integer jahr, Integer monat);

  @Query("""
      WITH
        herausgefilterteProjekte as (
           SELECT ID
           FROM Projekt
           WHERE Projektnummer IN ('99999', '99998')
        ),
        korrektur as
            (
                SELECT
                    CASE
                        WHEN COUNT(*) > 0 THEN 1
                        ELSE 0
                        END AS KorrekturVorhanden,
                    ProjektabrechnungKorrekturbuchung.ProjektID,
                    ProjektabrechnungKorrekturbuchung.Monat,
                    ProjektabrechnungKorrekturbuchung.Jahr
        
                FROM ProjektabrechnungKorrekturbuchung
        
                GROUP BY ProjektabrechnungKorrekturbuchung.ProjektID, ProjektabrechnungKorrekturbuchung.Monat, ProjektabrechnungKorrekturbuchung.Jahr
            )
        
        SELECT
        Projektabrechnung.*,
        CASE WHEN korrektur.KorrekturVorhanden IS NULL THEN 0
            ELSE korrektur.KorrekturVorhanden
           END as KorrekturVorhanden,
        CASE WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL THEN 0
            ELSE 1
           END as istAbgeschlossen
        FROM
        Projektabrechnung LEFT OUTER JOIN
        AbgeschlosseneMonate ON
                   Projektabrechnung.Monat = AbgeschlosseneMonate.Monat AND
                   Projektabrechnung.Jahr = AbgeschlosseneMonate.Jahr
                         LEFT JOIN
        korrektur ON
                   korrektur.ProjektID = Projektabrechnung.ProjektID AND
                   korrektur.Monat = Projektabrechnung.Monat AND
                   korrektur.Jahr = Projektabrechnung.Jahr
        WHERE
          Projektabrechnung.StatusID <= :statusID and
          Projektabrechnung.Jahr = :jahr and
          Projektabrechnung.Monat = :monat and
          NOT Projektabrechnung.ProjektID in (select * from herausgefilterteProjekte)

       """)
  Iterable<Projektabrechnung> findProjektabrechnungenKleinerGleichStatusFuerAbrechnungsmonat(
      Integer statusID, Integer jahr,
      Integer monat);

    @Query("""
              SELECT
                Projektabrechnung.ProjektID, SUM(ProjektabrechnungBerechneteLeistung.Leistung) as Leistung
              FROM
                ProjektabrechnungBerechneteLeistung INNER JOIN
                Projektabrechnung ON
                    ProjektabrechnungBerechneteLeistung.ProjektabrechnungID = Projektabrechnung.ID
              WHERE
                ProjektabrechnungBerechneteLeistung.MitarbeiterID IS NULL AND
                NOT EXISTS (
                    SELECT
                        'X'
                    FROM
                        ProjektabrechnungSonstige
                    WHERE
                        ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID AND
                        ProjektabrechnungSonstige.MitarbeiterID IS NULL
                ) AND
                Projektabrechnung.ProjektID = :projektId AND
                DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) < DATEFROMPARTS(:jahr, :monat, 1)
              GROUP BY Projektabrechnung.ProjektID
            """)
    BigDecimal ladeOhneMitarbeiterbezugVerteilteLeistung(Long projektId, Integer jahr, Integer monat);
}
