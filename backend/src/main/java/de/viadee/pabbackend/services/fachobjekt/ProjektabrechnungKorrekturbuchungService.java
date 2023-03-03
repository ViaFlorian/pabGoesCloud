package de.viadee.pabbackend.services.fachobjekt;

import de.viadee.pabbackend.entities.LeistungKumuliert;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.repositories.pabdb.ProjektabrechnungKorrekturbuchungRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjektabrechnungKorrekturbuchungService {

    private final ProjektabrechnungKorrekturbuchungRepository projektabrechnungKorrekturbuchungRepository;


    public ProjektabrechnungKorrekturbuchungService(
            final ProjektabrechnungKorrekturbuchungRepository projektabrechnungKorrekturbuchungRepository) {
        this.projektabrechnungKorrekturbuchungRepository = projektabrechnungKorrekturbuchungRepository;
    }

    public List<ProjektabrechnungKorrekturbuchung> ladeProjektabrechnungKorrekturbuchungenByProjektId(
            Long projektId) {
        List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchung = this.projektabrechnungKorrekturbuchungRepository.ladeProjektabrechnungKorrekturbuchungByProjektId(
                projektId);
        List<ProjektabrechnungKorrekturbuchung> buchung = this.projektabrechnungKorrekturbuchungRepository.ladeBuchungenByProjektId(
                projektId);
        projektabrechnungKorrekturbuchung.addAll(buchung);

        return projektabrechnungKorrekturbuchung;
    }

    public ProjektabrechnungKorrekturbuchung ladeProjektabrechnungKorrekturbuchungGegenbuchung(
            Long korrekturbuchungId,
            String gegenbuchungId) {
        Optional<ProjektabrechnungKorrekturbuchung> gegenbuchung = this.projektabrechnungKorrekturbuchungRepository.ladeProjektabrechnungKorrekturbuchungGegenbuchung(
                korrekturbuchungId, gegenbuchungId);
        return gegenbuchung.orElseThrow(RuntimeException::new);
    }

    public List<LeistungKumuliert> ladeAktuelleKorrekturenFuerProjektMonatUndJahr(Long projektId, Integer jahr, Integer monat) {
        return IterableUtils.toList(
                this.projektabrechnungKorrekturbuchungRepository.ladeAktuelleKorrekturenFuerProjektMonatUndJahr(
                        projektId, jahr, monat));
    }

    public void speichereProjektabrechnungKorrekturbuchungen(
            List<ProjektabrechnungKorrekturbuchung> projektabrechnungKorrekturbuchungen) {
        projektabrechnungKorrekturbuchungRepository.saveAll(projektabrechnungKorrekturbuchungen);
    }
}
