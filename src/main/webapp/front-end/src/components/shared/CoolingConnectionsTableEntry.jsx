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
      <td className="tight-edge">{moment(data.lastInteractionMillis).format('D MMM YYYY')}</td>
    </tr>)
  ;
};

export default CoolingConnectionsTableEntry;
