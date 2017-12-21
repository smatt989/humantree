import {createStore, applyMiddleware} from 'redux';
import { cleanState, ontologyTypes, ontologyTypesSuccess, ontologyTypesError, imageSourceTypes, imageSourceTypesSuccess, imageSourceTypesError } from './actions';
import reducer from './reducer';
import promise from 'redux-promise';

function initStore() {
  const createStoreWithMiddleware = applyMiddleware(
    promise
  )(createStore);
  const store = createStoreWithMiddleware(reducer, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());
  store.dispatch(cleanState());
  fetchReferenceData(store);
  return store;
}

const store = initStore();
export default store;

function fetchReferenceData(store) {
    //PUT CALLS IN HERE TO MAKE AT STARTUP
}
