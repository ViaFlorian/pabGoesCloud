UPDATE C_ViadeeZuschlaege
SET bisInklusive = 0
WHERE StundenAbwesendVon = 6.00;


UPDATE C_ViadeeZuschlaege
SET vonInklusive = 1
WHERE StundenAbwesendVon = 10.00;