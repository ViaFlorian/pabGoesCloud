UPDATE Parameter SET [Key] = 'Zumutbare_Reisezeit_kleiner_8_Stunden', Kommentar = 'viadee-Regelung für die zumutbare Reisezeit unter 8 Projektstunden' WHERE [Key] = 'Zumutbare_Reisezeit';

INSERT INTO Parameter ([Key], [Value], Kommentar) VALUES ('Zumutbare_Reisezeit_zwischen_8_und_9_Stunden', '1.50', 'viadee-Regelung für die zumutbare Reisezeit zwischen 8 und 9 Projektstunden');
INSERT INTO Parameter ([Key], [Value], Kommentar) VALUES ('Zumutbare_Reisezeit_ueber_9_Stunden', '1.00', 'viadee-Regelung für die zumutbare Reisezeit über 9 Projektstunden');

