/**
 * 
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

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class Tag {
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
	
//	private HashMap<String, TagReaderSighting> tagReaderSightings = new HashMap<String, TagReaderSighting>();
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
	}

	/**
	 * @param key the last reader key to set
	 */
	public void setLastReaderKey(String key) {
		this.lastReaderKey = key;
	}
	
	/**
	 * @return the key of the last spot tag
	 */
	public String getLastSpotTagKey() {
		return lastSpotTagKey;
	}

	/**
	 * @param key the key of the last spot tag to set
	 */
	public void setLastSpotTagKey(String key) {
		this.lastSpotTagKey = key;
	}
	
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
	
	public HashMap<String, TagProximitySighting> getProximitySightings() {
		return proximitySightings;
	} // getProximitySightings
	
	public TagProximitySighting getProximitySighting(String tagKey) {
		return getProximitySightings().get(tagKey);
	} // getProximitySighting
	
	public ArrayList<TagProximitySighting> getActiveProximitySightings() {
		ArrayList<TagProximitySighting> sightings = new ArrayList<TagProximitySighting>();
		
		for (TagProximitySighting sighting : getProximitySightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveProximitySightings
	
	public HashMap<String, TagSpotTagSighting> getSpotTagSightings() {
		return spotTagSightings;
	} // getSpotTagSightings
	
	protected TagSpotTagSighting getSpotTagSighting(String tagKey) {
		return getSpotTagSightings().get(tagKey);
	} // getSpotTagSighting
	
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
	
	public ArrayList<TagSpotTagSighting> getActiveSpotTagSightings() {
		ArrayList<TagSpotTagSighting> sightings = new ArrayList<TagSpotTagSighting>();
		
		for (TagSpotTagSighting sighting : getSpotTagSightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveSpotTagSightings
	
	public void setPositionEstimator(PositionEstimator estimator) {
		this.estimator = estimator;
	} // setPositionEstimator
	
	public PositionEstimator getPositionEstimator() {
		return estimator;
	} // getPositionEstimator
	
	// FIXME: This code ignores floor and group
	public void updatePositionEstimation() {
		if (estimator != null) {
			estimator.estimate(this);
			
			setX(estimator.getX());
			setY(estimator.getY());
			setMethod(estimator.getMethod());
			needsEstimation = false;
			
			System.out.println(this.toString());
		}
	} // updatePositionEstimation

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	} // getX

	/**
	 * @param x the x to set
	 */
	protected void setX(int x) {
		if (this.x != x) {
			this.x = x;
			setMainDataChanged(true);
		}
	} // setX

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	} // getY

	/**
	 * @param y the y to set
	 */
	protected void setY(int y) {
		if (this.y != y) {
			this.y = y;
			setMainDataChanged(true);
		}
	} // setY
	
	/**
	 * @return the method
	 */
	public EstimationMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	protected void setMethod(EstimationMethod method) {
		this.method = method;
	}
	
	/**
	 * @return Returns true, if spot tag sightings have been added or updated
	 *         so that a new position should be estimated. False otherwise. 
	 */
	public boolean needsEstimation() {
		return needsEstimation;
	}

	/**
	 * @param estimate Set to true to signal that the position estimation should
	 *        be updated.
	 */
	protected void setNeedsEstimation(boolean estimate) {
		this.needsEstimation = estimate;
	}

	/**
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
	}
	
	/**
	 * Signals, if x/y position or button pressed information has changed
	 * 
	 * @return the changed
	 */
	public boolean isMainDataChanged() {
		return mainDataChanged;
	}

	/**
	 * @param changed the changed to set
	 */
	protected void setMainDataChanged(boolean changed) {
		this.mainDataChanged = changed;
	}
	
	/**
	 * Set the MainDataChanged flag to false
	 */
	public void resetMainDataChanged() {
		this.mainDataChanged = false;
	}

	/* (non-Javadoc)
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
		
//		buffer.append("|Readers: ");
//		
//		int counter = 1;
//		for (TagReaderSighting sighting: getTagReaderSightings().values()) {
//			buffer.append("R");
//			buffer.append(counter);
//			buffer.append(": ");
//			buffer.append(sighting.getSpot().getId());
//			buffer.append(" (Active: ");
//			buffer.append(sighting.isActive());
//			buffer.append(", Strength: ");
//			buffer.append(sighting.getMinStrength());
//			buffer.append(") ");
//			
//			counter++;
//		}
//		
//		if (counter > 1) {
//			buffer.deleteCharAt(buffer.length() - 1);
//		}
		
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