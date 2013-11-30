/**
 * 
 */
package obt.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author bbehrens
 *
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
	
	public RawData() {
		this(0, new byte[]{});
	} // Constructor
	
	public RawData(long runId, byte[] rawData) {
		this.runId = runId;
		this.rawData = rawData;
	} // Constructor (full)
}