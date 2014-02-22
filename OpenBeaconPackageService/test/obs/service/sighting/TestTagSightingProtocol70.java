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
 * Test class for testing tag data encoded using protocol 70.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class TestTagSightingProtocol70 {
	
	private TagSighting tagSighting;
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	private static final byte[] packetData = 
		{-37,67,1,1,5,111,0,32,0,0,55,16,0,19,-64,-72,-29,-123,26,-31,-45,59,98,-127,-93,-100,106,53,38,-10,56,97};
	
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
		assertEquals(56131, tagSighting.getEnvelopeCRC());
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertEquals(1, tagSighting.getProtocol());
		assertEquals(1, tagSighting.getInterface());
		assertEquals(1391, tagSighting.getReaderId());
		assertEquals(32, tagSighting.getSize());
		assertEquals(14096, tagSighting.getSequence());
		assertEquals(1294520, tagSighting.getTimestamp());
		assertTrue(tagSighting.hasValidTagData());
		assertEquals(1119, tagSighting.getTagId());
		assertFalse(tagSighting.isTagButtonPressed());
		assertEquals(70, tagSighting.getTagProtocol());
		assertEquals(1, tagSighting.getFlags());
		assertEquals(3, tagSighting.getStrength());
		assertEquals(-1, tagSighting.getTagTime());
		assertEquals(-1, tagSighting.getTagBattery());
		assertEquals(17638, tagSighting.getTagSequence());
		assertNotNull(tagSighting.getProximitySightings());
		assertEquals(0, tagSighting.getProximitySightings().size());
		assertEquals(12, tagSighting.getTagCRC());
		assertTrue(tagSighting.hasValidTagCRC());
	}
}
