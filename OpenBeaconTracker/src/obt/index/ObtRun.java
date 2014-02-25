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
package obt.index;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Class to create an ongoing id per run (runId) using a database
 * sequence (auto incrementing column in SQL database).
 * 
 * @author Björn Behrens (uol@btech.de)
 * @version 1.0
 */
@Entity
public class ObtRun implements Serializable {
	private static final long serialVersionUID = -6176488860634980627L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long runId;
		
	@Column
	@Type(type="obt.persistence.joda.PersistentDateTime")
	private DateTime timeStamp;

	/**
	 * Constructor
	 * 
	 * Sets internal time stamp to "now".
	 */
	public ObtRun() {
		this(DateTime.now());
	} // Constructor (default)
	
	/**
	 * Constructor (with timeStamp parameter)
	 * 
	 * @param timeStamp
	 */
	public ObtRun(DateTime timeStamp) {
		setTimeStamp(timeStamp);
	} // Constructor

	/**
	 * @return Current run id.
	 */
	public Long getRunId() {
		return runId;
	} // getRunId

	/**
	 * Set run id.
	 * 
	 * @param runId
	 */
	protected void setRunId(Long runId) {
		this.runId = runId;
	} // setRunId

	/**
	 * @return Time stamp stored in the object
	 */
	public DateTime getTimeStamp() {
		return timeStamp;
	} // getTimeStamp

	/**
	 * Set time stamp.
	 * 
	 * @param timeStamp
	 */
	protected void setTimeStamp(DateTime timeStamp) {
		this.timeStamp = timeStamp;
	} // setTimeStamp
	
	/**
	 * Overridden toString method to support debugging.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Run: ");
		stringBuilder.append(runId);
		stringBuilder.append(" (");
		stringBuilder.append(timeStamp.toString());
		stringBuilder.append(")");
		
		return stringBuilder.toString();
	} // toString
}