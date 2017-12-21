import React from 'react';
import { connect } from 'react-redux';
import FormGroupBase from '../shared/FormGroupBase.jsx';

class PasswordFormGroup extends React.Component {
  constructor(props) {
    super(props);
    this.validation = (state) => {
      if (state.focused || !state.hasFocused) {
        return null;
      }

      const { value } = this.props.pwInputProps;
      if (value.length > 6) {
        return 'success';
      }

      return 'error';
    };

    this.onChange = (e) => this.props.onChange(e.target.value, this.props.pwInputProps.action);
  }

  render() {
    const { value, placeholder } = this.props.pwInputProps;
    const baseProps = {
      type: 'password',
      validation: this.validation,
      label: 'Password',
      placeholder: placeholder,
      onChange: this.onChange,
      value: value
    };

    return <FormGroupBase baseProps={baseProps} />;
  }
}

const mapStateToProps = state => { return {}; };
const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onChange: (password, action) => dispatch(action(password))
  };
};

const PasswordFormGroupContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(PasswordFormGroup);

export default PasswordFormGroupContainer;
