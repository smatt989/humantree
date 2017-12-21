import { Map, List, is } from 'immutable';
import Immutable from 'immutable';
import { getSession, setSession } from './utilities';
import {
  SIGNUP_EMAIL_CHANGED, SIGNUP_PASSWORD_CHANGED, SIGNUP_CLEAR_INPUTS,
  LOGIN_EMAIL_CHANGED, LOGIN_PASSWORD_CHANGED, LOGIN_CLEAR_INPUTS
} from './actions.js';


function cleanState() {
  const sessionKey = getSession();
  setSession(sessionKey); // refresh session key

  const cleanState = Map({
    createUser: Map({loading: false, error: null}),
    login: Map({session: sessionKey, error: null, loading: false}),
    user: Map({email: null, id: null}),
    logout: Map({error: null, loading: false}),
    signupEmail: Map({ email: '' }),
    signupPassword: Map({ password: '' }),
    loginEmail: Map({ email: '' }),
    loginPassword: Map({ password: '' })
  });

  return cleanState;
}

function createUser(state) {
  return state.set('createUser', Map({loading: true, error: null}));
}

function createUserSuccess(state, user) {
  return state.set('createUser', Map({loading: false, error: null}));
}

function createUserError(state, error) {
  return state.set('createUser', Map({loading: false, error: Immutable.fromJS(error)}));
}

function login(state) {
  return state.set('login', Map({session: null, error: null, loading: true}));
}

function loginSuccess(state, session) {
  setSession(session);
  return state.set('login', Map({session: session, error: null, loading: false}));
}

function loginError(state, error) {
  return state.set('login', Map({session: null, error: error, loading: false}));
}

function logout(state) {
  return state.set('logout', Map({error: null, loading: true}));
}

function logoutSuccess(state, payload) {
  setSession(null);
  const newState = state.set('login', Map({session: null, error: null, loading: false}));
  return newState.set('logout', Map({error: null, loading: false}));
}

function logoutError(state, error) {
  return state.set('logout', Map({error: error, loading: false}));
}

function signupEmailChanged(state, email) {
  return state.set('signupEmail', Map({ email: email }));
}

function signupPasswordChanged(state, password) {
  return state.set('signupPassword', Map({ password: password }));
}

function signupClearInputs(state) {
  const newState = state.set('signupEmail', Map({ email: '' }));
  return newState.set('signupPassword', Map({ password: '' }));
}

function loginEmailChanged(state, email) {
  return state.set('loginEmail', Map({ email: email }));
}

function loginPasswordChanged(state, password) {
  return state.set('loginPassword', Map({ password: password }));
}

function loginClearInputs(state) {
  const newState = state.set('loginEmail', Map({ email: '' }));
  return newState.set('loginPassword', Map({ password: '' }));
}

export default function reducer(state = Map(), action) {
  switch (action.type) {
    case 'CLEAN_STATE':
      return cleanState();
    case 'CREATE_USER':
      return createUser(state);
    case 'CREATE_USER_SUCCESS':
      return createUserSuccess(state, action.email);
    case 'CREATE_USER_ERROR':
      return createUserError(state, action.error);
    case 'LOGIN':
      return login(state);
    case 'LOGIN_SUCCESS':
      return loginSuccess(state, action.payload);
    case 'LOGIN_ERROR':
      return loginError(state, action.error);
    case 'LOGOUT':
      return logout(state);
    case 'LOGOUT_SUCCESS':
      return logoutSuccess(state, action.payload);
    case 'LOGOUT_ERROR':
      return logoutError(state, action.error);
    case SIGNUP_EMAIL_CHANGED:
      return signupEmailChanged(state, action.email);
    case SIGNUP_PASSWORD_CHANGED:
      return signupPasswordChanged(state, action.password);
    case SIGNUP_CLEAR_INPUTS:
      return signupClearInputs(state);
    case LOGIN_EMAIL_CHANGED:
      return loginEmailChanged(state, action.email);
    case LOGIN_PASSWORD_CHANGED:
      return loginPasswordChanged(state, action.password);
    case LOGIN_CLEAR_INPUTS:
      return loginClearInputs(state);
    default:
      return state;
  }
};
