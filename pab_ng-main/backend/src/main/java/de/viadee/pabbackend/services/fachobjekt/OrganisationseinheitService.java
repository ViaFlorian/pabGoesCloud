package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Organisationseinheit;
import de.viadee.pabbackend.repositories.pabdb.OrganisationseinheitRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class OrganisationseinheitService {

  private final OrganisationseinheitRepository organisationseinheitRepository;

  public OrganisationseinheitService(
      OrganisationseinheitRepository organisationseinheitRepository) {
    this.organisationseinheitRepository = organisationseinheitRepository;
  }

  public Organisationseinheit organisationseinheitById(final Long id) {
    return organisationseinheitRepository.findById(id).orElse(null);
  }

  public List<Organisationseinheit> alleOrganisationseinheiten() {
    return IterableUtils.toList(organisationseinheitRepository.findAll());
  }
}
