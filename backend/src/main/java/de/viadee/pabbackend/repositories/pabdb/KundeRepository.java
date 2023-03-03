package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Kunde;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KundeRepository extends CrudRepository<Kunde, Long> {

  Kunde findKundeByScribeId(final String scribeId);

}
