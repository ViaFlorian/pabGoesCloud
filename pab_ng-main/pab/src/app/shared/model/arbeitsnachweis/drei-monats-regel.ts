export interface DreiMonatsRegel {
  id: string;
  kundeScribeId: string;
  arbeitsstaette: string;
  mitarbeiterId: string;
  automatischErfasst: boolean;
  gueltigVon: Date;
  gueltigBis: Date;
}
