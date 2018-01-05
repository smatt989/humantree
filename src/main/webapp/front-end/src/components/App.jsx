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
import TreeContainer from './shared/Tree.jsx';
import PrivateRouteContainer from './PrivateRoute.jsx';
import Emails from './shared/Emails.jsx';
import TreePageContainer from './shared/TreePage.jsx';
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
          <PrivateRouteContainer exact path="/emails" component={Emails}/>
          <PrivateRouteContainer path="/tree/:root" component={TreePageContainer} />
          <PrivateRouteContainer path="/tree" component={TreePageContainer} />
          <Route path="/shared/:key/:root" component={TreePageContainer} />
          <Route path="/shared/:key" component={TreePageContainer} />
          <Route component={Err}/>
        </Switch>
      </div>
    </Router>;
  }
}
