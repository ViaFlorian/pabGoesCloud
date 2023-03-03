package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.GesetzlicheSpesenKonstante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GesetzlicheSpesenRepository extends
    CrudRepository<GesetzlicheSpesenKonstante, Long> {

}
