CREATE TABLE [dbo].[C_GesetzlicheSpesen](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[StundenAbwesendVon] [decimal](18, 2) NOT NULL,
	[vonInklusive] [bit] NOT NULL,
	[StundenAbwesendBis] [decimal](18, 2) NOT NULL,
	[bisInklusive] [bit] NOT NULL,
	[Betrag] [decimal](18, 2) NOT NULL
) ON [PRIMARY]