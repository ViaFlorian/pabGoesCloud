package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.MitarbeiterStundenKonto;
import de.viadee.pabbackend.entities.Projekt;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.enums.StundenBuchungstyp;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterStundenKontoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterStundenKontoService {

  final MitarbeiterStundenKontoRepository mitarbeiterStundenKontoRepository;
  final ProjektService projektService;
  final MitarbeiterService mitarbeiterService;
  final KonstantenService konstantenService;


  public MitarbeiterStundenKontoService(
      MitarbeiterStundenKontoRepository mitarbeiterStundenKontoRepository,
      ProjektService projektService, MitarbeiterService mitarbeiterService,
      KonstantenService konstantenService) {
    this.mitarbeiterStundenKontoRepository = mitarbeiterStundenKontoRepository;
    this.projektService = projektService;
    this.mitarbeiterService = mitarbeiterService;
    this.konstantenService = konstantenService;
  }


  public MitarbeiterStundenKonto erstelleNeuenStundenKontoSatzAusKorrekturbuchung(
      ProjektabrechnungKorrekturbuchung korrekturbuchung) {
    if (korrekturbuchung.getGegenbuchungID() != null
        && (korrekturbuchung.getStundendifferenzGegenbuchung() == null
        || korrekturbuchung.getStundendifferenzGegenbuchung().compareTo(
        BigDecimal.ZERO) == 0)) {
      return null;
    }

    MitarbeiterStundenKonto neuerKontoSatz = new MitarbeiterStundenKonto();

    Projekt projekt = projektService.projektById(korrekturbuchung.getProjektId());

    neuerKontoSatz.setAutomatisch(false);
    neuerKontoSatz.setBemerkung(
        "Korrekturbuchung Projekt " + projekt.getProjektnummer());
    neuerKontoSatz.setBuchungstypStundenId(konstantenService.buchungstypStundenByBezeichnung(
        StundenBuchungstyp.IST_STUNDEN.toString()).getId());
    neuerKontoSatz.setBuchungsdatum(LocalDateTime.now());
    neuerKontoSatz.setWertstellung(LocalDate.of(korrekturbuchung.getJahr(),
        korrekturbuchung.getMonat(), 1).plusMonths(1).minusDays(1));
    neuerKontoSatz.setMitarbeiterId(korrekturbuchung.getMitarbeiterId());
    if (korrekturbuchung.getGegenbuchungID() == null) {
      neuerKontoSatz.setAnzahlStunden(korrekturbuchung.getAnzahlStundenKosten());
    } else if (korrekturbuchung.getGegenbuchungID() != null
        && korrekturbuchung.getStundendifferenzGegenbuchung() != null
        && korrekturbuchung.getStundendifferenzGegenbuchung().compareTo(
        BigDecimal.ZERO) != 0) {
      neuerKontoSatz.setAnzahlStunden(korrekturbuchung.getStundendifferenzGegenbuchung());
    }
    neuerKontoSatz.setEndgueltig(true);

    return neuerKontoSatz;
  }

  public List<MitarbeiterStundenKonto> mitarbeiterStundenKontoByMitarbeiterId(Long mitarbeiterId) {
    List<MitarbeiterStundenKonto> mitarbeiterStundenKontoList = IterableUtils.toList(
        mitarbeiterStundenKontoRepository.findAllByMitarbeiterId(mitarbeiterId));
    Collections.sort(mitarbeiterStundenKontoList);
    return mitarbeiterStundenKontoList;
  }

  public void loescheMitarbeiterStundenKontosaetze(
      List<MitarbeiterStundenKonto> zuLoeschendeKontosaetze) {
    if (!zuLoeschendeKontosaetze.isEmpty()) {
      mitarbeiterStundenKontoRepository.deleteAll(
          zuLoeschendeKontosaetze);
    }
  }

//  public void loescheAutomatischeStundenKontosaetzeZuJahrMonat(final int jahr, final int monat){
//    mitarbeiterStundenKontoRepository.deleteByJahrMonat(jahr, monat);
//  }

  public void speichereMitarbeiterStundenkontosaetze(List<MitarbeiterStundenKonto> kontosaetze) {
    if (!kontosaetze.isEmpty()) {
      mitarbeiterStundenKontoRepository.saveAll(kontosaetze);
    }
  }

  public MitarbeiterStundenKonto ladeAktuellstenStundenSaldoFuerMitarbeiterAbrechnungsmonat(
      final Long mitarbeiterId, final Integer jahr, final Integer monat) {
    LocalDate wertstellung = LocalDate.of(jahr, monat, 1);

    return mitarbeiterStundenKontoRepository.findFirstByMitarbeiterIdAndWertstellungLessThanOrderByWertstellungDescBuchungsdatumDesc(
        mitarbeiterId, wertstellung);
  }

}
