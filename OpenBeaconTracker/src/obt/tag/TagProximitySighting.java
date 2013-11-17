/**
 * 
 */
package obt.tag;

import obt.configuration.ServiceConfiguration;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class TagProximitySighting {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private String tagKey;
	private int minStrength;
	private int count = 1;
	private DateTime lastUpdate;
	private boolean active = true;
		
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
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
}