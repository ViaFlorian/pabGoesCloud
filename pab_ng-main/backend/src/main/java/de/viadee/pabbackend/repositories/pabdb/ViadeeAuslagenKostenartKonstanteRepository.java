package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ViadeeAuslagenKostenartenKonstante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViadeeAuslagenKostenartKonstanteRepository extends
    CrudRepository<ViadeeAuslagenKostenartenKonstante, Long> {

}
