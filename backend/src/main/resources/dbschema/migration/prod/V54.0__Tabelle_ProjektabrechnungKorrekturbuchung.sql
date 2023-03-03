CREATE TABLE ProjektabrechnungKorrekturbuchung (
ID int IDENTITY(1,1) NOT NULL,
GegenbuchungID varchar(80) NULL,
ProjektID int NOT NULL,
MitarbeiterID int NULL,
Jahr smallint NULL,
Monat smallint NULL,
ReferenzJahr smallint NULL,
ReferenzMonat smallint NULL,
AnzahlStundenKosten decimal(18,2) NULL,
BetragKostensatz decimal(18,2) NULL,
AnzahlStundenLeistung decimal(18,2) NULL,
BetragStundensatz decimal(18,2) NULL,
KostenartID int NOT NULL,
Bemerkung [varchar](255) NULL,
ZuletztGeaendertAm datetime NOT NULL,
ZuletztGeaendertVon varchar(80) NOT NULL,
CONSTRAINT PK_ProjektabrechnungKorrekturbuchung PRIMARY KEY CLUSTERED
   (
    ID ASC
       )
);




