SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		HuM
-- Create date: 24.03.2020
-- Description:	Skript zum Einbuchen des Jahresurlaubs für alle aktiven Angestellten (nicht für Stundenten!) (Eingabeparameter: Anspruch, Jahr)
-- =============================================

CREATE PROCEDURE [dbo].[bucheJahresurlaubFuerAngestellte]
    @anspruch decimal,
    @jahr int
AS
BEGIN TRY
    DECLARE @mitarbeiterID bigint;
    DECLARE @mitarbeiterNachname VARCHAR(80);
    DECLARE @mitarbeiterVorname VARCHAR(80);
    DECLARE @cursorMitarbeiterID CURSOR;
    DECLARE @buchungstext char(25);
    DECLARE @saldo decimal(18,2);

--Prüfung, ob das aktuelle Jahr
    IF @jahr <> DATEPART(yyyy,CURRENT_TIMESTAMP)
        RAISERROR ('Es kann nur Jahresurlaub für das aktuelle Jahr eingebucht werden!', 11, 1)

--Buchungstext definieren
    SET @buchungstext = CONCAT('Urlaubsanspruch ',CAST(@jahr as char (4)));

--MitarbeiterID der aktiven Mitarbeiter bestimmen, für die Urlaub gebucht werden kann
    SET @cursorMitarbeiterID = CURSOR LOCAL STATIC READ_ONLY FORWARD_ONLY
        FOR
        SELECT ID, Nachname, Vorname
        FROM Mitarbeiter
        WHERE	IstAktiv = 1 AND
                IstIntern = 1 AND
                MitarbeiterTypID = (SELECT ID FROM C_MitarbeiterTyp WHERE TextKurz LIKE 'Angestellter')

    OPEN @cursorMitarbeiterID;

--Alle zu bebuchende Mitarbeiter durchlaufen
    FETCH NEXT FROM @cursorMitarbeiterID INTO @mitarbeiterID, @mitarbeiterNachname, @mitarbeiterVorname
    WHILE @@FETCH_STATUS = 0
        BEGIN

            SET NOCOUNT ON;

--aktuelles/letztes Saldo ermitteln
            SET @saldo = (
                SELECT lfdSaldo
                FROM MitarbeiterUrlaubkonto INNER JOIN Mitarbeiter ON MitarbeiterID=Mitarbeiter.ID
                WHERE Mitarbeiter.ID = @mitarbeiterID AND
                        Mitarbeiterurlaubkonto.ID IN
                        (SELECT ID
                         FROM (
                                  SELECT ID,row_number()
                                          OVER (PARTITION BY MitarbeiterID ORDER BY Wertstellung DESC, Buchungsdatum DESC) AS roworder
                                  FROM MitarbeiterUrlaubKonto
                              ) temp
                         WHERE roworder = 1
                        )
            );

            IF @saldo IS NULL
                SET @saldo = 0;

--Urlaubsanspruch einbuchen
            INSERT INTO MitarbeiterUrlaubKonto
            (	[MitarbeiterID]
            , [Wertstellung]
            , [Buchungsdatum]
            , [AnzahlTage]
            , [lfdSaldo]
            , [BuchungstypUrlaubID]
            , [Bemerkung]
            , [IstAutomatisch]
            , [ZuletztGeaendertVon]
            , [IstEndgueltig]
            )
            VALUES
            (	@mitarbeiterID
            , DATEFROMPARTS (@jahr, 1, 1 )
            , CURRENT_TIMESTAMP
            , @anspruch
            , @anspruch + @saldo
            , 3
            , @buchungstext
            , 1
            ,'System'
            , 1
            )

            PRINT CONCAT('ID: ', @mitarbeiterID, ' ', @mitarbeiterNachname, ', ',@mitarbeiterVorname, ', Anspruch: ', @anspruch, ', Saldo: ', @saldo + @anspruch)

            FETCH NEXT FROM @cursorMitarbeiterID INTO @mitarbeiterID, @mitarbeiterNachname, @mitarbeiterVorname
        END;

    CLOSE @cursorMitarbeiterID;
    DEALLOCATE @cursorMitarbeiterID;

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