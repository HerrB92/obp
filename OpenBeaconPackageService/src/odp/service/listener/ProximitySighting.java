/**
 * 
 */
package odp.service.listener;

/**
 * @author bbehrens
 *
 */
public class ProximitySighting {
	private int tagId;
	private int count;
	private int strength;
	
	public ProximitySighting(int tagId, int count, int strength) {
		setTagId(tagId);
		setCount(count);
		setStrength(strength);
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
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the strength
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}
}
