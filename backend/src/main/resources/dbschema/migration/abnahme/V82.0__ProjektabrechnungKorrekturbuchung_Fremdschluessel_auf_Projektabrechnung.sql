ALTER TABLE ProjektabrechnungKorrekturbuchung
    ADD ProjektabrechnungID int NULL;

ALTER TABLE ProjektabrechnungKorrekturbuchung
    ADD CONSTRAINT FK_ProjektabrechnungKorrekturbuchung_Projektabrechnung
        FOREIGN KEY (ProjektabrechnungID) REFERENCES Projektabrechnung (ID);