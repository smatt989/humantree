import React from 'react';
import {
  Grid,
  PageHeader,
  Button
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {IdentityLinksTableContainer} from './IdentityLinksTable.jsx';
import NavBar from '../NavBar.jsx';
import { domain } from '../../actions.js';


const IdentityLinks = (props) => {
  console.log(domain)
  return <Grid>
    <NavBar inverse={false}/>
    <div className='container'>
      <PageHeader>
        Identity Links
      </PageHeader>
      <IdentityLinksTableContainer {...props} />
    </div>
  </Grid>;
};

export default IdentityLinks;
