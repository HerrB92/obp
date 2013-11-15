/**
 * 
 */
package obp.spots;

import javax.persistence.Entity;

import obp.service.Constants;

/**
 * @author bbehrens
 *
 */
@Entity
public class Reader extends Spot {
	private static final long serialVersionUID = 7964675055454419473L;

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