package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.DreiMonatsRegel;
import de.viadee.pabbackend.repositories.pabdb.DreiMonatsRegelRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class DreiMonatsRegelService {

  final DreiMonatsRegelRepository dreiMonatsRegelRepository;

  public DreiMonatsRegelService(DreiMonatsRegelRepository dreiMonatsRegelRepository) {
    this.dreiMonatsRegelRepository = dreiMonatsRegelRepository;
  }

  public List<DreiMonatsRegel> speichereDreiMonatsRegeln(List<DreiMonatsRegel> dreiMonatsRegeln) {
    return IterableUtils.toList(dreiMonatsRegelRepository.saveAll(dreiMonatsRegeln));
  }

  public List<DreiMonatsRegel> manuelleDreiMonatsRegelnFuerAbrechnungsmonat(Long mitarbeiterId,
      int jahr, int monat) {
    return IterableUtils.toList(
        dreiMonatsRegelRepository.findManuelleDreiMonatsRegelnFuerAbrechnungsmonat(
            mitarbeiterId, jahr, monat));
  }

  public List<DreiMonatsRegel> dreiMonatsRegelnFuerAbrechnungsmonat(Long mitarbeiterId, int jahr,
      int monat) {
    return IterableUtils.toList(
        dreiMonatsRegelRepository.findDreiMonatsRegelnFuerAbrechnungsmonat(
            mitarbeiterId,
            jahr, monat));
  }

  public void loescheAutomatischErfassteOffeneDreiMonatsRegeln(Long mitarbeiterId) {
    dreiMonatsRegelRepository.deleteAutomatischErfassteRegelnByMitarbeiterId(mitarbeiterId);
  }

  public void loescheKollidierendeDreiMonatsregeln(
      List<DreiMonatsRegel> berechneteDreiMonatsRegeln) {
    for (DreiMonatsRegel regel : berechneteDreiMonatsRegeln) {
      dreiMonatsRegelRepository.deleteKollidierendeRegeln(
          regel.getArbeitsstaette(),
          regel.getKundeScribeId(),
          regel.getMitarbeiterId(),
          regel.getGueltigVon(),
          regel.getGueltigBis());
    }
  }
}
