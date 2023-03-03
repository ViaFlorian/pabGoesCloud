INSERT INTO Mitarbeiter (PersonalNr, Anrede, Nachname, Vorname, Kurzname, Geschaeftsstelle, eMail,
                         IstIntern, IstAktiv,
                         ZuletztGeaendertAm, ZuletztGeaendertVon,
                         MitarbeiterTypID)
VALUES (9999, 'Herr', 'Test', 'Test', 'Tst', 'Münster', 'test@viadee.de', 1, 1, GETDATE(), 'Tst',
        1);

INSERT INTO Organisationseinheit (Bezeichnung, ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID,
                                  Beschreibung, VerantwortlicherMitarbeiterID)
VALUES ('viadee CRM', '2011-10-28 10:19:21.000', null, '{7F2DE681-3D01-E111-A51F-00505685254F}',
        null, null);


INSERT INTO Kunde (Kurzbezeichnung, Bezeichnung, ScribeID, ZuletztGeaendertAm, ZuletztGeaendertVon)
VALUES ('Test', 'Test', 'TestScribeIDKunde1', GETDATE(), 'TsT');
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon, ScribeID, KundeID, OrganisationseinheitID)
VALUES ('999999', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()),
        1, 'Test',
        DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt1', 'TestScribeIDKunde1',
        (SELECT TOP (1) ScribeID FROM Organisationseinheit));
INSERT INTO Kunde (Kurzbezeichnung, Bezeichnung, ScribeID, ZuletztGeaendertAm, ZuletztGeaendertVon)
VALUES ('Test', 'Test', 'TestScribeIDKunde2', GETDATE(), 'TsT');
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon, ScribeID, KundeID, OrganisationseinheitID)
VALUES ('9999991', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()),
        1, 'Test',
        DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt2', 'TestScribeIDKunde2',
        (SELECT TOP (1) ScribeID FROM Organisationseinheit));
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, START, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm,
                     ZuletztGeaendertVon, ScribeID, KundeID, OrganisationseinheitID)
VALUES ('9999992', 'Test', 'Dienstleistung', DATEADD(yy, -4, GETDATE()), DATEADD(yy, 4, GETDATE()),
        1, 'Test',
        DATEADD(yy, -4, GETDATE()), 'TsT', 'TestScribeIDProjekt3', 'TestScribeIDKunde2',
        (SELECT TOP (1) ScribeID FROM Organisationseinheit));

