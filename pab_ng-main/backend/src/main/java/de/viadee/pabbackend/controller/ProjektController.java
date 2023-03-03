package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.SonstigeProjektkosten;
import de.viadee.pabbackend.services.fachobjekt.ProjektService;
import de.viadee.pabbackend.services.fachobjekt.SonstigeProjektkostenService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.PROJEKT)
public class ProjektController {

  private final ProjektService projektService;
  private final SonstigeProjektkostenService sonstigeProjektkostenService;

  public ProjektController(final ProjektService projektService,
      final SonstigeProjektkostenService sonstigeProjektkostenService) {
    this.projektService = projektService;
    this.sonstigeProjektkostenService = sonstigeProjektkostenService;
  }

  @Operation(summary = "Liefert eine Liste aller Projekte zurück.")
  @GetMapping("/all")
  public ResponseEntity<List<Projekt>> getAlleProjekte() {
    return ResponseEntity.ok(projektService.alleProjekte());
  }

  @Operation(summary = "Liefert eine Liste aller sonstigen Projektkosten zurück.")
  @GetMapping("/abrechnung/sonstige/projektkosten/all")
  public ResponseEntity<List<SonstigeProjektkosten>> getAlleSonstigenProjekte() {
    return ResponseEntity.ok(sonstigeProjektkostenService.ladeAlleSonstigeProjektkosten());
  }
}
