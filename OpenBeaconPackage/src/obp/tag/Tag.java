/**
 * 
 */
package obp.tag;

import java.util.HashMap;

import obp.Constants;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class Tag {
	private int id;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	
	private boolean buttonPressed = false;
	private DateTime buttonPressedStart;
	
	private HashMap<Integer, TagReaderSighting> tagReaderSightings = new HashMap<Integer, TagReaderSighting>();
	
	private int tagFlags;
	private int tagStrength;
	private int tagSequence;
	
	private int readerInterface;
	private int readerId;
	private int sequence;
	private int timestamp;
	
	private int[] proxTagId = new int[4];

	/**
	 * @param tagId
	 * @param buttonPressed
	 */
	public Tag(int id, int readerId, int strength) {
		setId(id);
		updateTagReaderSighting(readerId, strength);
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
	public void setButtonPressed(boolean buttonPressed) {
		// If the information is received, that the button is pressed,
		// set buttonPressed to true and update buttonPressedStart date.
		
		// If the information is received, that the button is not pressed
		// anymore (which can also be triggered by a protocol which does
		// not transmit the "button pressed" information) set buttonPressed
		// only to false after the specified delay
		if (buttonPressed == true) {
			this.buttonPressed = true;
			setButtonPressedStart(DateTime.now());
		} else if (isButtonPressed() == true && 
			getButtonPressedStart().plusSeconds(Constants.TAGSIGHTING_BUTTON_TIME_SECONDS).isBeforeNow()) {
			this.buttonPressed = false;
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

	/**
	 * @return the proxTagId
	 */
	public int[] getProxTagId() {
		return proxTagId;
	}

	/**
	 * @param proxTagId the proxTagId to set
	 */
	public void setProxTagId(int[] proxTagId) {
		this.proxTagId = proxTagId;
	}
	
	private void removeAgedSightings() {
		// FIXME
//		for (TagReaderSighting sighting : getTagReaderSightings().) {
//			
//		}
	} // removeAgedSightings
	
	public HashMap<Integer, TagReaderSighting> getTagReaderSightings() {
		removeAgedSightings();
		return tagReaderSightings;
	} // getTagReaderSightings
	
	public void updateTagReaderSighting(int readerId, int strength) {
		TagReaderSighting sighting = getTagReaderSightings().get(readerId);
		if (sighting == null) {
			getTagReaderSightings().put(readerId, new TagReaderSighting(readerId, strength));
		} else {
			sighting.setStrength(strength);
		}
	} // updateTagReaderSighting
}