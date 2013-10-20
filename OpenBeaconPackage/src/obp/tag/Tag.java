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
		Reader reader;
		TagReaderSighting sighting;
		
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
			sighting = sightings.get(0);
			reader = configuration.getReader(sighting.getReaderId());
			setX(reader.getX());
			setY(reader.getY());
			setAccuracyLevel(Constants.STRENGTH_LEVELS_COUNT - sighting.getMinStrength());
			
			System.out.println("1: X: " + getX() + " Y: " + getY() + " S: " + sighting.getMinStrength());
			return;
		}
		
		if (sightings.size() == 2) {
			sighting = sightings.get(0);
			reader = configuration.getReader(sighting.getReaderId());
			
			int x1 = reader.getX();
			int y1 = reader.getY();
			int strength1 = sighting.getMinStrength() + 1; // +1: Avoid 0
			
			sighting = sightings.get(1);
			reader = configuration.getReader(sighting.getReaderId());
			
			int x2 = reader.getX();
			int y2 = reader.getY();
			int strength2 = sighting.getMinStrength() + 1; // +1: Avoid 0
			
			double factor = strength1/strength2;
			
			// FIXME: Mostly this will result in something funny...
			setX((int)Math.round(x1 + (factor * Math.abs(x2 - x1))));
			setY((int)Math.round(y1 + (factor * Math.abs(y2 - y1))));			
			setAccuracyLevel(0);
			
			System.out.println("2: X: " + getX() + " Y: " + getY() + " S1: " + strength1 + " S2: " + strength2);
			return;
		}
		
		if (sightings.size() > 2) {
			// Use Trilateration to estimate a position of the first three
			// reader sightings.
			
			// Uses information from http://en.wikipedia.org/wiki/Trilateration, referencing
			// Manolakis, D.E (2011) 'Efficient Solution and Performance Analysis of 
			// 3-D Position Estimation by Trilateration' IEEE Transactions on
			// Aerospace And Electronic Systems, vol. 32, no. 4, pp. 1239-1248
			// Available at: http://www.general-files.org/download/gs5bac8d73h32i0/IEEE-AES-96-Efficient%20Solution-and-Performance-Analysis-of-3-D%20Position-Estimation-by-Trilateration.pdf.html#
			// Accessed: 18.10.2013
			// and Reba, D. (2012) Answer to '2d trilateration' stackoverflow.com [Online]
			// Available at: http://stackoverflow.com/a/9754358 (Accessed: 18.10.2013)
			
			sighting = sightings.get(0);
			reader = configuration.getReader(sighting.getReaderId());
			int x1 = reader.getX();
			int y1 = reader.getY();
			int strength1 = (4 - sighting.getMinStrength()) * 100;
			int reader1 = sighting.getReaderId();
			
			sighting = sightings.get(1);
			reader = configuration.getReader(sighting.getReaderId());
			int x2 = reader.getX();
			int y2 = reader.getY();
			int strength2 = (4 - sighting.getMinStrength()) * 100;
			int reader2 = sighting.getReaderId();
			
			sighting = sightings.get(2);
			reader = configuration.getReader(sighting.getReaderId());
			int x3 = reader.getX();
			int y3 = reader.getY();
			int strength3 = (4 - sighting.getMinStrength()) * 100;
			int reader3 = sighting.getReaderId();
			
			// ex = (P2 - P1) / ‖P2 - P1‖, result: vector
			double exx = (x2 - x1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
			double exy = (y2 - y1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
			
			System.out.println("R1: " + reader1 + "(" + strength1 + "/" + x1 + "/" + y1 + ") R2: " + reader2 + " (" + strength2 + "/" + x2 + "/" + y2 + ") R3: " + reader3 + " (" + strength3 + "/" + x3 + "/" + y3 + ")");
//			System.out.println("Exx: " + exx);
//			System.out.println("Exy: " + exy);
			
			// i = ex · (P3 - P1), result: scalar
			double i = exx * (x3 - x1) + exy * (y3 - y1);
//			System.out.println("i: " + i);
			
			// ey = (P3 - P1 - i · ex) / ‖P3 - P1 - i · ex‖, result: vector
			double eyx = (x3 - x1 - (i * exx))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
			double eyy = (y3 - y1 - (i * exy))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
			
			// d = ‖P2 - P1‖, result: scalar
			double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
//			System.out.println("d: " + d);
			
			// j = ey · (P3 - P1), result: scalar
			double j = eyx * (x3 - x1) + eyy * (y3 - y1);
//			System.out.println("j: " + j);
			
			// x = (r12 - r22 + d2) / 2d, result: scalar
			double x = (Math.pow(strength1, 2) - Math.pow(strength2, 2) + Math.pow(d, 2)) / (2 * d);
//			System.out.println("x: " + x);
			
			// y = (r12 - r32 + i2 + j2) / 2j - ix / j, result: scalar
			double y = (Math.pow(strength1, 2) - Math.pow(strength3, 2) + Math.pow(i, 2) + Math.pow(j, 2)) / (2 * j) - (i/j) * x;
//			System.out.println("y: " + y);
			
			// p1,2 = P1 + x*ex + y*ey
			double xPos = x1 + x*exx + y*eyx;
			double yPos = y1 + x*exy + y*eyy;
			
			setX((int)Math.round(xPos));
			setY((int)Math.round(yPos));
			
			System.out.println("3: X: " + getX() + ", Y: " + getY());
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