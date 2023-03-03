import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { LohnartberechnungLog } from '../../../../shared/model/arbeitsnachweis/lohnartberechnung-log';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbrechnungLohnartenberechnungLogDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'datum', name: 'Datum' },
    { id: 'meldung', name: 'Erläuterung' },
    { id: 'wert', name: 'Wert' },
    { id: 'einheit', name: 'Einheit' },
  ];

  tabelle: TableData<LohnartberechnungLog>;
  untertitel: string = '';

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenTabAbrechnungLohnartberechnungLogDialogData
  ) {
    this.tabelle = new TableData<LohnartberechnungLog>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<LohnartberechnungLog>(new MatMultiSort(), true);
    this.untertitel = this.erstelleUntertitel();
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<LohnartberechnungLog>(this.sort, true);
    this.tabelle.data = this.data.lohnartberechnungLog;
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['datum'];
    this.tabelle.sortDirs = ['asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleUntertitel() {
    return `Berechnung der Lohnart ${this.data.konto} für Arbeitsnachweis
    ${this.data.jahr}/${this.data.monat} von Mitabeiter:in
    ${this.data.mitarbeiterNachname},${this.data.mitarbeiterVorname}`;
  }
}

export interface ArbeitsnachweisBearbeitenTabAbrechnungLohnartberechnungLogDialogData {
  lohnartberechnungLog: LohnartberechnungLog[];
  konto: string;
  jahr: number;
  monat: number;
  mitarbeiterNachname: string;
  mitarbeiterVorname: string;
}
