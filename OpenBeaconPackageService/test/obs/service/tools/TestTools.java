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
package obs.service.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Tools class.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class TestTools {
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link obs.service.tools.Tools#decrypt(byte[], long[])}.
	 */
	@Test
	public void testDecryptByteArrayByteArray() {
		byte[] input = {(byte)42,(byte)31,(byte)-77,(byte)-77,(byte)106,(byte)40,(byte)-67,(byte)-71,
			    		(byte)-34,(byte)78,(byte)119,(byte)10,(byte)74,(byte)121,(byte)-104,(byte)-66};
		byte[] output = Tools.decrypt(input, key);
		
		assertEquals(10, output[0]);
		assertEquals(40, output[1]);
		assertEquals(30, output[2]);
		assertEquals(50, output[3]);
		assertEquals(1, output[4]);
		assertEquals(2, output[5]);
		assertEquals(3, output[6]);
		assertEquals(4, output[7]);
		assertEquals(21, output[8]);
		assertEquals(22, output[9]);
		assertEquals(23, output[10]);
		assertEquals(24, output[11]);
		assertEquals(101, output[12]);
		assertEquals(102, output[13]);
		assertEquals(103, output[14]);
		assertEquals(104, output[15]);
	} // testDecryptByteArrayByteArray

	/**
	 * Test method for {@link obs.service.tools.Tools#decrypt(int[], long[])}.
	 */
	@Test
	public void testDecryptIntArrayIntArray() {
		int[] input = {1644713709,-1686103062,232915823,1623405619}; // More than 4 bytes are not decrypted
		int[] output = Tools.decrypt(input, key);
		
		assertEquals(100, output[0]);
		assertEquals(40, output[1]);
		assertEquals(200, output[2]);
		assertEquals(60, output[3]);
	} // testDecryptIntArrayIntArray
	
	/**
	 * Test method for {@link obs.service.tools.Tools#encrypt(byte[], long[])}.
	 */
	@Test
	public void testEncryptByteArrayByteArray() {
		byte[] input = {(byte)10,(byte)40,(byte)30,(byte)50,(byte)1,(byte)2,(byte)3,(byte)4,
					    (byte)21,(byte)22,(byte)23,(byte)24,(byte)101,(byte)102,(byte)103,(byte)104};
		byte[] output = Tools.encrypt(input, key);
		
		assertEquals(42, output[0]);
		assertEquals(31, output[1]);
		assertEquals(-77, output[2]);
		assertEquals(-77, output[3]);
		assertEquals(106, output[4]);
		assertEquals(40, output[5]);
		assertEquals(-67, output[6]);
		assertEquals(-71, output[7]);
		assertEquals(-34, output[8]);
		assertEquals(78, output[9]);
		assertEquals(119, output[10]);
		assertEquals(10, output[11]);
		assertEquals(74, output[12]);
		assertEquals(121, output[13]);
		assertEquals(-104, output[14]);
		assertEquals(-66, output[15]);
	} // testEncryptByteArrayByteArray
	
	/**
	 * Test method for {@link obs.service.tools.Tools#encrypt(int[], long[])}.
	 */
	@Test
	public void testEncryptIntArrayIntArray() {
		int[] input = {100,40,200,60}; // More than 4 bytes are not encrypted
		int[] output = Tools.encrypt(input, key);
		
		// 0xff -> Just for better usability/optics
		assertEquals(237, output[0] & 0xff);
		assertEquals(234, output[1] & 0xff);
		assertEquals(111, output[2] & 0xff);
		assertEquals(51, output[3] & 0xff);
	} // testEncryptIntArrayIntArray

	/**
	 * Test method for the combination of encryption and decryption.
	 */
	@Test
	public void testEncryptDecrypt() {
		byte[] input = {(byte)10,(byte)40,(byte)30,(byte)50,(byte)1,(byte)2,(byte)3,(byte)4,
					    (byte)21,(byte)22,(byte)23,(byte)24,(byte)101,(byte)102,(byte)103,(byte)104};
		byte[] output = Tools.decrypt(Tools.encrypt(input, key), key);
		
		assertEquals(input[0], output[0]);
		assertEquals(input[1], output[1]);
		assertEquals(input[2], output[2]);
		assertEquals(input[3], output[3]);
		assertEquals(input[4], output[4]);
		assertEquals(input[5], output[5]);
		assertEquals(input[6], output[6]);
		assertEquals(input[7], output[7]);
		assertEquals(input[8], output[8]);
		assertEquals(input[9], output[9]);
		assertEquals(input[10], output[10]);
		assertEquals(input[11], output[11]);
		assertEquals(input[12], output[12]);
		assertEquals(input[13], output[13]);
		assertEquals(input[14], output[14]);
		assertEquals(input[15], output[15]);
	} // testEncryptByteArrayByteArray
	
	/**
	 * Test method for {@link obs.service.tools.Tools#flipArray(byte[])}.
	 */
	@Test
	public void testFlipArray() {
		byte[] input = {0,2,7,15};
		byte[] output = Tools.flipArray(input);
		
		assertEquals(input[3], output[0]);
		assertEquals(input[2], output[1]);
		assertEquals(input[1], output[2]);
		assertEquals(input[0], output[3]);
	} // testFlipArray
}
