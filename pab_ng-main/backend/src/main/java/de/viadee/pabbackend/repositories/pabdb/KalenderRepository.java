package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Kalender;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalenderRepository extends CrudRepository<Kalender, Long> {

  Kalender findFirstByDatum(final LocalDate datum);

  Iterable<Kalender> findAllByJahrAndMonat(final Integer jahr, final Integer monat);

}
