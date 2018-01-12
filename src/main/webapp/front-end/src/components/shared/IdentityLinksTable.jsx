import React from 'react';
import { connect } from 'react-redux';
import {
  Table,
  Button,
  ButtonGroup
} from 'react-bootstrap';
import { getIdentityLinks, getIdentityLinksSuccess, getIdentityLinksError, deleteIdentityLink, deleteIdentityLinkSuccess, deleteIdentityLinkError } from '../../actions.js';
import IdentityLinksTableEntry from './IdentityLinksTableEntry.jsx';
import {dispatchPattern} from '../../utilities.js';

class IdentityLinksTable extends React.Component {
  componentDidMount() {

    this.props.getIdentityLinks();
  }

  buildContent() {
    const { links, loading, error } = this.props;
    if (error) {
      return <div>Error</div>;
    } else if (loading) {
      return <div>Loading</div>;
    } else if (!links) {
      return null;
    };

    return <div className='container'>
        <h3>{this.props.tableHeader}</h3>
				<Table className="task-tbl" responsive striped hover>
		      <thead>
		        <tr>
		          <th>One email</th>
		          <th>Other email</th>
		          <th>Actions</th>
		        </tr>
		      </thead>
		      <tbody>
		        { links
              ? links.map(o =>
		            <IdentityLinksTableEntry key={o.left+"-"+o.right} data={o} removeFunction={() => this.props.deleteIdentityLink(o.left, o.right)} {...this.props} />)
		          : null
		        }
	      </tbody>
	    </Table>
      { links.length > 0 ? null : <div /> }
		</div>;
  }

  render() {
    return this.buildContent();
  }
}

const mapStateToProps = state => {
  return {
    links: state.getIn(['getLinks', 'links']).toJS(),
    loading: state.getIn(['getLinks', 'loading']),
    error: state.getIn(['getLinks', 'error'])
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  const idLinksRequest = dispatchPattern(getIdentityLinks, getIdentityLinksSuccess, getIdentityLinksError)
  return {
    getIdentityLinks: idLinksRequest,
    deleteIdentityLink: dispatchPattern(deleteIdentityLink, deleteIdentityLinkSuccess, deleteIdentityLinkError, idLinksRequest)
  }
}

export const IdentityLinksTableContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(IdentityLinksTable)
