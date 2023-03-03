package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Faktur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakturRepository extends CrudRepository<Faktur, Long> {

  Iterable<Faktur> findFaktursByProjektId(final Long projektId);

}
