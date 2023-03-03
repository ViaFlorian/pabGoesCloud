import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  private stack: string[] = [];

  constructor(private router: Router) {}

  navigiereZu(url: string, resetStack: boolean): void {
    if (resetStack) {
      this.stack = [];
    }
    this.stack.push(url);
    this.router.navigate([url]);
  }

  navigiereZuMitQueryParams(url: string, resetStack: boolean, queryParams: {}): void {
    if (resetStack) {
      this.stack = [];
    }
    this.stack.push(url);
    this.router.navigate([url], { queryParams: queryParams });
  }

  geheZurueck(): void {
    if (this.stack.length === 0) {
      this.router.navigate(['/']);
      return;
    }

    const nextUrl = this.stack.pop();
    const currentUrl = this.router.url.split('?')[0];

    // Die aktuelle Seite liegt immer oben auf dem Stack. Im folgenden finden Prüfungen statt, um nicht zur aktuellen
    // Seite zu navigieren.
    let futureUrl: string | undefined = '';
    if (currentUrl === nextUrl && this.stack.length === 0) {
      this.router.navigate(['/']);
      return;
    } else if (currentUrl === nextUrl && this.stack.length !== 0) {
      futureUrl = this.stack[this.stack.length - 1];
    } else {
      futureUrl = nextUrl;
    }

    this.router.navigate([futureUrl]);
  }

  initialisiereRoute(uri: string) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => this.router.navigate([uri]));
  }
}
