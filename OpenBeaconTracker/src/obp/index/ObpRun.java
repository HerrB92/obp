/**
 * 
 */
package obp.index;

import java.io.Serializable;
import java.text.MessageFormat;

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
public class ObpRun implements Serializable {
	private static final long serialVersionUID = -6176488860634980627L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long runId;
	
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="obp_run_seq")
//	@SequenceGenerator(name = "SQ_OBP_RUN", sequenceName = "obp_run_seq")
//  @GenericGenerator(name="increment", strategy = "increment")
	
	//@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	
	@Column
	@Type(type="obp.persistence.joda.PersistentDateTime")
	private DateTime timeStamp;

	public ObpRun() {
		this(DateTime.now());
	} // Constructor (default)
	
	public ObpRun(DateTime timeStamp) {
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
		return MessageFormat.format("Run: {0} {1}", new Object[] { runId, timeStamp });
	} // toString
}
