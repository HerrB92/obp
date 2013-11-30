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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author bbehrens
 *
 */
public abstract class OutputJSON {
	private final JsonFactory factory = new JsonFactory();
	private File file;
	private long sequence = 0;
	
	public OutputJSON(String fileName) {
		setFile(fileName);
	} // Constructor
	
	protected File getFile() {
		return file;
	} // getFile
	
	protected void setFile(String fileName) {
		file = new File(fileName);
	} // setFile
	
	protected JsonFactory getFactory() {
		return factory;
	} // getFactory
	
	protected long incrementSequence() {
		return sequence++;
	} // incrementSequence
	
	protected long getSequence() {
		return sequence;
	} // getSequence

	public void update() 
			throws JsonGenerationException {
		
		JsonGenerator generator;
		FileWriter fileWriter = null;
		long start = DateTime.now().getMillis();
		try {
			fileWriter = new FileWriter(file, false);
			
			try {
				generator = factory.createGenerator(fileWriter); // , JsonEncoding.UTF8?
				
				// Activate embedded Pretty Printer class to
				// get human-friendly JSON
				generator.useDefaultPrettyPrinter();
				
				generator.writeStartObject(); // Global
				
				generator.writeNumberField("id", incrementSequence());
		
				generator.writeObjectFieldStart("api");
				generator.writeStringField("name", "OBT");
				generator.writeStringField("ver", "1.0");
				generator.writeEndObject();
				
				generator.writeNumberField("time", DateTime.now().getMillis());
				
				process(generator);
				
				// TODO: This might not be a suitable performance counter
				// as the Java optimizer might decide to execute the code
				// not as written here.
				generator.writeNumberField("buildInMilliseconds", DateTime.now().getMillis() - start);
				
				generator.writeEndObject(); // Global
				generator.close();
			} finally {
				fileWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // update
	
	protected abstract void process(JsonGenerator generator)
		throws JsonGenerationException, IOException;
}