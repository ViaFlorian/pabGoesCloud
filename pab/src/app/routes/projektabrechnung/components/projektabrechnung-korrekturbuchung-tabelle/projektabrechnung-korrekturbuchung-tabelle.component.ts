import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Kostenart } from '../../../../shared/model/konstanten/kostenart';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { KorrekturbuchungTabellendarstellung } from '../../model/korrekturbuchung-tabellendarstellung';
import { ProjektabrechnungKorrekturbuchung } from '../../../../shared/model/projektabrechnung/projektabrechnung-korrekturbuchung';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { KostenartAnzeigeNamePipe } from '../../../../shared/pipe/kostenart-anzeige-name.pipe';
import { MitarbeiterAnzeigeNameKurzformPipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name-kurzform.pipe';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';

@Component({
  selector: 'pab-projektabrechnung-korrekturbuchung-tabelle',
  templateUrl: './projektabrechnung-korrekturbuchung-tabelle.component.html',
  styleUrls: ['./projektabrechnung-korrekturbuchung-tabelle.component.scss'],
})
export class ProjektabrechnungKorrekturbuchungTabelleComponent implements OnChanges, AfterViewInit {
  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  @Input()
  kostenartAuswahl!: Kostenart[];

  @Input()
  projektabrechnungKorrekturbuchungen!: ProjektabrechnungKorrekturbuchung[];

  korrekturbuchungTabellendarstellung!: KorrekturbuchungTabellendarstellung[];

  @Output()
  buchungAusgewaehltEvent = new EventEmitter<ProjektabrechnungKorrekturbuchung>();

  @ViewChild(MatMultiSort)
  sort!: MatMultiSort;
  tabelle: TableData<KorrekturbuchungTabellendarstellung>;
  spalten = [
    { id: 'typ', name: 'Typ' },
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'mitarbeiterName', name: 'Mitarbeiter/in' },
    { id: 'kostenartBezeichnung', name: 'Kostenart' },
    { id: 'anzahlStundenKosten', name: 'Kosten Anz. Std.' },
    { id: 'betragKostensatz', name: 'Betrag/Kostensatz' },
    { id: 'kosten', name: 'Kosten' },
    { id: 'anzahlStundenLeistung', name: 'Leistung Anz.' },
    { id: 'betragStundensatz', name: 'Betrag/Stundensatz' },
    { id: 'leistung', name: 'Leistung' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'zuletztGeaendertAm', name: 'Geändert am' },
    { id: 'zuletztGeaendertVon', name: 'Geändert von' },
  ];

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    private mitarbeiterAnzeigeNameKurzformPipe: MitarbeiterAnzeigeNameKurzformPipe,
    private kostenartAnzeigeNamePipe: KostenartAnzeigeNamePipe
  ) {
    this.tabelle = new TableData<KorrekturbuchungTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<KorrekturbuchungTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektabrechnungKorrekturbuchungen']) {
      const projektabrechnungKorrekturbuchungenChange = changes['projektabrechnungKorrekturbuchungen'];
      if (
        projektabrechnungKorrekturbuchungenChange.currentValue &&
        projektabrechnungKorrekturbuchungenChange.previousValue &&
        projektabrechnungKorrekturbuchungenChange.currentValue.length !==
          projektabrechnungKorrekturbuchungenChange.previousValue.length
      ) {
        this.aktualisiereKorrekturbuchungTabellendarstellung();
      }
    }
  }

  reagiereAufKorrekturbuchungAusgewaehlt(korrekturbuchungTabellendarstellung: KorrekturbuchungTabellendarstellung) {
    const korrekturBuchung: ProjektabrechnungKorrekturbuchung | undefined =
      this.projektabrechnungKorrekturbuchungen.find(
        (korrekturbuchung) => korrekturbuchung.id === korrekturbuchungTabellendarstellung.id
      );
    if (korrekturBuchung) {
      this.buchungAusgewaehltEvent.emit(korrekturBuchung);
    }
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<KorrekturbuchungTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.korrekturbuchungTabellendarstellung;
  }

  private initialisiereTabellenSortierung(): void {
    this.tabelle.sortParams = ['abrechnungsmonat', 'typ', 'mitarbeiterName', 'kostenartBezeichnung'];
    this.tabelle.sortDirs = ['desc', 'desc', 'asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private aktualisiereKorrekturbuchungTabellendarstellung() {
    this.tabelle.data = this.projektabrechnungKorrekturbuchungen.map((projektabrechnungKorrekturbuchung) => {
      return this.mapProjektabrechnungKorrekturbuchungAufTabellendarstellung(projektabrechnungKorrekturbuchung);
    });
  }

  private mapProjektabrechnungKorrekturbuchungAufTabellendarstellung(
    projektabrechnungKorrekturbuchung: ProjektabrechnungKorrekturbuchung
  ): KorrekturbuchungTabellendarstellung {
    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      projektabrechnungKorrekturbuchung.mitarbeiterId,
      this.mitarbeiterAuswahl
    ) as Mitarbeiter;
    const kostenart: Kostenart = getObjektAusListeDurchId(
      projektabrechnungKorrekturbuchung.kostenartId,
      this.kostenartAuswahl
    ) as Kostenart;

    const typ = projektabrechnungKorrekturbuchung.istKorrekturbuchung ? 'K' : 'B';

    const kosten = this.getKosten(projektabrechnungKorrekturbuchung);

    return {
      id: projektabrechnungKorrekturbuchung.id,
      typ: typ,
      abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(projektabrechnungKorrekturbuchung),
      mitarbeiterName: this.mitarbeiterAnzeigeNameKurzformPipe.transform(mitarbeiter),
      kostenartBezeichnung: this.kostenartAnzeigeNamePipe.transform(kostenart),
      anzahlStundenKosten: projektabrechnungKorrekturbuchung.anzahlStundenKosten,
      betragKostensatz: projektabrechnungKorrekturbuchung.betragKostensatz,
      kosten: kosten,
      anzahlStundenLeistung: projektabrechnungKorrekturbuchung.anzahlStundenLeistung,
      betragStundensatz: projektabrechnungKorrekturbuchung.betragStundensatz,
      leistung: projektabrechnungKorrekturbuchung.leistung,
      bemerkung: projektabrechnungKorrekturbuchung.bemerkung,
      zuletztGeaendertAm: projektabrechnungKorrekturbuchung.zuletztGeaendertAm,
      zuletztGeaendertVon: projektabrechnungKorrekturbuchung.zuletztGeaendertVon,
    } as KorrekturbuchungTabellendarstellung;
  }

  private getKosten(projektabrechnungKorrekturbuchung: ProjektabrechnungKorrekturbuchung) {
    if (
      projektabrechnungKorrekturbuchung.anzahlStundenKosten === null &&
      projektabrechnungKorrekturbuchung.betragKostensatz === null
    ) {
      return null;
    } else {
      return this.berechneProdukt(
        projektabrechnungKorrekturbuchung.anzahlStundenKosten,
        projektabrechnungKorrekturbuchung.betragKostensatz
      );
    }
  }

  private berechneProdukt(zahl1: number, zahl2: number) {
    const faktor1 = zahl1 === null || zahl1 === 0 ? 1 : zahl1;
    const faktor2 = zahl2 === null || zahl2 === 0 ? 0 : zahl2;
    return faktor1 * faktor2;
  }
}
