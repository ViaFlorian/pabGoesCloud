CREATE TABLE TempMitarbeiterStellenfaktor
(
    MitarbeiterID [int]                NOT NULL,
    Stellenfaktor [decimal](18, 3)     NOT NULL,
    GueltigAb     [datetime]           NOT NULL,
    GueltigBis    [datetime]           NULL
);

ALTER TABLE TempMitarbeiterStellenfaktor  WITH CHECK ADD  CONSTRAINT FK_TempMitarbeiterStellenfaktor_Mitarbeiter FOREIGN KEY([MitarbeiterID])
    REFERENCES Mitarbeiter ([ID]);

ALTER TABLE TempMitarbeiterStellenfaktor CHECK CONSTRAINT FK_TempMitarbeiterStellenfaktor_Mitarbeiter;


