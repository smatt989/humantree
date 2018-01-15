import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const ConnectorsTableEntry = ({ data }) => {

  return (
    <tr>
      <td><b>{data.name}</b></td>
      <td>{data.introductions}</td>
    </tr>)
  ;
};

export default ConnectorsTableEntry;
