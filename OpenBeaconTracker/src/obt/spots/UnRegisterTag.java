/**
 * 
 */
package obt.spots;

import javax.persistence.Entity;
import javax.persistence.Table;

import obp.service.Constants;

/**
 * @author bbehrens
 *
 */
@Entity
@Table(name = "UnRegisterTags")
public class UnRegisterTag extends Spot {
	private static final long serialVersionUID = 6833469865041050361L;

	protected UnRegisterTag() {
		super();
	} // Constructor (for Hibernate)
	
	public UnRegisterTag(int id) {
		super(id, "Unknown", true, 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	public UnRegisterTag(int id, String name, boolean active, int room, int floor, int group, int x, int y) {
		super(id, name, active, room, floor, group, x, y);
	} // Constructor (full)

	@Override
	public SpotType getType() {
		return SpotType.UnRegisterTag;
	} // getType
	
	@Override
	protected String getKeyCharacter() {
		return "T";
	} // getKeyCharacter
}
