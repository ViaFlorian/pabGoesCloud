package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.repositories.pabdb.VertragRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class VertragService {

  private final VertragRepository vertragRepository;

  VertragService(VertragRepository vertragRepository) {
    this.vertragRepository = vertragRepository;
  }

  BigDecimal leseMaximalenGueltigenStundensatzZuMitarbeiterUndOderProjekt(
      final Long mitarbeiterId, final Long projektId, final Integer jahr, final Integer monat) {
    return this.vertragRepository.leseMaximalenGueltigenStundensatzZuMitarbeiterUndOderProjekt(
        mitarbeiterId, projektId, jahr, monat);
  }

}
