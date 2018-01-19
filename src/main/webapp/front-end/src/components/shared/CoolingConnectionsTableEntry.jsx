import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const CoolingConnectionsTableEntry = ({ data }) => {

  return (
    <tr>
      <td><b>{data.email}</b></td>
      <td>{new Date(data.lastInteractionMillis).toDateString()}</td>
    </tr>)
  ;
};

export default CoolingConnectionsTableEntry;
