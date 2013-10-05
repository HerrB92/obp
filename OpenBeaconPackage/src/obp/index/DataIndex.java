/**
 * 
 */
package obp.index;

import java.util.HashMap;

import obp.tag.Tag;

/**
 * @author bbehrens
 *
 */
public class DataIndex {
	private HashMap<Integer, Tag> tagMap = new HashMap<Integer, Tag>();
	
	public DataIndex() { } // Constructor
	
	public HashMap<Integer, Tag> getTagMap() {
		return tagMap;
	} // getTagMap
	
	public Tag getTagById(int id) {
		return getTagMap().get(id);
	} // getTagById
	
	public void addTag(Tag tag) {
		getTagMap().put(tag.getTagId(), tag);
	} // addTag
}