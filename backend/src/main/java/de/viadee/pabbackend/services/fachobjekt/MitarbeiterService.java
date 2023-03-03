package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterService {

  private final MitarbeiterRepository mitarbeiterRepository;

  public MitarbeiterService(final MitarbeiterRepository mitarbeiterRepository) {
    this.mitarbeiterRepository = mitarbeiterRepository;
  }

  public List<Mitarbeiter> alleMitarbeiter() {
    return IterableUtils.toList(mitarbeiterRepository.findAll());
  }

  public List<Mitarbeiter> mitarbeiterSelectOptions(
      boolean aktiveMitarbeiter,
      boolean inaktiveMitarbeiter,
      boolean interneMitarbeiter,
      boolean externeMitarbeiter,
      boolean beruecksichtigeEintrittsdatum,
      boolean alleMitarbeiterMitArbeitsnachweis) {
    return IterableUtils.toList(
        mitarbeiterRepository.mitarbeiterSelectOptions(
            aktiveMitarbeiter,
            inaktiveMitarbeiter,
            interneMitarbeiter,
            externeMitarbeiter,
            beruecksichtigeEintrittsdatum,
            alleMitarbeiterMitArbeitsnachweis));
  }

  public Mitarbeiter mitarbeiterById(final Long id) {
    return mitarbeiterRepository.findById(id).orElse(null);
  }

  public Mitarbeiter mitarbeiterByPersonalnummer(String personalnummer) {
    return mitarbeiterRepository.findByPersonalNr(personalnummer).orElse(null);
  }

  public List<Mitarbeiter> mitarbeiterUndStudentenDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
      int jahr, int monat) {
    return IterableUtils.toList(mitarbeiterRepository
        .mitarbeiterUndStudentenDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(jahr, monat));
  }

  public List<Mitarbeiter> externeMitarbeiterDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(
      int jahr, int monat) {
    return IterableUtils.toList(mitarbeiterRepository
        .externeMitarbeiterDenenDerArbeitsnachweisFuerAbrechnungsmonatFehlt(jahr, monat));
  }
}
