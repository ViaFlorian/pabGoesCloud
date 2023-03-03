package de.viadee.pabbackend.services.berechnung;

import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchung;
import de.viadee.pabbackend.entities.ProjektabrechnungKorrekturbuchungVorgang;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class KorrekturbuchungErstellenService {


  public ProjektabrechnungKorrekturbuchungVorgang projektabrechnungKorrekturbuchungenErstellen(
      ProjektabrechnungKorrekturbuchungVorgang eingabe) {
    String uuid;

    ProjektabrechnungKorrekturbuchung korrekturbuchungNeu = erstelleKorrekturbuchungAusEingabe(
        eingabe.getKorrekturbuchung());

    ProjektabrechnungKorrekturbuchung gegenbuchungNeu = null;
    if (eingabe.getGegenbuchung().getProjektId() != null) {
      gegenbuchungNeu = erstelleKorrekturbuchungAusEingabe(eingabe.getGegenbuchung());

      uuid = UUID.randomUUID().toString();
      korrekturbuchungNeu.setGegenbuchungID(uuid);
      gegenbuchungNeu.setGegenbuchungID(uuid);

      if (korrekturbuchungNeu.getAnzahlStundenKosten() != null
          && gegenbuchungNeu.getAnzahlStundenKosten() != null) {
        korrekturbuchungNeu.setStundendifferenzGegenbuchung(
            korrekturbuchungNeu.getAnzahlStundenKosten()
                .add(gegenbuchungNeu.getAnzahlStundenKosten()));
      }
    }

    return new ProjektabrechnungKorrekturbuchungVorgang(korrekturbuchungNeu, gegenbuchungNeu);
  }

  private ProjektabrechnungKorrekturbuchung erstelleKorrekturbuchungAusEingabe(
      ProjektabrechnungKorrekturbuchung eingabeKorrekturbuchung) {
    ProjektabrechnungKorrekturbuchung korrekturbuchungNeu = new ProjektabrechnungKorrekturbuchung();
    korrekturbuchungNeu.setIstKorrekturbuchung(true);

    korrekturbuchungNeu.setKostenartId(eingabeKorrekturbuchung.getKostenartId());
    korrekturbuchungNeu.setMitarbeiterId(eingabeKorrekturbuchung.getMitarbeiterId());
    korrekturbuchungNeu.setJahr(eingabeKorrekturbuchung.getJahr());
    korrekturbuchungNeu.setMonat(eingabeKorrekturbuchung.getMonat());
    korrekturbuchungNeu.setReferenzJahr(eingabeKorrekturbuchung.getReferenzJahr());
    korrekturbuchungNeu.setReferenzMonat(eingabeKorrekturbuchung.getReferenzMonat());
    korrekturbuchungNeu.setBemerkung(eingabeKorrekturbuchung.getBemerkung());
    korrekturbuchungNeu.setProjektId(eingabeKorrekturbuchung.getProjektId());
    korrekturbuchungNeu.setAnzahlStundenKosten(eingabeKorrekturbuchung.getAnzahlStundenKosten());

    korrekturbuchungNeu.setBetragKostensatz(eingabeKorrekturbuchung.getBetragKostensatz());
    if (eingabeKorrekturbuchung.getBetragStundensatz() != null) {
      korrekturbuchungNeu.setAnzahlStundenLeistung(
          eingabeKorrekturbuchung.getAnzahlStundenLeistung());
    }
    korrekturbuchungNeu.setBetragStundensatz(eingabeKorrekturbuchung.getBetragStundensatz());

    return korrekturbuchungNeu;
  }
}
