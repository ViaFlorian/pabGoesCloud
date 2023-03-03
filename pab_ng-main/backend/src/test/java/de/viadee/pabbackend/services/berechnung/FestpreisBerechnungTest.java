package de.viadee.pabbackend.services.berechnung;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import de.viadee.pabbackend.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("development")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
class FestpreisBerechnungTest extends MsSqlTestContainersTest {

    @Autowired
    private FestpreisBerechnung festpreisBerechnung;

    @Test
    @Transactional("pabDbTransactionManager")
    @Rollback()
    void testFestpreisBerechnung() {

        Projektabrechnung projektabrechnung = new Projektabrechnung();
        projektabrechnung.setId(123L);

        Mitarbeiter evh = new Mitarbeiter();
        evh.setId(1L);
        Mitarbeiter hum = new Mitarbeiter();
        hum.setId(2L);
        Mitarbeiter stg = new Mitarbeiter();
        stg.setId(3L);
        Mitarbeiter hlm = new Mitarbeiter();
        hlm.setId(4L);

        ProjektabrechnungProjektzeit evhProjektzeit = new ProjektabrechnungProjektzeit();
        evhProjektzeit.setMitarbeiterId(evh.getId());
        evhProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("120.00"));
        evhProjektzeit.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeit = new ProjektabrechnungProjektzeit();
        humProjektzeit.setMitarbeiterId(hum.getId());
        humProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        humProjektzeit.setStundensatz(new BigDecimal("80.00"));

        ProjektabrechnungProjektzeit stgProjektzeit = new ProjektabrechnungProjektzeit();
        stgProjektzeit.setMitarbeiterId(stg.getId());
        stgProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("140.00"));
        stgProjektzeit.setStundensatz(new BigDecimal("90.00"));

        ProjektabrechnungProjektzeit hlmProjektzeit = new ProjektabrechnungProjektzeit();
        hlmProjektzeit.setMitarbeiterId(hlm.getId());
        hlmProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("120.00"));
        hlmProjektzeit.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> bisherigeProjektzeiten = new ArrayList<>();
        List<ProjektabrechnungProjektzeit> projektzeitenFuerErsteBerechnung = new ArrayList<>();
        projektzeitenFuerErsteBerechnung.add(evhProjektzeit);
        projektzeitenFuerErsteBerechnung.add(humProjektzeit);
        projektzeitenFuerErsteBerechnung.add(stgProjektzeit);
        projektzeitenFuerErsteBerechnung.add(hlmProjektzeit);

        BigDecimal budget = new BigDecimal("120000.00");
        BigDecimal ferstigstellungsgradErsterSchritt = new BigDecimal("35.00");

        List<ProjektabrechnungBerechneteLeistung> bisherBerechneteLeistung = new ArrayList<>();

        List<LeistungKumuliert> leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerErsteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        List<LeistungKumuliert> bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<ProjektabrechnungBerechneteLeistung> neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        ZERO,
                        budget,
                        ZERO,
                        ferstigstellungsgradErsterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);

        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        bisherigeProjektzeiten.addAll(projektzeitenFuerErsteBerechnung);

        ProjektabrechnungProjektzeit evhProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        evhProjektzeitZweiteBerechnung.setMitarbeiterId(evh.getId());
        evhProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("30.00"));
        evhProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        humProjektzeitZweiteBerechnung.setMitarbeiterId(hum.getId());
        humProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("70.00"));
        humProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("80.00"));

        ProjektabrechnungProjektzeit stgProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        stgProjektzeitZweiteBerechnung.setMitarbeiterId(stg.getId());
        stgProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        stgProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("90.00"));

        ProjektabrechnungProjektzeit hlmProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        hlmProjektzeitZweiteBerechnung.setMitarbeiterId(hlm.getId());
        hlmProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("100.00"));
        hlmProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> projektzeitenFuerZweiteBerechnung = new ArrayList<>();
        projektzeitenFuerZweiteBerechnung.add(evhProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(humProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(stgProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(hlmProjektzeitZweiteBerechnung);

        BigDecimal ferstigstellungsgradZweiterSchritt = new BigDecimal("50.00");

        leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerZweiteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        budget,
                        budget,
                        ferstigstellungsgradErsterSchritt,
                        ferstigstellungsgradZweiterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        bisherigeProjektzeiten.addAll(projektzeitenFuerZweiteBerechnung);

        ProjektabrechnungProjektzeit evhProjektzeitDritteBerechnung =
                new ProjektabrechnungProjektzeit();
        evhProjektzeitDritteBerechnung.setMitarbeiterId(evh.getId());
        evhProjektzeitDritteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("15.00"));
        evhProjektzeitDritteBerechnung.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeitDritteBerechnung =
                new ProjektabrechnungProjektzeit();
        humProjektzeitDritteBerechnung.setMitarbeiterId(hum.getId());
        humProjektzeitDritteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        humProjektzeitDritteBerechnung.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> projektzeitenFuerDritteBerechnung = new ArrayList<>();
        projektzeitenFuerDritteBerechnung.add(evhProjektzeitDritteBerechnung);
        projektzeitenFuerDritteBerechnung.add(humProjektzeitDritteBerechnung);

        BigDecimal ferstigstellungsgradDritterSchritt = new BigDecimal("40.00");

        leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerDritteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        budget,
                        budget,
                        ferstigstellungsgradZweiterSchritt,
                        ferstigstellungsgradDritterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        assertEquals(0, bisherBerechneteLeistung
                .get(0)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("12413.79")));
        assertEquals(0, bisherBerechneteLeistung
                .get(2)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("13034.49")));
        assertEquals(0, bisherBerechneteLeistung
                .get(5)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("4559.43")));
        assertEquals(0, bisherBerechneteLeistung
                .get(7)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("6466.49")));
        assertEquals(0, bisherBerechneteLeistung
                .get(8)
                .getLeistung()
                .setScale(2, bisherBerechneteLeistung.get(8).getLeistung().signum() < 0 ? UP : DOWN)
                .compareTo(new BigDecimal("-3020.80")));
        assertEquals(0, bisherBerechneteLeistung
                .get(9)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("1035.64")));
    }

    @Test
    @Transactional("pabDbTransactionManager")
    @Rollback()
    void testFestpreisBerechnungGesplittet() {

        Projektabrechnung projektabrechnung = new Projektabrechnung();
        projektabrechnung.setId(123L);

        Mitarbeiter evh = new Mitarbeiter();
        evh.setId(1L);
        Mitarbeiter hum = new Mitarbeiter();
        hum.setId(2L);
        Mitarbeiter stg = new Mitarbeiter();
        stg.setId(3L);
        Mitarbeiter hlm = new Mitarbeiter();
        hlm.setId(4L);

        ProjektabrechnungProjektzeit evhProjektzeit1 = new ProjektabrechnungProjektzeit();
        evhProjektzeit1.setMitarbeiterId(evh.getId());
        evhProjektzeit1.setLaufendeNummer(1);
        evhProjektzeit1.setStundenLautArbeitsnachweis(new BigDecimal("60.00"));
        evhProjektzeit1.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit evhProjektzeit2 = new ProjektabrechnungProjektzeit();
        evhProjektzeit2.setMitarbeiterId(evh.getId());
        evhProjektzeit2.setLaufendeNummer(2);
        evhProjektzeit2.setStundenLautArbeitsnachweis(new BigDecimal("60.00"));
        evhProjektzeit2.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeit = new ProjektabrechnungProjektzeit();
        humProjektzeit.setMitarbeiterId(hum.getId());
        humProjektzeit.setLaufendeNummer(1);
        humProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        humProjektzeit.setStundensatz(new BigDecimal("80.00"));

        ProjektabrechnungProjektzeit stgProjektzeit = new ProjektabrechnungProjektzeit();
        stgProjektzeit.setMitarbeiterId(stg.getId());
        stgProjektzeit.setLaufendeNummer(1);
        stgProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("140.00"));
        stgProjektzeit.setStundensatz(new BigDecimal("90.00"));

        ProjektabrechnungProjektzeit hlmProjektzeit = new ProjektabrechnungProjektzeit();
        hlmProjektzeit.setMitarbeiterId(hlm.getId());
        hlmProjektzeit.setLaufendeNummer(1);
        hlmProjektzeit.setStundenLautArbeitsnachweis(new BigDecimal("120.00"));
        hlmProjektzeit.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> bisherigeProjektzeiten = new ArrayList<>();
        List<ProjektabrechnungProjektzeit> projektzeitenFuerErsteBerechnung = new ArrayList<>();
        projektzeitenFuerErsteBerechnung.add(evhProjektzeit1);
        projektzeitenFuerErsteBerechnung.add(evhProjektzeit2);
        projektzeitenFuerErsteBerechnung.add(humProjektzeit);
        projektzeitenFuerErsteBerechnung.add(stgProjektzeit);
        projektzeitenFuerErsteBerechnung.add(hlmProjektzeit);

        BigDecimal budget = new BigDecimal("120000.00");
        BigDecimal ferstigstellungsgradErsterSchritt = new BigDecimal("35.00");

        List<LeistungKumuliert> leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerErsteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        List<LeistungKumuliert> bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<ProjektabrechnungBerechneteLeistung> bisherBerechneteLeistung = new ArrayList<>();
        List<ProjektabrechnungBerechneteLeistung> neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        budget,
                        budget,
                        ZERO,
                        ferstigstellungsgradErsterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        bisherigeProjektzeiten.addAll(projektzeitenFuerErsteBerechnung);

        ProjektabrechnungProjektzeit evhProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        evhProjektzeitZweiteBerechnung.setMitarbeiterId(evh.getId());
        evhProjektzeitZweiteBerechnung.setLaufendeNummer(1);
        evhProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("30.00"));
        evhProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        humProjektzeitZweiteBerechnung.setMitarbeiterId(hum.getId());
        humProjektzeitZweiteBerechnung.setLaufendeNummer(1);
        humProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("70.00"));
        humProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("80.00"));

        ProjektabrechnungProjektzeit stgProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        stgProjektzeitZweiteBerechnung.setMitarbeiterId(stg.getId());
        stgProjektzeitZweiteBerechnung.setLaufendeNummer(1);
        stgProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        stgProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("90.00"));

        ProjektabrechnungProjektzeit hlmProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        hlmProjektzeitZweiteBerechnung.setMitarbeiterId(hlm.getId());
        hlmProjektzeitZweiteBerechnung.setLaufendeNummer(1);
        hlmProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("100.00"));
        hlmProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> projektzeitenFuerZweiteBerechnung = new ArrayList<>();
        projektzeitenFuerZweiteBerechnung.add(evhProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(humProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(stgProjektzeitZweiteBerechnung);
        projektzeitenFuerZweiteBerechnung.add(hlmProjektzeitZweiteBerechnung);

        BigDecimal ferstigstellungsgradZweiterSchritt = new BigDecimal("50.00");

        leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerZweiteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        budget,
                        budget,
                        ferstigstellungsgradErsterSchritt,
                        ferstigstellungsgradZweiterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        bisherigeProjektzeiten.addAll(projektzeitenFuerZweiteBerechnung);

        ProjektabrechnungProjektzeit evhProjektzeitDritteBerechnung =
                new ProjektabrechnungProjektzeit();
        evhProjektzeitDritteBerechnung.setMitarbeiterId(evh.getId());
        evhProjektzeitDritteBerechnung.setLaufendeNummer(1);
        evhProjektzeitDritteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("15.00"));
        evhProjektzeitDritteBerechnung.setStundensatz(new BigDecimal("100.00"));

        ProjektabrechnungProjektzeit humProjektzeitDritteBerechnung =
                new ProjektabrechnungProjektzeit();
        humProjektzeitDritteBerechnung.setMitarbeiterId(hum.getId());
        humProjektzeitDritteBerechnung.setLaufendeNummer(1);
        humProjektzeitDritteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("80.00"));
        humProjektzeitDritteBerechnung.setStundensatz(new BigDecimal("80.00"));

        List<ProjektabrechnungProjektzeit> projektzeitenFuerDritteBerechnung = new ArrayList<>();
        projektzeitenFuerDritteBerechnung.add(evhProjektzeitDritteBerechnung);
        projektzeitenFuerDritteBerechnung.add(humProjektzeitDritteBerechnung);

        BigDecimal ferstigstellungsgradDritterSchritt = new BigDecimal("40.00");

        leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerDritteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnung,
                        budget,
                        budget,
                        ferstigstellungsgradZweiterSchritt,
                        ferstigstellungsgradDritterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        assertEquals(0, bisherBerechneteLeistung
                .get(0)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("12413.79")));
        assertEquals(0, bisherBerechneteLeistung
                .get(2)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("13034.49")));
        assertEquals(0, bisherBerechneteLeistung
                .get(5)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("4559.43")));
        assertEquals(0, bisherBerechneteLeistung
                .get(7)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("6466.49")));
        assertEquals(0, bisherBerechneteLeistung
                .get(8)
                .getLeistung()
                .setScale(2, bisherBerechneteLeistung.get(8).getLeistung().signum() < 0 ? UP : DOWN)
                .compareTo(new BigDecimal("-3020.80")));
        assertEquals(0, bisherBerechneteLeistung
                .get(9)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("1035.64")));
    }

    @Test
    @Transactional("pabDbTransactionManager")
    @Rollback()
    void testFestpreisBerechnungOhneUndMitMitarbeiter() {

        Projektabrechnung projektabrechnungErsterMonat = new Projektabrechnung();
        projektabrechnungErsterMonat.setJahr(2019);
        projektabrechnungErsterMonat.setMonat(1);
        projektabrechnungErsterMonat.setId(123L);

        List<ProjektabrechnungProjektzeit> bisherigeProjektzeiten = new ArrayList<>();
        List<ProjektabrechnungProjektzeit> projektzeitenFuerErsteBerechnung = new ArrayList<>();

        BigDecimal budget = new BigDecimal("120000.00");
        BigDecimal ferstigstellungsgradErsterSchritt = new BigDecimal("35.00");

        List<ProjektabrechnungBerechneteLeistung> bisherBerechneteLeistung = new ArrayList<>();

        List<LeistungKumuliert> leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerErsteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        List<LeistungKumuliert> bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<ProjektabrechnungBerechneteLeistung> neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnungErsterMonat,
                        ZERO,
                        budget,
                        ZERO,
                        ferstigstellungsgradErsterSchritt,
                        bisherigeLeistungKumuliert,
                        ZERO,
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        bisherigeProjektzeiten.addAll(projektzeitenFuerErsteBerechnung);

        Projektabrechnung projektabrechnungZweiterMonat = new Projektabrechnung();
        projektabrechnungZweiterMonat.setJahr(2019);
        projektabrechnungZweiterMonat.setMonat(2);

        projektabrechnungZweiterMonat.setId(123L);

        Mitarbeiter evh = new Mitarbeiter();
        evh.setId(1L);

        ProjektabrechnungProjektzeit evhProjektzeitZweiteBerechnung =
                new ProjektabrechnungProjektzeit();
        evhProjektzeitZweiteBerechnung.setMitarbeiterId(evh.getId());
        evhProjektzeitZweiteBerechnung.setProjektabrechnungId(projektabrechnungZweiterMonat.getId());
        evhProjektzeitZweiteBerechnung.setStundenLautArbeitsnachweis(new BigDecimal("30.00"));
        evhProjektzeitZweiteBerechnung.setStundensatz(new BigDecimal("100.00"));

        List<ProjektabrechnungProjektzeit> projektzeitenFuerZweiteBerechnung = new ArrayList<>();
        projektzeitenFuerZweiteBerechnung.add(evhProjektzeitZweiteBerechnung);

        BigDecimal ferstigstellungsgradZweiterSchritt = new BigDecimal("50.00");

        leistungKumuliertAktuell =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        projektzeitenFuerZweiteBerechnung,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
        bisherigeLeistungKumuliert =
                ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
                        bisherigeProjektzeiten, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        neueBerechneteLeistung =
                festpreisBerechnung.berechneFestpreis(
                        projektabrechnungZweiterMonat,
                        budget,
                        budget,
                        ferstigstellungsgradErsterSchritt,
                        ferstigstellungsgradZweiterSchritt,
                        bisherigeLeistungKumuliert,
                        new BigDecimal("42000.00"),
                        bisherBerechneteLeistung,
                        leistungKumuliertAktuell);
        bisherBerechneteLeistung.addAll(neueBerechneteLeistung);

        assertEquals(0, bisherBerechneteLeistung
                .get(0)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("42000.00")));
        assertEquals(1L, (long) bisherBerechneteLeistung.get(1).getMitarbeiterId());
        assertEquals(0, bisherBerechneteLeistung
                .get(1)
                .getLeistung()
                .setScale(2, DOWN)
                .compareTo(new BigDecimal("18000.00")));
    }

    private List<LeistungKumuliert> ueberfuehreAktuelleProjektabrechnungsdatenNachLeistungKumuliert(
            final List<ProjektabrechnungProjektzeit> alleProjektabrechnungProjektzeit,
            final List<ProjektabrechnungReise> alleProjektabrechnungReise,
            final List<ProjektabrechnungSonderarbeit> alleProjektabrechnungSonderarbeit,
            final List<ProjektabrechnungSonstige> alleProjektabrechnungSonstige) {

        List<LeistungKumuliert> alleLeistungenKumuliert = new ArrayList<>();
        Map<Long, BigDecimal> leistungProMitarbeiter = new HashMap<>();

        for (ProjektabrechnungProjektzeit projektzeit : alleProjektabrechnungProjektzeit) {
            if (leistungProMitarbeiter.get(projektzeit.getMitarbeiterId()) == null) {
                leistungProMitarbeiter.put(projektzeit.getMitarbeiterId(), projektzeit.getLeistung());
            } else {
                BigDecimal bisherigerLeistung = leistungProMitarbeiter.get(projektzeit.getMitarbeiterId());
                BigDecimal aktualisierteLeistung = bisherigerLeistung.add(projektzeit.getLeistung());
                leistungProMitarbeiter.put(projektzeit.getMitarbeiterId(), aktualisierteLeistung);
            }
        }

        for (ProjektabrechnungReise reise : alleProjektabrechnungReise) {
            if (leistungProMitarbeiter.get(reise.getMitarbeiterId()) == null) {
                leistungProMitarbeiter.put(reise.getMitarbeiterId(), reise.getReiseLeistung());
            } else {
                BigDecimal bisherigerLeistung = leistungProMitarbeiter.get(reise.getMitarbeiterId());
                BigDecimal aktualisierteLeistung = bisherigerLeistung.add(reise.getReiseLeistung());
                leistungProMitarbeiter.put(reise.getMitarbeiterId(), aktualisierteLeistung);
            }
        }

        for (ProjektabrechnungSonderarbeit sonderarbeit : alleProjektabrechnungSonderarbeit) {
            if (leistungProMitarbeiter.get(sonderarbeit.getMitarbeiterId()) == null) {
                leistungProMitarbeiter.put(
                        sonderarbeit.getMitarbeiterId(), sonderarbeit.getSonderzeitLeistung());
            } else {
                BigDecimal bisherigerLeistung = leistungProMitarbeiter.get(sonderarbeit.getMitarbeiterId());
                BigDecimal aktualisierteLeistung =
                        bisherigerLeistung.add(sonderarbeit.getSonderzeitLeistung());
                leistungProMitarbeiter.put(sonderarbeit.getMitarbeiterId(), aktualisierteLeistung);
            }
        }

        for (ProjektabrechnungSonstige sonstige : alleProjektabrechnungSonstige) {
            if (leistungProMitarbeiter.get(sonstige.getMitarbeiterId()) == null) {
                leistungProMitarbeiter.put(sonstige.getMitarbeiterId(), sonstige.getSonstigeLeistung());
            } else {
                BigDecimal bisherigerLeistung = leistungProMitarbeiter.get(sonstige.getMitarbeiterId());
                BigDecimal aktualisierteLeistung = bisherigerLeistung.add(sonstige.getSonstigeLeistung());
                leistungProMitarbeiter.put(sonstige.getMitarbeiterId(), aktualisierteLeistung);
            }
        }

        for (Long mitarbeiterId : leistungProMitarbeiter.keySet()) {
            LeistungKumuliert leistungKumuliert =
                    new LeistungKumuliert(mitarbeiterId, leistungProMitarbeiter.get(mitarbeiterId));
            alleLeistungenKumuliert.add(leistungKumuliert);
        }

        return alleLeistungenKumuliert;
    }
}
