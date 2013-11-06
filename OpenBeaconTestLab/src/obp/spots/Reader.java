/**
 * 
 */
package obp.spots;

import obp.service.Constants;

/**
 * @author bbehrens
 *
 */
public class Reader extends Spot {
	public Reader(int id) {
		super(id, "Unknown", 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	public Reader(int id, String name, int room, int floor, int group, int x, int y) {
		super(id, name, room, floor, group, x, y);
	} // Constructor (full)	

	@Override
	public SpotType getType() {
		return SpotType.Reader;
	} // getType
}