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
package obt.index.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import obt.index.DataIndex;
import org.junit.Before;
import org.junit.Test;

/**
 * @author bbehrens
 *
 */
public class TestOutputJSONUnRegisteredTagKeys {
	private Path tempFile;
	private OutputJSONUnRegisteredTagKeys output;
	private DataIndex index;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tempFile = TestOutputJSONSupport.createTempJsonFile("obp-outputjson-unregistered-");
		output = new OutputJSONUnRegisteredTagKeys(tempFile.toString());
		index = DataIndex.getInstance();
		index.getUnRegisteredTagKeys().clear();
		index.unRegisterTagKey("T333");
		index.unRegisterTagKey("T444");
	}

	/**
	 * Test method for {@link obt.index.output.OutputJSONUnRegisteredTagKeys#process(com.fasterxml.jackson.core.JsonGenerator)}.
	 */
	@Test
	public final void testProcess() throws Exception {
		output.update();
		String content = TestOutputJSONSupport.readFile(tempFile);
		assertTrue(content.contains("\"tag\""));
		assertTrue(content.contains("T333"));
		assertTrue(content.contains("T444"));
	}

	/**
	 * Test method for {@link obt.index.output.OutputJSONUnRegisteredTagKeys#OutputJSONUnRegisteredTagKeys(java.lang.String)}.
	 */
	@Test
	public final void testOutputJSONUnRegisteredTagKeys() {
		assertNotNull(output);
	}

}
