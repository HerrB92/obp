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

/**
 * Code based on the work of 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * Allesandro wrote: "Tools for decryption based on a java implementation of Ma
 * Bingyao optimized for 16 byte data"
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class Tools {

	/**
	 * Decrypt data using provided encryption key (array of four long values).
	 * 
	 * @param data
	 * @param key
	 * @return Decrypted byte data array
	 */
	public static byte[] decrypt(byte[] data, long[] key) {
		if (data.length == 0) {
			return data;
		}

		return toByteArray(decrypt(toIntArray(data, false), key), true);
	} // decrypt

	/**
	 * Decrypt data using provided encryption key (array of four long values).
	 * 
	 * @param data
	 * @param key
	 * @return Decrypted integer data array
	 */
	public static int[] decrypt(int[] data, long[] key) {
		int n = data.length - 1;

		if (n < 1) {
			return data;
		}

		if (key.length < 4) {
			long[] tmpKey = new long[4];

			System.arraycopy(key, 0, tmpKey, 0, key.length);
			key = tmpKey;
		}

		int z = data[n];
		int y = data[0];
		int e;
		int p;
		int q = 6 + 52 / (n + 1);
		int delta = 0x9E3779B9;
		int sum = q * delta;

		while (sum != 0) {
			e = sum >>> 2 & 3;
			for (p = n; p > 0; p--) {
				z = data[p - 1];
				y = data[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
						^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
			}
			z = data[n];
			y = data[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y)
					+ (key[p & 3 ^ e] ^ z);
			sum = sum - delta;
		}

		return data;
	} // decrypt

	/**
	 * Encrypt data using provided encryption key (array of four long values).
	 * 
	 * @param data
	 * @param key
	 * @return Encrypted byte data array
	 */
	public static byte[] encrypt(byte[] data, long[] key) {
		if (data.length == 0) {
			return data;
		}

		return toByteArray(encrypt(toIntArray(data, false), key), true);
	} // encrypt

	/**
	 * Encrypt data using provided encryption key (array of four long values).
	 * 
	 * @param data
	 * @param key
	 * @return Encrypted data array
	 */
	public static int[] encrypt(int[] data, long[] key) {
		int n = data.length - 1;

		if (n < 1) {
			return data;
		}

		if (key.length < 4) {
			long[] tmpKey = new long[4];

			System.arraycopy(key, 0, tmpKey, 0, key.length);
			key = tmpKey;
		}

		int delta = 0x9E3779B9;
		int z = data[n];
		int y = data[0];
		int e;
		int p;
		int sum = 0;
		int keyLength = key.length;
		int q = 6 + 52 / keyLength;

		for (int i = q; i > 0; i--) {
			sum += delta;
			e = sum >>> 2 & 3;

			for (p = 0; p < keyLength; p++) {
				y = data[(p + 1) & (keyLength - 1)];
				z = data[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
						^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
			}
		}

		return data;
	} // encrypt

	/**
	 * Convert byte array to integer array, converting two bytes into one
	 * integer.
	 * 
	 * @param data
	 * @param includeLength
	 * @return
	 */
	private static int[] toIntArray(byte[] data, boolean includeLength) {
		int n = (((data.length & 3) == 0) ? (data.length >>> 2)
				: ((data.length >>> 2) + 1));
		int[] result;

		if (includeLength) {
			result = new int[n + 1];
			result[n] = data.length;
		} else {
			result = new int[n];
		}

		n = data.length;
		for (int i = 0; i < n; i++) {
			result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
		}
		return result;
	} // toIntArray

	/**
	 * Convert integer array to byte array.
	 * 
	 * @param data
	 * @param includeLength
	 * @return
	 */
	private static byte[] toByteArray(int[] data, boolean includeLength) {
		int n = data.length << 2;
		byte[] result = new byte[n];

		for (int i = 0; i < n; i++) {
			result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
		}
		return result;
	} // toByteArray

	/**
	 * Change array order from Big-endian to Little-endian and vice versa.
	 * 
	 * @param input
	 *            Byte array to be flipped.
	 * @return Flipped byte array
	 */
	public static byte[] flipArray(byte[] input) {
		int INT_SIZE = 4;
		int i, j;
		byte flippedArray[] = new byte[input.length];

		for (i = 0; i < input.length / INT_SIZE; i++) {
			for (j = 0; j < INT_SIZE; j++) {
				flippedArray[(i + 1) * INT_SIZE - j - 1] = input[i * INT_SIZE + j];
			}
		}
		return flippedArray;
	} // flipArray
	
	/**
	 * Calculate CRC value from the provided byte data array as long value.
	 * 
	 * FIXME: Enhance parameter information
	 * 
	 * @param data
	 * @param start
	 * @param size
	 * @return CRC value
	 */
	public static int calculateLongCRC(byte[] data, int start, int size) {
		return (calculateCRC(data, start, size) ^ 0xFFFF);
	} // calculateLongCRC

	/**
	 * Calculate CRC value from the provided byte data array.
	 * 
	 * FIXME: Enhance parameter information
	 * 
	 * @param data
	 * @param start
	 * @param size
	 * @return CRC value
	 */
	public static int calculateCRC(byte[] data, int start, int size) {
		int crc = 0xFFFF;
		int p = start;

		while (size-- > 0) {
			crc = ((crc >> 8) | (crc << 8)) & 0xFFFF;
			crc ^= 0xFF & data[p++];
			crc ^= ((0xff & crc) >> 4) & 0xFFFF;
			crc ^= (crc << 12) & 0xFFFF;
			crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
		}

		return crc;
	} // calculateCRC
}