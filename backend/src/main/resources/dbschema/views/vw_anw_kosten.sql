CREATE VIEW
	[dbo].[VW_ANW_KOSTEN]
AS
SELECT
	 ANW.Jahr AS ANW_Jahr
	,ANW.Monat AS ANW_Monat
	,CASE WHEN ANW.StatusID = 10 THEN 'erfasst'
          WHEN ANW.StatusID = 20 THEN 'freigegeben'
          WHEN ANW.StatusID = 30 THEN 'geaendert'
          WHEN ANW.StatusID = 40 THEN 'abgerechnet'
          WHEN ANW.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS ANW_Status
    ,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
	,ABW.TagVon AS KST_Datum
	,ABW.UhrzeitVon AS ABW_UhrzeitVon
    ,ABW.UhrzeitBis AS ABW_UhrzeitBis
	,ABW.Spesen AS ABW_Spesen_Betrag
	,ABW.Zuschlag AS ABW_viadee_Zuschlag_Betrag
	,NULL AS BEL_Belegart
	,NULL AS BEL_Beleg_Betrag

FROM
	dbo.Abwesenheit AS ABW
    INNER JOIN dbo.Arbeitsnachweis AS ANW ON ANW.ID = ABW.ArbeitsnachweisID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = ANW.MitarbeiterID
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = ABW.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID
UNION ALL
SELECT
	 ANW.Jahr AS ANW_Jahr
	,ANW.Monat AS ANW_Monat
	,CASE WHEN ANW.StatusID = 10 THEN 'erfasst'
          WHEN ANW.StatusID = 20 THEN 'freigegeben'
          WHEN ANW.StatusID = 30 THEN 'geaendert'
          WHEN ANW.StatusID = 40 THEN 'abgerechnet'
          WHEN ANW.StatusID = 50 THEN 'abgeschlossen'
          ELSE null
     END AS ANW_Status
	,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
	,PRJ.Projektnummer AS PRJ_Projektnummer
	,PRJ.Bezeichnung AS PRJ_Bezeichnung
	,PRJ.Projekttyp AS PRJ_Projekttyp
	,OE.Bezeichnung AS PRJ_OEBez
	,KND.Kurzbezeichnung AS PRJ_KundeBez
	,BEL.Datum AS KST_Datum
	,NULL AS ABW_UhrzeitVon
    ,NULL AS ABW_UhrzeitBis
	,NULL AS ABW_Spesen_Betrag
	,NULL AS ABW_viadee_Zuschlag_Betrag
	,BELART.TextKurz AS BEL_Belegart
	,BEL.Betrag AS BEL_Beleg_Betrag

FROM
	dbo.Beleg AS BEL
	INNER JOIN dbo.C_BelegArt AS BELART ON BELART.ID = BEL.BelegArtID
    INNER JOIN dbo.Arbeitsnachweis AS ANW ON ANW.ID = BEL.ArbeitsnachweisID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = ANW.MitarbeiterID
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = BEL.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID