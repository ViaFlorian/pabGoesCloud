import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { ArbeitsnachweisUebersicht } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-uebersicht';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormGroup } from '@angular/forms';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { BearbeitungsstatusEnumZuStatusIdPipe } from '../../../../shared/pipe/bearbeitungsstatus-enum-zu-status-id.pipe';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { InternExternEnum } from '../../../../shared/enum/intern-extern.enum';
import { OffenVorhandenEnum } from '../../../../shared/enum/offen-vorhanden.enum';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { ArbeitsnachweisUebersichtTabellendarstellung } from '../../model/arbeitsnachweis-uebersicht-tabellendarstellung';
import { MitarbeiterAnzeigeNameKurzformPipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name-kurzform.pipe';
import { IsInternToTextPipe } from '../../../../shared/pipe/is-intern-to-text.pipe';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { ArbeitsnachweisBearbeitenQueryParams } from '../../../../shared/model/query-params/arbeitsnachweis-bearbeiten-query-params';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { filterMitOperator } from '../../../../shared/util/filter.util';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { debounceTime } from 'rxjs';
import * as IconUtil from '../../../../shared/util/icon.util';

@Component({
  selector: 'pab-arbeitsnachweis-uebersicht-tabelle',
  templateUrl: './arbeitsnachweis-uebersicht-tabelle.component.html',
  styleUrls: ['./arbeitsnachweis-uebersicht-tabelle.component.scss'],
})
export class ArbeitsnachweisUebersichtTabelleComponent implements OnInit, AfterViewInit, OnChanges {
  @Input()
  arbeitsnachweise: ArbeitsnachweisUebersicht[] = [];

  @Input()
  formGroup!: FormGroup;

  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  @Input()
  sachbearbeiterAuswahl!: Mitarbeiter[];

  @Input()
  filterArbeitsnachweiseSpinner!: SpinnerRef;

  @Output()
  anzahlArbeitsnachweiseEvent = new EventEmitter<number>(true);

  @Output()
  listeAnMitarbeiternEvent = new EventEmitter<string[]>(true);

  @Output()
  arbeitsnachweisLoeschenEvent = new EventEmitter<string>(true);

  eBearbeitungsstatusEnum = BearbeitungsstatusEnum;
  arbeitsnachweiseTabellendarstellung: ArbeitsnachweisUebersichtTabellendarstellung[] = [];

  spalten = [
    { id: 'actionEdit', name: 'Action Edit' },
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'mitarbeiterName', name: 'Mitarbeiter:in' },
    { id: 'internExtern', name: 'Intern/Extern' },
    { id: 'sachbearbeiterName', name: 'Sachbearbeiter:in' },
    { id: 'summeProjektstunden', name: 'Projektstunden' },
    { id: 'summeSpesen', name: 'Spesen u. Zuschläge' },
    { id: 'summeBelege', name: 'Belege u. Auslagen' },
    { id: 'statusId', name: 'Bearbeitungsstatus' },
    { id: 'actionRemove', name: 'Action Remove' },
  ];

  tabelle: TableData<ArbeitsnachweisUebersichtTabellendarstellung>;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private benachrichtigungService: BenachrichtigungService,
    private navigationService: NavigationService,
    private konstantenService: KonstantenService,
    private bearbeitungsstatusEnumZuStatusIdPipe: BearbeitungsstatusEnumZuStatusIdPipe,
    private mitarbeiterAnzeigeNameKurzformPipe: MitarbeiterAnzeigeNameKurzformPipe,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    private isInternToTextPipe: IsInternToTextPipe
  ) {
    this.tabelle = new TableData<ArbeitsnachweisUebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ArbeitsnachweisUebersichtTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngOnInit(): void {
    this.formGroup.valueChanges.pipe(debounceTime(500)).subscribe(() => {
      if (!this.formGroup.valid) {
        return;
      }
      this.filtereArbeitsnachweiseUndAktualisiereTabelle();
    });
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(): void {
    this.aktualisiereArbeitsnachweiseUebersichtTabellendarstellung();
    this.filtereArbeitsnachweiseUndAktualisiereTabelle();
  }

  navigiereZuArbeitsnachweisBearbeiten(
    arbeitsnachweisUebersichtTabellendarstellung: ArbeitsnachweisUebersichtTabellendarstellung
  ): void {
    const queryParams: ArbeitsnachweisBearbeitenQueryParams = {
      jahr: arbeitsnachweisUebersichtTabellendarstellung.jahr,
      monat: arbeitsnachweisUebersichtTabellendarstellung.monat,
      mitarbeiterId: arbeitsnachweisUebersichtTabellendarstellung.mitarbeiter.id,
    };
    const url: string = '/arbeitsnachweis/bearbeiten';
    this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
  }

  onLoeschen(arbeitsnachweisUebersicht: ArbeitsnachweisUebersichtTabellendarstellung, event: MouseEvent): void {
    event.stopPropagation();
    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      arbeitsnachweisUebersicht.mitarbeiter.id,
      this.mitarbeiterAuswahl
    ) as Mitarbeiter;

    const titel: string = 'Folgenden Arbeitsnachweis wirklich löschen?';
    const message: string = `${arbeitsnachweisUebersicht.jahr}/${arbeitsnachweisUebersicht.monat} von ${mitarbeiter.nachname}, ${mitarbeiter.vorname}`;
    const dialogRef = this.benachrichtigungService.erstelleBestaetigungMessage(message, titel);
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }
      this.arbeitsnachweisLoeschenEvent.emit(arbeitsnachweisUebersicht.arbeitsnachweisId);
    });
  }

  getAbgeschlossenIcon(bearbeitungsstatus: BearbeitungsstatusEnum): string {
    return IconUtil.getAbgeschlossenIconMitBoolean(bearbeitungsstatus === BearbeitungsstatusEnum.ABGESCHLOSSEN);
  }

  private filtereArbeitsnachweiseUndAktualisiereTabelle() {
    if (this.arbeitsnachweiseTabellendarstellung.length === 0) {
      this.listeAnMitarbeiternEvent.emit([]);
      return;
    }

    this.filterArbeitsnachweiseSpinner.open();

    let arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[] =
      this.arbeitsnachweiseTabellendarstellung.slice();

    arbeitsnachweiseGefiltert = this.filterNachSachbearbeiter(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachBearbeitungsstatus(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachMitarbeiter(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachOffenVorhanden(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachInternExtern(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachProjektstunden(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachSpesenZuschlaege(arbeitsnachweiseGefiltert);
    arbeitsnachweiseGefiltert = this.filterNachBelegeAuslagen(arbeitsnachweiseGefiltert);

    this.tabelle.data = arbeitsnachweiseGefiltert;
    this.anzahlArbeitsnachweiseEvent.emit(arbeitsnachweiseGefiltert.length);
    this.listeAnMitarbeiternEvent.emit(
      arbeitsnachweiseGefiltert.map((arbeitsnachweis) => arbeitsnachweis.mitarbeiter.id)
    );

    this.filterArbeitsnachweiseSpinner.close();
  }

  private filterNachSachbearbeiter(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const sachbearbeiter: Mitarbeiter = this.formGroup.getRawValue().sachbearbeiter;
    if (!sachbearbeiter || !sachbearbeiter.id) {
      return arbeitsnachweiseGefiltert;
    }

    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return arbeitsnachweisUebersicht.sachbearbeiter?.id === sachbearbeiter.id;
    });
  }

  private filterNachBearbeitungsstatus(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const bearbeitungsstatus: BearbeitungsstatusEnum = this.formGroup.getRawValue().bearbeitungsstatus;
    if (bearbeitungsstatus === BearbeitungsstatusEnum.ALLE) {
      return arbeitsnachweiseGefiltert;
    }

    const statusId: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(bearbeitungsstatus);
    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return arbeitsnachweisUebersicht.statusId === statusId;
    });
  }

  private filterNachMitarbeiter(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const mitarbeiter: Mitarbeiter = this.formGroup.getRawValue().mitarbeiter;
    if (!mitarbeiter || !mitarbeiter.id) {
      return arbeitsnachweiseGefiltert;
    }

    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return arbeitsnachweisUebersicht.mitarbeiter.id === mitarbeiter.id;
    });
  }

  private filterNachOffenVorhanden(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const offenVorhandenEnum: OffenVorhandenEnum = this.formGroup.getRawValue().offenVorhanden;
    switch (offenVorhandenEnum) {
      case OffenVorhandenEnum.ALLE: {
        return arbeitsnachweiseGefiltert;
      }
      case OffenVorhandenEnum.NUR_VORHANDENE: {
        return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
          return arbeitsnachweisUebersicht.arbeitsnachweisId;
        });
      }
      case OffenVorhandenEnum.NUR_OFFENE: {
        return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
          return !arbeitsnachweisUebersicht.arbeitsnachweisId;
        });
      }
    }
  }

  private filterNachInternExtern(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const internExternEnum: InternExternEnum = this.formGroup.getRawValue().internExtern;
    switch (internExternEnum) {
      case InternExternEnum.ALLE: {
        return arbeitsnachweiseGefiltert;
      }
      case InternExternEnum.NUR_INTERNE: {
        return arbeitsnachweiseGefiltert.filter((arbeitsnachweis) => {
          return (
            arbeitsnachweis.mitarbeiter.intern &&
            arbeitsnachweis.mitarbeiter.mitarbeiterTypId === this.konstantenService.getMitarbeiterTypAngestellter().id
          );
        });
      }
      case InternExternEnum.NUR_EXTERNE: {
        return arbeitsnachweiseGefiltert.filter((arbeitsnachweis) => {
          return !arbeitsnachweis.mitarbeiter.intern;
        });
      }
      case InternExternEnum.NUR_STUDENTEN_PRAKTIKANTEN: {
        return arbeitsnachweiseGefiltert.filter((arbeitsnachweis) => {
          return (
            arbeitsnachweis.mitarbeiter.intern &&
            arbeitsnachweis.mitarbeiter.mitarbeiterTypId === this.konstantenService.getMitarbeiterTypStudiPrakti().id
          );
        });
      }
    }
  }

  private filterNachProjektstunden(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const projektstundenFilter: string = this.formGroup.getRawValue().projektstundenFilter;
    if (!projektstundenFilter) {
      return arbeitsnachweiseGefiltert;
    }

    const projektstundenOperator: OperatorEnum = this.formGroup.getRawValue().projektstundenOperator;
    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return filterMitOperator(
        projektstundenOperator,
        arbeitsnachweisUebersicht.summeProjektstunden,
        Number.parseFloat(projektstundenFilter)
      );
    });
  }

  private filterNachSpesenZuschlaege(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const spesenZuschlaegeFilter: string = this.formGroup.getRawValue().spesenZuschlaegeFilter;
    if (!spesenZuschlaegeFilter) {
      return arbeitsnachweiseGefiltert;
    }

    const spesenZuschlaegeOperator: OperatorEnum = this.formGroup.getRawValue().spesenZuschlaegeOperator;
    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return filterMitOperator(
        spesenZuschlaegeOperator,
        arbeitsnachweisUebersicht.summeSpesen,
        Number.parseFloat(spesenZuschlaegeFilter)
      );
    });
  }

  private filterNachBelegeAuslagen(
    arbeitsnachweiseGefiltert: ArbeitsnachweisUebersichtTabellendarstellung[]
  ): ArbeitsnachweisUebersichtTabellendarstellung[] {
    const belegeAuslagenFilter: string = this.formGroup.getRawValue().belegeAuslagenFilter;
    if (!belegeAuslagenFilter) {
      return arbeitsnachweiseGefiltert;
    }

    const belegeAuslagenOperator: OperatorEnum = this.formGroup.getRawValue().belegeAuslagenOperator;
    return arbeitsnachweiseGefiltert.filter((arbeitsnachweisUebersicht) => {
      return filterMitOperator(
        belegeAuslagenOperator,
        arbeitsnachweisUebersicht.summeBelege,
        Number.parseFloat(belegeAuslagenFilter)
      );
    });
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ArbeitsnachweisUebersichtTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.data = this.arbeitsnachweiseTabellendarstellung;
  }

  private initialisiereTabellenSortierung(): void {
    this.tabelle.sortParams = ['abrechnungsmonat', 'mitarbeiterName'];
    this.tabelle.sortDirs = ['desc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private aktualisiereArbeitsnachweiseUebersichtTabellendarstellung(): void {
    this.arbeitsnachweiseTabellendarstellung = this.arbeitsnachweise.map((arbeitsnachweis) => {
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        arbeitsnachweis.mitarbeiterId,
        this.mitarbeiterAuswahl
      ) as Mitarbeiter;
      const sachbearbeiter: Mitarbeiter = getObjektAusListeDurchId(
        arbeitsnachweis.sachbearbeiterId,
        this.sachbearbeiterAuswahl
      ) as Mitarbeiter;
      return {
        arbeitsnachweisId: arbeitsnachweis.arbeitsnachweisId,
        jahr: arbeitsnachweis.jahr,
        monat: arbeitsnachweis.monat,
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(arbeitsnachweis),
        statusId: arbeitsnachweis.statusId,
        mitarbeiter: mitarbeiter,
        mitarbeiterName: this.mitarbeiterAnzeigeNameKurzformPipe.transform(mitarbeiter),
        internExtern: this.isInternToTextPipe.transform(mitarbeiter),
        sachbearbeiter: sachbearbeiter,
        sachbearbeiterName: this.mitarbeiterAnzeigeNameKurzformPipe.transform(sachbearbeiter),
        summeProjektstunden: arbeitsnachweis.summeProjektstunden,
        summeSpesen: arbeitsnachweis.summeSpesen,
        summeBelege: arbeitsnachweis.summeBelege,
      } as ArbeitsnachweisUebersichtTabellendarstellung;
    });
  }
}
