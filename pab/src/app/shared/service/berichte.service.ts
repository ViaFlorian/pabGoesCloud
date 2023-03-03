import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ErrorService } from './error.service';
import { MitarbeiterQuittungEmailDaten } from '../model/arbeitsnachweis/mitarbeiter-quittung-email-daten';
import { Arbeitsnachweis } from '../model/arbeitsnachweis/arbeitsnachweis';
import { catchError, map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ErgebnisB002Uebersicht } from '../model/berichte/ergebnis-b002-uebersicht';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { DatePipe } from '@angular/common';
import { BearbeitungsstatusEnumZuStatusIdPipe } from '../pipe/bearbeitungsstatus-enum-zu-status-id.pipe';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';
import { ErgebnisB004Uebersicht } from '../model/berichte/ergebnis-b004-uebersicht';
import { ErgebnisB008Uebersicht } from '../model/berichte/ergebnis-b008-uebersicht';
import { Projekt } from '../model/projekt/projekt';
import { Kunde } from '../model/kunde/kunde';
import { Organisationseinheit } from '../model/organisationseinheit/organisationseinheit';
import { format } from 'date-fns';
import { ErgebnisB007Uebersicht } from '../model/berichte/ergebnis-b007-uebersicht';
import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { AktivInaktivEnum } from '../enum/aktiv-inaktiv.enum';
import { BuchungstypBuchungOderKorrekturEnum } from '../enum/buchungstyp-buchung-korrektur.enum';
import { MitarbeitergruppeEnum } from '../enum/mitarbeitergruppe.enum';
import { ProjekttypEnum } from '../enum/projekttyp.enum';
import { BenutzerService } from './benutzer.service';

@Injectable({
  providedIn: 'root',
})
export class BerichteService {
  constructor(
    private http: HttpClient,
    private errorService: ErrorService,
    private datePipe: DatePipe,
    private bearbeitungsstatusZuStatusIdPipe: BearbeitungsstatusEnumZuStatusIdPipe,
    private benutzerService: BenutzerService
  ) {}

  getB002Uebersicht(
    abrechnungsmonat: MitarbeiterAbrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter
  ): Observable<ErgebnisB002Uebersicht[]> {
    let url = `${environment.api}/bericht/b002?jahr=${abrechnungsmonat.jahr}&monat=${abrechnungsmonat.monat}`;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;

    return this.http.get<ErgebnisB002Uebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleUndOeffneB002AlsExcel(
    abrechnungsmonat: MitarbeiterAbrechnungsmonat,
    mitarbeiter: Mitarbeiter,
    sachbearbeiter: Mitarbeiter
  ): Observable<Blob> {
    let url = `${environment.api}/bericht/b002/excel?jahr=${abrechnungsmonat.jahr}&monat=${abrechnungsmonat.monat}`;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;
    const options = { responseType: 'blob' as 'json' };

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'application/octet-stream' });
      })
    );
  }

  getB004Uebersicht(
    abrechnungsmonat: Abrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter,
    bearbeitungsstatus?: BearbeitungsstatusEnum
  ): Observable<ErgebnisB004Uebersicht[]> {
    let url = `${environment.api}/bericht/b004?jahr=${abrechnungsmonat.jahr}&monat=${abrechnungsmonat.monat}`;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;

    if (bearbeitungsstatus && bearbeitungsstatus !== BearbeitungsstatusEnum.ALLE) {
      url = url.concat(`&statusId=${this.bearbeitungsstatusZuStatusIdPipe.transform(bearbeitungsstatus)}`);
    }

    return this.http.get<ErgebnisB004Uebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleUndOeffneB004AlsPdf(
    abrechnungsmonat: Abrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter,
    bearbeitungsstatus?: BearbeitungsstatusEnum
  ): Observable<Blob> {
    let url = `${environment.api}/bericht/b004/pdf?jahr=${abrechnungsmonat.jahr}&monat=${abrechnungsmonat.monat}`;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;

    if (bearbeitungsstatus && bearbeitungsstatus !== BearbeitungsstatusEnum.ALLE) {
      url = url.concat(`&statusId=${this.bearbeitungsstatusZuStatusIdPipe.transform(bearbeitungsstatus)}`);
    }
    const options = { responseType: 'blob' as 'json' };

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'application/pdf' });
      })
    );
  }

  getB004FuerArbeitsnachweisPdfAnhang(arbeitsnachweis: Arbeitsnachweis): Observable<Blob> {
    const url = `${environment.api}/bericht/b004/pdf/arbeitsnachweis?arbeitsnachweisId=${arbeitsnachweis.id}`;
    const options = { responseType: 'blob' as 'json' };

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'application/pdf' });
      })
    );
  }

  sendeB004Email(
    emailDaten: MitarbeiterQuittungEmailDaten,
    abrechnungsmonat: Abrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter,
    bearbeitungsstatus?: BearbeitungsstatusEnum
  ): Observable<HttpResponse<any>> {
    let url = `${environment.api}/bericht/b004/email?jahr=${abrechnungsmonat.jahr}&monat=${abrechnungsmonat.monat}`;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;

    if (bearbeitungsstatus && bearbeitungsstatus !== BearbeitungsstatusEnum.ALLE) {
      url = url.concat(`&statusId=${this.bearbeitungsstatusZuStatusIdPipe.transform(bearbeitungsstatus)}`);
    }

    return this.http.post<HttpResponse<any>>(url, emailDaten).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  urlBuilderFuerB007(
    url: string,
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    kostenart?: Array<String>
  ): string {
    url = `abWann=${format(abrechnungsmonatAbWann, 'yyyy-MM-dd')}&bisWann=${format(
      abrechnungsmonatBisWann,
      'yyyy-MM-dd'
    )}`;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;
    url =
      aktivInaktiv === AktivInaktivEnum.ALLE
        ? url
        : aktivInaktiv === AktivInaktivEnum.AKTIV
        ? url.concat(`&istAktiv=true`)
        : url.concat(`&istAktiv=false`);
    url =
      bearbeitungsstatus === BearbeitungsstatusEnum.ALLE
        ? url
        : url.concat(`&statusId=${this.bearbeitungsstatusZuStatusIdPipe.transform(bearbeitungsstatus)}`);
    url = buchungstyp === BuchungstypBuchungOderKorrekturEnum.ALLE ? url : url.concat(`&buchungstyp=${buchungstyp}`);
    url = projekt && projekt.id ? url.concat(`&projektId=${projekt.id}`) : url;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = projekttyp === ProjekttypEnum.ALLE ? url : url.concat(`&projekttyp=${projekttyp}`);
    url = mitarbeitergruppe === MitarbeitergruppeEnum.ALLE ? url : url.concat(`&mitarbeiterTyp=${mitarbeitergruppe}`);
    kostenart?.forEach((kostenartVal) => {
      url = url.concat(`&kostenart=${kostenartVal}`);
    });
    url = url.concat(`&abfrageDurchOELeiter=${this.benutzerService.hatRolleOeLeiter()}`);
    return url;
  }

  getB007Uebersicht(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    kostenart?: Array<String>
  ): Observable<ErgebnisB007Uebersicht[]> {
    let url = `${environment.api}/bericht/b007?`;
    url =
      url +
      this.urlBuilderFuerB007(
        url,
        abrechnungsmonatAbWann,
        abrechnungsmonatBisWann,
        sachbearbeiter,
        aktivInaktiv,
        bearbeitungsstatus,
        buchungstyp,
        projekt,
        mitarbeiter,
        projekttyp,
        mitarbeitergruppe,
        kostenart
      );
    url = kunde && kunde.scribeId ? url.concat(`&kundeScribeId=${encodeURIComponent(kunde.scribeId)}`) : url;
    url =
      organisationseinheit && organisationseinheit.scribeId
        ? url.concat(`&organisationseinheitScribeId=${encodeURIComponent(organisationseinheit.scribeId)}`)
        : url;
    return this.http.get<ErgebnisB007Uebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleUndOeffneB007AlsExcel(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    istKostenartdetails: boolean,
    kostenart?: Array<String>
  ): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    let url = `${environment.api}/bericht/b007/excel?`;
    url =
      url +
      this.urlBuilderFuerB007(
        url,
        abrechnungsmonatAbWann,
        abrechnungsmonatBisWann,
        sachbearbeiter,
        aktivInaktiv,
        bearbeitungsstatus,
        buchungstyp,
        projekt,
        mitarbeiter,
        projekttyp,
        mitarbeitergruppe,
        kostenart
      );
    url = kunde && kunde.id ? url.concat(`&kundeId=${kunde.id}`) : url;
    url =
      organisationseinheit && organisationseinheit.id
        ? url.concat(`&organisationseinheitId=${organisationseinheit.id}`)
        : url;
    url = url.concat(`&istKostenartdetails=${istKostenartdetails}`);

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'application/octet-stream' });
      })
    );
  }

  erstelleUndOeffneB007AlsPdf(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    istKostenartdetails: boolean,
    kostenart?: Array<String>
  ): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    let url = `${environment.api}/bericht/b007/pdf?`;
    url =
      url +
      this.urlBuilderFuerB007(
        url,
        abrechnungsmonatAbWann,
        abrechnungsmonatBisWann,
        sachbearbeiter,
        aktivInaktiv,
        bearbeitungsstatus,
        buchungstyp,
        projekt,
        mitarbeiter,
        projekttyp,
        mitarbeitergruppe,
        kostenart
      );
    url = kunde && kunde.id ? url.concat(`&kundeId=${kunde.id}`) : url;
    url =
      organisationseinheit && organisationseinheit.id
        ? url.concat(`&organisationseinheitId=${organisationseinheit.id}`)
        : url;
    url = url.concat(`&istKostenartdetails=${istKostenartdetails}`);

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'pdf' });
      })
    );
  }

  sendeB007Email(
    emailDaten: MitarbeiterQuittungEmailDaten,
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    istKostenartdetails: boolean,
    kostenart?: Array<String>
  ): Observable<HttpResponse<any>> {
    let url =
      `${environment.api}/bericht/b007/email?` +
      `abWann=${format(abrechnungsmonatAbWann, 'yyyy-MM-dd')}&bisWann=${format(abrechnungsmonatBisWann, 'yyyy-MM-dd')}`;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;
    url =
      aktivInaktiv === AktivInaktivEnum.ALLE
        ? url
        : aktivInaktiv === AktivInaktivEnum.AKTIV
        ? url.concat(`&istAktiv=true`)
        : url.concat(`&istAktiv=false`);
    url =
      bearbeitungsstatus === BearbeitungsstatusEnum.ALLE
        ? url
        : url.concat(`&statusId=${this.bearbeitungsstatusZuStatusIdPipe.transform(bearbeitungsstatus)}`);
    url = buchungstyp === BuchungstypBuchungOderKorrekturEnum.ALLE ? url : url.concat(`&buchungstyp=${buchungstyp}`);
    url = projekt && projekt.id ? url.concat(`&projektId=${projekt.id}`) : url;
    url = kunde && kunde.id ? url.concat(`&kundeId=${kunde.id}`) : url;
    url =
      organisationseinheit && organisationseinheit.id
        ? url.concat(`&organisationseinheitId=${organisationseinheit.id}`)
        : url;
    url = mitarbeiter && mitarbeiter.id ? url.concat(`&mitarbeiterId=${mitarbeiter.id}`) : url;
    url = projekttyp === ProjekttypEnum.ALLE ? url : url.concat(`&projekttyp=${projekttyp}`);
    url = mitarbeitergruppe === MitarbeitergruppeEnum.ALLE ? url : url.concat(`&mitarbeiterTyp=${mitarbeitergruppe}`);
    kostenart?.forEach((kostenartVal) => {
      url = url.concat(`&kostenart=${kostenartVal}`);
    });
    url = url.concat(`&abfrageDurchOELeiter=${this.benutzerService.hatRolleOeLeiter()}`);
    url = url.concat(`&istKostenartdetails=${istKostenartdetails}`);

    return this.http.post<HttpResponse<any>>(url, emailDaten).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getB008Uebersicht(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit
  ): Observable<ErgebnisB008Uebersicht[]> {
    let url =
      `${environment.api}/bericht/b008?` +
      `abWann=${format(abrechnungsmonatAbWann, 'yyyy-MM-dd')}&bisWann=${format(abrechnungsmonatBisWann, 'yyyy-MM-dd')}`;
    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;
    url = projekt && projekt.id ? url.concat(`&projektId=${projekt.id}`) : url;
    url = kunde && kunde.scribeId ? url.concat(`&kundeScribeId=${encodeURIComponent(kunde.scribeId)}`) : url;
    url =
      organisationseinheit && organisationseinheit.scribeId
        ? url.concat(`&organisationseinheitScribeId=${encodeURIComponent(organisationseinheit.scribeId)}`)
        : url;

    return this.http.get<ErgebnisB008Uebersicht[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleUndOeffneB008AlsExcel(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    istDetailsAktiv: boolean,
    istAusgabeInPT: boolean
  ): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    let url =
      `${environment.api}/bericht/b008/excel?` +
      `abWann=${format(abrechnungsmonatAbWann, 'yyyy-MM-dd')}&bisWann=${format(abrechnungsmonatBisWann, 'yyyy-MM-dd')}`;

    url = sachbearbeiter && sachbearbeiter.id ? url.concat(`&sachbearbeiterId=${sachbearbeiter.id}`) : url;
    url = projekt && projekt.id ? url.concat(`&projektId=${projekt.id}`) : url;
    url = kunde && kunde.id ? url.concat(`&kundeId=${kunde.id}`) : url;
    url =
      organisationseinheit && organisationseinheit.id
        ? url.concat(`&organisationseinheitId=${organisationseinheit.id}`)
        : url;
    url = url.concat(`&istDetailsAktiv=${istDetailsAktiv}&istAusgabeInPT=${istAusgabeInPT}`);

    return this.http.get<Blob>(url, options).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      }),
      map((res) => {
        return new Blob([res], { type: 'application/octet-stream' });
      })
    );
  }

  sendeEmailFuerArbeitsnachweisAbrechnung(
    emailDaten: MitarbeiterQuittungEmailDaten,
    arbeitsnachweis: Arbeitsnachweis
  ): Observable<HttpResponse<any>> {
    const url = `${environment.api}/bericht/emailarbeitsnachweisabrechnung?arbeitsnachweisId=${arbeitsnachweis.id}`;

    return this.http.post<HttpResponse<any>>(url, emailDaten).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  getEmailDatenFuerArbeitsnachweisAbrechnung(
    arbeitsnachweis: Arbeitsnachweis
  ): Observable<MitarbeiterQuittungEmailDaten> {
    const url = `${environment.api}/bericht/quittungemail?arbeitsnachweisId=${arbeitsnachweis.id}`;

    return this.http.get<MitarbeiterQuittungEmailDaten>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorService.zeigeRestFehlerAn(error);
        throw error;
      })
    );
  }

  erstelleFileDateExtension(): string {
    return this.datePipe.transform(new Date(), 'yyyyMMdd_HHmm')!;
  }

  downloadFile(blob: Blob, fileName: string) {
    const a = document.createElement('a');
    document.body.appendChild(a);
    const url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = fileName;
    a.click();
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    }, 0);
  }
}
