import { ObjektMitId } from './objekt-mit-id';

export class StatusListen {
  private aktualisiert: ObjektMitId[];
  private geloescht: ObjektMitId[];
  private neu: ObjektMitId[];
  private unveraendert: ObjektMitId[];

  constructor(
    unveraendert: ObjektMitId[],
    aktualisiert?: ObjektMitId[],
    neu?: ObjektMitId[],
    geloescht?: ObjektMitId[]
  ) {
    this.unveraendert = unveraendert;

    if (aktualisiert && aktualisiert?.length > 0) {
      this.aktualisiert = aktualisiert;
    } else {
      this.aktualisiert = [];
    }

    if (neu && neu?.length > 0) {
      this.neu = neu;
    } else {
      this.neu = [];
    }

    if (geloescht && geloescht?.length > 0) {
      this.geloescht = geloescht;
    } else {
      this.geloescht = [];
    }
  }

  aktualisiereElement(elementZuAktualisieren: ObjektMitId) {
    // Wenn element in Liste "unverändert", loesche und fuege hinzu zu Liste aktualisiert
    if (this.loescheUndFuegeElementZuListen(elementZuAktualisieren, this.unveraendert, this.aktualisiert)) {
      return;
    }
    // Wenn element in Liste "aktualisiert", loesche und fuege hinzu zu Liste aktualisiert
    if (this.loescheUndFuegeElementZuListen(elementZuAktualisieren, this.aktualisiert, this.aktualisiert)) {
      return;
    }

    // Wenn element in Liste "neu", loesche und fuege hinzu zu Liste neu
    if (this.loescheUndFuegeElementZuListen(elementZuAktualisieren, this.neu, this.neu)) {
      return;
    }

    throw new Error(`Objekt ${elementZuAktualisieren} konnte nicht gefunden werden`);
  }

  loescheElement(elementZuLoeschen: ObjektMitId) {
    // Wenn element in Liste "unverändert", loesche und fuege hinzu zu Liste "gelöscht"
    if (this.loescheUndFuegeElementZuListen(elementZuLoeschen, this.unveraendert, this.geloescht)) {
      return;
    }
    // Wenn element in Liste "aktualisiert", loesche und fuege hinzu zu Lste "gelöscht"
    if (this.loescheUndFuegeElementZuListen(elementZuLoeschen, this.aktualisiert, this.geloescht)) {
      return;
    }
    // Wenn element in Liste "neu", loesche ohne verschieben
    if (this.loescheUndFuegeElementZuListen(elementZuLoeschen, this.neu, null)) {
      return;
    }

    throw new Error(`Objekt ${elementZuLoeschen} konnte nicht gefunden werden`);
  }

  fuegeNeuesElementHinzu(elementHinzuzufuegen: ObjektMitId) {
    this.neu.push(elementHinzuzufuegen);
  }

  getAnzeigeListe(): ObjektMitId[] {
    let anzeigeListe: ObjektMitId[] = [];
    anzeigeListe = anzeigeListe.concat(this.unveraendert);
    anzeigeListe = anzeigeListe.concat(this.aktualisiert);
    anzeigeListe = anzeigeListe.concat(this.neu);
    return anzeigeListe;
  }

  getUnveraendertListe(): ObjektMitId[] {
    return this.unveraendert;
  }

  getNeuListe(): ObjektMitId[] {
    return this.neu;
  }

  getAktualisiertListe(): ObjektMitId[] {
    return this.aktualisiert;
  }

  getGeloeschtListe(): ObjektMitId[] {
    return this.geloescht;
  }

  setGeloescht(value: ObjektMitId[]) {
    this.geloescht = value;
  }

  istVeraendert(): boolean {
    return this.neu.length !== 0 || this.aktualisiert.length !== 0 || this.geloescht.length !== 0;
  }

  private loescheUndFuegeElementZuListen(
    elementZuVerschieben: ObjektMitId,
    listToRemove: ObjektMitId[],
    listToAdd: ObjektMitId[] | null
  ): boolean {
    const index = listToRemove.findIndex((o) => o.id === elementZuVerschieben.id);
    if (index < 0) {
      return false;
    }

    listToRemove.splice(index, 1);

    if (listToAdd) {
      listToAdd.push(elementZuVerschieben);
    }

    return true;
  }
}
