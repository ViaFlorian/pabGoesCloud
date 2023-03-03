import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { catchError, map, Observable, tap } from 'rxjs';
import { ArbeitsnachweisUebersicht } from '../model/arbeitsnachweis/arbeitsnachweis-uebersicht';
import { ErrorService } from './error.service';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';
import { Arbeitsnachweis } from '../model/arbeitsnachweis/arbeitsnachweis';
import { Abwesenheit } from '../model/arbeitsnachweis/abwesenheit';
import { Beleg } from '../model/arbeitsnachweis/beleg';
import { Projektstunde } from '../model/arbeitsnachweis/projektstunde';
import { AbwesenheitZeitBackend } from '../model/backend-kommunikation/abwesenheit-zeit-backend';
import { ArbeitsnachweisAbrechnung } from '../model/arbeitsnachweis/arbeitsnachweis-abrechnung';
import { LohnartberechnungLog } from '../model/arbeitsnachweis/lohnartberechnung-log';
import { DreiMonatsRegel } from '../model/arbeitsnachweis/drei-monats-regel';
import { KundeService } from './kunde.service';
import { LohnartZuordnung } from '../model/arbeitsnachweis/lohnart-zuordnung';
import {
  erstelleDatumBisAusProjektstundeBackendZeit,
  erstelleDatumVonAusProjektstundeBackendZeit,
  erstelleTagBisAusProjektstundeNachTyp,
  erstelleUhrzeitBisAusProjektstunde,
  erstelleUhrzeitVonAusProjektstunde,
} from '../../routes/arbeitsnachweise/util/projektstunde-zeit.util';
import { ProjektstundeZeitBackend } from '../model/backend-kommunikation/projektstunde-zeit-backend';
import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { BenachrichtigungService } from './benachrichtigung.service';
import { ArbeitsnachweisSpeichernResponse } from '../model/backend-kommunikation/arbeitsnachweis-speichern-response';
import { StatusListen } from '../model/sonstiges/status-listen';
import { format } from 'date-fns';
import { KonstantenService } from './konstanten.service';
import { BelegZeitBackend } from '../model/backend-kommunikation/beleg-zeit-backend';
import { ExcelImportErgebnis } from '../model/arbeitsnachweis/excel-import-ergebnis';
import { ArbeitsnachweisSpeichernRequest } from '../model/backend-kommunikation/arbeitsnachweis-speichern-request';
import { ProjektstundeTyp } from '../model/konstanten/projektstunde-typ';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { Fehlerlog } from '../model/arbeitsnachweis/fehlerlog';
import { DmsVerarbeitung } from '../model/arbeitsnachweis/dms-verarbeitung';
import { ArbeitsnachweisAbrechnungRequest } from '../model/backend-kommunikation/arbeitsnachweis-abrechnung-request';
import { setzeIdAufNullWennNegativ } from '../util/id-nuller.util';

@Injectable({
  providedIn: 'root',
})
export class ArbeitsnachweisService {
  constructor(
    private http: HttpClient,
    private errorService: ErrorService,
    private benachrichtigungsService: BenachrichtigungService,
    private kundenService: KundeService,
    private konstantenService: KonstantenService
  ) {}

  getArbeitsnachweiseUebersicht(abWann: Date, bisWann: Date): Observable<ArbeitsnachweisUebersicht[]> {
    const url =
      `${environment.api}/arbeitsnachweis/uebersicht?` +
      `abWann=${format(abWann, 'yyyy-MM-dd')}&bisWann=${format(bisWann, 'yyyy-MM-dd')}`;
    return this.http.get<ArbeitsnachweisUebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getFehlendeArbeitsnachweise(abWann: Date, bisWann: Date): Observable<ArbeitsnachweisUebersicht[]> {
    const url =
      `${environment.api}/arbeitsnachweis/fehlendeArbeitsnachweise?` +
      `abWann=${format(abWann, 'yyyy-MM-dd')}&bisWann=${format(bisWann, 'yyyy-MM-dd')}`;
    return this.http.get<ArbeitsnachweisUebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getAbrechnungsmonateByMitarbeiter(mitarbeiter: Mitarbeiter): Observable<MitarbeiterAbrechnungsmonat[]> {
    return this.getAbrechnungsmonateByMitarbeiterId(mitarbeiter.id);
  }

  getAbrechnungsmonateByMitarbeiterId(mitarbeiterId: string): Observable<MitarbeiterAbrechnungsmonat[]> {
    const url = `${environment.api}/arbeitsnachweis/abrechnungsmonateFuerMitarbeiter?mitarbeiterId=${mitarbeiterId}`;
    return this.http.get<MitarbeiterAbrechnungsmonat[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  speichereArbeitsnachweis(
    arbeitsnachweis: Arbeitsnachweis,
    mitarbeiterId: string,
    projektstunden: StatusListen,
    belege: StatusListen,
    abwesenheiten: StatusListen
  ): Observable<ArbeitsnachweisSpeichernResponse> {
    const requestBody = this.erstelleArbeitnachweisSpeichernRequestObjekt(
      arbeitsnachweis,
      mitarbeiterId,
      projektstunden,
      abwesenheiten,
      belege
    );

    const url = `${environment.api}/arbeitsnachweis/`;
    return this.http.post<ArbeitsnachweisSpeichernResponse>(url, requestBody).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  deleteArbeitsnachweis(arbeitsnachweisId: string): Observable<number[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweisId}`;
    return this.http.delete<number[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getAlleArbeitsnachweise(): Observable<Arbeitsnachweis[]> {
    const url = `${environment.api}/arbeitsnachweis/`;
    return this.http.get<Arbeitsnachweis[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getArbeitsnachweisById(id: string): Observable<Arbeitsnachweis> {
    const url = `${environment.api}/arbeitsnachweis/${id}`;
    return this.http.get<Arbeitsnachweis>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getAbwesenheitenByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<Abwesenheit[]> {
    return this.getAbwesenheitenByArbeitsnachweisId(arbeitsnachweis.id);
  }

  getAbwesenheitenByArbeitsnachweisId(arbeitsnachweisId: string): Observable<Abwesenheit[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweisId}/abwesenheiten`;
    return this.http.get<Abwesenheit[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((abwesenheitsListe: Abwesenheit[]) => this.transformiereZuAbwesenheitListe(abwesenheitsListe))
    );
  }

  getBelegByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<Beleg[]> {
    return this.getBelegByArbeitsnachweisId(arbeitsnachweis.id);
  }

  getBelegByArbeitsnachweisId(arbeitsnachweisId: string): Observable<Beleg[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweisId}/belege`;
    return this.http.get<Beleg[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((belegListe: Beleg[]) => this.transformiereZuBelegListe(belegListe))
    );
  }

  getProjektstundenByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<Projektstunde[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/projektstunden`;
    return this.http.get<Projektstunde[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),

      map((projektstundenListe: Projektstunde[]) => this.transformiereZuProjektstundeListe(projektstundenListe))
    );
  }

  getDmsUrlByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<string> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/dmsurl`;
    return this.http.get<string>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  pruefeUndWarneBeiSonderzeitenInkosistenz(projektstunden: Projektstunde[]) {
    const url = `${environment.api}/arbeitsnachweis/sonderzeitinkonsistenz`;
    const projektstundenBackendFormat = this.transformiereVonProjektstundeListe(projektstunden);

    this.http
      .post<string>(url, projektstundenBackendFormat)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          this.errorService.zeigeRestFehlerAn(error);
          throw error;
        })
      )
      .subscribe((warnung) => {
        if (warnung) {
          this.benachrichtigungsService.erstelleWarnung(warnung);
        }
      });
  }

  pruefeUndWarneBeiProjektstundenInkosistenz(
    mitarbeiterId: string,
    abrechnungsmonat: MitarbeiterAbrechnungsmonat,
    projektstunden: Projektstunde[]
  ) {
    const url = `${environment.api}/arbeitsnachweis/projektstundenInkonsistenzen`;
    const requestObjekt = {
      mitarbeiterId: mitarbeiterId,
      jahr: abrechnungsmonat.jahr,
      monat: abrechnungsmonat.monat,
      projektstunden: this.transformiereVonProjektstundeListe(projektstunden),
    };

    this.http
      .post<string>(url, requestObjekt)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          this.errorService.zeigeRestFehlerAn(error);
          throw error;
        })
      )
      .subscribe((warnung) => {
        if (warnung) {
          this.benachrichtigungsService.erstelleWarnung(warnung);
        }
      });
  }

  getLohnartZuordnungenByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<LohnartZuordnung[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/lohnartzuordnungen`;
    return this.http.get<LohnartZuordnung[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getLohnartberechnungLogsByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<LohnartberechnungLog[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/lohnartberechnunglog`;
    return this.http.get<LohnartberechnungLog[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getDreiMonatsRegelnByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<DreiMonatsRegel[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/dreimonatsregeln`;
    return this.http.get<DreiMonatsRegel[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getFehlerlogsByArbeitsnachweis(arbeitsnachweis: Arbeitsnachweis): Observable<Fehlerlog[]> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/fehlerlogs`;
    return this.http.get<Fehlerlog[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getAlleAbrechnungsmonate(): Observable<Abrechnungsmonat[]> {
    const url = `${environment.api}/arbeitsnachweis/abrechnungsmonate`;
    return this.http.get<Abrechnungsmonat[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleArbeitsnachweisAbrechnung(
    aktuellerArbeitsnachweis: Arbeitsnachweis | undefined,
    projektstunden: Projektstunde[],
    abwesenheiten: Abwesenheit[],
    belege: Beleg[]
  ) {
    const anwAbrechnungsRequest = this.erstelleAbrechnungRequestObjekt(
      aktuellerArbeitsnachweis,
      this.filtereProjektstundenNachProjektstundeTyp(
        projektstunden,
        this.konstantenService.getProjektstundenTypNormal()
      ),
      this.filtereProjektstundenNachProjektstundeTyp(
        projektstunden,
        this.konstantenService.getProjektstundenTypRufbereitschaft()
      ),
      this.filtereProjektstundenNachProjektstundeTyp(
        projektstunden,
        this.konstantenService.getProjektstundenTypSonderarbeitszeit()
      ),
      this.filtereProjektstundenNachProjektstundeTyp(
        projektstunden,
        this.konstantenService.getProjektstundenTypTatsaechlicheReise()
      ),
      abwesenheiten,
      belege
    );

    const url = `${environment.api}/arbeitsnachweis/abrechnung`;

    return this.http.post<ArbeitsnachweisAbrechnung>(url, anwAbrechnungsRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      tap((arbeitsnachweisAbrechnung) => {
        if (arbeitsnachweisAbrechnung && arbeitsnachweisAbrechnung.warnung) {
          this.benachrichtigungsService.erstelleWarnung(arbeitsnachweisAbrechnung.warnung);
        }
      })
    );
  }

  rechneArbeitsnachweisAb(arbeitsnachweis: Arbeitsnachweis): Observable<HttpResponse<any>> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/abrechnen`;

    return this.http.patch<HttpResponse<any>>(url, null).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  berechneAngerechneteReisezeitFuerAlleProjektstunden(
    tatsaechlicheReisezeiten: Projektstunde[]
  ): Observable<Projektstunde[]> {
    const url = `${environment.api}/arbeitsnachweis/reisezeitberechnen`;
    const tatsaechlicheReisezeit = this.transformiereVonProjektstundeListe(tatsaechlicheReisezeiten);

    return this.http.post<Projektstunde[]>(url, tatsaechlicheReisezeit).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  verarbeiteArbeitsnachweisImport(ausgewaehlterANW: File, mitarbeiter: Mitarbeiter): Observable<ExcelImportErgebnis> {
    const url = `${environment.api}/arbeitsnachweis/anwimport?mitarbeiterId=${mitarbeiter.id}`;

    const formData = new FormData();
    formData.append('anwFile', ausgewaehlterANW);

    return this.http.post<ExcelImportErgebnis>(url, formData).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((excelImportErgebnis) => this.transformiereExcelImportErgebnis(excelImportErgebnis))
    );
  }

  verarbeiteBelegImport(beleg: File, arbeitsnachweis: Arbeitsnachweis): Observable<DmsVerarbeitung> {
    const url = `${environment.api}/arbeitsnachweis/${arbeitsnachweis.id}/belegUpload`;

    const formData = new FormData();
    formData.append('belegFile', beleg);
    formData.append('dateiname', beleg.name);

    return this.http.post<DmsVerarbeitung>(url, formData).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((belegUploadErgebnis) => belegUploadErgebnis)
    );
  }

  validiereArbeitsnachweis(arbeitsnachweisFile: File): Observable<Fehlerlog[]> {
    const url = `${environment.api}/arbeitsnachweis/anwvalidieren`;

    const formData = new FormData();
    formData.append('anwFile', arbeitsnachweisFile);

    return this.http.post<Fehlerlog[]>(url, formData).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  private filtereProjektstundenNachProjektstundeTyp(
    projektstunden: Projektstunde[],
    projektstundeTyp: ProjektstundeTyp
  ) {
    return projektstunden.filter(
      (projektstunde: Projektstunde) => projektstunde.projektstundeTypId === projektstundeTyp.id
    );
  }

  private erstelleAbrechnungRequestObjekt(
    aktuellerArbeitsnachweis: Arbeitsnachweis | undefined,
    projektstunden: Projektstunde[],
    rufbereitschaften: Projektstunde[],
    sonderarbeitszeiten: Projektstunde[],
    tatsaechlicheReisezeiten: Projektstunde[],
    abwesenheiten: Abwesenheit[],
    belege: Beleg[]
  ): ArbeitsnachweisAbrechnungRequest {
    return {
      arbeitsnachweis: aktuellerArbeitsnachweis,
      projektstundenNormal: this.transformiereVonProjektstundeListe(projektstunden),
      reisezeiten: this.transformiereVonProjektstundeListe(tatsaechlicheReisezeiten),
      sonderarbeitszeiten: this.transformiereVonProjektstundeListe(sonderarbeitszeiten),
      rufbereitschaften: this.transformiereVonProjektstundeListe(rufbereitschaften),
      abwesenheiten: this.transformiereVonAbwesenheitListe(abwesenheiten),
      belege: this.transformiereVonBelegeListe(belege),
    } as ArbeitsnachweisAbrechnungRequest;
  }

  private erstelleArbeitnachweisSpeichernRequestObjekt(
    aktuellerArbeitsnachweis: Arbeitsnachweis | undefined,
    mitarbeiterId: string | undefined,
    projektstunden: StatusListen | undefined,
    abwesenheiten: StatusListen | undefined,
    belege: StatusListen | undefined
  ): ArbeitsnachweisSpeichernRequest {
    let neueProjektstunden: Projektstunde[] = projektstunden
      ? (projektstunden.getNeuListe() as Projektstunde[]).slice()
      : [];
    neueProjektstunden = this.transformiereVonProjektstundeListe(neueProjektstunden);
    setzeIdAufNullWennNegativ(neueProjektstunden);

    let aktualisierteProjektstunden: Projektstunde[] = projektstunden
      ? (projektstunden.getAktualisiertListe() as Projektstunde[]).slice()
      : [];
    aktualisierteProjektstunden = this.transformiereVonProjektstundeListe(aktualisierteProjektstunden);
    setzeIdAufNullWennNegativ(aktualisierteProjektstunden);

    let geloeschteProjektstunden: Projektstunde[] = projektstunden
      ? (projektstunden.getGeloeschtListe() as Projektstunde[]).slice()
      : [];
    geloeschteProjektstunden = this.transformiereVonProjektstundeListe(geloeschteProjektstunden);
    setzeIdAufNullWennNegativ(geloeschteProjektstunden);

    let neueAbwesenheiten: Abwesenheit[] = abwesenheiten ? (abwesenheiten.getNeuListe() as Abwesenheit[]).slice() : [];
    neueAbwesenheiten = this.transformiereVonAbwesenheitListe(neueAbwesenheiten);
    setzeIdAufNullWennNegativ(neueAbwesenheiten);

    let aktualisierteAbwesenheiten: Abwesenheit[] = abwesenheiten
      ? (abwesenheiten.getAktualisiertListe() as Abwesenheit[]).slice()
      : [];
    aktualisierteAbwesenheiten = this.transformiereVonAbwesenheitListe(aktualisierteAbwesenheiten);
    setzeIdAufNullWennNegativ(aktualisierteAbwesenheiten);

    let geloschteAbwesenheiten: Abwesenheit[] = abwesenheiten
      ? (abwesenheiten.getGeloeschtListe() as Abwesenheit[]).slice()
      : [];
    geloschteAbwesenheiten = this.transformiereVonAbwesenheitListe(geloschteAbwesenheiten);
    setzeIdAufNullWennNegativ(geloschteAbwesenheiten);

    let neueBelege: Beleg[] = belege ? (belege.getNeuListe() as Beleg[]).slice() : [];
    neueBelege = this.transformiereVonBelegeListe(neueBelege);
    setzeIdAufNullWennNegativ(neueBelege);

    let aktualisierteBelege: Beleg[] = belege ? (belege.getAktualisiertListe() as Beleg[]).slice() : [];
    aktualisierteBelege = this.transformiereVonBelegeListe(aktualisierteBelege);
    setzeIdAufNullWennNegativ(aktualisierteBelege);

    let geloeschteBelege: Beleg[] = belege ? (belege.getGeloeschtListe() as Beleg[]).slice() : [];
    geloeschteBelege = this.transformiereVonBelegeListe(geloeschteBelege);
    setzeIdAufNullWennNegativ(geloeschteBelege);

    return {
      arbeitsnachweis: aktuellerArbeitsnachweis!,
      mitarbeiterId: mitarbeiterId!,
      neueProjektstunden: neueProjektstunden,
      aktualisierteProjektstunden: aktualisierteProjektstunden,
      geloeschteProjektstunden: geloeschteProjektstunden,
      neueAbwesenheiten: neueAbwesenheiten,
      aktualisierteAbwesenheiten: geloschteAbwesenheiten,
      geloschteAbwesenheiten: geloschteAbwesenheiten,
      neueBelege: geloeschteBelege,
      aktualisierteBelege: geloeschteBelege,
      geloeschteBelege: geloeschteBelege,
    } as ArbeitsnachweisSpeichernRequest;
  }

  private transformiereZuAbwesenheitListe(abwesenheitsListe: Abwesenheit[]): Abwesenheit[] {
    if (!abwesenheitsListe) {
      return [];
    }
    return abwesenheitsListe.map((abwesenheit: Abwesenheit) => {
      const abwesenheitBackendZeit = abwesenheit as unknown as AbwesenheitZeitBackend;
      abwesenheit.datumVon = new Date(`${abwesenheitBackendZeit.tagVon}T${abwesenheitBackendZeit.uhrzeitVon}`);
      abwesenheit.datumBis = new Date(`${abwesenheitBackendZeit.tagVon}T${abwesenheitBackendZeit.uhrzeitBis}`);

      return abwesenheit;
    });
  }

  private transformiereVonAbwesenheitListe(abwesenheitsListe: Abwesenheit[]): Abwesenheit[] {
    return abwesenheitsListe.map((abwesenheit: Abwesenheit) => {
      (abwesenheit as unknown as AbwesenheitZeitBackend).tagVon = format(abwesenheit.datumVon, 'yyyy-MM-dd');

      (abwesenheit as unknown as AbwesenheitZeitBackend).uhrzeitVon = abwesenheit.datumVon
        ? this.extrahiereUhrzeitStringAusDate(abwesenheit.datumVon)
        : '';

      (abwesenheit as unknown as AbwesenheitZeitBackend).uhrzeitBis = abwesenheit.datumBis
        ? this.extrahiereUhrzeitStringAusDate(abwesenheit.datumBis)
        : '';

      return abwesenheit;
    });
  }

  private transformiereVonBelegeListe(belegeListe: Beleg[]): Beleg[] {
    const transformierteBelege: Beleg[] = [];
    belegeListe.map((beleg: Beleg) => {
      const transformierterBeleg = { ...beleg };
      (transformierterBeleg as unknown as BelegZeitBackend).datum = format(beleg.datum, 'yyyy-MM-dd');
      transformierteBelege.push(transformierterBeleg);
    });
    return transformierteBelege;
  }

  private transformiereZuBelegListe(belegListe: Beleg[]): Beleg[] {
    if (!belegListe) {
      return [];
    }
    return belegListe.map((beleg: Beleg) => {
      beleg.datum = new Date(beleg.datum);
      return beleg;
    });
  }

  private transformiereZuProjektstundeListe(projektstundenListe: Projektstunde[]): Projektstunde[] {
    const transformierteProjektstunden: Projektstunde[] = [];
    if (!projektstundenListe) {
      return [];
    }
    projektstundenListe.forEach((projektstunde) => {
      const projektstundenTyp = this.konstantenService.getProjektstundeTypById(projektstunde.projektstundeTypId);
      projektstunde.datumVon = erstelleDatumVonAusProjektstundeBackendZeit(projektstunde, projektstundenTyp);
      projektstunde.datumBis = erstelleDatumBisAusProjektstundeBackendZeit(projektstunde, projektstundenTyp);
      transformierteProjektstunden.push(projektstunde);
    });
    return transformierteProjektstunden;
  }

  private transformiereVonProjektstundeListe(projektstundenListe: Projektstunde[]): Projektstunde[] {
    const transformierteProjektstunden = [];
    for (const projektstunde of projektstundenListe) {
      const projektstundenTyp = this.konstantenService.getProjektstundeTypById(projektstunde.projektstundeTypId);
      (projektstunde as unknown as ProjektstundeZeitBackend).tagVon = format(projektstunde.datumVon, 'yyyy-MM-dd');
      (projektstunde as unknown as ProjektstundeZeitBackend).uhrzeitVon = erstelleUhrzeitVonAusProjektstunde(
        projektstunde,
        projektstundenTyp
      );
      (projektstunde as unknown as ProjektstundeZeitBackend).tagBis = erstelleTagBisAusProjektstundeNachTyp(
        projektstunde,
        projektstundenTyp
      );
      (projektstunde as unknown as ProjektstundeZeitBackend).uhrzeitBis = erstelleUhrzeitBisAusProjektstunde(
        projektstunde,
        projektstundenTyp
      );

      transformierteProjektstunden.push(projektstunde);
    }
    return transformierteProjektstunden;
  }

  private extrahiereUhrzeitStringAusDate(time: Date): string {
    const hour = time.getHours() < 10 ? `0${time.getHours()}` : time.getHours().toString();
    const minute = time.getMinutes() < 10 ? `0${time.getMinutes()}` : time.getMinutes().toString();
    return `${hour}:${minute}:00`;
  }

  private transformiereExcelImportErgebnis(excelImportErgebnis: ExcelImportErgebnis): ExcelImportErgebnis {
    excelImportErgebnis.importierteProjektstunden = this.transformiereZuProjektstundeListe(
      excelImportErgebnis.importierteProjektstunden
    );
    excelImportErgebnis.importierteReisezeiten = this.transformiereZuProjektstundeListe(
      excelImportErgebnis.importierteReisezeiten
    );
    excelImportErgebnis.importierteRufbereitschaft = this.transformiereZuProjektstundeListe(
      excelImportErgebnis.importierteRufbereitschaft
    );
    excelImportErgebnis.importierteSonderarbeitszeiten = this.transformiereZuProjektstundeListe(
      excelImportErgebnis.importierteSonderarbeitszeiten
    );
    excelImportErgebnis.importierteBelege = this.transformiereZuBelegListe(excelImportErgebnis.importierteBelege);
    excelImportErgebnis.importierteAbwesenheiten = this.transformiereZuAbwesenheitListe(
      excelImportErgebnis.importierteAbwesenheiten
    );
    return excelImportErgebnis;
  }
}
