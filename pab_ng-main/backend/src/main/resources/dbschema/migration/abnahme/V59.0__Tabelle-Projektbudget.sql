CREATE TABLE [dbo].[Projektbudget](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ProjektID] [int] NOT NULL,
	[Wertstellung] [date] NOT NULL,
	[Buchungsdatum] [datetime2] NOT NULL,
	[BudgetBetrag] [decimal](18,2) NOT NULL,
	[Bemerkung] [varchar](255) NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
    [ZuletztGeaendertAm] [datetime] NOT NULL
 CONSTRAINT [PK_Projektbudget] PRIMARY KEY CLUSTERED
(
	[ID] ASC
)
)
;

ALTER TABLE [dbo].[Projektbudget]  WITH CHECK ADD  CONSTRAINT [FK_Projektbudget_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])
;
