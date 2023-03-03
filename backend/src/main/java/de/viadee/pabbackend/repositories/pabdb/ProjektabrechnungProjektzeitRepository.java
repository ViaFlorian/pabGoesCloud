package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeit;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeitVormonat;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungProjektzeitRepository
    extends CrudRepository<ProjektabrechnungProjektzeit, Long> {

  @Query("""
      DELETE FROM ProjektabrechnungProjektzeit
      WHERE ProjektabrechnungID = :projektabrechnungId AND
            MitarbeiterID = :mitarbeiterId
            """)
  @Modifying
  void deleteProjektabrechungProjektzeitByProjektabrechnungIdAndMitarbeiterId(
      Long projektabrechnungId, Long mitarbeiterId);

  Iterable<ProjektabrechnungProjektzeit> findAllByProjektabrechnungIdAndMitarbeiterId(
      Long projektabrechnungId, Long mitarbeiterId);

  Iterable<ProjektabrechnungProjektzeit> findAllByProjektabrechnungId(
      Long projektabrechnungId);

  @Query("""
      WITH
          aktuelleProjektabrechnung AS (
              SELECT
                  *
              FROM
                  Projektabrechnung
              WHERE
                  ID = :projektabrechnungId
          ),
          maximalerVormonat As (
          SELECT TOP 1
              Projektabrechnung.*
          FROM
              Projektabrechnung INNER JOIN
              ProjektabrechnungProjektzeit on Projektabrechnung.ID = ProjektabrechnungProjektzeit.ProjektabrechnungID AND
                                              ProjektabrechnungProjektzeit.MitarbeiterID = :mitarbeiterId
          WHERE
                  ProjektID = (SELECT ProjektID FROM aktuelleProjektabrechnung) AND
                  DATEFROMPARTS(JAHR, MONAT, 1) < (SELECT DATEFROMPARTS(Jahr, Monat, 1) FROM aktuelleProjektabrechnung)
          ORDER BY Projektabrechnung.JAHR DESC, Projektabrechnung.MONAT DESC
            
      )
      SELECT
          COALESCE((
                       SELECT kostensatz
                       FROM (SELECT TOP 1 MAX(Kostensatz) as kostensatz,
                                          Projektabrechnung.Jahr,
                                          Projektabrechnung.Monat
                             FROM ProjektabrechnungProjektzeit
                                      INNER JOIN
                                  Projektabrechnung
                                  on
                                              ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID AND
                                              Projektabrechnung.ProjektID = maximalerVormonat.ProjektID AND
                                              ProjektabrechnungProjektzeit.MitarbeiterID = :mitarbeiterId AND
                                              DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) <=
                                              DATEFROMPARTS(maximalerVormonat.Jahr, maximalerVormonat.Monat, 1)  AND
                                              ProjektabrechnungProjektzeit.Kostensatz <> 0
                             GROUP BY Jahr, Monat
                             ORDER BY Projektabrechnung.Jahr DESC, Projektabrechnung.Monat DESC) as maxKostensatzProMonat
                   ), 0) as KostensatzVormonat,
          COALESCE((
                       SELECT stundensatz
                       FROM (SELECT TOP 1 MAX(Stundensatz) as stundensatz,
                                          Projektabrechnung.Jahr,
                                          Projektabrechnung.Monat
                             FROM ProjektabrechnungProjektzeit
                                      INNER JOIN
                                  Projektabrechnung on
                                              ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID AND
                                              Projektabrechnung.ProjektID = maximalerVormonat.ProjektID AND
                                              ProjektabrechnungProjektzeit.MitarbeiterID = :mitarbeiterId AND
                                              DATEFROMPARTS(Projektabrechnung.Jahr, Projektabrechnung.Monat, 1) <=
                                              DATEFROMPARTS(maximalerVormonat.Jahr, maximalerVormonat.Monat, 1)  AND
                                              ProjektabrechnungProjektzeit.Stundensatz <> 0
                             GROUP BY Projektabrechnung.Jahr, Projektabrechnung.Monat
                             ORDER BY Projektabrechnung.Jahr DESC, Projektabrechnung.Monat DESC) as maxStundensatzProMonat
                   ), 0) as StundensatzVormonat
      FROM
          ProjektabrechnungProjektzeit aktuelleProjektzeit INNER JOIN
          aktuelleProjektabrechnung ON
                      aktuelleProjektzeit.ProjektabrechnungID = aktuelleProjektabrechnung.ID AND
                      aktuelleProjektzeit.MitarbeiterID = :mitarbeiterId LEFT JOIN
          maximalerVormonat ON
                  maximalerVormonat.ProjektID = aktuelleProjektabrechnung.ProjektID
      """)
  ProjektabrechnungProjektzeitVormonat getVormonatDaten(Long projektabrechnungId,
      Long mitarbeiterId);

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

      DELETE
          ProjektabrechnungProjektzeit
      WHERE
          ID IN (
                  SELECT
                      ProjektabrechnungProjektzeit.ID
                  FROM
                      ProjektabrechnungProjektzeit INNER JOIN
                      Projektabrechnung ON
                          ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID INNER JOIN
                      zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte ON
                          zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.ProjektID = Projektabrechnung.ProjektID AND
                          Projektabrechnung.Monat = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Monat AND
                          Projektabrechnung.Jahr = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Jahr AND
                          ProjektabrechnungProjektzeit.MitarbeiterID = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.MitarbeiterID
      		)
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
