import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { BenutzerService } from '../../../shared/service/benutzer.service';
import { NavigationService } from '../../../shared/service/navigation.service';
import { BenachrichtigungService } from '../../../shared/service/benachrichtigung.service';
import { ArbeitsnachweisValidatorService } from '../../../shared/service/arbeitsnachweis-validator.service';
import { SucheComponent } from '../suche/suche.component';

@Component({
  selector: 'pab-title-bar',
  templateUrl: './title-bar.component.html',
  styleUrls: ['./title-bar.component.scss'],
})
export class TitleBarComponent implements OnInit {
  private buildNummer: string = '152c1561113eb886d9afe912303084137abe07e0';
  private datum: string = '31.05.2022';

  @ViewChild(SucheComponent) suche!: SucheComponent;

  benutzerName: string = '';
  showMenuOverlay: boolean = false;
  showSearchOverlay: boolean = false;
  suchbegriff: string;

  @HostListener('keydown.esc', ['$event'])
  onEsc() {
    this.showMenuOverlay = false;
    this.setzeSucheZurueck();
  }

  constructor(
    private benutzerService: BenutzerService,
    private arbeitsnachweisValidierenService: ArbeitsnachweisValidatorService,
    private navigationService: NavigationService,
    private benachrichtigungService: BenachrichtigungService
  ) {
    this.suchbegriff = '';
  }

  ngOnInit(): void {
    this.benutzerService.erzeugeBenutzerLoginListener().subscribe(() => {
      this.benutzerName = this.benutzerService.getBenutzerNamen();
    });
  }

  navigiereZu(url: string): void {
    this.setzeSucheZurueck();
    this.showMenuOverlay = false;
    this.navigationService.navigiereZu(url, true);
  }

  oeffneInfoDialog(): void {
    const rollen: string[] = this.benutzerService.getBenutzerRollen();
    this.benachrichtigungService.erstelleInfoMessage(
      `Build-Nummer ${this.buildNummer} \n\n` + `Datum: ${this.datum} \n\n` + `Aktive Rolle(n): ${rollen}`,
      'Projektabrechnung 2.0'
    );
  }

  oeffneValdierungsDialog(): void {
    this.showMenuOverlay = false;
    this.arbeitsnachweisValidierenService.oeffneValidierungsDialog();
  }

  toggleMenuOverlay() {
    this.setzeSucheZurueck();
    this.showMenuOverlay = !this.showMenuOverlay;
  }

  reagiereAufSuchenEvent($event: any) {
    this.showMenuOverlay = false;
    this.showSearchOverlay = $event?.length;
    this.suchbegriff = $event;
  }

  reagiereAufSuchergebnisNavigationEvent() {
    this.setzeSucheZurueck();
    this.showMenuOverlay = false;
  }

  private setzeSucheZurueck() {
    this.showSearchOverlay = false;
    this.suche.sucheFormGroup.get('suchbegriff')?.setValue('');
  }
}
