import React from 'react';
import {
  FormGroup,
  ControlLabel,
  FormControl
} from 'react-bootstrap';

export default class FormGroupBase extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      focused: false,
      hasFocused: false
    };

    this.onFocus = () => this.setState({ focused: true });
    this.onBlur = () => this.setState({ focused: false, hasFocused: true });
  }

  render() {
    const { type, label, validation, placeholder, value, onChange } = this.props.baseProps;
    const getValidationState = validation != null ? validation : () => { return null; };
    return (
      <FormGroup controlId={type} validationState={getValidationState(this.state)}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl
          type={type}
          value={value}
          placeholder={placeholder}
          onChange={onChange}
          onFocus={this.onFocus}
          onBlur={this.onBlur} />
        <FormControl.Feedback />
      </FormGroup>
    );
  }
}
