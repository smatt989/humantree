import React from 'react';
import { connect } from 'react-redux';
import {
  Table,
  Button,
  ButtonGroup
} from 'react-bootstrap';
import ConnectorsTableEntry from './ConnectorsTableEntry.jsx';
import {dispatchPattern} from '../../utilities.js';

class ConnectorsTable extends React.Component {
  buildContent() {
    const { connectors, loading, error } = this.props;
    if (error) {
      return <div>Error</div>;
    } else if (loading) {
      return <div>Loading</div>;
    } else if (!connectors) {
      return null;
    };

    return <div className='container'>
        <h3>{this.props.tableHeader}</h3>
				<Table className="task-tbl"  bordered condensed>
		      <thead>
		        <tr>
		          <th className="col-md-8">Connector</th>
		          <th className="col-md-4">Introductions</th>
		        </tr>
		      </thead>
		      <tbody>
		        { connectors
              ? connectors.map(o =>
		            <ConnectorsTableEntry key={o.name} data={o} since={this.props.since} {...this.props} />)
		          : null
		        }
	      </tbody>
	    </Table>
      { connectors.length > 0 ? null : <div /> }
		</div>;
  }

  render() {
    return this.buildContent();
  }
}

const mapStateToProps = state => {
  return {
    connectors: state.getIn(['getConnectors', 'connectors']).sortBy(a => a.get('introductions')).reverse().toJS(),
    loading: state.getIn(['getConnectors', 'loading']),
    error: state.getIn(['getConnectors', 'error'])
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {

  }
}

export const ConnectorsTableContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ConnectorsTable)
