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
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { getAbgeschlossenIcon } from 'src/app/shared/util/icon.util';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { FakturTabellendarstellung } from '../../model/faktur-tabellendarstellung';
import { Faktur } from '../../../../shared/model/verwaltung/faktur';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import {
  getObjektAusListeDurchId,
  getObjektAusListeDurchScribeId,
} from '../../../../shared/util/objekt-in-array-finden.util';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';

@Component({
  selector: 'pab-fakturuebersicht-faktur-form-tabelle',
  templateUrl: './fakturuebersicht-faktur-form-tabelle.component.html',
  styleUrls: ['./fakturuebersicht-faktur-form-tabelle.component.scss'],
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
export class FakturuebersichtFakturFormTabelleComponent implements AfterViewInit, OnChanges {
  @Input()
  fakturenListe!: StatusListen;

  @Input()
  fakturFormGroup!: FormGroup;

  @Input()
  fakturAuswahlFormGroup!: FormGroup;

  @Input()
  istProjektAusgewaehlt!: boolean;

  @Input()
  kundeAuswahl!: Kunde[];

  @Input()
  abrechnungsmonateAuswahl!: Abrechnungsmonat[];

  @Output()
  rechnungssummeAktualisierenEvent = new EventEmitter();

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  tabelle: TableData<FakturTabellendarstellung>;

  spalten = [
    { id: 'referenzmonat', name: 'Referenzmonat' },
    { id: 'rechnungsdatum', name: 'Rechnungsdatum' },
    { id: 'rechnungsnummer', name: 'Rechnungsnummer' },
    { id: 'abwRechnungsempfaenger', name: 'Abw. Rechnungsempfänger' },
    { id: 'debitorennummer', name: 'Debitorennummer' },
    { id: 'betragNetto', name: 'Betrag (Netto)' },
    { id: 'nichtBudgetRelevant', name: 'Davon nicht budgetrelevant' },
    { id: 'umsatzsteuer', name: 'Umsatzsteuer' },
    { id: 'betragBrutto', name: 'Betrag (Brutto)' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: '' },
  ];

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private idGeneratorService: IdGeneratorService,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe
  ) {
    this.tabelle = new TableData<FakturTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<FakturTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenSortierung();
    this.initialisiereTabellenDaten();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['istProjektAusgewaehlt']) {
      this.aendereAktivierungsstatusFakturFormGroup(changes['istProjektAusgewaehlt'].currentValue);
    }

    if (changes['fakturenListe']) {
      const fakturenListe = changes['fakturenListe'];
      if (
        fakturenListe.currentValue &&
        fakturenListe.previousValue &&
        fakturenListe.currentValue.getAnzeigeListe().length !== fakturenListe.previousValue.getAnzeigeListe().length
      ) {
        this.tabelle.data = this.mapFakturenZuTabellendarstellung(this.fakturenListe.getAnzeigeListe() as Faktur[]);
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
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.fakturFormGroup, komponentenName, erlaubteExp);
  }

  fuegeOderAktualisiereFaktur() {
    if (this.fakturFormGroup.invalid) {
      this.fakturFormGroup.markAllAsTouched();
      return;
    }

    const faktur: Faktur = this.erstelleFakturAusForm();

    // Aktualisiere Faktur
    if (faktur.id) {
      this.fakturenListe.aktualisiereElement(faktur);
    } else {
      faktur.id = this.idGeneratorService.generiereId();
      this.fakturenListe.fuegeNeuesElementHinzu(faktur);
    }

    this.tabelle.data = this.mapFakturenZuTabellendarstellung(this.fakturenListe.getAnzeigeListe() as Faktur[]);
    this.rechnungssummeAktualisierenEvent.emit();
    this.leereForm();
  }

  leereForm() {
    this.fakturFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  loescheFaktur(fakturTabellendarstellung: FakturTabellendarstellung) {
    const faktur: Faktur = getObjektAusListeDurchId(
      fakturTabellendarstellung.id,
      this.fakturenListe.getAnzeigeListe()
    ) as Faktur;
    this.fakturenListe.loescheElement(faktur);
    this.tabelle.data = this.mapFakturenZuTabellendarstellung(this.fakturenListe.getAnzeigeListe() as Faktur[]);
    this.rechnungssummeAktualisierenEvent.emit();
  }

  ladeFakturInForm(fakturTabellendarstellung: FakturTabellendarstellung) {
    const kunde: Kunde = getObjektAusListeDurchScribeId(
      fakturTabellendarstellung.abweichenderRechnungsempfaengerKundeId,
      this.kundeAuswahl
    ) as Kunde;
    const referenzmonat: Abrechnungsmonat = this.abrechnungsmonateAuswahl.find(
      (element) =>
        element.monat === fakturTabellendarstellung.referenzMonat &&
        element.jahr === fakturTabellendarstellung.referenzJahr
    )!;

    this.fakturFormGroup.patchValue({
      id: fakturTabellendarstellung.id,
      rechnungsdatum: fakturTabellendarstellung.rechnungsdatum,
      rechnungsnummer: fakturTabellendarstellung.rechnungsnummer,
      referenzmonat: referenzmonat,
      betragNetto: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(fakturTabellendarstellung.betragNetto),
      nichtBudgetRelevant: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        fakturTabellendarstellung.nichtBudgetRelevant
      ),
      umsatzsteuer: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(fakturTabellendarstellung.umsatzsteuer),
      abwRechnungsempfaenger: kunde ? kunde : '',
      debitorennummer: fakturTabellendarstellung.debitorennummer,
      bemerkung: fakturTabellendarstellung.bemerkung,
    });
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['referenzmonat', 'rechnungsnummer'];
    this.tabelle.sortDirs = ['desc', 'desc'];

    this.tabelle.updateSortHeaders();
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<FakturTabellendarstellung>(this.sort, true);
    this.tabelle.data = [];
  }

  private aendereAktivierungsstatusFakturFormGroup(formOffen: boolean) {
    if (formOffen) {
      this.fakturFormGroup.enable();
      this.fakturFormGroup.get('debitorennummer')?.disable();
    } else {
      this.fakturFormGroup.disable();
    }
  }

  private mapFakturenZuTabellendarstellung(fakturen: Faktur[]): FakturTabellendarstellung[] {
    return fakturen.map((faktur) => {
      const kunde: Kunde = getObjektAusListeDurchScribeId(
        faktur.abweichenderRechnungsempfaengerKundeId,
        this.kundeAuswahl
      ) as Kunde;
      const betragBrutto: number = rundeNummerAufZweiNackommastellen(
        faktur.betragNetto * (1 + faktur.umsatzsteuer / 100)
      );

      return {
        id: faktur.id,
        referenzMonat: faktur.referenzMonat,
        referenzJahr: faktur.referenzJahr,
        referenzmonat: this.abrechnungsmonatAnzeigeNamePipe.transform({
          monat: faktur.referenzMonat,
          jahr: faktur.referenzJahr,
        }),
        rechnungsdatum: faktur.rechnungsdatum,
        rechnungsnummer: faktur.rechnungsnummer,
        abweichenderRechnungsempfaengerKundeId: faktur.abweichenderRechnungsempfaengerKundeId,
        abwRechnungsempfaenger: kunde ? kunde.bezeichnung : '',
        debitorennummer: kunde ? kunde.debitorennummer : '',
        betragNetto: faktur.betragNetto,
        nichtBudgetRelevant: faktur.nichtBudgetRelevant,
        umsatzsteuer: faktur.umsatzsteuer,
        betragBrutto: betragBrutto,
        bemerkung: faktur.bemerkung,
      } as FakturTabellendarstellung;
    });
  }

  private erstelleFakturAusForm(): Faktur {
    return {
      id: this.fakturFormGroup.getRawValue().id,
      projektId: this.fakturAuswahlFormGroup.getRawValue().projekt.id,
      referenzMonat: this.fakturFormGroup.getRawValue().referenzmonat.monat,
      referenzJahr: this.fakturFormGroup.getRawValue().referenzmonat.jahr,
      rechnungsdatum: this.fakturFormGroup.getRawValue().rechnungsdatum,
      betragNetto: convertDeStringZuUsNummer(this.fakturFormGroup.getRawValue().betragNetto),
      rechnungsnummer: this.fakturFormGroup.getRawValue().rechnungsnummer,
      nichtBudgetRelevant: convertDeStringZuUsNummer(this.fakturFormGroup.getRawValue().nichtBudgetRelevant),
      abweichenderRechnungsempfaengerKundeId: this.fakturFormGroup.getRawValue().abwRechnungsempfaenger
        ? this.fakturFormGroup.getRawValue().abwRechnungsempfaenger.scribeId
        : '',
      umsatzsteuer: convertDeStringZuUsNummer(this.fakturFormGroup.getRawValue().umsatzsteuer),
      bemerkung: this.fakturFormGroup.getRawValue().bemerkung,
    } as Faktur;
  }
}
