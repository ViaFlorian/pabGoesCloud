--
-- Die View stellt sämtliche Kosten und Leistungen eines Projektes unterteilt nach
-- Kostenart, Kosten/Leistungen, usw. dar
-- Beispiel:
-- PAB_Jahr...........: 2019
-- PAB_Monat..........: 7
-- PAB_Status.........: abgerechnet
-- PAB_Projektnummer..: 0877
-- MA_Kurzname........: EvS
-- PAB_IstKostenBool..: 1 (1=Kosten, 0=Leistung)
-- PAB_Kostenart......: Projektzeit
-- PAB_Stunden........: 10
-- PAB_BetragProStunde: 42,00
-- PAB_Betrag.........: 420,00
--
CREATE VIEW [dbo].[VW_PAB_KOSTEN_LEISTUNG]
AS
--
-- Kosten Projektzeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	, 'Projektzeit' As PAB_Kostenart
	,PZEIT.StundenANW AS PAB_Stunden
	,PZEIT.Kostensatz AS PAB_BetragProStunde
	,PZEIT.Kostensatz * PZEIT.StundenANW AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungProjektzeit AS PZEIT ON PZEIT.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PZEIT.MitarbeiterID
WHERE
	PZEIT.StundenANW <> 0
UNION ALL
--
-- Kosten Reisezeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	, 'Reisezeit' As PAB_Kostenart
	,PREISE.AngerechneteReisezeit AS PAB_Stunden
	,PREISE.Kostensatz AS PAB_BetragProStunde
	,PREISE.Kostensatz * PREISE.AngerechneteReisezeit AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungReise AS PREISE ON PREISE.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PREISE.MitarbeiterID
WHERE
    PREISE.AngerechneteReisezeit <> 0
UNION ALL
--
-- Kosten Reise(Belege+Abwesenheit)
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	, 'Reise' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_Kostensatz
	, coalesce(PREISE.BelegeLtArbeitsnachweisKosten,0) + coalesce(PREISE.BelegeViadeeKosten,0) +
	  coalesce(PREISE.SpesenKosten,0) + coalesce(PREISE.ZuschlaegeKosten,0) AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungReise AS PREISE ON PREISE.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PREISE.MitarbeiterID
WHERE
    (PREISE.BelegeLtArbeitsnachweisKosten + PREISE.BelegeViadeeKosten +
	 PREISE.SpesenKosten + PREISE.ZuschlaegeKosten) <> 0
UNION ALL
--
-- Kosten Rufbereitschaft
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	, 'Rufbereitschaft' As PAB_Kostenart
	,PABSON.RufbereitschaftKostenAnzahlStunden AS PAB_Stunden
	,PABSON.RufbereitschaftKostensatz AS PAB_BetragProStunde
	,PABSON.RufbereitschaftKostenAnzahlStunden * PABSON.RufbereitschaftKostensatz AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungSonderarbeit AS PABSON ON PABSON.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSON.MitarbeiterID
WHERE
    PABSON.RufbereitschaftKostenAnzahlStunden <> 0
UNION ALL
--
-- Kosten Sonderarbeitszeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	, 'Sonderarbeitszeit' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_BetragProStunde
	,(coalesce(PABSON.SonderarbeitAnzahlStunden50,0) * coalesce(PABSON.SonderarbeitKostensatz,0) * 0.5) +
	  (coalesce(PABSON.SonderarbeitAnzahlStunden100,0) * coalesce(PABSON.SonderarbeitKostensatz,0)) +
	  (coalesce(PABSON.SonderarbeitPauschale,0) ) AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungSonderarbeit AS PABSON ON PABSON.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSON.MitarbeiterID
WHERE
    (PABSON.SonderarbeitAnzahlStunden50 <> 0 OR PABSON.SonderarbeitAnzahlStunden100 <> 0 OR
	 PABSON.SonderarbeitPauschale <> 0)
--UNION ALL
--
-- Kosten Sonstige
--
-- am 21.08.2019 noch nicht existent
-- SELECT
--	 PAB.Jahr AS PAB_Jahr
--	,PAB.Monat AS PAB_Monat
--	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
--         WHEN PAB.StatusID = 20 THEN 'freigegeben'
--          WHEN PAB.StatusID = 30 THEN 'geaendert'
--          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
--         WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
--          ELSE null
--     END AS PAB_Status
--	,PRJ.Projektnummer AS PRJ_Projektnummer
--	,PRJ.Bezeichnung AS PRJ_Bezeichnung
--	,PRJ.Projekttyp AS PRJ_Projekttyp
--	,OE.Bezeichnung AS PRJ_OEBez
--	,KND.Kurzbezeichnung AS PRJ_KundeBez
--    ,MA.Kurzname AS MA_Kurzname
--	,MA.Nachname AS MA_Name
--	,MA.Vorname AS MA_Vorname
--	,MA.IstIntern AS MA_IstIntern
--	,MA.IstAktiv AS MA_IstAktiv
--	, CAST(1 AS bit) AS PAB_IstKostenBool
--	, 'Sonstige' As PAB_Kostenart
--	,NULL AS PAB_Stunden
--	,NULL AS PAB_BetragProStunde
--	,(PABSONST.AuslagenViadee + PABSONST.PauschaleKosten) AS PAB_Betrag
--FROM
--	Projektabrechnung AS PAB
--    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
--	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
--	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
--	INNER JOIN dbo.ProjektabrechnungSonstige AS PABSONST ON PABSONST.ProjektabrechnungID = PAB.ID
--   INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSONST.MitarbeiterID
--WHERE
--    PABSONST.AuslagenViadee <> 0 OR PABSONST.PauschaleKosten <> 0
--UNION ALL
--
-- Kosten Sonstige ohne Mitarbeiter
--
-- SELECT
--	 PAB.Jahr AS PAB_Jahr
--	,PAB.Monat AS PAB_Monat
--	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
--          WHEN PAB.StatusID = 20 THEN 'freigegeben'
--          WHEN PAB.StatusID = 30 THEN 'geaendert'
--          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
--         WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
--          ELSE null
--     END AS PAB_Status
--	,PRJ.Projektnummer AS PRJ_Projektnummer
--	,PRJ.Bezeichnung AS PRJ_Bezeichnung
--	,PRJ.Projekttyp AS PRJ_Projekttyp
--	,OE.Bezeichnung AS PRJ_OEBez
--	,KND.Kurzbezeichnung AS PRJ_KundeBez
--   ,NULL AS MA_Kurzname
--	,NULL AS MA_Name
--	,NULL AS MA_Vorname
--	,NULL AS MA_IstIntern
--	,NULL AS MA_IstAktiv
--	, CAST(1 AS bit) AS PAB_IstKostenBool
--	, 'Sonstige' As PAB_Kostenart
--	,NULL AS PAB_Stunden
--	,NULL AS PAB_BetragProStunde
--	,(PABSONST.AuslagenViadee + PABSONST.PauschaleKosten) AS PAB_Betrag
--FROM
--	Projektabrechnung AS PAB
--    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
--	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
--	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
--	INNER JOIN dbo.ProjektabrechnungSonstige AS PABSONST ON PABSONST.ProjektabrechnungID = PAB.ID
--WHERE
--    (PABSONST.AuslagenViadee <> 0 OR PABSONST.PauschaleKosten <> 0)
--	AND PABSONST.MitarbeiterID is NULL
UNION ALL
--
-- Kosten viadee Auslagen mit MA Zuordnung (nicht im ANW)
--
SELECT
	 SOPKO.Jahr AS PAB_Jahr
	,SOPKO.Monat AS PAB_Monat
	,'abgerechnet' AS PAB_STATUS
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	-- evtl. später zwischen Sonstige und Reise unterscheiden
	, 'Sonstige' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_BetragProStunde
	,SOPKO.KOSTEN AS PAB_Betrag
FROM
	SonstigeProjektkosten AS SOPKO
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = SOPKO.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = SOPKO.MitarbeiterID
WHERE
    SOPKO.KOSTEN <> 0
UNION ALL
--
-- Kosten viadee Auslagen ohne MA Zuordnung (nicht im ANW)
--
SELECT
	 SOPKO.Jahr AS PAB_Jahr
	,SOPKO.Monat AS PAB_Monat
	,'abgerechnet' AS PAB_STATUS
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,NULL AS MA_Kurzname
	,NULL AS MA_Name
	,NULL AS MA_Vorname
	,NULL AS MA_IstIntern
	,NULL AS MA_IstAktiv
	, CAST(1 AS bit) AS PAB_IstKostenBool
	-- evtl. später zwischen Sonstige und Reise unterscheiden
	, 'Sonstige' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_BetragProStunde
	,SOPKO.KOSTEN AS PAB_Betrag
FROM
	SonstigeProjektkosten AS SOPKO
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = SOPKO.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
WHERE
    SOPKO.MitarbeiterID is NULL
    AND SOPKO.KOSTEN <> 0
UNION ALL
--
-- Leistung Projektzeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(0 AS bit) AS PAB_IstKostenBool
	, 'Projektzeit' As PAB_Kostenart
	,PZEIT.StundenANW AS PAB_Stunden
	,PZEIT.Stundensatz AS PAB_BetragProStunde
	,PZEIT.Stundensatz * PZEIT.StundenANW AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungProjektzeit AS PZEIT ON PZEIT.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PZEIT.MitarbeiterID
WHERE
    (PZEIT.Stundensatz * PZEIT.StundenANW) <> 0
UNION ALL
--
-- Leistung Reisezeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(0 AS bit) AS PAB_IstKostenBool
	, 'Reisezeit' As PAB_Kostenart
	,PREISE.TatsaechlicheReisezeit AS PAB_Stunden
	,PREISE.Stundensatz AS PAB_BetragProStunde
	,PREISE.Stundensatz * PREISE.TatsaechlicheReisezeit AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungReise AS PREISE ON PREISE.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PREISE.MitarbeiterID
WHERE
    (PREISE.Stundensatz * PREISE.TatsaechlicheReisezeit) <> 0
UNION ALL
--
-- Leistung Reise(Belege+Abwesenheit)
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(0 AS bit) AS PAB_IstKostenBool
	, 'Reise' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_Kostensatz
	, coalesce(PREISE.BelegeLtArbeitsnachweisLeistung,0) + coalesce(PREISE.BelegeViadeeLeistung,0) +
	  coalesce(PREISE.SpesenLeistung,0) + coalesce(PREISE.ZuschlaegeLeistung,0) +
	  ( coalesce(PREISE.PauschaleProTag,0) * coalesce(PREISE.PauschaleAnzahl,0) ) AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungReise AS PREISE ON PREISE.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PREISE.MitarbeiterID
WHERE
    (PREISE.BelegeLtArbeitsnachweisLeistung + PREISE.BelegeViadeeLeistung +
         PREISE.SpesenLeistung + PREISE.ZuschlaegeLeistung +
    	(PREISE.PauschaleProTag * PREISE.PauschaleAnzahl) ) <> 0
UNION ALL
--
-- Leistung Rufbereitschaft
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(0 AS bit) AS PAB_IstKostenBool
	, 'Rufbereitschaft' As PAB_Kostenart
	,PABSON.RufbereitschaftLeistungAnzahlStunden AS PAB_Stunden
	,PABSON.RufbereitschaftStundensatz AS PAB_BetragProStunde
	,( coalesce(PABSON.RufbereitschaftLeistungAnzahlStunden,0) * coalesce(PABSON.RufbereitschaftStundensatz,0) ) +
	   coalesce(PABSON.RufbereitschaftPauschale,0) AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungSonderarbeit AS PABSON ON PABSON.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSON.MitarbeiterID
WHERE
    (PABSON.RufbereitschaftLeistungAnzahlStunden * PABSON.RufbereitschaftStundensatz +
	 PABSON.RufbereitschaftPauschale) <> 0
UNION ALL
--
-- Leistung Sonderarbeitszeit
--
SELECT
	 PAB.Jahr AS PAB_Jahr
	,PAB.Monat AS PAB_Monat
	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
          WHEN PAB.StatusID = 20 THEN 'freigegeben'
          WHEN PAB.StatusID = 30 THEN 'geaendert'
          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS PAB_Status
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	, CAST(0 AS bit) AS PAB_IstKostenBool
	, 'Sonderarbeitszeit' As PAB_Kostenart
	,NULL AS PAB_Stunden
	,NULL AS PAB_BetragProStunde
	,PABSON.SonderarbeitLeistungPauschale AS PAB_Betrag
FROM
	Projektabrechnung AS PAB
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
	INNER JOIN dbo.ProjektabrechnungSonderarbeit AS PABSON ON PABSON.ProjektabrechnungID = PAB.ID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSON.MitarbeiterID
WHERE
    PABSON.SonderarbeitLeistungPauschale <> 0
--UNION ALL
--
-- Leistung Sonstige
--
-- am 21.08.2019 noch nicht existent
--SELECT
--	 PAB.Jahr AS PAB_Jahr
--	,PAB.Monat AS PAB_Monat
--	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
--          WHEN PAB.StatusID = 20 THEN 'freigegeben'
--          WHEN PAB.StatusID = 30 THEN 'geaendert'
--          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
--          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
--          ELSE null
--     END AS PAB_Status
--	,PRJ.Projektnummer AS PRJ_Projektnummer
--	,PRJ.Bezeichnung AS PRJ_Bezeichnung
--	,PRJ.Projekttyp AS PRJ_Projekttyp
--	,OE.Bezeichnung AS PRJ_OEBez
--	,KND.Kurzbezeichnung AS PRJ_KundeBez
--   ,MA.Kurzname AS MA_Kurzname
--	,MA.Nachname AS MA_Name
--	,MA.Vorname AS MA_Vorname
--	,MA.IstIntern AS MA_IstIntern
--	,MA.IstAktiv AS MA_IstAktiv
--	, CAST(0 AS bit) AS PAB_IstKostenBool
--	, 'Sonstige' As PAB_Kostenart
--	,NULL AS PAB_Stunden
--	,NULL AS PAB_BetragProStunde
--	,PABSONST.PauschaleLeistungen AS PAB_Betrag
--FROM
--	Projektabrechnung AS PAB
--    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
--	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
--	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
--	INNER JOIN dbo.ProjektabrechnungSonstige AS PABSONST ON PABSONST.ProjektabrechnungID = PAB.ID
--   INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = PABSONST.MitarbeiterID
--WHERE
--    PABSONST.PauschaleLeistungen <> 0
--UNION ALL
--
-- Leistung Sonstige ohne Mitarbeiter
--
--SELECT
--	 PAB.Jahr AS PAB_Jahr
--	,PAB.Monat AS PAB_Monat
--	,CASE WHEN PAB.StatusID = 10 THEN 'erfasst'
--         WHEN PAB.StatusID = 20 THEN 'freigegeben'
--          WHEN PAB.StatusID = 30 THEN 'geaendert'
--          WHEN PAB.StatusID = 40 THEN 'abgerechnet'
--          WHEN PAB.StatusID = 50 THEN 'abgeschlossen'
--          ELSE null
--     END AS PAB_Status
--	,PRJ.Projektnummer AS PRJ_Projektnummer
--	,PRJ.Bezeichnung AS PRJ_Bezeichnung
--	,PRJ.Projekttyp AS PRJ_Projekttyp
--	,OE.Bezeichnung AS PRJ_OEBez
--	,KND.Kurzbezeichnung AS PRJ_KundeBez
--   ,NULL AS MA_Kurzname
--	,NULL AS MA_Name
--	,NULL AS MA_Vorname
--	,NULL AS MA_IstIntern
--	,NULL AS MA_IstAktiv
--	, CAST(0 AS bit) AS PAB_IstKostenBool
--	, 'Sonstige' As PAB_Kostenart
--	,NULL AS PAB_Stunden
--	,NULL AS PAB_BetragProStunde
--	,PABSONST.PauschaleLeistungen AS PAB_Betrag
--FROM
--	Projektabrechnung AS PAB
--    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PAB.ProjektID
--	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
--	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
--	INNER JOIN dbo.ProjektabrechnungSonstige AS PABSONST ON PABSONST.ProjektabrechnungID = PAB.ID
--WHERE
--    PABSONST.PauschaleLeistungen <> 0 AND PABSONST.MitarbeiterID is NULL
--;