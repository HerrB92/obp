/**
 * 
 */
package obt.spots;

import javax.persistence.Entity;
import javax.persistence.Table;

import obs.service.Constants;

/**
 * @author bbehrens
 *
 */
@Entity
@Table(name = "Readers")
public class Reader extends Spot {
	private static final long serialVersionUID = 7964675055454419473L;

	protected Reader() {
		super();
	} // Constructor (for Hibernate)
	
	public Reader(int id) {
		super(id, "Unknown", true, 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	public Reader(int id, String name, boolean active, int room, int floor, int group, int x, int y) {
		super(id, name, active, room, floor, group, x, y);
	} // Constructor (full)	

	@Override
	public SpotType getType() {
		return SpotType.Reader;
	} // getType

	@Override
	protected String getKeyCharacter() {
		return "R";
	} // getKeyCharacter
}