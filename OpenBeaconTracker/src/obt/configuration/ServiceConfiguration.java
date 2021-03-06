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
package obt.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import obs.service.Constants;
import obt.configuration.Setting.SettingType;
import obt.persistence.DatabaseSessionFactory;
import obt.spots.Reader;
import obt.spots.RegisterTag;
import obt.spots.Spot;
import obt.spots.SpotTag;
import obt.spots.UnRegisterTag;
import obt.tag.TagKey;

/**
 * Singleton class which loads the available configuration data from the
 * database on first load.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class ServiceConfiguration {
	// Instance
	private static ServiceConfiguration configuration = null;
	
	// Information, if configuration has been loaded
	private boolean configurationLoaded = false;
	
	// Data maps for settings, reader and spot tags
	private HashMap<SettingType, Setting> settingsMap = new HashMap<SettingType, Setting>();
	private HashMap<String, Reader> readerMap = new HashMap<String, Reader>();
	private HashMap<String, Spot> spotTagMap = new HashMap<String, Spot>();
	
	// List of tag encryption keys. Each key consists of an array
	// of four long values.
	private ArrayList<long[]> tagKeyList = new ArrayList<long[]>();
	
	// Helper maps to store pre-calculated distances between the 
	// readers and spot tags.
	private HashMap<String, HashMap<String, Long>> readerDistanceMap = 
			new HashMap<String, HashMap<String, Long>>();
	private HashMap<String, HashMap<String, Long>> spotTagDistanceMap = 
			new HashMap<String, HashMap<String, Long>>();
	
	// Maximum X and Y values over all reader and spot tag positions.
	private int maxX = 0;
	private int maxY = 0;
	
	// Unregistration x- and y-values for a forced unregistration. Determined by
	// the first loaded unregister tag position.
	private int forcedUnRegisterX = Constants.NOT_DEFINED;
	private int forcedUnRegisterY = Constants.NOT_DEFINED;
	
	/**
	 * Private constructor (singleton)
	 */
	private ServiceConfiguration() {}
	
	/**
	 * Loads configuration from database on first request and
	 * returns created instance.
	 * 
	 * @return Instance of ServiceConfiguration class
	 */
	public static ServiceConfiguration getInstance() {
		if (configuration == null) {
			configuration = new ServiceConfiguration();
			configuration.loadConfiguration();
		}
		
		return configuration;
	} // getInstance
	
	/**
	 * Helper method to determine maximum x and y-values.
	 * 
	 * @param x
	 * @param y
	 */
	private void updateMaxDimension(int x, int y) {
		if (x > maxX) {
			setMaxX(x);
		}
		
		if (y > maxY) {
			setMaxY(y);
		}
	} // updateMaxDimension
	
	/**
	 * Loads settings, reader, spot tag and encryption key data
	 * from database.
	 */
	private void loadConfiguration() {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
		// Settings
		List<?> settings = session.createQuery("from Setting where runId = 0").list();
		
		settingsMap.clear();
		Setting setting;
		for (Object object : settings) {
			setting = (Setting)object;
			settingsMap.put(setting.getSettingType(), setting);
		}
		
		// Readers
		List<?> readers = session.createQuery("from Reader where runId = 0 and active = true").list();
		
		readerMap.clear();
		Reader reader;
		for (Object object : readers) {
			reader = (Reader)object;
			addReader(reader);
		}
		
		// Spots
		spotTagMap.clear();
		
		// SpotTags
		SpotTag spotTag;
		
		List<?> spots = session.createQuery("from SpotTag where runId = 0 and active = true").list();
		for (Object object : spots) {
			spotTag = (SpotTag)object;
			addSpot(spotTag);
		}
		
		// RegisterTags
		RegisterTag registerTag;
		List<?> registerTags = session.createQuery("from RegisterTag where runId = 0 and active = true").list();
		for (Object object : registerTags) {
			registerTag = (RegisterTag)object;
			addSpot(registerTag);
		}
		
		// UnRegisterTags
		UnRegisterTag unRegisterTag;
		List<?> unUnRegisterTags = session.createQuery("from UnRegisterTag where runId = 0 and active = true").list();
		for (Object object : unUnRegisterTags) {
			unRegisterTag = (UnRegisterTag)object;
			addSpot(unRegisterTag);
			
			if (getForcedUnRegisterX() == Constants.NOT_DEFINED) {
				setForcedUnRegisterX(unRegisterTag.getX());
				setForcedUnRegisterY(unRegisterTag.getY());
			}
		}
		
		// TagKeys
		List<?> tagKeys = session.createQuery("from TagKey where runId = 0 and active = true").list();
		
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
	
	/**
	 * Method stores current configuration within the same tables
	 * using the given runId.
	 * 
	 * FIXME: The code of this method should be improved using
	 * Hibernate object logic (no plain SQL - which is not updated, 
	 * if the class definition changes).
	 */
	public void storeReplayInformation(Long runId) {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
		String sql = "";
		
		// Settings
		sql = 
			"INSERT INTO Settings (runId, settingType, value)" +  
			"SELECT :runId, settingType, value " +
			"  FROM Settings " +
			" WHERE runId = 0 ";
		 
		SQLQuery query;
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
		
		// Readers
		sql = 
			"INSERT INTO Readers (runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active)" +  
			"SELECT :runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active " +
			"  FROM Readers " +
			" WHERE runId = 0 AND active = 1";
		 
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
		
		// SpotTags
		sql = 
			"INSERT INTO SpotTags (runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active)" +  
			"SELECT :runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active " +
			"  FROM SpotTags " +
			" WHERE runId = 0 AND active = 1";
		 
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
		
		// RegisterTags
		sql = 
			"INSERT INTO RegisterTags (runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active)" +  
			"SELECT :runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active " +
			"  FROM RegisterTags " +
			" WHERE runId = 0 AND active = 1";
		 
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
		
		// UnRegisterTags
		sql = 
			"INSERT INTO UnRegisterTags (runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active)" +  
			"SELECT :runId, id, floor, SpotGroup, lastSeen, name, room, x, y, active " +
			"  FROM UnRegisterTags " +
			" WHERE runId = 0 AND active = 1";
		 
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
		
		// TagKeys
		sql = 
			"INSERT INTO TagKeys (runId, name, active, keyPart1, keyPart2, keyPart3, keyPart4)" +  
			"SELECT :runId, name, active, keyPart1, keyPart2, keyPart3, keyPart4 " +
			"  FROM TagKeys " +
			" WHERE runId = 0 AND active = 1";
		 
		query = session.createSQLQuery(sql);
		query.setParameter("runId", runId);
		query.executeUpdate();
				
		session.getTransaction().commit();
	} // storeReplayInformation
		
	/**
	 * Return integer value for the specified setting.
	 * 
	 * @param settingType
	 * @return Integer value for the specified setting
	 */
	private int getIntValue(SettingType settingType) {
		if (settingsMap.containsKey(settingType)) {
			return settingsMap.get(settingType).getIntValue();
		}
		
		return settingType.getDefaultIntValue();
	} // getIntValue
	
	/**
	 * @return the tagAgedSeconds
	 */
	public int getTagAgedSeconds() {
		return getIntValue(SettingType.TagAgedSeconds);
	} // getTagAgedSeconds

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
	 * @return ArrayList<long[]> The list of encryption keys
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
	
	/**
	 * Calculate the distance between x1, y1 and x2, y2 using
	 * Pythagoras (a^2 + b^2 = c^2).
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Long Distance
	 */
	private Long calcDistance(int x1, int y1, int x2, int y2) {
		// Pythagoras: a^2 + b^2 = c^2
		return Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
	} // calcDistance
	
	/**
	 * Add a reader to the reader map. Calculate the distances between already
	 * added readers and the new reader and store the value in the distance 
	 * map.
	 * 
	 * @param newReader
	 */
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
			
			updateMaxDimension(newReader.getX(), newReader.getY());
		}
	} // addReader
	
	/**
	 * @return HashMap<String, Reader> Map of readers
	 */
	protected HashMap<String, Reader> getReaderMap() {
		return readerMap;
	} // getReaders
	
	/**
	 * @return Collection<Reader> List of readers stored in the readers map
	 */
	public Collection<Reader> getReaders() {
		return getReaderMap().values();
	} // getReaders
	
	/**
	 * Get reader with the given key ("R...").
	 * 
	 * @param key
	 * @return Reader Reader object or null
	 */
	public Reader getReader(String key) {
		return getReaderMap().get(key);
	} // getReader
	
	/**
	 * Get distance between the given readers from the distance map.
	 * 
	 * @param readerKey1
	 * @param readerKey2
	 * @return long Distance
	 */
	public long getReaderDistance(String readerKey1, String readerKey2) {
		try {
			return readerDistanceMap.get(readerKey1).get(readerKey2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getReaderDistance
	
	/**
	 * @param key
	 * @return boolean True, if the given key exists as reader key;
	 * 				   False, otherwise
	 */
	public boolean isValidReader(String key) {
		return getReaderMap().containsKey(key);
	} // isValidReader
	
	/**
	 * Add a spot tag to the spot tag map. Calculate the distances between 
	 * already added spot tags and the new spot tag and store the value in 
	 * the distance map.
	 * 
	 * @param newSpotTag
	 */
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
			
			updateMaxDimension(newSpotTag.getX(), newSpotTag.getY());
		}
	} // addSpotTag
	
	/**
	 * @return The spot tag map
	 */
	protected HashMap<String, Spot> getSpotTagMap() {
		return spotTagMap;
	} // getSpotTagMap
	
	/**
	 * @return The list of spot tags as stored in the spot tag map.
	 */
	public Collection<Spot> getSpotTags() {
		return getSpotTagMap().values();
	} // getSpotTags
	
	/**
	 * Return the tag with the given key ("T...").
	 * 
	 * @param key
	 * @return Spot Spot tag with the given key or null
	 */
	public Spot getSpot(String key) {
		return getSpotTagMap().get(key);
	} // getSpotTag
	
	/**
	 * Determine, if the given key is associated with a spot tag.
	 * 
	 * @param key
	 * @return boolean	True, if the given key exists as spot tag key;
	 * 					False, otherwise 
	 */
	public boolean isSpotTag(String key) {
		return getSpotTagMap().containsKey(key);
	} // isValidSpotTag
	
	/**
	 * Determine, if the given key is associated with a register tag.
	 * 
	 * @param key
	 * @return boolean	True, if the given key identifies a register tag;
	 * 					False, otherwise 
	 */
	public boolean isRegisterTag(String key) {
		if (isSpotTag(key)) {
			if (getSpotTagMap().get(key) instanceof RegisterTag) {
				return true;
			}
		}
		return false;
	} // isRegisterTag
	
	/**
	 * Determine, if the given key is associated with an unregister tag.
	 * 
	 * @param key
	 * @return boolean	True, if the given key identifies an unregister tag;
	 * 					False, otherwise 
	 */
	public boolean isUnRegisterTag(String key) {
		if (isSpotTag(key)) {
			if (getSpotTagMap().get(key) instanceof UnRegisterTag) {
				return true;
			}
		}
		return false;
	} // isUnRegisterTag
	
	/**
	 * Return the distance between the tags with the given keys from
	 * the distance map or Constants.NOT_DEFINED, if there is no
	 * distance value available.
	 * 
	 * @param tagKey1
	 * @param tagKey2
	 * @return long Distance between the given tags or
	 * 				Constants.NOT_DEFINED
	 */
	public long getSpotDistance(String tagKey1, String tagKey2) {
		try {
			return spotTagDistanceMap.get(tagKey1).get(tagKey2);
		} catch (Exception e) {
			return Long.valueOf(Constants.NOT_DEFINED);
		}
	} // getSpotTagDistance	

	/**
	 * MaxX and maxY are the greatest x- and y-values given
	 * as spot tag, register tag, unregister tag or readers.
	 * 
	 * @return the maxX value
	 */
	public int getMaxX() {
		return maxX;
	} // getMaxX

	/**
	 * MaxX and maxY are the greatest x- and y-values given
	 * as spot tag, register tag, unregister tag or readers.
	 * 
	 * @param maxX the maxX value to set
	 */
	private void setMaxX(int maxX) {
		this.maxX = maxX;
	} // setMaxX

	/**
	 * MaxX and maxY are the greatest x- and y-values given
	 * as spot tag, register tag, unregister tag or readers.
	 * 
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	} // getMaxY

	/**
	 * MaxX and maxY are the greatest x- and y-values given
	 * as spot tag, register tag, unregister tag or readers.
	 * 
	 * @param maxY the maxY to set
	 */
	private void setMaxY(int maxY) {
		this.maxY = maxY;
	} // setMaxY

	/**
	 * ForcedUnRegisterX and ForcedUnRegisterY are the x- and y-
	 * values of the first loaded unregister tag. These positions are
	 * used, if a moving tag is automatically unregistered.
	 * 
	 * @return the forcedUnRegisterX
	 */
	public int getForcedUnRegisterX() {
		return forcedUnRegisterX;
	} // getForcedUnRegisterX

	/**
	 * ForcedUnRegisterX and ForcedUnRegisterY are the x- and y-
	 * values of the first loaded unregister tag. These positions are
	 * used, if a moving tag is automatically unregistered.
	 * 
	 * @param forcedUnRegisterX the forcedUnRegisterX to set
	 */
	private void setForcedUnRegisterX(int forcedUnRegisterX) {
		this.forcedUnRegisterX = forcedUnRegisterX;
	} // setForcedUnRegisterX

	/**
	 * ForcedUnRegisterX and ForcedUnRegisterY are the x- and y-
	 * values of the first loaded unregister tag. These positions are
	 * used, if a moving tag is automatically unregistered.
	 * 
	 * @return the forcedUnRegisterY
	 */
	public int getForcedUnRegisterY() {
		return forcedUnRegisterY;
	} // getForcedUnRegisterY

	/**
	 * ForcedUnRegisterX and ForcedUnRegisterY are the x- and y-
	 * values of the first loaded unregister tag. These positions are
	 * used, if a moving tag is automatically unregistered.
	 * 
	 * @param forcedUnRegisterY the forcedUnRegisterY to set
	 */
	private void setForcedUnRegisterY(int forcedUnRegisterY) {
		this.forcedUnRegisterY = forcedUnRegisterY;
	} // setForcedUnRegisterY
}