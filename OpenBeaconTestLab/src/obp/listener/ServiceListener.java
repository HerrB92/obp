/**
 * 
 */
package obp.listener;

import java.util.ArrayList;

//import org.joda.time.DateTime;


import obp.ServiceConfiguration;
import obp.service.Constants;
import obp.service.tools.SoundUtils;
import odp.service.listener.Listener;
import odp.service.listener.ProximitySighting;
import odp.service.listener.TagSighting;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private final int movingTagId = 1119;
	private final int proximityTagId1 = 1278;
	private final int proximityTagId2 = 382;
	
//	private final int readerId = 1259;
//	
//	private int minReaderStrength = 99;
	private int minProximityStrength = 99;
		
	private int count = 0;
	
	/**
	 * @param dataIndex
	 * @param estimator
	 */
	public ServiceListener() {} // Constructor

	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	@Override
	public void messageReceived(TagSighting tagSighting) {
		//DateTime now = DateTime.now();
		
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
			
//			Class.forName( "com.mysql.jdbc.Driver" );
//			cn = DriverManager.getConnection( "jdbc:mysql://MyDbComputerNameOrIP:3306/myDatabaseName", dbUsr, dbPwd );
			
			// Proximity
			if (id == movingTagId) {
				if (tagSighting.getTagProtocol() == 70) {
					System.out.println(
							"ID: " + id +
							" TagSeq: " + tagSighting.getTagSequence() + 
							" Intrfc: " + tagSighting.getInterface() +
							" Prox: " + tagSighting.getProximitySightings().size() + 
							// " Prot: " + tagSighting.getTagProtocol() + 
							" Strength: " + tagSighting.getStrength());
				}
				
				ArrayList<ProximitySighting> sightings = tagSighting.getProximitySightings();
				int proxId;
				
				if (sightings != null && sightings.size() > 0) {
					count++;
					for (ProximitySighting sighting : sightings) {
						proxId = sighting.getTagId();
						if (proxId == proximityTagId1 ||
							proxId == proximityTagId2) {
							strength = sighting.getStrength();
							
							System.out.println("ID: " + id + ": Prox: " + proxId + "| Count: " + count);
							switch (proxId) {
							case proximityTagId1:
								SoundUtils.setHz(500);
								new SoundUtils().start();
								break;
							case proximityTagId2:
								SoundUtils.setHz(1000);
								new SoundUtils().start();
								break;
							}
							
							if (strength < minProximityStrength) {
								minProximityStrength = strength;
								System.out.println("ID: " + id + "|Strength: " + strength);
								
								SoundUtils.setHz(750);
								new SoundUtils().start();
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