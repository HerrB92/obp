/**
 * 
 */
package obp.listener;

import org.joda.time.DateTime;

import obp.index.DataIndex;
import obp.tag.Tag;
import obp.tag.TagSighting;
import odp.reader.Reader;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	
	private DataIndex dataIndex; 
	
	public ServiceListener() { } // Constructor
	
	/**
	 * @param dataIndex
	 */
	public ServiceListener(DataIndex dataIndex) {
		setDataIndex(dataIndex);
	} // Constructor

	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	/**
	 * @return the dataIndex
	 */
	private DataIndex getDataIndex() {
		return dataIndex;
	}
	
	/**
	 * @param dataIndex the dataIndex to set
	 */
	private void setDataIndex(DataIndex dataIndex) {
		this.dataIndex = dataIndex;
	}

	@Override
	public void messageReceived(TagSighting tagSighting) {
		if (getDataIndex() != null) {
			DateTime now = DateTime.now();
			
			Tag tag = getDataIndex().getTagById(tagSighting.getTagId());
			if (tag == null) {
				tag = new Tag(tagSighting.getTagId());
				getDataIndex().addTag(tag);
			}
			tag.setLastSeen(now);
			tag.setButtonPressed(tagSighting.getTagButtonPressed());
			
			Reader reader = getDataIndex().getReaderById(tagSighting.getReaderId());
			if (reader == null) {
				reader = new Reader(tagSighting.getReaderId());
				getDataIndex().addReader(reader);
			}
			
			reader.setLastSeen(now);
		}
	} // messageReceived
}