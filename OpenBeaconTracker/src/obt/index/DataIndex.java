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
	private static DataIndex instance = null;
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	protected final HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
	protected final CircularQueue<String> registeredTagKeys = new CircularQueue<String>(10);
	protected final CircularQueue<String> unRegisteredTagKeys = new CircularQueue<String>(10);
	
	private DataIndex() {} // Constructor
	
	public static DataIndex getInstance() {
		if (instance == null) {
			instance = new DataIndex();
		}
		
		return instance;
	} // getInstance
	
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
	
	public void registerTagKey(String tagKey) {
		registeredTagKeys.offer(tagKey);
	} // registerTagKey
	
	public CircularQueue<String> getRegisteredTagKeys() {
		return registeredTagKeys;
	} // getRegisteredTagKeys
	
	public void unRegisterTagKey(String tagKey) {
		unRegisteredTagKeys.offer(tagKey);
	} // registerTagKey
	
	public CircularQueue<String> getUnRegisteredTagKeys() {
		return unRegisteredTagKeys;
	} // getUnRegisteredTagKeys
}