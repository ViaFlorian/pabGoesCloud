ALTER TABLE SonstigeProjektkosten
    ADD ZuletztGeaendertAm datetime DEFAULT SYSDATETIME() NOT NULL;
ALTER TABLE SonstigeProjektkosten
    ADD ZuletztGeaendertVon varchar(80) DEFAULT ' ' NOT NULL;
