package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.BuchungstypStundenKonstante;
import de.viadee.pabbackend.entities.BuchungstypUrlaubKonstante;
import de.viadee.pabbackend.entities.KostenartKonstante;
import de.viadee.pabbackend.entities.LohnartKonstante;
import de.viadee.pabbackend.entities.MitarbeiterTypKonstante;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.entities.Stadt;
import de.viadee.pabbackend.entities.ViadeeAuslagenKostenartenKonstante;
import de.viadee.pabbackend.services.fachobjekt.KonstantenService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.KONSTANTEN)
public class KonstantenController {

  private final KonstantenService konstantenService;

  public KonstantenController(final KonstantenService konstantenService) {
    this.konstantenService = konstantenService;
  }

  @Operation(summary = "Liefert eine Liste der möglichen Belegarten.")
  @GetMapping("/belegart")
  public ResponseEntity<List<BelegartKonstante>> getAllBelegartKonstante() {
    return ResponseEntity.ok(konstantenService.alleBelegarten());
  }

  @Operation(summary = "Liefert eine Liste aller Städte.")
  @GetMapping("/stadt")
  public ResponseEntity<List<Stadt>> getAllStaedte() {
    return ResponseEntity.ok(konstantenService.alleStaedte());
  }

  @GetMapping("/projektstundeTyp")
  public ResponseEntity<List<ProjektstundeTypKonstante>> getAllProjektstundeTypKonstante() {
    return ResponseEntity.ok(konstantenService.alleProjektstundeTypen());
  }

  @GetMapping("/mitarbeiterTyp")
  public ResponseEntity<List<MitarbeiterTypKonstante>> getAllMitarbeiterTypKonstante() {
    return ResponseEntity.ok(konstantenService.alleMitarbeiterTypen());
  }

  @GetMapping("/buchungstypStunden")
  public ResponseEntity<List<BuchungstypStundenKonstante>> getAllBuchungstypStundenKonstante() {
    return ResponseEntity.ok(konstantenService.alleBuchungstypStunden());
  }

  @GetMapping("/buchungstypUrlaub")
  public ResponseEntity<List<BuchungstypUrlaubKonstante>> getAllBuchungstypUrlaubKonstante() {
    return ResponseEntity.ok(konstantenService.alleBuchungstypUrlaubs());
  }

  @GetMapping("/viadeeAuslagenKostenart")
  public ResponseEntity<List<ViadeeAuslagenKostenartenKonstante>> getAllViadeeAuslagenKostenartKonstante() {
    return ResponseEntity.ok(konstantenService.alleViadeeAuslagenKostenarten());
  }

  @GetMapping("/kostenart")
  public ResponseEntity<List<KostenartKonstante>> getAllKostenartKonstante() {
    return ResponseEntity.ok(konstantenService.alleKostenarten());
  }

  @GetMapping("/lohnarten")
  public ResponseEntity<List<LohnartKonstante>> getAllLohnartKonstante() {
    return ResponseEntity.ok(konstantenService.alleLohnarten());
  }

}
