import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, from, Observable } from 'rxjs';
import { Projekt } from '../model/projekt/projekt';
import { environment } from '../../../environments/environment';
import { SonstigeProjektkosten } from '../model/projektabrechnung/sonstige-projektkosten';

@Injectable({
  providedIn: 'root',
})
export class ProjektService {
  private projekte: Projekt[] = [];

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getAlleProjekte(): Observable<Projekt[]> {
    if (this.projekte.length >= 1) {
      return from([this.projekte]);
    }

    const url = `${environment.api}/projekt/all`;

    const obs: Observable<Projekt[]> = this.http.get<Projekt[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.projekte = value;
    });
    return obs;
  }

  getAlleSonstigeProjektkosten(): Observable<SonstigeProjektkosten[]> {
    const url = `${environment.api}/projekt/abrechnung/sonstige/projektkosten/all`;

    return this.http.get<SonstigeProjektkosten[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }
}
