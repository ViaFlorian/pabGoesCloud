package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.BelegartKonstante;
import de.viadee.pabbackend.entities.BuchungstypStundenKonstante;
import de.viadee.pabbackend.entities.BuchungstypUrlaubKonstante;
import de.viadee.pabbackend.entities.GesetzlicheSpesenKonstante;
import de.viadee.pabbackend.entities.KostenartKonstante;
import de.viadee.pabbackend.entities.LohnartKonstante;
import de.viadee.pabbackend.entities.MitarbeiterTypKonstante;
import de.viadee.pabbackend.entities.ProjektstundeTypKonstante;
import de.viadee.pabbackend.entities.SonderzeitenKontoZuweisung;
import de.viadee.pabbackend.entities.Stadt;
import de.viadee.pabbackend.entities.ViadeeAuslagenKostenartenKonstante;
import de.viadee.pabbackend.entities.ViadeeZuschlaege;
import de.viadee.pabbackend.repositories.pabdb.BelegartKonstantenRepository;
import de.viadee.pabbackend.repositories.pabdb.BuchungstypStundenRepository;
import de.viadee.pabbackend.repositories.pabdb.BuchungstypUrlaubRepository;
import de.viadee.pabbackend.repositories.pabdb.GesetzlicheSpesenRepository;
import de.viadee.pabbackend.repositories.pabdb.KostenartKonstanteRepository;
import de.viadee.pabbackend.repositories.pabdb.LohnartRepository;
import de.viadee.pabbackend.repositories.pabdb.MitarbeiterTypKonstanteRepository;
import de.viadee.pabbackend.repositories.pabdb.ProjektstundeTypKonstanteRepository;
import de.viadee.pabbackend.repositories.pabdb.SonderzeitenKontoZuweisungRepository;
import de.viadee.pabbackend.repositories.pabdb.StadtKonstantenRepository;
import de.viadee.pabbackend.repositories.pabdb.ViadeeAuslagenKostenartKonstanteRepository;
import de.viadee.pabbackend.repositories.pabdb.ViadeeZuschlaegeRepository;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
public class KonstantenService {

  final BelegartKonstantenRepository belegartKonstantenRepository;
  final ProjektstundeTypKonstanteRepository projektstundeTypKonstanteRepository;
  final LohnartRepository lohnartRepository;
  final StadtKonstantenRepository stadtKonstantenRepository;
  final GesetzlicheSpesenRepository gesetzlicheSpesenRepository;
  final ViadeeZuschlaegeRepository viadeeZuschlaegeRepository;
  final SonderzeitenKontoZuweisungRepository sonderzeitenKontoZuweisungRepository;
  final BuchungstypStundenRepository buchungstypStundenRepository;
  final BuchungstypUrlaubRepository buchungstypUrlaubRepository;
  final KostenartKonstanteRepository kostenartKonstanteRepository;
  final ViadeeAuslagenKostenartKonstanteRepository viadeeAuslagenKostenartKonstanteRepository;
  final MitarbeiterTypKonstanteRepository mitarbeiterTypKonstanteRepository;

  public KonstantenService(final BelegartKonstantenRepository belegartKonstantenRepository,
      final ProjektstundeTypKonstanteRepository projektstundeTypKonstanteRepository,
      final LohnartRepository lohnartRepository,
      final StadtKonstantenRepository stadtKonstantenRepository,
      final GesetzlicheSpesenRepository gesetzlicheSpesenRepository,
      final ViadeeZuschlaegeRepository viadeeZuschlaegeRepository,
      final SonderzeitenKontoZuweisungRepository sonderzeitenKontoZuweisungRepository,
      final BuchungstypStundenRepository buchungstypStundenRepository,
      final BuchungstypUrlaubRepository buchungstypUrlaubRepository,
      final KostenartKonstanteRepository kostenartKonstanteRepository,
      final ViadeeAuslagenKostenartKonstanteRepository viadeeAuslagenKostenartKonstanteRepository,
      final MitarbeiterTypKonstanteRepository mitarbeiterTypKonstanteRepository) {
    this.belegartKonstantenRepository = belegartKonstantenRepository;
    this.projektstundeTypKonstanteRepository = projektstundeTypKonstanteRepository;
    this.lohnartRepository = lohnartRepository;
    this.stadtKonstantenRepository = stadtKonstantenRepository;
    this.gesetzlicheSpesenRepository = gesetzlicheSpesenRepository;
    this.viadeeZuschlaegeRepository = viadeeZuschlaegeRepository;
    this.sonderzeitenKontoZuweisungRepository = sonderzeitenKontoZuweisungRepository;
    this.buchungstypStundenRepository = buchungstypStundenRepository;
    this.buchungstypUrlaubRepository = buchungstypUrlaubRepository;
    this.kostenartKonstanteRepository = kostenartKonstanteRepository;
    this.viadeeAuslagenKostenartKonstanteRepository = viadeeAuslagenKostenartKonstanteRepository;
    this.mitarbeiterTypKonstanteRepository = mitarbeiterTypKonstanteRepository;
  }

  public List<BelegartKonstante> alleBelegarten() {
    return IterableUtils.toList(belegartKonstantenRepository.findAll());
  }

  public BelegartKonstante belegartByTextKurz(final String textKurz) {
    return belegartKonstantenRepository.findByTextKurz(textKurz);
  }

  public ProjektstundeTypKonstante projektstundeTypByTextKurz(final String textKurz) {
    return projektstundeTypKonstanteRepository.findByTextKurz(textKurz);
  }

  public List<Stadt> alleStaedte() {
    return IterableUtils.toList(stadtKonstantenRepository.findAll());
  }

  public List<LohnartKonstante> alleLohnarten() {
    return IterableUtils.toList(lohnartRepository.findAll());
  }

  public List<SonderzeitenKontoZuweisung> sonderzeitenKontoZuweisungen() {
    return IterableUtils.toList(sonderzeitenKontoZuweisungRepository.findAll());
  }

  public LohnartKonstante lohnartByKonto(String konto) {
    return lohnartRepository.findFirstByKonto(konto);
  }

  public LohnartKonstante lohnartById(Long id) {
    return lohnartRepository.findById(id).orElse(null);
  }

  public BelegartKonstante belegartByID(Long belegartId) {
    return belegartKonstantenRepository.findById(belegartId).orElse(null);
  }

  public List<GesetzlicheSpesenKonstante> gesetzlicheSpesen() {
    return IterableUtils.toList(gesetzlicheSpesenRepository.findAll());
  }

  public List<ViadeeZuschlaege> viadeeZuschlaege() {
    return IterableUtils.toList(viadeeZuschlaegeRepository.findAll());
  }

  public BuchungstypStundenKonstante buchungstypStundenByBezeichnung(String bezeichnung) {
    return buchungstypStundenRepository.findFirstByBezeichnung(bezeichnung);
  }

  public BuchungstypUrlaubKonstante buchungstypUrlaubByBezeichnung(String bezeichnung) {
    return buchungstypUrlaubRepository.findFirstByBezeichnung(bezeichnung);
  }

  public List<ProjektstundeTypKonstante> alleProjektstundeTypen() {
    return IterableUtils.toList(projektstundeTypKonstanteRepository.findAll());
  }

  public Stadt stadtByName(String trim) {
    return stadtKonstantenRepository.findByName(trim);
  }

  public List<Stadt> stadtLikeName(String name) {
    return stadtKonstantenRepository.findByNameContainingIgnoreCase(name);
  }

  public List<BuchungstypStundenKonstante> alleBuchungstypStunden() {
    return IterableUtils.toList(buchungstypStundenRepository.findAll());
  }

  public List<BuchungstypUrlaubKonstante> alleBuchungstypUrlaubs() {
    return IterableUtils.toList(buchungstypUrlaubRepository.findAll());
  }

  public List<KostenartKonstante> alleKostenarten() {
    return IterableUtils.toList(kostenartKonstanteRepository.findAll());
  }

  public KostenartKonstante kostenartByBezeichung(String bezeichung) {
    return kostenartKonstanteRepository.findFirstByBezeichnung(bezeichung);
  }

  public KostenartKonstante kostenartByID(Long kostenartId) {
    return kostenartKonstanteRepository.findById(kostenartId).orElse(null);
  }

  public List<ViadeeAuslagenKostenartenKonstante> alleViadeeAuslagenKostenarten() {
    return IterableUtils.toList(viadeeAuslagenKostenartKonstanteRepository.findAll());
  }

  public List<MitarbeiterTypKonstante> alleMitarbeiterTypen() {
    return IterableUtils.toList(mitarbeiterTypKonstanteRepository.findAll());
  }
}
