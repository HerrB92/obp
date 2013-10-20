/**
 * 
 */
package obp.listener;

import org.joda.time.DateTime;

import obp.ServiceConfiguration;
import obp.index.DataIndex;
import obp.reader.Reader;
import obp.tag.Tag;
import odp.service.listener.Listener;
import odp.service.listener.TagSighting;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	
	private DataIndex dataIndex;
	private ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
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
			
			if (configuration.isValidReader(tagSighting.getReaderId())) {
				// Reader is known, update data
				configuration.getReader(tagSighting.getReaderId()).setLastSeen(now);
				
				Tag tag = getDataIndex().getTagById(tagSighting.getTagId());
				if (tag == null) {
					tag = new Tag(tagSighting.getTagId());
					getDataIndex().addTag(tag);
				}
				tag.setLastSeen(now);
				tag.updateTagReaderSighting(tagSighting.getReaderId(), tagSighting.getStrength());
				tag.updateProximitySightings(tagSighting.getProximityTagIds());
				tag.setButtonPressed(tagSighting.isTagButtonPressed());
			} else {
				// Add unknown reader to list of unknown readers
				Reader reader = getDataIndex().getUnknownReaderById(tagSighting.getReaderId());
			
				if (reader == null) {
					reader = new Reader(tagSighting.getReaderId());
					getDataIndex().addUnknownReader(reader);
				}
			
				reader.setLastSeen(now);
			}
		}
	} // messageReceived
}