package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektBudget;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektBudgetRepository extends CrudRepository<ProjektBudget, Long> {

  @Query("""
      WITH budgetInDatumsReihenfolge AS
      (
      SELECT
        Projektbudget.*,
        ROW_NUMBER() OVER (ORDER BY ProjektID, Wertstellung desc, Buchungsdatum desc) as rn
      FROM
        Projektbudget
      WHERE
        Projektbudget.ProjektID = :projektId
      )
      SELECT
        pb.*,
        SUM(pb.BudgetBetrag)  OVER (ORDER BY rn desc) as SaldoBerechnet
      FROM
        budgetInDatumsReihenfolge pb
      WHERE
          DATEFROMPARTS(DATEPART(year, pb.Wertstellung), DATEPART(month, pb.Wertstellung), 1) <= :stichtag
      ORDER BY pb.Wertstellung desc, pb.Buchungsdatum desc
        """)
  Optional<ProjektBudget> ladeProjektbudgetByProjektIdMitStichtag(Long projektId,
      LocalDate stichtag);

  @Query("""
      WITH budgetInDatumsReihenfolge AS
      (
      SELECT
        Projektbudget.*,
        ROW_NUMBER() OVER (ORDER BY ProjektID, Wertstellung desc, Buchungsdatum desc) as rn
      FROM
        Projektbudget
      WHERE
        Projektbudget.ProjektID = :projektId
      )
      SELECT
        SUM(pb.BudgetBetrag)  OVER (ORDER BY rn desc) as SaldoBerechnet
      FROM
        budgetInDatumsReihenfolge pb
      WHERE
          DATEFROMPARTS(DATEPART(year, pb.Wertstellung), DATEPART(month, pb.Wertstellung), 1) <= :stichtag
      ORDER BY pb.Wertstellung desc, pb.Buchungsdatum desc
        """)
  BigDecimal errechneSaldoZuProjektIdMitSitchtag(Long projektId,
      LocalDate stichtag);

  Iterable<ProjektBudget> findProjektBudgetsByProjektId(final Long projektId);

}
