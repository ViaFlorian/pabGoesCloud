CREATE TABLE SonstigeProjektkosten(
	ID [int] IDENTITY(1,1) NOT NULL,
	Jahr [smallint] NOT NULL,
	Monat [smallint] NOT NULL,
	Kosten [decimal](18, 2) NOT NULL,
	MitarbeiterID [int] NULL,
	ProjektID [int] NOT NULL,
	Bemerkung [varchar](255) NULL,
CONSTRAINT [PK_SonstigeProjektkosten] PRIMARY KEY CLUSTERED
(
    [ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
);

ALTER TABLE SonstigeProjektkosten  WITH CHECK ADD  CONSTRAINT FK_Sonderkosten_Mitarbeiter FOREIGN KEY(MitarbeiterID)
REFERENCES Mitarbeiter ([ID])
;

ALTER TABLE SonstigeProjektkosten CHECK CONSTRAINT [FK_Sonderkosten_Mitarbeiter]
;

ALTER TABLE SonstigeProjektkosten  WITH CHECK ADD  CONSTRAINT [FK_Sonderkosten_Projekt] FOREIGN KEY(ProjektID)
REFERENCES Projekt ([ID])
;

ALTER TABLE SonstigeProjektkosten CHECK CONSTRAINT FK_Sonderkosten_Projekt
;
