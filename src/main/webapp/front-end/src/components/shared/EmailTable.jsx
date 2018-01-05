import React from 'react';
import { connect } from 'react-redux';
import {
  Table,
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { getConnectedEmailAccounts, getConnectedEmailAccountsSuccess, getConnectedEmailAccountsError, startScraping, startScrapingSuccess, startScrapingError } from '../../actions.js';
import EmailTableEntry from './EmailTableEntry.jsx';
import {dispatchPattern} from '../../utilities.js';

class EmailTable extends React.Component {
  componentDidMount() {

    this.props.getEmails();

    setInterval(this.props.getEmails, 1000)
  }

  buildContent() {
    const { emails, loading, error } = this.props;
    if (error) {
      return <div>Error</div>;
    } else if (loading) {
      return <div>Loading</div>;
    } else if (!emails) {
      return null;
    };

    const startScraping = this.props.startScraping

    return <div className='container'>
        <h3>{this.props.tableHeader}</h3>
				<Table className="task-tbl" responsive striped hover>
		      <thead>
		        <tr>
		          <th>Email</th>
		          <th>Status</th>
                  <th>Last Pulled</th>
		          <th>Actions</th>
		        </tr>
		      </thead>
		      <tbody>
		        { emails
              ? emails.map(o =>
		            <EmailTableEntry key={o.email} data={o} scrapeFunction={() => startScraping(o.email)} {...this.props} />)
		          : null
		        }
	      </tbody>
	    </Table>
      { emails.length > 0 ? null : <div /> }
		</div>;
  }

  render() {
    return this.buildContent();
  }
}

//TODO: update for emails
const mapStateToProps = state => {
  return {
    emails: state.getIn(['getEmails', 'emails']).toJS(),
    loading: state.getIn(['getEmails', 'loading']),
    error: state.getIn(['getEmails', 'error'])
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    getEmails: dispatchPattern(getConnectedEmailAccounts, getConnectedEmailAccountsSuccess, getConnectedEmailAccountsError),
    startScraping: dispatchPattern(startScraping, startScrapingSuccess, startScrapingError)
  }
}

export const EmailTableContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(EmailTable)
