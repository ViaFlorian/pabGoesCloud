package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.ProjektabrechnungBerechneteLeistung;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungBerechneteLeistungRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektabrechnungBerechneteLeistungService {

  private final ProjektabrechnungBerechneteLeistungRepository
      projektabrechnungBerechneteLeistungRepository;

  public ProjektabrechnungBerechneteLeistungService(
      ProjektabrechnungBerechneteLeistungRepository projektabrechnungBerechneteLeistungRepository) {
    this.projektabrechnungBerechneteLeistungRepository =
        projektabrechnungBerechneteLeistungRepository;
  }

  public List<ProjektabrechnungBerechneteLeistung> projektabrechnungBerechneteLeistungByProjektabrechnungId(
      Long projektabrechnungId) {
    return IterableUtils.toList(
        projektabrechnungBerechneteLeistungRepository.findAllByProjektabrechnungId(
            projektabrechnungId));
  }

  public List<ProjektabrechnungBerechneteLeistung> ladeVergangeneLeistungen(Long projektId,
      Integer jahr, Integer monat) {
    return IterableUtils.toList(
        projektabrechnungBerechneteLeistungRepository.ladeVergangeneLeistungen(projektId, jahr,
            monat));
  }

  public void loescheByProjektabrechnungId(Long projektabrechnungId) {
    projektabrechnungBerechneteLeistungRepository.deleteByProjektabrechnungId(projektabrechnungId);
  }
}
