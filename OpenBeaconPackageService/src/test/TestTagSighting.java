/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.ArrayList;

import odp.service.sighting.TagSighting;

import org.junit.Before;
import org.junit.Test;

/**
 * @author bbehrens
 *
 */
public class TestTagSighting {
	
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	private ArrayList<byte[]> data = new ArrayList<byte[]>();
	
	@Before
	public void setUp() throws Exception {
		BufferedReader reader = null;
		byte[] bytes;
		
		try {
			InputStream inputStream = getClass().getResourceAsStream("Tag_1119_SampleData.log");
		    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			
			//reader = new BufferedReader(new FileReader("Tag_1119_SampleData.log"));
		    String line = null;
	
		    while ((line = reader.readLine()) != null) {
		    	try {
		    		String lineByteArray[] = line.split(",");
		    		
		    		bytes = new byte[lineByteArray.length];
		    		for (int i = 0; i < lineByteArray.length; i++) {
		    			bytes[i] = (byte)Integer.parseInt(lineByteArray[i]);
		    		}
		    		
		    		data.add(bytes);
		    	} catch (Exception e) {
		    	    e.printStackTrace();
		    	}
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {}
		}
	} // setUp

	/**
	 * Test method for {@link odp.service.sighting.tagsighting.TagSighting#TagSighting(java.net.DatagramPacket, int[])}.
	 */
	@Test
	public final void testTagSighting() {		
		// Should not throw an exception
		
		for (byte[] packetData: data) {
			new TagSighting(new DatagramPacket(packetData, 32), key, true);
		}
	} // testTagSighting
}
