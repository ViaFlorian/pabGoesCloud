CREATE TABLE ProjektabrechnungSonderarbeit (
	ID int IDENTITY(1,1) NOT NULL,
	ProjektabrechnungID int NOT NULL,
	MitarbeiterID int NOT NULL,
	RufbereitschaftKostenAnzahlStunden decimal(18,2) NULL,
	RufbereitschaftKostensatz decimal(18,2) NULL,
	RufbereitschaftLeistungAnzahlStunden decimal(18,2) NULL,
	RufbereitschaftStundensatz decimal(18,2) NULL,
	RufbereitschaftPauschale decimal(18,2) NULL,
	SonderarbeitAnzahlStunden50 decimal(18,2) NULL,
	SonderarbeitAnzahlStunden100 decimal(18,2) NULL,
	SonderarbeitKostensatz decimal(18,2) NULL,
	SonderarbeitPauschale decimal(18,2) NULL,
	SonderarbeitLeistungPauschale decimal(18,2) NULL,
	ZuletztGeaendertAm datetime NOT NULL,
    ZuletztGeaendertVon varchar(80) NOT NULL,
 CONSTRAINT PK_ProjektabrechnungSonderarbeit PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[ProjektabrechnungSonderarbeit]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungSonderarbeit_Projektabrechnung] FOREIGN KEY([ProjektabrechnungID])
REFERENCES [dbo].[Projektabrechnung] ([ID]);

ALTER TABLE [dbo].[ProjektabrechnungSonderarbeit]  WITH CHECK ADD  CONSTRAINT [FK_ProjektabrechnungSonderarbeit_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])


