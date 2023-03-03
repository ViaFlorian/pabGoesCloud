import { ObjektMitId } from './objekt-mit-id';
import { StatusListen } from './status-listen';

describe('status-listen', function () {
  const unveraendertTestDaten: ObjektMitId[] = [
    {
      id: 'element-01',
    },
    {
      id: 'element-02',
    },
    {
      id: 'element-03',
    },
  ];

  const neuesElement: ObjektMitId = {
    id: 'element-neu',
  };

  it('Initialisierung', () => {
    const statusListen: StatusListen = new StatusListen(unveraendertTestDaten);

    expect(statusListen.istVeraendert()).toBeFalse();
    expect(statusListen.getUnveraendertListe().length).toEqual(unveraendertTestDaten.length);
    expect(statusListen.getAnzeigeListe().length).toEqual(unveraendertTestDaten.length);
  });

  it('Neues Element hinzufuegen', () => {
    const statusListen: StatusListen = new StatusListen(unveraendertTestDaten);
    statusListen.fuegeNeuesElementHinzu(neuesElement);

    expect(statusListen.istVeraendert()).toBeTrue();
    expect(statusListen.getUnveraendertListe().length).toEqual(unveraendertTestDaten.length);
    expect(statusListen.getAnzeigeListe().length).toEqual(unveraendertTestDaten.length + 1);
  });

  it('Neues Element hinzufuegen und löschen', () => {
    const statusListen: StatusListen = new StatusListen(unveraendertTestDaten);
    statusListen.fuegeNeuesElementHinzu(neuesElement);
    statusListen.loescheElement(neuesElement);

    expect(statusListen.istVeraendert()).toBeFalse();
    expect(statusListen.getUnveraendertListe().length).toEqual(unveraendertTestDaten.length);
    expect(statusListen.getAnzeigeListe().length).toEqual(unveraendertTestDaten.length);
  });
});
