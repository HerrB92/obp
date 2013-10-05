/**
 * 
 */
package obp.tag;

/**
 * @author bbehrens
 *
 */
public class Tag {
	private int tagId;
	private int tagProtocol;
	private int tagFlags;
	private int tagStrength;
	private int tagSequence;
	private boolean tagButtonPressed = false;
	
	private int readerInterface;
	private int readerId;
	private int sequence;
	private int timestamp;
	
	private int[] proxTagId = new int[4];

	/**
	 * @param tagId
	 * @param tagButtonPressed
	 */
	public Tag(int tagId, boolean tagButtonPressed) {
		this.tagId = tagId;
		this.tagButtonPressed = tagButtonPressed;
	} // Constructor

	/**
	 * @return the tagId
	 */
	public int getTagId() {
		return tagId;
	}

	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the tagProtocol
	 */
	public int getTagProtocol() {
		return tagProtocol;
	}

	/**
	 * @param tagProtocol the tagProtocol to set
	 */
	public void setTagProtocol(int tagProtocol) {
		this.tagProtocol = tagProtocol;
	}

	/**
	 * @return the tagFlags
	 */
	public int getTagFlags() {
		return tagFlags;
	}

	/**
	 * @param tagFlags the tagFlags to set
	 */
	public void setTagFlags(int tagFlags) {
		this.tagFlags = tagFlags;
	}

	/**
	 * @return the tagStrength
	 */
	public int getTagStrength() {
		return tagStrength;
	}

	/**
	 * @param tagStrength the tagStrength to set
	 */
	public void setTagStrength(int tagStrength) {
		this.tagStrength = tagStrength;
	}

	/**
	 * @return the tagSequence
	 */
	public int getTagSequence() {
		return tagSequence;
	}

	/**
	 * @param tagSequence the tagSequence to set
	 */
	public void setTagSequence(int tagSequence) {
		this.tagSequence = tagSequence;
	}

	/**
	 * @return the tagButtonPressed
	 */
	public boolean isTagButtonPressed() {
		return tagButtonPressed;
	}

	/**
	 * @param tagButtonPressed the tagButtonPressed to set
	 */
	public void setTagButtonPressed(boolean tagButtonPressed) {
		this.tagButtonPressed = tagButtonPressed;
	}

	/**
	 * @return the readerInterface
	 */
	public int getReaderInterface() {
		return readerInterface;
	}

	/**
	 * @param readerInterface the readerInterface to set
	 */
	public void setReaderInterface(int readerInterface) {
		this.readerInterface = readerInterface;
	}

	/**
	 * @return the readerId
	 */
	public int getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(int readerId) {
		this.readerId = readerId;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the timestamp
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the proxTagId
	 */
	public int[] getProxTagId() {
		return proxTagId;
	}

	/**
	 * @param proxTagId the proxTagId to set
	 */
	public void setProxTagId(int[] proxTagId) {
		this.proxTagId = proxTagId;
	}
}