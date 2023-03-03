CREATE TABLE Vertrag(
	ID [int] IDENTITY(1,1) NOT NULL,
	Bezeichnung [varchar](255) NOT NULL,
	Stundensatz [decimal](18, 2) NULL,
	GueltigAb [datetime] NULL,
	GueltigBis [datetime] NULL,
	IstAktiv [bit] NOT NULL,
	ZuletztGeaendertVon [varchar](80) NOT NULL,
	ZuletztGeaendertAm [datetime] NOT NULL,
	MitarbeiterID [int] NULL,
	ProjektID [int] NOT NULL,
	ScribeID [varchar](38) NOT NULL,
 CONSTRAINT PK_Vertrag PRIMARY KEY CLUSTERED 
(
	ID ASC
),
 CONSTRAINT UQ_Vetrag_ScribeID UNIQUE NONCLUSTERED 
(
	ScribeID ASC
)
) ON [PRIMARY]
;

ALTER TABLE Vertrag ADD  CONSTRAINT DF_Vertrag_IstAktiv  DEFAULT ((1)) FOR [IstAktiv]
;

ALTER TABLE Vertrag ADD  CONSTRAINT DF_Vertrag_ZuletztGeaendertAm  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
;

ALTER TABLE Vertrag  WITH CHECK ADD  CONSTRAINT FK_Vertrag_Mitarbeiter FOREIGN KEY([MitarbeiterID])
REFERENCES Mitarbeiter ([ID])
;

ALTER TABLE Vertrag CHECK CONSTRAINT FK_Vertrag_Mitarbeiter
;

ALTER TABLE Vertrag  WITH CHECK ADD  CONSTRAINT FK_Vertrag_Projekt FOREIGN KEY([ProjektID])
REFERENCES Projekt ([ID])
;

ALTER TABLE Vertrag CHECK CONSTRAINT FK_Vertrag_Projekt
;

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Zeigt, ob ein Vertrag aktiv ist (0=inaktiv;1=aktiv (default))' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Vertrag', @level2type=N'COLUMN',@level2name=N'IstAktiv'
;

