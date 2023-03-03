CREATE TABLE MitarbeiterStellenfaktor
(
    ID            [int] IDENTITY (1,1) NOT NULL,
    MitarbeiterID [int]                NOT NULL,
    Stellenfaktor [decimal](18, 3)     NOT NULL,
    GueltigAb     [datetime]           NOT NULL,
    GueltigBis    [datetime]           NULL,
    CONSTRAINT [PK_Stellenfaktor] PRIMARY KEY CLUSTERED
        (
         [ID] ASC
            )
);

ALTER TABLE MitarbeiterStellenfaktor
    WITH CHECK ADD CONSTRAINT FK_MitarbeiterStellenfaktor_Mitarbeiter FOREIGN KEY ([MitarbeiterID])
        REFERENCES Mitarbeiter ([ID]);

ALTER TABLE MitarbeiterStellenfaktor
    CHECK CONSTRAINT FK_MitarbeiterStellenfaktor_Mitarbeiter;