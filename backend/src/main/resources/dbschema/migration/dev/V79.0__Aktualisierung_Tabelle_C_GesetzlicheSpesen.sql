UPDATE PARAMETER SET VALUE = 14 WHERE [KEY] = 'anreiseAbreisePauschale';

UPDATE PARAMETER SET VALUE = 5.6 WHERE [KEY] = 'zuschlagFuerFruehstuckBeiUebernachtung';

UPDATE PARAMETER SET VALUE = 5.6 WHERE [KEY] = 'abzugFuerFruehstuckBeiUebernachtung';

UPDATE PARAMETER SET VALUE = 11.2 WHERE [KEY] = 'abzugFuerMittagessen';

UPDATE PARAMETER SET VALUE = 11.2 WHERE [KEY] = 'abzugFuerAbendessen';


UPDATE C_GesetzlicheSpesen SET Betrag = 14 WHERE Betrag = 12;

UPDATE C_GesetzlicheSpesen SET Betrag = 28 WHERE Betrag = 24;
