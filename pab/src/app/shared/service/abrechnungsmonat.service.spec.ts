import { AbrechnungsmonatService } from './abrechnungsmonat.service';

describe('abrechnungsmonat.service', function () {
  let abrechnungsmonatService: AbrechnungsmonatService;

  beforeEach(() => {
    const benachrichtigungServiceSpy = jasmine.createSpyObj('BenachrichtigungService', ['erstelleErrorMessage']);
    abrechnungsmonatService = new AbrechnungsmonatService(benachrichtigungServiceSpy);

    jasmine.clock().install();
    const baseTime = new Date(2022, 11, 1);
    jasmine.clock().mockDate(baseTime);
  });

  it('getAktuellenAbrechnungsmonatAsDate', () => {
    const expectedAbrechnungsmonat: Date = new Date(2022, 10, 1);
    const aktuellenAbrechnungsmonat: Date = abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();
    expect(aktuellenAbrechnungsmonat).toEqual(expectedAbrechnungsmonat);
  });
});
