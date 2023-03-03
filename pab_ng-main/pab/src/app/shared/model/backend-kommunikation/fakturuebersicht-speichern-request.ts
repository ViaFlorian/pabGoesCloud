import { Faktur } from '../verwaltung/faktur';

export interface FakturuebersichtSpeichernRequest {
  neueFakturen: Faktur[];
  aktualisierteFakturen: Faktur[];
  geloeschteFakturen: Faktur[];
}
