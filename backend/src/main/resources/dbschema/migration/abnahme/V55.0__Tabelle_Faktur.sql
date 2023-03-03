CREATE TABLE Faktur (
    ID int IDENTITY(1,1) NOT NULL,
    ProjektID int NOT NULL,
    Rechnungsdatum datetime NOT NULL,
    BetragNetto decimal(18,2) NOT NULL,
    Rechnungsnummer varchar(80) NOT NULL,
    NichtBudgetrelevant decimal(18,2),
    AbweichenderRechnungsempfaengerKundeID varchar(38) NULL,
    Umsatzsteuer decimal(18,2) NULL,
    Bemerkung [varchar](255) NULL,
    ZuletztGeaendertAm datetime NOT NULL,
    ZuletztGeaendertVon varchar(80) NOT NULL,
    CONSTRAINT PK_Faktur PRIMARY KEY CLUSTERED
        (
            ID ASC
       )
);

ALTER TABLE [dbo].[Faktur]  WITH CHECK ADD  CONSTRAINT [FK_Faktur_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID]);

ALTER TABLE [dbo].[Faktur]  WITH CHECK ADD  CONSTRAINT [FK_Faktur_Abw_Rechnungsempfaenger] FOREIGN KEY([AbweichenderRechnungsempfaengerKundeID])
REFERENCES [dbo].[Kunde] ([ScribeID]);




