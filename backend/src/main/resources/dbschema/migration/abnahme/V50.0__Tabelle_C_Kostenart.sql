CREATE TABLE C_Kostenart(
	ID [int] IDENTITY(1,1) NOT NULL,
	Bezeichnung [varchar](80) NOT NULL,
	CONSTRAINT KostenartID PRIMARY KEY CLUSTERED
	(
    	ID ASC
    )
)


