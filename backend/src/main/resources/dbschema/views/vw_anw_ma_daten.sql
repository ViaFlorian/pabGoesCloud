CREATE VIEW
	[dbo].[VW_ANW_MA_DATEN]
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
    ,ANW.Stellenfaktor AS ANW_Stellenfaktor
    ,ANW.Sollstunden AS ANW_Sollstunden
	,MA.Kurzname AS MA_Kurzname
	,MA.Nachname AS MA_Name
	,MA.Vorname AS MA_Vorname
	,MA.IstIntern AS MA_IstIntern
	,MA.IstAktiv AS MA_IstAktiv
FROM
    dbo.Arbeitsnachweis AS ANW
    INNER JOIN dbo.Mitarbeiter AS MA ON MA.ID = ANW.MitarbeiterID