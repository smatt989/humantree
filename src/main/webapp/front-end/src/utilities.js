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

export function dispatchPattern(request, success, error, successCallback) {
    return function(input1, input2, input3, input4, input5) {
            return(store.dispatch(request(input1, input2, input3, input4, input5))
                .then(response => {
                    if(response.error) {
                        store.dispatch(error(response.error));
                        return false;
                    }

                    store.dispatch(success(response.payload.data));
                    if(successCallback){
                        successCallback(response.payload.data)
                    }
                    return true;
                }))
                }
}