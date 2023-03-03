package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Projekt;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektRepository extends CrudRepository<Projekt, Long> {

  @Override
  @Cacheable("projekte")
  Optional<Projekt> findById(Long id);

  @Override
  @CacheEvict(cacheNames = "projekte", beforeInvocation = false, key = "#result.id")
  <S extends Projekt> S save(S s);

  @Cacheable("projekte")
  Projekt findByProjektnummer(String projektnummer);

  @Query("""
      SELECT
          *
      FROM
          Projekt
      WHERE
      NOT EXISTS
          (
              SELECT
                  1
              FROM
                  Projektabrechnung
              WHERE
                 Projektabrechnung.Jahr = :jahr AND
                  Projektabrechnung.Monat =  :monat AND
                 Projektabrechnung.ProjektID = Projekt.ID
          )
      """)
  Iterable<Projekt> findAlleProjekteFuerDieAbrechnungImAbrechnungsmonatFehlt(int jahr, int monat);

  @Query("""
      SELECT
          DISTINCT Projekt.*
      FROM
          Arbeitsnachweis INNER JOIN
          Projektstunde ON
              Projektstunde.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
          Projekt ON
              Projekt.ID = Projektstunde.ProjektID AND
              Projekt.Projekttyp IN ('Festpreis', 'Wartung', 'Produkt') INNER JOIN
          Projektabrechnung ON
              Projektabrechnung.Monat = Arbeitsnachweis.Monat AND
              Projektabrechnung.Jahr = Arbeitsnachweis.Jahr AND
              Projektabrechnung.ProjektID = Projekt.ID AND
              Projektabrechnung.Fertigstellungsgrad IS NOT NULL AND
              Projektabrechnung.BudgetBetragZurAbrechnung IS NOT NULL
      WHERE
          Arbeitsnachweis.ID = :arbeitsnachweisId
      """)
  List<Projekt> findFestpreisProjekteMitProjektabrechnungMitProjektstundenByArbeitsnachweisId(
      Long arbeitsnachweisId);

  @Query("""
      SELECT
      	DISTINCT Projekt.*
      FROM
      	Arbeitsnachweis INNER JOIN
      	Projektstunde ON
      		Projektstunde.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Projektstunde.ProjektID INNER JOIN
      	Projektabrechnung ON
      	    Projektabrechnung.Monat = Arbeitsnachweis.Monat AND
      	    Projektabrechnung.Jahr = Arbeitsnachweis.Jahr AND
      	    Projektabrechnung.ProjektID = Projekt.ID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId
      UNION
      SELECT
      	DISTINCT Projekt.*
      FROM
      	Arbeitsnachweis INNER JOIN
      	Beleg ON
      		Beleg.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Beleg.ProjektID INNER JOIN
      	Projektabrechnung ON
      	    Projektabrechnung.Monat = Arbeitsnachweis.Monat AND
      	    Projektabrechnung.Jahr = Arbeitsnachweis.Jahr AND
      	    Projektabrechnung.ProjektID = Projekt.ID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId
      UNION
      SELECT
      	DISTINCT Projekt.*
      FROM
      	Arbeitsnachweis INNER JOIN
      	Abwesenheit ON
      		Abwesenheit.ArbeitsnachweisID = Arbeitsnachweis.ID INNER JOIN
      	Projekt ON
      		Projekt.ID = Abwesenheit.ProjektID INNER JOIN
      	Projektabrechnung ON
      	    Projektabrechnung.Monat = Arbeitsnachweis.Monat AND
      	    Projektabrechnung.Jahr = Arbeitsnachweis.Jahr AND
      	    Projektabrechnung.ProjektID = Projekt.ID
      WHERE
      	Arbeitsnachweis.ID = :arbeitsnachweisId
      """)
  List<Projekt> findAlleProjekteZuArbeitsnachweisId(Long arbeitsnachweisId);

}
