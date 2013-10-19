/**
 * 
 */
package obp.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import obp.ServiceConfiguration;
import obp.reader.Reader;
import obp.service.Constants;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class Tag {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private int id;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	
	private boolean buttonPressed = false;
	private DateTime buttonPressedStart;
	
	private HashMap<Integer, TagReaderSighting> tagReaderSightings = new HashMap<Integer, TagReaderSighting>();
	private HashMap<Integer, Boolean> proximitySightings = new HashMap<Integer, Boolean>();
	
	private int tagFlags;
	private int tagStrength;
	private int tagSequence;
	
	private int readerInterface;
	private int readerId;
	private int sequence;
	private int timestamp;
	
	private int x;
	private int y;
	private int accuracyLevel;
	
	//private int[] proxTagId = new int[4];

	/**
	 * @param tagId
	 * @param buttonPressed
	 */
	public Tag(int id) {
		setId(id);
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
	 * @return the readerId
	 */
	public int getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(int readerId) {
		this.readerId = readerId;
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
			TagReaderSighting sighting = getTagReaderSightings().get(readerId);
			
			boolean updateEstimation = false;
			if (sighting == null) {
				getTagReaderSightings().put(readerId, new TagReaderSighting(readerId, strength));
				updateEstimation = true;
			} else {
				updateEstimation = sighting.setStrength(strength);
			}
			
			if (updateEstimation) {
				updatePositionEstimation();
			}
		}
	} // updateTagReaderSighting
	
	public HashMap<Integer, Boolean> getProximitySightings() {
		return proximitySightings;
	} // getProximitySightings
	
	public void updateProximitySightings(ArrayList<Integer> sightings) {
		if (sightings != null) {
			HashMap<Integer, Boolean> proximitySightings = getProximitySightings();
			
			for (Entry<Integer, Boolean> proximitySighting : proximitySightings.entrySet()) {
				if (sightings.contains(proximitySighting.getKey())) {
					proximitySighting.setValue(true);
					sightings.remove(proximitySighting.getKey());
				} else {
					proximitySighting.setValue(false);
				}
			}
			
			// Add new proximity sightings
			for (Integer proximityTagId : sightings) {
				proximitySightings.put(proximityTagId, true);
			}
		}
	} // updateProximitySightings
	
	private void updatePositionEstimation() {
		ArrayList<TagReaderSighting> sightings = getActiveTagReaderSightings();
		
		if (sightings.size() == 0) {
			// If there is no reader seeing this tag, we have
			// left the building...
			setX(0);
			setY(0);
			setAccuracyLevel(Constants.STRENGTH_LEVELS_COUNT);
			return;
		}
		
		if (sightings.size() == 1) {
			// If there is only one active reader sighting, than
			// we can only predict that we are at the position of the 
			// reader with the inaccuracy of the highest possible power 
			// level minus the provided minimal power level at the 
			// last update.
			TagReaderSighting sighting = sightings.get(0);
			Reader reader = configuration.getReader(sighting.getReaderId());
			setX(reader.getX());
			setY(reader.getY());
			setAccuracyLevel(Constants.STRENGTH_LEVELS_COUNT - sighting.getMinStrength());
			return;
		}
		
		for (TagReaderSighting sighting: getActiveTagReaderSightings()) {
			
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
	private void setX(int x) {
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
	private void setY(int y) {
		this.y = y;
	}
	
	/**
	 * @return the accuracy level
	 */
	public int getAccuracyLevel() {
		return accuracyLevel;
	}

	/**
	 * @param level the accuracy level to set
	 */
	private void setAccuracyLevel(int level) {
		this.accuracyLevel = level;
	}
}