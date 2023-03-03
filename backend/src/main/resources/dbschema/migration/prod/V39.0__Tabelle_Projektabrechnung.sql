CREATE TABLE Projektabrechnung (
	ID int IDENTITY(1,1) NOT NULL,
	Jahr smallint NOT NULL,
	Monat smallint NOT NULL,
	ProjektID int NOT NULL,
	StatusID int NOT NULL,
	ZuletztGeaendertAm datetime NOT NULL,
	ZuletztGeaendertVon varchar(80) NOT NULL,
 CONSTRAINT PK_Projektabrechnung PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[Projektabrechnung]  WITH CHECK ADD  CONSTRAINT [FK_Projektabrechnung_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])

