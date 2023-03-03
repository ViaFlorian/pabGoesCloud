CREATE TABLE ProjektabrechnungBerechneteLeistung (
	ID int IDENTITY(1,1) NOT NULL,
	ProjektabrechnungID int NOT NULL,
	MitarbeiterID int NULL,
	Leistung decimal(18,2) NULL,
	ZuletztGeaendertAm datetime NOT NULL,
    ZuletztGeaendertVon varchar(80) NOT NULL,
 CONSTRAINT PK_ProjektabrechnungBerechneteLeistung PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[ProjektabrechnungBerechneteLeistung]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungBerechneteLeistung_Projektabrechnung] FOREIGN KEY([ProjektabrechnungID])
REFERENCES [dbo].[Projektabrechnung] ([ID]);

ALTER TABLE [dbo].[ProjektabrechnungBerechneteLeistung]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungBerechneteLeistung_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])


