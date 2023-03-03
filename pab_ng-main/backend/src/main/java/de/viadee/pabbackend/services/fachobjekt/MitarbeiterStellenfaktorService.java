package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.MitarbeiterStellenfaktor;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterStellenfaktorRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterStellenfaktorService {

  private final MitarbeiterStellenfaktorRepository mitarbeiterStellenfaktorRepository;

  public MitarbeiterStellenfaktorService(
      MitarbeiterStellenfaktorRepository mitarbeiterStellenfaktorRepository) {
    this.mitarbeiterStellenfaktorRepository = mitarbeiterStellenfaktorRepository;
  }

  public MitarbeiterStellenfaktor mitarbeiterStellenfaktorByMitarbeiterIDJahrMonat(
      final Long mitarbeiterId, final Integer jahr, final Integer monat) {
    MitarbeiterStellenfaktor leererStellenfaktor = new MitarbeiterStellenfaktor();
    leererStellenfaktor.setStellenfaktor(new BigDecimal("0.00001")); // Division durch 0 verhindern
    LocalDate gueltigAb = LocalDate.of(jahr, monat, 1);
    MitarbeiterStellenfaktor mitarbeiterStellenfaktor =
        mitarbeiterStellenfaktorRepository.findFirstByMitarbeiterIdAndGueltigAbLessThanEqual(
            mitarbeiterId, gueltigAb.getYear(), gueltigAb.getMonthValue());
    return mitarbeiterStellenfaktor != null ? mitarbeiterStellenfaktor : leererStellenfaktor;
  }
}
