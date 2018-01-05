import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { logout, logoutError, logoutSuccess } from '../actions.js';
import { Navbar, NavItem, Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import BeeLabel from './BeeLabel.jsx';

const NavRight = (props) => {
  return props.session != null
    ? (
        <Nav pullRight>
          <LinkContainer to="/emails"><NavItem eventKey={0}>Email Accounts</NavItem></LinkContainer>
          <LinkContainer to="/tree"><NavItem eventKey={1}>Tree</NavItem></LinkContainer>
          <NavItem eventKey={2} onClick={() => props.logout(props.session)}>Log Out</NavItem>
        </Nav>
      )
    : (
      <Nav pullRight>
        <LinkContainer to="/login"><NavItem eventKey={1}>Log In</NavItem></LinkContainer>
        <LinkContainer to="/register"><NavItem eventKey={2}>Sign Up</NavItem></LinkContainer>
      </Nav>
    );
};

class NavBar extends React.Component {
  render() {
    const { inverse } = this.props;
    const props = inverse ? {inverse: inverse } : {};
    props.className = 'm-t-4';
    return <Navbar {...props} collapseOnSelect>
      <Navbar.Header>
        <BeeLabel />
      </Navbar.Header>
      <Navbar.Collapse>
        <NavRight session={this.props.session} logout={this.props.logout}/>
      </Navbar.Collapse>
    </Navbar>;
  }
}

const mapStateToProps = state => {
  return {
    session: state.getIn(['login', 'session'])
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    logout: (session) => {
      return dispatch(logout(session))
        .then(response => {
          if (response.error) {
            dispatch(logoutError(response.error));
            return false;
          }

          dispatch(logoutSuccess(response.payload));
          return true;
        });
    }
  };
};

const NavBarContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(NavBar);

export default NavBarContainer;
