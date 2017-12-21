import axios from 'axios';
import {authenticatedSession, authenticationHeader, authenticate} from './utilities';

const domain = CONFIG ? CONFIG.frontServer ? 'http://localhost:8080' : '' : '';

/*
 * action types
 */

export const SIGNUP_EMAIL_CHANGED = 'SIGNUP_EMAIL_CHANGED';
export const SIGNUP_PASSWORD_CHANGED = 'SIGNUP_PASSWORD_CHANGED';
export const SIGNUP_CLEAR_INPUTS = 'SIGNUP_CLEAR_INPUTS';
export const LOGIN_EMAIL_CHANGED = 'LOGIN_EMAIL_CHANGED';
export const LOGIN_PASSWORD_CHANGED = 'LOGIN_PASSWORD_CHANGED';
export const LOGIN_CLEAR_INPUTS = 'LOGIN_CLEAR_INPUTS';

/*
 * action creators
 */

export function cleanState() {
  return {
    type: 'CLEAN_STATE'
  };
}

export function createUser(email, password) {
  const request = axios({
    method: 'post',
    url: `${domain}/users/create`,
    headers: {
      'email': email,
      'password': password
    }
  });

  return {
    type: 'CREATE_USER',
    payload: request
  };
}

export function createUserSuccess(loaded) {
  return {
    type: 'CREATE_USER_SUCCESS',
    payload: loaded
  };
}

export function createUserError(error) {
  return {
    type: 'CREATE_USER_ERROR',
    error: error
  };
}

export function login(email, password) {
  const request = axios({
    method: 'get',
    url: `${domain}/sessions/new`,
    headers: {
      'email': email,
      'password': password
    }
  });

  return {
    type: 'LOGIN',
    payload: request
  };
}

export function loginSuccess(loaded) {
  return {
    type: 'LOGIN_SUCCESS',
    payload: loaded
  };
}

export function loginError(error) {
  return {
    type: 'LOGIN_ERROR',
    error: error
  };
}

export function logout(session) {
  const request = axios({
    method: 'post',
    url: `${domain}/sessions/logout`,
    headers: authenticate()
  });

  return {
    type: 'LOGOUT',
    payload: request
  };
}

export function logoutSuccess(loaded) {
  return {
    type: 'LOGOUT_SUCCESS',
    payload: loaded
  };
}

export function logoutError(error) {
  return {
    type: 'LOGOUT_ERROR',
    error: error
  };
}

export function signupEmailChanged(email) {
  return {
    type: SIGNUP_EMAIL_CHANGED,
    email: email
  }
}

export function signupPasswordChanged(password) {
  return {
    type: SIGNUP_PASSWORD_CHANGED,
    password: password
  }
}

export function signupClearInputs() {
  return {
    type: SIGNUP_CLEAR_INPUTS
  }
}

export function loginEmailChanged(email) {
  return {
    type: LOGIN_EMAIL_CHANGED,
    email: email
  }
}

export function loginPasswordChanged(password) {
  return {
    type: LOGIN_PASSWORD_CHANGED,
    password: password
  }
}

export function loginClearInputs() {
  return {
    type: LOGIN_CLEAR_INPUTS
  }
}