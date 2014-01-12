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
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * "Tools for decryption based on a java implementation of 
 * Ma Bingyao optimized for 16 byte data"
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
     * @return
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
     * @param v
     * @param k
     * @return
     */
    public static int[] decrypt(int[] v, long[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        
        if (k.length < 4) {
            long[] key = new long[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n];
        int y = v[0];
        int e;
        int p;
        int q = 6 + 52 / (n + 1);
        int delta = 0x9E3779B9;
        int sum = q * delta;
        
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                        ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }
            z = v[n];
            y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                    ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }
        
        return v;
    } // decrypt

    /**
     * @param data
     * @param includeLength
     * @return
     */
    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0)
                ? (data.length >>> 2)
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
     * Convert int array[] to byte array.
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
     * @param input
     * @return
     */
    public static byte[] flipArray(byte[] input) {
		int INT_SIZE = 4;
		int i, j;
		byte flipped_array[] = new byte[input.length];

		for (i = 0; i < input.length / INT_SIZE; i++) {
			for (j = 0; j < INT_SIZE; j++) {
				flipped_array[(i + 1) * INT_SIZE - j - 1] = input[i * INT_SIZE + j];
			}
		}
		return flipped_array;
	} // flipArray
}