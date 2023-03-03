DELETE
FROM Kalender
WHERE DATUM IN (DATEFROMPARTS(2050,1,1), DATEFROMPARTS(2018,12,24), DATEFROMPARTS(2019,12,24), DATEFROMPARTS(2022,12,24), DATEFROMPARTS(2023,12,24), DATEFROMPARTS(2018,12,31), DATEFROMPARTS(2019,12,31), DATEFROMPARTS(2022,12,31), DATEFROMPARTS(2023,12,31));

INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2050,1,1), 2050, 1, 1, 6, 1);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2018,12,24), 2018, 12, 24, 2, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2019,12,24), 2019, 12, 31, 2, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2022,12,24), 2022, 12, 24, 6, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2023,12,24), 2023, 12, 24, 7, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2018,12,31), 2018, 12, 31, 2, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2019,12,31), 2019, 12, 31, 2, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2022,12,31), 2022, 12, 31, 6, 0);
INSERT INTO Kalender (Datum, Jahr, Monat, Tag, Wochentag, IstFeiertag)
VALUES (DATEFROMPARTS(2023,12,31), 2023, 12, 31, 7, 0);


INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Normal','Normale Arbeitszeit', null, 1, 1);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Sonder','Sonderarbeitszeit', null, 0, 2);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Ruf','Rufbereitschaft', null, 0, 3);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('tat_Reise','Reisezeit (tatsächlich)', null, 0, 4);
INSERT INTO C_ProjektstundeTyp (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('ang_Reise','Reisezeit (angerechnet)', null, null, 5);

INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('PKW', 'PKW', null, 1, 1);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Bahn', 'Bahn', null, 0, 2);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Flug', 'Flug', null, 0, 3);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('ÖPNV', 'ÖPNV', null, 0, 4);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Taxi', 'Taxi', null, 0, 5);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Hotel', 'Hotel', null, 0, 6);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Parken', 'Parken', null, 0, 7);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Verbindungsentgelt', 'Verbindungsentgelt', null, 0, 8);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Sonstiges', 'Sonstiges', null, 0, 9);
INSERT INTO C_BelegArt (TextKurz, TextLang, Wert, IstDefault, SortierNr) VALUES ('Jobticket', 'Jobticket', null, 0, 10);

DELETE
FROM Parameter
WHERE [Key] = 'bereitstellungEigenesSmartphone';

DELETE
FROM Parameter
WHERE [Key] = 'rufbereitschaftErstattung';

INSERT INTO Parameter ([Key], [Value], Kommentar) VALUES ('bereitstellungEigenesSmartphone', '20.00', '');
INSERT INTO Parameter ([Key], [Value], Kommentar) VALUES ('rufbereitschaftErstattung', '15.00', '');

DELETE
FROM SonderzeitenKontoZuweisung;

INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('1', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('1', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('2', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('2', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('3', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('3', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('4', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('4', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('5', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('5', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('6', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(5, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('6', TIMEFROMPARTS(20, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 50, 25, '014', 25,
        '016');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerpflichtig, kontoSteuerpflichtig)
VALUES ('6', TIMEFROMPARTS(6, 0, 0, 0, 0), TIMEFROMPARTS(19, 59, 59, 0, 0), 50, 50, '009');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei, davonSteuerpflichtig,
                                       kontoSteuerpflichtig)
VALUES ('7', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 100, 50, '008', 50,
        '009');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei)
VALUES ('FEIERTAG', TIMEFROMPARTS(0, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 100, 100, '107');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei)
VALUES ('HEILIGABEND', TIMEFROMPARTS(14, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 100, 100,
        '107');
INSERT INTO SonderzeitenKontoZuweisung(TagDerWoche, uhrzeitVon, uhrzeitBis, zuschlagViadee,
                                       davonSteuerfrei, kontoSteuerfrei)
VALUES ('SILVESTER', TIMEFROMPARTS(14, 0, 0, 0, 0), TIMEFROMPARTS(23, 59, 59, 0, 0), 100, 100,
        '107');

INSERT INTO Mitarbeiter (PersonalNr, Anrede, Nachname, Vorname, Kurzname, Geschaeftsstelle, eMail,
                         IstIntern, IstAktiv, ZuletztGeaendertAm, ZuletztGeaendertVon,
                         SachbearbeiterID, MitarbeiterTypID)
VALUES (9999, 'Herr', 'Test', 'Test', 'Tst', 'Münster', 'test@viadee.de', 1, 1, GETDATE(), 'Tst',
        null, 1);

INSERT INTO MitarbeiterStellenfaktor (MitarbeiterID, Stellenfaktor, GueltigAb, GueltigBis) VALUES ((SELECT TOP 1 ID FROM Mitarbeiter), 1.000, '2018-01-01 00:00:00.000', null);


INSERT INTO Organisationseinheit (Bezeichnung, ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID,
                                  Beschreibung, VerantwortlicherMitarbeiterID)
VALUES ('viadee CRM', '2011-10-28 10:19:21.000', null, '{7F2DE681-3D01-E111-A51F-00505685254F}',
        null, null);


    INSERT
INTO Kunde (Kurzbezeichnung, Bezeichnung, ScribeID, ZuletztGeaendertAm, ZuletztGeaendertVon)
VALUES ( 'Test', 'Test', 'TestScribeIDKunde1', GETDATE(), 'TsT' );
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID)
VALUES ( '999999', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()), 1, 'Test', DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt1', 'TestScribeIDKunde1', (SELECT TOP(1) ScribeID FROM Organisationseinheit ) );
INSERT INTO Kunde (Kurzbezeichnung, Bezeichnung, ScribeID, ZuletztGeaendertAm, ZuletztGeaendertVon)
VALUES ( 'Test', 'Test', 'TestScribeIDKunde2', GETDATE(), 'TsT' );
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID)
VALUES ( '9999991', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()), 1, 'Test', DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt2', 'TestScribeIDKunde2', (SELECT TOP(1) ScribeID FROM Organisationseinheit ) );
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID)
VALUES ( '9999992', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()), 1, 'Test', DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt3', 'TestScribeIDKunde2', (SELECT TOP(1) ScribeID FROM Organisationseinheit ) );



