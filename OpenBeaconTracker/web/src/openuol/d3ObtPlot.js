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
 * getScreenCoordinates is based on
 * t.888 (2013) Answer to 'Getting Screen Positions of D3 Nodes After Transform'
 * stackoverflow.com, 02.09.2013
 * Available at: http://stackoverflow.com/a/18561829 (Accessed: 08.12.2013)
 * 
 * Copyright 2013 Björn Behrens <uol@btech.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 ***************************************************************/

// Plot area object
var svg;

// List if legend colors
var color;

// Width of plot area
var width;

// x and y axis objects
var x, y;

var ctm;

// Data area
var inactiveReaderTableBody;
var inactiveSpotTableBody;
var movingTagTableBody;

// Get the div element showing the data date and time of the processed data
var replayDateDOMObject = d3.select('#date'),
    replayDate = new Date();

// Prepare arrays to store SVG dot references for moving and spot tags and
// readers
var movingTagDots = new Array(),
    spotTagDots = new Array(),
    readerDots = new Array();

// Prepare list of tags which path is also shown
var trackTags = new Array();

// Sort function used to sort readers, other spots and tags by id
function sortElements(element1, element2) {
	if (element1.id > element2.id) {
		return 1;
	} else if (element1.id == element2.id) {
		return 0;
	}
	
	return -1;
} // sortElements

// Activate showing track for the specified tag id
function showTrack(id) {
	if (trackTags == null || trackTags.indexOf(id) == -1) {
		trackTags.push(id);
	}
} // showTrack

// Calculate screen positions from data coordinates
function getScreenCoordinates(givenX, givenY) {
	var screenX = x(givenX);
	var screenY = y(givenY);
		
	return { x: screenX, y: screenY };    
} // getScreenCoordinates

// Main method to process the loaded JSON file containing
// the reader, other spots and tags data
function processJSON(json) {
    var dot;
    var reader;
    var spotTag;
    var tag;
    var i;
    var dots = new Array();
    var tagX, tagY;
    var inactiveReaders = new Array();
    var inactiveSpots = new Array();
    var movingTags = new Array();
    var element;
    var tr;
    var startPos;
    var endPos;

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
			if(!readerDots[reader.id]) {
				dot = new Object;
				dot.id = reader.id;
				dot.name = 'Reader ' + reader.name + ' (' + reader.id + ')';
				dot.category = 'Reader';
				dot.x = reader.loc[0];
				dot.y = reader.loc[1];

				readerDots[reader.id] = dot;
				dots.push(dot);
			} else {
				dots.push(readerDots[reader.id]);
			}
		}
    }
    
    // Update inactive reader table
    // Sort elements
    inactiveReaders.sort(sortElements);
       
    // Add/Update elements
	inactiveReaderTableBody.selectAll("tr")
		 .data(inactiveReaders)
		.enter().append("tr").selectAll("td")
         .data(function(element) { return [element.id]; })
		.enter().append("td")
         .text(function(cell) { return cell; });
	
	// Remove old elements
	inactiveReaderTableBody.selectAll("tr")
	     .data(inactiveReaders)
        .exit()
         .remove();
    
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
			if(!spotTagDots[spotTag.id]) {
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

				spotTagDots[spotTag.id] = dot;
				dots.push(dot);
			} else {
				dots.push(spotTagDots[spotTag.id]);
			}
		}
    }
    
    // Update inactive spot table
    // Sort elements
    inactiveSpots.sort(sortElements);
       
    // Add/Update elements
	inactiveSpotTableBody.selectAll("tr")
		 .data(inactiveSpots)
		.enter().append("tr").selectAll("td")
         .data(function(element) { return [element.id]; })
		.enter().append("td")
         .text(function(cell) { return cell; });
	
	// Remove old elements
	inactiveSpotTableBody.selectAll("tr")
	     .data(inactiveSpots)
        .exit()
         .remove();
    
    // Get through JSON tag objects and identify tags.
    // If a tag has been found before, update position.
    // If a new tag has been found, add it.
    for (i in json.tag) {
		tag = json.tag[i];
		
		element = new Object();
		element.id = tag.id;
		
		if (!tag.registered) {
			element.color = "black";
		} else if (tag.lastseen == null || tag.lastseen == '0000-00-00 00:00:00') {
			element.color = "red";
		} else {
			element.color = "green";
			
			if (!tag.loc) {
				tagX = 1;
				tagY = 1;
			} else {
				tagX = tag.loc[0];
				tagY = tag.loc[1];
			}
			
			if (movingTagDots[tag.id]) {
				dot = movingTagDots[tag.id];
				
				if (dot.x != tagX || dot.y != tagY) {
					if (trackTags != null && trackTags.indexOf(tag.id) > -1) {
						startPos = getScreenCoordinates(dot.x, dot.y);
						endPos = getScreenCoordinates(tagX, tagY);
						
						svg.append("svg:line")
						    .attr("x1", startPos.x)
						    .attr("y1", startPos.y)
						    .attr("x2", endPos.x)
						    .attr("y2", endPos.y)
						    .style("stroke", "rgb(6,120,155)");
					}
					
					dot.x = tagX;
					dot.y = tagY;
				}
			} else {
				dot = new Object;
				dot.id = tag.id;
				dot.name = 'Tag ' + tag.id;
				dot.x = tagX;
				dot.y = tagY;

				movingTagDots[tag.id] = dot;
			}
			
			if (tag.button) {
				dot.category = 'Moving Tag (Button)';
			} else {
				dot.category = 'Moving Tag';
			}
				
			dots.push(dot);
		}
		
		movingTags.push(element);
	}
	
	// Update moving tags table
    // Sort elements
    movingTags.sort(sortElements);
    
    // Remove existing rows
    //movingTagTableBody.selectAll("tr").remove();
    
    // Add/Update elements
	movingTagTableBody.selectAll("tr")
		 .data(movingTags)
		.enter().append("tr").append("td")
		 .html(function(element) { return "<span onclick=\"showTrack('" + element.id + "');\">" + element.id + "</span>"; })
         //.text(function(element) { return element.id; })
         .style("color", function(element) { return element.color; });
	
	// Remove old elements
	movingTagTableBody.selectAll("tr")
	     .data(movingTags)
        .exit()
         .remove();
	
	// Create/Update legend
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
    
	// Main magic: Add or update all dots provided in
	// the dots array.
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

	// If a dot has changed, change dot from old to new
	// position by a transition
	svg.selectAll(".dot")
		.data(dots)
	   .transition()
        .duration(10)
        .attr("cx", function(d) { return x(d.x); })
        .attr("cy", function(d) { return y(d.y); })
        .style("fill", function(d) { return color(d.category); });
        
    // Remove old dots: Remove all dots, which are part of the 
	// plot area but not of the dots array anymore
	svg.selectAll(".dot")
		.data(dots)
       .exit()
        .remove();
} // processJSON

// Initialize plot area and required objects (e.g. tables)
function initialize(json) {
	// Calculate available plot area
	
	// Default values
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
	//
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
	
	// Add table for moving tags
	var movingTagTable = d3.select("#movingTags").append("table");
    movingTagTable.append("thead").append("th").text("Moving Tags");
	movingTagTableBody = movingTagTable.append("tbody");
	
	refreshSVG();
} // initialize

// Get JSON data from specified URL and process the data
function refreshSVG() {
	// Get JSON data from the specified URL
	d3.json('http://' + window.location.hostname + '/json/obtracker.json', processJSON);

	// Get data and refresh view every 500ms
	window.setTimeout(refreshSVG, 500);
} // refreshJSON

// Get the JSON file once to read the maximum coordinates
// and call initialize function
d3.json('http://' + window.location.hostname + '/json/obtracker.json', initialize);
