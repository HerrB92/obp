//  2007 Alessandro Marianantoni <alex@alexrieti.com>
 
// Sputnik.java
// Software object for the Sputnik HW module 
 

 /*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/*

 in order this represents the 16BYTE received from the EasyOpenBeacon:
 Size_1B
 Proto_1B
 Flags_1B
 Strength_1B
 Seq_4B
 TagID_4B
 reserved_2B
 crc_2B
 
 */

package obp.tag;

public class Tag {

//	private InetAddress reader;
//
//	private byte sArray[];
//
//	public long time;
//
//	public Tag(DatagramPacket packet, int[] encryptionKey) {
//		int i;
//		byte[] data;
//
//		reader = packet.getAddress();
//		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
//
//		time = System.currentTimeMillis();
//		
//		// Prepare byte array for packet data
//		if (buffer.hasArray()) {
//			System.out.println("HasArray");
//		}
//		data = new byte[packet.getLength()];
//		
//		// Read the bytes from the buffer into a byte array
//		for (i = 0; i < packet.getLength(); i++) {
//			data[i] = buffer.get();
//		}
//
//		if (encryptionKey != null) {
//			// flip encrypted array from network byte order to host
//			byte flippedArray[] = Tools.flipArray(data);
//			for (byte value : flippedArray) {
//				System.out.print(value);
//				System.out.print("|");
//			}
//			System.out.println("");
//			
//			//System.out.println(flippedArray);
//
//			// all packet decryption
//			byte[] decrypt = Tools.decrypt(flippedArray, encryptionKey);
//			System.out.println("DLength " + decrypt.length);
//
//			// flip decrypted data from network byte order to host
//			sArray = Tools.flipArray(decrypt);
////			for (byte test : sArray) {
////				System.out.println(test);
////			}
//		} else {
//			sArray = data;
//		}
//	}
//
//	public int getID() {
//		return ((0xff & sArray[8]) << 24) + ((0xff & sArray[9]) << 16)
//				+ ((0xff & sArray[10]) << 8) + ((0xff & sArray[11]));
//	}
//
//	public InetAddress getBeaconAddress() {
//		return reader;
//	}
//
//	public int getStrength() {
//		return (0xff & sArray[3]);
//	}
//
//	public int getSize() {
//		return (0xff & sArray[0]);
//	}
//
//	public int getProto() {
//		return (0xff & sArray[1]);
//	}
//
//	public int getFlag() {
//		return (0xff & sArray[2]);
//	}
//
//	public int getSeq() {
//		return ((0xff & sArray[4]) << 24) + ((0xff & sArray[5]) << 16)
//				+ ((0xff & sArray[6]) << 8) + ((0xff & sArray[7]));
//	}
//
//	public int getReserved() {
//		return ((0xff & sArray[12]) + (0xff & sArray[13]));
//	}
//
//	public int getCRC() {
//		return ((0xff & sArray[14]) << 8) + ((0xff & sArray[15]));
//	}
//
//	public boolean checkCRC() {
//		int size = 14;
//		long crc = 0xFFFF;
//		int p = 0;
//		while (size-- > 0) {
//			crc = 0xFFFF & ((crc >> 8) | (crc << 8));
//			crc ^= 0xFF & sArray[p++];
//			crc ^= ((0xff & crc) >> 4) & 0xFFFF;
//			crc ^= (crc << 12) & 0xFFFF;
//			crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
//		}
//		crc = (0xFFFF & crc);
//		return (crc == getCRC());
//	}
//
//	public long getTime() {
//		return time;
//	}
}
