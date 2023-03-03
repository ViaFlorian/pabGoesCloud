import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeToText',
})
export class TimeToTextPipe implements PipeTransform {
  transform(time: Date | null): string {
    if (!time) {
      return '';
    }
    if (time.getHours() === 23 && time.getMinutes() === 59 && time.getSeconds() === 59) {
      return '24:00';
    }

    const hour = time.getHours() < 10 ? `0${time.getHours()}` : time.getHours().toString();
    const minute = time.getMinutes() < 10 ? `0${time.getMinutes()}` : time.getMinutes().toString();
    return `${hour}:${minute}`;
  }
}
