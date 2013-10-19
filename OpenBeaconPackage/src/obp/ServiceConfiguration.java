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
import java.util.HashMap;
import java.util.Properties;

import obp.reader.Reader;

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
	private int strengthAggregationWindowSeconds = 2;
	private int strengthAggregationAgedSeconds = strengthAggregationWindowSeconds + 2;
	
	private long[] tagDataKey = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	
	private HashMap<Integer, Reader> readers = new HashMap<Integer, Reader>();
	
	private ServiceConfiguration() {}
	
	public static ServiceConfiguration getInstance() {
		if (configuration == null) {
			configuration = new ServiceConfiguration();
			configuration.loadConfiguration();
			
			configuration.getReaders().put(1259, new Reader(1259, 1, 1, 1, 1, 1));
			configuration.getReaders().put(1391, new Reader(1391, 2, 1, 1, 640, 620));
			configuration.getReaders().put(1291, new Reader(1291, 3, 1, 1, 1, 1165));
			configuration.getReaders().put(1300, new Reader(1300, 4, 1, 1, 610, 1395));
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
	            // Try loading from classpath
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
	
	public HashMap<Integer, Reader> getReaders() {
		return readers;
	} // getReaders
	
	public Reader getReader (Integer id) {
		return getReaders().get(id);
	} // getReader
	
	public boolean isValidReader(Integer id) {
		return getReaders().containsKey(id);
	} // isValidReader
}