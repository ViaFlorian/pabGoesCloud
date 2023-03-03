CREATE TABLE [dbo].[MitarbeiterStundenKonto](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[MitarbeiterID] [int] NOT NULL,
	[Wertstellung] [date] NOT NULL,
	[Buchungsdatum] [datetime2] NOT NULL,
	[AnzahlStunden] [decimal](18,2) NOT NULL,
	[lfdSaldo] [decimal](18,2) NOT NULL,
	[BuchungstypStundenID] [int] NOT NULL,
	[Bemerkung] [varchar](255) NULL,
	[IstAutomatisch] [bit] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[IstEndgueltig] [bit] NOT NULL,
 CONSTRAINT [PK_MitarbeiterStundenkonto] PRIMARY KEY CLUSTERED
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
;

ALTER TABLE [dbo].[MitarbeiterStundenkonto]  WITH CHECK ADD  CONSTRAINT [FK_MitarbeiterStundenkonto_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
;

ALTER TABLE [dbo].[MitarbeiterStundenkonto]  WITH CHECK ADD  CONSTRAINT [FK_MitarbeiterStundenkonto_Buchungstyp] FOREIGN KEY([BuchungstypStundenID])
REFERENCES [dbo].[C_BuchungstypStunden] ([ID])
;