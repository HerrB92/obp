/***************************************************************
 *
 * Example file to plot and refresh position data online
 *
 * The graphical representation is based on the d3.js API
 * by Michael Bostock.
 * 
 * Bostock, M. (2013) 'Data-Driven Documents' d3js.org [Online]
 * Available at: http://d3js.org/ (Accessed: 01.08.2013)
 * 
 * Bostock, M. (2013) 'API Reference' GitHub [Online]
 * Available at: https://github.com/mbostock/d3/wiki/API-Reference
 * (Accessed: 01.08.2013)
 * 
 * Code is partly based on the Scatterplot example by Michael
 * Bostock (2013):
 * 
 * Bostock, M. (2013) 'Scatterplot' bl.ocks.org [Online]
 * Available at: http://bl.ocks.org/mbostock/3887118
 * (Accessed: 30.08.2013)
 * 
 * Copyright 2013 Björn Behrens <uol@btech.de>
 *
 ***************************************************************/

/*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published
 by the Free Software Foundation; version 3.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

/* // Margins, area width & height
var margin = {top: 10, right: 10, bottom: 20, left: 40},
    width = 320 - margin.left - margin.right,
    height = 700 - margin.top - margin.bottom;

// Define a new linear scale for the x axis
var x = d3.scale.linear()
    .range([0, width]);

// Define a new linear scale for the y axis
var y = d3.scale.linear()
    .range([height, 0]);

// Create an x axis (bottom)
var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

// Create an y axis (left)
var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left");

// Create an SVG object within the page body based on the
// given width & height values.
// For the transform attribute see:
// http://www.w3.org/TR/SVG/coords.html#TransformAttribute  
var svg = d3.select("#chart").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")"); */

// Plot area object
var svg;

// List if legend colors
var color;

// Plot area width
var width;

// x and y axis objects
var x, y;

// Data area
var inactiveReaderTableBody;
var inactiveSpotTableBody;
var inactiveTagTableBody;

var replayDateDOMObject = d3.select('#date'),
    replayDate = new Date();

// Prepare arrays to store received reader and tag information
var movingTags = new Array(),
    spotTags = new Array(),
    readers = new Array();
    
function processJSON(json) {
    var dot;
    var reader;
    var spotTag;
    var tag;
    var i;
    var changed = false;
    var dots = new Array();
    var tagX, tagY;
    var inactiveReaders = new Array();
    var inactiveSpots = new Array();
    var inactiveTags = new Array();
    var element;
    var tr, td;

	if (json.time) {
		replayDate.setTime(parseInt(json.time));
		replayDateDOMObject.text(replayDate.toGMTString());
    }
    
    // Get through JSON reader objects and identify new
	// readers. As readers are not moving, no update
	// of existing readers will take place.
    for (i in json.reader) {
		reader = json.reader[i];

		if (reader.lastseen == null || reader.lastseen == '0000-00-00 00:00:00') {
			element = new Object();
			element.id = reader.id;
		
			inactiveReaders.push(element);
		} else {
			if(!readers[reader.id]) {
				changed = true;

				dot = new Object;
				dot.id = reader.id;
				dot.name = 'Reader ' + reader.name + ' (' + reader.id + ')';
				dot.category = 'Reader';
				dot.x = reader.loc[0];
				dot.y = reader.loc[1];

				readers[reader.id] = dot;
				dots.push(dot);
			} else {
				dots.push(readers[reader.id]);
			}
		}
    }
    
    // Update inactive reader table
    // Remove existing rows
    inactiveReaderTableBody.selectAll("tr").remove();
    
    // Add elements
	tr = inactiveReaderTableBody.selectAll("tr")
		.data(inactiveReaders)
		.enter().append("tr");

	td = tr.selectAll("td")
        .data(function(element) { return [element.id]; })
		.enter().append("td")
        .text(function(cell) { return cell; });
    
    // Get through JSON spot tag objects and identify new
	// spot tags. As spot tags are not moving, no update
	// of existing spot tag positions will take place.
    for (i in json.spots) {
		spotTag = json.spots[i];
		
		if (spotTag.lastseen == null || spotTag.lastseen == '0000-00-00 00:00:00') {
			element = new Object();
			element.id = spotTag.id;
		
			inactiveSpots.push(element);
		} else {
			if(!spotTags[spotTag.id]) {
				changed = true;

				dot = new Object;
				dot.id = spotTag.id;
				
				if (spotTag.type == 'RegisterTag') {
					dot.name = 'Register ' + spotTag.name + ' (' + spotTag.id + ')';
					dot.category = 'RegisterTag';
				} else if (spotTag.type == 'UnRegisterTag') {
					dot.name = 'UnRegister ' + spotTag.name + ' (' + spotTag.id + ')';
					dot.category = 'UnRegisterTag';
				} else {
					dot.name = 'Spot ' + spotTag.name + ' (' + spotTag.id + ')';
					dot.category = 'SpotTag';
				}
				
				dot.x = spotTag.loc[0];
				dot.y = spotTag.loc[1];

				spotTags[spotTag.id] = dot;
				dots.push(dot);
			} else {
				dots.push(spotTags[spotTag.id]);
			}
		}
    }
    
    // Update inactive spot table
    // Remove existing rows
    inactiveSpotTableBody.selectAll("tr").remove();
    
    // Add elements
	tr = inactiveSpotTableBody.selectAll("tr")
		.data(inactiveSpots)
		.enter().append("tr");

	td = tr.selectAll("td")
        .data(function(element) { return [element.id]; })
		.enter().append("td")
        .text(function(cell) { return cell; });
    
    // Get through JSON tag objects and identify tags.
    // If a tag has been found before, update position.
    // If a new tag has been found, add it.
    for (i in json.tag) {
		tag = json.tag[i];
		
		if (tag.lastseen == null || tag.lastseen == '0000-00-00 00:00:00') {
			element = new Object();
			element.id = tag.id;
		
			inactiveTags.push(element);
		} else {
			if (!tag.loc) {
				tagX = 1;
				tagY = 1;
			} else {
				tagX = tag.loc[0];
				tagY = tag.loc[1];
			}
			
			if (movingTags[tag.id]) {
				dot = movingTags[tag.id];
				
				if (dot.x != tagX || dot.y != tagY) {
					changed = true;
					
					dot.x = tagX;
					dot.y = tagY;
				}
			} else {
				changed = true;

				dot = new Object;
				dot.id = tag.id;
				dot.name = 'Tag ' + tag.id;
				dot.x = tagX;
				dot.y = tagY;

				movingTags[tag.id] = dot;
			}
			
			if (tag.button) {
				dot.category = 'Moving Tag (Button)';
			} else {
				dot.category = 'Moving Tag';
			}
				
			dots.push(dot);
		}
	}
	
	// Update inactive tags table
    // Remove existing rows
    inactiveTagTableBody.selectAll("tr").remove();
    
    // Add elements
	tr = inactiveTagTableBody.selectAll("tr")
		.data(inactiveTags)
		.enter().append("tr");

	td = tr.selectAll("td")
        .data(function(element) { return [element.id]; })
		.enter().append("td")
        .text(function(cell) { return cell; });
	
	if (dots.length) {  
		var legend = svg.selectAll(".legend")
            .data(color.domain())
		   .enter().append("g")
			.attr("class", "legend")
			.attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });
			
		legend.append("rect")
			.attr("x", width - 18)
			.attr("width", 18)
			.attr("height", 18)
			.style("fill", color);

		legend.append("text")
			.attr("x", width - 24)
			.attr("y", 9)
			.attr("dy", ".35em")
			.style("text-anchor", "end")
			.text(function(d) { return d; });
    }
    
    //if (changed) {
    
		svg.selectAll(".dot")
			.data(dots)
		   .enter().append("circle")
			.attr("class", "dot")
            .attr("r", 3.5)
            .attr("cx", function(d) { return x(d.x); })
            .attr("cy", function(d) { return y(d.y); })
            .style("fill", function(d) { return color(d.category); })
           .append('svg:title')
			.text(function(d) { return d.name; });
    
		svg.selectAll(".dot")
			.data(dots)
			.transition()
            .duration(10)
            .attr("cx", function(d) { return x(d.x); })
            .attr("cy", function(d) { return y(d.y); })
            .style("fill", function(d) { return color(d.category); });
            
        // Remove old
		svg.selectAll(".dot")
			.data(dots)
            .exit()
            .remove();
            
       	//window.alert("Change");
		//svg.start();
	//}
} // processJSON

function initialize(json) {
	// Plot area
	var maxX = 320;
	var maxY = 700;
	
	if (json.maxX) {
		maxX = parseInt(json.maxX);
	}
	
	if (json.maxY) {
		maxY = parseInt(json.maxY);
	}
	
	// Determine visible area size
	var docWidth = window.innerWidth - 20;
	var docHeight = window.innerHeight - 20;
	
	var virtualWidth = maxX;
	var virtualHeight = maxY;
	
	if (maxX > docWidth && maxY < docHeight) {
		// maxX to wide to fit, reduce width
		
		virtualWidth = docWidth;
		virtualHeight = maxY * docWidth/maxX;
	} else if (maxX < docWidth && maxY > docHeight) {
		// maxY to high to fit, reduce height
		
		virtualWidth = maxX * docHeight/maxY;
		virtualHeight = docHeight;
	} else if (maxX > docWidth && maxY > docHeight) {
		// Both dimensions are too great to show,
		// use client width and height relative to
		// maxX/maxY ratio
		
		if (maxX > maxY) {
			virtualWidth = docWidth * maxY/maxX;
			virtualHeight = docHeight * maxY/maxX;
		} else {
			virtualWidth = docWidth * maxX/maxY;
			virtualHeight = docHeight * maxX/maxY;
		}
	}
		
	// Margins, area width & height
	var margin = {top: 10, right: 10, bottom: 20, left: 40};
	width  = virtualWidth - margin.left - margin.right;
	var height = virtualHeight - margin.top - margin.bottom;

	// Define a new linear scale for the x axis
	x = d3.scale.linear().range([0, width]);

	// Define a new linear scale for the y axis
	y = d3.scale.linear().range([height, 0]);

	// Constructs an ordinal scale with ten categorical colors
	// See https://github.com/mbostock/d3/wiki/Ordinal-Scales#wiki-category10
	color = d3.scale.category10();

	// Create an x axis (bottom)
	var xAxis = d3.svg.axis().scale(x).orient("bottom");

	// Create an y axis (left)
	var yAxis = d3.svg.axis().scale(y).orient("left");
	
	// Create an SVG object within the page body based on the
	// given width & height values.
	// For the transform attribute see:
	// http://www.w3.org/TR/SVG/coords.html#TransformAttribute  
	svg = d3.select("#chart").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	x.domain([0, maxX + 200]).nice();
	y.domain([0, maxY + 200]).nice();
	
	svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
    .append("text")
      .attr("class", "label")
      .attr("x", width)
      .attr("y", -6)
      .style("text-anchor", "end")
      .text("Position (x)");

	svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)
    .append("text")
      .attr("class", "label")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Position (y)");
      
    // Data area
    // Add table for inactive readers
	var inactiveReaderTable = d3.select("#inactiveReaders").append("table");
	
	// Add header
    inactiveReaderTable.append("thead").append("th").text("Inactive Reader");
	
	// Add body
	inactiveReaderTableBody = inactiveReaderTable.append("tbody");
	
	// Add table for inactive spots
	var inactiveSpotTable = d3.select("#inactiveSpots").append("table");
    inactiveSpotTable.append("thead").append("th").text("Inactive Spots");
	inactiveSpotTableBody = inactiveSpotTable.append("tbody");
	
	// Add table for inactive tags
	var inactiveTagTable = d3.select("#inactiveTags").append("table");
    inactiveTagTable.append("thead").append("th").text("Inactive Tags");
	inactiveTagTableBody = inactiveTagTable.append("tbody");
} // initialize

function refreshSVG() {
	// Get JSON data from the specified URL
	d3.json('http://' + window.location.hostname + '/json/obtracker.json', processJSON);

	// Get data and refresh view every 500ms
	window.setTimeout(refreshSVG, 500);
}

// Get the JSON file once to read the maximum coordinates
d3.json('http://' + window.location.hostname + '/json/obtracker.json', initialize);
refreshSVG();
