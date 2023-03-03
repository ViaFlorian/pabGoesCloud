import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { AbwesenheitTabellendarstellung } from '../../model/abwesenheit-tabellendarstellung';
import { Abwesenheit } from '../../../../shared/model/arbeitsnachweis/abwesenheit';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { DatePipe } from '@angular/common';
import { TimeToTextPipe } from '../../../../shared/pipe/time-to-text.pipe';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog',
  templateUrl: './projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog.component.scss'],
})
export class ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'uhrzeitVon', name: 'von' },
    { id: 'uhrzeitBis', name: 'bis' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'spesen', name: 'Spesen' },
    { id: 'zuschlag', name: 'Zuschläge' },
  ];

  tabelle: TableData<AbwesenheitTabellendarstellung>;

  constructor(
    private datePipe: DatePipe,
    private timeToText: TimeToTextPipe,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogData
  ) {
    this.tabelle = new TableData<AbwesenheitTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AbwesenheitTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AbwesenheitTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.erstelleAbwesenheitTabellenDarstellung();
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['tagVon', 'uhrzeitVon'];
    this.tabelle.sortDirs = ['asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleAbwesenheitTabellenDarstellung(): AbwesenheitTabellendarstellung[] {
    return this.data.abwesenheiten.map((abwesenheit) => {
      const tagVon = this.datePipe.transform(abwesenheit.datumVon.toString(), 'dd');
      const projekt: Projekt = getObjektAusListeDurchId(abwesenheit.projektId, this.data.projekte) as Projekt;

      return {
        id: abwesenheit.id,
        tagVon: tagVon ? tagVon : '00',
        uhrzeitVon: this.timeToText.transform(abwesenheit.datumVon),
        uhrzeitBis: this.timeToText.transform(abwesenheit.datumBis),
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        spesen: abwesenheit.spesen ? abwesenheit.spesen : 0,
        zuschlag: abwesenheit.zuschlag ? abwesenheit.zuschlag : 0,
      };
    });
  }
}

export interface ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogData {
  abwesenheiten: Abwesenheit[];
  projekte: Projekt[];
}
