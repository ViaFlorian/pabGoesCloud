import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { catchError, map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProjektabrechnungUebersicht } from '../model/projektabrechnung/projektabrechnung-uebersicht';
import { format } from 'date-fns';
import { ProjektabrechnungMitarbeiterPair } from '../model/projektabrechnung/projektabrechnung-mitarbeiter-pair';
import { Projekt } from '../model/projekt/projekt';
import { ProjektAbrechnungsmonat } from '../model/projektabrechnung/projekt-abrechnungsmonat';
import { Projektabrechnung } from '../model/projektabrechnung/projektabrechnung';
import { ProjektabrechnungKostenLeistung } from '../model/projektabrechnung/projektabrechnung-kosten-leistung';
import { ProjektabrechnungKorrekturbuchung } from '../model/projektabrechnung/projektabrechnung-korrekturbuchung';
import { SonstigeProjektkosten } from '../model/projektabrechnung/sonstige-projektkosten';
import { SonstigeProjektkostenResponse } from '../model/projektabrechnung/sonstige-projektkosten-response';
import { ProjektabrechnungSonstige } from '../model/projektabrechnung/projektabrechnung-sonstige';
import { ProjektabrechnungSonderarbeit } from '../model/projektabrechnung/projektabrechnung-sonderarbeit';
import { ProjektabrechnungReise } from '../model/projektabrechnung/projektabrechnung-reise';
import { ProjektabrechnungProjektzeit } from '../model/projektabrechnung/projektabrechnung-projektzeit';
import { ProjektabrechnungKorrekturbuchungVorgang } from '../model/projektabrechnung/projektabrechnung-korrekturbuchung-vorgang';
import { setzeIdAufNullWennNegativ } from '../util/id-nuller.util';
import { ProjektabrechnungBerechneteLeistung } from '../model/projektabrechnung/projektabrechnung-berechnete-leistung';
import { ProjektabrechnungFertigstellungInitialDaten } from '../model/projektabrechnung/projektabrechnung-fertigstellung-initial-daten';
import { ValdiatorError } from '../model/error/valdiator-error';

@Injectable({
  providedIn: 'root',
})
export class ProjektabrechnungService {
  constructor(private http: HttpClient, private errorService: ErrorService) {}

  getProjektabrechnungenUebersicht(abWann: Date, bisWann: Date): Observable<ProjektabrechnungUebersicht[]> {
    const url =
      `${environment.api}/projektabrechnung/uebersicht?` +
      `abWann=${format(abWann, 'yyyy-MM-dd')}&bisWann=${format(bisWann, 'yyyy-MM-dd')}`;
    return this.http.get<ProjektabrechnungUebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getUebersichtZuAlleProjekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
    abWann: Date,
    bisWann: Date
  ): Observable<ProjektabrechnungUebersicht[]> {
    const url =
      `${environment.api}/projektabrechnung/abrechnungsmonatFehlt?` +
      `abWann=${format(abWann, 'yyyy-MM-dd')}&bisWann=${format(bisWann, 'yyyy-MM-dd')}`;

    return this.http.get<ProjektabrechnungUebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  speichernViadeeAuslage(
    geloeschteSonstigeProjektkostenListe: SonstigeProjektkosten[],
    aktualisierteSonstigeProjektkostenListe: SonstigeProjektkosten[],
    neueSonstigeProjektkostenListe: SonstigeProjektkosten[]
  ): Observable<SonstigeProjektkostenResponse> {
    setzeIdAufNullWennNegativ(neueSonstigeProjektkostenListe);

    const url = `${environment.api}/projektabrechnung/viadeeAuslage`;
    const sonstigeProjektkostenSpeichernRequest = this.erstelleSonstigeProjektkostenSpeichernRequestObjekt(
      geloeschteSonstigeProjektkostenListe,
      aktualisierteSonstigeProjektkostenListe,
      neueSonstigeProjektkostenListe
    );

    return this.http.post<SonstigeProjektkostenResponse>(url, sonstigeProjektkostenSpeichernRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungMitarbeiterPairs(abWann: Date, bisWann: Date): Observable<ProjektabrechnungMitarbeiterPair[]> {
    const url =
      `${environment.api}/projektabrechnung/projektabrechnungMitarbeiterPairs?` +
      `abWann=${format(abWann, 'yyyy-MM-dd')}&bisWann=${format(bisWann, 'yyyy-MM-dd')}`;
    return this.http.get<ProjektabrechnungMitarbeiterPair[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getAbrechnungsmonateByProjekt(projekt: Projekt): Observable<ProjektAbrechnungsmonat[]> {
    const url = `${environment.api}/projektabrechnung/abrechnungsmonateFuerProjekt?projektId=${projekt.id}`;
    return this.http.get<ProjektAbrechnungsmonat[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungById(id: string): Observable<Projektabrechnung> {
    const url = `${environment.api}/projektabrechnung/${id}`;
    return this.http.get<Projektabrechnung>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungBerechneteLeistungen(id: string): Observable<ProjektabrechnungBerechneteLeistung[]> {
    const url = `${environment.api}/projektabrechnung/${id}/berechneteLeistung`;
    return this.http.get<ProjektabrechnungBerechneteLeistung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungFertigstellungInitialeDaten(
    id: string,
    fertigstellungsgrad: number
  ): Observable<ProjektabrechnungFertigstellungInitialDaten> {
    const url = `${environment.api}/projektabrechnung/${id}/fertigstellung/initial?fertigstellungsgrad=${fertigstellungsgrad}`;
    return this.http.get<ProjektabrechnungFertigstellungInitialDaten>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  berechneNeueFertigstellung(
    id: string,
    monatFertigstellung: number
  ): Observable<ProjektabrechnungBerechneteLeistung[]> {
    const url = `${environment.api}/projektabrechnung/${id}/fertigstellung/berechne?monatFertigstellung=${monatFertigstellung}`;
    return this.http.get<ProjektabrechnungBerechneteLeistung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getMitarbeiterHatMehrAlsBerechneteLeistung(id: string, mitarbeiterId: string): Observable<boolean> {
    const url = `${environment.api}/projektabrechnung/${id}/mitarbeiterHatMehrAlsBerechneteLeistung?mitarbeiterId=${mitarbeiterId}`;
    return this.http.get<boolean>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungProjektzeituProjektabrechnungFuerMitarbeiter(
    id: string,
    mitarbeiterId: string,
    projektId: string,
    jahr: number,
    monat: number
  ): Observable<ProjektabrechnungProjektzeit[]> {
    const url = `${environment.api}/projektabrechnung/${id}/projektzeit?mitarbeiterId=${mitarbeiterId}&projektId=${projektId}&jahr=${jahr}&monat=${monat}`;
    return this.http.get<ProjektabrechnungProjektzeit[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungReiseZuProjektabrechnungFuerMitarbeiter(
    id: string,
    mitarbeiterId: string
  ): Observable<ProjektabrechnungReise> {
    const url = `${environment.api}/projektabrechnung/${id}/reise?mitarbeiterId=${mitarbeiterId}`;
    return this.http.get<ProjektabrechnungReise>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungSonderarbeitZuProjektabrechnungFuerMitarbeiter(
    id: string,
    mitarbeiterId: string
  ): Observable<ProjektabrechnungSonderarbeit> {
    const url = `${environment.api}/projektabrechnung/${id}/sonderarbeit?mitarbeiterId=${mitarbeiterId}`;
    return this.http.get<ProjektabrechnungSonderarbeit>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungSonstigeZuProjektabrechnungFuerMitarbeiter(
    id: string,
    mitarbeiterId: string
  ): Observable<ProjektabrechnungSonstige> {
    const url = `${environment.api}/projektabrechnung/${id}/sonstige?mitarbeiterId=${mitarbeiterId}`;
    return this.http.get<ProjektabrechnungSonstige>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungSonstigeZuProjektabrechnungOhneMitarbeiterbezug(
    id: string
  ): Observable<ProjektabrechnungSonstige> {
    const url = `${environment.api}/projektabrechnung/${id}/sonstige/ohneMitarbeiterbezug`;
    return this.http.get<ProjektabrechnungSonstige>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getKostenLeistungJeMitarbeiter(id: string): Observable<ProjektabrechnungKostenLeistung[]> {
    const url = `${environment.api}/projektabrechnung/${id}/kostenLeistungJeMitarbeiter`;
    return this.http.get<ProjektabrechnungKostenLeistung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getProjektabrechnungKorrekturbuchungByProjektId(projekt: Projekt): Observable<ProjektabrechnungKorrekturbuchung[]> {
    const url = `${environment.api}/projektabrechnung/korrekturbuchung/${projekt.id}`;
    return this.http.get<ProjektabrechnungKorrekturbuchung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((korrekturbuchungen: ProjektabrechnungKorrekturbuchung[]) =>
        this.modifiziereIdsNormalerBuchungen(korrekturbuchungen)
      )
    );
  }

  getProjektabrechnungKorrekturbuchungGegenbuchung(korrekturbuchungId: string, gegenbuchungId: string) {
    const url =
      `${environment.api}/projektabrechnung/korrekturbuchung/gegenbuchung?gegenbuchungId=${gegenbuchungId}&` +
      `korrekturbuchungId=${korrekturbuchungId}`;
    return this.http.get<ProjektabrechnungKorrekturbuchung>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleNeueProjektabrechnungKorrekturbuchung(
    projektabrechnungKorrekturbuchungListe: ProjektabrechnungKorrekturbuchungVorgang
  ): Observable<ProjektabrechnungKorrekturbuchungVorgang> {
    const url = `${environment.api}/projektabrechnung/korrekturbuchung`;
    return this.http.post<ProjektabrechnungKorrekturbuchungVorgang>(url, projektabrechnungKorrekturbuchungListe).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 422) {
          throw new ValdiatorError(error.error?.message);
        }
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  speichereKorrekturbuchungen(korrekturbuchungen: ProjektabrechnungKorrekturbuchung[]): Observable<string[]> {
    const url = `${environment.api}/projektabrechnung/korrekturbuchung/speichern`;
    setzeIdAufNullWennNegativ(korrekturbuchungen);
    return this.http.post<string[]>(url, korrekturbuchungen).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 422) {
          throw new ValdiatorError(error.error?.message);
        }
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  private transformiereDateInSonstigeProjektkostenList(
    sonstigeProjektkostens: SonstigeProjektkosten[]
  ): SonstigeProjektkosten[] {
    return sonstigeProjektkostens.map((sonstigeProjektkosten: SonstigeProjektkosten) => {
      (sonstigeProjektkosten as any).zuletztGeaendertAm = format(
        sonstigeProjektkosten.zuletztGeaendertAm,
        'yyyy-MM-dd'
      );
      return sonstigeProjektkosten;
    });
  }

  private erstelleSonstigeProjektkostenSpeichernRequestObjekt(
    geloeschteSonstigeProjektkostenListe: SonstigeProjektkosten[],
    aktualisierteSonstigeProjektkostenListe: SonstigeProjektkosten[],
    neueSonstigeProjektkostenListe: SonstigeProjektkosten[]
  ) {
    return {
      geloeschteSonstigeProjektkosten: geloeschteSonstigeProjektkostenListe,
      aktualisierteSonstigeProjektkosten: this.transformiereDateInSonstigeProjektkostenList(
        aktualisierteSonstigeProjektkostenListe
      ),
      neueSonstigeProjektkosten: this.transformiereDateInSonstigeProjektkostenList(neueSonstigeProjektkostenListe),
      listeZumAenderungsvergleich: [],
    };
  }

  private modifiziereIdsNormalerBuchungen(korrekturbuchungen: ProjektabrechnungKorrekturbuchung[]) {
    if (!korrekturbuchungen) {
      return [];
    }

    return korrekturbuchungen.map((korrekturbuchung) => {
      if (!korrekturbuchung.istKorrekturbuchung) {
        korrekturbuchung.id = `B_${korrekturbuchung.id}`;
      }
      return korrekturbuchung;
    });
  }
}
