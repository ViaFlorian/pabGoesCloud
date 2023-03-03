package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.SonderzeitenKontoZuweisung;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonderzeitenKontoZuweisungRepository extends
    CrudRepository<SonderzeitenKontoZuweisung, Long> {

}
