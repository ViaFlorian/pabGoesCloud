CREATE TABLE [dbo].[AbgeschlosseneMonate](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Jahr] [int] NOT NULL,
	[Monat] [int] NOT NULL,
	[AbgeschlossenAm] [date] NOT NULL,
	[AbgeschlossenVon] [varchar](80) NOT NULL,
 CONSTRAINT [PK_AbgeschlosseneMonate] PRIMARY KEY CLUSTERED
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
;