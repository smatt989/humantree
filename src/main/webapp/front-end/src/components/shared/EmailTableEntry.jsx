import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const EmailTableEntry = ({ data, scrapeFunction, match, leave }) => {

  var status = "not started"
  var lastPulled = null

  if(data.status == "running") {
    status = "starting up..."
  }

  if(data.totalThreads){
    lastPulled = new Date(data.lastPullMillis).toDateString()

    if(data.totalThreads == data.threadsProcessed) {
        status = "done"
    } else if (data.status == "stopped"){
        status = "paused"
    } else {
        status = data.threadsProcessed + " / " + data.totalThreads
    }
  }

  return (
    <tr>
      <td><Link to={{ pathname: "/emails/"+data.questionId }}>{data.email}</Link></td>
      <td>{status}</td>
      <td>{lastPulled}</td>
      <td>
        <ButtonGroup>
            <Button onClick={scrapeFunction}>
                Scrape
            </Button>
        </ButtonGroup>
      </td>
    </tr>)
  ;
};

export default EmailTableEntry;
