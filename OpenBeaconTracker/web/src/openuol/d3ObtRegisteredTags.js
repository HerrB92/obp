/***************************************************************
 *
 * Provides a frequently updated list of the registered tags.
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
 * Code is partly based on the Table With Embedded Line Chart example 
 * by llimllib (2013):
 * 
 * llimllib (2013) 'Table With Embedded Line Chart' bl.ocks.org [Online]
 * Available at: http://bl.ocks.org/llimllib/841dd138e429bb0545df
 * (Accessed: 23.11.2013)
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

// Reference to table body
var tbody;

var replayDateDiv = d3.select('#date')
var replayDate = new Date();
    
function processJSON(json) {
    var rows = new Array();
    var row;
    var i;
    
    // Set time
    replayDate.setTime(parseInt(json.time));
	replayDateDiv.text(replayDate.toGMTString());
    
    // Remove existing rows
    tbody.selectAll("tr").remove();
    
    for (i in json.tag) {
		row = new Object();
		row.id = json.tag[i];
		
		rows.push(row);
	}
	
	var tr = tbody.selectAll("tr")
		.data(rows)
		.enter().append("tr");

	var td = tr.selectAll("td")
        .data(function(row) { return [row.id]; })
		.enter().append("td")
        .text(function(cell) { return cell; });
} // processJSON

function initialize() {
	// Add table
	var table = d3.select("#datatable").append("table");
	
	// Add header
    var thead = table.append("thead");
	thead.append("th").text("Registered");
	
	// Add body
	tbody = table.append("tbody");
} // initialize

function refreshData() {	
	d3.json('http://' + window.location.hostname + '/json/obtregistered.json', processJSON);

	// Get data and refresh view every 500ms
	window.setTimeout(refreshData, 500);
} // refreshData

initialize();
refreshData();
