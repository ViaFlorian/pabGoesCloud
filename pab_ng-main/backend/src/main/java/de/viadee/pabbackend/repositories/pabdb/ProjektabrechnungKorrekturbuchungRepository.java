package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.LeistungKumuliert;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungKorrekturbuchungRepository extends
    CrudRepository<ProjektabrechnungKorrekturbuchung, Long> {

  @Query("""
      SELECT *
      , 'TRUE' as istKorrekturbuchung
            
      FROM ProjektabrechnungKorrekturbuchung
            
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
      """)
  List<ProjektabrechnungKorrekturbuchung> ladeProjektabrechnungKorrekturbuchungByProjektId(
      long projektId);


  @Query("""
      --Projektzeiten                         
      SELECT
          Projektabrechnung.ProjektID						as  ProjektID
           , ProjektabrechnungProjektzeit.ID as ID
           , null												as GegenbuchungID
           , ProjektabrechnungProjektzeit.MitarbeiterID		as MitarbeiterID
           , 1												as KostenartID
           , Projektabrechnung.Jahr							as Jahr
           , Projektabrechnung.Monat							as Monat
           , 'FALSE' as istKorrekturbuchung
           , null												as ReferenzJahr
           , null												as ReferenzMonat
           , ProjektabrechnungProjektzeit.StundenANW			as AnzahlStundenKosten
           , ProjektabrechnungProjektzeit.Kostensatz			as BetragKostensatz
           , ProjektabrechnungProjektzeit.StundenANW			as AnzahlStundenLeistung
           , ProjektabrechnungProjektzeit.Stundensatz			as BetragStundensatz
           , null												as Bemerkung
           , ProjektabrechnungProjektzeit.ZuletztGeaendertAm	as ZuletztGeaendertAm
           , ProjektabrechnungProjektzeit.ZuletztGeaendertVon  as ZuletztGeaendertVon
            
      FROM ProjektabrechnungProjektzeit
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
        AND (ProjektabrechnungProjektzeit.StundenANW	 <> 0)
            
      UNION ALL
      --Reisezeiten
            
      SELECT
          Projektabrechnung.ProjektID						as ProjektID
          , ProjektabrechnungReise.ID as ID
           , null												as GegenbuchungID
           , ProjektabrechnungReise.MitarbeiterID				as MitarbeiterID
           , 2												as KostenartID
           , Projektabrechnung.Jahr							as Jahr
           , Projektabrechnung.Monat							as Monat
           , 'FALSE' as istKorrekturbuchung
           , null												as ReferenzJahr
           , null												as ReferenzMonat
           , ProjektabrechnungReise.AngerechneteReisezeit		as AnzahlStundenKosten
           , ProjektabrechnungReise.Kostensatz					as BetragKostensatz
           , ProjektabrechnungReise.TatsaechlicheReisezeit		as AnzahlStundenLeistung
           , ProjektabrechnungReise.Stundensatz				as BetragStundensatz
           , null												as Bemerkung
           , ProjektabrechnungReise.ZuletztGeaendertAm			as ZuletztGeaendertAm
           , ProjektabrechnungReise.ZuletztGeaendertVon		as ZuletztGeaendertVon
            
      FROM ProjektabrechnungReise
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
        AND ((ProjektabrechnungReise.AngerechneteReisezeit <> 0)
          OR (ProjektabrechnungReise.TatsaechlicheReisezeit <> 0))
            
      UNION ALL
            
      --Reisekosten
      SELECT
          Projektabrechnung.ProjektID						as ProjektID
          , ProjektabrechnungReise.ID as ID
           , null												as GegenbuchungID
           , ProjektabrechnungReise.MitarbeiterID				as MitarbeiterID
           , 3												as KostenartID
           , Projektabrechnung.Jahr							as Jahr
           , Projektabrechnung.Monat							as Monat
           , 'FALSE' as istKorrekturbuchung
           , null												as ReferenzJahr
           , null												as ReferenzMonat
           , null												as AnzahlStundenKosten
           , ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten
          + ProjektabrechnungReise.SpesenKosten
          + ProjektabrechnungReise.ZuschlaegeKosten
          + ProjektabrechnungReise.BelegeViadeeKosten			as BetragKostensatz
           , null												as AnzahlStundenLeistung
           , ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung
          + ProjektabrechnungReise.SpesenLeistung
          + ProjektabrechnungReise.ZuschlaegeLeistung
          + ProjektabrechnungReise.BelegeViadeeLeistung		as BetragStundensatz
           , null												as Bemerkung
           , ProjektabrechnungReise.ZuletztGeaendertAm			as ZuletztGeaendertAm
           , ProjektabrechnungReise.ZuletztGeaendertVon		as ZuletztGeaendertVon
            
      FROM ProjektabrechnungReise
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
        AND ((ProjektabrechnungReise.BelegeLtArbeitsnachweisKosten + ProjektabrechnungReise.SpesenKosten + ProjektabrechnungReise.ZuschlaegeKosten + ProjektabrechnungReise.BelegeViadeeKosten <> 0)
          OR (ProjektabrechnungReise.BelegeLtArbeitsnachweisLeistung + ProjektabrechnungReise.SpesenLeistung + ProjektabrechnungReise.ZuschlaegeLeistung + ProjektabrechnungReise.BelegeViadeeLeistung  <> 0))
            
      UNION ALL
            
      --Sonderarbeit
      SELECT
          Projektabrechnung.ProjektID											as ProjektID
          , ProjektabrechnungSonderarbeit.ID as ID
           , null																as GegenbuchungID
           , ProjektabrechnungSonderarbeit.MitarbeiterID						as MitarbeiterID
           , 4																as KostenartID
           , Projektabrechnung.Jahr											as Jahr
           , Projektabrechnung.Monat											as Monat
           , 'FALSE' as istKorrekturbuchung
           , null																as ReferenzJahr
           , null																as ReferenzMonat
           , null																as AnzahlStundenKosten
           , (COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50,0) * COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitKostensatz,0) * 0.5) +(COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100, 0) * COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitKostensatz,0)) + COALESCE(ProjektabrechnungSonderarbeit.SonderarbeitPauschale,0)				as BetragKostensatz
           , null																as AnzahlStundenLeistung
           , ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale		as BetragStundensatz
           , null											                    as Bemerkung
           , ProjektabrechnungSonderarbeit.ZuletztGeaendertAm					as ZuletztGeaendertAm
           , ProjektabrechnungSonderarbeit.ZuletztGeaendertVon				as ZuletztGeaendertVon
            
      FROM ProjektabrechnungSonderarbeit
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
        AND ((ProjektabrechnungSonderarbeit.SonderarbeitPauschale <> 0) OR (ProjektabrechnungSonderarbeit.SonderarbeitLeistungPauschale <> 0) OR (ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden50 <> 0)OR (ProjektabrechnungSonderarbeit.SonderarbeitAnzahlStunden100 <> 0))
            
      UNION ALL
      --Rufbereitschaftzeiten
      SELECT
          Projektabrechnung.ProjektID												as ProjektID
          , ProjektabrechnungSonderarbeit.ID as ID
           , null																		as GegenbuchungID
           , ProjektabrechnungSonderarbeit.MitarbeiterID								as MitarbeiterID
           , 5																		as KostenartID
           , Projektabrechnung.Jahr													as Jahr
           , Projektabrechnung.Monat													as Monat
           , 'FALSE' as istKorrekturbuchung
           , null																		as ReferenzJahr
           , null																		as ReferenzMonat
           , ProjektabrechnungSonderarbeit.RufbereitschaftKostenAnzahlStunden			as AnzahlStundenKosten
           , ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz					as BetragKostensatz
            
           , ProjektabrechnungSonderarbeit.RufbereitschaftLeistungAnzahlStunden		as AnzahlStundenLeistung
           , ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz					as BetragStundensatz
           , null																		as Bemerkung
           , ProjektabrechnungSonderarbeit.ZuletztGeaendertAm							as ZuletztGeaendertAm
           , ProjektabrechnungSonderarbeit.ZuletztGeaendertVon							as ZuletztGeaendertVon
            
      FROM ProjektabrechnungSonderarbeit
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID
      WHERE ProjektID = :projektId
        AND ((ProjektabrechnungSonderarbeit.RufbereitschaftKostensatz <> 0)
          OR (ProjektabrechnungSonderarbeit.RufbereitschaftStundensatz  <> 0))
            
      UNION ALL
            
      --Sonstige
      SELECT
          Projektabrechnung.ProjektID						as ProjektID
          , ProjektabrechnungSonstige.ID as ID
           , null												as GegenbuchungID
           , ProjektabrechnungSonstige.MitarbeiterID			as MitarbeiterID
           , 6												as KostenartID
           , Projektabrechnung.Jahr							as Jahr
           , Projektabrechnung.Monat							as Monat
           , 'FALSE' as istKorrekturbuchung
           , null												as ReferenzJahr
           , null												as ReferenzMonat
           , null												as AnzahlStundenKosten
           , ProjektabrechnungSonstige.AuslagenViadee
          +  ProjektabrechnungSonstige.PauschaleKosten		as BetragKostensatz
           , 	null                                            as  AnzahlStundenLeistung
           , ProjektabrechnungSonstige.PauschaleLeistungen	as  BetragStundensatz
           , null												as Bemerkung
           , ProjektabrechnungSonstige.ZuletztGeaendertAm			as ZuletztGeaendertAm
           , ProjektabrechnungSonstige.ZuletztGeaendertVon		as ZuletztGeaendertVon
            
      FROM ProjektabrechnungSonstige
               INNER JOIN Projektabrechnung
                          ON ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID
      WHERE COALESCE(ProjektID, -1) = COALESCE(:projektId, -1)
        AND ((ProjektabrechnungSonstige.AuslagenViadee + ProjektabrechnungSonstige.PauschaleKosten <> 0)
          OR (ProjektabrechnungSonstige.PauschaleLeistungen <> 0))                                                
      """)
  List<ProjektabrechnungKorrekturbuchung> ladeBuchungenByProjektId(
      long projektId);

  @Query("""
          SELECT Top 1*
          , 'TRUE' as istKorrekturbuchung
          
          FROM ProjektabrechnungKorrekturbuchung
          
          
          WHERE GegenbuchungID = :gegenbuchungId AND ID != :id
      """)
  Optional<ProjektabrechnungKorrekturbuchung> ladeProjektabrechnungKorrekturbuchungGegenbuchung(
      Long id,
      String gegenbuchungId);

  @Query("""
      WITH
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
                              DATEFROMPARTS(Jahr, Monat, 1) = DATEFROMPARTS(:jahr, :monat, 1)
                        AND ProjektabrechnungKorrekturbuchung.ProjektID = :projektId
                  )
                      as Korrekturbuchung
          )
            
      SELECT
          MitarbeiterID,
          SUM(Leistung) AS Leistung
      FROM
          Korrekturbuchungen
      GROUP BY
          MitarbeiterID
      """)
  Iterable<LeistungKumuliert> ladeAktuelleKorrekturenFuerProjektMonatUndJahr(Long projektId,
      Integer jahr, Integer monat);
}
