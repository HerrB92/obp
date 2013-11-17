/**
 * 
 */
package obt.tag;

import obt.spots.Spot;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public abstract class SpotSighting<E extends Spot> {
	protected E spot;
	protected int minStrength;
	protected int aggregationAgedSeconds;
	protected int activeSeconds;
	protected DateTime lastUpdate;
	protected boolean active = true;
	
	public SpotSighting(E spot, int strength, int aggregationAgedSeconds, int activeSeconds) {
		setSpot(spot);
		setLastUpdate(DateTime.now()); // Has to be before setStrength!
		setStrength(strength);
		setAggregationAgedSeconds(aggregationAgedSeconds);
		setActiveSeconds(activeSeconds);
	} // Constructor
	
	/**
	 * @return the aggregationSeconds
	 */
	public int getAggregationAgedSeconds() {
		return aggregationAgedSeconds;
	} // getAggregationAgedSeconds

	/**
	 * @param aggregationAgedSeconds the aggregationAgedSeconds to set
	 */
	protected void setAggregationAgedSeconds(int aggregationAgedSeconds) {
		this.aggregationAgedSeconds = aggregationAgedSeconds;
	} // setAggregationAgedSeconds

	/**
	 * @return the activeSeconds
	 */
	public int getActiveSeconds() {
		return activeSeconds;
	} // getActiveSeconds

	/**
	 * @param activeSeconds the activeSeconds to set
	 */
	protected void setActiveSeconds(int activeSeconds) {
		this.activeSeconds = activeSeconds;
	} // setActiveSeconds

	/**
	 * @return the spot
	 */
	public E getSpot() {
		return spot;
	} // getSpot
	
	/**
	 * @param spot the spot to set
	 */
	protected void setSpot(E spot) {
		this.spot = spot;
	} // setSpot
	
	/**
	 * @param strength the minStrength to set
	 * @return True, if strength value is updated (triggers update
	 *         of position estimation)
	 */
	public boolean setStrength(int strength) {
		if (getLastUpdate().plusSeconds(getAggregationAgedSeconds()).isBeforeNow()) {
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
	 * @return the minStrength
	 */
	public int getMinStrength() {
		return minStrength;
	} // getMinStrength
	
	/**
	 * @param minStrength the minStrength to set
	 */
	protected void setMinStrength(int minStrength) {
		this.minStrength = minStrength;
		setLastUpdate(DateTime.now());
		setActive(true);
	} // setMinStrength
	
	/**
	 * @return the last update
	 */
	public DateTime getLastUpdate() {
		return lastUpdate;
	} // getLastUpdate
	
	/**
	 * @param lastUpdate the last update datetime to set
	 */
	protected void setLastUpdate(DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	} // setLastUpdate
	
	/**
	 * Set active status to inactive, if last update is older than
	 * TagSpotTagSightingActiveSeconds
	 */
	protected void checkActiveStatus() {
		if (active && 
			getLastUpdate().plusSeconds(getActiveSeconds()).isBeforeNow()) {
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
	protected void setActive(boolean active) {
		this.active = active;
	} // setActive
}