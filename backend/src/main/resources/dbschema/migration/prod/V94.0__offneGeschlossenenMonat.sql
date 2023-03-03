SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		HuM
-- Create date: 24.03.2020
-- Description:	Skript zum ffnen eines bereits geschlossenen Monats (Eingabeparameter: Jahr, Monat)
-- =============================================

CREATE PROCEDURE [dbo].[oeffeneGeschlossenenMonat]
    @jahr int,
    @monat int
AS
BEGIN TRY
    SET NOCOUNT ON;

--Prfung, ob der erfasste Monat existiert
    IF @monat NOT BETWEEN 1 and 12
        RAISERROR ('Der angegebene Monat entspricht nicht einem Monatswert!', 11, 1)

--Prfung, ob angegebener Monat > aktuellem Abrechnungsmonat
    IF DATEFROMPARTS (@jahr, @monat, 1) >= DATEFROMPARTS (DATEPART(yyyy,CURRENT_TIMESTAMP),DATEPART(mm,CURRENT_TIMESTAMP), 1)
        RAISERROR ('Der angegebene Monat ist grer als der aktuelle Abrechnungsmonat!', 11, 1)

--Prfung, ob ID existiert
    IF (SELECT COUNT(*) FROM AbgeschlosseneMonate where Monat = @monat AND Jahr = @jahr) = 0
        RAISERROR ('Der eingegebene Monat ist bisher nicht abgeschlossen!', 11, 1)

--Abgeschlossene Monate lschen
    DELETE
    FROM
        AbgeschlosseneMonate
    WHERE
            Monat = @monat AND
            Jahr = @jahr
    ;

--Arbeitsnachweise ffnen
    UPDATE
        Arbeitsnachweis
    SET StatusID = 40
    WHERE
            Monat = @monat AND
            Jahr = @jahr
    ;

--Stundenkonto ffnen
    UPDATE
        MitarbeiterStundenKonto
    SET IstEndgueltig = 0
    WHERE
            DATEPART(yyyy, Wertstellung) = @jahr
      AND	DATEPART(mm, Wertstellung) = @monat
      AND IstAutomatisch = 1
    ;

--Urlaubskonto ffnen
    UPDATE
        MitarbeiterUrlaubKonto
    SET IstEndgueltig = 0
    WHERE
            DATEPART(yyyy, Wertstellung) = @jahr AND
            DATEPART(mm, Wertstellung) = @monat AND
            IstAutomatisch = 1
    ;

--Projektabrechnung ffnen
    UPDATE
        Projektabrechnung
    SET StatusID = 40
    WHERE
            Monat = @monat AND
            Jahr = @jahr
    ;

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
