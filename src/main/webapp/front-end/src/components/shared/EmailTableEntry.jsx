import React from 'react';
import {
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const EmailTableEntry = ({ data, scrapeFunction, rescrapeFunction, match, leave }) => {

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

  var button = null

  if(status != "not started") {
    button =             <Button onClick={rescrapeFunction}>
                             Full Rescrape
                         </Button>
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
            {button}
        </ButtonGroup>
      </td>
    </tr>)
  ;
};

export default EmailTableEntry;
