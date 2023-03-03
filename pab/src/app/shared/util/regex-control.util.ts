import { FormGroup } from '@angular/forms';

export function entferneUnerlaubteZeichenAusFormGroupKomponente(
  formGroup: FormGroup,
  komponentenName: string,
  allowedExp: string
) {
  // Breche Methode ab wenn form noch nicht initialisiert oder keine eingabe noch leer
  if (!formGroup || !formGroup.get(komponentenName)?.value) {
    return;
  }
  let valueZuKontrollieren = formGroup.get(komponentenName)?.value;
  const regexp = new RegExp(allowedExp);

  while (!regexp.test(valueZuKontrollieren) && valueZuKontrollieren.length > 0) {
    valueZuKontrollieren = valueZuKontrollieren.slice(0, -1);
  }

  formGroup.get(komponentenName)?.setValue(valueZuKontrollieren);
}
