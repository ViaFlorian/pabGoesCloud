package de.viadee.pabbackend.controller;

import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.entities.Organisationseinheit;
import de.viadee.pabbackend.services.fachobjekt.OrganisationseinheitService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BackendPfade.ORGANISATIONSEINHEIT)
public class OrganisationseinheitController {

  private final OrganisationseinheitService organisationseinheitService;

  public OrganisationseinheitController(
      final OrganisationseinheitService organisationseinheitService) {
    this.organisationseinheitService = organisationseinheitService;
  }

  @Operation(summary = "Liefert eine Liste aller Organisationseinheiten zurück.")
  @GetMapping("/all")
  public ResponseEntity<List<Organisationseinheit>> getAlleOrganisationseinheiten() {
    return ResponseEntity.ok(organisationseinheitService.alleOrganisationseinheiten());
  }

}
