ALTER TABLE Mitarbeiter
    ADD VorgesetzterID [int] NULL
;

ALTER TABLE Mitarbeiter  WITH CHECK ADD CONSTRAINT FK_Mitarbeiter_Vorgesetzter FOREIGN KEY([VorgesetzterID])
    REFERENCES Mitarbeiter ([ID])
;

ALTER TABLE Mitarbeiter CHECK CONSTRAINT FK_Mitarbeiter_Vorgesetzter
;