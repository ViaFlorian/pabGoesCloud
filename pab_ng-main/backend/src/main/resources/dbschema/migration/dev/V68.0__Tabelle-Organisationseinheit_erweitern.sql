ALTER TABLE Organisationseinheit ADD Beschreibung [varchar](255) NULL, VerantwortlicherMitarbeiterID [int] NULL 
;
	
ALTER TABLE Organisationseinheit  WITH CHECK ADD  CONSTRAINT FK_OE_Mitarbeiter FOREIGN KEY([VerantwortlicherMitarbeiterID])
REFERENCES Mitarbeiter ([ID])
;

ALTER TABLE Organisationseinheit CHECK CONSTRAINT FK_OE_Mitarbeiter
;