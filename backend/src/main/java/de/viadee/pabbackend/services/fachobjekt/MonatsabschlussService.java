package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.AbgeschlosseneMonate;
import de.viadee.pabbackend.entities.MonatsabschlussAktion;
import de.viadee.pabbackend.repositories.pabdb.AbgeschlosseneMonateRepository;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;


@Service
public class MonatsabschlussService {

  private final AbgeschlosseneMonateRepository abgeschlosseneMonateRepository;

  public MonatsabschlussService(AbgeschlosseneMonateRepository abgeschlosseneMonateRepository) {
    this.abgeschlosseneMonateRepository = abgeschlosseneMonateRepository;
  }

  public boolean istAbgeschlossen(LocalDate abrechnungsmonat) {
    AbgeschlosseneMonate abgeschlossenerMonat = abgeschlosseneMonateRepository.findByJahrAndMonat(
        abrechnungsmonat.getYear(), abrechnungsmonat.getMonthValue());
    return abgeschlossenerMonat != null;
  }

  public boolean istAbgeschlossen(int jahr, int monat) {
    AbgeschlosseneMonate abgeschlossenerMonat = abgeschlosseneMonateRepository.findByJahrAndMonat(
        jahr, monat);
    return abgeschlossenerMonat != null;
  }

  public List<MonatsabschlussAktion> monatsabschlussAktionen(
      final int jahr, final int monat) {
    return IterableUtils.toList(
        abgeschlosseneMonateRepository.monatsabschlussAktionen(jahr, monat));
  }
}
