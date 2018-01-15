import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const IntroductionsTableEntry = ({ data }) => {

  var sender = data.sender ? " ("+data.sender+")" : null

  return (
    <tr>
      <td><b>{data.intro}</b>{sender}</td>
      <td>{new Date(data.dateMillis).toDateString()}</td>
    </tr>)
  ;
};

export default IntroductionsTableEntry;
