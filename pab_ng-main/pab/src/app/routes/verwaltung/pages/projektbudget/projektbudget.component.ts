import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { filter, forkJoin } from 'rxjs';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { KundeService } from '../../../../shared/service/kunde.service';
import { VerwaltungService } from '../../../../shared/service/verwaltung.service';
import { ProjektBudget } from '../../../../shared/model/verwaltung/projekt-budget';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { CustomValidatorVerwaltung } from '../../validation/custom-validator-verwaltung';

@Component({
  selector: 'pab-projektbudget',
  templateUrl: './projektbudget.component.html',
  styleUrls: ['./projektbudget.component.scss'],
})
export class ProjektbudgetComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ladeProjektbudgetSpinner: SpinnerRef = new SpinnerRef();

  mitarbeiterAlle: Mitarbeiter[] = [];
  sachbearbeiterAlle: Mitarbeiter[] = [];
  kundeAlle: Kunde[] = [];
  organisationseinheitAlle: Organisationseinheit[] = [];
  abrechnungsmonatAlle: Abrechnungsmonat[] = [];
  projektAuswahl: Projekt[] = [];

  projektbudgetAuswahlFormGroup: FormGroup;
  projektbudgetFormGroup: FormGroup;

  projektbudgetListe: StatusListen;

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organistationseinheitService: OrganisationseinheitService,
    private verwaltungService: VerwaltungService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private benachrichtigungService: BenachrichtigungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private navigationService: NavigationService
  ) {
    this.projektbudgetAuswahlFormGroup = this.fb.nonNullable.group({
      projekt: [{} as Projekt],
      kunde: [{ value: '', disabled: true }],
      projekttyp: [{ value: '', disabled: true }],
      sachbearbeiter: [{ value: '', disabled: true }],
      organisationseinheit: [{ value: '', disabled: true }],
      projektverantwortung: [{ value: '', disabled: true }],
      geschaeftsstelle: [{ value: '', disabled: true }],
    });

    this.projektbudgetFormGroup = this.fb.nonNullable.group({
      wertstellung: ['', Validators.required],
      budgetBetrag: ['', Validators.required],
      bemerkung: ['', Validators.required],
    });

    this.projektbudgetListe = new StatusListen([]);
  }

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  ngAfterViewInit(): void {
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  istProjektAusgewaehlt(): boolean {
    const projekt: Projekt = this.projektbudgetAuswahlFormGroup.get('projekt')?.value;
    return !!(projekt && projekt.id);
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum) {
    const dataSpinner = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    spinnerOverlay.close();
    this.benachrichtigungService.erstelleBenachrichtigung('Projektbudget wurde erfolgreich gespeichert.');
    this.fuehreAktionNachSpeichernAus(speichernPostAktion);
  }

  private reagiereAufAenderungProjekt(): void {
    this.projektbudgetAuswahlFormGroup
      .get('projekt')
      ?.valueChanges.pipe(filter((projekt) => projekt === '' || typeof projekt !== 'string'))
      .subscribe((projekt: Projekt) => {
        this.ladeProjektbudgetSpinner.open();

        // Relevante Daten zurücksetzen
        this.projektbudgetListe = new StatusListen([]);
        this.projektbudgetFormGroup.reset();

        // Ggf. neue Daten laden
        if (!projekt || !projekt.id) {
          this.ladeProjektbudgetSpinner.close();
          return;
        }

        this.verwaltungService.getProjektBudgetsByProjektId(projekt.id).subscribe((projektBudgets: ProjektBudget[]) => {
          this.projektbudgetListe = new StatusListen(projektBudgets);
          this.ladeProjektbudgetSpinner.close();
        });
      });
  }

  private fuehreAktionNachSpeichernAus(speichernPostAktion: SpeichernPostAktionEnum) {
    switch (speichernPostAktion) {
      case SpeichernPostAktionEnum.KEINE:
        break;
      case SpeichernPostAktionEnum.SCHLIESSEN:
        this.navigationService.geheZurueck();
        break;
    }
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.kundeService.getAlleKunden(),
      this.organistationseinheitService.getAlleOrganisationseinheiten(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
      this.projektService.getAlleProjekte(),
    ]).subscribe(([mitarbeiter, sachbearbeiter, kunden, organisationseinheiten, abrechnungsmonate, projekte]) => {
      this.mitarbeiterAlle = mitarbeiter.slice();
      this.sachbearbeiterAlle = sachbearbeiter.slice();
      this.kundeAlle = kunden.slice();
      this.organisationseinheitAlle = organisationseinheiten.slice();
      this.abrechnungsmonatAlle = abrechnungsmonate.slice();

      this.projektAuswahl = projekte.slice();

      // Validator erst hinzufügen, nachdem Abrechnungsmonate abgefragt wurden, andernfalls wird [] übergeben
      this.projektbudgetFormGroup
        .get('wertstellung')
        ?.addValidators(CustomValidatorVerwaltung.abrechnungsmonatIstNichtAbgeschlossen(this.abrechnungsmonatAlle));
    });
  }
}
