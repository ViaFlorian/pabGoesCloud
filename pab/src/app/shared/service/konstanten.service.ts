import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, from, map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Belegart } from '../model/konstanten/belegart';
import { ProjektstundeTyp } from '../model/konstanten/projektstunde-typ';
import { BuchungstypUrlaub } from '../model/konstanten/buchungstyp-urlaub';
import { BuchungstypStunden } from '../model/konstanten/buchungstyp-stunden';
import { Lohnart } from '../model/konstanten/lohnart';
import { ViadeeAuslagenKostenart } from '../model/konstanten/viadee-auslagen-kostenart';
import { ProjektstundeTypArtenEnum } from '../enum/projektstunde-typ-arten.enum';
import { MitarbeiterTyp } from '../model/konstanten/mitarbeiter-typ';
import { MitarbeiterTypArtenEnum } from '../enum/mitarbeiter-typ-arten.enum';
import { Kostenart } from '../model/konstanten/kostenart';
import { BenutzerService } from './benutzer.service';
import { ViadeeAuslagenKostenartArtenEnum } from '../enum/viadee-auslagen-kostenart-arten.enum';

@Injectable({
  providedIn: 'root',
})
export class KonstantenService {
  private staedte: string[] = [];
  private belegarten: Belegart[] = [];
  private projektstundeTypen: ProjektstundeTyp[] = [];
  private mitarbeiterTypen: MitarbeiterTyp[] = [];
  private buchungstypStunden: BuchungstypStunden[] = [];
  private buchungstypUrlaub: BuchungstypUrlaub[] = [];
  private lohnarten: Lohnart[] = [];
  private viadeeAuslagenKostenart: ViadeeAuslagenKostenart[] = [];
  private kostenart: Kostenart[] = [];

  constructor(private http: HttpClient, private errorService: ErrorService, private benutzerService: BenutzerService) {
    if (environment.test) {
      if (!this.benutzerService.istBenutzerEingeloggt()) {
        return;
      }
    }

    this.benutzerService.erzeugeBenutzerLoginListener().subscribe(() => {
      this.ladeDatenInitial();
    });
  }

  getStadtAlle(): Observable<string[]> {
    if (this.staedte.length >= 1) {
      return from([this.staedte]);
    }
    const url = `${environment.api}/konstanten/stadt`;

    const obs = this.http.get<string[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((list: any) => this.extractNamesFromStadtList(list))
    );

    obs.subscribe((value) => {
      this.staedte = value;
    });

    return obs;
  }

  getBelegartenAll(): Observable<Belegart[]> {
    if (this.belegarten.length >= 1) {
      return from([this.belegarten]);
    }
    const url = `${environment.api}/konstanten/belegart`;

    const obs = this.http.get<Belegart[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.belegarten = value;
    });

    return obs;
  }

  getBelegartByTextKurz(textKurz: string): Belegart {
    const belegart: Belegart | undefined = this.belegarten?.find((element) => element.textKurz === textKurz);
    if (!belegart) {
      this.errorService.zeigeTechnischenFehlerAn(`getBelegartById() mit unbekanntem TextKurz ${textKurz} aufgerufen.`);
    }
    return belegart as Belegart;
  }

  getBuchungsTypStundenAll(): Observable<BuchungstypStunden[]> {
    if (this.buchungstypStunden.length >= 1) {
      return from([this.buchungstypStunden]);
    }
    const url = `${environment.api}/konstanten/buchungstypStunden`;

    const obs = this.http.get<BuchungstypStunden[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.buchungstypStunden = value;
    });

    return obs;
  }

  getBuchungstypUrlaubAll(): Observable<BuchungstypUrlaub[]> {
    if (this.buchungstypUrlaub.length >= 1) {
      return from([this.buchungstypUrlaub]);
    }
    const url = `${environment.api}/konstanten/buchungstypUrlaub`;

    const obs = this.http.get<BuchungstypUrlaub[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.buchungstypUrlaub = value;
    });

    return obs;
  }

  getLohnartAll(): Observable<Lohnart[]> {
    if (this.lohnarten.length >= 1) {
      return from([this.lohnarten]);
    }
    const url = `${environment.api}/konstanten/lohnarten`;

    const obs = this.http.get<Lohnart[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.lohnarten = value;
    });

    return obs;
  }

  getViadeeAuslagenKostenartAll(): Observable<ViadeeAuslagenKostenart[]> {
    if (this.viadeeAuslagenKostenart.length >= 1) {
      return from([this.viadeeAuslagenKostenart]);
    }
    const url = `${environment.api}/konstanten/viadeeAuslagenKostenart`;

    const obs = this.http.get<ViadeeAuslagenKostenart[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.viadeeAuslagenKostenart = value;
    });

    return obs;
  }

  getKostenartAll(): Observable<Kostenart[]> {
    if (this.kostenart.length >= 1) {
      return from([this.kostenart]);
    }
    const url = `${environment.api}/konstanten/kostenart`;

    const obs = this.http.get<Kostenart[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.kostenart = value;
    });

    return obs;
  }

  getMitarbeiterTypAll(): Observable<MitarbeiterTyp[]> {
    if (this.mitarbeiterTypen.length >= 1) {
      return from([this.mitarbeiterTypen]);
    }
    const url = `${environment.api}/konstanten/mitarbeiterTyp`;

    const obs = this.http.get<MitarbeiterTyp[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.mitarbeiterTypen = value;
    });

    return obs;
  }

  getMitarbeiterTypAngestellter(): MitarbeiterTyp {
    return this.getMitarbeiterTypByTextKurz(MitarbeiterTypArtenEnum.ANGESTELLTER);
  }

  getMitarbeiterTypStudiPrakti(): MitarbeiterTyp {
    return this.getMitarbeiterTypByTextKurz(MitarbeiterTypArtenEnum.STUDI_PRAKTI);
  }

  getProjektstundeTypAll(): Observable<ProjektstundeTyp[]> {
    if (this.projektstundeTypen.length >= 1) {
      return from([this.projektstundeTypen]);
    }
    const url = `${environment.api}/konstanten/projektstundeTyp`;

    const obs = this.http.get<ProjektstundeTyp[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.projektstundeTypen = value;
    });

    return obs;
  }

  getViadeeAuslagenKostenartSonstiges(): ViadeeAuslagenKostenart {
    return this.getViadeeAuslagenKostenartByBezeichnung(ViadeeAuslagenKostenartArtenEnum.SONSTIGES);
  }

  getViadeeAuslagenKostenartReise(): ViadeeAuslagenKostenart {
    return this.getViadeeAuslagenKostenartByBezeichnung(ViadeeAuslagenKostenartArtenEnum.REISE);
  }

  getProjektstundenTypNormal(): ProjektstundeTyp {
    return this.getProjektstundenTypByTextKurz(ProjektstundeTypArtenEnum.NORMAL);
  }

  getProjektstundenTypTatsaechlicheReise(): ProjektstundeTyp {
    return this.getProjektstundenTypByTextKurz(ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT);
  }

  getProjektstundenTypAngerechneteReise(): ProjektstundeTyp {
    return this.getProjektstundenTypByTextKurz(ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT);
  }

  getProjektstundenTypRufbereitschaft(): ProjektstundeTyp {
    return this.getProjektstundenTypByTextKurz(ProjektstundeTypArtenEnum.RUFBEREITSCHAFT);
  }

  getProjektstundenTypSonderarbeitszeit(): ProjektstundeTyp {
    return this.getProjektstundenTypByTextKurz(ProjektstundeTypArtenEnum.SONDER);
  }

  getProjektstundeTypById(projektstundeTypId: string): ProjektstundeTyp {
    const projekstundeTyp: ProjektstundeTyp | undefined = this.projektstundeTypen?.find(
      (typ) => typ.id === projektstundeTypId
    );

    if (!projekstundeTyp) {
      this.errorService.zeigeTechnischenFehlerAn(
        `getProjektstundeTypById() mit unbekannter ID ${projektstundeTypId} aufgerufen.`
      );
    }
    return projekstundeTyp as ProjektstundeTyp;
  }

  private ladeDatenInitial() {
    // Wir laden einige, konstante Daten bereits vor
    this.getBelegartenAll();
    this.getBuchungsTypStundenAll();
    this.getBuchungstypUrlaubAll();
    this.getLohnartAll();
    this.getViadeeAuslagenKostenartAll();
    this.getKostenartAll();
    this.getMitarbeiterTypAll();
    this.getProjektstundeTypAll();
  }

  private extractNamesFromStadtList(list: any) {
    return list.map((value: { id: string; name: string }) => value.name);
  }

  private getProjektstundenTypByTextKurz(textKurz: string): ProjektstundeTyp {
    const projekstundeTyp: ProjektstundeTyp | undefined = this.projektstundeTypen?.find(
      (typ) => typ.textKurz === textKurz
    );
    if (!projekstundeTyp) {
      this.errorService.zeigeTechnischenFehlerAn(
        `getProjektstundenTypByTextKurz() mit unbekanntem TextKurz ${textKurz} aufgerufen.`
      );
    }
    return projekstundeTyp as ProjektstundeTyp;
  }

  private getViadeeAuslagenKostenartByBezeichnung(bezeichnung: string): ViadeeAuslagenKostenart {
    const viadeeAuslagenKostenart: ViadeeAuslagenKostenart | undefined = this.viadeeAuslagenKostenart?.find(
      (kostenart) => kostenart.bezeichnung === bezeichnung
    );
    if (!viadeeAuslagenKostenart) {
      this.errorService.zeigeTechnischenFehlerAn(
        `getViadeeAuslagenKostenartByBezeichnung() mit unbekannter Bezeichnung ${bezeichnung} aufgerufen.`
      );
    }
    return viadeeAuslagenKostenart as ViadeeAuslagenKostenart;
  }

  private getMitarbeiterTypByTextKurz(textKurz: string): MitarbeiterTyp {
    const mitarbeiterTyp: MitarbeiterTyp | undefined = this.mitarbeiterTypen?.find((typ) => typ.textKurz === textKurz);
    if (!mitarbeiterTyp) {
      this.errorService.zeigeTechnischenFehlerAn(
        `getMitarbeiterTypById() mit unbekanntem TextKurz ${textKurz} aufgerufen.`
      );
    }
    return mitarbeiterTyp as MitarbeiterTyp;
  }
}
