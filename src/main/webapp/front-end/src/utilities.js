import store from './store.js';
import Cookies from 'js-cookie';
import { login, loginError, loginSuccess, loginClearInputs } from './actions.js';
var _ = require('lodash');

export var authenticationHeader = 'scalatra-session-key';
export var cookieName = 'SCALATRA_SESS_KEY';

export function getSession() {
  return Cookies.get(cookieName);
}

export function setSession(session) {
  if (session == null) {
    Cookies.remove(cookieName);
  } else {
    Cookies.set(cookieName, session, { expires: 30 });
  }
}

export function authenticate() {
  var authentication = {};
  authentication[authenticationHeader] = Cookies.get(cookieName);
  return authentication;
}

export const tryLogin = (email, password) => {
  return store.dispatch(login(email, password))
    .then(response => {
      if (response.error) {
        store.dispatch(loginError(response.error));
        return false;
      }

      const session = response.payload.headers[authenticationHeader];
      if (!session) {
        return false;
      }

      store.dispatch(loginSuccess(session));
      return true;
    });
};

export function isNullLabel(label) {
  return label.labelValue == 0 && !label.point1x && !label.xCoordinate
}