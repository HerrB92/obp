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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the ProximitySighting class.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class TestProximitySighting {
	private ProximitySighting sighting;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sighting = new ProximitySighting(1111, 5, 2);
	} // setUp

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#ProximitySighting(int, int, int)}.
	 */
	@Test
	public void testProximitySighting() {
		assertTrue(sighting != null);
	} // testProximitySighting

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#getTagId()}.
	 */
	@Test
	public void testGetTagId() {
		assertEquals(1111, sighting.getTagId());
	} // testGetTagId

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#setTagId(int)}.
	 */
	@Test
	public void testSetTagId() {
		assertEquals(1111, sighting.getTagId());
		sighting.setTagId(2000);
		assertEquals(2000, sighting.getTagId());
	} // testSetTagId

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#getTagKey()}.
	 */
	@Test
	public void testGetTagKey() {
		assertEquals("T1111", sighting.getTagKey());
	} // testGetTagKey

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#getCount()}.
	 */
	@Test
	public void testGetCount() {
		assertEquals(5, sighting.getCount());
	} // testGetCount

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#setCount(int)}.
	 */
	@Test
	public void testSetCount() {
		assertEquals(5, sighting.getCount());
		sighting.setCount(3);
		assertEquals(3, sighting.getCount());
	} // testSetCount

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#getStrength()}.
	 */
	@Test
	public void testGetStrength() {
		assertEquals(2, sighting.getStrength());
	} // testGetStrength

	/**
	 * Test method for {@link obs.service.sighting.ProximitySighting#setStrength(int)}.
	 */
	@Test
	public void testSetStrength() {
		assertEquals(2, sighting.getStrength());
		sighting.setStrength(1);
		assertEquals(1, sighting.getStrength());
	} // testSetStrength
}
