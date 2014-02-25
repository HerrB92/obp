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
package obt.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import obt.tag.estimation.EstimationMethod;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Class used to store the tracking updates in the database.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
@Entity
public class Tracking { 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private long runId;
	
	@Column
	@Type(type="obt.persistence.joda.PersistentDateTime")
	private DateTime timestamp;
	
	@Column
	private String tagKey;
	
	@Column
	private int trackId;
	
	@Column
	@Enumerated(EnumType.STRING)
	private TrackingAction action = TrackingAction.Unknown;
	
	@Column
	private int x;
	
	@Column
	private int y;
	
	@Column
	private boolean button;
	
	@Column
	@Enumerated(EnumType.STRING)
	private EstimationMethod method;
	
	@Column
	@Type(type="obt.persistence.joda.PersistentDateTime")
	private DateTime lastseen;
	
	/**
	 * Constructor which initializes the properties with default values.
	 */
	public Tracking() {
		this(0, "", 0, TrackingAction.Unknown, 0, 0, false, EstimationMethod.None, DateTime.now());
	} // Constructor
	
	/**
	 * Full qualified constructor.
	 * 
	 * @param runId		Run id
	 * @param tagKey	Tag key
	 * @param trackId	ID of the related track/trail
	 * @param action	Tracking event (e.g. Register or Spot)
	 * @param x			Estimated x position
	 * @param y			Estimated y position
	 * @param button	Button pressed (true/false)
	 * @param method	Position estimation method
	 * @param lastseen	Last seen time stamp
	 */
	public Tracking(long runId, String tagKey, int trackId, TrackingAction action, 
					int x, int y, boolean button, EstimationMethod method, DateTime lastseen) {
		this.runId = runId;
		this.timestamp = DateTime.now();
		this.tagKey = tagKey;
		this.trackId = trackId;
		this.action = action;
		this.x = x;
		this.y = y;
		this.button = button;
		this.method = method;
		this.lastseen = lastseen;
	} // Constructor (full)
}