import React from 'react';
import { connect } from 'react-redux';
import FormGroupBase from '../shared/FormGroupBase.jsx';

class EmailFormGroup extends React.Component {
  constructor(props) {
    super(props);
    this.regexp = new RegExp(/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}[a-zA-Z0-9])?)*$/);
    this.validation = (state) => {
      if (state.focused || !state.hasFocused) {
        return null;
      }

      const { value } = this.props.emailInputProps;
      if (value.length > 0 && this.regexp.exec(value)) {
        return 'success';
      }

      return 'error';
    };

    this.onChange = (e) => this.props.onChange(e.target.value, this.props.emailInputProps.action);
  }

  render() {
    const { value, placeholder } = this.props.emailInputProps;
    const baseProps = {
      type: 'email',
      validation: this.validation,
      label: 'Email Address',
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
    onChange: (email, action) => dispatch(action(email))
  };
};

const EmailFormGroupContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(EmailFormGroup);

export default EmailFormGroupContainer;
