import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ergaenzeProzentZeichen',
})
export class ErgaenzeProzentZeichenPipe implements PipeTransform {
  transform(text: string | null): string | null {
    if (text === null) {
      return null;
    }
    return `${text}%`;
  }
}
