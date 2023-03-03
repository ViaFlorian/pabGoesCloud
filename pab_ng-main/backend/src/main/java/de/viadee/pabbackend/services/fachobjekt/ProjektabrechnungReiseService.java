package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektabrechnungReise;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungReiseRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektabrechnungReiseService {

  private final ProjektabrechnungReiseRepository projektabrechnungReiseRepository;

  public ProjektabrechnungReiseService(
      ProjektabrechnungReiseRepository projektabrechnungReiseRepository) {
    this.projektabrechnungReiseRepository = projektabrechnungReiseRepository;
  }

  public ProjektabrechnungReise projektabrechnungReiseByProjektabrechnungIdUndMitarbeiterId(
      final Long projektabrechnungId, final Long mitarbeiterId) {
    return projektabrechnungReiseRepository.findByProjektabrechnungIdAndMitarbeiterId(
        projektabrechnungId, mitarbeiterId);
  }

  public ProjektabrechnungReise projektabrechnungReiseByMonatJahrUndMitarbeiterId(Integer monat,
      Integer jahr,
      Long mitarbeiterId, Long projektId) {
    return projektabrechnungReiseRepository.ladeProjektabrechnungReiseByMonatJahrMitarbeiterId(
        monat, jahr, mitarbeiterId, projektId);
  }

  public List<ProjektabrechnungReise> projektabrechnungReiseByProjektabrechnungId(
      Long projektabrechnungId) {
    return IterableUtils.toList(
        projektabrechnungReiseRepository.findAllByProjektabrechnungId(
            projektabrechnungId));
  }

  public ProjektabrechnungReise speichereProjektabrechnungReise(
      ProjektabrechnungReise projektabrechnungReise) {
    return projektabrechnungReiseRepository.save(projektabrechnungReise);
  }

  public void loescheProjektabrechnungReise(Long id) {
    projektabrechnungReiseRepository.deleteById(id);
  }

  public void loescheAlleReiseEintraegeZuProjektabrechnungUndMitarbeiter(Long projektabrechnungId,
      Long mitarbeiterId) {
    projektabrechnungReiseRepository.deleteByProjektabrechnungIdAndMitarbeiterId(
        projektabrechnungId, mitarbeiterId);
  }

  public void loescheProjektabrechnungReiseFuerArbeitsnachweis(Long arbeitsnachweisId) {
    projektabrechnungReiseRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }
}
