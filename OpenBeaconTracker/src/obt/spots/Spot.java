/**
 * 
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
 * @author bbehrens
 *
 */
@MappedSuperclass
public abstract class Spot implements Serializable {
	private static final long serialVersionUID = -2171247901903093310L;
	
	@Id
	@Column(nullable = false)
	private int id;
	
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
	
	// The column name "group" is not allowed in MySQL/MariaDB
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
	private DateTime lastSeen = DateTime.now();
	
	protected Spot() {
		this(0, "-", true, 0, 0, 0, 0, 0);
	} // Constructor (for Hibernate)
	
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
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(int room) {
		this.room = room;
	}

	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public abstract SpotType getType();
	protected abstract String getKeyCharacter();
}
