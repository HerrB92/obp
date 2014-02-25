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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Abstract class to define spots (spot, register, unregister tags or
 * readers) and store or receive the information in the database.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
@MappedSuperclass // This class provides the Hibernate definition also for child classes
public abstract class Spot implements Serializable {
	private static final long serialVersionUID = -2171247901903093310L;
	
	@Id
	@Column(nullable = false)
	private int id;
	
	@Id
	@Column(nullable = false)
	private Long runId;
	
	@Transient
	private String key;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private boolean active;
		
	@Column(nullable = false)
	private int room;
	
	@Column(nullable = false)
	private int floor;
	
	// The column name "group" is not allowed in MySQL/MariaDB,
	// use "SpotGroup" as column name.
	@Column(name="SpotGroup", nullable = false)
	private int group;
	
	@Column(nullable = false)
	private int x;
	
	@Column(nullable = false)
	private int y;
	
	@Transient
	private DateTime created = DateTime.now();
	
	@Column
	@Type(type="obt.persistence.joda.PersistentDateTime")
	private DateTime lastSeen;
	
	/**
	 * Light constructor (for hibernate)
	 */
	protected Spot() {
		this(0, "-", true, 0, 0, 0, 0, 0);
	} // Constructor (for Hibernate)
	
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
	protected Spot(int id, String name, boolean active, int room, int floor, int group, int x, int y) {
		setId(id);
		setName(name);
		setActive(active);
		setRoom(room);
		setFloor(floor);
		setGroup(room);
		setX(x);
		setY(y);
	} // Constructor
	
	/**
	 * @return the reader id
	 */
	public int getId() {
		return id;
	} // getId

	/**
	 * @param id the reader id to set
	 */
	public void setId(int id) {
		this.id = id;
	} // setId
	
	/**
	 * @return Run id, 0 for current run
	 */
	@SuppressWarnings("unused")
	private Long getRunId() {
		return runId;
	} // getRunId

	/**
	 * @param runId
	 */
	@SuppressWarnings("unused")
	private void setRunId(Long runId) {
		this.runId = runId;
	} // setRunId
	
	/**
	 * @return the key
	 */
	public String getKey() {
		if (key == null) {
			key = getKeyCharacter() + getId();
		}
		return key;
	} // getKey
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	} // getName

	/**
	 * @param name a name for the reader
	 */
	public void setName(String name) {
		this.name = name;
	} // setName
	
	/**
	 * @return if spot is active (in general)
	 */
	public boolean isActive() {
		return active;
	} // isActive

	/**
	 * @param active set to true to mark this spot as active
	 */
	public void setActive(boolean active) {
		this.active = active;
	} // setActive
	
	/**
	 * @return Created joda datetime
	 */
	public DateTime getCreated() {
		return created;
	} // getCreated

	/**
	 * @return the last seen date
	 */
	public DateTime getLastSeen() {
		return lastSeen;
	} // getLastSeen

	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(DateTime lastSeen) {
		this.lastSeen = lastSeen;
	} // setLastSeen

	/**
	 * @return the room
	 */
	public int getRoom() {
		return room;
	} // getRoom

	/**
	 * @param room the room to set
	 */
	public void setRoom(int room) {
		this.room = room;
	} // setRoom

	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	} // getFloor

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	} // setFloor

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	} // getGroup

	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	} // setGroup

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	} // getX

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	} // setX

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	} // getY
	
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	} // setY
	
	/**
	 * @return SpotType The type of the spot
	 */
	public abstract SpotType getType();
	
	/**
	 * Return key character for this type of spot (e.g. "T" for
	 * tag or "R" for reader). Has to be overridden.
	 */
	protected abstract String getKeyCharacter();
}
