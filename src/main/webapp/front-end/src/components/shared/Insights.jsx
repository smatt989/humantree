import React from 'react';
import { connect } from 'react-redux';
import {
  Grid,
  PageHeader,
  Button
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {IntroductionsTableContainer} from './IntroductionsTable.jsx';
import {ConnectorsTableContainer} from './ConnectorsTable.jsx';
import DatePicker from 'react-datepicker';
import NavBar from '../NavBar.jsx';
import { getIntroductions, getIntroductionsSuccess, getIntroductionsError, getConnectors, getConnectorsSuccess, getConnectorsError } from '../../actions.js';
import {dispatchPattern} from '../../utilities.js';

class Insights extends React.Component {

  constructor(props) {
  	super(props);

    this.handleChangeDate = this.handleChangeDate.bind(this);
    this.update = this.update.bind(this);

  	this.state = {
  	    since: moment().subtract(14, 'days')
  	}
  }

  componentDidMount() {
    this.update()
  }

  componentDidUpdate() {
    this.update()
  }

  update() {
     const since = this.state.since.valueOf()

     this.props.fetchIntroductions(since);
     this.props.fetchConnectors(since);
  }

  handleChangeDate(a){
    this.setState({since: a})
  }

  buildContent() {

    return <Grid>
               <NavBar inverse={false}/>
               <div className='container'>
                 <PageHeader>
                   Insights as of
                   <DatePicker selected={this.state.since} onChange={this.handleChangeDate} />
                 </PageHeader>
                 <div className="col-md-6" >
                    <h3>Introductions</h3>
                    <IntroductionsTableContainer since={this.state.since.valueOf()} />
                 </div>
                 <div className="col-md-6" >
                    <h3>Top Connectors</h3>
                    <ConnectorsTableContainer since={this.state.since.valueOf()} />
                 </div>
               </div>
             </Grid>;
  }

  render() {
    return this.buildContent();
  }
}

const mapStateToProps = state => {
  return {

  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    fetchIntroductions: dispatchPattern(getIntroductions, getIntroductionsSuccess, getIntroductionsError),
    fetchConnectors: dispatchPattern(getConnectors, getConnectorsSuccess, getConnectorsError)
  }
}

export const InsightsContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(Insights)