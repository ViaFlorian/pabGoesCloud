CREATE TABLE [dbo].[LohnartenberechnungLog](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL,
	[Konto] varchar(5) NOT NULL,
	[Datum] [date] NOT NULL,
	[Meldung] [varchar](500) NOT NULL,
	[Wert] [decimal](18, 2) NOT NULL,
	[Einheit] [varchar] (10) NOT NULL
) ON [PRIMARY];

ALTER TABLE [dbo].[LohnartenberechnungLog]  WITH CHECK ADD  CONSTRAINT [FK_LohnartenberechnungLog_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID]);