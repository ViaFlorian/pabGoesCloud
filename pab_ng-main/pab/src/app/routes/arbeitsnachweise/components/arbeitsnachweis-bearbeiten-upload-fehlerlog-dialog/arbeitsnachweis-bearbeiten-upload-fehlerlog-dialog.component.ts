import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { Fehlerlog } from '../../../../shared/model/arbeitsnachweis/fehlerlog';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'blatt', name: 'Blatt' },
    { id: 'zelle', name: 'Position' },
    { id: 'fehlertext', name: 'Meldung' },
    { id: 'fehlerklasse', name: 'Fehlerklasse' },
  ];

  tabelle: TableData<Fehlerlog>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenUploadFehlerlogDialogData
  ) {
    this.tabelle = new TableData<Fehlerlog>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<Fehlerlog>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<Fehlerlog>(this.sort, true);
    this.tabelle.data = this.data.fehlerlog;
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['blatt', 'fehlerklasse'];
    this.tabelle.sortDirs = ['asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }
}

export interface ArbeitsnachweisBearbeitenUploadFehlerlogDialogData {
  fehlerlog: Fehlerlog[];
}
