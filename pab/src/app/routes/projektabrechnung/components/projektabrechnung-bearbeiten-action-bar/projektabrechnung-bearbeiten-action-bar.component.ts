import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Projektabrechnung } from '../../../../shared/model/projektabrechnung/projektabrechnung';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { Arbeitsnachweis } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { StatusIdZuBearbeitungsstatusEnumPipe } from '../../../../shared/pipe/status-id-zu-bearbeitungsstatus-enum.pipe';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-action-bar',
  templateUrl: './projektabrechnung-bearbeiten-action-bar.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-action-bar.component.scss'],
})
export class ProjektabrechnungBearbeitenActionBarComponent implements OnChanges {
  @Input()
  aenderung!: boolean;

  @Input()
  offen!: boolean;

  @Input()
  aktuelleProjektabrechnung: Projektabrechnung | undefined;

  @Input()
  istFestpreisProjekt!: boolean;

  @Input()
  istProjektKunde!: boolean;

  @Output()
  speichernEvent = new EventEmitter<SpeichernPostAktionEnum>();

  @Output()
  projektabrechnungAbrechnenEvent = new EventEmitter();

  @Output()
  oeffneFertigstellungDialogEvent = new EventEmitter();

  @Output()
  oeffneFakturuebersichtEvent = new EventEmitter();

  darfAbgerechnetWerden: boolean = false;
  darfGespeichertWerden: boolean = false;

  constructor(private statusIdZuBearbeitungsstatusEnumPipe: StatusIdZuBearbeitungsstatusEnumPipe) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['aktuelleProjektabrechnung'] && changes['aktuelleProjektabrechnung'].currentValue) {
      this.darfAbgerechnetWerden = false;

      const aktuelleProjektabrechnung: Arbeitsnachweis = changes['aktuelleProjektabrechnung'].currentValue;

      if (aktuelleProjektabrechnung.statusId) {
        const status: BearbeitungsstatusEnum = this.statusIdZuBearbeitungsstatusEnumPipe.transform(
          aktuelleProjektabrechnung.statusId
        );
        this.darfAbgerechnetWerden = status === BearbeitungsstatusEnum.ERFASST && !this.aenderung;
        if (this.istFestpreisProjekt) {
          this.darfAbgerechnetWerden = !!this.aktuelleProjektabrechnung?.fertigstellungsgrad;
        }
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

  abrechnen() {
    this.projektabrechnungAbrechnenEvent.emit();
  }

  oeffneFertigstellungDialog() {
    this.oeffneFertigstellungDialogEvent.emit();
  }

  oeffneFakturuebersicht() {
    this.oeffneFakturuebersichtEvent.emit();
  }
}
