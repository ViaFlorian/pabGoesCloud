CREATE TABLE Skonto
(
    ID            [int] IDENTITY (1,1) NOT NULL,
    ProjektID     [int]                NOT NULL,
    Wertstellung  [date]               NOT NULL,
    ReferenzMonat [smallint]           NOT NULL,
    ReferenzJahr  [smallint]           NOT NULL,
    BetragNetto   [decimal](18,2)      NOT NULL,
    Umsatzsteuer  [decimal](18,2)      NOT NULL,
    Bemerkung     [varchar](255)       NULL,
    ZuletztGeaendertAm datetime        NOT NULL,
    ZuletztGeaendertVon varchar(80)    NOT NULL,
    CONSTRAINT [PK_Skonto] PRIMARY KEY CLUSTERED
        (
            [ID] ASC
        )
);

ALTER TABLE Skonto
    WITH CHECK ADD CONSTRAINT FK_Skonto_Projekt FOREIGN KEY ([ProjektID])
        REFERENCES Projekt ([ID]);

ALTER TABLE Skonto
    CHECK CONSTRAINT FK_Skonto_Projekt;