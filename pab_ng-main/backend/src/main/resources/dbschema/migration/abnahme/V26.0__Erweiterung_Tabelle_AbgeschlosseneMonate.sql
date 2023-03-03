ALTER TABLE AbgeschlosseneMonate ADD
                                [AbgeschlossenVonMitarbeiterID] [int] NULL,
                                [LodasExport] [varbinary](max) NULL,
                                [LodasErzeugtAm] [datetime] NULL,
                                [LodasErzeugtMitarbeiterID] [int] NULL,
                                [JahresuebersichtVersendetAm] [datetime] NULL,
                                [JahresuebersichtVersendetMitarbeiterID] [int] NULL;

ALTER TABLE [dbo].[AbgeschlosseneMonate]  WITH CHECK ADD  CONSTRAINT [FK_AbgeschlosseneMonate_LodasErzeugtMitarbeiterID] FOREIGN KEY([LodasErzeugtMitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
;

ALTER TABLE [dbo].[AbgeschlosseneMonate]  WITH CHECK ADD  CONSTRAINT [FK_AbgeschlosseneMonate_JahresuebersichtVersendetMitarbeiterID] FOREIGN KEY([JahresuebersichtVersendetMitarbeiterID])
REFERENCES [dbo].[Mitarbeiter] ([ID])
;
