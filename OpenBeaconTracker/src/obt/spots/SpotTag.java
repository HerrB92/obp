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
@Table(name = "SpotTags")
public class SpotTag extends Spot {
	private static final long serialVersionUID = -5713994066880579783L;

	protected SpotTag() {
		super();
	} // Constructor (for Hibernate)
	
	public SpotTag(int id) {
		super(id, "Unknown", true, 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	public SpotTag(int id, String name, boolean active, int room, int floor, int group, int x, int y) {
		super(id, name, active, room, floor, group, x, y);
	} // Constructor (full)

	@Override
	public SpotType getType() {
		return SpotType.SpotTag;
	} // getType
	
	@Override
	protected String getKeyCharacter() {
		return "T";
	} // getKeyCharacter
}
