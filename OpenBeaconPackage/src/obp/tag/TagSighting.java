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

import obp.Constants;
import obp.tools.Tools;

public class TagSighting {
	public static final int ENVELOPE_SIZE_BYTE = 32;
	public static final int ENVELOPE_HEADER_SIZE_BYTE = 16;
	
	private InetAddress reader;

	public long time;
	
	private byte[] rawData;
	private int envelopeCRC;
	private boolean validEnvelopeCRC = false;
	private int protocol;
	private int readerInterface;
	private int readerId;
	private int size;
	private int sequence;
	private int timestamp;
	
	private byte[] tagData;
	
	private int tagId;
	private int tagProtocol;
	private int tagFlags;
	private int tagStrength;
	private int tagSequence;
	private int tagCRC;
	private boolean validTagCRC = false;
	
	private int[] proxTagId = new int[4];
	
	private boolean tagButtonPressed = false;

	public TagSighting(DatagramPacket packet, int[] encryptionKey) {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		
		reader = packet.getAddress();
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
		
		for (int i = 0; i < packet.getLength(); i++) {
			rawData[i] = buffer.get();
						
			switch (i) {
			case 0: // CRC (first byte)
				envelopeCRC = (0xff & rawData[i]) << 8;
				break;
			case 1: // CRC (second byte)
				envelopeCRC += 0xff & rawData[i];
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
			default:
				tagData[i - 16] = rawData[i];
				break;
			}
		}
		
		// Check envelope CRC
		// CRC is calculated from all raw data except the first two bytes
		// (which contain the envelope CRC value)
//		byte[] envelopeRawData = new byte[rawData.length - 2];
//		System.arraycopy(rawData, 2, envelopeRawData, 0, 30);
		
		if (calculateLongCRC(rawData, 2, 30) == getEnvelopeCRC()) {
			validEnvelopeCRC = true;
		}
		
		validTagCRC = decryptTagData(tagData, encryptionKey);
						
		//System.out.println("Sighting: " + this.toString());
	}
	
	private boolean decryptTagData(byte[] tagData, int[] encryptionKey) {
		// Decode packet
		tagData = Tools.flipArray(tagData);
		tagData = Tools.decrypt(tagData, encryptionKey);
		tagData = Tools.flipArray(tagData);
		
		tagProtocol = 0xff & tagData[0];
		
		// FIXME: THis should be somewhere else
		int ignoredProtocols = 0;
		int invalidProtocols = 0;
		
		for (int i = 1; i < tagData.length; i++) {
			switch (getTagProtocol()) {
			case Constants.RFBPROTO_BEACONTRACKER_OLD:
				// TBeaconTrackerOld:
				// Byte 00:    u_int8_t proto
				// Byte 01:    u_int8_t proto2
				// Byte 02:    u_int8_t flags
				// Byte 03:    u_int8_t strength
				// Byte 04-07: u_int32_t seq
				// Byte 08-11: u_int32_t oid
				// Byte 12-13: u_int16_t reserved
				// Byte 14-15: u_int16_t crc
				
				// FIXME: Test this untested code
				
				int tagProtocol2 = 0xff & tagData[1];
				if (tagProtocol2 != Constants.RFBPROTO_BEACONTRACKER_OLD2) {
					tagStrength = -1;
					tagSequence = 0;
					System.out.printf("\t\tunknown old packet protocol2[%i] key[%i] ", tagProtocol2);
					ignoredProtocols++;
				} else {
					tagFlags = 0;
					if ((tagData[2] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
						tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
					}
					
					int tempStrength = 0xff & tagData[3];
					if (tempStrength >= 0x55) {
						tagStrength = tempStrength / 0x55;
					} else if (tempStrength >= Constants.STRENGTH_LEVELS_COUNT) {
						tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
					} else {
						tagStrength = tempStrength;
					}
					
					tagSequence = (0xff & tagData[4]) << 24;
					tagSequence += (0xff & tagData[5]) << 16;
					tagSequence += (0xff & tagData[6]) << 8;
					tagSequence += 0xff & tagData[7];
					
					tagId = (0xff & tagData[8]) << 24;
					tagId += (0xff & tagData[9]) << 16;
					tagId += (0xff & tagData[10]) << 8;
					tagId += 0xff & tagData[11];
					
					// Not used:
					// Reserved: byte 12 & 13
					
					tagCRC = (0xff & tagData[14]) << 8;
					tagCRC = 0xff & tagData[15];
				}
				break;
			case Constants.RFBPROTO_BEACONTRACKER_EXT:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconTrackerExt:
				// Byte 04:    uint8_t strength;
				// Byte 05-06: uint16_t oid_last_seen;
				// Byte 07-08: uint16_t time;
				// Byte 09:    uint8_t battery;
				// Byte 10-13: uint32_t seq;
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				
				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}
				
				// Not used
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];
				
				// time = (0xff & tagData[7]) << 8;
				// time = 0xff & tagData[8];
				
				// battery = 0xff & tagData[9];
				
				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				break;
			case Constants.RFBPROTO_BEACONTRACKER:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconTracker:
				// Byte 04:    u_int8_t strength;
				// Byte 05-06: u_int16_t oid_last_seen;
				// Byte 07-08: u_int16_t powerup_count;
				// Byte 09:    u_int8_t reserved;
				// Byte 10-13: u_int32_t seq;
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				
				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}
				
				// Not used
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];
				
				// powerup_count = (0xff & tagData[7]) << 8;
				// powerup_count = 0xff & tagData[8];
				
				// reserved = 0xff & tagData[9];
				
				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				break;
			case Constants.RFBPROTO_PROXTRACKER:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// TODO: Check, if this is the correct structure:
				// Payload p = TBeaconTracker:
				// Byte 04:    u_int8_t strength;
				// Byte 05-06: u_int16_t oid_last_seen;
				// Byte 07-08: u_int16_t powerup_count;
				// Byte 09:    u_int8_t reserved;
				// Byte 10-13: u_int32_t seq;
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				
				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}
				
				// Not used
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];
				
				// powerup_count = (0xff & tagData[7]) << 8;
				// powerup_count = 0xff & tagData[8];
				
				// reserved = 0xff & tagData[9];
				
				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				break;
			case Constants.RFBPROTO_PROXREPORT:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconProx:
				// Byte 04-11: u_int16_t oid_prox[PROX_MAX], PROX_MAX: currently fixed 4;
				// Byte 12-13: u_int16_t seq;
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				tagFlags |= Constants.TAGSIGHTINGFLAG_SHORT_SEQUENCE;
				
				// In RFBPROTO_PROXREPORT oid_prox data is not used
				
				tagSequence = (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];
				
				// Fixed values
				tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				// FIXME: OpenBeacon: Add support for old proximity format
				break;
			case Constants.RFBPROTO_PROXREPORT_EXT:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconProx:
				// Byte 04-11: u_int16_t oid_prox[PROX_MAX], PROX_MAX: currently fixed 4;
				// Byte 12-13: u_int16_t seq;
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				tagFlags = 0xff & tagData[3];
				
				tagFlags = Constants.TAGSIGHTINGFLAG_SHORT_SEQUENCE;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags |= Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				
				if ((tagFlags & Constants.TAGSIGHTINGFLAG_BUTTON_PRESS) == Constants.TAGSIGHTINGFLAG_BUTTON_PRESS) {
					tagButtonPressed = true;
				}
				
				proxTagId[0] = (0xff & tagData[4]) << 8;
				proxTagId[0] += 0xff & tagData[5];
				proxTagId[1] = (0xff & tagData[6]) << 8;
				proxTagId[1] += 0xff & tagData[7];
				proxTagId[2] = (0xff & tagData[8]) << 8;
				proxTagId[2] += 0xff & tagData[9];
				proxTagId[3] = (0xff & tagData[10]) << 8;
				proxTagId[3] += 0xff & tagData[11];
				
				// FIXME: Code me!!!
//				if (tag_sighting)
//				{
//					prox_tag_id = tag_sighting & PROX_TAG_ID_MASK;
//					prox_tag_count =
//						(tag_sighting >> PROX_TAG_ID_BITS) &
//						PROX_TAG_COUNT_MASK;
//					prox_tag_strength =
//						(tag_sighting >>
//						 (PROX_TAG_ID_BITS +
//						  PROX_TAG_COUNT_BITS)) & PROX_TAG_STRENGTH_MASK;
//					/* add proximity tag sightings to table */
//					prox_tag_sighting (timestamp, tag_id, prox_tag_id,
//									   prox_tag_strength, prox_tag_count);
//				}
				
				tagSequence = (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				
				// Fixed values
				tagStrength = Constants.PROX_TAG_STRENGTH_MASK;
				break;
			case Constants.RFBPROTO_READER_ANNOUNCE:
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconReaderAnnounce:
				// Byte 04:    u_int8_t opcode
				// Byte 05:    u_int8_t strength
				// Byte 06-09: u_int32_t uptime
				// Byte 10-13: u_int32_t ip
				
				// Fixed values
				tagId = 0;
				tagStrength = -1;
				tagSequence = 0;
				ignoredProtocols++;
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				break;
			case Constants.RFBPROTO_BEACONTRACKER_STRANGE:
				// FIXME: OpenBeacon: Fix complete case
				
				// TBeaconWrapper:
				// Byte 00:    u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03:    u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;
				
				// Payload p = TBeaconReaderCommand:
				// Byte 04:    u_int8_t opcode
				// Byte 05:    u_int8_t res;
				// Byte 06-13: u_int32_t data[2];
				
				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				
				// Fixed values
				tagStrength = 3;
				tagSequence = 0;
				tagSequence = 0;
				tagFlags = 0;
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
				break;
			default:
				// FIXME: Change this to logger
//				System.out.println("\t\tunknown packet protocol[%03i] [key=%i] [reader=0x%08X] ",
//						getTagProtocol(), key_id, reader_id);
//				hex_dump (&env, 0, sizeof (env));
				
				// Fixed values
				tagId = 0;
				tagStrength = -1;
				tagSequence = 0;
				invalidProtocols++;
				// res = 0;
				
				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];
			}
		}

		return (tagCRC == (calculateCRC(tagData, 0, 14) & 0xFF));		
	} // decryptTagData
	
	public InetAddress getReader() {
		return reader;
	} // getReader
	
	public byte[] getRawData() {
		return rawData;
	} // getRawData
	
	public int getEnvelopeCRC() {
		return envelopeCRC;
	} // getEnvelopeCRC
	
	public boolean hasValidEnvelopeCRC() {
		return validEnvelopeCRC;
	} // hasValidEnvelopeCRC
	
	public int getProtocol() {
		return protocol;
	} // getProtocol
	
	public int getInterface() {
		return readerInterface;
	} // getInterface
	
	public int getReaderId() {
		return readerId;
	} // getReaderId
	
	public int getSize() {
		return size;
	} // getSize
	
	public byte[] getTagData() {
		return tagData;
	} // getTagData
	
	public int getSequence() {
		return sequence;
	} // getSequence
	
	public int getTimestamp() {
		return timestamp;
	} // getTimestamp
	
	public int getTagId() {
		return tagId;
	} // getTagId
	
	public boolean getTagButtonPressed() {
		return tagButtonPressed;
	} // getTagButtonPressed
	
	public int getTagProtocol() {
		return tagProtocol;
	} // getTagProtocol
		
	public int getFlags() {
		return tagFlags;
	} // getFlags
	
	public int getStrength() {
		return tagStrength;
	} // getStrength
	
	public int getTagSequence() {
		return tagSequence;
	} // getTagSequence
		
	public int getTagCRC() {
		return tagCRC;
	} // getTagCRC
	
	public boolean hasValidTagCRC() {
		return validTagCRC;
	} // hasValidCRC
	
	private int calculateCRC(byte[] data, int start, int size) {
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
	
	private int calculateLongCRC(byte[] data, int start, int size) {
		return (calculateCRC(data, start, size) ^ 0xFFFF);
	} // calculateLongCRC
	
	public String toString(boolean rawData) {
		StringBuffer buffer = new StringBuffer();
		
		if (rawData) {
			for (byte data : getRawData()) {
				if (buffer.length() > 0) {
					buffer.append(",");
				}
				buffer.append(data & 0xFF);
			}
		} else {
			buffer.append("CRC: ");
			buffer.append(getEnvelopeCRC());
			buffer.append(" (Check: ");
			buffer.append(hasValidEnvelopeCRC());
			buffer.append(")|");
			buffer.append("Protocol: ");
			buffer.append(getProtocol());
			buffer.append("|Interface: ");
			buffer.append(getInterface());
			buffer.append("|Reader ID: ");
			buffer.append(getReaderId());
			buffer.append("|Size: ");
			buffer.append(getSize());
			buffer.append("|Sequence: ");
			buffer.append(getSequence());
			buffer.append("|Timestamp: ");
			buffer.append(getTimestamp());
			buffer.append("|Tag ID: ");
			buffer.append(getTagId());
			buffer.append("|Tag Button: ");
			buffer.append(getTagButtonPressed());
			buffer.append("|Tag Protocol: ");
			buffer.append(getTagProtocol());
			buffer.append("|Flags: ");
			buffer.append(getFlags());
			buffer.append("|Strength: ");
			buffer.append(getStrength());
			buffer.append("|TagSequence: ");
			buffer.append(getTagSequence());
			buffer.append("|Tag CRC: ");
			buffer.append(getTagCRC());
			buffer.append(" (Check: ");
			buffer.append(hasValidTagCRC());
			buffer.append(")");
		}
		
		return buffer.toString();
	} // toString(boolean rawData)
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(false);
	} // toString
}
