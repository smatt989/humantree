import React from 'react';
import {
  Grid,
  PageHeader,
  Button,
  Modal
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {EmailTableContainer} from './EmailTable.jsx';
import TreeContainer from './Tree.jsx';
import NavBar from '../NavBar.jsx';
import { connect } from 'react-redux';
import { shareTree, shareTreeSuccess, shareTreeError, removeKeyFromState } from '../../actions.js';
import {dispatchPattern} from '../../utilities.js';


class TreePage extends React.Component {

  constructor(props) {
  	super(props);

  	this.getRoot = this.getRoot.bind(this);
  }

  getRoot() {
    return this.props.match.params.root || null
  }



  render() {
      return <Grid>
        <NavBar inverse={false}/>
        <div className='container'>
            <Button onClick={() => this.props.shareTree(this.getRoot())}>Share</Button>
            <TreeContainer />
        </div>

        <Modal class="static-modal" show={this.props.sharingKey.get('key') != null} >
                <Modal.Header>
                    <Modal.Title>Copy and paste this link to share:</Modal.Title>
                </Modal.Header>

                <Modal.Body>{window.location.href.split("#")[0]}#/shared/{this.props.sharingKey.get('key')}</Modal.Body>

                <Modal.Footer>
                    <Button bsStyle="primary" onClick={this.props.removeKeyFromState}>Done</Button>
                </Modal.Footer>
        </Modal>
      </Grid>;
  }
};

const mapStateToProps = state => {
  return {
    sharingKey: state.get('sharingKey')
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    shareTree: dispatchPattern(shareTree, shareTreeSuccess, shareTreeError),
    removeKeyFromState: () => {
        return dispatch(removeKeyFromState())
    }
  }
}

const TreePageContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(TreePage)

export default TreePageContainer;
