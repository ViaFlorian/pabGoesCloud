import { IdGeneratorService } from './id-generator.service';

describe('id-generator.service', function () {
  let idGeneratorService: IdGeneratorService;

  beforeEach(() => {
    idGeneratorService = new IdGeneratorService();
  });

  it('Initiale ID ist -1', () => {
    const id = idGeneratorService.generiereId();
    expect(id).toEqual('-1');
  });

  it('IDs werden immer um -1 weitergezählt', () => {
    for (let i = 1; i <= 10; i++) {
      console.log(i);
      const id = idGeneratorService.generiereId();
      expect(id).toEqual(`-${i}`);
    }
  });
});
