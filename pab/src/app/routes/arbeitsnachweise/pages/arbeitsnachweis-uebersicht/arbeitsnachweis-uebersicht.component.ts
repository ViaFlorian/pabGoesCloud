import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { InternExternEnum } from '../../../../shared/enum/intern-extern.enum';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { OffenVorhandenEnum } from '../../../../shared/enum/offen-vorhanden.enum';
import { ArbeitsnachweisUebersicht } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-uebersicht';
import { BehaviorSubject, debounceTime, forkJoin } from 'rxjs';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { CustomValidator } from '../../../../shared/validation/custom-validator';

@Component({
  selector: 'pab-arbeitsnachweis',
  templateUrl: './arbeitsnachweis-uebersicht.component.html',
  styleUrls: ['./arbeitsnachweis-uebersicht.component.scss'],
})
export class ArbeitsnachweisUebersichtComponent implements OnInit {
  formGroup: FormGroup;
  ueberschrift: string = '';
  arbeitsnachweise: ArbeitsnachweisUebersicht[] = [];
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  mitarbeiterAuswahlGefiltert: BehaviorSubject<Mitarbeiter[]> = new BehaviorSubject<Mitarbeiter[]>([]);
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  ladeArbeitsnachweiseSpinner: SpinnerRef = new SpinnerRef();

  constructor(
    private fb: FormBuilder,
    private mitarbeiterService: MitarbeiterService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private spinnerOverlayService: SpinnerOverlayService,
    private benachrichtigungService: BenachrichtigungService
  ) {
    const abrechnungsmonatAbWann: Date = new Date();
    abrechnungsmonatAbWann.setMonth(abrechnungsmonatAbWann.getMonth() - 1);

    this.formGroup = this.fb.nonNullable.group({
      abrechnungsmonatAbWann: [abrechnungsmonatAbWann],
      abrechnungsmonatBisWann: [new Date()],
      sachbearbeiter: [{} as Mitarbeiter],
      bearbeitungsstatus: [BearbeitungsstatusEnum.ALLE],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
      offenVorhanden: [OffenVorhandenEnum.NUR_VORHANDENE],
      internExtern: [InternExternEnum.NUR_INTERNE],
      projektstundenOperator: [OperatorEnum.GREATER_THAN],
      projektstundenFilter: [''],
      spesenZuschlaegeOperator: [OperatorEnum.GREATER_THAN],
      spesenZuschlaegeFilter: [''],
      belegeAuslagenOperator: [OperatorEnum.GREATER_THAN],
      belegeAuslagenFilter: [''],
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

  reagiereAufAnzahlArbeitsnachweiseEvent(anzahlArbeitsnachweise: number) {
    this.ueberschrift = `Übersicht der Arbeitsnachweise [${anzahlArbeitsnachweise}]`;
  }

  reagiereAufLoeschenEvent(arbeitsnachweisId: string) {
    const data = {
      title: 'Arbeitsnachweis wird gelöscht...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(data);
    this.arbeitsnachweisService.deleteArbeitsnachweis(arbeitsnachweisId).subscribe(() => {
      this.aktualisiereArbeitsnachweise();
      spinnerOverlay.close();
      this.benachrichtigungService.erstelleBenachrichtigung('Arbeitsnachweis wurde erfolgreich gelöscht.');
    });
  }

  private reagiereAufAenderungAbrechnungsmonat(): void {
    // Aktualisierung über Backend bei Änderung der Monatsauswahl
    this.formGroup
      .get('abrechnungsmonatAbWann')!
      .valueChanges.pipe(debounceTime(500))
      .subscribe(() => {
        this.aktualisiereArbeitsnachweise();
      });
    this.formGroup
      .get('abrechnungsmonatBisWann')!
      .valueChanges.pipe(debounceTime(500))
      .subscribe(() => {
        this.aktualisiereArbeitsnachweise();
      });
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([this.mitarbeiterService.getAlleMitarbeiter(), this.mitarbeiterService.getAlleSachbearbeiter()]).subscribe(
      ([mitarbeiter, sachbearbeiter]) => {
        this.mitarbeiterAuswahl = mitarbeiter.slice();
        this.sachbearbeiterAuswahl = sachbearbeiter.slice();
        this.aktualisiereArbeitsnachweise();
      }
    );
  }

  private aktualisiereArbeitsnachweise() {
    this.ladeArbeitsnachweiseSpinner.open();
    const abWann: Date = this.formGroup.getRawValue().abrechnungsmonatAbWann;
    const bisWann: Date = this.formGroup.getRawValue().abrechnungsmonatBisWann;
    forkJoin([
      this.arbeitsnachweisService.getArbeitsnachweiseUebersicht(abWann, bisWann),
      this.arbeitsnachweisService.getFehlendeArbeitsnachweise(abWann, bisWann),
    ]).subscribe(([vorhandeneArbeitsnachweise, fehlendeArbeitsnachweise]) => {
      this.arbeitsnachweise = vorhandeneArbeitsnachweise.concat(fehlendeArbeitsnachweise);
      this.ladeArbeitsnachweiseSpinner.close();
    });
  }
}
