import React from 'react';
import {
  Grid,
  PageHeader,
  Button
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {EmailTableContainer} from './EmailTable.jsx';
import NavBar from '../NavBar.jsx';
import { domain } from '../../actions.js';


const Emails = (props) => {
  console.log(domain)
  return <Grid>
    <NavBar inverse={false}/>
    <div className='container'>
      <PageHeader>
        Emails
        <a href={domain+"/auth"}>
          <Button
            className="new-tbl-item-btn"
            bsStyle="primary"
            type="button">
            New Email
          </Button>
        </a>
      </PageHeader>
      <EmailTableContainer {...props} />
    </div>
  </Grid>;
};

export default Emails;
