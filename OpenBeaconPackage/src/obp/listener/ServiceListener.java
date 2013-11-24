/**
 * 
 */
package obp.listener;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import obp.ServiceConfiguration;
import obp.index.DataIndex;
import obp.spots.Reader;
import obp.tag.Tag;
import obp.tag.TagProximitySighting;
import obp.tag.estimation.DefaultPositionEstimator;
import obp.tag.estimation.PositionEstimator;
import odp.service.listener.Listener;
import odp.service.sighting.ProximitySighting;
import odp.service.sighting.TagSighting;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private DataIndex dataIndex;
	private PositionEstimator estimator;
	
	/**
	 * @param dataIndex
	 * @param estimator
	 */
	public ServiceListener(DataIndex dataIndex, PositionEstimator estimator) {
		setDataIndex(dataIndex);
		setPositionEstimator(estimator);
	} // Constructor
	
	/**
	 * @param dataIndex
	 */
	public ServiceListener(DataIndex dataIndex) {
		this(dataIndex, DefaultPositionEstimator.getInstance());
	} // Constructor

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
	
	/**
	 * @return the position estimator
	 */
	private PositionEstimator getPositionEstimator() {
		return estimator;
	}
	
	/**
	 * @param estimator the position estimator class reference to set
	 */
	private void setPositionEstimator(PositionEstimator estimator) {
		this.estimator = estimator;
	}
	
	private HashMap<Integer, TagProximitySighting> convertProximitySightings(Tag tag, ArrayList<ProximitySighting> rawSightings) {
		HashMap<Integer, TagProximitySighting> sightings = new HashMap<Integer, TagProximitySighting>();
		
		if (rawSightings != null && rawSightings.size() > 0) {
			Tag otherTag;
			for (ProximitySighting sighting : rawSightings) {
				otherTag = getDataIndex().getTagById(sighting.getTagId());
				
				if (otherTag != null) {
					sightings.put(otherTag.getId(), new TagProximitySighting(tag, otherTag, sighting.getStrength(), sighting.getCount()));
				}
			}
		}
		
		return sightings;
	} // convertProximitySightings

	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	@Override
	public void messageReceived(TagSighting tagSighting) {
		if (getDataIndex() != null) {
			DateTime now = DateTime.now();
			
			if (configuration.isValidReader(tagSighting.getReaderId())) {
				// Reader is known, update data
				configuration.getReader(tagSighting.getReaderId()).setLastSeen(now);
				
				Tag tag = getDataIndex().getTagById(tagSighting.getTagId());
				if (tag == null) {
					tag = new Tag(tagSighting.getTagId(), getPositionEstimator());
					getDataIndex().addTag(tag);
				}
				tag.setLastSeen(now);
				tag.updateTagReaderSighting(tagSighting.getReaderId(), tagSighting.getStrength());
				tag.updateProximitySightings(convertProximitySightings(tag, tagSighting.getProximitySightings()));
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