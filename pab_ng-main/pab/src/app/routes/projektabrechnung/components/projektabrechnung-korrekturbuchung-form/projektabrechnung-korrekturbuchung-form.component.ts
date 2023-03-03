import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Kostenart } from '../../../../shared/model/konstanten/kostenart';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import * as CompareUtil from '../../../../shared/util/compare.util';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeString,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { ProjektabrechnungKorrekturbuchung } from '../../../../shared/model/projektabrechnung/projektabrechnung-korrekturbuchung';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { KostenartEnum } from '../../../../shared/enum/kostenart.enum';
import { getAbgeschlossenIcon } from 'src/app/shared/util/icon.util';
import { ProjektabrechnungKorrekturbuchungVorgang } from '../../../../shared/model/projektabrechnung/projektabrechnung-korrekturbuchung-vorgang';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { CustomValidatorProjektabrechnung } from '../../validation/custom-validator-projektabrechnung';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { Observable } from 'rxjs';

@Component({
  selector: 'pab-projektabrechnung-korrekturbuchung-form',
  templateUrl: './projektabrechnung-korrekturbuchung-form.component.html',
  styleUrls: ['./projektabrechnung-korrekturbuchung-form.component.scss'],
})
export class ProjektabrechnungKorrekturbuchungFormComponent implements OnInit {
  korrekturbuchungFormGroup!: FormGroup;

  @Input()
  abrechnungsmonateGesamtauswahl!: Abrechnungsmonat[];

  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  @Input()
  kostenartAuswahl!: Kostenart[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  formKorrekturbuchungsVorgangObs!: Observable<ProjektabrechnungKorrekturbuchungVorgang | undefined>;

  @Output()
  neueBuchungHinzufuegenEvent = new EventEmitter<ProjektabrechnungKorrekturbuchungVorgang>();

  abrechnungsmonatAuswahl!: Abrechnungsmonat[];
  referenzmonatAuswahl!: Abrechnungsmonat[];
  kostenartAuswahlGefiltert: Kostenart[] = [];

  mitarbeiterFehlderMeldungen: Map<string, string> = new Map<string, string>([
    ['leerBeiUrlaub', 'Für Urlaub muss Mitarbeiter ausgewählt sein'],
    ['externBeiUrlaub', 'Für Urlaub muss Mitarbeiter intern sein'],
  ]);

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  constructor(private fb: FormBuilder) {
    this.korrekturbuchungFormGroup = this.fb.nonNullable.group(
      {
        abrechnungsmonat: [
          {} as Abrechnungsmonat,
          [
            Validators.required,
            CustomValidatorProjektabrechnung.abrechnungsmonatIstNichtAbgeschlossen(),
            CustomValidatorProjektabrechnung.abrechnungsmonatIstNichtLeer(),
          ],
        ],
        mitarbeiter: [{} as Mitarbeiter],
        kostenart: [
          {} as Kostenart,
          [
            Validators.required,
            CustomValidator.objektIstNichtLeer(),
            CustomValidatorProjektabrechnung.korrekturbuchungKostenartGueltig(),
            CustomValidatorProjektabrechnung.korrekturbuchungRabattIstMoeglichFuerProjektTyp(),
          ],
        ],
        referenzmonat: [{} as Abrechnungsmonat],
        projektComboBoxKorrektur: [{} as Projekt, [Validators.required]],
        stundenKorrektur: [''],
        betragKostensatzKorrektur: [''],
        kostenKorrektur: [''],
        faktStundenKorrektur: [''],
        betragStundensatzKorrektur: [''],
        leistungKorrektur: [''],
        bemerkungKorrektur: ['', [Validators.required]],
        projektComboBoxGegen: [{} as Projekt],
        stundenGegen: [''],
        betragKostensatzGegen: [''],
        kostenGegen: [''],
        faktStundenGegen: [''],
        betragStundensatzGegen: [''],
        leistungGegen: [''],
        bemerkungGegen: [''],
      },
      {
        validators: [
          CustomValidatorProjektabrechnung.korrekturbuchungBuchungsBetraegeSindBedingtVorhanden(),
          CustomValidatorProjektabrechnung.korrekturbuchungGegenbuchungIstValide(),
          CustomValidatorProjektabrechnung.korrekturbuchungBeiUrlaubIstMitarbeiterValide(),
        ],
      }
    );
    this.korrekturbuchungFormGroup.disable();
  }

  ngOnInit(): void {
    this.setzeFormUndMonatsAuswahlZurueck();

    this.hinzufuegenVonAktivierungsListener();

    this.hinzufuegenVonValueSetListener();

    this.reagiereAufAenderungVonKorrekturbuchungsVorgangsForm();
  }

  reagiereAufProjektabrechnungKorrekturbuchungHinzufuegen() {
    if (this.korrekturbuchungFormGroup.invalid) {
      this.korrekturbuchungFormGroup.markAllAsTouched();
      return;
    }
    this.neueBuchungHinzufuegenEvent.emit(this.erstelleKorrekturbuchungsVorgangAusForm());
    this.leereForm();
  }

  leereForm() {
    const projekt = this.korrekturbuchungFormGroup.getRawValue().projektComboBoxKorrektur;
    this.setzeFormUndMonatsAuswahlZurueck();
    this.setzeDefaultAktivierungsStatus();
    setTimeout(() => {
      this.formRef.resetForm();
      this.korrekturbuchungFormGroup.get('projektComboBoxKorrektur')?.setValue(projekt);
    }, 100);
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return getAbgeschlossenIcon(abrechnungsmonat);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.korrekturbuchungFormGroup, komponentenName, erlaubteExp);
  }

  private reagiereAufAenderungVonKorrekturbuchungsVorgangsForm() {
    this.formKorrekturbuchungsVorgangObs.subscribe(
      (korrekturbuchungsVorgang: ProjektabrechnungKorrekturbuchungVorgang | undefined) => {
        this.setzeFormUndMonatsAuswahlZurueck(korrekturbuchungsVorgang);
        if (!korrekturbuchungsVorgang) {
          return;
        }
        this.korrekturbuchungFormGroup.patchValue(this.ladeKorrekturbuchungsVorgangInForm(korrekturbuchungsVorgang));
        //Notwendig, damit wenn kein Korrekturbuchungssatz ausgewählt wird
        // und keine valueChange trigger laufen, felder enabled werden
        this.aktualisiereFormFelderAktivStatus();
      }
    );
  }

  private hinzufuegenVonAktivierungsListener() {
    this.korrekturbuchungFormGroup.get('kostenart')!.valueChanges.subscribe((kostenart: Kostenart) => {
      if (!kostenart || !kostenart.id) {
        return;
      }
      this.aktualisiereFormFelderAktivStatus();
    });

    this.korrekturbuchungFormGroup.get('projektComboBoxGegen')!.valueChanges.subscribe((projekt2: Projekt) => {
      if (!projekt2 || !projekt2.id) {
        return;
      }
      this.aktualisiereFormFelderAktivStatus();
    });
  }

  private aktualisiereFormFelderAktivStatus() {
    this.setzeDefaultAktivierungsStatus();
    const projekt: Projekt = this.korrekturbuchungFormGroup.getRawValue().projektComboBoxKorrektur;
    const kostenart: Kostenart = this.korrekturbuchungFormGroup.getRawValue().kostenart;
    const projektGegenbuchung: Projekt = this.korrekturbuchungFormGroup.getRawValue().projektComboBoxGegen;

    if (this.pruefeObProjektIntern(projekt)) {
      this.deaktiviereLeistungsFelderBuchung();
    }
    if (this.pruefeObProjektIntern(projektGegenbuchung)) {
      this.deaktiviereLeistungsFelderGegenbuchung();
    }

    if (kostenart && kostenart.id) {
      this.deaktiviereFormFelderNachKostenart(kostenart);
    }
  }

  private hinzufuegenVonValueSetListener() {
    this.korrekturbuchungFormGroup.valueChanges.subscribe(() => {
      this.berechneAbhaengigeFelder();

      this.korrekturbuchungFormGroup.updateValueAndValidity({ emitEvent: false });
    });
  }

  private berechneAbhaengigeFelder() {
    this.berechnekostenKorrektur();
    this.berechnekostenGegen();
    this.berechneleistungKorrektur();
    this.berechneleistungGegen();

    if (
      this.korrekturbuchungFormGroup.getRawValue().kostenart &&
      this.korrekturbuchungFormGroup.getRawValue().kostenart.id
    ) {
      this.berechnefaktStundenKorrektur();
      this.berechnefaktStundenGegen();
    }
  }

  private berechnekostenKorrektur() {
    const kostenKorrektur = this.berechneProdukt(
      this.korrekturbuchungFormGroup.getRawValue().stundenKorrektur,
      this.korrekturbuchungFormGroup.getRawValue().betragKostensatzKorrektur
    );
    this.korrekturbuchungFormGroup.get('kostenKorrektur')?.setValue(kostenKorrektur, { emitEvent: false });
  }

  private berechnekostenGegen() {
    const kostenGegen = this.berechneProdukt(
      this.korrekturbuchungFormGroup.getRawValue().stundenGegen,
      this.korrekturbuchungFormGroup.getRawValue().betragKostensatzGegen
    );
    this.korrekturbuchungFormGroup.get('kostenGegen')?.setValue(kostenGegen, { emitEvent: false });
  }

  private berechneleistungKorrektur() {
    const leistungKorrektur = this.berechneProdukt(
      this.korrekturbuchungFormGroup.getRawValue().faktStundenKorrektur,
      this.korrekturbuchungFormGroup.getRawValue().betragStundensatzKorrektur
    );
    this.korrekturbuchungFormGroup.get('leistungKorrektur')?.setValue(leistungKorrektur, { emitEvent: false });
  }

  private berechneleistungGegen() {
    const leistungGegen = this.berechneProdukt(
      this.korrekturbuchungFormGroup.getRawValue().faktStundenGegen,
      this.korrekturbuchungFormGroup.getRawValue().betragStundensatzGegen
    );
    this.korrekturbuchungFormGroup.get('leistungGegen')?.setValue(leistungGegen, { emitEvent: false });
  }

  private berechnefaktStundenKorrektur() {
    if (!this.pruefeObProjektIntern(this.korrekturbuchungFormGroup.getRawValue().projektComboBoxKorrektur)) {
      if (this.korrekturbuchungFormGroup.getRawValue().kostenart.bezeichnung === KostenartEnum.PROJEKTZEITEN) {
        this.korrekturbuchungFormGroup
          .get('faktStundenKorrektur')
          ?.setValue(this.korrekturbuchungFormGroup.getRawValue().stundenKorrektur, { emitEvent: false });
      }
    }
  }

  private berechnefaktStundenGegen() {
    if (!this.pruefeObProjektIntern(this.korrekturbuchungFormGroup.getRawValue().projektComboBoxGegen)) {
      if (this.korrekturbuchungFormGroup.getRawValue().kostenart.bezeichnung === KostenartEnum.PROJEKTZEITEN) {
        this.korrekturbuchungFormGroup
          .get('faktStundenGegen')
          ?.setValue(this.korrekturbuchungFormGroup.getRawValue().stundenGegen, { emitEvent: false });
      }
    }
  }

  private berechneProdukt(zahl1S: string, zahl2S: string) {
    const zahl1 = convertDeStringZuUsNummer(zahl1S);
    const zahl2 = convertDeStringZuUsNummer(zahl2S);
    const faktor1 = !zahl1 || zahl1 === 0 ? 1 : zahl1;
    const faktor2 = !zahl2 || zahl2 === 0 ? 0 : zahl2;
    const produkt = faktor1 * faktor2;
    return isNaN(produkt) || produkt === 0 ? '' : convertUsNummerZuDeString(rundeNummerAufZweiNackommastellen(produkt));
  }

  private deaktiviereFormFelderNachKostenart(kostenart: Kostenart) {
    if (kostenart.bezeichnung === KostenartEnum.SKONTO || kostenart.bezeichnung === KostenartEnum.RABATTE) {
      this.deaktiviereFelderFuerRabattModus();
    }

    if (!this.pruefeObKostenartTypStunden(kostenart)) {
      this.deaktiviereStundenFelder();
      this.deaktivereFaktStundenFelder();
    } else if (kostenart.bezeichnung === KostenartEnum.PROJEKTZEITEN) {
      this.deaktivereFaktStundenFelder();
    }
  }

  private setzeDefaultAktivierungsStatus() {
    this.korrekturbuchungFormGroup.enable({ emitEvent: false });
    this.korrekturbuchungFormGroup.get('projektComboBoxKorrektur')?.disable();
    this.korrekturbuchungFormGroup.get('kostenKorrektur')?.disable();
    this.korrekturbuchungFormGroup.get('leistungKorrektur')?.disable();
    this.korrekturbuchungFormGroup.get('kostenGegen')?.disable();
    this.korrekturbuchungFormGroup.get('leistungGegen')?.disable();
  }

  private deaktiviereLeistungsFelderBuchung() {
    this.korrekturbuchungFormGroup.get('faktStundenKorrektur')?.reset();
    this.korrekturbuchungFormGroup.get('faktStundenKorrektur')?.disable();
    this.korrekturbuchungFormGroup.get('betragStundensatzKorrektur')?.reset();
    this.korrekturbuchungFormGroup.get('betragStundensatzKorrektur')?.disable();
    this.korrekturbuchungFormGroup.get('leistungKorrektur')?.reset();
    this.korrekturbuchungFormGroup.get('leistungKorrektur')?.disable();
  }

  private deaktiviereLeistungsFelderGegenbuchung() {
    this.korrekturbuchungFormGroup.get('faktStundenGegen')?.reset();
    this.korrekturbuchungFormGroup.get('faktStundenGegen')?.disable();
    this.korrekturbuchungFormGroup.get('betragStundensatzGegen')?.reset();
    this.korrekturbuchungFormGroup.get('betragStundensatzGegen')?.disable();
    this.korrekturbuchungFormGroup.get('leistungGegen')?.reset();
    this.korrekturbuchungFormGroup.get('leistungGegen')?.disable();
  }

  private deaktiviereFelderFuerRabattModus() {
    this.korrekturbuchungFormGroup.disable({ emitEvent: false });
    this.korrekturbuchungFormGroup.reset(
      {
        abrechnungsmonat: this.korrekturbuchungFormGroup.getRawValue().abrechnungsmonat,
        mitarbeiter: this.korrekturbuchungFormGroup.getRawValue().mitarbeiter,
        kostenart: this.korrekturbuchungFormGroup.getRawValue().kostenart,
        referenzmonat: this.korrekturbuchungFormGroup.getRawValue().referenzmonat,
        projektComboBoxKorrektur: this.korrekturbuchungFormGroup.getRawValue().projektComboBoxKorrektur,
        betragStundensatzKorrektur: this.korrekturbuchungFormGroup.getRawValue().betragStundensatzKorrektur,
        bemerkungKorrektur: this.korrekturbuchungFormGroup.getRawValue().bemerkungKorrektur,
      },
      { emitEvent: false }
    );
    this.korrekturbuchungFormGroup.get('abrechnungsmonat')?.enable();
    this.korrekturbuchungFormGroup.get('mitarbeiter')?.enable();
    this.korrekturbuchungFormGroup.get('kostenart')?.enable({ emitEvent: false });
    this.korrekturbuchungFormGroup.get('referenzmonat')?.enable();
    this.korrekturbuchungFormGroup.get('betragStundensatzKorrektur')?.enable();
    this.korrekturbuchungFormGroup.get('bemerkungKorrektur')?.enable();
  }

  private deaktiviereStundenFelder() {
    this.korrekturbuchungFormGroup.get('stundenKorrektur')?.reset();
    this.korrekturbuchungFormGroup.get('stundenKorrektur')?.disable();

    this.korrekturbuchungFormGroup.get('stundenGegen')?.reset();
    this.korrekturbuchungFormGroup.get('stundenGegen')?.disable();
  }

  private deaktivereFaktStundenFelder() {
    this.korrekturbuchungFormGroup.get('faktStundenKorrektur')?.reset();
    this.korrekturbuchungFormGroup.get('faktStundenKorrektur')?.disable();

    this.korrekturbuchungFormGroup.get('faktStundenGegen')?.reset();
    this.korrekturbuchungFormGroup.get('faktStundenGegen')?.disable();
  }

  private pruefeObKostenartTypStunden(kostenart: Kostenart) {
    return kostenart.bezeichnung === KostenartEnum.PROJEKTZEITEN || kostenart.bezeichnung === KostenartEnum.REISEZEITEN;
  }

  private pruefeObProjektIntern(projekt: Projekt): boolean {
    if (projekt && projekt.id) {
      return projekt.projekttyp === ProjekttypEnum.INTERN;
    }
    return false;
  }

  private setzeFormUndMonatsAuswahlZurueck(korrekturbuchungsPair?: ProjektabrechnungKorrekturbuchungVorgang) {
    this.korrekturbuchungFormGroup.reset({ emitEvent: false });
    this.korrekturbuchungFormGroup.disable({ emitEvent: false });

    this.erstelleAbrechnungsAuswahlAusAlleAbrechnungsmonatAuswahl(
      korrekturbuchungsPair ? korrekturbuchungsPair.korrekturbuchung : undefined
    );

    this.erstelleReferenzmonatAuswahlAusAlleAbrechnungsmonatAuswahl(
      korrekturbuchungsPair ? korrekturbuchungsPair.korrekturbuchung : undefined
    );

    this.erstelleKostenartAuswahlAusAllenKostenarten(
      korrekturbuchungsPair ? korrekturbuchungsPair.korrekturbuchung : undefined
    );
  }

  private erstelleReferenzmonatAuswahlAusAlleAbrechnungsmonatAuswahl(
    korrektubuchung?: ProjektabrechnungKorrekturbuchung
  ) {
    this.referenzmonatAuswahl = this.abrechnungsmonateGesamtauswahl.filter(
      (abrechnungsmonat) => abrechnungsmonat.abgeschlossen
    );
    if (korrektubuchung && korrektubuchung.referenzJahr && korrektubuchung.referenzMonat) {
      const extraAbrechnungsmonat = this.getExtraAbrechnungsmonatgetKorrekturbuchungAbrechnungsmonat(
        korrektubuchung.referenzJahr,
        korrektubuchung.referenzMonat
      );
      if (extraAbrechnungsmonat && !extraAbrechnungsmonat.abgeschlossen) {
        this.referenzmonatAuswahl.push(extraAbrechnungsmonat);
      }
    }
  }

  private erstelleAbrechnungsAuswahlAusAlleAbrechnungsmonatAuswahl(
    korrektubuchung?: ProjektabrechnungKorrekturbuchung
  ) {
    this.abrechnungsmonatAuswahl = this.abrechnungsmonateGesamtauswahl.filter(
      (abrechnungsmonat) => !abrechnungsmonat.abgeschlossen
    );
    if (korrektubuchung && korrektubuchung.jahr && korrektubuchung.monat) {
      const extraAbrechnungsmonat = this.getExtraAbrechnungsmonatgetKorrekturbuchungAbrechnungsmonat(
        korrektubuchung.jahr,
        korrektubuchung.monat
      );
      if (extraAbrechnungsmonat && extraAbrechnungsmonat.abgeschlossen) {
        this.abrechnungsmonatAuswahl.push(extraAbrechnungsmonat);
      }
    }
  }

  private erstelleKostenartAuswahlAusAllenKostenarten(korrektubuchung?: ProjektabrechnungKorrekturbuchung) {
    const skontoExistiert =
      !!korrektubuchung &&
      !!korrektubuchung.kostenartId &&
      this.pruefeObKostenartIdSkontoIst(korrektubuchung.kostenartId);
    this.kostenartAuswahlGefiltert = this.ermittleGueltigeKostenarten(skontoExistiert);
  }

  private pruefeObKostenartIdSkontoIst(kostenartId: string): boolean {
    const skonto = this.kostenartAuswahl.find((kostenart) => kostenart.bezeichnung === KostenartEnum.SKONTO);

    return !!skonto && skonto.id === kostenartId;
  }

  private getExtraAbrechnungsmonatgetKorrekturbuchungAbrechnungsmonat(
    extraJahr: number,
    extraMonat: number
  ): Abrechnungsmonat | undefined {
    return this.abrechnungsmonateGesamtauswahl.find((abrechnungsmonat) => {
      return abrechnungsmonat.jahr === extraJahr && abrechnungsmonat.monat === extraMonat;
    });
  }

  private ermittleGueltigeKostenarten(skontoExistiert: boolean): Kostenart[] {
    return this.kostenartAuswahl.filter(
      (k) =>
        k.bezeichnung !== KostenartEnum.FAKTFAEHIGELEISTUNGEN &&
        (k.bezeichnung !== KostenartEnum.SKONTO || skontoExistiert)
    );
  }

  private erstelleKorrekturbuchungsVorgangAusForm(): ProjektabrechnungKorrekturbuchungVorgang {
    const korrekturbuchung = {
      jahr: this.korrekturbuchungFormGroup.getRawValue().abrechnungsmonat?.jahr,
      monat: this.korrekturbuchungFormGroup.getRawValue().abrechnungsmonat?.monat,
      mitarbeiterId: this.korrekturbuchungFormGroup.getRawValue().mitarbeiter?.id,
      kostenartId: this.korrekturbuchungFormGroup.getRawValue().kostenart?.id,
      referenzJahr: this.korrekturbuchungFormGroup.getRawValue().referenzmonat?.jahr,
      referenzMonat: this.korrekturbuchungFormGroup.getRawValue().referenzmonat?.monat,
      projektId: this.korrekturbuchungFormGroup.getRawValue().projektComboBoxKorrektur?.id,
      anzahlStundenKosten: this.korrekturbuchungFormGroup.getRawValue().stundenKorrektur
        ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().stundenKorrektur)
        : undefined,
      betragKostensatz: this.korrekturbuchungFormGroup.getRawValue().betragKostensatzKorrektur
        ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().betragKostensatzKorrektur)
        : undefined,
      anzahlStundenLeistung: this.korrekturbuchungFormGroup.getRawValue().faktStundenKorrektur
        ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().faktStundenKorrektur)
        : undefined,
      betragStundensatz: this.korrekturbuchungFormGroup.getRawValue().betragStundensatzKorrektur
        ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().betragStundensatzKorrektur)
        : undefined,
      bemerkung: this.korrekturbuchungFormGroup.getRawValue().bemerkungKorrektur,
    } as ProjektabrechnungKorrekturbuchung;
    let gegenbuchung: ProjektabrechnungKorrekturbuchung = {} as ProjektabrechnungKorrekturbuchung;
    if (this.korrekturbuchungFormGroup.getRawValue().projektComboBoxGegen?.id) {
      gegenbuchung = {
        jahr: this.korrekturbuchungFormGroup.getRawValue().abrechnungsmonat?.jahr,
        monat: this.korrekturbuchungFormGroup.getRawValue().abrechnungsmonat?.monat,
        mitarbeiterId: this.korrekturbuchungFormGroup.getRawValue().mitarbeiter?.id,
        kostenartId: this.korrekturbuchungFormGroup.getRawValue().kostenart?.id,
        referenzJahr: this.korrekturbuchungFormGroup.getRawValue().referenzmonat?.jahr,
        referenzMonat: this.korrekturbuchungFormGroup.getRawValue().referenzmonat?.monat,
        projektId: this.korrekturbuchungFormGroup.getRawValue().projektComboBoxGegen?.id,
        anzahlStundenKosten: this.korrekturbuchungFormGroup.getRawValue().stundenGegen
          ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().stundenGegen)
          : undefined,
        betragKostensatz: this.korrekturbuchungFormGroup.getRawValue().betragKostensatzGegen
          ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().betragKostensatzGegen)
          : undefined,
        anzahlStundenLeistung: this.korrekturbuchungFormGroup.getRawValue().faktStundenGegen
          ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().faktStundenGegen)
          : undefined,
        betragStundensatz: this.korrekturbuchungFormGroup.getRawValue().betragStundensatzGegen
          ? convertDeStringZuUsNummer(this.korrekturbuchungFormGroup.getRawValue().betragStundensatzGegen)
          : undefined,
        bemerkung: this.korrekturbuchungFormGroup.getRawValue().bemerkungGegen,
      } as ProjektabrechnungKorrekturbuchung;
    }
    return {
      korrekturbuchung,
      gegenbuchung,
    };
  }

  private ladeKorrekturbuchungsVorgangInForm(korrekturbuchungsPair: ProjektabrechnungKorrekturbuchungVorgang) {
    const korrekturbuchung = korrekturbuchungsPair.korrekturbuchung;
    const gegenbuchung = korrekturbuchungsPair.gegenbuchung;

    const mitarbeiter = getObjektAusListeDurchId(
      korrekturbuchung.mitarbeiterId,
      this.mitarbeiterAuswahl
    ) as Mitarbeiter;
    const kostenart = getObjektAusListeDurchId(korrekturbuchung.kostenartId, this.kostenartAuswahl) as Kostenart;
    const abrechnungsmonat = this.getExtraAbrechnungsmonatgetKorrekturbuchungAbrechnungsmonat(
      korrekturbuchung.jahr,
      korrekturbuchung.monat
    );
    const referenzmonat = this.getExtraAbrechnungsmonatgetKorrekturbuchungAbrechnungsmonat(
      korrekturbuchung.referenzJahr,
      korrekturbuchung.referenzMonat
    );

    return {
      mitarbeiter: mitarbeiter ? mitarbeiter : {},
      kostenart: kostenart ? kostenart : {},
      abrechnungsmonat: abrechnungsmonat ? abrechnungsmonat : {},
      referenzmonat: referenzmonat ? referenzmonat : {},
      ...this.extrahiereFormElementeAusBuchung(korrekturbuchung),
      ...this.extrahiereFormElementeAusGegenbuchung(gegenbuchung),
    };
  }

  private extrahiereFormElementeAusBuchung(buchung: ProjektabrechnungKorrekturbuchung | undefined) {
    if (!buchung) {
      return {};
    }
    const projekt = getObjektAusListeDurchId(buchung.projektId, this.projektAuswahl) as Projekt;
    return {
      stundenKorrektur: buchung.anzahlStundenKosten ? convertUsNummerZuDeString(buchung.anzahlStundenKosten) : '',
      betragKostensatzKorrektur: buchung.betragKostensatz ? convertUsNummerZuDeString(buchung.betragKostensatz) : '',
      faktStundenKorrektur: buchung.anzahlStundenLeistung
        ? convertUsNummerZuDeString(buchung.anzahlStundenLeistung)
        : '',
      betragStundensatzKorrektur: buchung.betragStundensatz ? convertUsNummerZuDeString(buchung.betragStundensatz) : '',
      bemerkungKorrektur: buchung.bemerkung,
      projektComboBoxKorrektur: projekt ? projekt : {},
    };
  }

  private extrahiereFormElementeAusGegenbuchung(gegenbuchung: ProjektabrechnungKorrekturbuchung | undefined) {
    if (!gegenbuchung) {
      return {};
    }
    const projekt = getObjektAusListeDurchId(gegenbuchung.projektId, this.projektAuswahl) as Projekt;
    return {
      stundenGegen: convertUsNummerZuDeString(gegenbuchung.anzahlStundenKosten),
      betragKostensatzGegen: convertUsNummerZuDeString(gegenbuchung.betragKostensatz),
      faktStundenGegen: convertUsNummerZuDeString(gegenbuchung.anzahlStundenLeistung),
      betragStundensatzGegen: convertUsNummerZuDeString(gegenbuchung.betragStundensatz),
      bemerkungGegen: gegenbuchung.bemerkung,
      projektComboBoxGegen: projekt ? projekt : {},
    };
  }
}
