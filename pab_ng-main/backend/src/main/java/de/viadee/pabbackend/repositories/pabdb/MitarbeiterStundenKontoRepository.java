package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.MitarbeiterStundenKonto;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitarbeiterStundenKontoRepository extends
    CrudRepository<MitarbeiterStundenKonto, Long> {

  Iterable<MitarbeiterStundenKonto> findAllByMitarbeiterId(Long mitarbeiterId);


  MitarbeiterStundenKonto findFirstByMitarbeiterIdAndWertstellungLessThanOrderByWertstellungDescBuchungsdatumDesc(
      Long mitarbeiterId, LocalDate wertstellung);
}
