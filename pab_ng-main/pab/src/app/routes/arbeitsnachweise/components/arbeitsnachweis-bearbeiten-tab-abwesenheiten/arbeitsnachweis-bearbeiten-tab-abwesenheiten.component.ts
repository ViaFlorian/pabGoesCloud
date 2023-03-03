import { AfterViewInit, ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { Abwesenheit } from '../../../../shared/model/arbeitsnachweis/abwesenheit';
import { VonBisDatumErrorStateMatcher } from '../../validation/von-bis-datum-error-state-matcher';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { AbwesenheitTabellendarstellung } from '../../model/abwesenheit-tabellendarstellung';
import { TimeToTextPipe } from '../../../../shared/pipe/time-to-text.pipe';
import { DatePipe } from '@angular/common';
import {
  erzeugeDatumMit2400Fall,
  transformiereZu2400FallFuerMinuten,
  transformiereZu2400FallFuerStunde,
} from '../../util/uhrzeit.util';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { MatDialog } from '@angular/material/dialog';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { konvertiereAbrechnungsmonatToJsDate } from '../../../../shared/util/abrechnungsmonat-to-date.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-abwesenheiten',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abwesenheiten.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abwesenheiten.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbwesenheitenComponent implements AfterViewInit, OnChanges {
  @Input()
  abwesenheitenFormGroup!: FormGroup;

  @Input()
  arbeitsnachweisOffen!: boolean;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  einsatzortAuswahl!: string[];

  @Input()
  abrechnungsmonat!: MitarbeiterAbrechnungsmonat;

  @Input()
  abwesenheitsListen!: StatusListen;

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'uhrzeitVon', name: 'von' },
    { id: 'uhrzeitBis', name: 'bis' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'einsatzort', name: 'Einsatzort' },
    { id: 'mitFruehstueck', name: 'Fruehstueck' },
    { id: 'mitMitagessen', name: 'Mitagessen' },
    { id: 'mitAbendessen', name: 'Abendessen' },
    { id: 'mitUebernachtung', name: 'Uebernachtung' },
    { id: 'dreiMonatsRegelAktiv', name: 'Dreimonatsregel' },
    { id: 'bemerkung', name: 'bemerkung' },
    { id: 'actions', name: 'Actions' },
  ];

  tabelle: TableData<AbwesenheitTabellendarstellung>;

  vonBisAngabenValidMatcher: VonBisDatumErrorStateMatcher = new VonBisDatumErrorStateMatcher();

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private timeToText: TimeToTextPipe,
    private datePipe: DatePipe,
    private idGeneratorService: IdGeneratorService,
    public dialog: MatDialog
  ) {
    this.tabelle = new TableData<AbwesenheitTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AbwesenheitTabellendarstellung>(new MatMultiSort(), true);
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['arbeitsnachweisOffen']) {
      this.aendereAktivierungsstatusAbwesenheitenFormGroup(changes['arbeitsnachweisOffen'].currentValue);
    }

    if (changes['abwesenheitsListen']) {
      const abwesenheitsListen = changes['abwesenheitsListen'];
      if (
        abwesenheitsListen.currentValue &&
        abwesenheitsListen.previousValue &&
        abwesenheitsListen.currentValue.getAnzeigeListe().length !==
          abwesenheitsListen.previousValue.getAnzeigeListe().length
      ) {
        this.tabelle.data = this.mapAbwesenheitZuTabellendarstellung(
          this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[]
        );
      }
    }
  }

  fuegeOderAktualisiereAbwesenheit() {
    if (this.abwesenheitenFormGroup.invalid) {
      this.abwesenheitenFormGroup.markAllAsTouched();
      return;
    }
    if (this.existiertZeitKollisionMitBestehendenDaten()) {
      this.abwesenheitenFormGroup.setErrors({ datumKollidiert: true });
      return;
    }

    const abwesenheiten: Abwesenheit[] = this.erstelleAbwesenheitenAusForm();
    this.aktualisiereStatusListen(abwesenheiten);
    this.tabelle.data = this.mapAbwesenheitZuTabellendarstellung(
      this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[]
    );

    const projektnummer = this.abwesenheitenFormGroup.getRawValue().projekt.id;
    this.leereForm();
    this.abwesenheitenFormGroup.patchValue({
      projekt: getObjektAusListeDurchId(projektnummer.toString(), this.projektAuswahl) as Projekt,
    });
  }

  leereForm() {
    this.abwesenheitenFormGroup.reset();
    setTimeout(() => {
      this.formRef.resetForm();
      this.aktiviereAbwesenheitenOptionen();
    }, 100);
  }

  loescheAbwesenheit(abwesenheitTabellenDarstellung: AbwesenheitTabellendarstellung) {
    const abwesenheit: Abwesenheit = getObjektAusListeDurchId(
      abwesenheitTabellenDarstellung.id,
      this.abwesenheitsListen.getAnzeigeListe()
    ) as Abwesenheit;
    this.abwesenheitsListen.loescheElement(abwesenheit);
    this.tabelle.data = this.mapAbwesenheitZuTabellendarstellung(
      this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[]
    );

    if (abwesenheit.id === this.abwesenheitenFormGroup.getRawValue().id) {
      this.leereForm();
    }
  }

  ladeAbwesenheitInForm(abwesenheitTabellenDarstellung: AbwesenheitTabellendarstellung) {
    const abwesenheit: Abwesenheit = getObjektAusListeDurchId(
      abwesenheitTabellenDarstellung.id,
      this.abwesenheitsListen.getAnzeigeListe()
    ) as Abwesenheit;
    this.abwesenheitenFormGroup.patchValue({
      id: abwesenheit.id,
      tagVon: abwesenheit.datumVon.getDate(),
      hhVon: transformiereZu2400FallFuerStunde(abwesenheit.datumVon),
      mmVon: transformiereZu2400FallFuerMinuten(abwesenheit.datumVon),
      tagBis: abwesenheit.datumBis.getDate(),
      hhBis: transformiereZu2400FallFuerStunde(abwesenheit.datumBis),
      mmBis: transformiereZu2400FallFuerMinuten(abwesenheit.datumBis),
      projekt: getObjektAusListeDurchId(abwesenheit.projektId, this.projektAuswahl) as Projekt,
      einsatzort: abwesenheit.arbeitsstaette,
      bemerkung: abwesenheit.bemerkung,
      dreimonatsregelAktiv: abwesenheit.dreiMonatsRegelAktiv,
      mitUebernachtung: abwesenheit.mitUebernachtung,
      mitFruehstueck: abwesenheit.mitFruehstueck,
      mitMitagessen: abwesenheit.mitMittagessen,
      mitAbendessen: abwesenheit.mitAbendessen,
    });
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.abwesenheitenFormGroup, komponentenName, erlaubteExp);
  }

  onVerlasseTagBis() {
    this.aendereAktivierungsstatusAbwesenheitenOptionen();
  }

  onVerlasseTagVon() {
    if (
      this.abwesenheitenFormGroup.getRawValue().tagBis === null ||
      this.abwesenheitenFormGroup.getRawValue().tagBis === ''
    ) {
      this.abwesenheitenFormGroup.patchValue({
        tagBis: this.abwesenheitenFormGroup.getRawValue().tagVon,
      });
    } else {
      this.aendereAktivierungsstatusAbwesenheitenOptionen();
    }
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<AbwesenheitTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.mapAbwesenheitZuTabellendarstellung(
      this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[]
    );
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['tagVon', 'uhrzeitVon'];
    this.tabelle.sortDirs = ['asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private aendereAktivierungsstatusAbwesenheitenOptionen() {
    const tagBis = this.abwesenheitenFormGroup.getRawValue().tagBis;
    const tagVon = this.abwesenheitenFormGroup.getRawValue().tagVon;
    if (tagBis === '' || tagVon === '') {
      return;
    }

    if (tagBis === tagVon) {
      this.aktiviereAbwesenheitenOptionen();
    } else {
      this.deaktiviereUndSetzeZurueckAbwOptionen();
    }
  }

  private existiertZeitKollisionMitBestehendenDaten(): boolean {
    const von = this.erzeugeDateMitAllenFeldernAusFormGroup(true);
    const bis = this.erzeugeDateMitAllenFeldernAusFormGroup(false);
    return !!(this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[]).find((abwesenheit) =>
      this.hatAbwesenheitUeberschneidungMitDateRange(abwesenheit, von, bis)
    );
  }

  private hatAbwesenheitUeberschneidungMitDateRange(abwesenheit: Abwesenheit, von: Date, bis: Date) {
    if (abwesenheit.id === this.abwesenheitenFormGroup.getRawValue().id) {
      return false;
    }
    const vonVergleich = konvertiereAbrechnungsmonatToJsDate(
      this.abrechnungsmonat,
      abwesenheit.datumVon.getDate(),
      abwesenheit.datumVon.getHours(),
      abwesenheit.datumVon.getMinutes()
    );

    const bisVergleich = konvertiereAbrechnungsmonatToJsDate(
      this.abrechnungsmonat,
      abwesenheit.datumBis.getDate(),
      abwesenheit.datumBis.getHours(),
      abwesenheit.datumBis.getMinutes()
    );

    return !(bis < vonVergleich || von > bisVergleich);
  }

  private erzeugeDateMitAllenFeldernAusFormGroup(vonFelder: boolean): Date {
    const timeSpez = vonFelder ? 'Von' : 'Bis';
    const tag = parseInt(this.abwesenheitenFormGroup.getRawValue()['tag' + timeSpez]);
    const hh = parseInt(this.abwesenheitenFormGroup.getRawValue()['hh' + timeSpez]);
    const mm = parseInt(this.abwesenheitenFormGroup.getRawValue()['mm' + timeSpez]);

    return erzeugeDatumMit2400Fall(this.abrechnungsmonat, tag, hh, mm);
  }

  private erzeugeDateFuerTagMit2400FormGroup(vonFelder: boolean, tagDiff?: number): Date {
    const timeSpez = vonFelder ? 'Von' : 'Bis';
    tagDiff = tagDiff ? tagDiff : 0;
    const tag = parseInt(this.abwesenheitenFormGroup.getRawValue()['tag' + timeSpez]) + tagDiff;
    const hh = 24;
    const mm = 0;

    return erzeugeDatumMit2400Fall(this.abrechnungsmonat, tag, hh, mm);
  }

  private erzeugeDateFuerTagMit0UhrFormGroup(vonFelder: boolean, tagDiff?: number): Date {
    const timeSpez = vonFelder ? 'Von' : 'Bis';
    tagDiff = tagDiff ? tagDiff : 0;
    const tag = parseInt(this.abwesenheitenFormGroup.getRawValue()['tag' + timeSpez]) + tagDiff;
    const hh = 0;
    const mm = 0;

    return erzeugeDatumMit2400Fall(this.abrechnungsmonat, tag, hh, mm);
  }

  private erstelleAbwesenheitenAusForm() {
    const abwesenheiten: Abwesenheit[] = [];
    const anzahlTage =
      parseInt(this.abwesenheitenFormGroup.getRawValue().tagBis) -
      parseInt(this.abwesenheitenFormGroup.getRawValue().tagVon);

    if (anzahlTage === 0) {
      abwesenheiten.push(this.erstelleEinzelTagAbwesenheitAusForm());
      return abwesenheiten;
    }

    abwesenheiten.push(this.erstelleStartTagAbwesenheitAusForm());

    for (let i = 1; i < anzahlTage; i++) {
      abwesenheiten.push(this.erstelleMittelTagAbwesenheitAusForm(i));
    }

    abwesenheiten.push(this.erstelleEndTagAbwesenheitAusForm());

    return abwesenheiten;
  }

  private erstelleEinzelTagAbwesenheitAusForm(): Abwesenheit {
    return {
      id: this.abwesenheitenFormGroup.getRawValue().id,
      datumVon: this.erzeugeDateMitAllenFeldernAusFormGroup(true),
      datumBis: this.erzeugeDateMitAllenFeldernAusFormGroup(false),
      projektId: this.abwesenheitenFormGroup.getRawValue().projekt.id,
      arbeitsstaette: this.abwesenheitenFormGroup.getRawValue().einsatzort,
      bemerkung: this.abwesenheitenFormGroup.getRawValue().bemerkung,
      dreiMonatsRegelAktiv: this.abwesenheitenFormGroup.getRawValue().dreimonatsregelAktiv,
      mitUebernachtung: this.abwesenheitenFormGroup.getRawValue().mitUebernachtung,
      mitFruehstueck: this.abwesenheitenFormGroup.getRawValue().mitFruehstueck,
      mitMittagessen: this.abwesenheitenFormGroup.getRawValue().mitMitagessen,
      mitAbendessen: this.abwesenheitenFormGroup.getRawValue().mitAbendessen,
    };
  }

  private erstelleStartTagAbwesenheitAusForm(): Abwesenheit {
    return {
      id: '',
      datumVon: this.erzeugeDateMitAllenFeldernAusFormGroup(true),
      datumBis: this.erzeugeDateFuerTagMit2400FormGroup(false),
      projektId: this.abwesenheitenFormGroup.getRawValue().projekt.id,
      arbeitsstaette: this.abwesenheitenFormGroup.getRawValue().einsatzort,
      bemerkung: this.abwesenheitenFormGroup.getRawValue().bemerkung,
      dreiMonatsRegelAktiv: this.abwesenheitenFormGroup.getRawValue().dreimonatsregelAktiv,
      mitUebernachtung: true,
      mitFruehstueck: false,
      mitMittagessen: false,
      mitAbendessen: false,
    };
  }

  private erstelleMittelTagAbwesenheitAusForm(tagDiff: number): Abwesenheit {
    return {
      id: '',
      datumVon: this.erzeugeDateFuerTagMit0UhrFormGroup(true, tagDiff),
      datumBis: this.erzeugeDateFuerTagMit2400FormGroup(false, tagDiff),
      projektId: this.abwesenheitenFormGroup.getRawValue().projekt.id,
      arbeitsstaette: this.abwesenheitenFormGroup.getRawValue().einsatzort,
      bemerkung: this.abwesenheitenFormGroup.getRawValue().bemerkung,
      dreiMonatsRegelAktiv: this.abwesenheitenFormGroup.getRawValue().dreimonatsregelAktiv,
      mitUebernachtung: true,
      mitFruehstueck: true,
      mitMittagessen: false,
      mitAbendessen: false,
    };
  }

  private erstelleEndTagAbwesenheitAusForm(): Abwesenheit {
    return {
      id: '',
      datumVon: this.erzeugeDateFuerTagMit0UhrFormGroup(true),
      datumBis: this.erzeugeDateMitAllenFeldernAusFormGroup(false),
      projektId: this.abwesenheitenFormGroup.getRawValue().projekt.id,
      arbeitsstaette: this.abwesenheitenFormGroup.getRawValue().einsatzort,
      bemerkung: this.abwesenheitenFormGroup.getRawValue().bemerkung,
      dreiMonatsRegelAktiv: this.abwesenheitenFormGroup.getRawValue().dreimonatsregelAktiv,
      mitUebernachtung: false,
      mitFruehstueck: true,
      mitMittagessen: false,
      mitAbendessen: false,
    };
  }

  private aktualisiereStatusListen(abwesenheitenListe: Abwesenheit[]) {
    if (this.abwesenheitenFormGroup.getRawValue().id) {
      if (abwesenheitenListe.length === 1) {
        const abw = abwesenheitenListe.pop();
        if (abw) {
          this.abwesenheitsListen.aktualisiereElement(abw);
        }
        return;
      } else {
        const abwZuLoeschen = this.abwesenheitsListen
          .getAnzeigeListe()
          .find((o) => o.id === this.abwesenheitenFormGroup.getRawValue().id);
        if (abwZuLoeschen) {
          this.abwesenheitsListen.loescheElement(abwZuLoeschen);
        }
      }
    }
    for (const abwesenheit of abwesenheitenListe) {
      abwesenheit.id = this.idGeneratorService.generiereId();
      this.abwesenheitsListen.fuegeNeuesElementHinzu(abwesenheit);
    }
  }

  private aktiviereAbwesenheitenOptionen() {
    this.abwesenheitenFormGroup.controls['mitUebernachtung'].enable();
    this.abwesenheitenFormGroup.controls['mitFruehstueck'].enable();
    this.abwesenheitenFormGroup.controls['mitMitagessen'].enable();
    this.abwesenheitenFormGroup.controls['mitAbendessen'].enable();
  }

  private deaktiviereUndSetzeZurueckAbwOptionen() {
    this.abwesenheitenFormGroup.controls['mitUebernachtung'].disable();
    this.abwesenheitenFormGroup.controls['mitFruehstueck'].disable();
    this.abwesenheitenFormGroup.controls['mitMitagessen'].disable();
    this.abwesenheitenFormGroup.controls['mitAbendessen'].disable();

    this.abwesenheitenFormGroup.controls['mitUebernachtung'].reset();
    this.abwesenheitenFormGroup.controls['mitFruehstueck'].reset();
    this.abwesenheitenFormGroup.controls['mitMitagessen'].reset();
    this.abwesenheitenFormGroup.controls['mitAbendessen'].reset();
  }

  private aendereAktivierungsstatusAbwesenheitenFormGroup(anwOffen: boolean) {
    if (anwOffen) {
      this.abwesenheitenFormGroup.enable();
    } else {
      this.abwesenheitenFormGroup.disable();
    }
  }

  private mapAbwesenheitZuTabellendarstellung(abwesenheiten: Abwesenheit[]): AbwesenheitTabellendarstellung[] {
    return abwesenheiten.map((abwesenheit) => {
      const tagVon = this.datePipe.transform(abwesenheit.datumVon.toString(), 'dd');
      const projekt: Projekt = getObjektAusListeDurchId(abwesenheit.projektId, this.projektAuswahl) as Projekt;

      return {
        id: abwesenheit.id,
        tagVon: tagVon ? tagVon : '00',
        uhrzeitVon: this.timeToText.transform(abwesenheit.datumVon),
        uhrzeitBis: this.timeToText.transform(abwesenheit.datumBis),
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        arbeitsstaette: abwesenheit.arbeitsstaette,
        bemerkung: abwesenheit.bemerkung,
        dreiMonatsRegelAktiv: abwesenheit.dreiMonatsRegelAktiv,
        mitUebernachtung: abwesenheit.mitUebernachtung,
        mitFruehstueck: abwesenheit.mitFruehstueck,
        mitMitagessen: abwesenheit.mitMittagessen,
        mitAbendessen: abwesenheit.mitAbendessen,
      };
    });
  }
}
