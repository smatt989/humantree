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
import PrivacyPolicy from './PrivacyPolicy.jsx';
import LoginContainer from './account_forms/Login.jsx';
import RegisterContainer from './account_forms/Register.jsx';
import TreeContainer from './shared/Tree.jsx';
import PrivateRouteContainer from './PrivateRoute.jsx';
import Emails from './shared/Emails.jsx';
import IdentityLinks from './shared/IdentityLinks.jsx';
import {InsightsContainer} from './shared/Insights.jsx';
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
          <Route exact path="/privacy" component={PrivacyPolicy}/>
          <PrivateRouteContainer exact path="/emails" component={Emails}/>
          <PrivateRouteContainer path="/tree/:root" component={TreePageContainer} />
          <PrivateRouteContainer path="/tree" component={TreePageContainer} />
          <Route path="/shared/:key/:root" component={TreePageContainer} />
          <Route path="/shared/:key" component={TreePageContainer} />
          <PrivateRouteContainer path="/links" component={IdentityLinks} />
          <PrivateRouteContainer path="/insights" component={InsightsContainer} />
          <Route component={Err}/>
        </Switch>
      </div>
    </Router>;
  }
}
