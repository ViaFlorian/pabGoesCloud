import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektabrechnungUebersicht } from '../../../../shared/model/projektabrechnung/projektabrechnung-uebersicht';
import { ProjektabrechnungUebersichtTabellendarstellung } from '../../model/projektabrechnung-uebersicht-tabellendarstellung';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { debounceTime } from 'rxjs';
import {
  getObjektAusListeDurchId,
  getObjektAusListeDurchScribeId,
} from '../../../../shared/util/objekt-in-array-finden.util';
import { MitarbeiterAnzeigeNameKurzformPipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name-kurzform.pipe';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { BearbeitungsstatusEnumZuStatusIdPipe } from '../../../../shared/pipe/bearbeitungsstatus-enum-zu-status-id.pipe';
import { AktivInaktivEnum } from '../../../../shared/enum/aktiv-inaktiv.enum';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { filterMitOperator } from '../../../../shared/util/filter.util';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { BuchungsstatusProjektEnum } from '../../../../shared/enum/buchungsstatus-projekt.enum';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { ProjektabrechnungMitarbeiterPair } from '../../../../shared/model/projektabrechnung/projektabrechnung-mitarbeiter-pair';
import * as IconUtil from '../../../../shared/util/icon.util';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { ProjektabrechnungBearbeitenQueryParams } from '../../../../shared/model/query-params/projektabrechnung-bearbeiten-query-params';

@Component({
  selector: 'pab-projektabrechnung-uebersicht-tabelle',
  templateUrl: './projektabrechnung-uebersicht-tabelle.component.html',
  styleUrls: ['./projektabrechnung-uebersicht-tabelle.component.scss'],
})
export class ProjektabrechnungUebersichtTabelleComponent implements OnInit, OnChanges, AfterViewInit {
  @Input()
  projektabrechnungen: ProjektabrechnungUebersicht[] = [];

  @Input()
  projektabrechnungMitarbeiterPairs: ProjektabrechnungMitarbeiterPair[] = [];

  @Input()
  formGroup!: FormGroup;

  @Input()
  sachbearbeiterAuswahl!: Mitarbeiter[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAuswahl!: Kunde[];

  @Input()
  organisationseinheitAuswahl!: Organisationseinheit[];

  @Input()
  filterProjektabrechnungenSpinner!: SpinnerRef;

  @Output()
  anzahlProjektabrechnungenEvent = new EventEmitter<number>(true);

  @Output()
  listeAnMitarbeiternEvent = new EventEmitter<string[]>(true);

  projektabrechnungenTabellendarstellung: ProjektabrechnungUebersichtTabellendarstellung[] = [];

  spalten = [
    { id: 'actionEdit', name: 'Action Edit' },
    { id: 'istKorrektur', name: 'Ist Korrektur' },
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'projektBezeichnung', name: 'Projekt Bezeichnung' },
    { id: 'projekttyp', name: 'Projekttyp' },
    { id: 'organisationseinheitName', name: 'Organisationseinheit' },
    { id: 'kundeName', name: 'Kunde' },
    { id: 'sachbearbeiterName', name: 'Sachbearbeiter:in' },
    { id: 'anzahlMitarbeiter', name: 'Anzahl Mitarbeiter' },
    { id: 'kosten', name: 'Kosten' },
    { id: 'leistung', name: 'Leistung' },
    { id: 'statusId', name: 'Bearbeitungsstatus' },
  ];

  tabelle: TableData<ProjektabrechnungUebersichtTabellendarstellung>;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private navigationService: NavigationService,
    private bearbeitungsstatusEnumZuStatusIdPipe: BearbeitungsstatusEnumZuStatusIdPipe,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    private mitarbeiterAnzeigeNameKurzformPipe: MitarbeiterAnzeigeNameKurzformPipe
  ) {
    this.tabelle = new TableData<ProjektabrechnungUebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektabrechnungUebersichtTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngOnInit(): void {
    this.formGroup.valueChanges.pipe(debounceTime(500)).subscribe(() => {
      if (!this.formGroup.valid) {
        return;
      }
      this.filtereProjektabrechnungenUndAktualisiereTabelle();
    });
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(): void {
    this.aktualisiereProjektabrechnungenUebersichtTabellendarstellung();
    this.filtereProjektabrechnungenUndAktualisiereTabelle();
  }

  getAbgeschlossenIcon(bearbeitungsstatus: BearbeitungsstatusEnum): string {
    return IconUtil.getAbgeschlossenIconMitBoolean(bearbeitungsstatus === BearbeitungsstatusEnum.ABGESCHLOSSEN);
  }

  navigiereZuProjektabrechnungBearbeiten(
    projektabrechnungUebersichtTabellendarstellung: ProjektabrechnungUebersichtTabellendarstellung
  ): void {
    const queryParams: ProjektabrechnungBearbeitenQueryParams = {
      jahr: projektabrechnungUebersichtTabellendarstellung.jahr,
      monat: projektabrechnungUebersichtTabellendarstellung.monat,
      projektId: projektabrechnungUebersichtTabellendarstellung.projekt.id,
    };
    const url: string = '/projektabrechnung/bearbeiten';
    this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
  }

  private filtereProjektabrechnungenUndAktualisiereTabelle(): void {
    if (this.projektabrechnungenTabellendarstellung.length === 0) {
      this.listeAnMitarbeiternEvent.emit([]);
      return;
    }

    this.filterProjektabrechnungenSpinner.open();

    let projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[] =
      this.projektabrechnungenTabellendarstellung.slice();

    projektabrechnungenGefiltert = this.filterNachSachbearbeiter(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachBearbeitungsstatus(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachAktivInaktiv(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachProjekt(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachBuchungsstatus(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachProjekttyp(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachOrganisationseinheit(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachKunde(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachMitarbeiter(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachKosten(projektabrechnungenGefiltert);
    projektabrechnungenGefiltert = this.filterNachLeistung(projektabrechnungenGefiltert);

    this.tabelle.data = projektabrechnungenGefiltert;
    this.anzahlProjektabrechnungenEvent.emit(projektabrechnungenGefiltert.length);
    this.aktualisiereMitarbeiterAuswahl(projektabrechnungenGefiltert);

    this.filterProjektabrechnungenSpinner.close();
  }

  private aktualisiereMitarbeiterAuswahl(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ) {
    const projektabrechnungIds: string[] = projektabrechnungenGefiltert
      .filter((projektabrechnungenGefiltert) => projektabrechnungenGefiltert.projektabrechnungId)
      .map((projektabrechnungenGefiltert) => projektabrechnungenGefiltert.projektabrechnungId);
    const mitarbeiterIds: string[] = this.projektabrechnungMitarbeiterPairs
      .filter((projektabrechnungMitarbeiterPair) =>
        projektabrechnungIds.includes(projektabrechnungMitarbeiterPair.projektabrechnungId)
      )
      .map((projektabrechnungMitarbeiterPair) => projektabrechnungMitarbeiterPair.mitarbeiterId);
    this.listeAnMitarbeiternEvent.emit([...new Set(mitarbeiterIds)]);
  }

  private filterNachSachbearbeiter(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const sachbearbeiter: Mitarbeiter = this.formGroup.getRawValue().sachbearbeiter;
    if (!sachbearbeiter || !sachbearbeiter.id) {
      return projektabrechnungenGefiltert;
    }

    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungUebersicht.sachbearbeiter?.id === sachbearbeiter.id;
    });
  }

  private filterNachBearbeitungsstatus(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const bearbeitungsstatus: BearbeitungsstatusEnum = this.formGroup.getRawValue().bearbeitungsstatus;
    if (bearbeitungsstatus === BearbeitungsstatusEnum.ALLE) {
      return projektabrechnungenGefiltert;
    }

    const statusId: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(bearbeitungsstatus);
    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungUebersicht.statusId === statusId;
    });
  }

  private filterNachAktivInaktiv(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const aktivInaktiv: AktivInaktivEnum = this.formGroup.getRawValue().aktivInaktiv;
    switch (aktivInaktiv) {
      case AktivInaktivEnum.ALLE: {
        return projektabrechnungenGefiltert;
      }
      case AktivInaktivEnum.AKTIV: {
        return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
          return projektabrechnungUebersicht.projekt.istAktiv;
        });
      }
      case AktivInaktivEnum.INAKTIV: {
        return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
          return !projektabrechnungUebersicht.projekt.istAktiv;
        });
      }
    }
  }

  private filterNachProjekttyp(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const projekttypEnum: ProjekttypEnum = this.formGroup.getRawValue().projekttyp;
    if (projekttypEnum === ProjekttypEnum.ALLE) {
      return projektabrechnungenGefiltert;
    }

    // Typ ist z.T. in LowerCase, z.B. mit Großbuchstaben am Wortanfang
    const projekttypEnumLowerCase: string = projekttypEnum.toString().toLowerCase();
    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return (
        projektabrechnungUebersicht.projekt.projekttyp === projekttypEnumLowerCase ||
        projektabrechnungUebersicht.projekt.projekttyp === projekttypEnum
      );
    });
  }

  private filterNachProjekt(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const projekt: Projekt = this.formGroup.getRawValue().projekt;
    if (!projekt || !projekt.id) {
      return projektabrechnungenGefiltert;
    }

    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungUebersicht.projekt?.id === projekt.id;
    });
  }

  private filterNachBuchungsstatus(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const buchungsstatusProjektEnum: BuchungsstatusProjektEnum = this.formGroup.getRawValue().buchungsstatus;
    switch (buchungsstatusProjektEnum) {
      case BuchungsstatusProjektEnum.ALLE: {
        return projektabrechnungenGefiltert;
      }
      case BuchungsstatusProjektEnum.BEBUCHTE_PROJEKTE: {
        return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
          return projektabrechnungUebersicht.projektabrechnungId;
        });
      }
      case BuchungsstatusProjektEnum.NICHT_BEBUCHTE_PROJEKTE: {
        return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
          return !projektabrechnungUebersicht.projektabrechnungId;
        });
      }
    }
  }

  private filterNachOrganisationseinheit(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const organisationseinheit: Organisationseinheit = this.formGroup.getRawValue().organisationseinheit;
    if (!organisationseinheit || !organisationseinheit.id) {
      return projektabrechnungenGefiltert;
    }

    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungUebersicht.organisationseinheit?.id === organisationseinheit.id;
    });
  }

  private filterNachKunde(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const kunde: Kunde = this.formGroup.getRawValue().kunde;
    if (!kunde || !kunde.id) {
      return projektabrechnungenGefiltert;
    }

    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungUebersicht.kunde?.id === kunde.id;
    });
  }

  private filterNachMitarbeiter(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const mitarbeiter: Mitarbeiter = this.formGroup.getRawValue().mitarbeiter;
    if (!mitarbeiter || !mitarbeiter.id) {
      return projektabrechnungenGefiltert;
    }

    const projektabrechnungIds: string[] = this.projektabrechnungMitarbeiterPairs
      .filter((pair) => pair.mitarbeiterId === mitarbeiter.id)
      .map((pair) => pair.projektabrechnungId);

    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return projektabrechnungIds.includes(projektabrechnungUebersicht.projektabrechnungId);
    });
  }

  private filterNachKosten(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const kostenFilter: string = this.formGroup.getRawValue().kostenFilter;
    if (!kostenFilter) {
      return projektabrechnungenGefiltert;
    }

    const kostenOperator: OperatorEnum = this.formGroup.getRawValue().kostenOperator;
    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return filterMitOperator(kostenOperator, projektabrechnungUebersicht.kosten, Number.parseFloat(kostenFilter));
    });
  }

  private filterNachLeistung(
    projektabrechnungenGefiltert: ProjektabrechnungUebersichtTabellendarstellung[]
  ): ProjektabrechnungUebersichtTabellendarstellung[] {
    const leistungFilter: string = this.formGroup.getRawValue().leistungFilter;
    if (!leistungFilter) {
      return projektabrechnungenGefiltert;
    }

    const leistungOperator: OperatorEnum = this.formGroup.getRawValue().leistungOperator;
    return projektabrechnungenGefiltert.filter((projektabrechnungUebersicht) => {
      return filterMitOperator(
        leistungOperator,
        projektabrechnungUebersicht.leistung,
        Number.parseFloat(leistungFilter)
      );
    });
  }

  private aktualisiereProjektabrechnungenUebersichtTabellendarstellung(): void {
    this.projektabrechnungenTabellendarstellung = this.projektabrechnungen.map((projektabrechnung) => {
      const sachbearbeiter: Mitarbeiter = getObjektAusListeDurchId(
        projektabrechnung.sachbearbeiterId,
        this.sachbearbeiterAuswahl
      ) as Mitarbeiter;
      const projekt: Projekt = getObjektAusListeDurchId(projektabrechnung.projektId, this.projektAuswahl) as Projekt;
      const organisationseinheit: Organisationseinheit = getObjektAusListeDurchScribeId(
        projektabrechnung.organisationseinheitId,
        this.organisationseinheitAuswahl
      ) as Organisationseinheit;
      const kunde: Kunde = getObjektAusListeDurchScribeId(projektabrechnung.kundeId, this.kundeAuswahl) as Kunde;

      return {
        projektabrechnungId: projektabrechnung.projektabrechnungId,
        jahr: projektabrechnung.jahr,
        monat: projektabrechnung.monat,
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(projektabrechnung),
        statusId: projektabrechnung.statusId,
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        projektBezeichnung: projekt.bezeichnung,
        projekttyp: projekt.projekttyp,
        organisationseinheit: organisationseinheit,
        organisationseinheitName: organisationseinheit.bezeichnung,
        kunde: kunde,
        kundeName: kunde.bezeichnung,
        sachbearbeiter: sachbearbeiter,
        sachbearbeiterName: this.mitarbeiterAnzeigeNameKurzformPipe.transform(sachbearbeiter),
        anzahlMitarbeiter: projektabrechnung.anzahlMitarbeiter,
        hatKorrekturbuchungen: projektabrechnung.anzahlKorrekturbuchungen > 0,
        kosten: projektabrechnung.kosten,
        leistung: projektabrechnung.leistung,
      } as ProjektabrechnungUebersichtTabellendarstellung;
    });
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ProjektabrechnungUebersichtTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.data = this.projektabrechnungenTabellendarstellung;
  }

  private initialisiereTabellenSortierung(): void {
    this.tabelle.sortParams = ['abrechnungsmonat', 'projektnummer'];
    this.tabelle.sortDirs = ['desc', 'asc'];

    this.tabelle.updateSortHeaders();
  }
}
