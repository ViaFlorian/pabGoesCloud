ALTER TABLE SonstigeProjektkosten ADD BelegArtID int NULL;
ALTER TABLE SonstigeProjektkosten ADD KostenartID int NULL;



ALTER TABLE SonstigeProjektkosten WITH CHECK ADD CONSTRAINT FK_SonstigeProjektkosten_Belegart FOREIGN KEY(BelegArtID)
REFERENCES C_BelegArt (ID);

ALTER TABLE SonstigeProjektkosten WITH CHECK ADD CONSTRAINT FK_SonstigeProjektkosten_Kostenart FOREIGN KEY(KostenartID)
REFERENCES C_ViadeeAuslagenKostenart (ID);