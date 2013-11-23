/**
 * 
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
			generator.writeStringField("id", tagKey);
		}
		
		generator.writeEndArray();
	} // process
}