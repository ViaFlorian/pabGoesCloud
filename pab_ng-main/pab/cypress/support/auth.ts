import { decode, JwtPayload } from 'jsonwebtoken';
import { environment } from '../../src/environments/environment';
import { credentials } from './credentials';

const envUrl = 'login.windows.net';

const buildAccountEntity = (
  homeAccountId: string,
  realm: string,
  localAccountId: string,
  username: string,
  name: string,
  idToken: JwtPayload
) => {
  return {
    authorityType: 'MSSTS',
    // This could be filled in but it involves a bit of custom base64 encoding
    // and would make this sample more complicated.
    // This value does not seem to get used, so we can leave it out.
    clientInfo: '',
    homeAccountId,
    environment: envUrl,
    realm,
    localAccountId,
    username,
    name,
    idTokenClaims: idToken,
  };
};

const buildIdTokenEntity = (homeAccountId: string, idToken: string, realm: string) => {
  return {
    credentialType: 'IdToken',
    homeAccountId,
    environment: envUrl,
    clientId: environment.clientId,
    secret: idToken,
    realm,
  };
};

const buildAccessTokenEntity = (
  homeAccountId: string,
  accessToken: string,
  expiresIn: number,
  extExpiresIn: number,
  realm: string,
  scope: string
) => {
  const now = Math.floor(Date.now() / 1000);
  return {
    homeAccountId,
    credentialType: 'AccessToken',
    secret: accessToken,
    cachedAt: now.toString(),
    expiresOn: (now + expiresIn).toString(),
    extendedExpiresOn: (now + extExpiresIn).toString(),
    environment: envUrl,
    clientId: environment.clientId,
    realm,
    // Scopes _must_ be lowercase or the token won't be found
    target: scope.toLowerCase(),
    tokenType: 'Bearer',
  };
};

const buildRefreshTokenEntity = (homeAccountId: string, refreshToken: string) => {
  return {
    clientId: environment.clientId,
    credentialType: 'RefreshToken',
    environment: envUrl,
    homeAccountId: homeAccountId,
    secret: refreshToken,
  };
};

const injectTokens = (tokenResponse: any) => {
  const idToken: JwtPayload = decode(tokenResponse.id_token) as JwtPayload;
  const localAccountId = idToken['oid'] || idToken['sid'];
  const realm = idToken['tid'];
  const homeAccountId = `${localAccountId}.${realm}`;
  const username = idToken['preferred_username'];
  const name = idToken['name'];

  const accountKey = `${homeAccountId}-${envUrl}-${realm}`;
  const accountEntity = buildAccountEntity(homeAccountId, realm, localAccountId, username, name, idToken);

  const idTokenKey = `${homeAccountId}-${envUrl}-idtoken-${environment.clientId}-${realm}-`;
  const idTokenEntity = buildIdTokenEntity(homeAccountId, tokenResponse.id_token, realm);

  const accessTokenKey = `${homeAccountId}-${envUrl}-accesstoken-${environment.clientId}-${realm}-${environment.scope}`;
  const accessTokenEntity = buildAccessTokenEntity(
    homeAccountId,
    tokenResponse.access_token,
    tokenResponse.expires_in,
    tokenResponse.ext_expires_in,
    realm,
    environment.scope
  );

  const refreshTokenKey = `${homeAccountId}-${envUrl}-refreshtoken-${environment.clientId}--`;
  const refreshTokenEntity = buildRefreshTokenEntity(homeAccountId, tokenResponse.refresh_token);

  localStorage.setItem(accountKey, JSON.stringify(accountEntity));
  localStorage.setItem(idTokenKey, JSON.stringify(idTokenEntity));
  localStorage.setItem(accessTokenKey, JSON.stringify(accessTokenEntity));
  localStorage.setItem(refreshTokenKey, JSON.stringify(refreshTokenEntity));
};

const login = (cachedTokenResponse: any, username: string, password: string) => {
  let tokenResponse: any = null;
  let chainable: Cypress.Chainable = cy.visit('http://localhost:4200/testentry');

  if (!cachedTokenResponse) {
    chainable = chainable.request({
      url: `${environment.authority}/oauth2/v2.0/token`,
      method: 'POST',
      body: {
        grant_type: 'password',
        client_id: environment.clientId,
        client_secret: credentials.clientSecret,
        scope: ['openid profile'].concat(environment.scope).join(' '),
        username: username,
        password: password,
      },
      form: true,
    });
  } else {
    chainable = chainable.then(() => {
      return {
        body: cachedTokenResponse,
      };
    });
  }

  chainable
    .then((response) => {
      injectTokens(response.body);
      tokenResponse = response.body;
    })
    .reload()
    .then(() => {
      return tokenResponse;
    });

  return chainable;
};

export const loginAdmin = (cachedTokenResponse: any) => {
  return login(cachedTokenResponse, credentials.admin.username, credentials.admin.password);
};

export const loginRolle = (cachedTokenResponse: any) => {
  return login(cachedTokenResponse, credentials.rolle.username, credentials.rolle.password);
};
