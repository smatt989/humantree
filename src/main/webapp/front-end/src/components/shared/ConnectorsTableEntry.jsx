import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const ConnectorsTableEntry = ({ data, since }) => {

  return (
    <tr>
      <td><Link to={"/tree/"+data.name+"?since="+since}><b>{data.name}</b></Link></td>
      <td>{data.introductions}</td>
    </tr>)
  ;
};

export default ConnectorsTableEntry;
