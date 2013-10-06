/**
 * 
 */
package odp.reader;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public class Reader {
	private int id;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	
	public Reader(int id) {
		setId(id);
	} // Constructor

	/**
	 * @return the reader id
	 */
	public int getId() {
		return id;
	} // getId

	/**
	 * @param id the reader id to set
	 */
	public void setId(int id) {
		this.id = id;
	} // setId
	
	/**
	 * @return Created joda datetime
	 */
	public DateTime getCreated() {
		return created;
	} // getCreated

	/**
	 * @return the last seen date
	 */
	public DateTime getLastSeen() {
		return lastSeen;
	} // getLastSeen

	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(DateTime lastSeen) {
		this.lastSeen = lastSeen;
	} // setLastSeen
}