import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, from, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Organisationseinheit } from '../model/organisationseinheit/organisationseinheit';

@Injectable({
  providedIn: 'root',
})
export class OrganisationseinheitService {
  private organisationseinheit: Organisationseinheit[] = [];

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getAlleOrganisationseinheiten(): Observable<Organisationseinheit[]> {
    if (this.organisationseinheit.length >= 1) {
      return from([this.organisationseinheit]);
    }

    const url = `${environment.api}/organisationseinheit/all`;

    const obs: Observable<Organisationseinheit[]> = this.http.get<Organisationseinheit[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );

    obs.subscribe((value) => {
      this.organisationseinheit = value;
    });
    return obs;
  }
}
