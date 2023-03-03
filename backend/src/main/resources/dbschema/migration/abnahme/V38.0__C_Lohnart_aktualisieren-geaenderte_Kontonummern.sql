UPDATE C_Lohnart SET Bearbeitungsschluessel = 3 WHERE Konto in ('081', '078');
UPDATE C_Lohnart SET Konto = '9978' WHERE Konto = '081';
UPDATE C_Lohnart SET Konto = '9979' WHERE Konto = '078';
