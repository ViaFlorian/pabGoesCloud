CREATE TABLE Jahresplanung (
	ID [int] IDENTITY(1,1) NOT NULL,
	Jahr [smallint] NOT NULL,
	OrganisationseinheitScribeID [varchar](38) NOT NULL,
	KundeScribeID [varchar](38) NULL,
	ProjektID [int] NOT NULL,
	Bemerkung [varchar](255) NULL,
	Kosten [decimal](18,2) NULL,
	Leistung [decimal](18,2) NULL,
	Stunden [decimal](18,2) NULL,
	ZuletztGeaendertAm [datetime] NOT NULL,
	ZuletztGeaendertVon [varchar](80) NOT NULL,
 CONSTRAINT PK_Jahresplanung PRIMARY KEY CLUSTERED
(
	ID ASC
)
);

ALTER TABLE [dbo].[Jahresplanung]  WITH CHECK ADD  CONSTRAINT [FK_Jahresplanung_Organisationseinheit] FOREIGN KEY([OrganisationseinheitScribeID])
REFERENCES [dbo].[Organisationseinheit] ([ScribeID]);

ALTER TABLE [dbo].[Jahresplanung]  WITH CHECK ADD  CONSTRAINT [FK_Jahresplanung_ProjektID] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID]);

ALTER TABLE [dbo].[Jahresplanung]  WITH CHECK ADD  CONSTRAINT [FK_Jahresplanung_Kunde] FOREIGN KEY([KundeScribeID])
REFERENCES [dbo].[Kunde] ([ScribeID]);

