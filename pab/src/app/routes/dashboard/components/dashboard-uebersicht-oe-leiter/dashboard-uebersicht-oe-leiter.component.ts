import { Component, Input } from '@angular/core';
import { LegendPosition } from '@swimlane/ngx-charts';
import { Constants } from '../../../../shared/util/constants.util';

@Component({
  selector: 'pab-dashboard-uebersicht-oe-leiter',
  templateUrl: './dashboard-uebersicht-oe-leiter.component.html',
  styleUrls: ['./dashboard-uebersicht-oe-leiter.component.scss'],
})
export class DashboardUebersichtOeLeiterComponent {
  private namePlan = 'Plan';
  private nameIst = 'Ist';

  @Input()
  aktuellesAbrechnungsjahr!: number;

  // Optionen
  // Hinweis: Breite muss mit der CSS-Klasse .charts-line-chart-container übereinstimmen
  ansicht: [number, number] = [1300, 375];
  zeigeLegaendeAn: boolean = true;
  legendePosition: LegendPosition = LegendPosition.Right;
  legendeTitel: string = '';
  zeigeXAxis: boolean = true;
  zeigeYAxis: boolean = true;
  zeigeYAxisLabel: boolean = true;
  zeigeXAxisLabel: boolean = true;
  farben = [
    { name: this.namePlan, value: Constants.FARBE_GRUEN },
    { name: this.nameIst, value: Constants.FARBE_ROT },
  ];

  daten = [
    {
      name: this.namePlan,
      series: [
        {
          name: 'Januar',
          value: 2387152.83,
        },
        {
          name: 'Februar',
          value: 2309751.89,
        },
        {
          name: 'März',
          value: 2610228.48,
        },
        {
          name: 'April',
          value: 2231561.45,
        },
        {
          name: 'Mai',
          value: 2334565.24,
        },
        {
          name: 'Juni',
          value: 2326165.29,
        },
        {
          name: 'Juli',
          value: 2400040.37,
        },
        {
          name: 'August',
          value: 2718304.53,
        },
        {
          name: 'September',
          value: 2438146.84,
        },
        {
          name: 'Oktober',
          value: 2384936.77,
        },
        {
          name: 'November',
          value: 2788281.39,
        },
        {
          name: 'Dezember',
          value: 2118451.33,
        },
      ],
    },
    {
      name: this.nameIst,
      series: [
        {
          name: 'Januar',
          value: 1400000,
        },
        {
          name: 'Februar',
          value: 1500000,
        },
        {
          name: 'März',
          value: 1700000,
        },
        {
          name: 'April',
          value: 1750000,
        },
        {
          name: 'Mai',
          value: 1800000,
        },
        {
          name: 'Juni',
          value: 2000000,
        },
        {
          name: 'Juli',
          value: 2200000,
        },
        {
          name: 'August',
          value: 2300000,
        },
        {
          name: 'September',
          value: 2400000,
        },
        {
          name: 'Oktober',
          value: 2400000,
        },
        {
          name: 'November',
          value: 2500000,
        },
        {
          name: 'Dezember',
          value: 2900000,
        },
      ],
    },
  ];
}
