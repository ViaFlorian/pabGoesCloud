import { loginAdmin, loginRolle } from './auth';

declare global {
  namespace Cypress {
    interface Chainable<Subject> {
      loginAdmin(): void;

      loginRolle(): void;
    }
  }
}

let cachedTokenExpiryTimeAdmin = new Date().getTime();
let cachedTokenResponseAdmin: any = null;

Cypress.Commands.add('loginAdmin', () => {
  // Clear our cache if tokens are expired
  if (cachedTokenExpiryTimeAdmin <= new Date().getTime()) {
    cachedTokenResponseAdmin = null;
  }

  return loginAdmin(cachedTokenResponseAdmin).then((tokenResponse) => {
    cachedTokenResponseAdmin = tokenResponse;
    // Set expiry time to 50 minutes from now
    cachedTokenExpiryTimeAdmin = new Date().getTime() + 50 * 60 * 1000;
  });
});

let cachedTokenExpiryTimeRolle = new Date().getTime();
let cachedTokenResponseRolle: any = null;

Cypress.Commands.add('loginRolle', () => {
  // Clear our cache if tokens are expired
  if (cachedTokenExpiryTimeRolle <= new Date().getTime()) {
    cachedTokenResponseRolle = null;
  }

  return loginRolle(cachedTokenResponseRolle).then((tokenResponse) => {
    cachedTokenResponseRolle = tokenResponse;
    // Set expiry time to 50 minutes from now
    cachedTokenExpiryTimeRolle = new Date().getTime() + 50 * 60 * 1000;
  });
});
