import { AfterViewInit, ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { DreiMonatsRegel } from '../../../../shared/model/arbeitsnachweis/drei-monats-regel';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DreiMonatsRegelTabellendarstellung } from '../../model/drei-monats-regel-tabellendarstellung';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { getObjektAusListeDurchScribeId } from '../../../../shared/util/objekt-in-array-finden.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbwesenheitenDreimonatsregelDialogComponent implements AfterViewInit {
  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'kundeBezeichnung', name: 'Kunde' },
    { id: 'arbeitsstaette', name: 'Einsatzort' },
    { id: 'gueltigVon', name: 'Gültig von' },
    { id: 'gueltigBis', name: 'Gültig bis' },
  ];

  tabelle: TableData<DreiMonatsRegelTabellendarstellung>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenTabAbwesenheitDreimonatsregelDialogData
  ) {
    this.tabelle = new TableData<DreiMonatsRegelTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<DreiMonatsRegelTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<DreiMonatsRegelTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.erstelleDreiMonatsRegelTabellenDarstellung();
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['kundeBezeichnung'];
    this.tabelle.sortDirs = ['asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleDreiMonatsRegelTabellenDarstellung(): DreiMonatsRegelTabellendarstellung[] {
    if (!this.data.dreiMonatsRegeln || this.data.dreiMonatsRegeln.length === 0) {
      return [];
    }
    return this.data.dreiMonatsRegeln.map((dreiMonatsRegel: DreiMonatsRegel) => {
      const kunde: Kunde = getObjektAusListeDurchScribeId(
        dreiMonatsRegel.kundeScribeId,
        this.data.kundeAuswahl
      ) as Kunde;
      return {
        kundeBezeichnung: kunde ? kunde.bezeichnung : '',
        arbeitsstaette: dreiMonatsRegel.arbeitsstaette,
        gueltigVon: dreiMonatsRegel.gueltigVon,
        gueltigBis: dreiMonatsRegel.gueltigBis,
      };
    });
  }
}

export interface ArbeitsnachweisBearbeitenTabAbwesenheitDreimonatsregelDialogData {
  dreiMonatsRegeln: DreiMonatsRegel[];
  kundeAuswahl: Kunde[];
  mitarbeiter: Mitarbeiter | undefined;
}
