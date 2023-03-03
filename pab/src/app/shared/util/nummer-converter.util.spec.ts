import { rundeNummerAufZweiNackommastellen } from './nummer-converter.util';

describe('nummer-converter.util', function () {
  it('Runde Nummer mit 2 Nachkommastellen nicht', () => {
    const nummer: number = 1.15;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.15);
  });

  it('Runde Nummer mit 3 Nachkommastellen auf 2 Nachkommastellen ab', () => {
    const nummer: number = 1.111;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.11);
  });

  it('Runde Nummer mit 3 Nachkommastellen auf 2 Nachkommastellen auf', () => {
    const nummer: number = 1.115;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.12);
  });

  it('Runde Nummer mit 4 Nachkommastellen auf 2 Nachkommastellen ab', () => {
    const nummer: number = 1.1144;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.11);
  });

  it('Runde Nummer mit 4 Nachkommastellen auf 2 Nachkommastellen auf', () => {
    const nummer: number = 1.1145;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.12);
  });

  it('Runde Nummer mit mehr als 4 Nachkommastellen auf 2 Nachkommastellen ab', () => {
    const nummer: number = 1.11445;
    const gerundeteNummer = rundeNummerAufZweiNackommastellen(nummer);
    expect(gerundeteNummer).toEqual(1.11);
  });
});
