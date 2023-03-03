import { AbstractControl, FormArray, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Abrechnungsmonat } from '../../../shared/model/sonstiges/abrechnungsmonat';
import { MitarbeiterAbrechnungsmonat } from '../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { KostenartEnum } from '../../../shared/enum/kostenart.enum';
import { Kostenart } from '../../../shared/model/konstanten/kostenart';
import { ProjekttypEnum } from '../../../shared/enum/projekttyp.enum';
import { Projekt } from '../../../shared/model/projekt/projekt';
import { convertDeStringZuUsNummer } from '../../../shared/util/nummer-converter.util';
import { ProjektnummernEnum } from '../../../shared/enum/projektnummern.enum';

export class CustomValidatorProjektabrechnung {
  static bearbeitenProjektzeitGesamtstundenUebertschritten(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const pabProjektzeitFormGroup: FormGroup = control as FormGroup;
      const pabProjektzeitAlternativeStundensaetzeFormArray: FormArray = pabProjektzeitFormGroup.controls[
        'alternativeStundensaetze'
      ] as FormArray;

      const kostenStundenGesamtRaw = pabProjektzeitFormGroup.getRawValue().kostenStundenGesamt;
      const kostenStundenGesamt: number = convertDeStringZuUsNummer(kostenStundenGesamtRaw);

      const kostenStundenRaw = pabProjektzeitFormGroup.getRawValue().kostenStunden;
      let angegebeneStundenGesamt: number = convertDeStringZuUsNummer(kostenStundenRaw);
      pabProjektzeitAlternativeStundensaetzeFormArray.controls.forEach((control) => {
        if (!(control instanceof FormGroup)) {
          return;
        }
        const kostenStundenAlternativRaw = (control as FormGroup).getRawValue().kostenStunden;
        angegebeneStundenGesamt += convertDeStringZuUsNummer(kostenStundenAlternativRaw);
      });

      if (angegebeneStundenGesamt < kostenStundenGesamt) {
        return { bearbeitenProjektzeitGesamtstundenUnterschritten: true };
      }
      if (angegebeneStundenGesamt > kostenStundenGesamt) {
        return { bearbeitenProjektzeitGesamtstundenUeberschritten: true };
      }

      return null;
    };
  }

  static bearbeitenFertigstellungErrechneteLeistungZuKlein(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const pabFertigstellungFormGroup: FormGroup = control as FormGroup;

      const monatLeistungFaktuierfaehigKumuliert: number = convertDeStringZuUsNummer(
        pabFertigstellungFormGroup.getRawValue().monatLeistungFaktuierfaehigKumuliert
      );
      const monatLeistungRechnerisch: number = convertDeStringZuUsNummer(
        pabFertigstellungFormGroup.getRawValue().monatLeistungRechnerisch
      );

      return monatLeistungRechnerisch > monatLeistungFaktuierfaehigKumuliert
        ? { bearbeitenFertigstellungErrechneteLeistungZuKlein: true }
        : null;
    };
  }

  static abrechnungsmonatIstNichtAbgeschlossen(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return !control.value.abgeschlossen ? null : { abrechnungsmonatAbgeschlossen: true };
    };
  }

  static abrechnungsmonatIstNichtLeer(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const abrechnungsmonat: Abrechnungsmonat | MitarbeiterAbrechnungsmonat = control.value;
      return abrechnungsmonat.jahr ? null : { abrechnungsmonatLeer: true };
    };
  }

  static korrekturbuchungKostenartGueltig(): ValidatorFn {
    const erlaubteKostenarten: string[] = [
      KostenartEnum.PROJEKTZEITEN,
      KostenartEnum.REISEZEITEN,
      KostenartEnum.REISEKOSTEN,
      KostenartEnum.SONDERARBEITSZEITEN,
      KostenartEnum.RUFBEREITSCHAFT,
      KostenartEnum.SONSTIGE,
      KostenartEnum.RABATTE,
    ];
    return (control: AbstractControl): ValidationErrors | null => {
      const kostenart: Kostenart = control.value;
      if (!kostenart || !kostenart.id) {
        return null;
      }
      return erlaubteKostenarten.includes(kostenart.bezeichnung) ? null : { kostenartUngueltig: true };
    };
  }

  static korrekturbuchungRabattIstMoeglichFuerProjektTyp(): ValidatorFn {
    const erlaubteProjekte: string[] = [ProjekttypEnum.FESTPREIS, ProjekttypEnum.WARTUNG, ProjekttypEnum.PRODUKT];
    return (control: AbstractControl): ValidationErrors | null => {
      const kostenart: Kostenart = control.value;
      if (!kostenart || !kostenart.bezeichnung || kostenart.bezeichnung !== KostenartEnum.RABATTE) {
        return null;
      }
      const projektBuchung: Projekt = control.parent?.getRawValue().projektComboBoxKorrektur;
      if (projektBuchung && projektBuchung.id && erlaubteProjekte.includes(projektBuchung.projekttyp)) {
        return { rabattFuerProjektTypNichtMoeglich: true };
      }
      const projektGegenbuchung: Projekt = control.parent?.getRawValue().projektComboBoxGegen;
      if (projektGegenbuchung && projektGegenbuchung.id && erlaubteProjekte.includes(projektGegenbuchung.projekttyp)) {
        return { rabattFuerProjektTypNichtMoeglich: true };
      }
      return null;
    };
  }

  static korrekturbuchungBuchungsBetraegeSindBedingtVorhanden(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const group = control as FormGroup;
      const betragKostensatz: AbstractControl = group.controls['betragKostensatzKorrektur'];
      const betragStudensatz: AbstractControl = group.controls['betragStundensatzKorrektur'];

      this.korrekturbuchungAktualisiereValidierungFuerBetragBuchung(
        betragKostensatz,
        betragStudensatz,
        group.controls['stundenKorrektur'].value
      );

      this.korrekturbuchungAktualisiereValidierungFuerBetragBuchung(
        betragStudensatz,
        betragKostensatz,
        group.controls['faktStundenKorrektur'].value
      );

      return null;
    };
  }

  static korrekturbuchungGegenbuchungIstValide(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const group = control as FormGroup;
      if (!this.korrekturbuchungPruefeObEinGegenbuchungsfeldBefuellt(group)) {
        group.controls['projektComboBoxGegen'].removeValidators(Validators.required);
        group.controls['projektComboBoxGegen'].setErrors(null);
        group.controls['betragKostensatzGegen'].removeValidators(Validators.required);
        group.controls['betragKostensatzGegen'].setErrors(null);
        group.controls['betragStundensatzGegen'].removeValidators(Validators.required);
        group.controls['betragStundensatzGegen'].setErrors(null);
        group.controls['bemerkungGegen'].removeValidators(Validators.required);
        group.controls['bemerkungGegen'].setErrors(null);
        return null;
      }
      this.korrekturbuchungAktualisiereValidierungFuerProjekt(group.controls['projektComboBoxGegen']);

      const betragKostensatz: AbstractControl = group.controls['betragKostensatzGegen'];
      const betragStundensatz: AbstractControl = group.controls['betragStundensatzGegen'];

      this.korrekturbuchungAktualisiereValidierungFuerBetragBuchung(
        betragKostensatz,
        betragStundensatz,
        group.controls['stundenGegen'].value
      );

      this.korrekturbuchungAktualisiereValidierungFuerBetragBuchung(
        betragStundensatz,
        betragKostensatz,
        group.controls['faktStundenGegen'].value
      );

      this.korrekturbuchungAktualisiereValidierungFuerBemerkung(group.controls['bemerkungGegen']);
      return null;
    };
  }

  static korrekturbuchungBeiUrlaubIstMitarbeiterValide(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const group = control as FormGroup;
      if (
        !this.pruefeObProjektUrlaub(group.controls['projektComboBoxKorrektur']) &&
        !this.pruefeObProjektUrlaub(group.controls['projektComboBoxGegen'])
      ) {
        group.controls['mitarbeiter'].setErrors(null);
      } else if (!group.controls['mitarbeiter'].value || !group.controls['mitarbeiter'].value.id) {
        group.controls['mitarbeiter'].setErrors({ leerBeiUrlaub: true });
      } else if (!group.controls['mitarbeiter'].value.intern) {
        group.controls['mitarbeiter'].setErrors({ externBeiUrlaub: true });
      } else {
        group.controls['mitarbeiter'].setErrors(null);
      }
      return null;
    };
  }

  static korrekturbuchungPruefeObBetragBedingtRequired(andererBetrag: string, stunden: string): boolean {
    return !!(!andererBetrag || stunden);
  }

  private static korrekturbuchungAktualisiereValidierungFuerBetragBuchung(
    betragZuAktualisieren: AbstractControl,
    andererBetrag: AbstractControl,
    stunden: string
  ) {
    if (this.korrekturbuchungPruefeObBetragBedingtRequired(andererBetrag.value, stunden)) {
      betragZuAktualisieren.addValidators(Validators.required);
      if (!betragZuAktualisieren.value) {
        betragZuAktualisieren.setErrors({ required: true });
      }
      return;
    }
    betragZuAktualisieren.removeValidators(Validators.required);
    betragZuAktualisieren.setErrors(null);
  }

  private static korrekturbuchungAktualisiereValidierungFuerBemerkung(bemerkungControl: AbstractControl<any>) {
    bemerkungControl.addValidators(Validators.required);

    if (bemerkungControl.value) {
      bemerkungControl.setErrors(null);
    } else {
      bemerkungControl.setErrors({ required: true });
    }
  }

  private static korrekturbuchungAktualisiereValidierungFuerProjekt(projektControl: AbstractControl<Projekt>) {
    projektControl.addValidators(Validators.required);

    if (projektControl.value && projektControl.value.id) {
      projektControl.setErrors(null);
    } else {
      projektControl.setErrors({ required: true });
    }
  }

  private static korrekturbuchungPruefeObEinGegenbuchungsfeldBefuellt(group: FormGroup) {
    return !!(
      (group.controls['projektComboBoxGegen'].value && group.controls['projektComboBoxGegen'].value.id) ||
      group.controls['stundenGegen'].value ||
      group.controls['betragKostensatzGegen'].value ||
      group.controls['faktStundenGegen'].value ||
      group.controls['betragStundensatzGegen'].value ||
      group.controls['leistungGegen'].value ||
      group.controls['bemerkungGegen'].value
    );
  }

  private static pruefeObProjektUrlaub(projektControl: AbstractControl<Projekt>) {
    return (
      projektControl.value &&
      projektControl.value.id &&
      projektControl.value.projektnummer === ProjektnummernEnum.URLAUB
    );
  }
}
