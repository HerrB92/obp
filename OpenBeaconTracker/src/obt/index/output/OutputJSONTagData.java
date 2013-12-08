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

import org.joda.time.DateTime;

import obp.service.Constants;
import obt.configuration.ServiceConfiguration;
import obt.index.DataIndex;
import obt.spots.Reader;
import obt.spots.Spot;
import obt.tag.Tag;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author bbehrens
 *
 */
public class OutputJSONTagData extends OutputJSON {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	private final DataIndex index = DataIndex.getInstance();
	
	public OutputJSONTagData(String fileName) {
		super(fileName);
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
	@Override
	protected void process(JsonGenerator generator)
			throws JsonGenerationException, IOException {
		DateTime nowWindow = DateTime.now().minusSeconds(5);
		
		generator.writeNumberField("maxX", configuration.getMaxX());
		generator.writeNumberField("maxY", configuration.getMaxY());
		
		// Elements of the original OpenBeacon JSON format,
		// currently not provided or used.
//		generator.writeObjectFieldStart("packets");
//		generator.writeNumberField("rate", 0); // FIXME
//		generator.writeNumberField("ignored", 0); // FIXME
//		generator.writeNumberField("invalid_protocol", 0); // FIXME
//		generator.writeNumberField("unknown_reader", 0); // FIXME
//		generator.writeNumberField("crc_error", 0); // FIXME
//		generator.writeNumberField("crc_ok", 0); // FIXME
//		generator.writeEndObject();
		
		generator.writeArrayFieldStart("tag");
		
		for (Tag tag: index.getTags()) {
			generator.writeStartObject();
			generator.writeStringField("id", tag.getKey());
			generator.writeBooleanField("registered", tag.isRegistered());
			
			if (tag.isRegistered()) {
				// If registered, check, if the tag was not seen for some time
				// and output position
				
				if (tag.getLastSeen() == null || tag.getLastSeen().isBefore(nowWindow)) {
					generator.writeStringField("lastseen", "0000-00-00 00:00:00");
				} else {
					generator.writeStringField("lastseen", tag.getLastSeen().toString());
				}
				
				generator.writeArrayFieldStart("loc");
				generator.writeNumber(tag.getX());
				generator.writeNumber(tag.getY());
				generator.writeEndArray();
			} else {
				// If the tag is not registered, always report
				// default values for lastseen and location.
				
				generator.writeStringField("lastseen", "0000-00-00 00:00:00");
				
				generator.writeArrayFieldStart("loc");
				generator.writeNumber(Constants.NOT_DEFINED);
				generator.writeNumber(Constants.NOT_DEFINED);
				generator.writeEndArray();
			}
			
			// For OpenBeaconPackage, this is currently always 0.
			// The original C++ implementation provides the feature 
			// to define different keys for decryption to support
			// different OpenBeacon keys used on different events 
			// (e.g. CCC 2010).
			generator.writeNumberField("key", 0);
			
			generator.writeStringField("reader", tag.getLastReaderKey());
			generator.writeBooleanField("button", tag.isButtonPressed());
			generator.writeEndObject();
		}
		
		generator.writeEndArray();
		
		generator.writeArrayFieldStart("reader");
		
		for (Reader reader: index.getKnownReaders()) {
			generator.writeStartObject();
			generator.writeStringField("id", reader.getKey());
			generator.writeStringField("name", reader.getName());
			
			if (reader.getLastSeen() == null || reader.getLastSeen().isBefore(nowWindow)) {
				generator.writeStringField("lastseen", "0000-00-00 00:00:00");
			} else {
				generator.writeStringField("lastseen", reader.getLastSeen().toString());
			}
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
		
		generator.writeArrayFieldStart("spots");
		
		for (Spot spotTag: configuration.getSpotTags()) {
			generator.writeStartObject();
			generator.writeStringField("id", spotTag.getKey());
			generator.writeStringField("name", spotTag.getName());
			generator.writeStringField("type", spotTag.getType().name());
			
			if (spotTag.getLastSeen() == null || spotTag.getLastSeen().isBefore(nowWindow)) {
				generator.writeStringField("lastseen", "0000-00-00 00:00:00");
			} else {
				generator.writeStringField("lastseen", spotTag.getLastSeen().toString());
			}
			
			generator.writeArrayFieldStart("loc");
			generator.writeNumber(spotTag.getX());
			generator.writeNumber(spotTag.getY());
			generator.writeEndArray();					
			generator.writeEndObject();
		}
		
		generator.writeEndArray();
		
//		generator.writeArrayFieldStart("edge");
//		
////		for (Proximity proximity: index.getProximities()) {
////			generator.writeStartObject();
////			generator.writeArrayFieldStart("tag");
////			generator.writeNumber(tagId1);
////			generator.writeNumber(tagId2);
////			generator.writeEndArray();
////			generator.writeNumberField("power", strength);
////			generator.writeEndObject();
////		}
//		
//		generator.writeEndArray();		
	}
}