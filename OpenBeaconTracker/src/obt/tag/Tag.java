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

import java.util.ArrayList;
import java.util.HashMap;

import obs.service.sighting.ProximitySighting;
import obt.configuration.ServiceConfiguration;
import obt.index.DataIndex;
import obt.spots.Spot;
import obt.tag.estimation.EstimationMethod;
import obt.tag.estimation.PositionEstimator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Tag class.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class Tag {
	private static final Logger logger = LogManager.getLogger(Tag.class.getName());
	
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	private final DataIndex index = DataIndex.getInstance();
	private PositionEstimator estimator;
	
	private int id;
	private int trackId = 0;
	private String key;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	private boolean buttonPressed = false;
	private DateTime buttonPressedStart;
	private String lastReaderKey = "";
	private String lastSpotTagKey = "";
	
	private HashMap<String, TagSpotTagSighting> spotTagSightings = new HashMap<String, TagSpotTagSighting>();
	private HashMap<String, TagProximitySighting> proximitySightings = new HashMap<String, TagProximitySighting>();
	
	private int x;
	private int y;
	private EstimationMethod method;
	private boolean needsEstimation = false;
	private boolean registered = false;
	private boolean mainDataChanged = false;
	
	/**
	 * @param id Tag id
	 * @param estimator Position Estimator
	 */
	public Tag(int id, PositionEstimator estimator) {
		setId(id);
		setPositionEstimator(estimator);
	} // Constructor

	/**
	 * @return the tag id
	 */
	public int getId() {
		return id;
	} // getId

	/**
	 * @param id the tagId to set
	 */
	public void setId(int id) {
		this.id = id;
		setKey(id);
	} // setId
	
	/**
	 * Returns the track id. The track id will be incremented
	 * by each registration (per run starting at 0).
	 * 
	 * @return the track id
	 */
	public int getTrackId() {
		return trackId;
	} // getTrackId

	/**
	 *
	 */
	protected void incrementTrackId() {
		trackId++;
	} // incrementTrackId
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	} // getKey

	/**
	 * @param key the tag key to set
	 */
	private void setKey(int id) {
		this.key = "T" + id;
	} // setKey
	
	/**
	 * @return Created joda datetime
	 */
	public DateTime getCreated() {
		return created;
	} // getCreated
	
	/**
	 * @return the lastSeen
	 */
	public DateTime getLastSeen() {
		return lastSeen;
	} // getLastSeen

	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(DateTime lastSeen) {
		this.lastSeen = lastSeen;
	} // setLastSeen
	
	/**
	 * @return true, if tag button is pressed, false otherwise
	 */
	public boolean isButtonPressed() {
		return buttonPressed;
	} // isButtonPressed

	/**
	 * @param buttonPressed the ButtonPressed to set
	 */
	public void setButtonPressed(Boolean buttonPressed) {
		if (buttonPressed != null) {
			// If the information is received, that the button is pressed,
			// set buttonPressed to true and update buttonPressedStart date.			
			if (buttonPressed == true) {
				if (isButtonPressed() == false) {
					setMainDataChanged(true);
				}
				
				this.buttonPressed = true;
				setButtonPressedStart(DateTime.now());
			} else if (isButtonPressed() == true && 
				getButtonPressedStart().plusSeconds(configuration.getTagButtonActiveSeconds()).isBeforeNow()) {
				
				setMainDataChanged(true);
				this.buttonPressed = false;
			}
		}
	} // setButtonPressed
	
	/**
	 * @return the joda datetime the button has been
	 *         recognized as "pressed"
	 */
	private DateTime getButtonPressedStart() {		
		return buttonPressedStart;
	} // getButtonPressedStart

	/**
	 * @param lastSeen the lastSeen to set
	 */
	private void setButtonPressedStart(DateTime buttonPressedStart) {
		this.buttonPressedStart = buttonPressedStart;
	} // setButtonPressedStart
	
	/**
	 * @return the key of the last reader
	 */
	public String getLastReaderKey() {
		return lastReaderKey;
	} // getLastReaderKey

	/**
	 * @param key the last reader key to set
	 */
	public void setLastReaderKey(String key) {
		this.lastReaderKey = key;
	} // setLastReaderKey
	
	/**
	 * @return the key of the last spot tag
	 */
	public String getLastSpotTagKey() {
		return lastSpotTagKey;
	} // getLastSpotTagKey

	/**
	 * @param key the key of the last spot tag to set
	 */
	public void setLastSpotTagKey(String key) {
		this.lastSpotTagKey = key;
	} // setLastSpotTagKey
	
	/**
	 * Add proximity (to other moving tag) sighting.
	 * 
	 * @param newSighting
	 */
	public void addProximitySighting(ProximitySighting newSighting) {
		String tagKey = "T" + newSighting.getTagId();
		TagProximitySighting sighting;
		
		if (proximitySightings.containsKey(tagKey)) {
			sighting = proximitySightings.get(tagKey);
			sighting.setStrength(newSighting.getStrength());
		} else {
			sighting = new TagProximitySighting(tagKey, newSighting.getStrength());
		}
		
		sighting.setCount(newSighting.getCount());
	} // addProximitySighting
	
	/**
	 * Get map of proximity (to other moving tag) sightings.
	 * 
	 * @return HashMap<String, TagProximitySighting> Map of proximity sightings
	 */
	public HashMap<String, TagProximitySighting> getProximitySightings() {
		return proximitySightings;
	} // getProximitySightings
	
	/**
	 * Get proximity (to other moving tag) sighting for the given (other)
	 * moving tag key
	 * 
	 * @param tagKey
	 * @return TagProximitySighting Sighting object or null
	 */
	public TagProximitySighting getProximitySighting(String tagKey) {
		return getProximitySightings().get(tagKey);
	} // getProximitySighting
	
	/**
	 * Update active status of the proximity sightings, based on the last seen
	 * time stamp and the specified aging time frame and return list of 
	 * remaining active proximity sightings.
	 * 
	 * @return ArrayList<TagProximitySighting> List of active proximity sightings
	 */
	public ArrayList<TagProximitySighting> getActiveProximitySightings() {
		ArrayList<TagProximitySighting> sightings = new ArrayList<TagProximitySighting>();
		
		for (TagProximitySighting sighting : getProximitySightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveProximitySightings
	
	/**
	 * Get map of spot tag proximity sightings.
	 * 
	 * @return HashMap<String, TagSpotTagSighting> Map of spot tag proximity sightings
	 */
	public HashMap<String, TagSpotTagSighting> getSpotTagSightings() {
		return spotTagSightings;
	} // getSpotTagSightings
	
	/**
	 * Get spot tag proximity sighting for the given spot tag key
	 * 
	 * @param tagKey
	 * @return TagSpotTagSighting Sighting object or null
	 */
	protected TagSpotTagSighting getSpotTagSighting(String tagKey) {
		return getSpotTagSightings().get(tagKey);
	} // getSpotTagSighting
	
	/**
	 * Add spot tag proximity sighting.
	 * 
	 * @param spot		Spot tag object
	 * @param strength	Determined signal strength
	 */
	public void addSpotTagSighting(Spot spot, int strength) {
		TagSpotTagSighting sighting = getSpotTagSighting(spot.getKey());
		
		if (sighting == null) {
			spotTagSightings.put(spot.getKey(), new TagSpotTagSighting(spot, strength));
			needsEstimation = true;
		} else if (sighting.setStrength(strength)) {
			needsEstimation = true;
		}
		
		setLastSpotTagKey(spot.getKey());
	} // addSpotTagSighting
	
	/**
	 * Update active status of the spot tag proximity sightings, based on the last 
	 * seen time stamp and the specified aging time frame and return list of 
	 * remaining active spot tag proximity sightings.
	 * 
	 * @return ArrayList<TagSpotTagSighting> List of active spot tag sightings
	 */
	public ArrayList<TagSpotTagSighting> getActiveSpotTagSightings() {
		ArrayList<TagSpotTagSighting> sightings = new ArrayList<TagSpotTagSighting>();
		
		for (TagSpotTagSighting sighting : getSpotTagSightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveSpotTagSightings
	
	/**
	 * Set position estimator instance.
	 * 
	 * @param estimator
	 */
	public void setPositionEstimator(PositionEstimator estimator) {
		this.estimator = estimator;
	} // setPositionEstimator
	
	/**
	 * @return PositionEstimator Used instance of the position estimator
	 */
	public PositionEstimator getPositionEstimator() {
		return estimator;
	} // getPositionEstimator
	
	/**
	 * Update position estimation.
	 * 
	 * FIXME: This code ignores floor and group
	 */
	public void updatePositionEstimation() {
		if (estimator != null) {
			estimator.estimate(this);
			
			setX(estimator.getX());
			setY(estimator.getY());
			setMethod(estimator.getMethod());
			needsEstimation = false;
			
			logger.debug(this.toString());
		}
	} // updatePositionEstimation

	/**
	 * @return The (last estimated) x position
	 */
	public int getX() {
		return x;
	} // getX

	/**
	 * @param x The x position to set
	 */
	protected void setX(int x) {
		if (this.x != x) {
			this.x = x;
			setMainDataChanged(true);
		}
	} // setX

	/**
	 * @return The (last estimated) y position
	 */
	public int getY() {
		return y;
	} // getY

	/**
	 * @param y The y position to set
	 */
	protected void setY(int y) {
		if (this.y != y) {
			this.y = y;
			setMainDataChanged(true);
		}
	} // setY
	
	/**
	 * @return The (last) method used for position estimation
	 */
	public EstimationMethod getMethod() {
		return method;
	} // getMethod

	/**
	 * @param method The estimation method to set
	 */
	protected void setMethod(EstimationMethod method) {
		this.method = method;
	} // setMethod
	
	/**
	 * @return Returns true, if spot tag sightings have been added or updated
	 *         so that a new position should be estimated. False otherwise. 
	 */
	public boolean needsEstimation() {
		return needsEstimation;
	} // needsEstimation

	/**
	 * @param estimate Set to true to signal that the position estimation should
	 *        be updated.
	 */
	protected void setNeedsEstimation(boolean estimate) {
		this.needsEstimation = estimate;
	} // setNeedsEstimation

	/**
	 * Register the tag.
	 * 
	 * @param spotTagX X coordinate of the register spot tag
	 * @param spotTagY Y coordinate of the register spot tag
	 */
	public void register(int spotTagX, int spotTagY) {
		registered = true;
		index.registerTagKey(getKey());
		
		incrementTrackId();
		setX(spotTagX);
		setY(spotTagY);
		setMethod(EstimationMethod.OneSpotTag);
		needsEstimation = false;
	} // register
	
	/**
	 * Unregister the tag.
	 * 
	 * @param spotTagX X coordinate of the unregister spot tag
	 * @param spotTagY Y coordinate of the unregister spot tag
	 */
	public void unregister(int spotTagX, int spotTagY) {
		registered = false;
		index.unRegisterTagKey(getKey());
		
		setX(spotTagX);
		setY(spotTagY);
		setMethod(EstimationMethod.OneSpotTag);
		needsEstimation = false;
	} // unregister
	
	/**
	 * @return Returns true, if the tag is currently registered. False otherwise. 
	 */
	public boolean isRegistered() {
		return registered;
	} // isRegistered
	
	/**
	 * Signals, if x-/y-position or button pressed information has changed
	 * 
	 * @return the changed
	 */
	public boolean isMainDataChanged() {
		return mainDataChanged;
	} // isMainDataChanged

	/**
	 * @param changed the changed to set
	 */
	protected void setMainDataChanged(boolean changed) {
		this.mainDataChanged = changed;
	} // setMainDataChanged
	
	/**
	 * Set the MainDataChanged flag to false
	 */
	public void resetMainDataChanged() {
		this.mainDataChanged = false;
	} // resetMainDataChanged

	/**
	 * Overridden toString method to simplify debugging.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Tag: ID: ");
		buffer.append(getId());
		buffer.append("|Method: ");
		buffer.append(getMethod());
		buffer.append("|X: ");
		buffer.append(getX());
		buffer.append("|Y: ");
		buffer.append(getY());
		buffer.append("|Button: ");
		buffer.append(isButtonPressed());
		
		buffer.append("|SpotTags: ");
		
		int counter = 1;
		for (TagSpotTagSighting sighting: getSpotTagSightings().values()) {
			buffer.append("S");
			buffer.append(counter);
			buffer.append(": ");
			buffer.append(sighting.getSpot().getId());
			buffer.append(" (Active: ");
			buffer.append(sighting.isActive());
			buffer.append(", Strength: ");
			buffer.append(sighting.getMinStrength());
			buffer.append(") ");
			
			counter++;
		}
		
		buffer.append("|Proximity: ");
		
		if (counter > 1) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		
		counter = 1;
		for (TagProximitySighting sighting: getProximitySightings().values()) {
			buffer.append("P");
			buffer.append(counter);
			buffer.append(": ");
			buffer.append(sighting.getTagKey());
			buffer.append(" (Active: ");
			buffer.append(sighting.isActive());
			buffer.append(", Strength: ");
			buffer.append(sighting.getMinStrength());
			buffer.append(", Count: ");
			buffer.append(sighting.getCount());
			buffer.append(") ");
			
			counter++;
		}
		
		if (counter > 1) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		
		return buffer.toString();
	} // toString
}