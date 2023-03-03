import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { MonatsabschlussAktion } from '../model/verwaltung/monatsabschluss-aktion';
import { MitarbeiterNichtBereitFuerMonatsabschluss } from '../model/verwaltung/mitarbeiter-nicht-bereit-fuer-monatsabschluss';
import { Projektabrechnung } from '../model/projektabrechnung/projektabrechnung';
import { ProjektBudget } from '../model/verwaltung/projekt-budget';
import { Faktur } from '../model/verwaltung/faktur';
import { Skonto } from '../model/verwaltung/skonto';
import { StatusListen } from '../model/sonstiges/status-listen';
import { FakturuebersichtSpeichernRequest } from '../model/backend-kommunikation/fakturuebersicht-speichern-request';
import { setzeIdAufNullWennNegativ } from '../util/id-nuller.util';

@Injectable({
  providedIn: 'root',
})
export class VerwaltungService {
  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getMonatsabschlussAktion(abrechnungsmoant: Abrechnungsmonat): Observable<MonatsabschlussAktion[]> {
    const url = `${environment.api}/verwaltung/monatsabschluss/aktionen?jahr=${abrechnungsmoant.jahr}&monat=${abrechnungsmoant.monat}`;

    return this.http.get<MonatsabschlussAktion[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getMitarbeiterNichtBereitFuerMonatsabschluss(
    abrechnungsmoant: Abrechnungsmonat
  ): Observable<MitarbeiterNichtBereitFuerMonatsabschluss[]> {
    const url = `${environment.api}/verwaltung/monatsabschluss/mitarbeiter?jahr=${abrechnungsmoant.jahr}&monat=${abrechnungsmoant.monat}`;

    return this.http.get<MitarbeiterNichtBereitFuerMonatsabschluss[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektNichtBereitFuerMonatsabschluss(abrechnungsmoant: Abrechnungsmonat): Observable<Projektabrechnung[]> {
    const url = `${environment.api}/verwaltung/monatsabschluss/projekte?jahr=${abrechnungsmoant.jahr}&monat=${abrechnungsmoant.monat}`;

    return this.http.get<Projektabrechnung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektBudgetsByProjektId(projektId: string): Observable<ProjektBudget[]> {
    const url = `${environment.api}/verwaltung/projektbudget?projektId=${projektId}`;

    return this.http.get<ProjektBudget[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getFakturenByProjektId(projektId: string): Observable<Faktur[]> {
    const url = `${environment.api}/verwaltung/faktur?projektId=${projektId}`;

    return this.http.get<Faktur[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getSkontosByProjektId(projektId: string): Observable<Skonto[]> {
    const url = `${environment.api}/verwaltung/skonto?projektId=${projektId}`;

    return this.http.get<Skonto[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  speicherFakturen(fakturen: StatusListen): Observable<void> {
    const neueFakturen: Faktur[] = (fakturen.getNeuListe() as Faktur[]).slice();
    setzeIdAufNullWennNegativ(neueFakturen);

    const aktualisierteFakturen: Faktur[] = (fakturen.getAktualisiertListe() as Faktur[]).slice();
    setzeIdAufNullWennNegativ(aktualisierteFakturen);

    const geloeschteFakturen: Faktur[] = (fakturen.getGeloeschtListe() as Faktur[]).slice();
    setzeIdAufNullWennNegativ(geloeschteFakturen);

    const requestBody: FakturuebersichtSpeichernRequest = {
      neueFakturen: neueFakturen,
      aktualisierteFakturen: aktualisierteFakturen,
      geloeschteFakturen: geloeschteFakturen,
    } as FakturuebersichtSpeichernRequest;

    const url = `${environment.api}/verwaltung/faktur`;
    return this.http.post<void>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }
}
