package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektBudget;
import de.viadee.pabbackend.repositories.pabdb.ProjektBudgetRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektBudgetService {

  private final ProjektBudgetRepository projektBudgetRepository;

  public ProjektBudgetService(ProjektBudgetRepository projektBudgetRepository) {
    this.projektBudgetRepository = projektBudgetRepository;
  }

  public ProjektBudget ladeProjektbudgetByProjektIdMitStichtag(Long projektId,
      LocalDate stichtag) {
    Optional<ProjektBudget> optionalProjektBudget = this.projektBudgetRepository.ladeProjektbudgetByProjektIdMitStichtag(
        projektId,
        stichtag);
    if (optionalProjektBudget.isEmpty()) {
      return null;
    }

    ProjektBudget projektBudget = optionalProjektBudget.get();
    projektBudget.setSaldoBerechnet(this.errechneSaldoZuProjektIdMitStichtag(projektId, stichtag));
    return projektBudget;
  }

  public BigDecimal errechneSaldoZuProjektIdMitStichtag(Long projektId,
      LocalDate stichtag) {
    return this.projektBudgetRepository.errechneSaldoZuProjektIdMitSitchtag(projektId, stichtag);
  }

  public List<ProjektBudget> alleProjektBudgetsByProjektId(Long projektIdId) {
    return IterableUtils.toList(
        this.projektBudgetRepository.findProjektBudgetsByProjektId(projektIdId));
  }
}
