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
public class OutputJSONUnRegisteredTagKeys extends OutputJSON {
	private final DataIndex index = DataIndex.getInstance();
	
	public OutputJSONUnRegisteredTagKeys(String fileName) {
		super(fileName);
	} // Constructor
	
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