package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Mitarbeiter;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeit;
import de.viadee.pabbackend.entities.ProjektabrechnungProjektzeitVormonat;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungProjektzeitRepository;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class ProjektabrechnungProjektzeitService {

  private final ProjektabrechnungProjektzeitRepository projektabrechnungProjektzeitRepository;

  private final MitarbeiterService mitarbeiterService;
  private final VertragService vertragService;

  public ProjektabrechnungProjektzeitService(
      ProjektabrechnungProjektzeitRepository projektabrechnungProjektzeitRepository,
      MitarbeiterService mitarbeiterService,
      VertragService vertragService) {
    this.projektabrechnungProjektzeitRepository = projektabrechnungProjektzeitRepository;
    this.mitarbeiterService = mitarbeiterService;
    this.vertragService = vertragService;
  }

  public List<ProjektabrechnungProjektzeit> projektabrechnungProjektzeitByProjektabrechnungIdUndMitarbeiterId(
      Long projektabrechnungId, Long mitarbeiterId, Long projektId, Integer jahr, Integer monat) {
    List<ProjektabrechnungProjektzeit> projektabrechnungProjektzeits = IterableUtils.toList(
        projektabrechnungProjektzeitRepository.findAllByProjektabrechnungIdAndMitarbeiterId(
            projektabrechnungId, mitarbeiterId));

    Mitarbeiter mitarbeiter = this.mitarbeiterService.mitarbeiterById(mitarbeiterId);
    BigDecimal stundensatzVertrag = this.vertragService.leseMaximalenGueltigenStundensatzZuMitarbeiterUndOderProjekt(
        mitarbeiterId, projektId, jahr, monat);
    ProjektabrechnungProjektzeitVormonat vormonatDaten = this.projektabrechnungProjektzeitRepository.getVormonatDaten(
        projektabrechnungId, mitarbeiterId);

    projektabrechnungProjektzeits.forEach((element) -> {
      if (mitarbeiter.getKostensatz() != null
          && mitarbeiter.getKostensatz().compareTo(BigDecimal.ZERO) != 0) {
        element.setKostensatzVertrag(mitarbeiter.getKostensatz());
      }

      element.setStundensatzVertrag(stundensatzVertrag);
      element.setStundensatzVormonat(vormonatDaten.getStundensatzVormonat());
      element.setKostensatzVormonat(vormonatDaten.getKostensatzVormonat());
    });

    return projektabrechnungProjektzeits;
  }

  public List<ProjektabrechnungProjektzeit> projektabrechnungProjektzeitByProjektabrechnungId(
      Long projektabrechnungId) {
    return IterableUtils.toList(
        projektabrechnungProjektzeitRepository.findAllByProjektabrechnungId(
            projektabrechnungId));
  }

  public ProjektabrechnungProjektzeit speichereProjektabrechnungProjektzeit(
      ProjektabrechnungProjektzeit projektabrechnungProjektzeit) {
    return projektabrechnungProjektzeitRepository.save(projektabrechnungProjektzeit);
  }

  public void loescheProjektabrechnungProjektzeit(Long id) {
    projektabrechnungProjektzeitRepository.deleteById(id);
  }

  public void loescheProjektabrechnungProjektzeitenFuerArbeitsnachweis(Long arbeitsnachweisId) {
    projektabrechnungProjektzeitRepository.deleteByArbeitsnachweisId(arbeitsnachweisId);
  }

  public void loescheAlleProjektzeitEintraegeZuProjektabrechnungUndMitarbeiter(
      Long projektabrechnungId, Long mitarbeiterId) {
    projektabrechnungProjektzeitRepository
        .deleteProjektabrechungProjektzeitByProjektabrechnungIdAndMitarbeiterId(
            projektabrechnungId, mitarbeiterId);
  }
}
