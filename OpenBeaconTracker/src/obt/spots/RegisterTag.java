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
package obt.spots;

import javax.persistence.Entity;
import javax.persistence.Table;

import obs.service.Constants;

/**
 * Class for register tags.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
@Entity
@Table(name = "RegisterTags")
public class RegisterTag extends Spot {
	private static final long serialVersionUID = -764963424217634831L;

	/**
	 * Light constructor
	 */
	protected RegisterTag() {
		super();
	} // Constructor (for Hibernate)
	
	/**
	 * Constructor with the register tag id
	 * 
	 * @param id
	 */
	public RegisterTag(int id) {
		super(id, "Unknown", true, 0, 0, 0, Constants.NOT_DEFINED, Constants.NOT_DEFINED);
	} // Constructor (id only)
	
	/**
	 * Full qualified constructor
	 * 
	 * @param id
	 * @param name
	 * @param active
	 * @param room
	 * @param floor
	 * @param group
	 * @param x
	 * @param y
	 */
	public RegisterTag(int id, String name, boolean active, int room, int floor, int group, int x, int y) {
		super(id, name, active, room, floor, group, x, y);
	} // Constructor (full)

	/**
	 * @see obt.spots.Spot#getType()
	 */
	@Override
	public SpotType getType() {
		return SpotType.RegisterTag;
	} // getType

	/**
	 * Return key character for this type of spot (tag)
	 * 
	 * @see obt.spots.Spot#getKeyCharacter()
	 */
	@Override
	protected String getKeyCharacter() {
		return "T";
	} // getKeyCharacter
}
