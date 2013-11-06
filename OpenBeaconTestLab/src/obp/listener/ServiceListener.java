/**
 * 
 */
package obp.listener;

import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import org.joda.time.DateTime;

import obp.ServiceConfiguration;
import obp.SoundUtils;
import obp.service.Constants;
import odp.service.listener.Listener;
import odp.service.listener.ProximitySighting;
import odp.service.listener.TagSighting;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	private SoundUtils sound = new SoundUtils();
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private final int movingTagId = 1119;
	private final int proximityTagId1 = 1279;
	private final int proximityTagId2 = 382;
	
	private final int readerId = 1259;
	
	private int minReaderStrength = 99;
	private int minProximityStrength = 99;
	
	private int lastSequence;
	private int lastStrength;
	
	private int count = 0;
	private int strength0;
	private int strength1;
	private int strength2;
	private int strength3;
	
	/**
	 * @param dataIndex
	 * @param estimator
	 */
	public ServiceListener() {
	} // Constructor
	
	private void beep(int hz) {
		try {
			SoundUtils.tone(hz, 485, 1);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	} // beep

	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	@Override
	public void messageReceived(TagSighting tagSighting) {
		DateTime now = DateTime.now();
		
		int strength = tagSighting.getStrength();
		if (configuration.isValidReader(tagSighting.getReaderId()) && 
			strength != Constants.NOT_DEFINED) {
			int id = tagSighting.getTagId();
			
			// First signal
//			if (id == movingTagId && tagSighting.getReaderId() == readerId) {
//				beep();
//			}
			
			// Minimal strength
//			if (lastSequence != tagSighting.getTagSequence()) {
//				lastSequence = tagSighting.getTagSequence();
//				lastStrength = strength;
//				
//				count++;
//				
//				switch (strength) {
//				case 0:
//					strength0++;
//					break;
//				case 1:
//					strength1++;
//					break;
//				case 2:
//					strength2++;
//					break;
//				case 3:
//					strength3++;
//					break;
//				default:
//					System.out.println("Unknown strength!");
//					beep(500);
//					break;
//				}
//			
//				if (strength < minReaderStrength) {
//					minReaderStrength = strength;
//					System.out.println("ID: " + id + "|Reader: " + tagSighting.getReaderId() + " (" + strength + ")");
//					
//					beep(500);
//				}
//				
//				if (count == 51) {
//					if (strength0 >= (strength1 - 2) && strength0 <= (strength2 + +2)) {
//						beep(500);
//					} else {
//						beep(750);
//					}
//					
//					System.out.println("ID: " + id + "|Counter: " + count + 
//							"|0: " + strength0 + " (" + ((count / 4) - strength0) + ")" +
//							"|1: " + strength1 + " (" + ((count / 4) - strength1) + ")" +
//							"|2: " + strength2 + " (" + ((count / 4) - strength2) + ")" +
//							"|3: " + strength3 + " (" + ((count / 4) - strength3) + ")");
//					
//					count = 0;
//					strength0 = 0;
//					strength1 = 0;
//					strength2 = 0;
//					strength3 = 0;
//				}
//			}
			
//			// Reader position
//			if (lastSequence != tagSighting.getTagSequence()) {
//				lastSequence = tagSighting.getTagSequence();
//				lastStrength = strength;
//				
//				count++;
//				
//				switch (strength) {
//				case 0:
//					strength0++;
//					break;
//				case 1:
//					strength1++;
//					break;
//				case 2:
//					strength2++;
//					break;
//				case 3:
//					strength3++;
//					break;
//				default:
//					System.out.println("Unknown strength!");
//					beep(500);
//					break;
//				}
//			
//				if (strength < minReaderStrength) {
//					minReaderStrength = strength;
//					System.out.println("ID: " + id + "|Reader: " + tagSighting.getReaderId() + " (" + strength + ")");
//					
//					beep(500);
//				}
//				
//				if (count == 51) {
//					if (strength0 >= (strength1 - 2) && strength0 <= (strength2 + +2)) {
//						beep(500);
//					} else {
//						beep(750);
//					}
//					
//					System.out.println("ID: " + id + "|Counter: " + count + 
//							"|0: " + strength0 + " (" + ((count / 4) - strength0) + ")" +
//							"|1: " + strength1 + " (" + ((count / 4) - strength1) + ")" +
//							"|2: " + strength2 + " (" + ((count / 4) - strength2) + ")" +
//							"|3: " + strength3 + " (" + ((count / 4) - strength3) + ")");
//					
//					count = 0;
//					strength0 = 0;
//					strength1 = 0;
//					strength2 = 0;
//					strength3 = 0;
//				}
//			}
			
//			System.out.println(tagSighting.getTagSequence() + " " + tagSighting.getInterface() + 
//					" " + tagSighting.getTagProtocol() + " " + tagSighting.getStrength());
			
			if (id == movingTagId) {
//				System.out.println("ID: " + id + " Strength: " + tagSighting.getStrength());
				ArrayList<ProximitySighting> sightings = tagSighting.getProximitySightings();
				
				if (sightings != null && sightings.size() > 0) {
					count++;
					for (ProximitySighting sighting : sightings) {
						if (sighting.getTagId() == proximityTagId1 ||
							sighting.getTagId() == proximityTagId2) {
							strength = sighting.getStrength();
							
							System.out.println("ID: " + id + ": Prox1: " + strength + "| Count: " + count);
							switch (sighting.getTagId()) {
							case proximityTagId1:
								beep(500);
								break;
							case proximityTagId2:
								beep(1000);
								break;
							}
							
							if (strength < minProximityStrength) {
								minProximityStrength = strength;
								System.out.println("ID: " + id + "|Prox1: " + strength);
								
								beep(750);
							}
						}
					}
				}
			
//				if (tagSighting.isTagButtonPressed() != null && 
//					tagSighting.isTagButtonPressed()) {
//					System.out.println("ID: " + id + ": Button pressed");
//				}
//				
//				int protocol = tagSighting.getProtocol();
			}
		}
	} // messageReceived
}