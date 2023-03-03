import { Injectable } from '@angular/core';
import { BenachrichtigungService } from './benachrichtigung.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ValdiatorError } from '../model/error/valdiator-error';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  constructor(private benachrichtigungService: BenachrichtigungService) {}

  zeigeTechnischenFehlerAn(error: string): void {
    this.benachrichtigungService.erstelleBenachrichtigung(error);
  }

  zeigeValidatorFehlerAn(error: ValdiatorError): void {
    const errorMessage = error.message ? error.message : 'Ein unbekannter Fehler ist aufgetreten.';
    this.benachrichtigungService.erstelleErrorMessage(errorMessage);
  }

  zeigeRestFehlerAn(error: HttpErrorResponse): void {
    const errorMessage = error.error?.message ? error.error.message : this.createErrorMessage(error.status);
    this.benachrichtigungService.erstelleErrorMessage(errorMessage);
  }

  private createErrorMessage(status: number): string {
    switch (status) {
      case 403:
        return 'Sie sind leider nicht authenifiziert.';
      case 406:
        return 'Die Eingabe ist leider fehlerhaft.';
      case 0:
        return 'Es konnte keine Verbindung mit dem Server aufgebaut werden. Bitte überprüfen Sie, ob eine eventuell notwendige VPN Verbindung existiert und Ihr Rechner mit dem Netzwerk verbunden ist.';
      default:
        return 'Ein Fehler ist aufgetreten.';
    }
  }
}
