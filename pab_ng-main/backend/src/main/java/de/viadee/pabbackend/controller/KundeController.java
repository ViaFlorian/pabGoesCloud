package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Kunde;
import de.viadee.pabbackend.services.fachobjekt.KundeService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.KUNDE)
public class KundeController {

  private final KundeService kundeService;

  public KundeController(final KundeService kundeService) {
    this.kundeService = kundeService;
  }

  @Operation(summary = "Liefert eine Liste aller Kunden zurück.")
  @GetMapping("/all")
  public ResponseEntity<List<Kunde>> getAlleKunden() {
    return ResponseEntity.ok(kundeService.alleKunden());
  }

}
