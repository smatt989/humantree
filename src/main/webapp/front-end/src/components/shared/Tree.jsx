import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { createAnnotation, createAnnotationSuccess, createAnnotationError, deleteAnnotation, deleteAnnotationSuccess, deleteAnnotationError } from '../../actions.js';
import { Navbar, NavItem, Nav, Button } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { withRouter } from "react-router-dom";
import { Map, List, is } from 'immutable';
import {dispatchPattern} from '../../utilities.js';
import {HIDDEN} from '../../constants/annotations.js';

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


      const editingHidden = this.props.editingHidden

      const createAnnotation = this.props.createAnnotation
      const deleteAnnotation = this.props.deleteAnnotation

      const annotationMap = this.props.annotations

      function actionFromHiddenState(hidden) {
        if(hidden){
            return (name) => createAnnotation(name, HIDDEN)
        } else {
            return (name) => deleteAnnotation(name, HIDDEN)
        }
      }

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
        .attr("transform", "translate(" + (margin.left + 100) + "," + margin.top + ")");

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


        //MANUALLY KEEPING TRACK OF HIDDEN -- not great
        nodes.forEach(function(d){
            const label = annotationMap.get(d.name, null)
            if(d.hidden == null && label){
                d.hidden = true;
            }
        })

        if(!editingHidden){
            nodes.forEach(function(d){
                if(annotationMap.get(d.name, null)){
                    const indexOfSelf = d.parent.children.map(c => c.name).indexOf(d.name);
                    d.parent.children.splice(indexOfSelf, 1)
                }
            })
        }

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
          .style("fill", function(d){
            const color = d._children ? "lightsteelblue" : "#fff"
            if(editingHidden){
                return d.hidden ? "#333" : color
            } else {
                return color
            }
          })

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
          .style("fill", function(d){
            const color = d._children ? "lightsteelblue" : "#fff"
            if(editingHidden){
                return d.hidden ? "#333" : color
            } else {
                return color
            }
          })

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
        if(editingHidden){
            d.hidden = d.hidden ? !d.hidden : true;
            actionFromHiddenState(d.hidden)(d.name)
            update(d)
        } else {
            history.push(makeNodeUrl(d.name));
        }
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

  render() {
    this.makeTree();

    var loadingDiv = null
    if(!this.props.treeObj.get('tree')) {
        loadingDiv = <div><h2>loading...</h2></div>
    }
    if(this.props.treeObj.get('error')){
        loadingDiv = <div><h2>error</h2></div>
    }

    return <div id="tree-container">{loadingDiv}</div>
  }
}

const mapStateToProps = state => {
  return {
    treeObj: state.get('getTree'),
    annotations: state.getIn(['getAnnotationsMap', 'annotations'])
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    createAnnotation: dispatchPattern(createAnnotation, createAnnotationSuccess, createAnnotationError),
    deleteAnnotation: dispatchPattern(deleteAnnotation, deleteAnnotationSuccess, deleteAnnotationError)
  };
};

const TreeContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(Tree);

export default withRouter(TreeContainer);
