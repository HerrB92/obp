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
package obs.service.sighting;

import obs.service.Constants;

/**
 * Class used to provide the data structure for proximity sightings.
 * 
 * @author Björn Behrens (uol@btech.de)
 * @version 1.0
 */
public class ProximitySighting {
	// Tag id
	private int tagId = Constants.NOT_DEFINED;
	
	// String of T + tagId, may be used for hash maps
	private String tagKey;
	
	// Number of sightings (FIXME: Check with OpenBeacon: Of this particular id?)
	private int count = Constants.NOT_DEFINED;
	
	// Signal strength on proximity
	private int strength = Constants.NOT_DEFINED;
	
	/**
	 * Constructor
	 * 
	 * @param tagId		Tag id
	 * @param count		Number of sightings?
	 * @param strength	Signal strength of the proximity sighting
	 */
	public ProximitySighting(int tagId, int count, int strength) {
		setTagId(tagId);
		setCount(count);
		setStrength(strength);
	} // Constructor

	/**
	 * @return The tag id
	 */
	public int getTagId() {
		return tagId;
	} // getTagId

	/**
	 * @param tagId The tag id to set
	 */
	public void setTagId(int tagId) {
		this.tagId = tagId;
		this.tagKey = "T" + tagId;
	} // setTagId
	
	/**
	 * @return The tag key ("T" + tag id)
	 */
	public String getTagKey() {
		return tagKey;
	} // getTagKey

	/**
	 * @return The count of sightings
	 */
	public int getCount() {
		return count;
	} // getCount

	/**
	 * @param count The count of sightings to set
	 */
	public void setCount(int count) {
		this.count = count;
	} // setCount

	/**
	 * @return The signal strength on proximity
	 */
	public int getStrength() {
		return strength;
	} // getStrength

	/**
	 * @param strength The signal strength on proximity to set
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	} // setStrength
}