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
package obs.service.sighting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.ArrayList;

import obs.service.sighting.TagSighting;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the test of the service class reading previously
 * recorded tag data.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
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
			new TagSighting(new DatagramPacket(packetData, 32), key);
		}
	} // testTagSighting
}
