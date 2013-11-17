/**
 * 
 */
package obt.index;

import java.util.Collection;
import java.util.HashMap;

import obt.configuration.ServiceConfiguration;
import obt.spots.Reader;
import obt.tag.Tag;

/**
 * @author bbehrens
 *
 */
public class DataIndex {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
//	protected ArrayList<String> registeredTags = new ArrayList<String>();
	
	protected HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
	
	public DataIndex() { } // Constructor
	
	protected HashMap<String, Tag> getTagMap() {
		return tagMap;
	} // getTagMap
	
	public Collection<Tag> getTags() {
		return getTagMap().values();
	} // getTags
	
	public Tag getTagByKey(String key) {
		return getTagMap().get(key);
	} // getTagById
	
	public void addTag(Tag tag) {
		getTagMap().put(tag.getKey(), tag);
	} // addTag
	
	public Collection<Reader> getKnownReaders() {
		return configuration.getReaders();
	} // getKnownReaders
	
//	public void registerTag(Tag tag) {
//		if (!registeredTags.contains(tag.getKey())) {
//			tag.setRegistered(true);
//			registeredTags.add(tag.getKey());
//			
//			System.out.println(tag.getKey() + " registered ");
//		}
//	} // registerTag
//	
//	public boolean isRegistered(String key) {
//		return registeredTags.contains(key);
//	} // isRegistered
//	
//	public void unRegisterTag(Tag tag) {
//		if (registeredTags.contains(tag.getKey())) {
//			tag.setRegistered(false);
//			registeredTags.remove(tag.getKey());
//			
//			System.out.println(tag.getKey() + " unRegistered ");
//		}
//	} // unRegisterTag
}