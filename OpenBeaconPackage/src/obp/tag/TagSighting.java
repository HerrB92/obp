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

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import obp.tools.Tools;

public class TagSighting {
	public static final int ENVELOPE_SIZE_BYTE = 32;
	public static final int ENVELOPE_HEADER_SIZE_BYTE = 16;
	
//	#define RFBPROTO_BEACONTRACKER_OLD      16
//	#define RFBPROTO_READER_ANNOUNCE        22
//	#define RFBPROTO_BEACONTRACKER_OLD2     23
//	#define RFBPROTO_BEACONTRACKER          24
//	#define RFBPROTO_BEACONTRACKER_STRANGE  25
//	#define RFBPROTO_BEACONTRACKER_EXT      26
//	#define RFBPROTO_PROXTRACKER            42
//	#define RFBPROTO_PROXREPORT             69
//	#define RFBPROTO_PROXREPORT_EXT         70

	private InetAddress reader;

	public long time;
	
	private byte[] rawData;
	private int crcChecksum;
	private int protocol;
	private int readerInterface;
	private int readerId;
	private int size;
	private int sequence;
	private int timestamp;
	
	private int proto;
	private int[] block = new int[4];
	
	private byte[] tagData;

	public TagSighting(DatagramPacket packet, int[] encryptionKey) {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		
		rawData = new byte[packet.getLength()];
		tagData = new byte[ENVELOPE_SIZE_BYTE - ENVELOPE_HEADER_SIZE_BYTE];
		
		// Read the bytes from the buffer and assign values to the
		// respective fields.
		
		// Envelope is defined as TBeaconNetworkHdr as:
		// Byte 00-01: u_int16_t icrc16;
		// Byte 02: u_int8_t protocol;
		// Byte 03: u_int8_t interface;
		// Byte 04-05: u_int16_t reader_id;
		// Byte 06-07: u_int16_t size;
		
		// TBeaconLogSighting:
		// TBeaconNetworkHdr hdr;
		// Byte 08-11: u_int32_t sequence;
		// Byte 12-15: u_int32_t timestamp;
		// TBeaconEnvelope log;
		
		// TBeaconEnvelope:
		// Byte 16: u_int8_t proto;
		// TBeaconWrapper pkt;
		// TBeaconTrackerOld old;
		// u_int32_t block[XXTEA_BLOCK_COUNT];
		// u_int8_t byte[XXTEA_BLOCK_COUNT * 4];
		
		// ??? TBeaconEnvelope
		
		int i;
		int tagDataPos = 0;
		int tagBlock = 0;
		for (i = 0; i < packet.getLength(); i++) {
			rawData[i] = buffer.get();
			
			switch (i) {
			case 0: // CRC (first byte)
				crcChecksum = (0xff & rawData[i]) << 8;
				break;
			case 1: // CRC (second byte)
				crcChecksum += 0xff & rawData[i];
				break;
			case 2: // Protocol
				protocol = 0xff & rawData[i];
				break;
			case 3: // Reader interface (antenna)
				readerInterface = 0xff & rawData[i];
				break;
			case 4: // Reader id (first byte)
				readerId = (0xff & rawData[i]) << 8;
				break;
			case 5: // Reader id (second byte)
				readerId += 0xff & rawData[i];
				break;
			case 6: // Size (first byte)
				size = 0xff & rawData[i];
				break;
			case 7: // Size (second byte)
				size += 0xff & rawData[i];
				break;
			case 8: // Sequence (first byte)
				sequence = (0xff & rawData[i]) << 24;
				break;
			case 9: // Sequence (second byte)
				sequence += (0xff & rawData[i]) << 16;
				break;
			case 10: // Sequence (third byte)
				sequence += (0xff & rawData[i]) << 8;
				break;
			case 11: // Sequence (fourth byte)
				sequence += 0xff & rawData[i];
				break;
			case 12: // Timestamp (first byte)
				timestamp = (0xff & rawData[i]) << 24;
				break;
			case 13: // Timestamp (second byte)
				timestamp += (0xff & rawData[i]) << 16;
				break;
			case 14: // Timestamp (third byte) 
				timestamp += (0xff & rawData[i]) << 8;
				break;
			case 15: // Timestamp (fourth byte)
				timestamp += 0xff & rawData[i];
				break;
			case 16: // Proto?
				proto = 0xff & rawData[i];
				break;
			default:
				tagData[i - 17] = rawData[i];
				break;
//			case 17:
//			case 18:
//			case 19:
//			case 20: // Block[XXTEA_BLOCK_COUNT];
//				if (tagBlock == 0) {
//					tagBlock = 24;
//				} else {
//					tagBlock = tagBlock - 8;
//				}
//				block[0] += (0xff & rawData[i]) << tagBlock;
//				break;
//			case 21:
//			case 22:
//			case 23:
//			case 24: // Block[XXTEA_BLOCK_COUNT];
//				if (tagBlock == 0) {
//					tagBlock = 24;
//				} else {
//					tagBlock = tagBlock - 8;
//				}
//				block[1] += (0xff & rawData[i]) << tagBlock;
//				break;
//			case 25:
//			case 26:
//			case 27:
//			case 28: // Block[XXTEA_BLOCK_COUNT];
//				if (tagBlock == 0) {
//					tagBlock = 24;
//				} else {
//					tagBlock = tagBlock - 8;
//				}
//				block[2] += (0xff & rawData[i]) << tagBlock;
//				break;
//			case 29:
//			case 30:
//			case 31:
//			case 32: // Block[XXTEA_BLOCK_COUNT];
//				if (tagBlock == 0) {
//					tagBlock = 24;
//				} else {
//					tagBlock = tagBlock - 8;
//				}
//				block[3] += (0xff & rawData[i]) << tagBlock;
//				break;
//			default:
//				System.out.println("Other data?");
////				tagData[tagDataPos] = rawData[i];
////				tagDataPos++;
			}
		}
		
//		// Decrypt tagData
//		byte[] flippedArray = Tools.flipArray(tagData);
//		byte[] decryptedArray = Tools.decrypt(flippedArray, encryptionKey);
//		tagData = Tools.flipArray(decryptedArray);
//		
//		// Store the decrypted data again within the raw data byte array,
//		// used for CRC calculation
//		for (i = 0; i < tagData.length; i++) {
//			rawData[ENVELOPE_HEADER_SIZE_BYTE + i] = tagData[i];
//		}
//				
		System.out.println("Envelope: " + this.toString());
		
//		((0xff & sArray[8]) << 24) + ((0xff & sArray[9]) << 16)
//		+ ((0xff & sArray[10]) << 8) + ((0xff & sArray[11]));
		
//		
//		
//		u_int16_t icrc16;
//		u_int8_t protocol;
//		u_int8_t interface;
//		u_int16_t reader_id;
//		u_int16_t size;
//	
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
	}
	
	public byte[] getRawData() {
		return rawData;
	}
	
	public int getCRCChecksum() {
		return crcChecksum;
	}
	
	public int getProtocol() {
		return protocol;
	}
	
	public int getInterface() {
		return readerInterface;
	}
	
	public int getReaderId() {
		return readerId;
	}
	
	public int getSize() {
		return size;
	}
	
	public byte[] getTagData() {
		return tagData;
	}
	
	public int getSequence() {
		return sequence;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public int getProto() {
		return proto;
	}
	
	public boolean checkCRC() {
		long crc = 0xFFFF;
		int p = 0;
		int size = 14;
		
		while (size-- > 0) {
			crc = ((crc >> 8) | (crc << 8)) & 0xFFFF;
			crc ^= 0xFF & rawData[p++];
			crc ^= ((0xff & crc) >> 4) & 0xFFFF;
			crc ^= (crc << 12) & 0xFFFF;
			crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
		}
		crc = (0xFFFF & crc);
		
		if (crc == getCRCChecksum()) {
			System.out.println("TRUE: " + size);
		}
		
//		//int size = 20;
//		long crc = 0xFFFF;
//		int p = 0;
//		
//		while (size-- > 0) {
//			crc = ((crc >> 8) | (crc << 8)) & 0xFFFF;
//			crc ^= 0xFF & rawData[p++];
//			crc ^= ((0xff & crc) >> 4) & 0xFFFF;
//			crc ^= (crc << 12) & 0xFFFF;
//			crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
//		}
//		crc = (0xFFFF & crc);
//		
//		System.out.println("CRC (Calc): " + crc);
//		System.out.println("CRC (Tag): " + getCRCChecksum());
		
//		return (crc == getCRCChecksum());
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CRC: ");
		buffer.append(getCRCChecksum());
		buffer.append(" (Check: ");
		buffer.append(checkCRC());
		buffer.append(")");
		buffer.append("|");
		buffer.append("Protocol: ");
		buffer.append(getProtocol());
		buffer.append("|");
		buffer.append("Interface: ");
		buffer.append(getInterface());
		buffer.append("|");
		buffer.append("Reader ID: ");
		buffer.append(getReaderId());
		buffer.append("|");
		buffer.append("Size: ");
		buffer.append(getSize());
		buffer.append("|");
		buffer.append("Sequence: ");
		buffer.append(getSequence());
		buffer.append("|");
		buffer.append("Timestamp: ");
		buffer.append(getTimestamp());
		buffer.append("|");
		buffer.append("Proto: ");
		buffer.append(getProto());
		buffer.append("|");
		buffer.append("Tag Data: ");
		buffer.append(getTagData());
		
		return buffer.toString();
	}
}
