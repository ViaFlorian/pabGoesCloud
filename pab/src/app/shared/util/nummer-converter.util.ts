export function convertUsNummerZuDeString(nummer: number): string {
  return convertUsNummerZuDeStringMitAnzahlNachkommastellen(nummer, 0);
}

export function convertUsNummerZuDeStringMitGenauZweiNachkommastellen(nummer: number): string {
  return convertUsNummerZuDeStringMitAnzahlNachkommastellen(nummer, 2);
}

export function convertUsNummerZuDeStringMitGenauSechsNachkommastellen(nummer: number): string {
  return convertUsNummerZuDeStringMitAnzahlNachkommastellen(nummer, 6);
}

export function convertUsNummerZuDeStringMitAnzahlNachkommastellen(nummer: number, nachkommastellen: number): string {
  if (nummer || nummer === 0) {
    const zahlMitNachkommstellen = nummer.toFixed(nachkommastellen);
    const zahlMitKomma = zahlMitNachkommstellen.replace('.', ',');
    return zahlMitKomma.replace(/\B(?<!,\d*)(?=(\d{3})+(?!\d))/g, '.');
  }

  if (nachkommastellen === 0) {
    return '0';
  }

  return `0.${'0'.repeat(nachkommastellen)}`;
}

export function convertDeStringZuUsNummer(nummerAlsString: string): number {
  if (!nummerAlsString) {
    return 0;
  }
  // Tausender-Trennzeichen entfernen, dann Komma durch Punkt ersetzen
  return Number(nummerAlsString.replace('.', '').replace(',', '.'));
}

/**
 * Rundet eine Zahl kaufmännisch auf die 2. Nachkommastelle. Hierbei wird maximal bis einschließlich
 * der 4. Nachkommastelle gerundet.
 */
export function rundeNummerAufZweiNackommastellen(betrag: number): number {
  const gerundetAuf3Nachkommastellen: number = Math.round(betrag * 1000) / 1000;
  return Math.round(gerundetAuf3Nachkommastellen * 100) / 100;
}
