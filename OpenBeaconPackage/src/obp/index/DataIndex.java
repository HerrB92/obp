/**
 * 
 */
package obp.index;

import java.util.HashMap;

import obp.tag.Tag;
import odp.reader.Reader;

/**
 * @author bbehrens
 *
 */
public class DataIndex {
	private HashMap<Integer, Tag> tagMap = new HashMap<Integer, Tag>();
	private HashMap<Integer, Reader> readerMap = new HashMap<Integer, Reader>();
	
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
	
	public HashMap<Integer, Reader> getReaderMap() {
		return readerMap;
	} // getReaderMap
	
	public Reader getReaderById(int id) {
		return getReaderMap().get(id);
	} // getReaderById
	
	public void addReader(Reader reader) {
		getReaderMap().put(reader.getId(), reader);
	} // addReader
}