/**
 * 
 */
package obp.index.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import obp.index.DataIndex;
import obp.spots.Reader;
import obp.tag.Tag;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author bbehrens
 *
 */
public class JSONOutputService { //implements Closeable {
	private final JsonFactory factory = new JsonFactory();
	private File file;
	private long sequence = 0;
	
	public JSONOutputService(String fileName) {
		file = new File(fileName);
	} // Constructor
	
	/**
	 * Updates the content of the given file (including path) with the
	 * JSON encoded data of the service, tags, readers and proximity
	 * information.
	 * 
	 * Information: First service run requires about 35 milliseconds, 
	 * further updates between 1 and 5 milliseconds (for 1 tag, 4 reader).
	 * 
	 * @param index
	 * @throws JsonGenerationException
	 */
	public void update(DataIndex index) 
			throws JsonGenerationException {
		
		JsonGenerator generator;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file, false);
			
			try {
				generator = factory.createGenerator(fileWriter); // // , JsonEncoding.UTF8?
				
				// Activate embedded Pretty Printer class to
				// get human-friendly JSON
				generator.useDefaultPrettyPrinter();
				
				generator.writeStartObject();
				generator.writeNumberField("id", sequence++);
		
				generator.writeObjectFieldStart("api");
				generator.writeStringField("name", "OBP");
				generator.writeStringField("ver", "1.0");
				generator.writeEndObject();
				
				generator.writeNumberField("time", DateTime.now().getMillis());
				
				generator.writeObjectFieldStart("packets");
				generator.writeNumberField("rate", 0); // FIXME
				generator.writeNumberField("ignored", 0); // FIXME
				generator.writeNumberField("invalid_protocol", 0); // FIXME
				generator.writeNumberField("unknown_reader", 0); // FIXME
				generator.writeNumberField("crc_error", 0); // FIXME
				generator.writeNumberField("crc_ok", 0); // FIXME
				generator.writeEndObject();
				
				generator.writeArrayFieldStart("tag");
				
				for (Tag tag: index.getTags()) {
					generator.writeStartObject();
					generator.writeNumberField("id", tag.getId());
					generator.writeArrayFieldStart("loc");
					generator.writeNumber(tag.getX());
					generator.writeNumber(tag.getY());
					generator.writeEndArray();
					
					// For OpenBeaconPackage, this is currently always 0.
					// The original C++ implementation provides the feature 
					// to define different keys for decryption to support
					// different OpenBeacon keys used on different events 
					// (e.g. CCC 2010).
					generator.writeNumberField("key", 0);
					
					generator.writeNumberField("reader", tag.getLastReaderId());
					generator.writeBooleanField("button", tag.isButtonPressed());
					generator.writeEndObject();
				}
				
				generator.writeEndArray();
				
				generator.writeArrayFieldStart("reader");
				
				for (Reader reader: index.getKnownReaders()) {
					generator.writeStartObject();
					generator.writeNumberField("id", reader.getId());
					generator.writeArrayFieldStart("loc");
					generator.writeNumber(reader.getX());
					generator.writeNumber(reader.getY());
					generator.writeEndArray();
					generator.writeNumberField("room", reader.getRoom());
					generator.writeNumberField("floor", reader.getFloor());
					generator.writeNumberField("group", reader.getGroup());
					generator.writeEndObject();
				}
				
				generator.writeEndArray();
				
				generator.writeArrayFieldStart("edge");
				
		//		for (Proximity proximity: index.getProximities()) {
		//			generator.writeStartObject();
		//			generator.writeArrayFieldStart("tag");
		//			generator.writeNumber(tagId1);
		//			generator.writeNumber(tagId2);
		//			generator.writeEndArray();
		//			generator.writeNumberField("power", strength);
		//			generator.writeEndObject();
		//		}
				
				generator.writeEndArray();
				
				generator.writeEndObject();
				generator.close();
			} finally {
				fileWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
					
//		System.out.println(System.currentTimeMillis() - startTime);
	}

//	@Override
//	public void close() throws IOException {
//		generator.close();
//	}
}