package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.MitarbeiterTypKonstante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitarbeiterTypKonstanteRepository extends
    CrudRepository<MitarbeiterTypKonstante, Long> {

}
