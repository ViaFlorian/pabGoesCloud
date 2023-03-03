import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Arbeitsnachweis } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { StatusIdZuBearbeitungsstatusEnumPipe } from '../../../../shared/pipe/status-id-zu-bearbeitungsstatus-enum.pipe';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-action-bar',
  templateUrl: './arbeitsnachweis-bearbeiten-action-bar.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-action-bar.component.scss'],
})
export class ArbeitsnachweisBearbeitenActionBarComponent implements OnChanges {
  @Input()
  aenderung!: boolean;

  @Input()
  aktuellerArbeitsnachweis: Arbeitsnachweis | undefined;

  @Input()
  arbeitsnachweisFehlerlogExistiert!: boolean;

  @Output()
  speichernEvent = new EventEmitter<SpeichernPostAktionEnum>();

  @Output()
  arbeitsnachweisImportierenEvent = new EventEmitter();

  @Output()
  belegeImportierenEvent = new EventEmitter();

  @Output()
  fehlerlogAnzeigenEvent = new EventEmitter();

  @Output()
  arbeitsnachweisAbrechnenEvent = new EventEmitter();

  darfAbgerechnetWerden: boolean = false;
  darfImportiertWerden: boolean = false;
  darfGespeichertWerden: boolean = false;
  belegeDuerfenImportierWerden: boolean = false;

  constructor(private statusIdZuBearbeitungsstatusEnumPipe: StatusIdZuBearbeitungsstatusEnumPipe) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['aktuellerArbeitsnachweis'] && changes['aktuellerArbeitsnachweis'].currentValue) {
      this.darfAbgerechnetWerden = false;
      this.darfImportiertWerden = false;
      this.belegeDuerfenImportierWerden = false;

      const aktuellerArbeitsnachweis: Arbeitsnachweis = changes['aktuellerArbeitsnachweis'].currentValue;

      if (aktuellerArbeitsnachweis.statusId) {
        const status: BearbeitungsstatusEnum = this.statusIdZuBearbeitungsstatusEnumPipe.transform(
          aktuellerArbeitsnachweis.statusId
        );
        this.darfAbgerechnetWerden = status === BearbeitungsstatusEnum.ERFASST && !this.aenderung;
        this.darfImportiertWerden = status === BearbeitungsstatusEnum.ERFASST || !aktuellerArbeitsnachweis.id;
        this.belegeDuerfenImportierWerden = status !== BearbeitungsstatusEnum.NEU;
      } else if (!aktuellerArbeitsnachweis.id) {
        this.darfImportiertWerden = true;
      }
    }
    if (changes['aenderung']) {
      this.darfGespeichertWerden = changes['aenderung'].currentValue;
      this.darfAbgerechnetWerden = false;
    }
  }

  speichern(): void {
    this.speichernEvent.emit(SpeichernPostAktionEnum.KEINE);
  }

  speichernUndSchliessen(): void {
    this.speichernEvent.emit(SpeichernPostAktionEnum.SCHLIESSEN);
  }

  speichernUndNeu(): void {
    this.speichernEvent.emit(SpeichernPostAktionEnum.NEU);
  }

  arbeitsnachweisImportierenDialogOeffnen() {
    this.arbeitsnachweisImportierenEvent.emit();
  }

  belegeImportierenDialogOeffnen() {
    this.belegeImportierenEvent.emit();
  }

  abrechnen() {
    this.arbeitsnachweisAbrechnenEvent.emit();
  }
}
