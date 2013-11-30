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
 * @author bbehrens
 *
 */
public class OutputJSONRegisteredTagKeys extends OutputJSON {
	private final DataIndex index = DataIndex.getInstance();
	
	public OutputJSONRegisteredTagKeys(String fileName) {
		super(fileName);
	} // Constructor
	
	@Override
	protected void process(JsonGenerator generator) 
			throws JsonGenerationException, IOException {
		generator.writeArrayFieldStart("tag");
		
		for (String tagKey: index.getRegisteredTagKeys()) {
			generator.writeString(tagKey);
		}
		
		generator.writeEndArray();
	} // process
}