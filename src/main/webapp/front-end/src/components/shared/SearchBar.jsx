import React from 'react';
import {
  Button,
  ButtonGroup,
  FormControl,
  ListGroup,
  ListGroupItem
} from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { Redirect, Link } from 'react-router-dom';

const SearchBar = ({ placeholder, results, onChangeFunction, showList, onFocusFunction, onSelectFunction }) => {

  var resultDropdownDisplay = "invisible"

  if(results.size > 0 && showList) {
        resultDropdownDisplay = "visible"
  }

  return (
  <div className="search-bar-container">
        <FormControl onChange={onChangeFunction} type="text" placeholder={placeholder} onFocus={onFocusFunction}/>
        <ListGroup className={"dropdown-results "+resultDropdownDisplay}>
            {results.map(r => <ListGroupItem key={r} onClick={() => onSelectFunction(r)}>{r}</ListGroupItem>)}
        </ListGroup>
    </div>)
  ;
};

export default SearchBar;
