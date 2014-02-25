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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class to store the raw data of tag sightings in the database.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
@Entity
public class RawData { 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private long runId;
	
	@Column
	private byte[] rawData;
	
	/**
	 * Light constructor
	 */
	public RawData() {
		this(0, new byte[]{});
	} // Constructor
	
	/**
	 * Full qualified constructor
	 * 
	 * @param runId
	 * @param rawData
	 */
	public RawData(long runId, byte[] rawData) {
		this.runId = runId;
		this.rawData = rawData;
	} // Constructor (full)
}