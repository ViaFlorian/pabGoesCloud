package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektabrechnungSonderarbeit;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungSonderarbeitRepository extends
    CrudRepository<ProjektabrechnungSonderarbeit, Long> {

  void deleteByProjektabrechnungIdAndMitarbeiterId(Long projektabrechnungId, Long mitarbeiterId);

  ProjektabrechnungSonderarbeit findByProjektabrechnungIdAndMitarbeiterId(
      final Long projektabrechnungId, final Long mitarbeiterId);

  Iterable<ProjektabrechnungSonderarbeit> findAllByProjektabrechnungId(
      final Long projektabrechnungId);

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
          ProjektabrechnungSonderarbeit
      WHERE
          ID IN (
                  SELECT
                      ProjektabrechnungSonderarbeit.ID
                  FROM
                      ProjektabrechnungSonderarbeit INNER JOIN
                      Projektabrechnung ON
                          ProjektabrechnungSonderarbeit.ProjektabrechnungID = Projektabrechnung.ID INNER JOIN
                      zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte ON
                          zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.ProjektID = Projektabrechnung.ProjektID AND
                          Projektabrechnung.Monat = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Monat AND
                          Projektabrechnung.Jahr = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.Jahr AND
                          ProjektabrechnungSonderarbeit.MitarbeiterID = zuLoeschenderArbeitsnachweisSamtZugehoerigerProjekte.MitarbeiterID
      		)
      """)
  @Modifying
  void deleteByArbeitsnachweisId(Long arbeitsnachweisId);
}
