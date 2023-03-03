CREATE TABLE ProjektabrechnungProjektzeit (
	ID int IDENTITY(1,1) NOT NULL,
	ProjektabrechnungID int NOT NULL,
	laufendeNummer int NOT NULL,
	MitarbeiterID int NOT NULL,
	Stundensatz decimal(18,2) NOT NULL,
	StundenANW decimal(18,2) NOT NULL,
	Kostensatz decimal(18,2) NOT NULL,
	ZuletztGeaendertAm datetime NOT NULL,
	ZuletztGeaendertVon varchar(80) NOT NULL,
 CONSTRAINT PK_ProjektabrechnungProjektzeit PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[ProjektabrechnungProjektzeit]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungProjektzeit_Projektabrechnung] FOREIGN KEY([ProjektabrechnungID])
REFERENCES [dbo].[Projektabrechnung] ([ID]);

ALTER TABLE [dbo].[ProjektabrechnungProjektzeit]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungProjektzeit_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])


