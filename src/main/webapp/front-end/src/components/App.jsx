import '../styles/app.less';
import React from 'react';
import {
  HashRouter as Router,
  Route,
  Switch
} from 'react-router-dom';
import { Grid, Row, Col } from 'react-bootstrap';
import NavBarContainer from './NavBar.jsx';
import Home from './Home.jsx';
import LoginContainer from './account_forms/Login.jsx';
import RegisterContainer from './account_forms/Register.jsx';
import PrivateRouteContainer from './PrivateRoute.jsx';
import Err from './Error.jsx';

export default class App extends React.Component {
  render() {
    return <Router>
      <div>
        <Switch>
          <Route exact path="/" component={Home}/>
          <Route exact path="/login" component={LoginContainer}/>
          <Route exact path="/register" component={RegisterContainer}/>
          <Route exact path="/recover" component={LoginContainer}/>
          <Route component={Err}/>
        </Switch>
      </div>
    </Router>;
  }
}
