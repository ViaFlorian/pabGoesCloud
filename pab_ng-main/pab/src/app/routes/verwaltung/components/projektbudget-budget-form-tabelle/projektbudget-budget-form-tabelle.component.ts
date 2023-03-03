import { AfterViewInit, ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektBudgetTabellendarstellung } from '../../model/projekt-budget-tabellendarstellung';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { ProjektBudget } from '../../../../shared/model/verwaltung/projekt-budget';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { convertDeStringZuUsNummer } from '../../../../shared/util/nummer-converter.util';

@Component({
  selector: 'pab-projektbudget-budget-form-tabelle',
  templateUrl: './projektbudget-budget-form-tabelle.component.html',
  styleUrls: ['./projektbudget-budget-form-tabelle.component.scss'],
  providers: [
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'dd.MM.yyyy',
        },
        display: {
          dateInput: 'dd.MM.yyyy',
          monthYearLabel: 'MMM y',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM y',
        },
      },
    },
  ],
})
export class ProjektbudgetBudgetFormTabelleComponent implements AfterViewInit, OnChanges {
  @Input()
  projektbudgetListe!: StatusListen;

  @Input()
  projektbudgetFormGroup!: FormGroup;

  @Input()
  projektbudgetAuswahlFormGroup!: FormGroup;

  @Input()
  istProjektAusgewaehlt!: boolean;

  @Input()
  projektAlle!: Projekt[];

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  tabelle: TableData<ProjektBudgetTabellendarstellung>;

  spalten = [
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'wertstellung', name: 'Wertstellung' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'budgetBetrag', name: 'Budgetbetrag' },
    { id: 'saldoBerechnet', name: 'Saldo Berechnet' },
  ];

  constructor(private changeDetectorRef: ChangeDetectorRef, private idGeneratorService: IdGeneratorService) {
    this.tabelle = new TableData<ProjektBudgetTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektBudgetTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenSortierung();
    this.initialisiereTabellenDaten();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['istProjektAusgewaehlt']) {
      this.aendereAktivierungsstatusProjektbudgetFormGroup(changes['istProjektAusgewaehlt'].currentValue);
    }

    if (changes['projektbudgetListe']) {
      const fakturenListe = changes['projektbudgetListe'];
      if (
        fakturenListe.currentValue &&
        fakturenListe.previousValue &&
        fakturenListe.currentValue.getAnzeigeListe().length !== fakturenListe.previousValue.getAnzeigeListe().length
      ) {
        this.tabelle.data = this.mapProjektBudgetsZuTabellendarstellung(
          this.projektbudgetListe.getAnzeigeListe() as ProjektBudget[]
        );
      }
    }
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.projektbudgetFormGroup, komponentenName, erlaubteExp);
  }

  fuegeOderAktualisiereProjektBudget() {
    if (this.projektbudgetFormGroup.invalid) {
      this.projektbudgetFormGroup.markAllAsTouched();
      return;
    }

    const projektBudget: ProjektBudget = this.erstelleProjektBudgetAusForm();
    this.projektbudgetListe.fuegeNeuesElementHinzu(projektBudget);

    this.tabelle.data = this.mapProjektBudgetsZuTabellendarstellung(
      this.projektbudgetListe.getAnzeigeListe() as ProjektBudget[]
    );
    this.leereForm();
  }

  leereForm() {
    this.projektbudgetFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['wertstellung'];
    this.tabelle.sortDirs = ['desc'];

    this.tabelle.updateSortHeaders();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektBudgetTabellendarstellung>(this.sort, true);
    this.tabelle.data = [];
  }

  private aendereAktivierungsstatusProjektbudgetFormGroup(formOffen: boolean) {
    formOffen ? this.projektbudgetFormGroup.enable() : this.projektbudgetFormGroup.disable();
  }

  private mapProjektBudgetsZuTabellendarstellung(projektBudgets: ProjektBudget[]): ProjektBudgetTabellendarstellung[] {
    const projektBudgetsTabellendarstellung: ProjektBudgetTabellendarstellung[] = projektBudgets.map(
      (projektBudget) => {
        const projekt: Projekt = getObjektAusListeDurchId(projektBudget.projektId, this.projektAlle) as Projekt;

        return {
          projektnummer: projekt.projektnummer,
          wertstellung: projektBudget.wertstellung,
          bemerkung: projektBudget.bemerkung,
          budgetBetrag: projektBudget.budgetBetrag,
          saldoBerechnet: 0,
        } as ProjektBudgetTabellendarstellung;
      }
    );

    // Sortieren
    projektBudgetsTabellendarstellung.sort(
      (a, b): number => new Date(b.wertstellung).getTime() - new Date(a.wertstellung).getTime()
    );

    // Saldo berechnen
    for (let i = 0; i < projektBudgetsTabellendarstellung.length; i++) {
      projektBudgetsTabellendarstellung[i].saldoBerechnet = projektBudgetsTabellendarstellung
        .slice(i, projektBudgetsTabellendarstellung.length)
        .reduce((sum, element) => sum + element.budgetBetrag, 0);
    }

    return projektBudgetsTabellendarstellung;
  }

  private erstelleProjektBudgetAusForm(): ProjektBudget {
    return {
      id: this.idGeneratorService.generiereId(),
      projektId: this.projektbudgetAuswahlFormGroup.getRawValue().projekt.id,
      wertstellung: this.projektbudgetFormGroup.getRawValue().wertstellung,
      budgetBetrag: convertDeStringZuUsNummer(this.projektbudgetFormGroup.getRawValue().budgetBetrag),
      bemerkung: this.projektbudgetFormGroup.getRawValue().bemerkung,
    } as ProjektBudget;
  }
}
