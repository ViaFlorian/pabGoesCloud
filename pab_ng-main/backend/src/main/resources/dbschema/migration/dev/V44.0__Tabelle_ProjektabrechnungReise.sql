CREATE TABLE ProjektabrechnungReise (
	ID int IDENTITY(1,1) NOT NULL,
	ProjektabrechnungID int NOT NULL,
	MitarbeiterID int NOT NULL,
	AngerechneteReisezeit decimal(18,2) NULL,
	Kostensatz decimal(18,2) NULL,
	BelegeLtArbeitsnachweisKosten decimal(18,2) NULL,
	BelegeViadeeKosten decimal(18,2) NULL,
	SpesenKosten decimal(18,2) NULL,
	ZuschlaegeKosten decimal(18,2) NULL,
	TatsaechlicheReisezeit decimal(18,2) NULL,
	TatsaechlicheReisezeitInformatorisch decimal(18,2) NULL,
	Stundensatz decimal(18,2) NULL,
	BelegeLtArbeitsnachweisLeistung decimal(18,2) NULL,
	BelegeViadeeLeistung decimal(18,2) NULL,
	SpesenLeistung decimal(18,2) NULL,
	ZuschlaegeLeistung decimal(18,2) NULL,
	PauschaleAnzahl decimal(18,2) NULL,
	PauschaleProTag decimal(18,2) NULL,
	ZuletztGeaendertAm datetime NOT NULL,
	ZuletztGeaendertVon varchar(80) NOT NULL,
 CONSTRAINT PK_ProjektabrechnungReise PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[ProjektabrechnungReise]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungReise_Projektabrechnung] FOREIGN KEY([ProjektabrechnungID])
REFERENCES [dbo].[Projektabrechnung] ([ID]);

ALTER TABLE [dbo].[ProjektabrechnungReise]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungReise_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])


