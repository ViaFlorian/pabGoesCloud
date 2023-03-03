import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { ProjektabrechnungFertigstellungInitialDaten } from '../../../../shared/model/projektabrechnung/projektabrechnung-fertigstellung-initial-daten';
import { debounceTime, Observable } from 'rxjs';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauSechsNachkommastellen,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
} from '../../../../shared/util/nummer-converter.util';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-fertigstellung-dialog',
  templateUrl: './projektabrechnung-bearbeiten-fertigstellung-dialog.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-fertigstellung-dialog.component.scss'],
})
export class ProjektabrechnungBearbeitenFertigstellungDialogComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public data: ProjektabrechnungBearbeitenFertigstellungDialogData) {}

  aktuellProjektabrechnungFertigstellungInitialDaten: ProjektabrechnungFertigstellungInitialDaten | undefined =
    undefined;

  ngOnInit(): void {
    this.felderDeaktivieren();
    this.reagiereAufInitialeDaten();
    this.berechneWeitereDaten();
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.data.pabFertigstellungFormGroup, komponentenName, erlaubteExp);
  }

  private felderDeaktivieren(): void {
    this.data.pabFertigstellungFormGroup.disable();
    this.data.pabFertigstellungFormGroup.get('monatFertigstellung')?.enable();
  }

  private reagiereAufInitialeDaten(): void {
    this.data.fertigstellungInitialDatenObs.subscribe(
      (projektabrechnungFertigstellungInitialDaten: ProjektabrechnungFertigstellungInitialDaten | undefined) => {
        if (!projektabrechnungFertigstellungInitialDaten) {
          return;
        }

        this.aktuellProjektabrechnungFertigstellungInitialDaten = projektabrechnungFertigstellungInitialDaten;

        this.data.pabFertigstellungFormGroup.patchValue({
          bisherFertigstellung: convertUsNummerZuDeStringMitGenauSechsNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.bisherFertigstellung
          ),
          bisherProjektbudget: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.bisherProjektbudget
          ),
          bisherLeistungRechnerisch: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.bisherLeistungRechnerisch
          ),
          monatFertigstellung: convertUsNummerZuDeStringMitGenauSechsNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.monatFertigstellung
          ),
          monatProjektbudget: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.monatProjektbudget
          ),
          monatLeistungRechnerisch: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungFertigstellungInitialDaten.monatLeistungRechnerisch
          ),
        });
      }
    );
  }

  private berechneWeitereDaten(): void {
    this.data.pabFertigstellungFormGroup.valueChanges.pipe(debounceTime(500)).subscribe(() => {
      if (!this.aktuellProjektabrechnungFertigstellungInitialDaten) {
        return;
      }

      const monatFertigstellung: number = convertDeStringZuUsNummer(
        this.data.pabFertigstellungFormGroup.getRawValue().monatFertigstellung
      );

      const bisherLeistungFaktuierfaehgiKumuliert: number =
        this.aktuellProjektabrechnungFertigstellungInitialDaten.bisherProjektbudget *
        (this.aktuellProjektabrechnungFertigstellungInitialDaten.bisherFertigstellung / 100);

      const monatLeistungFaktuierfaehgiKumuliert: number =
        this.aktuellProjektabrechnungFertigstellungInitialDaten.monatProjektbudget * (monatFertigstellung / 100);

      const monatLeistungFaktuierfaehig: number =
        monatLeistungFaktuierfaehgiKumuliert - bisherLeistungFaktuierfaehgiKumuliert;

      const leistungSummer: number =
        bisherLeistungFaktuierfaehgiKumuliert +
        this.aktuellProjektabrechnungFertigstellungInitialDaten.monatLeistungRechnerisch;
      const monatErrechneteFertigstellung: number =
        leistungSummer === 0
          ? 0
          : (leistungSummer / this.aktuellProjektabrechnungFertigstellungInitialDaten.monatProjektbudget) * 100;

      this.data.pabFertigstellungFormGroup.patchValue(
        {
          bisherLeistungFaktuierfaehigKumuliert: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            bisherLeistungFaktuierfaehgiKumuliert
          ),
          monatLeistungFaktuierfaehigKumuliert: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            monatLeistungFaktuierfaehgiKumuliert
          ),
          monatLeistungFaktuierfaehig:
            convertUsNummerZuDeStringMitGenauZweiNachkommastellen(monatLeistungFaktuierfaehig),
          monatErrechneteFertigstellung:
            convertUsNummerZuDeStringMitGenauSechsNachkommastellen(monatErrechneteFertigstellung),
        },
        { emitEvent: false }
      );
    });
  }
}

export interface ProjektabrechnungBearbeitenFertigstellungDialogData {
  pabFertigstellungFormGroup: FormGroup;
  ladeFertigstellungDialogDatenSpinner: SpinnerRef;
  fertigstellungInitialDatenObs: Observable<ProjektabrechnungFertigstellungInitialDaten | undefined>;
}
