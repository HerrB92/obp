/**
 * 
 */
package obt.tag;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author bbehrens
 *
 */
@Entity
@Table(name="TagKeys")
public class TagKey implements Serializable {
	private static final long serialVersionUID = 7321398494969398781L;

	@Id
	@Column(nullable = false)
	private String name;
	
	@Id
	@Column(nullable = false)
	private Long runId;
	
	@Column(nullable = false)
	private boolean active;
	
	@Column(length=10, nullable = false)
	private String keyPart1;
	
	@Column(length=10, nullable = false)
	private String keyPart2;
	
	@Column(length=10, nullable = false)
	private String keyPart3;
	
	@Column(length=10, nullable = false)
	private String keyPart4;
	
	@Transient
	private long[] tagKey = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
		
	protected TagKey() {
		this("-", true, "0x00112233", "0x44556677", "0x8899aabb", "0xccddeeff");
	} // Constructor (for Hibernate)
	
	protected TagKey(String name, boolean active, String keyPart1, String keyPart2, String keyPart3, String keyPart4) {
		setName(name);
		setActive(active);
		setKeyPart1(keyPart1);
		setKeyPart2(keyPart2);
		setKeyPart3(keyPart3);
		setKeyPart4(keyPart4);
	} // Constructor
		
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
	 * @param keyPart1
	 */
	public void setKeyPart1(String keyPart1) {
		this.keyPart1 = keyPart1;
		updateTagKey(0, getKeyPart1AsLong());
	} // setKeyPart1
	
	/**
	 * @return keyPart1
	 */
	public String getKeyPart1() {
		return keyPart1;
	} // getKeyPart1
	
	/**
	 * @return keyPart1 as long value
	 */
	public Long getKeyPart1AsLong() {
		return Long.decode(getKeyPart1());
	} // getKeyPart1AsLong
	
	/**
	 * @param keyPart2
	 */
	public void setKeyPart2(String keyPart2) {
		this.keyPart2 = keyPart2;
		updateTagKey(1, getKeyPart2AsLong());
	} // setKeyPart2
	
	/**
	 * @return keyPart2
	 */
	public String getKeyPart2() {
		return keyPart2;
	} // getKeyPart2
	
	/**
	 * @return keyPart2 as long value
	 */
	public Long getKeyPart2AsLong() {
		return Long.decode(getKeyPart2());
	} // getKeyPart2AsLong
	
	/**
	 * @param keyPart3
	 */
	public void setKeyPart3(String keyPart3) {
		this.keyPart3 = keyPart3;
		updateTagKey(2, getKeyPart3AsLong());
	} // setKeyPart3
	
	/**
	 * @return keyPart3
	 */
	public String getKeyPart3() {
		return keyPart3;
	} // getKeyPart3
	
	/**
	 * @return keyPart3 as long value
	 */
	public Long getKeyPart3AsLong() {
		return Long.decode(getKeyPart3());
	} // getKeyPart3AsLong
	
	/**
	 * @param keyPart4
	 */
	public void setKeyPart4(String keyPart4) {
		this.keyPart4 = keyPart4;
		updateTagKey(3, getKeyPart4AsLong());
	} // setKeyPart4
	
	/**
	 * @return keyPart4
	 */
	public String getKeyPart4() {
		return keyPart4;
	} // getKeyPart4
	
	/**
	 * @return keyPart4 as long value
	 */
	public Long getKeyPart4AsLong() {
		return Long.decode(getKeyPart4());
	} // getKeyPart4AsLong
	
	private void updateTagKey(int part, long value) {
		tagKey[part] = value;
	} // updateTagKey
	
	/**
	 * @return tag key as array of long (size 4)
	 */
	public long[] getTagKey() {
		return tagKey;
	} // getTagKey
}
