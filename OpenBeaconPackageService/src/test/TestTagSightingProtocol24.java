/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;

import odp.service.listener.TagSighting;

import org.junit.Before;
import org.junit.Test;

/**
 * @author bbehrens
 *
 */
public class TestTagSightingProtocol24 {
	
	private TagSighting tagSighting;
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	private static final byte[] packetData = 
		{69,(byte)145,1,1,5,20,0,32,0,0,1,(byte)176,0,17,46,(byte)151,13,43,103,48,(byte)213,(byte)161,(byte)133,84,(byte)151,18,99,14,(byte)132,(byte)224,(byte)172,87};
	
	@Before
	public void setUp() throws Exception {
		tagSighting = new TagSighting(new DatagramPacket(packetData, 32), key, true);
	}

	/**
	 * Test method for {@link obp.service.tagsighting.TagSighting#TagSighting(java.net.DatagramPacket, int[])}.
	 */
	@Test
	public final void testTagSighting() {		
		// Should not throw an exception
	}

	/**
	 * Test method for {@link obp.service.tagsighting.TagSighting#checkCRC()}.
	 */
	@Test
	public final void testData() {		
		assertEquals(17809, tagSighting.getEnvelopeCRC());
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertEquals(1, tagSighting.getProtocol());
		assertEquals(1, tagSighting.getInterface());
		assertEquals(1300, tagSighting.getReaderId());
		assertEquals(32, tagSighting.getSize());
		assertEquals(432, tagSighting.getSequence());
		assertEquals(1126039, tagSighting.getTimestamp());
		assertTrue(tagSighting.hasValidTagData());
		assertEquals(1119, tagSighting.getTagId());
		assertNull(tagSighting.isTagButtonPressed());
		assertEquals(24, tagSighting.getTagProtocol());
		assertEquals(0, tagSighting.getFlags());
		assertEquals(2, tagSighting.getStrength());
		assertNull(tagSighting.getProximitySightings());
		assertEquals(-1, tagSighting.getTagTime());
		assertEquals(-1, tagSighting.getTagBattery());
		assertEquals(1770132, tagSighting.getTagSequence());
		assertEquals(136, tagSighting.getTagCRC());
		assertTrue(tagSighting.hasValidTagCRC());
	}
}
