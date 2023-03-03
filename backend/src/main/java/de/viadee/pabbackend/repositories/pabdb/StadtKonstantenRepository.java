package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Stadt;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StadtKonstantenRepository extends CrudRepository<Stadt, Long> {
    Stadt findByName(String trim);

    List<Stadt> findByNameContainingIgnoreCase(String trim);
}
