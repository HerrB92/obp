/**
 * 
 */
package obp.tag;

import obp.Constants;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class TagReaderSighting {
	private int readerId;
	private int minStrength;
	private DateTime lastUpdate;
	
	public TagReaderSighting(int readerId, int strength) {
		setReaderId(readerId);
		setStrength(strength);
		setLastUpdate(DateTime.now());
	} // Constructor
	
	/**
	 * @return the readerId
	 */
	public int getReaderId() {
		return readerId;
	} // getReaderId
	
	/**
	 * @param lastSeen the lastSeen to set
	 */
	private void setReaderId(int readerId) {
		this.readerId = readerId;
	} // setReaderId
	
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
	} // setMinStrength
	
	/**
	 * @param strength the minStrength to set
	 */
	public void setStrength(int strength) {
		if (getLastUpdate().plusSeconds(Constants.STRENGTH_AGGREGATION_AGED_SECONDS).isBeforeNow()) {
			// If last update plus aggregation aged delta is older than now,
			// discard previous minimal strength value and use the new one.
			setMinStrength(strength);
		} else if (strength <= getMinStrength()) {
			// New strength signal is smaller or equal to the current one.
			// Set the new strength value and update last update datetime
			setMinStrength(strength);
		}
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
}
