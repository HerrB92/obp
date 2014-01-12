/**
 * 
 */
package obp.spots;

import obs.service.Constants;

/**
 * @author bbehrens
 *
 */
public class SpotTag extends Spot {
	public SpotTag(int id) {
		super(id, "Unknown", 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	public SpotTag(int id, String name, int room, int floor, int group, int x, int y) {
		super(id, name, room, floor, group, x, y);
	} // Constructor (full)

	@Override
	public SpotType getType() {
		return SpotType.SpotTag;
	} // getType
}
