package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.repositories.pabdb.ProjektRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektService {

  private final ProjektRepository projektRepository;

  public ProjektService(final ProjektRepository projektRepository) {
    this.projektRepository = projektRepository;
  }

  public List<Projekt> alleProjekte() {
    return IterableUtils.toList(projektRepository.findAll());
  }

  public Projekt projektByProjektnummer(final String projektnummer) {
    return projektRepository.findByProjektnummer(projektnummer);
  }

  public Projekt projektById(final Long id) {
    return projektRepository.findById(id).orElse(null);
  }

  public List<Projekt> projekteFuerDieAbrechnungImAbrechnungsmonatFehlt(int jahr, int monat) {
    return IterableUtils.toList(
        projektRepository.findAlleProjekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
            jahr, monat));
  }

  public List<Projekt>
  alleFestpreisProjekteZuDenenArbeitsnachweisProjektstundenHatUndProjektabrechnungExistiert(
      Long arbeitsnachweisId) {
    return projektRepository
        .findFestpreisProjekteMitProjektabrechnungMitProjektstundenByArbeitsnachweisId(
            arbeitsnachweisId);
  }

  public List<Projekt> alleProjekteZuArbeitsnachweis(Long arbeitsnachweisId) {
    return projektRepository.findAlleProjekteZuArbeitsnachweisId(arbeitsnachweisId);
  }
}
