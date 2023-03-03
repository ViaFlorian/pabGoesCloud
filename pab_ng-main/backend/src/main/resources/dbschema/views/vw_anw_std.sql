CREATE VIEW
	[dbo].[VW_ANW_STD]
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
    ,ANW.StatusID AS ANW_StatusID
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
	,PSTD.TagVon AS PSTD_TagVon
	,PSTD.UhrzeitVon AS PSTD_UhrzeitVon
    ,PSTD.TagBis AS PSTD_TagBis
    ,PSTD.UhrzeitBis AS PSTD_UhrzeitBis
	,COALESCE(CASE WHEN PSTD.ProjektstundeTypID = 1 THEN PSTD.AnzahlStunden END, 0) AS PSTD_ProjektStd
	,COALESCE(CASE WHEN PSTD.ProjektstundeTypID = 2 THEN PSTD.AnzahlStunden END, 0) AS PSTD_SonderStd
	,COALESCE(CASE WHEN PSTD.ProjektstundeTypID = 3 THEN PSTD.AnzahlStunden END, 0) AS PSTD_RufbereitStd
	,COALESCE(CASE WHEN PSTD.ProjektstundeTypID = 4 THEN PSTD.AnzahlStunden END, 0) AS PSTD_ReiseTatsStd
	,COALESCE(CASE WHEN PSTD.ProjektstundeTypID = 5 THEN PSTD.AnzahlStunden END, 0) AS PSTD_ReiseAngerechnetStd
FROM
	dbo.Projektstunde AS PSTD
    INNER JOIN dbo.Arbeitsnachweis AS ANW ON ANW.ID = PSTD.ArbeitsnachweisID
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = ANW.MitarbeiterID
    INNER JOIN dbo.Projekt AS PRJ ON PRJ.ID = PSTD.ProjektID
	INNER JOIN dbo.Organisationseinheit AS OE ON OE.ScribeID = PRJ.OrganisationseinheitID
	INNER JOIN dbo.Kunde AS KND ON KND.ScribeID = PRJ.KundeID