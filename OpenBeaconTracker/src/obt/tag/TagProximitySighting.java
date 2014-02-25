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
package obt.tag;

import obt.configuration.ServiceConfiguration;

import org.joda.time.DateTime;

/**
 * Proximity (to other moving tag) sighting class.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class TagProximitySighting {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	// Tag key
	private String tagKey;
	
	// Minimal identified signal strength
	private int minStrength;
	
	// Sighting count (FIXME: Check with OpenBeacon: Of this particular other tag)
	private int count = 1;
	
	// Last update time stamp
	private DateTime lastUpdate;
	
	// Active flag, if the last sighting is younger than
	// the specified aging time delta.
	private boolean active = true;
		
	/**
	 * Constructor
	 * 
	 * @param tagKey	Tag key
	 * @param strength	Determined strength
	 */
	public TagProximitySighting(String tagKey, int strength) {
		setTagKey(tagKey);
		setLastUpdate(DateTime.now()); // Has to be before setMinStrength!
		setMinStrength(strength);
	} // Constructor
	
	/**
	 * @return the tag key
	 */
	public String getTagKey() {
		return tagKey;
	} // getTagKey

	/**
	 * @param tag key the key of the tag to set
	 */
	private void setTagKey(String tagKey) {
		this.tagKey = tagKey;
	} // setTagKey
		
	/**
	 * @return the minStrength
	 */
	public int getMinStrength() {
		return minStrength;
	} // getMinStrength
	
	/**
	 * @param minStrength the minStrength to set
	 */
	private void setMinStrength(int minStrength) {
		this.minStrength = minStrength;
		setLastUpdate(DateTime.now());
		setActive(true);
	} // setMinStrength
	
	/**
	 * @param strength the minStrength to set
	 * @return True, if strength value is updated (triggers update
	 *         of position estimation)
	 */
	public boolean setStrength(int strength) {
		if (getLastUpdate().plusSeconds(configuration.getStrengthAggregationAgedSeconds()).isBeforeNow()) {
			// If last update plus aggregation aged delta is older than now,
			// discard previous minimal strength value and use the new one.
			setMinStrength(strength);
			return true;
		}
		
		if (strength < getMinStrength()) {
			// New strength signal is smaller than the current one.
			// Set the new strength value and update last update datetime
			setMinStrength(strength);
			return true;
		}
		
		if (strength == getMinStrength()) {
			// Strength has not changed, but we know, it is still there - 
			// so refresh last update time
			setLastUpdate(DateTime.now());
		}
		
		return false;
	} // setStrength
	
	/**
	 * @return the last update
	 */
	public DateTime getLastUpdate() {
		return lastUpdate;
	} // getLastUpdate
	
	/**
	 * @param lastUpdate the last update datetime to set
	 */
	private void setLastUpdate(DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	} // setLastUpdate
	
	/**
	 * Set active status to inactive, if last update is older than
	 * TAGSIGHTING_ACTIVE_WINDOW_SECONDS
	 */
	private void checkActiveStatus() {
		if (active && 
			getLastUpdate().plusSeconds(configuration.getTagProximitySightingActiveSeconds()).isBeforeNow()) {
			setActive(false);
		}
	} // checkActiveStatus
	
	/**
	 * @return the last update
	 */
	public boolean isActive() {
		checkActiveStatus();
		
		return active;
	} // isActive
	
	/**
	 * @param active set to true to set this sighting to active
	 */
	private void setActive(boolean active) {
		this.active = active;
	} // setActive

	/**
	 * @return the count of sightings of the same tag
	 */
	public int getCount() {
		return count;
	} // getCount

	/**
	 * @param count The number of sightings of the same tag
	 */
	public void setCount(int count) {
		this.count = count;
	} // setCount
}