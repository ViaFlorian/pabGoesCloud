import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'https://login.microsoftonline.com/b58815b9-1d99-470a-a2b1-b3fd7f663db6/v2.0',
  redirectUri: window.location.origin + '/',
  clientId: '636ec64f-aa75-4f97-918e-8cc002feebb4',
  responseType: 'code',
  strictDiscoveryDocumentValidation: false,
  scope: 'openid api://636ec64f-aa75-4f97-918e-8cc002feebb4/profile',
};
