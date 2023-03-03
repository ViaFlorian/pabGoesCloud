CREATE TABLE Neuigkeiten
(
    ID                                   int IDENTITY (1,1) NOT NULL,
    meldung                              varchar(2000)      NOT NULL,
    nurFuerRolle                         varchar(100)       NULL,
    anzeigenVon                          datetime           NOT NULL,
    anzeigenBis                          datetime           NOT NULL,
    CONSTRAINT PK_Neuigkeiten PRIMARY KEY CLUSTERED
        (
         ID ASC
        )
)