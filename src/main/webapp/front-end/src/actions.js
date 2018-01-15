import axios from 'axios';
import {authenticatedSession, authenticationHeader, authenticate} from './utilities';

export const domain = CONFIG ? CONFIG.frontServer ? 'http://localhost:8080' : '' : '';

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

export function getTree(root, emails) {

  var data = {}

  if(emails) {
    data['emails'] = emails
  }

  if(root){
    data['root'] = root
  }

  const request = axios({
    method: 'post',
    url: `${domain}/tree`,
    data: data,
    headers: authenticate()
  });

  return {
    type: 'GET_TREE',
    payload: request
  };
}

export function getTreeSuccess(loaded) {
  return {
    type: 'GET_TREE_SUCCESS',
    payload: loaded
  };
}

export function getTreeError(error) {
  return {
    type: 'GET_TREE_ERROR',
    error: error
  };
}

export function getConnectedEmailAccounts(root, emails) {

  const request = axios({
    method: 'get',
    url: `${domain}/connectedemails`,
    headers: authenticate()
  });

  return {
    type: 'GET_CONNECTED_EMAIL_ACCOUNTS',
    payload: request
  };
}

export function getConnectedEmailAccountsSuccess(loaded) {
  return {
    type: 'GET_CONNECTED_EMAIL_ACCOUNTS_SUCCESS',
    payload: loaded
  };
}

export function getConnectedEmailAccountsError(error) {
  return {
    type: 'GET_CONNECTED_EMAIL_ACCOUNTS_ERROR',
    error: error
  };
}

export function startScraping(email) {

  const request = axios({
    method: 'get',
    url: `${domain}/scrape/${email}`,
    headers: authenticate()
  });

  return {
    type: 'START_SCRAPING',
    payload: request
  };
}

export function startScrapingSuccess(loaded) {
  return {
    type: 'START_SCRAPING_SUCCESS',
    payload: loaded
  };
}

export function startScrapingError(error) {
  return {
    type: 'START_SCRAPING_ERROR',
    error: error
  };
}

export function startRescraping(email) {

  const request = axios({
    method: 'get',
    url: `${domain}/rescrape/${email}`,
    headers: authenticate()
  });

  return {
    type: 'START_RESCRAPING',
    payload: request
  };
}

export function startRescrapingSuccess(loaded) {
  return {
    type: 'START_RESCRAPING_SUCCESS',
    payload: loaded
  };
}

export function startRescrapingError(error) {
  return {
    type: 'START_RESCRAPING_ERROR',
    error: error
  };
}

export function shareTree(root) {

  const request = axios({
    method: 'post',
    url: `${domain}/share`,
    headers: authenticate(),
    data: {root: root}
  });

  return {
    type: 'SHARE_TREE',
    payload: request
  };
}

export function shareTreeSuccess(loaded) {
  return {
    type: 'SHARE_TREE_SUCCESS',
    payload: loaded
  };
}

export function shareTreeError(error) {
  return {
    type: 'SHARE_TREE_ERROR',
    error: error
  };
}

export function removeKeyFromState() {
  return {
    type: 'REMOVE_KEY_FROM_STATE'
  }
}

export function getSharedTree(key) {

  const request = axios({
    method: 'post',
    url: `${domain}/shared/${key}`
  });

  return {
    type: 'GET_SHARED_TREE',
    payload: request
  };
}

export function getSharedTreeSuccess(loaded) {
  return {
    type: 'GET_SHARED_TREE_SUCCESS',
    payload: loaded
  };
}

export function getSharedTreeError(error) {
  return {
    type: 'GET_SHARED_TREE_ERROR',
    error: error
  };
}

export function searchNames(query) {
  return {
    type: 'SEARCH_NAMES',
    query: query
  }
}

export function clearSearchNames() {
  return {
    type: 'CLEAR_SEARCH_NAMES'
  }
}

export function startNewLink() {
  return {
    type: 'START_NEW_LINK'
  }
}

export function updateNewLinkLeft(left) {
  return {
    type: 'UPDATE_NEW_LINK_LEFT',
    left: left
  }
}

export function updateNewLinkRight(right) {
  return {
    type: 'UPDATE_NEW_LINK_RIGHT',
    right: right
  }
}

export function createIdentityLink(left, right) {

  const request = axios({
    method: 'post',
    url: `${domain}/links/create`,
    headers: authenticate(),
    data: {left: left, right: right}
  });

  return {
    type: 'CREATE_IDENTITY_LINK',
    payload: request
  };
}

export function createIdentityLinkSuccess(loaded) {
  return {
    type: 'CREATE_IDENTITY_LINK_SUCCESS',
    payload: loaded
  };
}

export function createIdentityLinkError(error) {
  return {
    type: 'CREATE_IDENTITY_LINK_ERROR',
    error: error
  };
}

export function getIdentityLinks() {

  const request = axios({
    method: 'get',
    url: `${domain}/links`,
    headers: authenticate()
  });

  return {
    type: 'GET_IDENTITY_LINKS',
    payload: request
  };
}

export function getIdentityLinksSuccess(loaded) {
  return {
    type: 'GET_IDENTITY_LINKS_SUCCESS',
    payload: loaded
  };
}

export function getIdentityLinksError(error) {
  return {
    type: 'GET_IDENTITY_LINKS_ERROR',
    error: error
  };
}

export function deleteIdentityLink(left, right) {

  const request = axios({
    method: 'post',
    url: `${domain}/links/delete`,
    headers: authenticate(),
    data: {left: left, right: right}
  });

  return {
    type: 'DELETE_IDENTITY_LINK',
    payload: request
  };
}

export function deleteIdentityLinkSuccess(loaded) {
  return {
    type: 'DELETE_IDENTITY_LINK_SUCCESS',
    payload: loaded
  };
}

export function deleteIdentityLinkError(error) {
  return {
    type: 'DELETE_IDENTITY_LINK_ERROR',
    error: error
  };
}

export function createAnnotation(nodeName, annotation) {

  const request = axios({
    method: 'post',
    url: `${domain}/annotations/create`,
    headers: authenticate(),
    data: {name:  nodeName, annotation: annotation}
  });

  return {
    type: 'CREATE_ANNOTATION',
    payload: request
  };
}

export function createAnnotationSuccess(loaded) {
  return {
    type: 'CREATE_ANNOTATION_SUCCESS',
    payload: loaded
  };
}

export function createAnnotationError(error) {
  return {
    type: 'CREATE_ANNOTATION_ERROR',
    error: error
  };
}

export function getAnnotations(annotation) {

  const request = axios({
    method: 'get',
    url: `${domain}/annotations/${annotation}`,
    headers: authenticate()
  });

  return {
    type: 'GET_ANNOTATIONS',
    payload: request
  };
}

export function getAnnotationsSuccess(loaded) {
  return {
    type: 'GET_ANNOTATIONS_SUCCESS',
    payload: loaded
  };
}

export function getAnnotationsError(error) {
  return {
    type: 'GET_ANNOTATIONS_ERROR',
    error: error
  };
}

export function getSharedAnnotations(key, annotation) {

  const request = axios({
    method: 'get',
    url: `${domain}/shared/${key}/annotations/${annotation}`
  });

  return {
    type: 'GET_SHARED_ANNOTATIONS',
    payload: request
  };
}

export function getSharedAnnotationsSuccess(loaded) {
  return {
    type: 'GET_SHARED_ANNOTATIONS_SUCCESS',
    payload: loaded
  };
}

export function getSharedAnnotationsError(error) {
  return {
    type: 'GET_SHARED_ANNOTATIONS_ERROR',
    error: error
  };
}

export function deleteAnnotation(nodeName, annotation) {

  const request = axios({
    method: 'post',
    url: `${domain}/annotations/delete`,
    headers: authenticate(),
    data: {name: nodeName, annotation: annotation}
  });

  return {
    type: 'DELETE_ANNOTATION',
    payload: request
  };
}

export function deleteAnnotationSuccess(loaded) {
  return {
    type: 'DELETE_ANNOTATION_SUCCESS',
    payload: loaded
  };
}

export function deleteAnnotationError(error) {
  return {
    type: 'DELETE_ANNOTATION_ERROR',
    error: error
  };
}

export function getIntroductions(sinceMillis) {

  const request = axios({
    method: 'get',
    url: `${domain}/introductions?since=${sinceMillis}`,
    headers: authenticate()
  });

  return {
    type: 'GET_INTRODUCTIONS',
    payload: request
  };
}

export function getIntroductionsSuccess(loaded) {
  return {
    type: 'GET_INTRODUCTIONS_SUCCESS',
    payload: loaded
  };
}

export function getIntroductionsError(error) {
  return {
    type: 'GET_INTRODUCTIONS_ERROR',
    error: error
  };
}

export function getConnectors(sinceMillis) {

  const request = axios({
    method: 'get',
    url: `${domain}/connectors?since=${sinceMillis}`,
    headers: authenticate()
  });

  return {
    type: 'GET_CONNECTORS',
    payload: request
  };
}

export function getConnectorsSuccess(loaded) {
  return {
    type: 'GET_CONNECTORS_SUCCESS',
    payload: loaded
  };
}

export function getConnectorsError(error) {
  return {
    type: 'GET_CONNECTORS_ERROR',
    error: error
  };
}