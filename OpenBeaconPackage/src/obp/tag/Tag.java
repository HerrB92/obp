/**
 * 
 */
package obp.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import obp.ServiceConfiguration;
import obp.tag.estimation.EstimationMethod;
import obp.tag.estimation.PositionEstimator;
import obs.service.Constants;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class Tag {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	private PositionEstimator estimator;
	
	private int id;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	
	private boolean buttonPressed = false;
	private DateTime buttonPressedStart;
	
	private HashMap<Integer, TagReaderSighting> tagReaderSightings = new HashMap<Integer, TagReaderSighting>();
	private HashMap<Integer, TagSpotTagSighting> tagSpotTagSightings = new HashMap<Integer, TagSpotTagSighting>();
	private HashMap<Integer, TagProximitySighting> proximitySightings = new HashMap<Integer, TagProximitySighting>();
	
	private int tagFlags;
	private int tagStrength;
	private int tagSequence;
	
	private int readerInterface;
	private int lastReaderId;
	private int sequence;
	private int timestamp;
	
	private int x;
	private int y;
	private EstimationMethod method;
//	private int accuracyLevel;

//	/**
//	 * @param id Tag id
//	 */
//	public Tag(int id) {
//		this(id, DefaultPositionEstimator.getInstance());
//	} // Constructor
	
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
	} // setId
	
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
				this.buttonPressed = true;
				setButtonPressedStart(DateTime.now());
			} else if (isButtonPressed() == true && 
				getButtonPressedStart().plusSeconds(configuration.getTagButtonActiveSeconds()).isBeforeNow()) {
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
	 * @return the tagFlags
	 */
	public int getTagFlags() {
		return tagFlags;
	} // getTagFlags

	/**
	 * @param tagFlags the tagFlags to set
	 */
	public void setTagFlags(int tagFlags) {
		this.tagFlags = tagFlags;
	} // setTagFlags

	/**
	 * @return the tagStrength
	 */
	public int getTagStrength() {
		return tagStrength;
	} // getTagStrength

	/**
	 * @param tagStrength the tagStrength to set
	 */
	public void setTagStrength(int tagStrength) {
		this.tagStrength = tagStrength;
	} // setTagStrength

	/**
	 * @return the tagSequence
	 */
	public int getTagSequence() {
		return tagSequence;
	} // getTagSequence

	/**
	 * @param tagSequence the tagSequence to set
	 */
	public void setTagSequence(int tagSequence) {
		this.tagSequence = tagSequence;
	}

	/**
	 * @return the readerInterface
	 */
	public int getReaderInterface() {
		return readerInterface;
	}

	/**
	 * @param readerInterface the readerInterface to set
	 */
	public void setReaderInterface(int readerInterface) {
		this.readerInterface = readerInterface;
	}

	/**
	 * @return the lastReaderId
	 */
	public int getLastReaderId() {
		return lastReaderId;
	}

	/**
	 * @param lastReaderId the last reader Id to set
	 */
	protected void setLastReaderId(int readerId) {
		this.lastReaderId = readerId;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the timestamp
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

//	/**
//	 * @return the proxTagId
//	 */
//	public int[] getProxTagId() {
//		return proxTagId;
//	}
//
//	/**
//	 * @param proxTagId the proxTagId to set
//	 */
//	public void setProxTagId(int[] proxTagId) {
//		this.proxTagId = proxTagId;
//	}
	
	public HashMap<Integer, TagReaderSighting> getTagReaderSightings() {
		return tagReaderSightings;
	} // getTagReaderSightings
	
	protected TagReaderSighting getTagReaderSighting(int readerId) {
		return getTagReaderSightings().get(readerId);
	} // getTagReaderSighting
	
	protected void addTagReaderSighting(int readerId, int strength) {
		tagReaderSightings.put(readerId, new TagReaderSighting(configuration.getReader(readerId), strength));
	} // addTagReaderSighting
	
	public ArrayList<TagReaderSighting> getActiveTagReaderSightings() {
		ArrayList<TagReaderSighting> sightings = new ArrayList<TagReaderSighting>();
		
		for (TagReaderSighting sighting : getTagReaderSightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveTagReaderSightings
	
	public void updateTagReaderSighting(int readerId, int strength) {
		if (readerId > Constants.NOT_DEFINED && strength > Constants.NOT_DEFINED) {
			setLastReaderId(readerId);
			
			boolean updateEstimation = false;
			
			TagReaderSighting sighting = getTagReaderSighting(readerId);
			if (sighting == null) {
				addTagReaderSighting(readerId, strength);
				updateEstimation = true;
			} else if (sighting.setStrength(strength)) {
				updateEstimation = true;
			}
			
			if (updateEstimation) {
				updatePositionEstimation();
				System.out.println(this.toString());
			}
		}
	} // updateTagReaderSighting
	
	public HashMap<Integer, TagProximitySighting> getProximitySightings() {
		return proximitySightings;
	} // getProximitySightings
	
	public HashMap<Integer, TagSpotTagSighting> getTagSpotTagSightings() {
		return tagSpotTagSightings;
	} // getTagSpotTagSightings
	
	protected TagSpotTagSighting getTagSpotTagSighting(int tagId) {
		return getTagSpotTagSightings().get(tagId);
	} // getTagSpotTagSighting
	
	protected void addTagSpotTagSighting(int tagId, int strength) {
		tagSpotTagSightings.put(tagId, new TagSpotTagSighting(configuration.getSpotTag(tagId), strength));
	} // addTagSpotTagSighting
	
	public ArrayList<TagSpotTagSighting> getActiveTagSpotTagSightings() {
		ArrayList<TagSpotTagSighting> sightings = new ArrayList<TagSpotTagSighting>();
		
		for (TagSpotTagSighting sighting : getTagSpotTagSightings().values()) {
			if (sighting.isActive()) {
				sightings.add(sighting);
			}
		}
		
		return sightings;
	} // getActiveTagSpotTagSightings
	
	public void updateProximitySightings(HashMap<Integer, TagProximitySighting> currentSightings) {
		if (currentSightings != null && currentSightings.size() > 0) {
			
			Integer tagId;
			boolean updateEstimation = false;
			ArrayList<Integer> removeSightings = new ArrayList<Integer>();
			
			for (TagProximitySighting newSighting: currentSightings.values()) {
				tagId = newSighting.getTag().getId();
				
				if (configuration.isValidSpotTag(tagId)) {
					TagSpotTagSighting sighting = getTagSpotTagSighting(tagId);
					
					if (sighting == null) {
						addTagSpotTagSighting(tagId, newSighting.getMinStrength());
						updateEstimation = true;
					} else if (sighting.setStrength(newSighting.getMinStrength())) {
						updateEstimation = true;
					}
					
					removeSightings.add(tagId);
				}
			}
			
			if (updateEstimation) {
				updatePositionEstimation();
				System.out.println(this.toString());
			}
			
			for (Integer removeTagId : removeSightings) {
				currentSightings.remove(removeTagId);
			}
		}
		
		if (currentSightings != null && currentSightings.size() > 0) {
			HashMap<Integer, TagProximitySighting> proximitySightings = getProximitySightings();
			
			TagProximitySighting sighting;
			Integer id;
			for (Entry<Integer, TagProximitySighting> proximitySighting : proximitySightings.entrySet()) {
				id = proximitySighting.getKey();
				sighting = proximitySighting.getValue();
				
				if (currentSightings.containsKey(id)) {
					sighting.setStrength(currentSightings.get(id).getMinStrength());
					sighting.setCount(currentSightings.get(id).getCount());
					currentSightings.remove(id);
//				} else {
//					proximitySighting.setValue(false);
				}
			}
			
			// Add new proximity sightings
			for (TagProximitySighting newSighting: currentSightings.values()) {
				proximitySightings.put(newSighting.getTag().getId(), newSighting);
			}			
		}
	} // updateProximitySightings
	
	public void setPositionEstimator(PositionEstimator estimator) {
		this.estimator = estimator;
	} // setPositionEstimator
	
	public PositionEstimator getPositionEstimator() {
		return estimator;
	} // getPositionEstimator
	
	// FIXME: This code ignores floor and group
	private void updatePositionEstimation() {
		if (estimator != null) {
			estimator.estimate(this);
			
			setX(estimator.getX());
			setY(estimator.getY());
			setMethod(estimator.getMethod());
		}
	} // updatePositionEstimation

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	protected void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	protected void setY(int y) {
		this.y = y;
	}
	
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
	
//	/**
//	 * @return the accuracy level
//	 */
//	public int getAccuracyLevel() {
//		return accuracyLevel;
//	}
//
//	/**
//	 * @param level the accuracy level to set
//	 */
//	private void setAccuracyLevel(int level) {
//		this.accuracyLevel = level;
//	}
	
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
		
		buffer.append("|Readers: ");
		
		int counter = 1;
		for (TagReaderSighting sighting: getTagReaderSightings().values()) {
			buffer.append("R");
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
		
		if (counter > 1) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		
		buffer.append("|SpotTags: ");
		
		counter = 1;
		for (TagSpotTagSighting sighting: getTagSpotTagSightings().values()) {
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
			buffer.append(sighting.getTag().getId());
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