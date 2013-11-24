/**
 * Idea and base code taken from:
 * Bruno, E. (2011) 'Read/Write Properties Files in Java' Dr.Dobb's, 
 * The World of Software Development [Online]
 * Available at: http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005 (Accessed: 01.10.2013)
 */
package obt.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;

import obp.service.Constants;
import obt.configuration.Setting.SettingType;
import obt.persistence.DatabaseSessionFactory;
import obt.spots.Reader;
import obt.spots.RegisterTag;
import obt.spots.Spot;
import obt.spots.SpotTag;
import obt.spots.UnRegisterTag;
import obt.tag.TagKey;

/**
 * Singleton
 * 
 * @author bbehrens
 *
 */
public class ServiceConfiguration {
	private static ServiceConfiguration configuration = null;
	private boolean configurationLoaded = false;
		
	private HashMap<SettingType, Setting> settingsMap = new HashMap<SettingType, Setting>();
		
	private HashMap<String, Reader> readerMap = new HashMap<String, Reader>();
	protected HashMap<String, Spot> spotTagMap = new HashMap<String, Spot>();
	
	protected ArrayList<long[]> tagKeyList = new ArrayList<long[]>();
	
	private HashMap<String, HashMap<String, Long>> readerDistanceMap = 
			new HashMap<String, HashMap<String, Long>>();
	private HashMap<String, HashMap<String, Long>> spotTagDistanceMap = 
			new HashMap<String, HashMap<String, Long>>();
	
	private int maxX = 0;
	private int maxY = 0;
	
	private ServiceConfiguration() {}
	
	public static ServiceConfiguration getInstance() {
		if (configuration == null) {
			configuration = new ServiceConfiguration();
			configuration.loadConfiguration();
			
//			configuration.addReader(1259, "Spider", 1, 1, 1, 1, 1); 		// Sleeping room, Window
//			configuration.addReader(1391, "Sleeper", 2, 1, 1, 640, 620); 	// Sleeping Room
//			configuration.addReader(1291, "Relax", 3, 1, 1, 1, 1165); 		// Living Room
//			configuration.addReader(1300, "Cooking", 4, 1, 1, 610, 1395); 	// Kitchen
		}
		
		return configuration;
	} // getInstance
	
	private void updateMaxDimension(int x, int y) {
		if (x > maxX) {
			setMaxX(x);
		}
		
		if (y > maxY) {
			setMaxY(y);
		}
	} // updateMaxDimension
	
	private void loadConfiguration() {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
		// Settings
		List<?> settings = session.createQuery("from Setting").list();
		
		settingsMap.clear();
		Setting setting;
		for (Object object : settings) {
			setting = (Setting)object;
			settingsMap.put(setting.getSettingType(), setting);
		}
		
		// Readers
		List<?> readers = session.createQuery("from Reader where active = true").list();
		
		readerMap.clear();
		Reader reader;
		for (Object object : readers) {
			reader = (Reader)object;
			addReader(reader);
			updateMaxDimension(reader.getX(), reader.getY());
		}
		
		// Spots
		spotTagMap.clear();
		
		// SpotTags
		SpotTag spotTag;
		
		List<?> spots = session.createQuery("from SpotTag where active = true").list();
		for (Object object : spots) {
			spotTag = (SpotTag)object;
			addSpot(spotTag);
			updateMaxDimension(spotTag.getX(), spotTag.getY());
		}
		
		// RegisterTags
		RegisterTag registerTag;
		List<?> registerTags = session.createQuery("from RegisterTag where active = true").list();
		for (Object object : registerTags) {
			registerTag = (RegisterTag)object;
			addSpot(registerTag);
			updateMaxDimension(registerTag.getX(), registerTag.getY());
		}
		
		// UnRegisterTags
		UnRegisterTag unRegisterTag;
		List<?> unUnRegisterTags = session.createQuery("from UnRegisterTag where active = true").list();
		for (Object object : unUnRegisterTags) {
			unRegisterTag = (UnRegisterTag)object;
			addSpot(unRegisterTag);
			updateMaxDimension(unRegisterTag.getX(), unRegisterTag.getY());
		}
		
		// TagKeys
		List<?> tagKeys = session.createQuery("from TagKey where active = true").list();
		
		tagKeyList.clear();
		TagKey tagKey;
		for (Object object : tagKeys) {
			tagKey = (TagKey)object;
			tagKeyList.add(tagKey.getTagKey());
		}
		
		// Add default tag key, if no key has been provided from configuration
		if (tagKeyList.size() == 0) {
			tagKeyList.add(new long[]{0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff});
		}
		
		session.getTransaction().commit();
		
		configurationLoaded = true;
	} // loadConfiguration
		
	private int getIntValue(SettingType settingType) {
		if (settingsMap.containsKey(settingType)) {
			return settingsMap.get(settingType).getIntValue();
		}
		
		return settingType.getDefaultIntValue();
	} // getIntValue

	/**
	 * @return the tagButtonActiveSeconds
	 */
	public int getTagButtonActiveSeconds() {
		return getIntValue(SettingType.TagButtonActiveSeconds);
	} // getTagButtonActiveSeconds

	/**
	 * @return the tagReaderSightingActiveSeconds
	 */
	public int getTagReaderSightingActiveSeconds() {
		return getIntValue(SettingType.TagReaderSightingActiveSeconds);
	} // getTagReaderSightingActiveSeconds
	
	/**
	 * @return the tagSpotTagSightingActiveSeconds
	 */
	public int getTagSpotTagSightingActiveSeconds() {
		return getIntValue(SettingType.TagSpotTagSightingActiveSeconds);
	} // getTagSpotTagSightingActiveSeconds
	
	/**
	 * @return the tagProximitySightingActiveSeconds
	 */
	public int getTagProximitySightingActiveSeconds() {
		return getIntValue(SettingType.TagProximitySightingActiveSeconds);
	} // getTagProximitySightingActiveSeconds

	/**
	 * @return the strengthAggregationWindowSeconds
	 */
	public int getStrengthAggregationWindowSeconds() {
		return getIntValue(SettingType.StrengthAggregationWindowSeconds);
	} // getStrengthAggregationWindowSeconds

	/**
	 * @return the strengthAggregationAgedSeconds
	 */
	public int getStrengthAggregationAgedSeconds() {
		return getIntValue(SettingType.StrengthAggregationAgedSeconds);
	} // getStrengthAggregationAgedSeconds

	/**
	 * @return the tagDataKey
	 */
	public ArrayList<long[]> getTagKeyList() {
		return tagKeyList;
	} // getTagKeyList
	
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
	
	protected void addReader(Reader newReader) {
		if (!readerMap.containsKey(newReader.getKey())) {
			HashMap<String, Long> distances = new HashMap<String, Long>();
			
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
				Set<Entry<String, HashMap<String, Long>>> distanceSet = readerDistanceMap.entrySet();
				for (Entry<String, HashMap<String, Long>> distanceEntry : distanceSet) {
					reader = getReader(distanceEntry.getKey());
					
					if (reader.getFloor() == newReader.getFloor() && 
						reader.getGroup() == newReader.getGroup()) {
						distance = calcDistance(newReader.getX(), newReader.getY(), reader.getX(), reader.getY());
					} else {
						distance = Long.valueOf(Constants.NOT_DEFINED);
					}
					
					// Add distance information to existing reader
					distanceEntry.getValue().put(newReader.getKey(), distance);
					
					// Add distance information to new reader
					distances.put(distanceEntry.getKey(), distance);
				}
			}
			
			// Add reader to reader and reader distance map

			readerMap.put(newReader.getKey(), newReader);
			readerDistanceMap.put(newReader.getKey(), distances);
		}
	} // addReader
	
	protected HashMap<String, Reader> getReaderMap() {
		return readerMap;
	} // getReaders
	
	public Collection<Reader> getReaders() {
		return getReaderMap().values();
	} // getReaders
	
	public Reader getReader (String key) {
		return getReaderMap().get(key);
	} // getReader
	
	public long getReaderDistance(String readerKey1, String readerKey2) {
		try {
			return readerDistanceMap.get(readerKey1).get(readerKey2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getReaderDistance
	
	public boolean isValidReader(String key) {
		return getReaderMap().containsKey(key);
	} // isValidReader
	
	protected void addSpot(Spot newSpotTag) {
		if (!spotTagMap.containsKey(newSpotTag.getKey())) {
			HashMap<String, Long> distances = new HashMap<String, Long>();
			
			// If there are other spots, calculate distance between each spot
			// and store information in hash map for faster access
			if (spotTagMap.size() > 1) {
				Spot tag;
				Long distance;
				
				// The spotTagDistanceMap stores the distance as
				// HashMap<TagKey1, HashMap<TagKey2, Distance>>
				// Get through the list of existing relations and add
				// new relation for new spot tag, if the spot tag is on
				// the same room, floor and group.
				Set<Entry<String, HashMap<String, Long>>> distanceSet = 
						spotTagDistanceMap.entrySet();
				
				for (Entry<String, HashMap<String, Long>> distanceEntry : distanceSet) {
					tag = getSpot(distanceEntry.getKey());
					
					if (tag.getFloor() == newSpotTag.getFloor() && 
						tag.getGroup() == newSpotTag.getGroup()) {
						distance = calcDistance(newSpotTag.getX(), newSpotTag.getY(), tag.getX(), tag.getY());
					} else {
						distance = Long.valueOf(Constants.NOT_DEFINED);
					}
					
					// Add distance information to existing reader
					distanceEntry.getValue().put(newSpotTag.getKey(), distance);
					
					// Add distance information to new reader
					distances.put(distanceEntry.getKey(), distance);
				}
			}
			
			// Add spot tag to spot tag and spot tag distance map
			spotTagMap.put(newSpotTag.getKey(), newSpotTag);
			spotTagDistanceMap.put(newSpotTag.getKey(), distances);
		}
	} // addSpotTag
	
	protected HashMap<String, Spot> getSpotTagMap() {
		return spotTagMap;
	} // getSpotTagMap
	
	public Collection<Spot> getSpotTags() {
		return getSpotTagMap().values();
	} // getSpotTags
	
	public Spot getSpot (String key) {
		return getSpotTagMap().get(key);
	} // getSpotTag
	
	public boolean isSpotTag (String key) {
		return getSpotTagMap().containsKey(key);
	} // isValidSpotTag
	
	public boolean isRegisterTag (String key) {
		if (isSpotTag(key)) {
			if (getSpotTagMap().get(key) instanceof RegisterTag) {
				return true;
			}
		}
		return false;
	} // isRegisterTag
	
	public boolean isUnRegisterTag (String key) {
		if (isSpotTag(key)) {
			if (getSpotTagMap().get(key) instanceof UnRegisterTag) {
				return true;
			}
		}
		return false;
	} // isUnRegisterTag
	
	public long getSpotDistance(String tagKey1, String tagKey2) {
		try {
			return spotTagDistanceMap.get(tagKey1).get(tagKey2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getSpotTagDistance	

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maxX to set
	 */
	private void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY the maxY to set
	 */
	private void setMaxY(int maxY) {
		this.maxY = maxY;
	}
}