package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.AbgeschlosseneMonate;
import de.viadee.pabbackend.entities.MonatsabschlussAktion;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbgeschlosseneMonateRepository extends CrudRepository<AbgeschlosseneMonate, Long> {

  AbgeschlosseneMonate findByJahrAndMonat(int jahr, int monat);

  @Query("""
      WITH
          jahrMonat as (
              SELECT :jahr as Jahr, :monat as Monat
          ),
          monatAbgeschlossen as (
              SELECT
                  jahrMonat.Jahr,
                  jahrMonat.Monat,
                  'Monat abgeschlossen' as titel,
                  CASE WHEN AbgeschlosseneMonate.AbgeschlossenAm IS NULL
                        THEN 'FALSE'
                        ELSE 'TRUE'
                      END as istErledigt,
                  AbgeschlosseneMonate.AbgeschlossenAm as durchgefuehrtAm,
                  AbgeschlosseneMonate.AbgeschlossenVonMitarbeiterID as mitarbeiterID,
                  1 as rang,
                  null as LodasExport
              FROM
                  jahrMonat LEFT JOIN
                  AbgeschlosseneMonate ON
                              jahrMonat.Jahr = AbgeschlosseneMonate.Jahr AND
                              jahrMonat.Monat = AbgeschlosseneMonate.Monat
          ),
          lodasDateiErzeugt as (
              SELECT
                  jahrMonat.Jahr,
                  jahrMonat.Monat,
                  'LODAS erzeugt' as titel,
                  CASE WHEN AbgeschlosseneMonate.LodasErzeugtAm IS NULL
                        THEN 'FALSE'
                        ELSE 'TRUE'
                      END as istErledigt,
                  AbgeschlosseneMonate.LodasErzeugtAm  as durchgefuehrtAm,
                  AbgeschlosseneMonate.LodasErzeugtMitarbeiterID as mitarbeiterID,
                  3 as rang,
                  AbgeschlosseneMonate.LodasExport as LodasExport
              FROM
                  jahrMonat LEFT JOIN
                  AbgeschlosseneMonate ON
                              jahrMonat.Jahr = AbgeschlosseneMonate.Jahr AND
                              jahrMonat.Monat = AbgeschlosseneMonate.Monat
          ),
          jahresuebersichtVersendet as (
              SELECT
                  jahrMonat.Jahr,
                  jahrMonat.Monat,
                  'Jahresübersicht versendet' as titel,
                  CASE WHEN AbgeschlosseneMonate.JahresuebersichtVersendetAm IS NULL 
                        THEN 'FALSE'
                        ELSE 'TRUE'
                      END as istErledigt,
                  AbgeschlosseneMonate.JahresuebersichtVersendetAm  as durchgefuehrtAm,
                  AbgeschlosseneMonate.JahresuebersichtVersendetMitarbeiterID as mitarbeiterID,
                  2 as rang,
                  null as LodasExport
              FROM
                  jahrMonat LEFT JOIN
                  AbgeschlosseneMonate ON
                              jahrMonat.Jahr = AbgeschlosseneMonate.Jahr AND
                              jahrMonat.Monat = AbgeschlosseneMonate.Monat
          )
            
      SELECT
          *
      FROM
          monatAbgeschlossen
      UNION
      SELECT
          *
      FROM
          lodasDateiErzeugt
      UNION
      SELECT
          *
      FROM
          jahresuebersichtVersendet
      """)
  Iterable<MonatsabschlussAktion> monatsabschlussAktionen(int jahr,
      int monat);
}
