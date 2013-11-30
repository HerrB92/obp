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
package obt.index;

import java.util.Collection;
import java.util.HashMap;

import obt.configuration.ServiceConfiguration;
import obt.spots.Reader;
import obt.tag.Tag;

/**
 * Singleton class which keeps the moving tag, registered tag and unregistered tag data 
 * in memory for faster access.
 * 
 * @author Björn Behrens
 * @version 1.0
 * @since 1.0
 */
public class DataIndex {
	// Instance
	private static DataIndex instance = null;
	
	// Reference to service configuration, used for simpler
	// access to data kept in configuration class.
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	// Map for tag data
	protected final HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
	
	// Queue objects to store lists of the last 10 keys of registered or 
	// unregistered tags.
	protected final CircularQueue<String> registeredTagKeys = new CircularQueue<String>(10);
	protected final CircularQueue<String> unRegisteredTagKeys = new CircularQueue<String>(10);
	
	/**
	 * Private constructor (singleton)
	 */
	private DataIndex() {} // Constructor
	
	/**
	 * @return Instance of DataIndex class
	 */
	public static DataIndex getInstance() {
		if (instance == null) {
			instance = new DataIndex();
		}
		
		return instance;
	} // getInstance
	
	/**
	 * @return
	 */
	protected HashMap<String, Tag> getTagMap() {
		return tagMap;
	} // getTagMap
	
	/**
	 * @return
	 */
	public Collection<Tag> getTags() {
		return getTagMap().values();
	} // getTags
	
	/**
	 * @param key
	 * @return
	 */
	public Tag getTagByKey(String key) {
		return getTagMap().get(key);
	} // getTagById
	
	/**
	 * @param tag
	 */
	public void addTag(Tag tag) {
		getTagMap().put(tag.getKey(), tag);
	} // addTag
	
	/**
	 * @return
	 */
	public Collection<Reader> getKnownReaders() {
		return configuration.getReaders();
	} // getKnownReaders
	
	/**
	 * @param tagKey
	 */
	public void registerTagKey(String tagKey) {
		registeredTagKeys.offer(tagKey);
	} // registerTagKey
	
	/**
	 * @return
	 */
	public CircularQueue<String> getRegisteredTagKeys() {
		return registeredTagKeys;
	} // getRegisteredTagKeys
	
	/**
	 * @param tagKey
	 */
	public void unRegisterTagKey(String tagKey) {
		unRegisteredTagKeys.offer(tagKey);
	} // registerTagKey
	
	/**
	 * @return
	 */
	public CircularQueue<String> getUnRegisteredTagKeys() {
		return unRegisteredTagKeys;
	} // getUnRegisteredTagKeys
}