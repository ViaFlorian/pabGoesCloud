INSERT INTO Organisationseinheit (Bezeichnung, ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID,
                                  Beschreibung, VerantwortlicherMitarbeiterID)
VALUES ('viadee CRM', '2011-10-28 10:19:21.000', null, '{7F2DE681-3D01-E111-A51F-00505685254F}',
        null, null);

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