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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Bjoern Behrens
 *
 */
public class TestObtRun {
	private ObtRun run;
	private DateTime fixedDateTime;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fixedDateTime = new DateTime(2020, 1, 2, 3, 4, 5, 0);
		run = new ObtRun(fixedDateTime);
	}

	/**
	 * Test method for {@link obt.index.ObtRun#ObtRun()}.
	 */
	@Test
	public final void testObtRun() {
		ObtRun defaultRun = new ObtRun();
		assertNotNull(defaultRun.getTimeStamp());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#ObtRun(org.joda.time.DateTime)}.
	 */
	@Test
	public final void testObtRunDateTime() {
		assertEquals(fixedDateTime, run.getTimeStamp());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#getRunId()}.
	 */
	@Test
	public final void testGetRunId() {
		assertNull(run.getRunId());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#setRunId(java.lang.Long)}.
	 */
	@Test
	public final void testSetRunId() {
		run.setRunId(42L);
		assertEquals(Long.valueOf(42L), run.getRunId());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#getTimeStamp()}.
	 */
	@Test
	public final void testGetTimeStamp() {
		assertEquals(fixedDateTime, run.getTimeStamp());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#setTimeStamp(org.joda.time.DateTime)}.
	 */
	@Test
	public final void testSetTimeStamp() {
		DateTime updatedDateTime = fixedDateTime.plusHours(1);
		run.setTimeStamp(updatedDateTime);
		assertEquals(updatedDateTime, run.getTimeStamp());
	}

	/**
	 * Test method for {@link obt.index.ObtRun#toString()}.
	 */
	@Test
	public final void testToString() {
		run.setRunId(7L);
		String output = run.toString();
		assertTrue(output.contains("Run: 7"));
		assertTrue(output.contains(run.getTimeStamp().toString()));
	}

}
