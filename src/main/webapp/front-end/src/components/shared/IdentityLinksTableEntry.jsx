import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const IdentityLinksTableEntry = ({ data, removeFunction, match, leave }) => {

  return (
    <tr>
      <td>{data.left}</td>
      <td>{data.right}</td>
      <td>
        <ButtonGroup>
            <Button bsStyle="danger" onClick={removeFunction}>
                Delete
            </Button>
        </ButtonGroup>
      </td>
    </tr>)
  ;
};

export default IdentityLinksTableEntry;
