package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Skonto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkontoRepository extends CrudRepository<Skonto, Long> {

  Iterable<Skonto> findSkontosByProjektId(final Long projektId);

}
