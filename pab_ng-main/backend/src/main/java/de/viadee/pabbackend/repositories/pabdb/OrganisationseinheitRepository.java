package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Organisationseinheit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationseinheitRepository extends
    CrudRepository<Organisationseinheit, Long> {

}
