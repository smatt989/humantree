import React from 'react';
import { connect } from 'react-redux';
import {
  Table,
  Button,
  ButtonGroup
} from 'react-bootstrap';
import CoolingConnectionsTableEntry from './CoolingConnectionsTableEntry.jsx';
import {dispatchPattern} from '../../utilities.js';

class CoolingConnectionsTable extends React.Component {

  buildContent() {
    const { connections, loading, error } = this.props;
    if (error) {
      return <div>Error</div>;
    } else if (loading) {
      return <div>Loading</div>;
    } else if (!connections) {
      return null;
    };

    return <div className='container'>
        <h3>{this.props.tableHeader}</h3>
				<Table className="task-tbl"  bordered condensed>
		      <thead>
		        <tr>
		          <th>Connection</th>
		          <th>Contacted</th>
		        </tr>
		      </thead>
		      <tbody>
		        { connections
              ? connections.map(o =>
		            <CoolingConnectionsTableEntry key={o.email} data={o} {...this.props} />)
		          : null
		        }
	      </tbody>
	    </Table>
      { connections.length > 0 ? null : <div /> }
		</div>;
  }

  render() {
    return this.buildContent();
  }
}

const mapStateToProps = state => {
  return {
    connections: state.getIn(['getCoolingConnections', 'connections']).toJS(),
    loading: state.getIn(['getCoolingConnections', 'loading']),
    error: state.getIn(['getCoolingConnections', 'error'])
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {

  }
}

export const CoolingConnectionsTableContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(CoolingConnectionsTable)
