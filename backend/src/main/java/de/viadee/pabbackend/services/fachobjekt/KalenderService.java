package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.Kalender;
import de.viadee.pabbackend.repositories.pabdb.KalenderRepository;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class KalenderService {

  final KalenderRepository kalenderRepository;

  public KalenderService(final KalenderRepository kalenderRepository) {
    this.kalenderRepository = kalenderRepository;
  }

  public boolean tagIstFeiertag(final LocalDate datum) {
    Kalender kalenderEintrag = kalenderRepository.findFirstByDatum(datum);
    return kalenderEintrag == null ? false : kalenderEintrag.istFeiertag();
  }

  public Kalender leseKalenderByDatum(final LocalDate datum) {
    return kalenderRepository.findFirstByDatum(datum);
  }

  public List<Kalender> ladeAlleTageDesMonats(Integer jahr, Integer monat) {
    return IterableUtils.toList(kalenderRepository.findAllByJahrAndMonat(jahr, monat));
  }
}
