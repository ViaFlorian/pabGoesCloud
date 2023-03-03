SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		HuM
-- Create date: 26.03.2020
-- Description:	Skript zum Löschen einer Korrektur, sofern das Urlaubs- oder Stundenkonto nicht betroffen ist (Eingabeparameter: KorrekturID)
--				Dies ist unabhängig vom MitarbeiterTyp (keine Prüfung auf interner Mitarbeiter oder Freiberufler)
-- =============================================

CREATE PROCEDURE [dbo].[loescheKorrekturbuchung]
@korrekturID INT
AS
BEGIN TRY

    DECLARE @jahr INT;
    DECLARE @monat INT;
    DECLARE @projNr INT;
    DECLARE @anzahlStunden DECIMAL(18,2);
    DECLARE @korrekturGegenbuchungID INT;
    DECLARE @gegenbuchungID VARCHAR(80);
    DECLARE @projNrGegenbuchung INT;
    DECLARE @anzahlStundenGegenbuchung DECIMAL(18,2);

--Prüfung, ob ID existiert
    IF (SELECT COUNT(*) FROM ProjektabrechnungKorrekturbuchung where ProjektabrechnungKorrekturbuchung.ID = @korrekturID) = 0
        RAISERROR ('Zur eingegebenen ID existiert keine Korrekturbuchung. Bitte ID prüfen und Vorgang erneut ausführen!', 11, 1)

-- ermittel Projektnummer und Anzahl Stunden der Korrektur
    SELECT	@monat = Monat,
              @jahr = Jahr,
              @projNr = Projektnummer,
              @anzahlStunden = COALESCE(AnzahlStundenKosten,0),
              @gegenbuchungID = GegenbuchungID
    FROM ProjektabrechnungKorrekturbuchung INNER JOIN
         Projekt ON ProjektID = Projekt.ID
    WHERE ProjektabrechnungKorrekturbuchung.ID = @korrekturID

-- ermittel Gegenbuchung
    IF @gegenbuchungID IS NOT NULL
        BEGIN
            SELECT	@projNrGegenbuchung = Projektnummer,
                      @anzahlStundenGegenbuchung = AnzahlStundenKosten,
                      @korrekturGegenbuchungID = ProjektabrechnungKorrekturbuchung.ID
            FROM ProjektabrechnungKorrekturbuchung INNER JOIN
                 Projekt ON ProjektID = Projekt.ID
            WHERE ProjektabrechnungKorrekturbuchung.ID <> @korrekturID and ProjektabrechnungKorrekturbuchung.GegenbuchungID = @gegenbuchungID
        END

--Prüfung, ob Korrektur in einem abgeschlossenen Monat liegt
    IF (SELECT COUNT(*) FROM AbgeschlosseneMonate where Jahr = @jahr AND Monat = @monat) > 0
        RAISERROR ('Korrektur liegt in einem abgeschlossenen Abrechnungsmonat. Löschung nicht möglich!', 11, 1)

--Prüfung, ob Urlaubskonto betroffen
    IF @projNr = 9004 OR @projNrGegenbuchung = 9004
        RAISERROR ('Urlaubskonto betroffen. Keine Löschung vorgenommen. Bitte detailliert prüfen und manuell löschen!', 11, 1)

--Prüfung, ob Stundenkonto betroffen
    IF @anzahlStunden + COALESCE(@anzahlStundenGegenbuchung,0) <> 0
        RAISERROR ('Stundenkonto betroffen. Keine Löschung vorgenommen. Bitte detailliert prüfen und manuell löschen!', 11, 1)

--Löschung der Buchung und ggfs. Gegenbuchung
    DELETE
    FROM ProjektabrechnungKorrekturbuchung
    WHERE ID in (@korrekturID,@korrekturGegenbuchungID);

END TRY

BEGIN CATCH
    SELECT
        ERROR_NUMBER(),
        ERROR_STATE(),
        ERROR_SEVERITY(),
        ERROR_LINE(),
        ERROR_PROCEDURE(),
        ERROR_MESSAGE(),
        GETDATE();

    DECLARE @Message varchar(MAX) = ERROR_MESSAGE(),
        @Severity int = ERROR_SEVERITY(),
        @State smallint = ERROR_STATE()

    RAISERROR (@Message, @Severity, @State)
END CATCH