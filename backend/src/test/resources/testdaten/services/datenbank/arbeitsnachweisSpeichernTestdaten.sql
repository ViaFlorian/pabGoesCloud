INSERT INTO Organisationseinheit (Bezeichnung, ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID,
                                  Beschreibung, VerantwortlicherMitarbeiterID)
VALUES ('viadee CRM', '2011-10-28 10:19:21.000', null, '{7F2DE681-3D01-E111-A51F-00505685254F}',
        null, null);

INSERT INTO Parameter ([Key], Value, Kommentar)
VALUES ('Zumutbare_Reisezeit', '1',
        'viadee-Regelung für die zumutbare Reisezeit unter 8 Projektstunde');
INSERT INTO Parameter ([Key], Value, Kommentar)
VALUES ('Zumutbare_Reisezeit_zwischen_8_und_9_Stunde', '1.50',
        'viadee-Regelung für die zumutbare Reisezeit zwischen 8 und 9 Projektstunde');
INSERT INTO Parameter ([Key], Value, Kommentar)
VALUES ('Zumutbare_Reisezeit_ueber_9_Stunde', '1.00',
        'viadee-Regelung für die zumutbare Reisezeit über 9 Projektstunde');


INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Normal', 'Normale Arbeitszeit', null, 1, 1);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Sonder', 'Sonderarbeitszeit', null, 0, 2);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Ruf', 'Rufbereitschaft', null, 0, 3);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('tat_Reise', 'Reisezeit (tatsächlich)', null, 0, 4);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('ang_Reise', 'Reisezeit (angerechnet)', null, null, 5);

INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('PKW', 'PKW', null, 1, 1);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Bah', 'Bah', null, 0, 2);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Flug', 'Flug', null, 0, 3);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('ÖPNV', 'ÖPNV', null, 0, 4);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Taxi', 'Taxi', null, 0, 5);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Hotel', 'Hotel', null, 0, 6);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Parke', 'Parke', null, 0, 7);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Verbindungsentgelt', 'Verbindungsentgelt', null, 0, 8);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Sonstiges', 'Sonstiges', null, 0, 9);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr)
VALUES ('Jobticket', 'Jobticket', null, 0, 10);


INSERT
INTO Kunde (Kurzbezeichnung,
            Bezeichnung,
            ScribeID,
            ZuletztGeaendertAm,
            ZuletztGeaendertVon)
VALUES ('Test',
        'Test',
        'TestScribeIDKunde1',
        GETDATE(),
        'TsT');

INSERT INTO Projekt (Projektnummer,
                     Bezeichnung,
                     Projekttyp,
                     Start,
                     Ende,
                     IstAktiv,
                     Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon,
                     ScribeID,
                     KundeID,
                     OrganisationseinheitID)
VALUES ('999999',
        'Test',
        'Dienstleistung',
        DATEADD(yy, -4, GETDATE()),
        DATEADD(yy, 4, GETDATE()),
        1,
        'Test',
        DATEADD(yy, -4, GETDATE()),
        'TsT',
        'TestScribeIDProjekt1',
        'TestScribeIDKunde1',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit));

INSERT INTO Projekt (Projektnummer,
                     Bezeichnung,
                     Projekttyp,
                     Start,
                     Ende,
                     IstAktiv,
                     Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon,
                     ScribeID,
                     KundeID,
                     OrganisationseinheitID)
VALUES ('9004',
        'Urlaub',
        'inter',
        null,
        null,
        1,
        'In Bearbeitung',
        DATEADD(yy, -4, GETDATE()),
        'TsT',
        'TestScribeIDProjekt4',
        'TestScribeIDKunde1',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit));

INSERT INTO Kunde (Kurzbezeichnung,
                   Bezeichnung,
                   ScribeID,
                   ZuletztGeaendertAm,
                   ZuletztGeaendertVon)
VALUES ('Test',
        'Test',
        'TestScribeIDKunde2',
        GETDATE(),
        'TsT');

INSERT INTO Projekt (Projektnummer,
                     Bezeichnung,
                     Projekttyp,
                     START,
                     Ende,
                     IstAktiv,
                     Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon,
                     ScribeID,
                     KundeID,
                     OrganisationseinheitID)
VALUES ('9999991',
        'Test',
        'Dienstleistung',
        DATEADD(yy, -4, GETDATE()),
        DATEADD(yy, 4, GETDATE()),
        1,
        'Test',
        DATEADD(yy, -4, GETDATE()),
        'TsT',
        'TestScribeIDProjekt2',
        'TestScribeIDKunde2',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit));

INSERT INTO Projekt (Projektnummer,
                     Bezeichnung,
                     Projekttyp,
                     START,
                     Ende,
                     IstAktiv,
                     Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon,
                     ScribeID,
                     KundeID,
                     OrganisationseinheitID)
VALUES ('9999992',
        'Test',
        'Dienstleistung',
        DATEADD(yy, -4, GETDATE()),
        DATEADD(yy, 4, GETDATE()),
        1,
        'Test',
        DATEADD(yy, -4, GETDATE()),
        'TsT',
        'TestScribeIDProjekt3',
        'TestScribeIDKunde2',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit));


INSERT INTO Mitarbeiter (PersonalNr,
                         Anrede,
                         Nachname,
                         Vorname,
                         Kurzname,
                         Geschaeftsstelle,
                         eMail,
                         IstIntern,
                         IstAktiv,
                         ZuletztGeaendertAm,
                         ZuletztGeaendertVon,
                         SachbearbeiterID,
                         MitarbeiterTypID)
VALUES (9999,
        'Herr',
        'Test',
        'Test',
        'Tst',
        'Münster',
        'test@viadee.de',
        1,
        1,
        GETDATE(),
        'Tst',
        (SELECT TOP(1) ID FROM Mitarbeiter),
        1);

INSERT INTO MitarbeiterStellenfaktor(MitarbeiterID,
                                     Stellenfaktor,
                                     GueltigAb,
                                     GueltigBis)
VALUES ((SELECT ID FROM Mitarbeiter WHERE PersonalNr = '9999'),
        1.00,
        DATEFROMPARTS(2022, 12, 1),
        DATEFROMPARTS(2050, 8, 31));

INSERT INTO Arbeitsnachweis (Jahr,
                             Monat,
                             ImportANW,
                             Auszahlung,
                             SmartphoneEigen,
                             Stellenfaktor,
                             StatusID,
                             ZuletztGeaendertAm,
                             ZuletztGeaendertVon,
                             MitarbeiterID,
                             Uebertrag,
                             Sollstunden,
                             Firmenwagen)
VALUES (2022,
        12,
        null,
        0.00,
        0,
        1.000,
        10,
        GETDATE(),
        'TsT',
        (SELECT ID FROM Mitarbeiter where PersonalNr = '9999'),
        null,
        168.00,
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 2),
        null,
        null,
        null,
        7.75,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 3),
        null,
        null,
        null,
        8.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 4),
        null,
        null,
        null,
        8.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 5),
        null,
        null,
        null,
        8.50,
        null,
        null,
        null,
        '',
        '2018-09-07 10:30:19.747',
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 6),
        null,
        null,
        null,
        7.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 9),
        null,
        null,
        null,
        8.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 10),
        null,
        null,
        null,
        7.75,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 11),
        null,
        null,
        null,
        8.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 12),
        null,
        null,
        null,
        9.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);
INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 13),
        null,
        null,
        null,
        7.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 16),
        null,
        null,
        null,
        7.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 17),
        null,
        null,
        null,
        7.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 18),
        null,
        null,
        null,
        7.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 18),
        null,
        null,
        null,
        1.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999991'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 19),
        null,
        null,
        null,
        8.25,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 19),
        null,
        null,
        null,
        1.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999991'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 20),
        null,
        null,
        null,
        6.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 20),
        null,
        null,
        null,
        2.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999991'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 23),
        null,
        null,
        null,
        8.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 24),
        null,
        null,
        null,
        8.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 25),
        null,
        null,
        null,
        7.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 26),
        null,
        null,
        null,
        7.75,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 27),
        null,
        null,
        null,
        6.50,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 30),
        null,
        null,
        null,
        8.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999992'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 31),
        null,
        null,
        null,
        8.00,
        null,
        null,
        null,
        'autom. importiert',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999992'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Normal'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 6),
        TIMEFROMPARTS(0, 0, 0, 0, 0),
        null,
        TIMEFROMPARTS(5, 0, 0, 0, 0),
        5.00,
        null,
        null,
        null,
        null,
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 7),
        TIMEFROMPARTS(12, 0, 0, 0, 0),
        null,
        TIMEFROMPARTS(14, 0, 0, 0, 0),
        2.00,
        null,
        null,
        null,
        null,
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 8),
        TIMEFROMPARTS(10, 0, 0, 0, 0),
        null,
        TIMEFROMPARTS(14, 0, 0, 0, 0),
        4.00,
        null,
        null,
        null,
        null,
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 10),
        TIMEFROMPARTS(10, 0, 0, 0, 0),
        DATEFROMPARTS(2022, 12, 10),
        TIMEFROMPARTS(18, 0, 0, 0, 0),
        8.00,
        null,
        null,
        null,
        null,
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Ruf'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 11),
        TIMEFROMPARTS(12, 0, 0, 0, 0),
        DATEFROMPARTS(2022, 12, 13),
        TIMEFROMPARTS(14, 0, 0, 0, 0),
        50.00,
        null,
        null,
        null,
        null,
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '9999992'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Ruf'),
        null);

INSERT INTO Projektstunde (TagVon,
                           UhrzeitVon,
                           TagBis,
                           UhrzeitBis,
                           AnzahlStunden,
                           davonFakturierbar,
                           KostensatzIntern,
                           KostensatzExtern,
                           Bemerkung,
                           ZuletztGeaendertAm,
                           ZuletztGeaendertVon,
                           ArbeitsnachweisID,
                           ProjektID,
                           ProjektstundeTypID,
                           Fakturierfaehig)
VALUES (DATEFROMPARTS(2022, 12, 12),
        TIMEFROMPARTS(11, 0, 0, 0, 0),
        null,
        TIMEFROMPARTS(19, 0, 0, 0, 0),
        8.00,
        null,
        null,
        null,
        'test',
        GETDATE(),
        'StG',
        (SELECT ID
         FROM Arbeitsnachweis
         WHERE MONAT = 12
           AND JAHR = 2022
           AND MitarbeiterID = (SELECT ID FROM Mitarbeiter where PersonalNr = '9999')),
        (SELECT id FROM Projekt WHERE Projektnummer = '999999'),
        (SELECT ID FROM C_ProjektstundeTyp WHERE TextKurz = 'Sonder'),
        null);




