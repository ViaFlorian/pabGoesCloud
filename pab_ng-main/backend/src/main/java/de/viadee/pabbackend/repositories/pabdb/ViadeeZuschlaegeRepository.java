package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ViadeeZuschlaege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViadeeZuschlaegeRepository extends CrudRepository<ViadeeZuschlaege, Long> {

}
