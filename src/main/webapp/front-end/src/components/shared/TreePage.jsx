import React from 'react';
import {
  Grid,
  PageHeader,
  Button,
  Modal,
  ButtonToolbar,
  FormGroup,
  FormControl,
  ListGroup,
  ListGroupItem,
  Label
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {EmailTableContainer} from './EmailTable.jsx';
import TreeContainer from './Tree.jsx';
import NavBar from '../NavBar.jsx';
import { connect } from 'react-redux';
import { shareTree, shareTreeSuccess, shareTreeError, removeKeyFromState, searchNames, clearSearchNames, startNewLink, updateNewLinkLeft, updateNewLinkRight, createIdentityLink, createIdentityLinkSuccess, createIdentityLinkError } from '../../actions.js';
import {dispatchPattern} from '../../utilities.js';
import SearchBar from './SearchBar.jsx';


class TreePage extends React.Component {

  constructor(props) {
  	super(props);

  	this.getRoot = this.getRoot.bind(this);
  	this.handleChange = this.handleChange.bind(this);
  	this.focusFirstEmail = this.focusFirstEmail.bind(this);
  	this.focusSecondEmail = this.focusSecondEmail.bind(this);
  	this.updateNewLinkLeft = this.updateNewLinkLeft.bind(this);
  	this.updateNewLinkRight = this.updateNewLinkRight.bind(this);
  	this.setCreatingLink = this.setCreatingLink.bind(this);
  	this.submitLinkCreate = this.submitLinkCreate.bind(this);

  	this.state = {
  	    firstEmailFocus: true,
  	    secondEmailFocus: false,
  	    creatingLink: false
  	}
  }

  setCreatingLink(bool){
    this.setState({creatingLink: bool});
    this.props.clearSearchNames();
    this.props.updateNewLinkLeft(null);
    this.props.updateNewLinkRight(null);
  }

  focusFirstEmail() {
    this.props.clearSearchNames()
    this.setState({firstEmailFocus: true, secondEmailFocus: false})
  }

  focusSecondEmail() {
    this.props.clearSearchNames()
    this.setState({firstEmailFocus: false, secondEmailFocus: true})
  }

  getRoot() {
    return this.props.match.params.root || null
  }

  handleChange(e) {
    const query = e.target.value.toLowerCase()
    if(query.length > 2){
        this.props.searchNames(query)
    } else {
        this.props.clearSearchNames()
    }
  }

  updateNewLinkLeft(s) {
    this.props.clearSearchNames()
    this.props.updateNewLinkLeft(s)
  }

  updateNewLinkRight(s) {
    this.props.clearSearchNames()
    this.props.updateNewLinkRight(s)
  }

  submitLinkCreate() {
    const selectedLeftEmail = this.props.newLink.get('left')
    const selectedRightEmail = this.props.newLink.get('right')
    this.props.createIdentityLink(selectedLeftEmail, selectedRightEmail)
    this.setCreatingLink(false)
  }


  render() {

      const handleChange = this.handleChange

      var leftEmail = <SearchBar placeholder="One email..." results={this.props.searchResults} onChangeFunction={handleChange} showList={this.state.firstEmailFocus} onFocusFunction={this.focusFirstEmail} onSelectFunction={this.updateNewLinkLeft} />;
      var rightEmail = <SearchBar placeholder="Another email..." results={this.props.searchResults} onChangeFunction={handleChange} showList={this.state.secondEmailFocus} onFocusFunction={this.focusSecondEmail} onSelectFunction={this.updateNewLinkRight} />

      const selectedLeftEmail = this.props.newLink.get('left')
      const selectedRightEmail = this.props.newLink.get('right')

      if(selectedLeftEmail){
        leftEmail = <div><Label onClick={() => this.updateNewLinkLeft(null)}>{selectedLeftEmail}</Label></div>
      }

      if(selectedRightEmail) {
        rightEmail = <div><Label onClick={() => this.updateNewLinkRight(null)}>{selectedRightEmail}</Label></div>
      }

      var ableToSubmitLink = !(selectedLeftEmail && selectedRightEmail)

      return <Grid>
        <NavBar inverse={false}/>
        <div className='container'>
            <ButtonToolbar>
                <Button onClick={() => this.props.shareTree(this.getRoot())}>Share</Button>
                <Button onClick={() => this.setCreatingLink(true)} >Link Emails</Button>
            </ButtonToolbar>
            <TreeContainer />
        </div>

        <Modal className="static-modal" show={this.props.sharingKey.get('key') != null} >
                <Modal.Header>
                    <Modal.Title>Copy and paste this link to share:</Modal.Title>
                </Modal.Header>

                <Modal.Body>{window.location.href.split("#")[0]}#/shared/{this.props.sharingKey.get('key')}</Modal.Body>

                <Modal.Footer>
                    <Button bsStyle="primary" onClick={this.props.removeKeyFromState}>Done</Button>
                </Modal.Footer>
        </Modal>
        <Modal className="static-modal" show={this.state.creatingLink} >
                <Modal.Header>
                    <Modal.Title>Search for 2 emails to link them</Modal.Title>
                </Modal.Header>

                <Modal.Body>

                		<FormGroup>
                            {leftEmail}
                			<br />
                			{rightEmail}
                		</FormGroup>

                </Modal.Body>

                <Modal.Footer>
                    <Button onClick={() => this.setCreatingLink(false)} >Cancel</Button>
                    <Button bsStyle="primary" onClick={this.submitLinkCreate} disabled={ableToSubmitLink}>Link</Button>
                </Modal.Footer>
        </Modal>
      </Grid>;
  }
};

const mapStateToProps = state => {
  return {
    sharingKey: state.get('sharingKey'),
    searchResults: state.get('searchResults'),
    newLink: state.get('newLink')
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    shareTree: dispatchPattern(shareTree, shareTreeSuccess, shareTreeError),
    createIdentityLink: dispatchPattern(createIdentityLink, createIdentityLinkSuccess, createIdentityLinkError),
    removeKeyFromState: () => {
        return dispatch(removeKeyFromState())
    },
    searchNames: (query) => {
        return dispatch(searchNames(query))
    },
    clearSearchNames: () => {
        return dispatch(clearSearchNames())
    },
    startNewLink: () => {
        return dispatch(startNewLink())
    },
    updateNewLinkLeft: (left) => {
        return dispatch(updateNewLinkLeft(left))
    },
    updateNewLinkRight: (right) => {
        return dispatch(updateNewLinkRight(right))
    }
  }
}

const TreePageContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(TreePage)

export default TreePageContainer;
