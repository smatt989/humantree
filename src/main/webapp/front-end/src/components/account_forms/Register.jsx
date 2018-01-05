import React from 'react';
import {
  Button
} from 'react-bootstrap';
import { connect } from 'react-redux';
import { Link, Redirect } from 'react-router-dom';
import { signupEmailChanged, signupPasswordChanged, signupClearInputs, createUser, createUserSuccess, createUserError } from '../../actions.js';
import { tryLogin } from '../../utilities.js';
import EmailFormGroupContainer from './EmailFormGroup.jsx';
import PasswordFormGroup from './PasswordFormGroup.jsx';
import NavBar from '../NavBar.jsx';

class Register extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      redirectToReferrer: false
    };

    this.onSubmit = (e) => {
      e.preventDefault();
      this.props.onSubmit(this.props.email, this.props.password)
        .then(isSuccess => {
          this.setState({ redirectToReferrer: isSuccess })
          if (isSuccess) {
            this.props.clearInputs();
          }
        });
    };
  }

  render() {
    const emailInputProps = { value: this.props.email, placeholder: 'Enter your email', action: (email) => signupEmailChanged(email) };
    const pwInputProps = { value: this.props.password, placeholder: 'Choose a password', action: (password) => signupPasswordChanged(password) };
    const { from } = this.props.location.state || { from: { pathname: '/' } }

    if (this.state.redirectToReferrer) {
      return <Redirect to={'/emails'} />;
    }

    return (
      <div className='sign-up full-screen-page'>
        <NavBar inverse={true} />
        <div className='col-md-push-4 col-md-4 content-block-inverse'>
          <h1 className='m-t-3 m-b-3'>Sign Up</h1>
          <form role='form' onSubmit={e => this.onSubmit(e)}>
            <EmailFormGroupContainer emailInputProps={emailInputProps}/>
            <PasswordFormGroup pwInputProps={pwInputProps}/>
            <div className='text-xs-center'>
              <Button
                bsStyle='primary'
                type='submit'>
                Create Account
              </Button>
              <p className='m-t-1'><span className='m-l-1'>Have an account? <Link to={{ pathname: '/login' }}>Log in</Link>.</span></p>
            </div>
          </form>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    email: state.getIn(['signupEmail', 'email']),
    password: state.getIn(['signupPassword', 'password'])
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    // TODO onSubmit validation, prevent submission if error
    onSubmit: (email, password) => {
      return dispatch(createUser(email, password))
        .then(response => {
          if (response.error) {
            dispatch(createUserError(response.error));
            return false;
          }

          dispatch(createUserSuccess(response.payload.data));
          return tryLogin(email, password);
        });
    },
    clearInputs: () => dispatch(signupClearInputs())
  };
};

const RegisterContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(Register);

export default RegisterContainer;
