SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		HuM
-- Create date: 25.03.2020
-- Description:	Skript zum Setzen/Einfgen einer Testrolle fr einen Mitarbeiter (Eingabeparameter: Krzel, Rollenbezeichnung)

-- Mgliche Rollen sind:
--	'PAB-OELeiter'
--	'PAB-GF'
--	'PAB-Sachbearbeiter'
-- =============================================

CREATE PROCEDURE [dbo].[setzenTestrolleFuerKuerzel]
    @kuerzel char(3),
    @rolle NVARCHAR(MAX)
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

-- wenn kein Inhalt vorhanden
    IF LEN(@stringTestrolle) < 4
        SET @result = CONCAT(@stringTestrolle,' "', @kuerzel,'" : "',@rolle,'"')
    ELSE
        BEGIN
            -- wenn Krzel nicht gefunden, dann Rolle anfgen
            IF CHARINDEX(@kuerzel,@stringTestrolle) = 0
                SET @result = CONCAT(@stringTestrolle,', "',@kuerzel,'" : "',@rolle,'"')
            ELSE
-- wenn Krzel im String vorhanden, dann Rolle ersetzen
                BEGIN
                    WHILE CHARINDEX(@separator,@stringTestrolle,@position) <> 0
                        BEGIN
                            --String wird abschnittsweise analysiert
                            SET @zuAnalysierenderSubstring = substring(@stringTestrolle, @position, charindex(@separator,@stringTestrolle,@position) - @position)

                            --wenn Krzel gefunden, Rolle ersetzen
                            IF CHARINDEX(@kuerzel,@zuAnalysierenderSubstring) > 0
                                BEGIN
                                    SET @result = CONCAT(@result,LEFT(@zuAnalysierenderSubstring,CHARINDEX(':',@zuAnalysierenderSubstring)))
                                    SET @result = CONCAT(@result,' "',@rolle,'",')
                                END
                            ELSE
                                SET @result = CONCAT(@result,@zuAnalysierenderSubstring,',')

                            SET @position = charindex(@separator,@stringTestrolle,@position) + 1
                        END

                    --letzter Teil des Strings analysieren
                    SET @zuAnalysierenderSubstring = substring(@stringTestrolle, @position, Len(@stringTestrolle))

                    --wenn Krzel gefunden, Rolle ersetzen
                    IF CHARINDEX(@kuerzel,@zuAnalysierenderSubstring) > 0
                        BEGIN
                            SET @result = CONCAT(@result,LEFT(@zuAnalysierenderSubstring,CHARINDEX(':',@zuAnalysierenderSubstring)))
                            SET @result = CONCAT(@result,' "',@rolle,'"')
                        END
                    ELSE
                        SET @result = CONCAT(@result,@zuAnalysierenderSubstring)
                END
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
