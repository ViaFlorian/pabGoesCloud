CREATE TABLE [dbo].[ArbeitsnachweisLohnartZuordnung](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL,
	[LohnartID] [int] NOT NULL,
	[Betrag] [decimal](18, 2) NOT NULL,
 CONSTRAINT [PK_ArbeitsnachweisLohnartZuordnung] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
;

ALTER TABLE [dbo].[ArbeitsnachweisLohnartZuordnung]  WITH CHECK ADD  CONSTRAINT [FK_ArbeitsnachweisLohnartZuordnung_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID])
;

ALTER TABLE [dbo].[ArbeitsnachweisLohnartZuordnung] CHECK CONSTRAINT [FK_ArbeitsnachweisLohnartZuordnung_Arbeitsnachweis]
;

ALTER TABLE [dbo].[ArbeitsnachweisLohnartZuordnung]  WITH CHECK ADD  CONSTRAINT [FK_ArbeitsnachweisLohnartZuordnung_C_Lohnart] FOREIGN KEY([LohnartID])
REFERENCES [dbo].[C_Lohnart] ([ID])
;

ALTER TABLE [dbo].[ArbeitsnachweisLohnartZuordnung] CHECK CONSTRAINT [FK_ArbeitsnachweisLohnartZuordnung_C_Lohnart]
;