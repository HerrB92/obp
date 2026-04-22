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

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Before;
import org.junit.Test;

public class TestOutputJSON {
	private static class TestableOutputJSON extends OutputJSON {
		public TestableOutputJSON(String fileName) {
			super(fileName);
		}

		@Override
		protected void process(JsonGenerator generator) throws IOException {
			generator.writeStringField("status", "ok");
		}
	}

	private Path tempFile;
	private TestableOutputJSON output;

	@Before
	public void setUp() throws Exception {
		tempFile = TestOutputJSONSupport.createTempJsonFile("obp-outputjson-");
		output = new TestableOutputJSON(tempFile.toString());
	}

	@Test
	public final void testOutputJSON() {
		assertNotNull(output);
		assertEquals(tempFile.toFile(), output.getFile());
	}

	@Test
	public final void testGetFile() {
		assertNotNull(output.getFile());
		assertEquals(tempFile.toFile(), output.getFile());
	}

	@Test
	public final void testSetFile() {
		Path replacementFile = tempFile.resolveSibling("replacement-outputjson.json");
		output.setFile(replacementFile.toString());
		assertEquals(replacementFile.toFile(), output.getFile());
	}

	@Test
	public final void testGetFactory() {
		assertNotNull(output.getFactory());
	}

	@Test
	public final void testIncrementSequence() {
		assertEquals(0L, output.incrementSequence());
		assertEquals(1L, output.incrementSequence());
	}

	@Test
	public final void testGetSequence() {
		assertEquals(0L, output.getSequence());
		output.incrementSequence();
		assertEquals(1L, output.getSequence());
	}

	@Test
	public final void testUpdate() throws Exception {
		output.update();
		assertTrue(output.getFile().exists());
	}

	@Test
	public final void testProcess() throws IOException {
		output.update();
		String content = TestOutputJSONSupport.readFile(tempFile);
		assertTrue(content.contains("\"status\""));
		assertTrue(content.contains("\"ok\""));
	}

}
