/**
 * Idea and base code taken from:
 * Bruno, E. (2011) 'Read/Write Properties Files in Java' Dr.Dobb's, 
 * The World of Software Development [Online]
 * Available at: http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005 (Accessed: 01.10.2013)
 */
package obp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import obp.service.Constants;
import obp.spots.Reader;
import obp.spots.SpotTag;
import obp.tag.Tag;

/**
 * Singleton
 * 
 * @author bbehrens
 *
 */
public class ServiceConfiguration {
	private static final String CONFIGURATION_FILE_NAME = "ServiceConfiguration.xml";
	
	private static ServiceConfiguration configuration = null;
	private boolean configurationLoaded = false;
	
	private int tagButtonActiveSeconds = 5;
	private int tagReaderSightingActiveSeconds = 5;
	private int tagSpotTagSightingActiveSeconds = 2;
	private int tagProximitySightingActiveSeconds = 5;
	private int strengthAggregationWindowSeconds = 2;
	private int strengthAggregationAgedSeconds = strengthAggregationWindowSeconds + 2;
	
	private long[] tagDataKey = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	
	private HashMap<Integer, Reader> readerMap = new HashMap<Integer, Reader>();
	private HashMap<Integer, SpotTag> spotTagMap = new HashMap<Integer, SpotTag>();
	private HashMap<Integer, HashMap<Integer, Long>> readerDistanceMap = 
			new HashMap<Integer, HashMap<Integer, Long>>();
	private HashMap<Integer, HashMap<Integer, Long>> spotTagDistanceMap = 
			new HashMap<Integer, HashMap<Integer, Long>>();
	
	protected HashMap<Integer, Tag> registrationTags = new HashMap<Integer, Tag>();
	protected HashMap<Integer, Tag> deRegistrationTags = new HashMap<Integer, Tag>();
	
	private ServiceConfiguration() {}
	
	public static ServiceConfiguration getInstance() {
		if (configuration == null) {
			configuration = new ServiceConfiguration();
			configuration.loadConfiguration();
			
			configuration.addReader(1259, "Spider", 1, 1, 1, 1, 1); 		// Sleeping room, Window
			configuration.addReader(1391, "Sleeper", 2, 1, 1, 640, 620); 	// Sleeping Room
			configuration.addReader(1291, "Relax", 3, 1, 1, 1, 1165); 		// Living Room
			configuration.addReader(1300, "Cooking", 4, 1, 1, 610, 1395); 	// Kitchen
			
			configuration.addSpotTag(1279, "Weird", 1, 1, 1, 10, 1165);
			
//			configuration.addReader(1300, 1, 1, 1, 1, 1);
//			configuration.addReader(1291, 1, 1, 1, 1, 550);
//			configuration.addReader(1259, 1, 1, 1, 400, 550);
		}
		
		return configuration;
	} // getInstance
	
	private void loadConfiguration() {
	    Properties properties = new Properties();
	    InputStream inputStream = null;
	 
	    // First try loading from the current directory
	    try {
	        inputStream = new FileInputStream(new File(CONFIGURATION_FILE_NAME));
	    } catch ( Exception e ) {
	    	inputStream = null;
	    }
	 
	    try {
	        if (inputStream == null) {
	            // Try loading from class path
	            inputStream = getClass().getResourceAsStream(CONFIGURATION_FILE_NAME);
	        }
	    } catch (Exception e) {
	    	inputStream = null;
	    }
	    
	    if (inputStream != null) {
	    	//try {
	    		// Try loading properties from the file (if found)
	            try {
					properties.load(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            tagButtonActiveSeconds = 
	            	new Integer(properties.getProperty("TAG_BUTTON_ACTIVE_SECONDS", "5"));
	            tagReaderSightingActiveSeconds = 
	            	new Integer(properties.getProperty("TAG_READER_SIGHTING_ACTIVE_SECONDS", "5"));
	            tagSpotTagSightingActiveSeconds = 
			        new Integer(properties.getProperty("TAG_SPOT_SIGHTING_ACTIVE_SECONDS", "2"));
	            tagProximitySightingActiveSeconds = 
		            new Integer(properties.getProperty("TAG_PROXIMITY_SIGHTING_ACTIVE_SECONDS", "5"));
	            strengthAggregationWindowSeconds  = 
	            	new Integer(properties.getProperty("STRENGTH_AGGREGATION_WINDOW_SECONDS", "2"));
	            strengthAggregationAgedSeconds  = 
	            	new Integer(properties.getProperty("STRENGTH_AGGREGATION_AGED_SECONDS", "" + (strengthAggregationWindowSeconds + 2)));
	            
	            tagDataKey[0] = Long.decode(properties.getProperty("TAG_DATA_KEY1", "0x00112233"));
	            tagDataKey[1] = Long.decode(properties.getProperty("TAG_DATA_KEY2", "0x44556677"));
	            tagDataKey[2] = Long.decode(properties.getProperty("TAG_DATA_KEY3", "0x8899aabb"));
	            tagDataKey[3] = Long.decode(properties.getProperty("TAG_DATA_KEY4", "0xccddeeff"));
	            
	            configurationLoaded = true;
	    	//} catch (Exception e) { System.out.println(e.getMessage());}
	    }
	} // loadConfiguration
	
	public void saveConfiguration() {
		try {
			Properties properties = new Properties();
			properties.setProperty("TAG_BUTTON_ACTIVE_SECONDS", Integer.toString(tagButtonActiveSeconds));
			properties.setProperty("TAG_READER_SIGHTING_ACTIVE_SECONDS", Integer.toString(tagReaderSightingActiveSeconds));
			properties.setProperty("TAG_SPOT_SIGHTING_ACTIVE_SECONDS", Integer.toString(tagSpotTagSightingActiveSeconds));
			properties.setProperty("TAG_PROXIMITY_SIGHTING_ACTIVE_SECONDS", Integer.toString(tagProximitySightingActiveSeconds));
			properties.setProperty("STRENGTH_AGGREGATION_WINDOW_SECONDS", Integer.toString(strengthAggregationWindowSeconds));
			properties.setProperty("STRENGTH_AGGREGATION_AGED_SECONDS", Integer.toString(strengthAggregationAgedSeconds));
			
			properties.setProperty("TAG_DATA_KEY1", String.format("0x%08x", (0xFFFFFFFF & tagDataKey[0])));
			properties.setProperty("TAG_DATA_KEY2", String.format("0x%08x", (0xFFFFFFFF & tagDataKey[1])));
			properties.setProperty("TAG_DATA_KEY3", String.format("0x%08x", (0xFFFFFFFF & tagDataKey[2])));
			properties.setProperty("TAG_DATA_KEY4", String.format("0x%08x", (0xFFFFFFFF & tagDataKey[3])));
			
	        OutputStream out = new FileOutputStream(new File(CONFIGURATION_FILE_NAME));
	        properties.storeToXML(out, "OpenBeaconPackage configuration file - do not alter the file while the service is running");
	    } catch (Exception e ) {
	        e.printStackTrace();
	    }
	} // saveConfiguration

	/**
	 * @return the tagButtonActiveSeconds
	 */
	public int getTagButtonActiveSeconds() {
		return tagButtonActiveSeconds;
	} // getTagButtonActiveSeconds

	/**
	 * @param tagButtonActiveSeconds the tagButtonActiveSeconds to set
	 */
	public void setTagButtonActiveSeconds(int tagButtonActiveSeconds) {
		this.tagButtonActiveSeconds = tagButtonActiveSeconds;
	} // setTagButtonActiveSeconds

	/**
	 * @return the tagReaderSightingActiveSeconds
	 */
	public int getTagReaderSightingActiveSeconds() {
		return tagReaderSightingActiveSeconds;
	} // getTagReaderSightingActiveSeconds

	/**
	 * @param tagReaderSightingActiveSeconds the tagReaderSightingActiveSeconds to set
	 */
	public void setTagReaderSightingActiveSeconds(int tagReaderSightingActiveSeconds) {
		this.tagReaderSightingActiveSeconds = tagReaderSightingActiveSeconds;
	} // setTagReaderSightingActiveSeconds
	
	/**
	 * @return the tagSpotTagSightingActiveSeconds
	 */
	public int getTagSpotTagSightingActiveSeconds() {
		return tagSpotTagSightingActiveSeconds;
	} // getTagSpotTagSightingActiveSeconds

	/**
	 * @param tagSpotTagSightingActiveSeconds the tagSpotTagSightingActiveSeconds to set
	 */
	public void setTagSpotTagSightingActiveSeconds(int tagSpotTagSightingActiveSeconds) {
		this.tagSpotTagSightingActiveSeconds = tagSpotTagSightingActiveSeconds;
	} // setTagSpotTagSightingActiveSeconds
	
	/**
	 * @return the tagProximitySightingActiveSeconds
	 */
	public int getTagProximitySightingActiveSeconds() {
		return tagProximitySightingActiveSeconds;
	} // getTagProximitySightingActiveSeconds

	/**
	 * @param tagProximitySightingActiveSeconds the tagProximitySightingActiveSeconds to set
	 */
	public void setTagProximitySightingActiveSeconds(int tagProximitySightingActiveSeconds) {
		this.tagProximitySightingActiveSeconds = tagProximitySightingActiveSeconds;
	} // setTagProximitySightingActiveSeconds

	/**
	 * @return the strengthAggregationWindowSeconds
	 */
	public int getStrengthAggregationWindowSeconds() {
		return strengthAggregationWindowSeconds;
	} // getStrengthAggregationWindowSeconds

	/**
	 * @param strengthAggregationWindowSeconds the strengthAggregationWindowSeconds to set
	 */
	public void setStrengthAggregationWindowSeconds(
			int strengthAggregationWindowSeconds) {
		this.strengthAggregationWindowSeconds = strengthAggregationWindowSeconds;
	} // setStrengthAggregationWindowSeconds

	/**
	 * @return the strengthAggregationAgedSeconds
	 */
	public int getStrengthAggregationAgedSeconds() {
		return strengthAggregationAgedSeconds;
	} // getStrengthAggregationAgedSeconds

	/**
	 * @param strengthAggregationAgedSeconds the strengthAggregationAgedSeconds to set
	 */
	public void setStrengthAggregationAgedSeconds(int strengthAggregationAgedSeconds) {
		this.strengthAggregationAgedSeconds = strengthAggregationAgedSeconds;
	} // setStrengthAggregationAgedSeconds

	/**
	 * @return the tagDataKey
	 */
	public long[] getTagDataKey() {
		return tagDataKey;
	} // getTagDataKey

	/**
	 * @param tagDataKey the tagDataKey to set
	 */
	public void setTagDataKey(long[] tagDataKey) {
		this.tagDataKey = tagDataKey;
	} // setTagDataKey
	
	/**
	 * @return True, if the configuration has been loaded from file
	 *         False, otherwise
	 */
	public boolean getConfigurationLoaded() {
		return configurationLoaded;
	} // getConfigurationLoaded
	
	private Long calcDistance(int x1, int y1, int x2, int y2) {
		// Pythagoras: a2 + b2 = c2
		return Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
	} // calcDistance
	
	public void addReader(Integer id, String name, int room, int floor, int group, int x, int y) {
		if (!readerMap.containsKey(id)) {
			HashMap<Integer, Long> distances = new HashMap<Integer, Long>();
			
			// If there are other readers, calculate distance between each reader
			// and store information in hash map for faster access
			if (readerMap.size() > 1) {
				Reader reader;
				Long distance;
				
				// The readerDistanceMap stores the distance as
				// HashMap<ReaderId1, HashMap<ReaderId2, Distance>>
				// Get through the list of existing relations and add
				// new relation for new reader, if the reader is on
				// the same room, floor and group.
				Set<Entry<Integer, HashMap<Integer, Long>>> distanceSet = readerDistanceMap.entrySet();
				for (Entry<Integer, HashMap<Integer, Long>> distanceEntry : distanceSet) {
					reader = getReader(distanceEntry.getKey());
					
					if (reader.getFloor() == floor && reader.getGroup() == group) {
						distance = calcDistance(x, reader.getX(), y, reader.getY());
					} else {
						distance = Long.valueOf(Constants.NOT_DEFINED);
					}
					
					// Add distance information to existing reader
					distanceEntry.getValue().put(id, distance);
					
					// Add distance information to new reader
					distances.put(distanceEntry.getKey(), distance);
				}
			}
			
			// Add reader to reader and reader distance map
			readerMap.put(id, new Reader(id, name, room, floor, group, x, y));
			readerDistanceMap.put(id, distances);
		}
	} // addReader
	
	protected HashMap<Integer, Reader> getReaderMap() {
		return readerMap;
	} // getReaders
	
	public Collection<Reader> getReaders() {
		return getReaderMap().values();
	} // getReaders
	
	public Reader getReader (Integer id) {
		return getReaderMap().get(id);
	} // getReader
	
	public long getReaderDistance(Integer readerId1, Integer readerId2) {
		try {
			return readerDistanceMap.get(readerId1).get(readerId2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getReaderDistance
	
	public boolean isValidReader(Integer id) {
		return getReaderMap().containsKey(id);
	} // isValidReader
	
	public void addSpotTag(Integer id, String name, int room, int floor, int group, int x, int y) {
		if (!spotTagMap.containsKey(id)) {
			HashMap<Integer, Long> distances = new HashMap<Integer, Long>();
			
			// If there are other readers, calculate distance between each reader
			// and store information in hash map for faster access
			if (spotTagMap.size() > 1) {
				SpotTag tag;
				Long distance;
				
				// The spotTagDistanceMap stores the distance as
				// HashMap<TagId1, HashMap<TagId2, Distance>>
				// Get through the list of existing relations and add
				// new relation for new spot tag, if the spot tag is on
				// the same room, floor and group.
				Set<Entry<Integer, HashMap<Integer, Long>>> distanceSet = spotTagDistanceMap.entrySet();
				for (Entry<Integer, HashMap<Integer, Long>> distanceEntry : distanceSet) {
					tag = getSpotTag(distanceEntry.getKey());
					
					if (tag.getFloor() == floor && tag.getGroup() == group) {
						distance = calcDistance(x, tag.getX(), y, tag.getY());
					} else {
						distance = Long.valueOf(Constants.NOT_DEFINED);
					}
					
					// Add distance information to existing reader
					distanceEntry.getValue().put(id, distance);
					
					// Add distance information to new reader
					distances.put(distanceEntry.getKey(), distance);
				}
			}
			
			// Add spot tag to spot tag and spot tag distance map
			spotTagMap.put(id, new SpotTag(id, name, room, floor, group, x, y));
			spotTagDistanceMap.put(id, distances);
		}
	} // addSpotTag
	
	protected HashMap<Integer, SpotTag> getSpotTagMap() {
		return spotTagMap;
	} // getSpotTagMap
	
	public Collection<SpotTag> getSpotTags() {
		return getSpotTagMap().values();
	} // getSpotTags
	
	public SpotTag getSpotTag (Integer id) {
		return getSpotTagMap().get(id);
	} // getSpotTag
	
	public boolean isValidSpotTag (Integer id) {
		return getSpotTagMap().containsKey(id);
	} // isValidSpotTag
	
	public long getSpotTagDistance(Integer tagId1, Integer tagId2) {
		try {
			return spotTagDistanceMap.get(tagId1).get(tagId2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getSpotTagDistance
}