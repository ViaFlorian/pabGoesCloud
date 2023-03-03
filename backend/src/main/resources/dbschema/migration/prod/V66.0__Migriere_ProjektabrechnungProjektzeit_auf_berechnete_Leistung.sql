UPDATE ProjektabrechnungProjektzeit
SET
	ProjektabrechnungProjektzeit.BerechneteLeistung = COALESCE(ProjektabrechnungProjektzeit.StundenANW,0) * COALESCE(ProjektabrechnungProjektzeit.Stundensatz,0)
WHERE
	ProjektabrechnungProjektzeit.ID IN
	(
		SELECT
			ProjektabrechnungProjektzeit.ID
		FROM
			ProjektabrechnungProjektzeit INNER JOIN
			Projektabrechnung ON
				ProjektabrechnungProjektzeit.ProjektabrechnungID = Projektabrechnung.ID INNER JOIN
			Projekt ON
				Projektabrechnung.ProjektID = Projekt.ID AND
				NOT Projekt.Projekttyp IN ('Festpreis', 'Wartung', 'Produkt')
		)