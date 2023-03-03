import { Component } from '@angular/core';
import { ChangelogEintrag } from '../../../../shared/model/sonstiges/changelog-eintrag';

@Component({
  selector: 'pab-dashboard-changelog',
  templateUrl: './dashboard-changelog.component.html',
  styleUrls: ['./dashboard-changelog.component.scss'],
})
export class DashboardChangelogComponent {
  changelogEintraege: ChangelogEintrag[] = [
    {
      datum: '01.04.2022',
      version: 'Release 4.0.0',
      eintraege: ['Austausch von vaadin durch Angular'],
    },
    {
      datum: '20.08.2020',
      version: 'Release 3.9.0',
      eintraege: ['neuer Bericht B016 für diszipl. Vorgesetzte', 'Fehlerbehebungen'],
    },
    {
      datum: '07.08.2020',
      version: 'Release 3.8.0',
      eintraege: ['neuer Bericht B015 für OE-Leiter', 'Fehlerbehebungen'],
    },
  ];
}
