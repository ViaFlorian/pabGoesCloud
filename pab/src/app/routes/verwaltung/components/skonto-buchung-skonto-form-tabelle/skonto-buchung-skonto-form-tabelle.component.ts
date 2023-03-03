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
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { SkontoTabellendarstellung } from '../../model/skonto-tabellendarstellung';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { getAbgeschlossenIcon } from 'src/app/shared/util/icon.util';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Skonto } from '../../../../shared/model/verwaltung/skonto';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';

@Component({
  selector: 'pab-skonto-buchung-skonto-form-tabelle',
  templateUrl: './skonto-buchung-skonto-form-tabelle.component.html',
  styleUrls: ['./skonto-buchung-skonto-form-tabelle.component.scss'],
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
export class SkontoBuchungSkontoFormTabelleComponent implements AfterViewInit, OnChanges {
  @Input()
  skontoBuchungenListe!: StatusListen;

  @Input()
  skontoBuchungFormGroup!: FormGroup;

  @Input()
  skontoBuchungAuswahlFormGroup!: FormGroup;

  @Input()
  istProjektAusgewaehlt!: boolean;

  @Input()
  abrechnungsmonateAuswahl!: Abrechnungsmonat[];

  @Output()
  summenAktualisierenEvent = new EventEmitter();

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  tabelle: TableData<SkontoTabellendarstellung>;

  spalten = [
    { id: 'referenzmonat', name: 'Referenzmonat' },
    { id: 'wertstellung', name: 'Wertstellung' },
    { id: 'skontoNettoBetrag', name: 'Skonto (Netto)' },
    { id: 'umsatzsteuer', name: 'Umsatzsteuer' },
    { id: 'skontoBruttoBetrag', name: 'Skonto (Brutto)' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: '' },
  ];

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private idGeneratorService: IdGeneratorService,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe
  ) {
    this.tabelle = new TableData<SkontoTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<SkontoTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenSortierung();
    this.initialisiereTabellenDaten();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['istProjektAusgewaehlt']) {
      this.aendereAktivierungsstatusSkontoBuchungFormGroup(changes['istProjektAusgewaehlt'].currentValue);
    }

    if (changes['skontoBuchungenListe']) {
      const skontoBuchungenListe = changes['skontoBuchungenListe'];
      if (
        skontoBuchungenListe.currentValue &&
        skontoBuchungenListe.previousValue &&
        skontoBuchungenListe.currentValue.getAnzeigeListe().length !==
          skontoBuchungenListe.previousValue.getAnzeigeListe().length
      ) {
        this.tabelle.data = this.mapSkontosZuTabellendarstellung(
          this.skontoBuchungenListe.getAnzeigeListe() as Skonto[]
        );
      }
    }
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return getAbgeschlossenIcon(abrechnungsmonat);
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.skontoBuchungFormGroup, komponentenName, erlaubteExp);
  }

  fuegeOderAktualisiereSkontoBuchung() {
    if (this.skontoBuchungFormGroup.invalid) {
      this.skontoBuchungFormGroup.markAllAsTouched();
      return;
    }

    const skonto: Skonto = this.erstelleSkontoAusForm();

    // Aktualisiere Skonto
    if (skonto.id) {
      this.skontoBuchungenListe.aktualisiereElement(skonto);
    } else {
      skonto.id = this.idGeneratorService.generiereId();
      this.skontoBuchungenListe.fuegeNeuesElementHinzu(skonto);
    }

    this.tabelle.data = this.mapSkontosZuTabellendarstellung(this.skontoBuchungenListe.getAnzeigeListe() as Skonto[]);
    this.summenAktualisierenEvent.emit();
    this.leereForm();
  }

  leereForm() {
    this.skontoBuchungFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  loescheSkonto(skontoTabellendarstellung: SkontoTabellendarstellung) {
    const skonto: Skonto = getObjektAusListeDurchId(
      skontoTabellendarstellung.id,
      this.skontoBuchungenListe.getAnzeigeListe()
    ) as Skonto;
    this.skontoBuchungenListe.loescheElement(skonto);
    this.tabelle.data = this.mapSkontosZuTabellendarstellung(this.skontoBuchungenListe.getAnzeigeListe() as Skonto[]);
    this.summenAktualisierenEvent.emit();
  }

  ladeSkontoInForm(skontoTabellendarstellung: SkontoTabellendarstellung) {
    const referenzmonat: Abrechnungsmonat = this.abrechnungsmonateAuswahl.find(
      (element) =>
        element.monat === skontoTabellendarstellung.referenzMonat &&
        element.jahr === skontoTabellendarstellung.referenzJahr
    )!;

    this.skontoBuchungFormGroup.patchValue({
      id: skontoTabellendarstellung.id,
      wertstellung: skontoTabellendarstellung.wertstellung,
      referenzmonat: referenzmonat,
      bemerkung: skontoTabellendarstellung.bemerkung,
      skontoNettoBetrag: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        skontoTabellendarstellung.skontoNettoBetrag
      ),
      umsatzsteuer: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(skontoTabellendarstellung.umsatzsteuer),
      skontoBruttoBetrag: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        skontoTabellendarstellung.skontoBruttoBetrag
      ),
    });
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['referenzmonat', 'wertstellung'];
    this.tabelle.sortDirs = ['desc', 'desc'];

    this.tabelle.updateSortHeaders();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<SkontoTabellendarstellung>(this.sort, true);
    this.tabelle.data = [];
  }

  private aendereAktivierungsstatusSkontoBuchungFormGroup(formOffen: boolean) {
    if (formOffen) {
      this.skontoBuchungFormGroup.enable();
      this.skontoBuchungFormGroup.get('skontoBruttoBetrag')?.disable();
    } else {
      this.skontoBuchungFormGroup.disable();
    }
  }

  private mapSkontosZuTabellendarstellung(skontos: Skonto[]): SkontoTabellendarstellung[] {
    return skontos.map((skonto) => {
      const skontoBruttoBetrag: number = rundeNummerAufZweiNackommastellen(
        skonto.skontoNettoBetrag * (1 + skonto.umsatzsteuer / 100)
      );

      return {
        id: skonto.id,
        referenzJahr: skonto.referenzJahr,
        referenzMonat: skonto.referenzMonat,
        referenzmonat: this.abrechnungsmonatAnzeigeNamePipe.transform({
          monat: skonto.referenzMonat,
          jahr: skonto.referenzJahr,
        }),
        wertstellung: skonto.wertstellung,
        skontoNettoBetrag: skonto.skontoNettoBetrag,
        umsatzsteuer: skonto.umsatzsteuer,
        skontoBruttoBetrag: skontoBruttoBetrag,
        bemerkung: skonto.bemerkung,
      } as SkontoTabellendarstellung;
    });
  }

  private erstelleSkontoAusForm(): Skonto {
    return {
      id: this.skontoBuchungFormGroup.getRawValue().id,
      projektId: this.skontoBuchungAuswahlFormGroup.getRawValue().projekt.id,
      wertstellung: this.skontoBuchungFormGroup.getRawValue().wertstellung,
      referenzMonat: this.skontoBuchungFormGroup.getRawValue().referenzmonat.monat,
      referenzJahr: this.skontoBuchungFormGroup.getRawValue().referenzmonat.jahr,
      skontoNettoBetrag: convertDeStringZuUsNummer(this.skontoBuchungFormGroup.getRawValue().skontoNettoBetrag),
      umsatzsteuer: convertDeStringZuUsNummer(this.skontoBuchungFormGroup.getRawValue().umsatzsteuer),
      bemerkung: this.skontoBuchungFormGroup.getRawValue().bemerkung,
    } as Skonto;
  }
}
