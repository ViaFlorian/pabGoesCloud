package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektabrechnungSonstige;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungSonstigeRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektabrechnungSonstigeService {

  private final ProjektabrechnungSonstigeRepository projektabrechnungSonstigeRepository;

  public ProjektabrechnungSonstigeService(
      final ProjektabrechnungSonstigeRepository projektabrechnungSonstigeRepository) {
    this.projektabrechnungSonstigeRepository = projektabrechnungSonstigeRepository;
  }

  public List<ProjektabrechnungSonstige> projektabrechnungSonstigeByProjektabrechnungId(
      Long projektabrechnungId) {
    return IterableUtils.toList(
        projektabrechnungSonstigeRepository.findAllByProjektabrechnungId(
            projektabrechnungId));
  }

  public ProjektabrechnungSonstige projektabrechnungSonstigeByProjektabrechnungIdMitarbeiterId(
      final Long projektabrechnungId, final Long mitarbeiterId) {
    return projektabrechnungSonstigeRepository.findByProjektabrechnungIdAndMitarbeiterId(
        projektabrechnungId, mitarbeiterId);
  }

  public ProjektabrechnungSonstige projektabrechnungSonstigeByProjektabrechnungIdOhneMitarbeiterbezug(
      final Long projektabrechnungId) {
    return projektabrechnungSonstigeRepository.findByProjektabrechnungIdOhneMitarbeiterbezug(
        projektabrechnungId);
  }

  ProjektabrechnungSonstige projektabrechnungSonstigeByMonatJahrProjektIdUndMitarbeiter(
      Integer monat,
      Integer jahr, long mitarbeiterID, Long projektId) {
    return projektabrechnungSonstigeRepository.ladeProjektabrechnungSonstigeByUndMonatJahrProjektIdUndMitarbeiterId(
        projektId, monat, jahr, mitarbeiterID);
  }

  public void aktualisiereOderFuegeSonstigeProjektkostenEin(
      ProjektabrechnungSonstige aktualisierteSonstigeProjektkosten) {
    this.projektabrechnungSonstigeRepository.save(aktualisierteSonstigeProjektkosten);
  }

  public void loescheByProjektabrechnungSonstigeId(
      Long geloeschteSonstigeProjektkostenId) {
    this.projektabrechnungSonstigeRepository.deleteById(geloeschteSonstigeProjektkostenId);
  }
}
