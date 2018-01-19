import React from 'react';
import { connect } from 'react-redux';
import {
  Table,
  Button,
  ButtonGroup
} from 'react-bootstrap';
import IntroductionsTableEntry from './IntroductionsTableEntry.jsx';
import {dispatchPattern} from '../../utilities.js';

class IntroductionsTable extends React.Component {
  buildContent() {
    const { introductions, loading, error } = this.props;
    if (error) {
      return <div>Error</div>;
    } else if (loading) {
      return <div>Loading</div>;
    } else if (!introductions) {
      return null;
    };

    return <div className='container'>
        <h3>{this.props.tableHeader}</h3>
				<Table className="task-tbl"  bordered condensed>
		      <thead>
		        <tr>
		          <th className="col-md-8">Introduced to (by)</th>
		          <th className="col-md-4">Date</th>
		        </tr>
		      </thead>
		      <tbody>
		        { introductions
              ? introductions.map(o =>
		            <IntroductionsTableEntry key={o.dateMillis} data={o} {...this.props} />)
		          : null
		        }
	      </tbody>
	    </Table>
      { introductions.length > 0 ? null : <div /> }
		</div>;
  }

  render() {
    return this.buildContent();
  }
}

const mapStateToProps = state => {
  return {
    introductions: state.getIn(['getIntroductions', 'introductions']).sortBy(a => a.get('dateMillis')).reverse().toJS(),
    loading: state.getIn(['getIntroductions', 'loading']),
    error: state.getIn(['getIntroductions', 'error'])
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {

  }
}

export const IntroductionsTableContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(IntroductionsTable)
