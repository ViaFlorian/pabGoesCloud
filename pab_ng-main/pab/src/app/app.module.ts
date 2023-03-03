import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { StrukturModule } from './struktur/struktur.module';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './shared/material/material.module';
import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import de from 'date-fns/locale/de';
import { MsalGuard, MsalInterceptor, MsalModule, MsalRedirectComponent } from '@azure/msal-angular';
import { InteractionType, PublicClientApplication } from '@azure/msal-browser';
import { environment } from '../environments/environment';
import { PipesModule } from './shared/pipes.module';

registerLocaleData(localeDe);

const isIE = window.navigator.userAgent.indexOf('MSIE ') > -1 || window.navigator.userAgent.indexOf('Trident/') > -1;

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    StrukturModule,
    HttpClientModule,
    MaterialModule,
    PipesModule,
    MsalModule.forRoot(
      new PublicClientApplication({
        auth: {
          clientId: environment.clientId,
          authority: environment.authority,
          redirectUri: window.location.origin + '/',
        },
        cache: {
          cacheLocation: 'localStorage',
          storeAuthStateInCookie: isIE,
        },
      }),
      {
        interactionType: InteractionType.Redirect,
        authRequest: {
          scopes: ['user.read'],
        },
      },
      {
        interactionType: InteractionType.Redirect,
        protectedResourceMap: new Map([
          ['https://graph.microsoft.com/v1.0/me', ['user.read']],
          [environment.api, [environment.scope]],
        ]),
      }
    ),
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'de' },
    {
      provide: MAT_DATE_LOCALE,
      useValue: de,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: MsalInterceptor,
      multi: true,
    },
    MsalGuard,
  ],
  bootstrap: [AppComponent, MsalRedirectComponent],
})
export class AppModule {}
