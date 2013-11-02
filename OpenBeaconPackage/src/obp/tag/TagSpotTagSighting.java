/**
 * 
 */
package obp.tag;

import obp.ServiceConfiguration;
import obp.spots.SpotTag;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class TagSpotTagSighting {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private SpotTag spotTag;
	private int minStrength;
	private DateTime lastUpdate;
	private boolean active = true;
		
	public TagSpotTagSighting(SpotTag spotTag, int strength) {
		setSpotTag(spotTag);
		setLastUpdate(DateTime.now()); // Has to be before setStrength!
		setStrength(strength);
	} // Constructor
	
	/**
	 * @return the spotTag
	 */
	public SpotTag getSpotTag() {
		return spotTag;
	} // getSpotTag
	
	/**
	 * @param spotTag the spotTag to set
	 */
	private void setSpotTag(SpotTag spotTag) {
		this.spotTag = spotTag;
	} // setSpotTag
	
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
		if (getLastUpdate().plusSeconds(configuration.getTagSpotTagSightingActiveSeconds()).isBeforeNow()) {
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
			// Strength has not changed, but we now, it is still there - 
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
	 * TagSpotTagSightingActiveSeconds
	 */
	private void checkActiveStatus() {
		if (active && 
			getLastUpdate().plusSeconds(configuration.getTagSpotTagSightingActiveSeconds()).isBeforeNow()) {
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
}
