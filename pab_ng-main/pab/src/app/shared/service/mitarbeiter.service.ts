import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { catchError, from, map, Observable } from 'rxjs';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';
import { ErrorService } from './error.service';
import { MitarbeiterStundenKonto } from '../model/mitarbeiter/mitarbeiter-stunden-konto';
import { MitarbeiterUrlaubskonto } from '../model/mitarbeiter/mitarbeiter-urlaubskonto';
import { format } from 'date-fns';
import { setzeIdAufNullWennNegativ } from '../util/id-nuller.util';

@Injectable({
  providedIn: 'root',
})
export class MitarbeiterService {
  private mitarbeiter: Mitarbeiter[] = [];
  private mitarbeiterFuerArbeitsnachweisBearbeiten: Mitarbeiter[] = [];
  private mitarbeiterFuerMitarbeiterKonten: Mitarbeiter[] = [];

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getAlleMitarbeiter(): Observable<Mitarbeiter[]> {
    if (this.mitarbeiter.length >= 1) {
      return from([this.mitarbeiter]);
    }

    const url = `${environment.api}/mitarbeiter/all`;
    const obs: Observable<Mitarbeiter[]> = this.http.get<Mitarbeiter[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
    obs.subscribe((value) => {
      this.mitarbeiter = value;
    });
    return obs;
  }

  getMitarbeiterAuswahlFuerMitarbeiterKonten(): Observable<Mitarbeiter[]> {
    if (this.mitarbeiterFuerMitarbeiterKonten.length >= 1) {
      return from([this.mitarbeiterFuerMitarbeiterKonten]);
    }

    const url = `${environment.api}/mitarbeiter/selectOptions?aktiveMitarbeiter=true&interneMitarbeiter=true`;
    const obs: Observable<Mitarbeiter[]> = this.http.get<Mitarbeiter[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
    obs.subscribe((value) => {
      this.mitarbeiterFuerMitarbeiterKonten = value;
    });
    return obs;
  }

  getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(): Observable<Mitarbeiter[]> {
    if (this.mitarbeiterFuerArbeitsnachweisBearbeiten.length >= 1) {
      return from([this.mitarbeiterFuerArbeitsnachweisBearbeiten]);
    }

    const url = `${environment.api}/mitarbeiter/selectOptions?aktiveMitarbeiter=true&interneMitarbeiter=true&externeMitarbeiter=true&beruecksichtigeEintrittsdatum=true&alleMitarbeiterMitArbeitsnachweis=true`;
    const obs: Observable<Mitarbeiter[]> = this.http.get<Mitarbeiter[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
    obs.subscribe((value) => {
      this.mitarbeiterFuerArbeitsnachweisBearbeiten = value;
    });
    return obs;
  }

  getAlleSachbearbeiter(): Observable<Mitarbeiter[]> {
    return this.getAlleMitarbeiter().pipe(
      map((mitarbeiterListe: Mitarbeiter[]) => this.extrahiereSachbearbeiter(mitarbeiterListe))
    );
  }

  getStundenkontoVonMitarbeiter(id: string): Observable<MitarbeiterStundenKonto[]> {
    const url = `${environment.api}/mitarbeiter/${id}/stundenkonto`;
    return this.http.get<MitarbeiterStundenKonto[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((mitarbeiterStundenkontoListe: MitarbeiterStundenKonto[]) =>
        this.transformiereZuMitarbeiterStundenkontoListe(mitarbeiterStundenkontoListe)
      )
    );
  }

  speichernStundenkontoVonMitarbeiter(
    id: string,
    stundenkontoList: MitarbeiterStundenKonto[]
  ): Observable<HttpResponse<any>> {
    const url = `${environment.api}/mitarbeiter/${id}/stundenkonto`;

    const stundenkontListeKopie: MitarbeiterStundenKonto[] = stundenkontoList.slice();
    setzeIdAufNullWennNegativ(stundenkontListeKopie);
    const requestBody = this.transformiereVonMitarbeiterStundenkontoListe(stundenkontListeKopie);

    return this.http.post<HttpResponse<any>>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getMitarbeiterStundenkontoMitNeuemSaldo(
    id: string,
    stundenkontoList: MitarbeiterStundenKonto[]
  ): Observable<MitarbeiterStundenKonto[]> {
    const url = `${environment.api}/mitarbeiter/${id}/stundenkonto/saldo`;

    const requestBody = this.transformiereVonMitarbeiterStundenkontoListe(stundenkontoList);

    return this.http.post<MitarbeiterStundenKonto[]>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((mitarbeiterStundenkontoListe: MitarbeiterStundenKonto[]) =>
        this.transformiereZuMitarbeiterStundenkontoListe(mitarbeiterStundenkontoListe)
      )
    );
  }

  getUrlaubskontoVonMitarbeiter(id: string): Observable<MitarbeiterUrlaubskonto[]> {
    const url = `${environment.api}/mitarbeiter/${id}/urlaubskonto`;
    return this.http.get<MitarbeiterUrlaubskonto[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((mitarbeiterUrlaubskontoListe: MitarbeiterUrlaubskonto[]) =>
        this.transformiereZuMitarbeiterUrlaubskontoListe(mitarbeiterUrlaubskontoListe)
      )
    );
  }

  speichernUrlaubskontoVonMitarbeiter(
    id: string,
    urlaubskontoListe: MitarbeiterUrlaubskonto[]
  ): Observable<HttpResponse<any>> {
    const url = `${environment.api}/mitarbeiter/${id}/urlaubskonto`;

    const urlaubskontoListeKopie: MitarbeiterUrlaubskonto[] = urlaubskontoListe.slice();
    setzeIdAufNullWennNegativ(urlaubskontoListeKopie);
    const requestBody = this.transformiereVonMitarbeiterUrlaubskontoListe(urlaubskontoListeKopie);

    return this.http.post<HttpResponse<any>>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getMitarbeiterUrlaubskontoMitNeuemSaldo(
    id: string,
    urlaubskontoList: MitarbeiterUrlaubskonto[]
  ): Observable<MitarbeiterUrlaubskonto[]> {
    const url = `${environment.api}/mitarbeiter/${id}/urlaubskonto/saldo`;

    const requestBody = this.transformiereVonMitarbeiterUrlaubskontoListe(urlaubskontoList);

    return this.http.post<MitarbeiterUrlaubskonto[]>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((mitarbeiterUrlaubskontoListe: MitarbeiterUrlaubskonto[]) =>
        this.transformiereZuMitarbeiterUrlaubskontoListe(mitarbeiterUrlaubskontoListe)
      )
    );
  }

  private extrahiereSachbearbeiter(mitarbeiterListe: Mitarbeiter[]): Mitarbeiter[] {
    const sachbearbeiterIds: string[] = mitarbeiterListe.map((mitarbeiter) => mitarbeiter.sachbearbeiterId);
    const eindeutigeSachbearbeiterIds: string[] = [...new Set(sachbearbeiterIds)];
    const gesaeuberteSachbeareiterIds: string[] = eindeutigeSachbearbeiterIds.filter((element) => element !== null);

    return gesaeuberteSachbeareiterIds.map(
      (sachbearbeiterId) => mitarbeiterListe.find((mitarbeiter) => mitarbeiter.id === sachbearbeiterId)!
    );
  }

  private transformiereVonMitarbeiterUrlaubskontoListe(
    mitarbeiterUrlaubskontoListe: MitarbeiterUrlaubskonto[]
  ): MitarbeiterUrlaubskonto[] {
    return mitarbeiterUrlaubskontoListe.map((mitarbeiterUrlaubskonto: MitarbeiterUrlaubskonto) => {
      (mitarbeiterUrlaubskonto as any).wertstellung = format(mitarbeiterUrlaubskonto.wertstellung, 'yyyy-MM-dd');
      (mitarbeiterUrlaubskonto as any).buchungsdatum = mitarbeiterUrlaubskonto.buchungsdatum.toISOString();

      return mitarbeiterUrlaubskonto;
    });
  }

  private transformiereZuMitarbeiterUrlaubskontoListe(
    mitarbeiterUrlaubskontoListe: MitarbeiterUrlaubskonto[]
  ): MitarbeiterUrlaubskonto[] {
    return mitarbeiterUrlaubskontoListe.map((mitarbeiterUrlaubskonto: MitarbeiterUrlaubskonto) => {
      mitarbeiterUrlaubskonto.wertstellung = new Date(mitarbeiterUrlaubskonto.wertstellung);
      mitarbeiterUrlaubskonto.buchungsdatum = new Date(mitarbeiterUrlaubskonto.buchungsdatum);

      return mitarbeiterUrlaubskonto;
    });
  }

  private transformiereZuMitarbeiterStundenkontoListe(
    mitarbeiterStundenkontoListe: MitarbeiterStundenKonto[]
  ): MitarbeiterStundenKonto[] {
    return mitarbeiterStundenkontoListe.map((mitarbeiterStundenkonto: MitarbeiterStundenKonto) => {
      mitarbeiterStundenkonto.wertstellung = new Date(mitarbeiterStundenkonto.wertstellung);
      mitarbeiterStundenkonto.buchungsdatum = new Date(mitarbeiterStundenkonto.buchungsdatum);

      return mitarbeiterStundenkonto;
    });
  }

  private transformiereVonMitarbeiterStundenkontoListe(
    mitarbeiterStundenkontoListe: MitarbeiterStundenKonto[]
  ): MitarbeiterStundenKonto[] {
    return mitarbeiterStundenkontoListe.map((mitarbeiterStundenkonto: MitarbeiterStundenKonto) => {
      (mitarbeiterStundenkonto as any).wertstellung = format(mitarbeiterStundenkonto.wertstellung, 'yyyy-MM-dd');
      (mitarbeiterStundenkonto as any).buchungsdatum = mitarbeiterStundenkonto.buchungsdatum.toISOString();
      return mitarbeiterStundenkonto;
    });
  }
}
