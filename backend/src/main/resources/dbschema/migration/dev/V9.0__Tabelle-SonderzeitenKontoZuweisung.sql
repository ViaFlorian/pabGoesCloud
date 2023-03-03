CREATE TABLE [dbo].[SonderzeitenKontoZuweisung](
    [ID] [int] IDENTITY(1,1) NOT NULL,
	[TagDerWoche] [varchar](11) NOT NULL,
	[uhrzeitVon] [time] NOT NULL,
	[uhrzeitBis] [time] NOT NULL,
	[zuschlagViadee] [int] NOT NULL,
	[davonSteuerfrei] [int],
	[kontoSteuerfrei] [varchar](5),
	[davonSteuerpflichtig] [int],
	[kontoSteuerpflichtig] [varchar](5)
) ON [PRIMARY]