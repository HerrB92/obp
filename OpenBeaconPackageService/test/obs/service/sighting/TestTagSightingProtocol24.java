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

import static org.junit.Assert.*;

import java.net.DatagramPacket;

import obs.service.sighting.TagSighting;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for testing tag data encoded using protocol 24 (RFBPROTO_BEACONTRACKER).
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class TestTagSightingProtocol24 {
	
	private TagSighting tagSighting;
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	private static final byte[] packetData = 
		{69,(byte)145,1,1,5,20,0,32,0,0,1,(byte)176,0,17,46,(byte)151,13,43,103,48,(byte)213,(byte)161,(byte)133,84,(byte)151,18,99,14,(byte)132,(byte)224,(byte)172,87};
	
	@Before
	public void setUp() throws Exception {
		tagSighting = new TagSighting(new DatagramPacket(packetData, 32), key);
	}

	/**
	 * Test method for {@link odp.service.sighting.tagsighting.TagSighting#TagSighting(java.net.DatagramPacket, int[])}.
	 */
	@Test
	public final void testTagSighting() {		
		// Should not throw an exception
	}

	/**
	 * Test method for {@link odp.service.sighting.tagsighting.TagSighting#checkCRC()}.
	 */
	@Test
	public final void testData() {		
		assertEquals(17809, tagSighting.getEnvelopeCRC());
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertEquals(1, tagSighting.getProtocol());
		assertEquals(1, tagSighting.getInterface());
		assertEquals(1300, tagSighting.getReaderId());
		assertEquals("R1300", tagSighting.getReaderKey());
		assertEquals(32, tagSighting.getSize());
		assertEquals(432, tagSighting.getSequence());
		assertEquals(1126039, tagSighting.getTimestamp());
		assertTrue(tagSighting.hasValidTagData());
		assertEquals(1119, tagSighting.getTagId());
		assertEquals("T1119", tagSighting.getTagKey());
		assertNull(tagSighting.isTagButtonPressed());
		assertEquals(24, tagSighting.getTagProtocol());
		assertEquals(0, tagSighting.getFlags());
		assertEquals(2, tagSighting.getStrength());
		assertNull(tagSighting.getProximitySightings());
		assertEquals(-1, tagSighting.getTagTime());
		assertEquals(-1, tagSighting.getTagBattery());
		assertEquals(1770132, tagSighting.getTagSequence());
		assertEquals(41352, tagSighting.getTagCRC());
		assertTrue(tagSighting.hasValidTagCRC());
		assertTrue(tagSighting.isValid());
		
		for (int i = 0; i < packetData.length; i++) {
			try {
				assertEquals(packetData[i], tagSighting.getRawData()[i]);
			} catch (AssertionError e) {
				System.out.printf("PacketData failed at %d (expected: %d, got: %d)\n", i, packetData[i], tagSighting.getRawData()[i]);
			}
		}
		
		assertFalse(tagSighting.getTagData() == null);
		assertEquals(16, tagSighting.getTagData().length);
		
		assertEquals("69,145,1,1,5,20,0,32,0,0,1,176,0,17,46,151,13,43,103,48,213,161,133,84,151,18,99,14,132,224,172,87",
					tagSighting.toString(true));
	} // testData
}
