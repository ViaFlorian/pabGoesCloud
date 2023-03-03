INSERT INTO Organisationseinheit (Bezeichnung, ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID,
                                  Beschreibung, VerantwortlicherMitarbeiterID)
VALUES ('viadee CRM', '2011-10-28 10:19:21.000', null, '{7F2DE681-3D01-E111-A51F-00505685254F}',
        null, null);

DELETE
FROM C_GesetzlicheSpesen;
DELETE
FROM C_ViadeeZuschlaege;
DELETE
FROM Parameter;

INSERT INTO C_GesetzlicheSpesen ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])
VALUES (CAST(0.00 AS Decimal(18, 2)), 1, CAST(8.00 AS Decimal(18, 2)), 1,
        CAST(0.00 AS Decimal(18, 2)));
INSERT INTO C_GesetzlicheSpesen ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])
VALUES (CAST(8.00 AS Decimal(18, 2)), 0, CAST(24.00 AS Decimal(18, 2)), 0,
        CAST(12.00 AS Decimal(18, 2)));
INSERT INTO C_GesetzlicheSpesen ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])
VALUES (CAST(24.00 AS Decimal(18, 2)), 1, CAST(24.00 AS Decimal(18, 2)), 1,
        CAST(24.00 AS Decimal(18, 2)));

INSERT
C_ViadeeZuschlaege ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])ValueS (CAST(0.00 AS Decimal(18, 2)), 1, CAST(6.00 AS Decimal(18, 2)), 0, CAST(0.00 AS Decimal(18, 2)))
INSERT C_ViadeeZuschlaege ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])ValueS (CAST(6.00 AS Decimal(18, 2)), 1, CAST(10.00 AS Decimal(18, 2)), 0, CAST(11.00 AS Decimal(18, 2)))
INSERT C_ViadeeZuschlaege ([StundenAbwesendVon], [vonInklusive], [StundenAbwesendBis], [bisInklusive], [Betrag])ValueS (CAST(10.00 AS Decimal(18, 2)), 1, CAST(24.00 AS Decimal(18, 2)), 1, CAST(26.00 AS Decimal(18, 2)))

INSERT INTO Parameter ([Key], [Value], [Kommentar])ValueS ('Projekt_Verbindungsentgelte', '99999', 'Projektnummer zur Speicherung von Verbindungsentgelten');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Kilometerpauschale', '0.3',
        'Gesetzliche Kilometerpauschale; als Dezimalzahl anzugeben, z.B. 0.3');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Zumutbare_Reisezeit_kleiner_8_Stunden', '2',
        'viadee-Regelung für die zumutbare Reisezeit unter 8 Projektstunden');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Hilfeseite', 'https://confluence.intern.viadee.de/display/PAB20/Changelog',
        'Link zur Hilfeseite des Systems');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('abzugFuerFruehstuckBeiUebernachtung', '4.80',
        'Betrag, der bei der gesetzl. Spesenberechnung für das Frühstück abgezogen wird');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('anreiseAbreisePauschale', '12.00', 'Pauschalbetrag für An-/Abreisetage');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('zuschlagFuerFruehstuckBeiUebernachtung', '4.80',
        'Betrag, der bei der Zuschlagsrechnung für das Frühstück addiert wird');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('uebernachtungPauschale', '30.00', 'Pauschalbetrag für Abwesenheit mit Übernachtung');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('bereitstellungEigenesSmartphone', '20.00',
        'Betrag, der für die Bereitstellung des eigenen Smartphones erstattet wird.');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('rufbereitschaftErstattung', '15.00',
        'Betrag, der für Rufbereitschaft pro Stunde erstattet wird.');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyAnwFreigabe', ' ', 'Email-Text für Email bei Freigabe von Arbeitsnachweisen');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectAnwFreigabe', 'Rückmeldung Arbeitsnachweis ',
        'Betreff für Email bei Arbeitsnachweis-Freigabe; Abrechnungsmonat wird automatisch angehängt');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB001', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B001');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB001', 'Jahresübersicht Stunden',
        'Email-Betreff für Versand von Bericht B001');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB002', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B002');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB002', 'Spesen, Reisekosten und Sonderzeiten',
        'Email-Betreff für Versand von Bericht B002');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB004', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B004');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB004', 'Arbeitsnachweis pro Mitarbeiter/in und Abrechnungsmonat',
        'Email-Betreff für Versand von Bericht B004');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB003', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B003');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB003', 'Projektabrechnungsblatt',
        'Email-Betreff für Versand von Bericht B003');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB006', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B006');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB006', 'Arbeitszeit, Stundenkonto pro Mitarbeiter/in und Jahr',
        'Email-Betreff für Versand von Bericht B006');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('abzugFuerMittagessen', '9.60',
        'Betrag, der bei der gesetzl. Spesenberechnung für das Mittagessen abgezogen wird');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('abzugFuerAbendessen', '9.60',
        'Betrag, der bei der gesetzl. Spesenberechnung für das Abendessen abgezogen wird');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyJahresuebersicht', ' ', 'Email-Text für Email bei Versand Jahresübersicht');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectJahresuebersicht', 'Jahresübersicht Urlaubs- und Stundenkonto ',
        'Betreff für Email bei Versand Jahresübersicht; Mitarbeiterkürzel wird automatisch angehängt');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Zumutbare_Reisezeit_zwischen_8_und_9_Stunden', '1.50',
        'viadee-Regelung für die zumutbare Reisezeit zwischen 8 und 9 Projektstunden');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Zumutbare_Reisezeit_ueber_9_Stunden', '1.00',
        'viadee-Regelung für die zumutbare Reisezeit über 9 Projektstunden');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('verbindungsentgeltGrenze', '30.00',
        'Grenze, bis zu der das Verbindungsentgelt erstattet wird');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('ArbeitsnachweisTemplateVersion', 'V20190315.0',
        'Versionsnummer des aktuellen Arbeitsnachweis-Template');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailBodyB007', 'Email automatisch durch PAB 2.0 erstellt',
        'Email-Text für Versand von Bericht B007');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('emailSubjectB007', 'Projekt, Kosten und Leistung',
        'Email-Betreff für Versand von Bericht B007');
INSERT INTO Parameter ([Key], [Value], [Kommentar])
VALUES ('Umsatzsteuerdefault', '19.0', 'Standardwert für Umsatzsteuer');

INSERT INTO Kunde (Kurzbezeichnung, Bezeichnung, ScribeID, ZuletztGeaendertAm, ZuletztGeaendertVon, Debitorennummer) VALUES ('viadee', 'viadee', '{33648DAC-64B4-E511-96E6-00505685254F}', '2020-02-26 16:04:27.000', 'Pierick, Manuela', '12345');

INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, Start, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID, SachbearbeiterID, Geschaeftsstelle,
                     VerantwortlicherMitarbeiterID)
VALUES ('9002', 'Ausbildung', 'intern', null, null, 1, 'In Bearbeitung', '2016-03-18 17:10:00.000',
        'Service, Scribe', '{D807A113-14ED-E511-80C4-005056A22B18}',
        '{33648DAC-64B4-E511-96E6-00505685254F}', (SELECT TOP(1) ScribeID FROM Organisationseinheit ), (SELECT TOP (1) ID FROM Mitarbeiter),
        'Münster', (SELECT TOP (1) ID FROM Mitarbeiter));
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, Start, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID, SachbearbeiterID, Geschaeftsstelle,
                     VerantwortlicherMitarbeiterID)
VALUES ('9155', 'viadee PAB 2.0 - Konzeption und Einführung', 'intern', null, null, 1,
        'In Bearbeitung', '2016-07-22 15:39:24.000', 'Everding, Hans-Juergen',
        '{4D7D05A4-1150-E611-80C6-81E956C174D6}', '{33648DAC-64B4-E511-96E6-00505685254F}',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit ), (SELECT TOP (1) ID FROM Mitarbeiter), 'Münster', (SELECT TOP (1) ID FROM Mitarbeiter));
INSERT INTO Projekt (Projektnummer, Bezeichnung, Projekttyp, Start, Ende, IstAktiv, Statuszusatz,
                     ZuletztGeaendertAm, ZuletztGeaendertVon, ScribeID, KundeID,
                     OrganisationseinheitID, SachbearbeiterID, Geschaeftsstelle,
                     VerantwortlicherMitarbeiterID)
VALUES ('1809', 'Kundenprojekt', 'Dienstleistung', null, null, 1,
        'In Bearbeitung', '2016-07-22 15:39:24.000', 'Everding, Hans-Juergen',
        '{4D7D05A4-1150-E611-80C6-81E977C174D6}', '{33648DAC-64B4-E511-96E6-00505685254F}',
        (SELECT TOP(1) ScribeID FROM Organisationseinheit ), (SELECT TOP (1) ID FROM Mitarbeiter), 'Münster', (SELECT TOP (1) ID FROM Mitarbeiter));


