ALTER TABLE Projekt ADD
                                [SachbearbeiterID] [int] NULL;


ALTER TABLE [dbo].[Projekt]  WITH CHECK ADD  CONSTRAINT [FK_Projekt_SachbearbeiterID] FOREIGN KEY([SachbearbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
;

