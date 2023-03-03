import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { AuslagenTabellendarstellung } from '../../model/auslagen-tabellendarstellung';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SonstigeProjektkosten } from '../../../../shared/model/projektabrechnung/sonstige-projektkosten';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-auslagen-dialog',
  templateUrl: './projektabrechnung-bearbeiten-tab-auslagen-dialog.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-auslagen-dialog.component.scss'],
})
export class ProjektabrechnungBearbeitenTabAuslagenDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'belegartName', name: 'Belegart' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'kosten', name: 'Betrag (Brutto)' },
  ];

  tabelle: TableData<AuslagenTabellendarstellung>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ProjektabrechnungBearbeitenTabAuslagenDialogData
  ) {
    this.tabelle = new TableData<AuslagenTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AuslagenTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AuslagenTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.erstelleAuslagenTabellenDarstellung();
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.updateSortHeaders();
  }

  erstelleAuslagenTabellenDarstellung(): AuslagenTabellendarstellung[] {
    return this.data.sonstigeProjektkostens.map((sonstigeProjektkosten) => {
      const belegart: Belegart = getObjektAusListeDurchId(
        sonstigeProjektkosten.belegartId,
        this.data.belegarten
      ) as Belegart;

      return {
        belegartName: belegart.textKurz,
        bemerkung: sonstigeProjektkosten.bemerkung,
        kosten: sonstigeProjektkosten.kosten,
      } as AuslagenTabellendarstellung;
    });
  }
}

export interface ProjektabrechnungBearbeitenTabAuslagenDialogData {
  sonstigeProjektkostens: SonstigeProjektkosten[];
  belegarten: Belegart[];
}
