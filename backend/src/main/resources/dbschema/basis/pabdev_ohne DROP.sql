USE [PABDEV]
GO
/****** Object:  Table [dbo].[Abwesenheit]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Abwesenheit](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Arbeitsstaette] [varchar](80) NOT NULL,
	[TagVon] [date] NOT NULL,
	[UhrzeitVon] [time](0) NOT NULL,
	[UhrzeitBis] [time](0) NOT NULL,
	[Spesen] [decimal](18, 2) NOT NULL,
	[Zuschlag] [decimal](18, 2) NOT NULL,
	[Bemerkung] [varchar](255) NULL,
	[FruehstueckGenommen] [bit] NULL,
	[Uebernachtet] [bit] NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL,
	[ProjektID] [int] NOT NULL,
 CONSTRAINT [PK_Abwesenheit] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Arbeitsnachweis]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Arbeitsnachweis](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Jahr] [smallint] NOT NULL,
	[Monat] [smallint] NOT NULL,
	[ImportANW] [varbinary](max) NULL,
	[Auszahlung] [decimal](18, 2) NULL,
	[SmartphoneEigen] [bit] NULL,
	[Stellenfaktor] [decimal](18, 3) NULL,
	[StatusID] [int] NOT NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[MitarbeiterID] [int] NOT NULL,
 CONSTRAINT [PK_Arbeitsnachweis] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Beleg]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Beleg](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Datum] [date] NOT NULL,
	[Arbeitsstaette] [varchar](80) NULL,
	[km] [decimal](18, 2) NULL,
	[Betrag] [decimal](18, 2) NOT NULL,
	[BelegNr] [varchar](255) NULL,
	[Bemerkung] [varchar](255) NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL,
	[ProjektID] [int] NOT NULL,
	[BelegArtID] [int] NOT NULL,
 CONSTRAINT [PK_Beleg] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[C_BelegArt]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[C_BelegArt](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[TextKurz] [varchar](20) NULL,
	[TextLang] [varchar](80) NULL,
	[Wert] [decimal](18, 2) NULL,
	[IstDefault] [bit] NULL,
	[SortierNr] [int] NULL,
 CONSTRAINT [PK_C_BelegArt] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[C_MitarbeiterTyp]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[C_MitarbeiterTyp](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[TextKurz] [varchar](20) NULL,
	[TextLang] [varchar](80) NULL,
	[Wert] [decimal](18, 2) NULL,
	[IstDefault] [bit] NULL,
	[SortierNr] [int] NULL,
 CONSTRAINT [PK_C_MitarbeiterTyp] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[C_ProjektstundeTyp]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[C_ProjektstundeTyp](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[TextKurz] [varchar](20) NULL,
	[TextLang] [varchar](80) NULL,
	[Wert] [decimal](18, 2) NULL,
	[IstDefault] [bit] NULL,
	[SortierNr] [int] NULL,
 CONSTRAINT [PK_C_ProjektstundeTyp] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Fehlerlog]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Fehlerlog](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Fehlerklasse] [varchar](15) NULL,
	[Blatt] [varchar](50) NOT NULL,
	[Feld] [varchar](50) NULL,
	[Fehlertext] [varchar](255) NOT NULL,
	[Anzahl] [decimal](18, 2) NULL,
	[DurchgefuehrtVon] [varchar](80) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Kalender]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Kalender](
	[Datum] [date] NOT NULL,
	[Jahr] [smallint] NOT NULL,
	[Monat] [smallint] NOT NULL,
	[Tag] [smallint] NOT NULL,
	[Wochentag] [smallint] NOT NULL,
	[IstFeiertag] [bit] NOT NULL,
 CONSTRAINT [PK_Datum] PRIMARY KEY CLUSTERED 
(
	[Datum] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Kunde]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Kunde](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Kurzbezeichnung] [varchar](128) NOT NULL,
	[Bezeichnung] [varchar](160) NOT NULL,
	[ScribeID] [varchar](38) NOT NULL,
	[ZuletztGeaendertAm] [datetime] NULL,
	[ZuletztGeaendertVon] [varchar](80) NULL,
 CONSTRAINT [PK_Kunde] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Kunde_ScribeID] UNIQUE NONCLUSTERED 
(
	[ScribeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Mitarbeiter]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Mitarbeiter](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[PersonalNr] [int] NOT NULL,
	[Anrede] [varchar](10) NOT NULL,
	[Titel] [varchar](30) NULL,
	[Nachname] [varchar](100) NULL,
	[Vorname] [varchar](50) NULL,
	[Kurzname] [varchar](10) NULL,
	[Geschaeftsstelle] [varchar](30) NULL,
	[Stellenfaktor] [decimal](18, 3) NULL,
	[eMail] [varchar](200) NULL,
	[IstIntern] [bit] NOT NULL,
	[IstAktiv] [bit] NOT NULL,
	[ZuletztGeaendertAm] [datetime] NULL,
	[ZuletztGeaendertVon] [varchar](80) NULL,
	[SachbearbeiterID] [int] NULL,
	[MitarbeiterTypID] [int] NULL,
 CONSTRAINT [PK_Mitarbeiter] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_PersonalNr] UNIQUE NONCLUSTERED 
(
	[PersonalNr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Organisationseinheit]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Organisationseinheit](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Bezeichnung] [varchar](160) NULL,
	[ZuletztGeaendertAm] [datetime] NULL,
	[ZuletztGeaendertVon] [varchar](80) NULL,
	[ScribeID] [varchar](38) NOT NULL,
 CONSTRAINT [PK_Organisationseinheit] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_OE_ScribeID] UNIQUE NONCLUSTERED 
(
	[ScribeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Parameter]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Parameter](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Key] [varchar](4000) NOT NULL,
	[Value] [varchar](4000) NOT NULL,
	[Kommentar] [varchar](4000) NOT NULL,
 CONSTRAINT [PK_Parameter] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Projekt]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Projekt](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Projektnummer] [varchar](10) NOT NULL,
	[Bezeichnung] [varchar](100) NOT NULL,
	[Projekttyp] [varchar](100) NOT NULL,
	[Start] [datetime] NULL,
	[Ende] [datetime] NULL,
	[IstAktiv] [bit] NOT NULL,
	[Statuszusatz] [varchar](150) NOT NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[ScribeID] [varchar](38) NOT NULL,
	[KundeID] [varchar](38) NOT NULL,
	[OrganisationseinheitID] [varchar](38) NOT NULL,
 CONSTRAINT [PK_Projekt] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Projekt_ScribeID] UNIQUE NONCLUSTERED 
(
	[ScribeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Projektnummer] UNIQUE NONCLUSTERED 
(
	[Projektnummer] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProjektBearbeitungsstatus]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProjektBearbeitungsstatus](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Jahr] [smallint] NOT NULL,
	[Monat] [smallint] NOT NULL,
	[ProjektID] [int] NOT NULL,
	[StatusID] [int] NOT NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
 CONSTRAINT [PK_Bearbeitungsstatus] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Projektstunde]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Projektstunde](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[TagVon] [date] NOT NULL,
	[UhrzeitVon] [time](0) NULL,
	[TagBis] [date] NULL,
	[UhrzeitBis] [time](0) NULL,
	[AnzahlStunden] [decimal](18, 2) NOT NULL,
	[davonFakturierbar] [decimal](18, 2) NULL,
	[KostensatzIntern] [decimal](18, 2) NULL,
	[KostensatzExtern] [decimal](18, 2) NULL,
	[Bemerkung] [varchar](255) NULL,
	[ZuletztGeaendertAm] [datetime] NOT NULL,
	[ZuletztGeaendertVon] [varchar](80) NOT NULL,
	[ArbeitsnachweisID] [int] NOT NULL,
	[ProjektID] [int] NOT NULL,
	[ProjektstundeTypID] [int] NOT NULL,
 CONSTRAINT [PK_Projektstunde] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Abwesenheit] ADD  CONSTRAINT [DF_Abwesenheit_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Arbeitsnachweis] ADD  CONSTRAINT [DF_Arbeitsnachweis_Auszahlung]  DEFAULT ((0)) FOR [Auszahlung]
GO
ALTER TABLE [dbo].[Arbeitsnachweis] ADD  CONSTRAINT [DF_Arbeitsnachweis_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Beleg] ADD  CONSTRAINT [DF_Beleg_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Kalender] ADD  CONSTRAINT [DF_Kalender_IstFeiertag]  DEFAULT ((0)) FOR [IstFeiertag]
GO
ALTER TABLE [dbo].[Kunde] ADD  CONSTRAINT [DF_Kunde_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Mitarbeiter] ADD  CONSTRAINT [DF_Mitarbeiter_IstIntern]  DEFAULT ((1)) FOR [IstIntern]
GO
ALTER TABLE [dbo].[Mitarbeiter] ADD  CONSTRAINT [DF_Mitarbeiter_IstAktiv]  DEFAULT ((1)) FOR [IstAktiv]
GO
ALTER TABLE [dbo].[Mitarbeiter] ADD  CONSTRAINT [DF_Mitarbeiter_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Organisationseinheit] ADD  CONSTRAINT [DF_Organisationseinheit_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Projekt] ADD  CONSTRAINT [DF_Projekt_IstAktiv]  DEFAULT ((1)) FOR [IstAktiv]
GO
ALTER TABLE [dbo].[Projekt] ADD  CONSTRAINT [DF_Projekt_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[ProjektBearbeitungsstatus] ADD  CONSTRAINT [DF_Bearbeitungsstatus_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Projektstunde] ADD  CONSTRAINT [DF_Projektstunde_ZuletztGeaendertAm]  DEFAULT (getdate()) FOR [ZuletztGeaendertAm]
GO
ALTER TABLE [dbo].[Abwesenheit]  WITH CHECK ADD  CONSTRAINT [FK_Abwesenheit_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID])
GO
ALTER TABLE [dbo].[Abwesenheit] CHECK CONSTRAINT [FK_Abwesenheit_Arbeitsnachweis]
GO
ALTER TABLE [dbo].[Abwesenheit]  WITH CHECK ADD  CONSTRAINT [FK_Abwesenheit_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])
GO
ALTER TABLE [dbo].[Abwesenheit] CHECK CONSTRAINT [FK_Abwesenheit_Projekt]
GO
ALTER TABLE [dbo].[Arbeitsnachweis]  WITH CHECK ADD  CONSTRAINT [FK_Arbeitsnachweis_Mitarbeiter] FOREIGN KEY([MitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
GO
ALTER TABLE [dbo].[Arbeitsnachweis] CHECK CONSTRAINT [FK_Arbeitsnachweis_Mitarbeiter]
GO
ALTER TABLE [dbo].[Beleg]  WITH CHECK ADD  CONSTRAINT [FK_Beleg_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID])
GO
ALTER TABLE [dbo].[Beleg] CHECK CONSTRAINT [FK_Beleg_Arbeitsnachweis]
GO
ALTER TABLE [dbo].[Beleg]  WITH CHECK ADD  CONSTRAINT [FK_Beleg_C_BelegArt] FOREIGN KEY([BelegArtID])
REFERENCES [dbo].[C_BelegArt] ([ID])
GO
ALTER TABLE [dbo].[Beleg] CHECK CONSTRAINT [FK_Beleg_C_BelegArt]
GO
ALTER TABLE [dbo].[Beleg]  WITH CHECK ADD  CONSTRAINT [FK_Beleg_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])
GO
ALTER TABLE [dbo].[Beleg] CHECK CONSTRAINT [FK_Beleg_Projekt]
GO
ALTER TABLE [dbo].[Fehlerlog]  WITH CHECK ADD  CONSTRAINT [FK_Fehlerlog_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID])
GO
ALTER TABLE [dbo].[Fehlerlog] CHECK CONSTRAINT [FK_Fehlerlog_Arbeitsnachweis]
GO
ALTER TABLE [dbo].[Mitarbeiter]  WITH CHECK ADD  CONSTRAINT [FK_Mitarbeiter_C_MitarbeiterTyp] FOREIGN KEY([MitarbeiterTypID])
REFERENCES [dbo].[C_MitarbeiterTyp] ([ID])
GO
ALTER TABLE [dbo].[Mitarbeiter] CHECK CONSTRAINT [FK_Mitarbeiter_C_MitarbeiterTyp]
GO
ALTER TABLE [dbo].[Mitarbeiter]  WITH CHECK ADD  CONSTRAINT [FK_Mitarbeiter_Mitarbeiter] FOREIGN KEY([SachbearbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
GO
ALTER TABLE [dbo].[Mitarbeiter] CHECK CONSTRAINT [FK_Mitarbeiter_Mitarbeiter]
GO
ALTER TABLE [dbo].[Projekt]  WITH CHECK ADD  CONSTRAINT [FK_Projekt_Kunde] FOREIGN KEY([KundeID])
REFERENCES [dbo].[Kunde] ([ScribeID])
GO
ALTER TABLE [dbo].[Projekt] CHECK CONSTRAINT [FK_Projekt_Kunde]
GO
ALTER TABLE [dbo].[Projekt]  WITH CHECK ADD  CONSTRAINT [FK_Projekt_Organisationseinheit] FOREIGN KEY([OrganisationseinheitID])
REFERENCES [dbo].[Organisationseinheit] ([ScribeID])
GO
ALTER TABLE [dbo].[Projekt] CHECK CONSTRAINT [FK_Projekt_Organisationseinheit]
GO
ALTER TABLE [dbo].[ProjektBearbeitungsstatus]  WITH CHECK ADD  CONSTRAINT [FK_Bearbeitungsstatus_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])
GO
ALTER TABLE [dbo].[ProjektBearbeitungsstatus] CHECK CONSTRAINT [FK_Bearbeitungsstatus_Projekt]
GO
ALTER TABLE [dbo].[Projektstunde]  WITH CHECK ADD  CONSTRAINT [FK_Projektstunde_Arbeitsnachweis] FOREIGN KEY([ArbeitsnachweisID])
REFERENCES [dbo].[Arbeitsnachweis] ([ID])
GO
ALTER TABLE [dbo].[Projektstunde] CHECK CONSTRAINT [FK_Projektstunde_Arbeitsnachweis]
GO
ALTER TABLE [dbo].[Projektstunde]  WITH CHECK ADD  CONSTRAINT [FK_Projektstunde_C_ProjektstundeTyp] FOREIGN KEY([ProjektstundeTypID])
REFERENCES [dbo].[C_ProjektstundeTyp] ([ID])
GO
ALTER TABLE [dbo].[Projektstunde] CHECK CONSTRAINT [FK_Projektstunde_C_ProjektstundeTyp]
GO
ALTER TABLE [dbo].[Projektstunde]  WITH CHECK ADD  CONSTRAINT [FK_Projektstunde_Projekt] FOREIGN KEY([ProjektID])
REFERENCES [dbo].[Projekt] ([ID])
GO
ALTER TABLE [dbo].[Projektstunde] CHECK CONSTRAINT [FK_Projektstunde_Projekt]
GO
ALTER TABLE [dbo].[Kalender]  WITH CHECK ADD  CONSTRAINT [CK_Monat] CHECK  (([Monat]<=(12)))
GO
ALTER TABLE [dbo].[Kalender] CHECK CONSTRAINT [CK_Monat]
GO
ALTER TABLE [dbo].[Kalender]  WITH CHECK ADD  CONSTRAINT [CK_Tag] CHECK  (([Tag]<=(31)))
GO
ALTER TABLE [dbo].[Kalender] CHECK CONSTRAINT [CK_Tag]
GO
/****** Object:  StoredProcedure [dbo].[deaktiviereAlteMitarbeiterdatensaetze]    Script Date: 29.05.2018 10:36:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[deaktiviereAlteMitarbeiterdatensaetze]
AS
BEGIN
	update Mitarbeiter
set IstAktiv = 0 
where ID in (
select alt.ID from Mitarbeiter neu LEFT JOIN
                   Mitarbeiter alt
				on alt.Vorname = neu.Vorname and
				   alt.Nachname = neu.Nachname and
				   alt.Geschaeftsstelle = neu.Geschaeftsstelle and
				   alt.PersonalNr <> neu.PersonalNr and
				   alt.Kurzname IS NULL and
				   neu.IstIntern = 1 and
				   alt.IstAktiv = 1
WHERE 
	alt.Nachname IS NOT NULL and
	alt.Vorname IS NOT NULL
)
    
END
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Status, ob einMitarbeiter ein eigenes oder von der Firma gestelltes Smartphone verwendet (0 = nein, 1 = ja) (nur für interne Mitarbeiter relevant)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Arbeitsnachweis', @level2type=N'COLUMN',@level2name=N'SmartphoneEigen'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'10=erfasst;20=freigegeben;30=geändert;40=abgerechnet;50=abgeschlossen' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Arbeitsnachweis', @level2type=N'COLUMN',@level2name=N'StatusID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Verweis auf den Kalender (es wird immer die ID verwendet, die auf den 1. eines jeden Monats verweist)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Beleg', @level2type=N'COLUMN',@level2name=N'Datum'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'ID, die den Wochentag kennzeichnet (1=Montag; 2=Dienstag; ....; 7=Sonntag)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Kalender', @level2type=N'COLUMN',@level2name=N'Wochentag'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Gibt an, ob es sich um einen Feiertag handelt (0 = kein Feiertag (default); 1 = Feiertag)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Kalender', @level2type=N'COLUMN',@level2name=N'IstFeiertag'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Kunde', @level2type=N'COLUMN',@level2name=N'ID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Zeigt an, ob es sich um einen internen Mitarbeiter handelt (0=Extern; 1=Intern (Default))' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Mitarbeiter', @level2type=N'COLUMN',@level2name=N'IstIntern'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Zeigt, ob ein Mitarbeiter für die viadee tätig ist (0=inaktiv;1=aktiv (default))' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Mitarbeiter', @level2type=N'COLUMN',@level2name=N'IstAktiv'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Zeigt, ob ein Projekt als aktiv gekennzeichnet ist (0=aktiv;1=inaktiv)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Projekt', @level2type=N'COLUMN',@level2name=N'IstAktiv'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'10=erfasst;20=freigegeben;30=geändert;40=abgerechnet;50=abgeschlossen' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ProjektBearbeitungsstatus', @level2type=N'COLUMN',@level2name=N'StatusID'
GO
