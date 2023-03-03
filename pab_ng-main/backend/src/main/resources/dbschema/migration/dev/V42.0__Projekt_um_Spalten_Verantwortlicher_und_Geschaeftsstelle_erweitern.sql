ALTER TABLE Projekt ADD Geschaeftsstelle varchar(30) NULL, VerantwortlicherMitarbeiterID int NULL;

ALTER TABLE [dbo].[Projekt]  WITH CHECK ADD  CONSTRAINT [FK_Projekt_VerantwortlicherMitarbeiterID] FOREIGN KEY([VerantwortlicherMitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID]);

