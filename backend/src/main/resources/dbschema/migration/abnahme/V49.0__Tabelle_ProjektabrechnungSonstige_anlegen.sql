CREATE TABLE ProjektabrechnungSonstige
(
    ID                  int IDENTITY(1,1) NOT NULL,
    ProjektabrechnungID int            NOT NULL,
    MitarbeiterID       int            NULL,
    AuslagenViadee      decimal(18, 2) NULL,
    PauschaleKosten     decimal(18, 2) NULL,
    PauschaleLeistungen decimal(18, 2) NULL,
    ZuletztGeaendertAm  datetime       NOT NULL,
    ZuletztGeaendertVon varchar(80)    NOT NULL,
    CONSTRAINT PK_ProjektabrechnungSontige PRIMARY KEY CLUSTERED
        (
         ID ASC
            )
);

ALTER TABLE [dbo].[ProjektabrechnungSonstige] WITH CHECK ADD CONSTRAINT [FK_ProjektabrechnungSonstige_Projektabrechnung] FOREIGN KEY ([ProjektabrechnungID])
    REFERENCES [dbo].[Projektabrechnung] ([ID]);