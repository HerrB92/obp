/**
 * 
 */
package obp.index;

import java.util.Collection;
import java.util.HashMap;

import obp.ServiceConfiguration;
import obp.spots.Reader;
import obp.tag.Tag;

/**
 * @author bbehrens
 *
 */
public class DataIndex {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	protected HashMap<Integer, Tag> registeredTags = new HashMap<Integer, Tag>();
	protected HashMap<Integer, Tag> tagMap = new HashMap<Integer, Tag>();
	protected HashMap<Integer, Reader> unknownReaderMap = new HashMap<Integer, Reader>();
	
	public DataIndex() { } // Constructor
	
	protected HashMap<Integer, Tag> getTagMap() {
		return tagMap;
	} // getTagMap
	
	public Collection<Tag> getTags() {
		return getTagMap().values();
	} // getTags
	
	public Tag getTagById(int id) {
		return getTagMap().get(id);
	} // getTagById
	
	public void addTag(Tag tag) {
		getTagMap().put(tag.getId(), tag);
	} // addTag
	
	public Collection<Reader> getKnownReaders() {
		return configuration.getReaders();
	} // getKnownReaders
	
	protected HashMap<Integer, Reader> getUnknownReaderMap() {
		return unknownReaderMap;
	} // getUnknownReaderMap
	
	public Collection<Reader> getUnknownReaders() {
		return getUnknownReaderMap().values();
	} // getUnknownReaders
	
	public Reader getUnknownReaderById(int id) {
		return getUnknownReaderMap().get(id);
	} // getUnknownReaderById
	
	public void addUnknownReader(Reader reader) {
		getUnknownReaderMap().put(reader.getId(), reader);
	} // addUnknownReader
}