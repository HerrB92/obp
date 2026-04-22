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
package obt.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import obt.tag.Tag;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Bjoern Behrens
 *
 */
public class TestDataIndex {
	private DataIndex index;
	private Tag tag;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		index = DataIndex.getInstance();
		index.getTagMap().clear();
		index.getRegisteredTagKeys().clear();
		index.getUnRegisteredTagKeys().clear();
		tag = new Tag(1234, null);
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getInstance()}.
	 */
	@Test
	public final void testGetInstance() {
		assertNotNull(DataIndex.getInstance());
		assertSame(index, DataIndex.getInstance());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getTagMap()}.
	 */
	@Test
	public final void testGetTagMap() {
		assertNotNull(index.getTagMap());
		assertTrue(index.getTagMap().isEmpty());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getTags()}.
	 */
	@Test
	public final void testGetTags() {
		Collection<Tag> tags = index.getTags();
		assertNotNull(tags);
		assertTrue(tags.isEmpty());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getTagByKey(java.lang.String)}.
	 */
	@Test
	public final void testGetTagByKey() {
		index.addTag(tag);
		assertSame(tag, index.getTagByKey(tag.getKey()));
		assertNull(index.getTagByKey("T9999"));
	}

	/**
	 * Test method for {@link obt.index.DataIndex#addTag(obt.tag.Tag)}.
	 */
	@Test
	public final void testAddTag() {
		index.addTag(tag);
		assertEquals(1, index.getTagMap().size());
		assertSame(tag, index.getTagMap().get(tag.getKey()));
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getKnownReaders()}.
	 */
	@Test
	public final void testGetKnownReaders() {
		assertNotNull(index.getKnownReaders());
		assertTrue(index.getKnownReaders().size() > 0);
	}

	/**
	 * Test method for {@link obt.index.DataIndex#registerTagKey(java.lang.String)}.
	 */
	@Test
	public final void testRegisterTagKey() {
		index.registerTagKey("T1234");
		assertEquals(1, index.getRegisteredTagKeys().size());
		assertEquals("T1234", index.getRegisteredTagKeys().peek());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getRegisteredTagKeys()}.
	 */
	@Test
	public final void testGetRegisteredTagKeys() {
		assertNotNull(index.getRegisteredTagKeys());
		assertTrue(index.getRegisteredTagKeys().isEmpty());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#unRegisterTagKey(java.lang.String)}.
	 */
	@Test
	public final void testUnRegisterTagKey() {
		index.unRegisterTagKey("T1234");
		assertEquals(1, index.getUnRegisteredTagKeys().size());
		assertEquals("T1234", index.getUnRegisteredTagKeys().peek());
	}

	/**
	 * Test method for {@link obt.index.DataIndex#getUnRegisteredTagKeys()}.
	 */
	@Test
	public final void testGetUnRegisteredTagKeys() {
		assertNotNull(index.getUnRegisteredTagKeys());
		assertTrue(index.getUnRegisteredTagKeys().isEmpty());
	}

}
