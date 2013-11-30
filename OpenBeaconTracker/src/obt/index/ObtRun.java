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
 * @author bbehrens
 *
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

	public ObtRun() {
		this(DateTime.now());
	} // Constructor (default)
	
	public ObtRun(DateTime timeStamp) {
		setTimeStamp(timeStamp);
	} // Constructor

	public Long getRunId() {
		return runId;
	} // getRunId

	public void setRunId(Long runId) {
		this.runId = runId;
	} // setRunId

	
	public DateTime getTimeStamp() {
		return timeStamp;
	} // getTimeStamp

	public void setTimeStamp(DateTime timeStamp) {
		this.timeStamp = timeStamp;
	} // setTimeStamp

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Run: ");
		stringBuilder.append(runId);
		stringBuilder.append(" ");
		stringBuilder.append(timeStamp.toString());
		
		return stringBuilder.toString();
	} // toString
}
