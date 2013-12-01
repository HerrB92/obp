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

// Margins, area width & height
var margin = {top: 10, right: 10, bottom: 20, left: 40},
    width = 320 - margin.left - margin.right,
    height = 700 - margin.top - margin.bottom;

// Define a new linear scale for the x axis
var x = d3.scale.linear()
    .range([0, width]);

// Define a new linear scale for the y axis
var y = d3.scale.linear()
    .range([height, 0]);

// Constructs an ordinal scale with ten categorical colors
// See https://github.com/mbostock/d3/wiki/Ordinal-Scales#wiki-category10
var color = d3.scale.category10();

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
// For the transform attrribute see:
// http://www.w3.org/TR/SVG/coords.html#TransformAttribute  
var svg = d3.select("#chart").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var replayDateDOMObject = d3.select('#date'),
    replayDate = new Date();

// Prepare arrays to store received reader and tag information
var movingTags = new Array(),
    fixedTags = new Array(),
    readers = new Array();
    
var visibleDots = null;
    
function processJSON(json) {

    var dot;
    var reader;
    var tag;
    var i;
    var changed = false;
    var dots = new Array();
    var tagX, tagY;

	if (json.time) {
		replayDate.setTime(parseInt(json.time)*1000);
		replayDateDOMObject.text(replayDate.toGMTString());
    }
    
    // Get through JSON reader objects and identify new
	// readers. As readers are not moving, no update
	// of existing readers will take place.
    for (i in json.reader) {
		reader = json.reader[i];

		if(!readers[reader.id]) {
			changed = true;

			dot = new Object;
			dot.id = 'R' + reader.id;
			dot.name = 'Reader ' + reader.id;
			dot.category = 'Reader';
			dot.x = reader.loc[0];
			dot.y = reader.loc[1];

			readers[reader.id] = dot;
			dots.push(dot);
		} else {
			dots.push(readers[reader.id]);
		}
    }
    
    // Get through JSON tag objects and identify tags.
    // If a tag has been found before, update position.
    // If a new tag has been found, add it.
    for (i in json.tag) {
		tag = json.tag[i];

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
			dot.id = 'T' + tag.id;
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
	
	if (dots.length) {
		/* var newVisibleDots = svg.selectAll(".dot")
			.data(newDots)
		   .enter().append("circle")
			.attr("class", "dot")
            .attr("r", 3.5)
            .attr("cx", function(d) { return x(d.x); })
            .attr("cy", function(d) { return y(d.y); })
            .style("fill", function(d) { return color(d.category); });
            
        newVisibleDots.append('svg:title')
			.text(function(d) { return d.name; }); */
                        
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
}

function initialize() {
	x.domain([0,650]).nice();
	y.domain([0,1500]).nice();
	
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
}

function refreshSVG() {
	// Get JSON data from the specified URL 
	d3.json('http://10.0.0.212/json/mytest.json', processJSON);

	// Get data and refresh view every 500ms
	window.setTimeout(refreshSVG, 500);
}

initialize();
refreshSVG();
