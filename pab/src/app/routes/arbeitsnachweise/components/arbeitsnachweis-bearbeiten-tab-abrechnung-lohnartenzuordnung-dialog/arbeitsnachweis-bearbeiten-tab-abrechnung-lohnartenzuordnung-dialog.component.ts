import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Inject, Output, ViewChild } from '@angular/core';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { LohnartZuordnungTabellendarstellung } from '../../model/lohnart-zuordnung-tabellendarstellung';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LohnartZuordnung } from '../../../../shared/model/arbeitsnachweis/lohnart-zuordnung';
import { Lohnart } from '../../../../shared/model/konstanten/lohnart';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbrechnungLohnartenzuordnungDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  @Output()
  lohnartZuordnungKlick = new EventEmitter<LohnartZuordnungTabellendarstellung>();

  spalten = [
    { id: 'konto', name: 'Konto' },
    { id: 'bezeichnung', name: 'Bezeichnung' },
    { id: 'betrag', name: 'Betrag' },
    { id: 'einheit', name: 'Einheit' },
  ];

  tabelle: TableData<LohnartZuordnungTabellendarstellung>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenTabAbrechnungLohnartDialogData
  ) {
    this.tabelle = new TableData<LohnartZuordnungTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<LohnartZuordnungTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  oeffneLohnartLogBuch(lohnartZuordnung: LohnartZuordnungTabellendarstellung) {
    this.lohnartZuordnungKlick.emit(lohnartZuordnung);
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<LohnartZuordnungTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.erstelleLohnartZuordnungenInTabellendarstellung();
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['konto'];
    this.tabelle.sortDirs = ['asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleLohnartZuordnungenInTabellendarstellung(): LohnartZuordnungTabellendarstellung[] {
    if (!this.data.lohnartZuordnungen) {
      return [];
    }
    return this.data.lohnartZuordnungen.map((lohnartZuordnung: LohnartZuordnung) => {
      const lohnart: Lohnart = getObjektAusListeDurchId(lohnartZuordnung.lohnartId, this.data.lohnarten) as Lohnart;
      return {
        id: lohnartZuordnung.id,
        arbeitsnachweisId: lohnartZuordnung.arbeitsnachweisId,
        betrag: lohnartZuordnung.betrag,
        einheit: lohnartZuordnung.einheit,
        konto: lohnart.konto,
        bezeichnung: lohnart.bezeichnung,
      };
    });
  }
}

export interface ArbeitsnachweisBearbeitenTabAbrechnungLohnartDialogData {
  lohnartZuordnungen: LohnartZuordnung[];
  lohnarten: Lohnart[];
}
