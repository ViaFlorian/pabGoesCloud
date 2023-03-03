package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektabrechnungReise;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungReiseRepository extends
    CrudRepository<ProjektabrechnungReise, Long> {

  void deleteByProjektabrechnungIdAndMitarbeiterId(Long projektabrechnungId, Long mitarbeiterId);

  ProjektabrechnungReise findByProjektabrechnungIdAndMitarbeiterId(final Long projektabrechnungId,
      final Long mitarbeiterId);

  Iterable<ProjektabrechnungReise> findAllByProjektabrechnungId(Long projektabrechnungId);

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

      DELETE FROM
          ProjektabrechnungReise
      WHERE
          ID IN (
                  SELECT
                      ProjektabrechnungReise.ID
                  FROM
                      ProjektabrechnungReise INNER JOIN
                      Projektabrechnung ON
                          ProjektabrechnungReise.ProjektabrechnungID = Projektabrechnung.ID INNER JOIN
                      zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte ON
                          zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.ProjektID = Projektabrechnung.ProjektID AND
                          Projektabrechnung.Monat = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Monat AND
                          Projektabrechnung.Jahr = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Jahr AND
                          ProjektabrechnungReise.MitarbeiterID = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.MitarbeiterID
      		)
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);

  @Query("""
      SELECT ProjektabrechnungSonstige.*
                                     FROM ProjektabrechnungSonstige
                                              INNER JOIN Projektabrechnung ON ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID
                                     WHERE Projektabrechnung.ProjektID = :projektId
                                       AND Monat = :monat
                                       AND JAHR = :jahr
                                       AND COALESCE(MitarbeiterID, -1) = COALESCE(:mitarbeiterId, -1)
      """)
  ProjektabrechnungReise ladeProjektabrechnungReiseByMonatJahrMitarbeiterId(Integer monat,
      Integer jahr,
      Long mitarbeiterId, Long projektId);
}
