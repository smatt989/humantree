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
    loginPassword: Map({ password: '' }),
    getTree: Map({tree: null, error: null, loading: false}),
    getEmails: Map({emails: List.of(), error: null, loading: false}),
    startScraping: Map({loading: false, error: null}),
    sharingKey: Map({key: null, error: null, loading: false})
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

function getTree(state) {
  return state.set('getTree', Map({tree: null, error: null, loading: true}));
}

function getTreeSuccess(state, tree) {
  return state.set('getTree', Map({tree: Immutable.fromJS(tree), error: null, loading: false}));
}

function getTreeError(state, error) {
  return state.set('getTree', Map({tree: null, error: Immutable.fromJS(error), loading: false}));
}

function getConnectedEmailAccounts(state) {
  return state.set('getEmails', Map({emails: List.of(), error: null, loading: true}));
}

function getConnectedEmailAccountsSuccess(state, emails) {
 return state.set('getEmails', Map({emails: Immutable.fromJS(emails), error: null, loading: false}));
}

function getConnectedEmailAccountsError(state, error) {
  return state.set('getEmails', Map({emails: List.of(), error: Immutable.fromJS(error), loading: false}));
}

function startScraping(state) {
  return state.set('startScraping', Map({loading: true, error: null}));
}

function startScrapingSuccess(state) {
  return state.set('startScraping', Map({loading: false, error: null}));
}

function startScrapingError(state, error) {
  return state.set('startScraping', Map({loading: false, error: Immutable.fromJS(error)}));
}

function startRescraping(state) {
  return startScraping(state);
}

function startRescrapingSuccess(state) {
  return startScrapingSuccess(state);
}

function startRescrapingError(state, error) {
  return startScrapingError(state, error);
}

function shareTree(state) {
  return state.set('sharingKey', Map({key: null, error: null, loading: true}));
}

function shareTreeSuccess(state, key) {
  return state.set('sharingKey', Map({key: key.key, error: null, loading: false}));
}

function shareTreeError(state, error) {
  return state.set('sharingKey', Map({key: null, error: Immutable.fromJS(error), loading: false}));
}

function removeKeyFromState(state) {
  return state.set('sharingKey', Map({key: null, error: null, loading: false}));
}

function getSharedTree(state) {
  return getTree(state);
}

function getSharedTreeSuccess(state, tree) {
  return getTreeSuccess(state, tree);
}

function getSharedTreeError(state, error) {
  return getTreeError(state, error);
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
    case 'GET_TREE':
      return getTree(state);
    case 'GET_TREE_SUCCESS':
      return getTreeSuccess(state, action.payload);
    case 'GET_TREE_ERROR':
      return getTreeError(state, action.error);
    case 'GET_CONNECTED_EMAIL_ACCOUNTS':
      return getConnectedEmailAccounts(state);
    case 'GET_CONNECTED_EMAIL_ACCOUNTS_SUCCESS':
      return getConnectedEmailAccountsSuccess(state, action.payload);
    case 'GET_CONNECTED_EMAIL_ACCOUNTS_ERROR':
      return getConnectedEmailAccountsError;
    case 'START_SCRAPING':
      return startScraping(state);
    case 'START_SCRAPING_SUCCESS':
      return startScrapingSuccess(state, action.payload);
    case 'START_SCRAPING_ERROR':
      return startScrapingError(state, action.error);
    case 'START_RESCRAPING':
      return startRescraping(state);
    case 'START_RESCRAPING_SUCCESS':
      return startRescrapingSuccess(state, action.payload);
    case 'START_RESCRAPING_ERROR':
      return startRescrapingError(state, action.error);
    case 'SHARE_TREE':
      return shareTree(state);
    case 'SHARE_TREE_SUCCESS':
      return shareTreeSuccess(state, action.payload);
    case 'SHARE_TREE_ERROR':
      return shareTreeError(state, action.error);
    case 'REMOVE_KEY_FROM_STATE':
      return removeKeyFromState(state);
    case 'GET_SHARED_TREE':
      return getSharedTree(state);
    case 'GET_SHARED_TREE_SUCCESS':
      return getSharedTreeSuccess(state, action.payload);
    case 'GET_SHARED_TREE_ERROR':
      return getSharedTreeError(state, action.error);
    default:
      return state;
  }
};
