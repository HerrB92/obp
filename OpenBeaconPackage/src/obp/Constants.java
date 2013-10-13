/**
 * 
 */
package obp;

/**
 * @author bbehrens
 *
 */
public class Constants {
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
	
	public static final int TAG_BUTTON_ACTIVE_SECONDS = 5;
	public static final int TAG_READER_SIGHTING_ACTIVE_SECONDS = 5;
	
	public static final int PROX_TAG_STRENGTH_BITS = 2;
	public static final int PROX_TAG_STRENGTH_MASK = (1<<PROX_TAG_STRENGTH_BITS)-1;
	
	public static final int STRENGTH_AGGREGATION_WINDOW_SECONDS = 2;
	public static final int STRENGTH_AGGREGATION_AGED_SECONDS = STRENGTH_AGGREGATION_WINDOW_SECONDS + 2;
}