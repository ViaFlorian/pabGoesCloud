package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektabrechnungSonderarbeit;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungSonderarbeitRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektabrechnungSonderarbeitService {

  private final ProjektabrechnungSonderarbeitRepository projektabrechnungSonderarbeitRepository;

  public ProjektabrechnungSonderarbeitService(
      ProjektabrechnungSonderarbeitRepository projektabrechnungSonderarbeitRepository) {
    this.projektabrechnungSonderarbeitRepository = projektabrechnungSonderarbeitRepository;
  }

  public List<ProjektabrechnungSonderarbeit> projektabrechnungSonderarbeitByProjektabrechnungId(
      Long projektabrechnungId) {
    return IterableUtils.toList(
        projektabrechnungSonderarbeitRepository.findAllByProjektabrechnungId(
            projektabrechnungId));
  }

  public ProjektabrechnungSonderarbeit projektabrechnungSonderarbeitByProjektabrechnungIdMitarbeiterId(
      final Long projektabrechnungId, final Long mitarbeiterId) {
    return projektabrechnungSonderarbeitRepository.findByProjektabrechnungIdAndMitarbeiterId(
        projektabrechnungId, mitarbeiterId);
  }

  public ProjektabrechnungSonderarbeit speichereProjektabrechnungSonderarbeit(
      ProjektabrechnungSonderarbeit projektabrechnungSonderarbeit) {
    return projektabrechnungSonderarbeitRepository.save(projektabrechnungSonderarbeit);
  }

  public void loescheById(Long id) {
    projektabrechnungSonderarbeitRepository.deleteById(id);
  }

  public void loescheProjektabrechnungSonderarbeitFuerArbeitsnachweis(Long arbeitsnachweisId) {
    projektabrechnungSonderarbeitRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }

  public void loescheAlleSonderarbeitEintraegeZuProjektabrechnungUndMitarbeiter(
      final Long projektabrechnungId, final Long mitarbeiterId) {
    projektabrechnungSonderarbeitRepository.deleteByProjektabrechnungIdAndMitarbeiterId(
        projektabrechnungId, mitarbeiterId);
  }
}
