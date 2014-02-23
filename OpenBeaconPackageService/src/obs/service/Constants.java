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
package obs.service;

/**
 * Class providing constants applying for the OpenBeaconService and derived 
 * packages. The constants have to be reviewed, if OpenBeacon releases newer
 * firmwares, readers or tags.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class Constants {
	public static final int ENVELOPE_SIZE_BYTE = 32;
	public static final int ENVELOPE_HEADER_SIZE_BYTE = 16;
	
	public static final int ENVELOPE_PROTOCOL_BEACONLOG_SIGHTING = 1; // BEACONLOG_SIGHTING
	
	public static final int RFBPROTO_BEACONTRACKER_OLD = 16;
	public static final int RFBPROTO_READER_ANNOUNCE = 22;
	public static final int RFBPROTO_BEACONTRACKER_OLD2 = 23;
	public static final int RFBPROTO_BEACONTRACKER = 24;
	public static final int RFBPROTO_BEACONTRACKER_STRANGE = 25;
	public static final int RFBPROTO_BEACONTRACKER_EXT = 26;
	public static final int RFBPROTO_PROXTRACKER = 42;
	public static final int RFBPROTO_PROXREPORT = 69;
	public static final int RFBPROTO_PROXREPORT_EXT = 70;
	
	public static final int RFBFLAGS_ACK = 0x01;
	public static final int RFBFLAGS_SENSOR = 0x02;
	public static final int RFBFLAGS_INFECTED = 0x04;
	
	public static final int TAGSIGHTINGFLAG_SHORT_SEQUENCE = 0x01;
	public static final int TAGSIGHTINGFLAG_BUTTON_PRESS = 0x02;
	
	public static final int STRENGTH_LEVELS_COUNT = 4;
	public static final int STRENGTH_MAX_LEVEL = 3;
	
	public static final int PROX_TAG_MAX_COUNT = 4;
	public static final int PROX_TAG_ID_BITS = 12;
	public static final int PROX_TAG_ID_MASK = (1<<PROX_TAG_ID_BITS)-1;
	public static final int PROX_TAG_COUNT_BITS = 2;
	public static final int PROX_TAG_COUNT_MASK = (1<<PROX_TAG_COUNT_BITS)-1;
	public static final int PROX_TAG_STRENGTH_BITS = 2;
	public static final int PROX_TAG_STRENGTH_MASK = (1<<PROX_TAG_STRENGTH_BITS)-1;
	
	public static final int NOT_DEFINED = -1;
	
	public static final String TagPrefix = "T";
	public static final String ReaderPrefix = "R";
}