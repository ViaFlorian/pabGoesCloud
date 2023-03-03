CREATE TABLE [dbo].[DreiMonatsRegel](
    [ID] [int] IDENTITY(1,1) NOT NULL,
	[MitarbeiterID] [int] NOT NULL,
	[KundeID] [varchar](38) NOT NULL,
	[Arbeitsstaette] [varchar](400) NOT NULL,
	[gueltigVon] [date] NOT NULL,
	[gueltigBis] [date] 
) ON [PRIMARY]