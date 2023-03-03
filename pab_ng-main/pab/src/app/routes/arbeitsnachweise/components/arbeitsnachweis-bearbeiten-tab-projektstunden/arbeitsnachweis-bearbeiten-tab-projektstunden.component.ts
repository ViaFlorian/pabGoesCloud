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
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { Projektstunde } from '../../../../shared/model/arbeitsnachweis/projektstunde';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { convertDeStringZuUsNummer, convertUsNummerZuDeString } from '../../../../shared/util/nummer-converter.util';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { TabellenDarstellungService } from '../../service/tabellen-darstellung.service';
import { ProjektstundeTabellendarstellung } from '../../model/projektstunde-tabellendarstellung';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { konvertiereAbrechnungsmonatToJsDate } from '../../../../shared/util/abrechnungsmonat-to-date.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-projektstunden',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-projektstunden.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-projektstunden.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabProjektstundenComponent implements AfterViewInit, OnChanges {
  @Input()
  projektstundenFormGroup!: FormGroup;

  @Input()
  arbeitsnachweisOffen!: boolean;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  projektstundenListen!: StatusListen;

  @Input()
  abrechnungsmonat!: MitarbeiterAbrechnungsmonat;

  @Input()
  angerechneteReisezeit!: number;

  @Output()
  projektstundenAktualisiert = new EventEmitter();

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'stunden', name: 'Stunden' },
    { id: 'nichtFakturierfaehig', name: 'Nicht Fakturierfaehig' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: 'Actions' },
  ];

  tabelle: TableData<ProjektstundeTabellendarstellung>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private tabellenDarstellungService: TabellenDarstellungService,
    private idGeneratorService: IdGeneratorService,
    private konstantenService: KonstantenService
  ) {
    this.tabelle = new TableData<ProjektstundeTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektstundenListen']) {
      const projektstundenListenChange = changes['projektstundenListen'];
      if (
        projektstundenListenChange.currentValue &&
        projektstundenListenChange.previousValue &&
        projektstundenListenChange.currentValue.getAnzeigeListe().length !==
          projektstundenListenChange.previousValue.getAnzeigeListe().length
      ) {
        if (this.projektstundenListen.getAnzeigeListe().length > 0) {
          this.aktualisiereTabellenDaten();
        } else {
          this.tabelle.data = [];
        }
      }
    }
    if (changes['arbeitsnachweisOffen']) {
      this.aendereAktivierungsstatusProjektstundenFormGroup(changes['arbeitsnachweisOffen'].currentValue);
    }
    if (changes['angerechneteReisezeit']) {
      this.angerechneteReisezeit = changes['angerechneteReisezeit'].currentValue;
    }
  }

  ladeProjektstundenInForm(projektstundeTabelleDarstellung: ProjektstundeTabellendarstellung): void {
    const projektstunde: Projektstunde = getObjektAusListeDurchId(
      projektstundeTabelleDarstellung.id,
      this.projektstundenListen.getAnzeigeListe()
    ) as Projektstunde;
    this.projektstundenFormGroup.patchValue({
      id: projektstunde.id,
      tag: projektstunde.datumVon.getDate(),
      stunden: convertUsNummerZuDeString(projektstunde.anzahlStunden),
      projekt: getObjektAusListeDurchId(projektstunde.projektId, this.projektAuswahl) as Projekt,
      bemerkung: projektstunde.bemerkung,
      nichtFakturierfaehig: projektstunde.nichtFakturierfaehig,
    });
  }

  loescheProjektstunde(projektstundeTabellenDarstellung: ProjektstundeTabellendarstellung) {
    const projektstunde: Projektstunde = getObjektAusListeDurchId(
      projektstundeTabellenDarstellung.id,
      this.projektstundenListen.getAnzeigeListe()
    ) as Projektstunde;
    this.projektstundenListen.loescheElement(projektstunde);
    this.aktualisiereTabellenDaten();
    if (projektstunde.id === this.projektstundenFormGroup.getRawValue().id) {
      this.leereForm();
    }
    this.projektstundenAktualisiert.emit();
  }

  leereForm() {
    this.projektstundenFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  fuegeOderAktualisiereProjektstunde() {
    if (this.projektstundenFormGroup.invalid) {
      this.projektstundenFormGroup.markAllAsTouched();
      return;
    }

    const projektstunden: Projektstunde = this.erstelleProjektstundenAusForm();

    // Projektstunden aktualisieren
    if (projektstunden.id) {
      this.projektstundenListen.aktualisiereElement(projektstunden);
    } else {
      projektstunden.id = this.idGeneratorService.generiereId();
      this.projektstundenListen.fuegeNeuesElementHinzu(projektstunden);
    }

    this.aktualisiereTabellenDaten();

    this.projektstundenFormGroup.reset();
    this.formRef.resetForm();

    // Setze bisher ausgewähltes Projekt wieder im Multiselect
    this.projektstundenFormGroup.patchValue({
      projekt: getObjektAusListeDurchId(projektstunden.projektId, this.projektAuswahl) as Projekt,
    });

    this.projektstundenAktualisiert.emit();
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.projektstundenFormGroup, komponentenName, erlaubteExp);
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(this.sort, true);
    if (this.projektstundenListen.getAnzeigeListe().length > 0) {
      this.aktualisiereTabellenDaten();
    } else {
      this.tabelle.data = [];
    }
  }

  private aktualisiereTabellenDaten() {
    this.tabelle.data = this.tabellenDarstellungService.filtereUndMappeProjektstundenNachProjektstundeTyp(
      this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
      this.konstantenService.getProjektstundenTypNormal(),
      this.projektAuswahl
    );
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['tagVon', 'projektnummer'];
    this.tabelle.sortDirs = ['asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleProjektstundenAusForm(): Projektstunde {
    return {
      id: this.projektstundenFormGroup.getRawValue().id,
      datumVon: konvertiereAbrechnungsmonatToJsDate(
        this.abrechnungsmonat,
        parseInt(this.projektstundenFormGroup.getRawValue().tag)
      ),
      datumBis: null,
      projektId: this.projektstundenFormGroup.getRawValue().projekt.id,
      projektstundeTypId: this.konstantenService.getProjektstundenTypNormal().id,
      anzahlStunden: convertDeStringZuUsNummer(this.projektstundenFormGroup.getRawValue().stunden),
      nichtFakturierfaehig: this.projektstundenFormGroup.getRawValue().nichtFakturierfaehig,
      bemerkung: this.projektstundenFormGroup.getRawValue().bemerkung,
    };
  }

  private aendereAktivierungsstatusProjektstundenFormGroup(anwOffen: boolean) {
    if (anwOffen) {
      this.projektstundenFormGroup.enable();
    } else {
      this.projektstundenFormGroup.disable();
    }
  }
}
