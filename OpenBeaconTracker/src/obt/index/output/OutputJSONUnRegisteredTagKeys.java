/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package obt.index.output;

import java.io.IOException;

import obt.index.DataIndex;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * JSON writer class used to provide a list of the tag keys of the last 
 * n registered tags as JSON file. n is determined by the cardinality
 * of the CircularQueue object to store the registered tags.
 * 
 * @author Björn Behrens (uol@btech.de)
 * @version 1.0
 */
public class OutputJSONUnRegisteredTagKeys extends OutputJSON {
	private final DataIndex index = DataIndex.getInstance();
	
	/**
	 * Constructor receiving the JSON file name (including path)
	 * 
	 * @param fileName
	 */
	public OutputJSONUnRegisteredTagKeys(String fileName) {
		super(fileName);
	} // Constructor
	
	/**
	 * Overwritten process method to add the tag keys of the 
	 * registered tags to the JSON output.
	 * 
	 * @see obt.index.output.OutputJSON#process(com.fasterxml.jackson.core.JsonGenerator)
	 */
	@Override
	protected void process(JsonGenerator generator) 
			throws JsonGenerationException, IOException {
		generator.writeArrayFieldStart("tag");
		
		for (String tagKey: index.getUnRegisteredTagKeys()) {
			generator.writeString(tagKey);
		}
		
		generator.writeEndArray();
	} // process
}