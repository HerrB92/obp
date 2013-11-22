/**
 * 
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
 * @author bbehrens
 *
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
	
	public Tracking() {
		this(0, "", TrackingAction.Unknown, 0, 0, false, EstimationMethod.None);
	}
	
	public Tracking(long runId, String tagKey, TrackingAction action, 
					int x, int y, boolean button, EstimationMethod method) {
		this.runId = runId;
		this.timestamp = DateTime.now();
		this.tagKey = tagKey;
		this.action = action;
		this.x = x;
		this.y = y;
		this.button = button;
		this.method = method;
	}
}