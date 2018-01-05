import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { getTree, getTreeSuccess, getTreeError, getSharedTree, getSharedTreeSuccess, getSharedTreeError } from '../../actions.js';
import { Navbar, NavItem, Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { withRouter } from "react-router-dom";
import { Map, List, is } from 'immutable';
import {dispatchPattern} from '../../utilities.js';

//TODO: tree should go to specified root without HTTP request after getting FULL tree
//TODO: include 'shared' route

class Tree extends React.Component {

  constructor(props) {
    super(props);

    this.makeTree = this.makeTree.bind(this);
    this.getRoot = this.getRoot.bind(this);
    this.getKey = this.getKey.bind(this);
    this.treeFromRoot = this.treeFromRoot.bind(this);
    this.makeNodeUrl = this.makeNodeUrl.bind(this);
  }

  getRoot() {
    return this.props.match.params.root || null
  }

  getKey() {
    return this.props.match.params.key || null
  }

  pruneDeadEndFirstConnections(tree){
    var firstLevel = tree.children
    var pruned = firstLevel.filter(f => f.children.length > 0)

    tree.children = pruned

    return tree
  }

  treeFromRoot(tree, root) {

    if(!root){
        return tree;
    }

    var lookingFor;

    var treeFromRoot = this.treeFromRoot

    if(tree.get('name') == root) {
        return tree;
    } else {
        tree.get('children').forEach(function(e) {
            const result = treeFromRoot(e, root)
            if(result) {
                lookingFor = result
                return false
            }
        })
    }

    return lookingFor
  }

  makeNodeUrl(root) {
    if(this.getKey()) {
        return '/shared/'+this.getKey()+'/'+root
    } else {
        return '/tree/'+root
    }
  }

  makeTree() {
      var treeData = this.props.treeObj.get('tree', null)

      if(!treeData){
        console.log('no tree');
        return false;
      }

      treeData = treeData.get(0)
      treeData = this.treeFromRoot(treeData, this.getRoot())
      treeData = treeData.toJS();

      if(treeData.children.length > 100){
        treeData = this.pruneDeadEndFirstConnections(treeData);
      }


      var history = this.props.history;
      const makeNodeUrl = this.makeNodeUrl

      // ************** Generate the tree diagram	 *****************
      var margin = {top: 20, right: 120, bottom: 20, left: 120},
        width = 4000 - margin.right - margin.left,
        height = (treeData.children.length * 100) - margin.top - margin.bottom;

      var i = 0,
        duration = 750,
        root;

      var tree = d3.layout.tree()
        .size([height, width]);

      var diagonal = d3.svg.diagonal()
        .projection(function(d) { return [d.y, d.x]; });

      d3.select("svg").remove();

      var svg = d3.select("#tree-container").append("svg")
        .attr("width", width + margin.right + margin.left)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

      root = treeData;
      root.x0 = height / 2;
      root.y0 = 0;

      update(root);

      d3.select(self.frameElement).style("height", "500px");

      function update(source) {

        // Compute the new tree layout.
        var nodes = tree.nodes(root).reverse(),
          links = tree.links(nodes);

        // Normalize for fixed-depth.
        nodes.forEach(function(d) { d.y = d.depth * 180; });

        // Update the nodes…
        var node = svg.selectAll("g.node")
          .data(nodes, function(d) { return d.id || (d.id = ++i); });

        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("g")
          .attr("class", "node")
          .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
          .on("click", click);

        nodeEnter.append("circle")
          .attr("r", 1e-6)
          .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

        nodeEnter.append("text")
          .attr("x", function(d) { return d.children || d._children ? -13 : 13; })
          .attr("dy", ".35em")
          .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
          .text(function(d) { return d.name; })
          .style("fill-opacity", 1e-6);

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
          .duration(duration)
          .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

        nodeUpdate.select("circle")
          .attr("r", 10)
          .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

        nodeUpdate.select("text")
          .style("fill-opacity", 1);

        // Transition exiting nodes to the parent's new position.
        var nodeExit = node.exit().transition()
          .duration(duration)
          .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
          .remove();

        nodeExit.select("circle")
          .attr("r", 1e-6);

        nodeExit.select("text")
          .style("fill-opacity", 1e-6);

        // Update the links…
        var link = svg.selectAll("path.link")
          .data(links, function(d) { return d.target.id; });

        // Enter any new links at the parent's previous position.
        link.enter().insert("path", "g")
          .attr("class", "link")
          .attr("d", function(d) {
            var o = {x: source.x0, y: source.y0};
            return diagonal({source: o, target: o});
          });

        // Transition links to their new position.
        link.transition()
          .duration(duration)
          .attr("d", diagonal);

        // Transition exiting nodes to the parent's new position.
        link.exit().transition()
          .duration(duration)
          .attr("d", function(d) {
            var o = {x: source.x, y: source.y};
            return diagonal({source: o, target: o});
          })
          .remove();

        // Stash the old positions for transition.
        nodes.forEach(function(d) {
        d.x0 = d.x;
        d.y0 = d.y;
        });

      }

      function toggle(d) {
        if (d.children) {
          d._children = d.children;
          d.children = null;
        } else {
          d.children = d._children;
          d._children = null;
        }
      }

      // Toggle children on click.
      function click(d) {
        //if (d.children) {
        //d._children = d.children;
        //d.children = null;
        //} else {
        //d.children = d._children;
        //d._children = null;
        //}
        //update(d);

        history.push(makeNodeUrl(d.name));
      }

      function toggleAll(d) {
          if (d.children) {
            d.children.forEach(toggleAll);
            toggle(d);
          }
      }

      // Initialize the display to show a few nodes.
      //root.children.forEach(toggleAll);

      update(root);
  }

  componentDidMount() {
    if(this.getKey()){
        this.props.getSharedTree(this.getKey())
    } else {
        this.props.getTree();
    }
  }

  render() {
    this.makeTree();
    return <div id="tree-container"></div>
  }
}

const mapStateToProps = state => {
  return {
    treeObj: state.get('getTree'),
    session: state.getIn(['login', 'session'])
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {

    getTree: (root, emails) => {
      return dispatch(getTree(root, emails))
        .then(response => {
            if(response.error) {
                dispatch(getTreeError(response.payload.error));
                return false;
            }

            dispatch(getTreeSuccess(response.payload.data));
            return true;
        })
    },
    getSharedTree: dispatchPattern(getSharedTree, getSharedTreeSuccess, getSharedTreeError)
  };
};

const TreeContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(Tree);

export default withRouter(TreeContainer);
