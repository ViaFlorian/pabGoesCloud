SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		HuM
-- Create date: 25.03.2020
-- Description:	Skript zum Entfernen einer Testrolle fr einen Mitarbeiter (Eingabeparameter: Krzel)
-- =============================================

CREATE PROCEDURE [dbo].[entferneTestrolleFuerKuerzel]
@kuerzel char(3)
AS
BEGIN

    DECLARE @separator NCHAR(1)
    DECLARE @position int
    DECLARE @stringTestrolle NVARCHAR(MAX)
    DECLARE @zuAnalysierenderSubstring NVARCHAR(MAX);
    DECLARE @result NVARCHAR(MAX)

    SET @separator=','
    SET @position = 1
    SET @stringTestrolle = (SELECT [VALUE]
                            FROM Parameter
                            WHERE [Key] = 'TestRollen')

-- Geschweifte Klammer am Ende entfernen
    SET @stringTestrolle = LEFT(@stringTestrolle,LEN(@stringTestrolle)-2)

-- wenn kein Inhalt vorhanden oder Krzel nicht gefunden
    IF LEN(@stringTestrolle) < 4 OR CHARINDEX(@kuerzel,@stringTestrolle) = 0
        SET @result = @stringTestrolle
    ELSE
        BEGIN
            -- wenn Krzel im String vorhanden, dann suchen und entfernen
            WHILE CHARINDEX(@separator,@stringTestrolle,@position) <> 0
                BEGIN
                    --String wird abschnittsweise analysiert
                    SET @zuAnalysierenderSubstring = SUBSTRING(@stringTestrolle, @position, CHARINDEX(@separator,@stringTestrolle,@position) - @position)

                    --wenn Krzel nicht gefunden, wird der Teilstring dem Ergebnis angefgt
                    IF CHARINDEX(@kuerzel,@zuAnalysierenderSubstring) = 0
                        SET @result = CONCAT(@result,@zuAnalysierenderSubstring,',')
                    ELSE
                        IF CHARINDEX('{',@zuAnalysierenderSubstring) = 1
                            SET @result = '{'
                    SET @position = CHARINDEX(@separator,@stringTestrolle,@position) + 1
                END

            --letzter Teil des Strings analysieren
            SET @zuAnalysierenderSubstring = SUBSTRING(@stringTestrolle, @position, LEN(@stringTestrolle))

            --wenn Krzel nicht gefunden, wird der Teilstring dem Ergebnis angefgt
            IF CHARINDEX(@kuerzel,@zuAnalysierenderSubstring) = 0
                SET @result = CONCAT(@result,@zuAnalysierenderSubstring)
            ELSE
                IF CHARINDEX('{',@zuAnalysierenderSubstring) = 1
                    SET @result = '{'
                ELSE
                    --Komma entfernen an der letzten Stelle
                    SET @result = LEFT(@result,LEN(@result)-1)
        END

-- Geschweifte Klammer am Ende wieder hinzufgen
    SET @result = CONCAT(@result,' }')

--Rolle in Datenbank aktualisieren
    UPDATE
        PARAMETER
    SET	[Value] = @result
    WHERE
            [Key] = 'TestRollen';

END
