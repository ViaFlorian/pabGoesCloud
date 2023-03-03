import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { NavigationService } from '../../../shared/service/navigation.service';
import { MatMultiSort } from 'ngx-mat-multi-sort';
import { Suchergebnis } from '../../../shared/model/suche/suchergebnis';
import { ProjektService } from '../../../shared/service/projekt.service';
import { Projekt } from '../../../shared/model/projekt/projekt';
import { MitarbeiterService } from '../../../shared/service/mitarbeiter.service';
import { Mitarbeiter } from '../../../shared/model/mitarbeiter/mitarbeiter';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { FormBuilder, FormGroup } from '@angular/forms';
import { LocalStorageService } from '../../../shared/service/local-storage.service';
import { pabStruktur } from 'src/app/shared/model/sonstiges/pab-struktur';
import { AbrechnungsmonatService } from '../../../shared/service/abrechnungsmonat.service';
import { SuchergebnisAktion } from '../../../shared/model/suche/suchergebnis-aktion';

@Component({
  selector: 'pab-suchergebnis',
  templateUrl: './suchergebnis.component.html',
  styleUrls: ['./suchergebnis.component.scss'],
})
export class SuchergebnisComponent implements OnChanges {
  @Input()
  suchbegriff: string = '';

  @Output()
  suchergebnisNavigationEvent: EventEmitter<string> = new EventEmitter();

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  pabSucheEinstellungStorageKey: string = 'pab-suche-einstellung';

  suchergebnisse: Suchergebnis[] = [];

  gefundeneProjekte: Suchergebnis[] = [];

  gefundeneDialoge: Suchergebnis[] = [];
  gefundeneMitarbeiter: Suchergebnis[] = [];
  filterFormGroup: FormGroup;

  constructor(
    private fb: FormBuilder,
    private navigationService: NavigationService,
    private projektService: ProjektService,
    private mitarbeiterService: MitarbeiterService,
    private localStorageService: LocalStorageService,
    private abrechnungsmonatService: AbrechnungsmonatService
  ) {
    if (this.localStorageService.getLocalstorageProperty(this.pabSucheEinstellungStorageKey)) {
      this.filterFormGroup = this.fb.nonNullable.group(
        this.localStorageService.getLocalstorageProperty(this.pabSucheEinstellungStorageKey)
      );
    } else {
      this.filterFormGroup = this.fb.nonNullable.group({
        maskenAnzeigen: true,
        projekteAnzeigen: true,
        mitarbeiterAnzeigen: true,
        angeboteAnzeigen: true,
        kontakteAnzeigen: true,
        unternehmenAnzeigen: true,
        vertraegeAnzeigen: true,
      });
      this.localStorageService.setLocalstorageProperty(
        this.pabSucheEinstellungStorageKey,
        this.filterFormGroup.getRawValue()
      );
    }
    this.filterFormGroup.valueChanges.subscribe(() => {
      this.aktualisiereSuchergebnisse();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['suchbegriff']) {
      this.aktualisiereSuchergebnisse();
    }
  }

  aktualisiereSuchergebnisse() {
    this.sichereFilterAuswahlInLocalStorage();
    forkJoin([this.projektService.getAlleProjekte(), this.mitarbeiterService.getAlleMitarbeiter()]).subscribe(
      ([projekte, mitarbeiter]) => {
        this.durchsucheProjekte(projekte);
        this.durchsucheMitarbeiter(mitarbeiter);
        this.durchsucheAnwendungsmasken();
        this.stelleSuchergebnisseZusammen();
      }
    );
  }

  private sichereFilterAuswahlInLocalStorage() {
    this.localStorageService.setLocalstorageProperty(
      this.pabSucheEinstellungStorageKey,
      this.filterFormGroup.getRawValue()
    );
  }

  private stelleSuchergebnisseZusammen() {
    this.suchergebnisse = [];
    this.suchergebnisse.push(...this.gefundeneDialoge);
    this.suchergebnisse.push(...this.gefundeneProjekte);
    this.suchergebnisse.push(...this.gefundeneMitarbeiter);
  }

  private durchsucheAnwendungsmasken() {
    this.gefundeneDialoge = [];
    if (this.filterFormGroup.get('maskenAnzeigen')?.value) {
      pabStruktur.map((maske) => {
        if (this.maskeEnthaeltSuchbegriff(maske)) {
          this.gefundeneDialoge.push(this.erstelleMaskeSuchergebnis(maske));
        }
      });
    }
  }

  private erstelleMaskeSuchergebnis(maske: { route: string; bezeichnung: string }) {
    return {
      bezeichnung: maske.bezeichnung,
      typ: 'Maske',
      aktion: [{ route: maske.route, tooltip: '', icon: 'play_arrow', queryParams: null }],
    };
  }

  private maskeEnthaeltSuchbegriff(maske: { route: string; bezeichnung: string }) {
    return maske.bezeichnung.toLowerCase().indexOf(this.suchbegriff.toLowerCase()) > -1;
  }

  private durchsucheProjekte(projekte: Projekt[]) {
    this.gefundeneProjekte = [];
    if (this.filterFormGroup.get('projekteAnzeigen')?.value) {
      projekte.forEach((projekt) => {
        if (this.projektEnthaeltSuchbegriff(projekt)) {
          this.gefundeneProjekte.push(this.erstelleProjektSurchergebnis(projekt));
        }
      });
    }
  }

  private projektEnthaeltSuchbegriff(projekt: Projekt) {
    return (
      (projekt.projektnummer + ' ' + projekt.bezeichnung).toLowerCase().indexOf(this.suchbegriff.toLowerCase()) > -1
    );
  }

  private erstelleProjektSurchergebnis(projekt: Projekt) {
    return {
      bezeichnung: projekt.projektnummer + ' ' + projekt.bezeichnung,
      typ: 'Projekt',
      aktion: [
        {
          route: '/projektabrechnung',
          tooltip: '',
          icon: 'view_list',
          queryParams: { id: projekt.id },
        },
        { route: '/projekt/', tooltip: '', icon: 'edit', queryParams: { id: projekt.id } },
      ],
    };
  }

  private durchsucheMitarbeiter(mitarbeiter: Mitarbeiter[]) {
    this.gefundeneMitarbeiter = [];
    if (this.filterFormGroup.get('mitarbeiterAnzeigen')?.value) {
      mitarbeiter.forEach((mitarb) => {
        if (this.mitarbeiterEnthaeltSuchbegriff(mitarb)) {
          this.gefundeneMitarbeiter.push(this.erstelleMitarbeiterSuchergebnis(mitarb));
        }
      });
    }
  }

  private mitarbeiterEnthaeltSuchbegriff(mitarb: Mitarbeiter) {
    return (mitarb.vorname + ' ' + mitarb.nachname).toLowerCase().indexOf(this.suchbegriff.toLowerCase()) > -1;
  }

  private erstelleMitarbeiterSuchergebnis(mitarb: Mitarbeiter) {
    const aktuellerAbrechnungmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonat();
    return {
      bezeichnung: mitarb.vorname + ' ' + mitarb.nachname,
      typ: 'Mitarbeiter',
      aktion: [
        {
          route: `arbeitsnachweis/bearbeiten`,
          tooltip: 'Arbeitsnachweis des aktuellen Abrechnungsmonat bearbeiten',
          icon: 'edit',
          queryParams: {
            jahr: aktuellerAbrechnungmonat.jahr,
            monat: aktuellerAbrechnungmonat.monat,
            mitarbeiterId: mitarb.id,
          },
        },
      ],
    };
  }

  fuehreAktionAus(suchergebnisAktion: SuchergebnisAktion) {
    this.suchergebnisNavigationEvent.emit();
    if (suchergebnisAktion.queryParams) {
      this.navigationService.navigiereZuMitQueryParams(suchergebnisAktion.route, false, suchergebnisAktion.queryParams);
    } else {
      this.navigationService.navigiereZu(suchergebnisAktion.route, false);
    }
  }
}
