import React from 'react';
import { Grid, Jumbotron, Button, Glyphicon } from 'react-bootstrap';
import { Link, Redirect } from 'react-router-dom';
import BeeLabel from './BeeLabel.jsx';
import NavBar from './NavBar.jsx';

export default class Home extends React.Component {
  render() {
    return <div className="landing full-screen-page">
        <NavBar inverse={true} />
        <div className='container'>
          <div className="jumbotron jumbotron-home text-xs-center">
              <div className='row'>
                <div className='col-md-12'>
                  <h1>Know Thyself</h1>
                  <h3>See what's good</h3>
                </div>
              </div>
              <div className='row m-t-2'>
                <div className='col-md-12'>
                  <Link to="/register">
                    <Button bsStyle="success">Create Account</Button>
                  </Link>
                </div>
              </div>
          </div>
        </div>
      </div>;
  }
};
