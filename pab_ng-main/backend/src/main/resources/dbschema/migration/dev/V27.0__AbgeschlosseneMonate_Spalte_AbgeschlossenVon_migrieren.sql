UPDATE AbgeschlosseneMonate set AbgeschlosseneMonate.AbgeschlossenVonMitarbeiterID = (SELECT ID FROM Mitarbeiter WHERE Kurzname = AbgeschlosseneMonate.AbgeschlossenVon);