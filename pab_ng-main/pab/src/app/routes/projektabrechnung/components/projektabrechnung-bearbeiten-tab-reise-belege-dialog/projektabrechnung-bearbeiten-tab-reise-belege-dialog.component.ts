import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { Beleg } from '../../../../shared/model/arbeitsnachweis/beleg';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { DatePipe } from '@angular/common';
import { BelegTabellendarstellung } from '../../../../shared/model/tabellendarstellung/beleg-tabellendarstellung';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-reise-belege-dialog',
  templateUrl: './projektabrechnung-bearbeiten-tab-reise-belege-dialog.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-reise-belege-dialog.component.scss'],
})
export class ProjektabrechnungBearbeitenTabReiseBelegeDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'tag', name: 'Tag' },
    { id: 'einsatzort', name: 'Einsatzort' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'belegartName', name: 'Belegart' },
    { id: 'betrag', name: 'Betrag' },
    { id: 'bemerkung', name: 'Bemerkung' },
  ];

  tabelle: TableData<BelegTabellendarstellung>;

  constructor(
    private datePipe: DatePipe,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ProjektabrechnungBearbeitenTabReiseBelegeDialogData
  ) {
    this.tabelle = new TableData<BelegTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<BelegTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<BelegTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.erstelleBelegeTabellenDarstellung();
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['tag', 'projektnummer', 'belegart'];
    this.tabelle.sortDirs = ['asc', 'asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleBelegeTabellenDarstellung(): BelegTabellendarstellung[] {
    return this.data.belege.map((beleg) => {
      const datum = this.datePipe.transform(beleg.datum.toString(), 'dd');
      const projekt: Projekt = getObjektAusListeDurchId(beleg.projektId, this.data.projekte) as Projekt;

      return {
        id: beleg.id,
        datum: datum ? datum : '00',
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        belegartName: (getObjektAusListeDurchId(beleg.belegartId, this.data.belegarten) as Belegart).textKurz,
        betrag: beleg.betrag,
        kilometer: beleg.kilometer,
        arbeitsstaette: beleg.arbeitsstaette,
        belegNr: beleg.belegNr,
        bemerkung: beleg.bemerkung,
      };
    });
  }
}

export interface ProjektabrechnungBearbeitenTabReiseBelegeDialogData {
  belege: Beleg[];
  projekte: Projekt[];
  belegarten: Belegart[];
}
