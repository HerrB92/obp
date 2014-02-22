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
package obs.service.sighting;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import obs.service.Constants;
import obs.service.tools.Tools;

/**
 * Class used to provide the data structure for tag sightings.
 * 
 * Code based on the work of 2007
 * Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class TagSighting {
	static final Logger logger = LogManager.getLogger(TagSighting.class.getName());
	
	// Packet
	// IP address of the sending reader
	private InetAddress sendingReader;

	// Envelope
	// Byte array of network data packet
	private byte[] rawData;

	// CRC value of envelope
	private int envelopeCRC = Constants.NOT_DEFINED;

	// Signals, if the provided and the calculated envelope
	// CRC are equal (valid)
	private boolean validEnvelopeCRC = false;

	// Envelope protocol number
	private int protocol = Constants.NOT_DEFINED;

	// The number of the reader interface (more or less
	// the antenna within the reader, currently 0 or 1)
	private int readerInterface = Constants.NOT_DEFINED;

	// The reader id
	private int readerId = Constants.NOT_DEFINED;

	// String of R + reader id, may be used for hash maps
	private String readerKey;

	// Envelope size
	private int size = Constants.NOT_DEFINED;

	// Envelope sequence number
	private int sequence = Constants.NOT_DEFINED;

	// Envelope time stamp
	private int timestamp = Constants.NOT_DEFINED;

	// Data
	// Byte array of tag data as wrapped by the envelope
	private byte[] tagData;

	// Signals, if it was possible to decrypt the
	// tag data and to fill the data fields
	private boolean validTagData = false;

	// Tag id
	private int tagId = Constants.NOT_DEFINED;

	// String of T + tagId, may be used for hash maps
	private String tagKey;

	// Protocol number used to serialize the tag data
	private int tagProtocol = Constants.NOT_DEFINED;

	// Flags set for the tag
	private int tagFlags = Constants.NOT_DEFINED;

	// Signal strength number between 0 and STRENGTH_MAX_LEVEL
	private int tagStrength = Constants.NOT_DEFINED;

	// Tag sequence
	private int tagSequence = Constants.NOT_DEFINED;

	// CRC value of tag data
	private int tagCRC = Constants.NOT_DEFINED;

	// Signals, if the provided and the calculated tag
	// data CRC are equal (valid)
	private boolean validTagCRC = false;

	// List of proximity sightings (if provided)
	private ArrayList<ProximitySighting> proximitySightings;

	// Signals, if - based on the tag flags - the
	// tag button has been pressed
	private Boolean tagButtonPressed;

	// Time information as provided by the tag
	// (used only in RFBPROTO_BEACONTRACKER_EXT)
	private int tagTime = Constants.NOT_DEFINED;

	// Battery status information as provided by the tag
	// (used only in RFBPROTO_BEACONTRACKER_EXT)
	private int tagBattery = Constants.NOT_DEFINED;

	/**
	 * Constructor which splits the raw data provided in the network packet and
	 * assigns the values to the different fields.
	 * 
	 * @param packet
	 *            Network packet data
	 * @param encryptionKey
	 *            Encryption key used to decrypt the tag data (array of four
	 *            long values)
	 */
	public TagSighting(DatagramPacket packet, long[] encryptionKey) {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());

		sendingReader = packet.getAddress();

		rawData = new byte[packet.getLength()];
		tagData = new byte[Constants.ENVELOPE_SIZE_BYTE
				- Constants.ENVELOPE_HEADER_SIZE_BYTE];

		// Read the bytes from the buffer and assign values to the
		// respective fields.

		// Envelope is defined in TBeaconNetworkHdr as:
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
			default: // Remaining bytes: Tag data
				tagData[i - 16] = rawData[i];
				break;
			}
		}

		// Compose reader key
		readerKey = Constants.ReaderPrefix + readerId;

		// Check envelope CRC
		// CRC is calculated from all raw data except the first two bytes
		// (which contain the envelope CRC value)
		if (calculateLongCRC(rawData, 2, 30) == getEnvelopeCRC()) {
			validEnvelopeCRC = true;
		}

		// Decrypt tag data and return information, if provided and
		// calculated tag CRC are equal (valid)
		validTagCRC = decryptTagData(tagData, encryptionKey);

		logger.debug("Sighting: " + this.toString());
	} // Constructor

	/**
	 * Try to decrypt the tag data using the provided encryption key and - if
	 * successfully decrypted - assign tag data values to fields.
	 * 
	 * @param tagData
	 *            Byte array of raw tag data
	 * @param encryptionKey
	 *            Encryption key used to decrypt the tag data (array of four
	 *            long values)
	 * @return True, if tag data has been decrypted. False, otherwise.
	 */
	private boolean decryptTagData(byte[] tagData, long[] encryptionKey) {
		// First, flip every two bytes, as they have been flipped
		// by the tag firmware for the network transport
		tagData = Tools.flipArray(tagData);

		// Second, decrypt the tag data using the encryption key
		tagData = Tools.decrypt(tagData, encryptionKey);

		// Third, flip the bytes again, as the data was prepared
		// that way by the tag firmware
		tagData = Tools.flipArray(tagData);

		tagProtocol = 0xff & tagData[0];

		for (int i = 1; i < tagData.length; i++) {
			switch (getTagProtocol()) {
			case Constants.RFBPROTO_BEACONTRACKER_OLD:
				// TBeaconTrackerOld:
				// Byte 00: u_int8_t proto
				// Byte 01: u_int8_t proto2
				// Byte 02: u_int8_t flags
				// Byte 03: u_int8_t strength
				// Byte 04-07: u_int32_t seq
				// Byte 08-11: u_int32_t oid
				// Byte 12-13: u_int16_t reserved
				// Byte 14-15: u_int16_t crc

				// FIXME: Test this untested code

				int tempProtocol = 0xff & tagData[1];

				if (tempProtocol == Constants.RFBPROTO_BEACONTRACKER_OLD2) {
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
					setTagKey(tagId);

					// Ignored:
					// Reserved: Byte 12 & 13

					tagCRC = (0xff & tagData[14]) << 8;
					tagCRC = 0xff & tagData[15];

					setValidTagData(true);
				} else {
					logger.error("Unknown tag protocol 2: %i", tempProtocol);
				}
				break;
			case Constants.RFBPROTO_BEACONTRACKER_EXT:
				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconTrackerExt:
				// Byte 04: uint8_t strength;
				// Byte 05-06: uint16_t oid_last_seen;
				// Byte 07-08: uint16_t time;
				// Byte 09: uint8_t battery;
				// Byte 10-13: uint32_t seq;

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				tagFlags = 0;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}

				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}

				// TODO: Not used (usage/specification unknown)
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];

				setTagTime((0xff & tagData[7]) << 8);
				setTagTime(getTagTime() + (0xff & tagData[8]));

				setTagBattery(0xff & tagData[9]);

				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				setValidTagData(true);
				break;
			case Constants.RFBPROTO_BEACONTRACKER:
				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconTracker:
				// Byte 04: u_int8_t strength;
				// Byte 05-06: u_int16_t oid_last_seen;
				// Byte 07-08: u_int16_t powerup_count;
				// Byte 09: u_int8_t reserved;
				// Byte 10-13: u_int32_t seq;

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				tagFlags = 0;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}

				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}

				// TODO: Not used (usage/specification unknown)
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];

				// TODO: Not used (usage/specification unknown)
				// powerup_count = (0xff & tagData[7]) << 8;
				// powerup_count = 0xff & tagData[8];

				// Ignored:
				// Reserved: Byte 09

				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				setValidTagData(true);
				break;
			case Constants.RFBPROTO_PROXTRACKER:
				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// TODO: Check, if this is the correct structure:
				// Payload p = TBeaconTracker:
				// Byte 04: u_int8_t strength;
				// Byte 05-06: u_int16_t oid_last_seen;
				// Byte 07-08: u_int16_t powerup_count;
				// Byte 09: u_int8_t reserved;
				// Byte 10-13: u_int32_t seq;

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				tagFlags = 0;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}

				tagStrength = 0xff & tagData[4];
				if (tagStrength >= Constants.STRENGTH_LEVELS_COUNT) {
					tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;
				}

				// TODO: Not used (usage/specification unknown)
				// oid_last_seen = (0xff & tagData[5]) << 8;
				// oid_last_seen = 0xff & tagData[6];

				// TODO: Not used (usage/specification unknown)
				// powerup_count = (0xff & tagData[7]) << 8;
				// powerup_count = 0xff & tagData[8];

				// Ignored:
				// Reserved: Byte 09

				tagSequence = (0xff & tagData[10]) << 24;
				tagSequence += (0xff & tagData[11]) << 16;
				tagSequence += (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				setValidTagData(true);
				break;
			case Constants.RFBPROTO_PROXREPORT:
				// FIXME: OpenBeacon says:
				// "Add support for old proximity format"

				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconProx:
				// Byte 04-11: u_int16_t oid_prox[PROX_MAX], PROX_MAX: currently
				// fixed 4;
				// Byte 12-13: u_int16_t seq;

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				tagFlags = 0;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags = Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}
				tagFlags |= Constants.TAGSIGHTINGFLAG_SHORT_SEQUENCE;

				// Ignored: Byte 04-11, as within the original OpenBeacon C++
				// code
				// oid_prox data is not extracted/used

				tagSequence = (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];

				// Fixed value (as in the original OpenBeacon C++ code)
				tagStrength = Constants.STRENGTH_LEVELS_COUNT - 1;

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				setValidTagData(true);
				break;
			case Constants.RFBPROTO_PROXREPORT_EXT:
				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconProx:
				// Byte 04-11: u_int16_t oid_prox[PROX_MAX], PROX_MAX: currently
				// fixed 4;
				// Byte 12-13: u_int16_t seq;

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				tagFlags = Constants.TAGSIGHTINGFLAG_SHORT_SEQUENCE;
				if ((tagData[3] & Constants.RFBFLAGS_SENSOR) == Constants.RFBFLAGS_SENSOR) {
					tagFlags |= Constants.TAGSIGHTINGFLAG_BUTTON_PRESS;
				}

				tagButtonPressed = false;
				if ((tagFlags & Constants.TAGSIGHTINGFLAG_BUTTON_PRESS) == 
					Constants.TAGSIGHTINGFLAG_BUTTON_PRESS) {
					tagButtonPressed = true;
				}

				proximitySightings = new ArrayList<ProximitySighting>();

				int proximityData;
				for (int j = 0; j < Constants.PROX_TAG_MAX_COUNT; j++) {
					proximityData = 
							((0xff & tagData[4 + (j * 2)]) << 8)
							+ (0xff & tagData[5 + (j * 2)]);

					if (proximityData > 0) {
						proximitySightings
							.add(new ProximitySighting(
								// Other tag id
								proximityData
									& Constants.PROX_TAG_ID_MASK,
								// Count
								(proximityData >> Constants.PROX_TAG_ID_BITS)
									& Constants.PROX_TAG_COUNT_MASK,
								// Strength
								(proximityData >> (Constants.PROX_TAG_ID_BITS + Constants.PROX_TAG_COUNT_BITS))
									& Constants.PROX_TAG_STRENGTH_MASK));
					}
				}

				tagSequence = (0xff & tagData[12]) << 8;
				tagSequence += 0xff & tagData[13];

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				// Fixed value (as in the original OpenBeacon C++ code)
				// This is always energy level 3, as only on level 3
				// the protocol RFBPROTO_PROXREPORT_EXT (70) is used.
				tagStrength = Constants.PROX_TAG_STRENGTH_MASK;

				setValidTagData(true);
				break;
			case Constants.RFBPROTO_READER_ANNOUNCE:
				// FIXME: Ask OpenBeacon about details

				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconReaderAnnounce:
				// Byte 04: u_int8_t opcode
				// Byte 05: u_int8_t strength
				// Byte 06-09: u_int32_t uptime
				// Byte 10-13: u_int32_t ip

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				// setValidTagData(true) intentionally not set
				break;
			case Constants.RFBPROTO_BEACONTRACKER_STRANGE:
				// FIXME: OpenBeacon says: "Fix complete case"

				// TBeaconWrapper:
				// Byte 00: u_int8_t proto;
				// Byte 01-02: u_int16_t oid;
				// Byte 03: u_int8_t flags;
				// Byte 04-13: TBeaconPayload p;
				// Byte 14-15: u_int16_t crc;

				// Payload p = TBeaconReaderCommand:
				// Byte 04: u_int8_t opcode
				// Byte 05: u_int8_t res;
				// Byte 06-13: u_int32_t data[2];

				tagId = (0xff & tagData[1]) << 8;
				tagId += 0xff & tagData[2];
				setTagKey(tagId);

				// Fixed values (as in the original OpenBeacon C++ code)
				tagStrength = 3;
				tagSequence = 0;
				tagFlags = 0;

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				setValidTagData(true);
				break;
			default:
				logger.error("Unknown packet protocol %i, reader: %i",
						getTagProtocol(), readerId);

				tagCRC = (0xff & tagData[14]) << 8;
				tagCRC = 0xff & tagData[15];

				// setValidTagData(true) intentionally not set
			}
		}

		return (tagCRC == (calculateCRC(tagData, 0, 14) & 0xFF));
	} // decryptTagData

	private void setTagKey(int tagId) {
		tagKey = Constants.TagPrefix + tagId;
	} // setTagLey

	public InetAddress getSendingReader() {
		return sendingReader;
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

	public String getReaderKey() {
		return readerKey;
	} // getReaderKey

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

	/**
	 * @return the validTagData
	 */
	public boolean hasValidTagData() {
		return validTagData;
	} // hasValidTagData

	/**
	 * @param validTagData
	 *            the validTagData to set
	 */
	public void setValidTagData(boolean validTagData) {
		this.validTagData = validTagData;
	} // setValidTagData

	public int getTagId() {
		return tagId;
	} // getTagId

	/**
	 * @return The tag key ("T" + tag id)
	 */
	public String getTagKey() {
		return tagKey;
	} // getTagKey

	public Boolean isTagButtonPressed() {
		return tagButtonPressed;
	} // isTagButtonPressed

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

	public ArrayList<ProximitySighting> getProximitySightings() {
		return proximitySightings;
	} // getProximitySightings

	/**
	 * @return the tagTime
	 */
	public int getTagTime() {
		return tagTime;
	} // getTagTime

	/**
	 * @param tagTime
	 *            the tagTime to set
	 */
	public void setTagTime(int tagTime) {
		this.tagTime = tagTime;
	} // setTagTime

	/**
	 * @return the tagBattery
	 */
	public int getTagBattery() {
		return tagBattery;
	} // getTagBattery

	/**
	 * @param tagBattery
	 *            the tagBattery to set
	 */
	public void setTagBattery(int tagBattery) {
		this.tagBattery = tagBattery;
	} // setTagBattery

	/**
	 * @return True, if envelope and tag CRC have been successfully validated
	 *         and the tag data has been decrypted successfully. False,
	 *         otherwise.
	 */
	public boolean isValid() {
		return (hasValidEnvelopeCRC() && hasValidTagCRC() && hasValidTagData());
	} // isValid

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
	private int calculateLongCRC(byte[] data, int start, int size) {
		return (calculateCRC(data, start, size) ^ 0xFFFF);
	} // calculateLongCRC

	/**
	 * Custom toString method to provide a human readable output
	 * 
	 * @param rawData
	 *            Set to true to only output the tag sighting raw data. Set to
	 *            false to output the object data in a human friendly way.
	 * @return String representation of the object
	 */
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
			buffer.append("Envelope: CRC: ");
			buffer.append(getEnvelopeCRC());
			buffer.append(" (Check: ");
			buffer.append(hasValidEnvelopeCRC());
			buffer.append(")|Protocol: ");
			buffer.append(getProtocol());
			buffer.append("|Interface: ");
			buffer.append(getInterface());
			buffer.append("|Reader ID: ");
			buffer.append(getReaderId());
			buffer.append("|Sender: ");

			if (getSendingReader() == null) {
				buffer.append("Unknown");
			} else {
				buffer.append(getSendingReader().getHostAddress());
			}

			buffer.append("|Size: ");
			buffer.append(getSize());
			buffer.append("|Sequence: ");
			buffer.append(getSequence());
			buffer.append("|Timestamp: ");
			buffer.append(getTimestamp());
			buffer.append("\nTag: Valid Data: ");
			buffer.append(hasValidTagData());
			buffer.append("|ID: ");
			buffer.append(getTagId());
			buffer.append("|TagProtocol: ");
			buffer.append(getTagProtocol());

			buffer.append("|Button: ");
			if (isTagButtonPressed() == null) {
				buffer.append("null");
			} else {
				buffer.append(isTagButtonPressed());
			}

			buffer.append("|Proximity: ");
			if (getProximitySightings() == null) {
				buffer.append("None");
			} else {
				for (ProximitySighting sighting : getProximitySightings()) {
					buffer.append(sighting.getTagId());
					buffer.append(": ");
					buffer.append(sighting.getStrength());
					buffer.append(" (");
					buffer.append(sighting.getCount());
					buffer.append("),");
				}
				buffer.deleteCharAt(buffer.length() - 1);
			}

			buffer.append("|Flags: ");
			buffer.append(getFlags());
			buffer.append("|Strength: ");
			buffer.append(getStrength());
			buffer.append("|TagTime: ");
			buffer.append(getTagTime());
			buffer.append("|Battery: ");
			buffer.append(getTagBattery());
			buffer.append("|TagSequence: ");
			buffer.append(getTagSequence());
			buffer.append("|TagCRC: ");
			buffer.append(getTagCRC());
			buffer.append(" (Check: ");
			buffer.append(hasValidTagCRC());
			buffer.append(")");
		}

		return buffer.toString();
	} // toString (boolean rawData)

	/**
	 * Overridden toString method calling the custom toString method to provide
	 * the object data human-friendly string representation.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(false);
	} // toString
}