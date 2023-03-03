import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  constructor() {}

  public setLocalstorageProperty(key: string, value: unknown): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  public getLocalstorageProperty(key: string): string {
    return JSON.parse(localStorage.getItem(key) as string);
  }

  public clearLocalStorageProperty(key: string): void {
    localStorage.removeItem(key);
  }
}
