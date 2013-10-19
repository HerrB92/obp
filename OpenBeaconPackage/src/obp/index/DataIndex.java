/**
 * 
 */
package obp.index;

import java.util.HashMap;

import obp.reader.Reader;
import obp.tag.Tag;

/**
 * @author bbehrens
 *
 */
public class DataIndex {
	private HashMap<Integer, Tag> tagMap = new HashMap<Integer, Tag>();
	private HashMap<Integer, Reader> unknownReaderMap = new HashMap<Integer, Reader>();
	
	public DataIndex() { } // Constructor
	
	public HashMap<Integer, Tag> getTagMap() {
		return tagMap;
	} // getTagMap
	
	public Tag getTagById(int id) {
		return getTagMap().get(id);
	} // getTagById
	
	public void addTag(Tag tag) {
		getTagMap().put(tag.getId(), tag);
	} // addTag
	
	public HashMap<Integer, Reader> getUnknownReaderMap() {
		return unknownReaderMap;
	} // getReaderMap
	
	public Reader getUnknownReaderById(int id) {
		return getUnknownReaderMap().get(id);
	} // getUnknownReaderById
	
	public void addUnknownReader(Reader reader) {
		getUnknownReaderMap().put(reader.getId(), reader);
	} // addUnknownReader
}