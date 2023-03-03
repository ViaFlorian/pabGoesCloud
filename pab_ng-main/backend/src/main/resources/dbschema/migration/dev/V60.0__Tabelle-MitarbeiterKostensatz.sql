CREATE TABLE [dbo].[MitarbeiterKostensatz](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Bezeichnung] [varchar](80) NOT NULL,
	[Beschreibung] [varchar](255) NOT NULL,
	[Betrag] [decimal] (18,2) NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
    [ZuletztGeaendertAm] [datetime] NOT NULL
 CONSTRAINT [PK_MitarbeiterKostensatz] PRIMARY KEY CLUSTERED
(
	[ID] ASC
)
)
;
