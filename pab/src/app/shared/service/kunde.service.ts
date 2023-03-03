import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, from, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Kunde } from '../model/kunde/kunde';

@Injectable({
  providedIn: 'root',
})
export class KundeService {
  private kunden: Kunde[] = [];

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getAlleKunden(): Observable<Kunde[]> {
    if (this.kunden.length >= 1) {
      return from([this.kunden]);
    }

    const url = `${environment.api}/kunde/all`;

    const obs: Observable<Kunde[]> = this.http.get<Kunde[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.kunden = value;
    });
    return obs;
  }
}
