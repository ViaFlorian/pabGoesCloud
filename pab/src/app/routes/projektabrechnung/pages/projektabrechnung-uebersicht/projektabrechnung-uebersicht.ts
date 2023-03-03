import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { AktivInaktivEnum } from '../../../../shared/enum/aktiv-inaktiv.enum';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { BuchungsstatusProjektEnum } from '../../../../shared/enum/buchungsstatus-projekt.enum';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { BehaviorSubject, debounceTime, forkJoin } from 'rxjs';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { KundeService } from '../../../../shared/service/kunde.service';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { ProjektabrechnungService } from '../../../../shared/service/projektabrechnung.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { ProjektabrechnungUebersicht } from '../../../../shared/model/projektabrechnung/projektabrechnung-uebersicht';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { ProjektabrechnungMitarbeiterPair } from '../../../../shared/model/projektabrechnung/projektabrechnung-mitarbeiter-pair';
import { CustomValidator } from '../../../../shared/validation/custom-validator';

@Component({
  selector: 'pab-projektabrechnung-uebersicht',
  templateUrl: './projektabrechnung-uebersicht.component.html',
  styleUrls: ['./projektabrechnung-uebersicht.component.scss'],
})
export class ProjektabrechnungUebersichtComponent implements OnInit {
  formGroup: FormGroup;
  ueberschrift: string = '';

  projektabrechnungen: ProjektabrechnungUebersicht[] = [];
  projektabrechnungMitarbeiterPairs: ProjektabrechnungMitarbeiterPair[] = [];

  mitarbeiterAuswahl: Mitarbeiter[] = [];
  mitarbeiterAuswahlGefiltert: BehaviorSubject<Mitarbeiter[]> = new BehaviorSubject<Mitarbeiter[]>([]);
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  projektAuswahl: Projekt[] = [];
  kundeAuswahl: Kunde[] = [];
  organisationseinheitAuswahl: Organisationseinheit[] = [];

  ladeProjektabrechnungenSpinner: SpinnerRef = new SpinnerRef();

  constructor(
    private fb: FormBuilder,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organistationseinheitService: OrganisationseinheitService,
    private projektabrechnungService: ProjektabrechnungService
  ) {
    const abrechnungsmonatAbWann: Date = new Date();
    abrechnungsmonatAbWann.setMonth(abrechnungsmonatAbWann.getMonth() - 1);

    this.formGroup = this.fb.nonNullable.group({
      abrechnungsmonatAbWann: [abrechnungsmonatAbWann],
      abrechnungsmonatBisWann: [new Date()],
      sachbearbeiter: [{} as Mitarbeiter],
      bearbeitungsstatus: [BearbeitungsstatusEnum.ALLE],
      aktivInaktiv: [AktivInaktivEnum.ALLE],
      projekt: [{} as Projekt, [CustomValidator.valueIstObjektOderLeererString()]],
      buchungsstatus: [BuchungsstatusProjektEnum.BEBUCHTE_PROJEKTE],
      projekttyp: [ProjekttypEnum.ALLE],
      organisationseinheit: [{}],
      kunde: [{} as Kunde, [CustomValidator.valueIstObjektOderLeererString()]],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
      kostenOperator: [OperatorEnum.GREATER_THAN],
      kostenFilter: [''],
      leistungOperator: [OperatorEnum.GREATER_THAN],
      leistungFilter: [''],
    });
  }

  ngOnInit(): void {
    this.reagiereAufAenderungAbrechnungsmonat();
    this.rufeInitialeDatenAb();
  }

  reagiereAufListeAnMitarbeiternEvent(mitarbeiterIds: string[]) {
    this.mitarbeiterAuswahlGefiltert.next(
      this.mitarbeiterAuswahl.filter((mitarbeiter) => mitarbeiterIds.includes(mitarbeiter.id)).slice()
    );
  }

  reagiereAufAnzahlProjektabrechnungenEvent(anzahlProjektabrechungen: number) {
    this.ueberschrift = `Übersicht der Projekte [${anzahlProjektabrechungen}]`;
  }

  private reagiereAufAenderungAbrechnungsmonat(): void {
    // Aktualisierung über Backend bei Änderung der Monatsauswahl
    this.formGroup
      .get('abrechnungsmonatAbWann')!
      .valueChanges.pipe(debounceTime(500))
      .subscribe(() => {
        this.aktualisiereProjektabrechnungen();
      });
    this.formGroup
      .get('abrechnungsmonatBisWann')!
      .valueChanges.pipe(debounceTime(500))
      .subscribe(() => {
        this.aktualisiereProjektabrechnungen();
      });
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.projektService.getAlleProjekte(),
      this.kundeService.getAlleKunden(),
      this.organistationseinheitService.getAlleOrganisationseinheiten(),
    ]).subscribe(([mitarbeiter, sachbearbeiter, projekte, kunden, organisationseinheiten]) => {
      this.mitarbeiterAuswahl = mitarbeiter.slice();
      this.sachbearbeiterAuswahl = sachbearbeiter.slice();
      this.projektAuswahl = projekte.slice();
      this.kundeAuswahl = kunden.slice();
      this.organisationseinheitAuswahl = organisationseinheiten.slice();
      this.aktualisiereProjektabrechnungen();
    });
  }

  private aktualisiereProjektabrechnungen() {
    this.ladeProjektabrechnungenSpinner.open();
    const abWann: Date = this.formGroup.getRawValue().abrechnungsmonatAbWann;
    const bisWann: Date = this.formGroup.getRawValue().abrechnungsmonatBisWann;

    forkJoin([
      this.projektabrechnungService.getProjektabrechnungenUebersicht(abWann, bisWann),
      this.projektabrechnungService.getUebersichtZuAlleProjekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
        abWann,
        bisWann
      ),
      this.projektabrechnungService.getProjektabrechnungMitarbeiterPairs(abWann, bisWann),
    ]).subscribe(([projektabrechnungen, fehlendenProjektabrechnungen, projektabrechnungMitarbeiterPairs]) => {
      this.projektabrechnungMitarbeiterPairs = projektabrechnungMitarbeiterPairs;
      this.projektabrechnungen = projektabrechnungen.concat(fehlendenProjektabrechnungen);
      this.ladeProjektabrechnungenSpinner.close();
    });
  }
}
