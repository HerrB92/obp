/**
 * 
 */
package obt.listener;

import java.util.ArrayList;

import org.hibernate.Session;
import org.joda.time.DateTime;

import obt.configuration.ServiceConfiguration;
import obt.index.DataIndex;
import obt.persistence.DatabaseSessionFactory;
import obt.spots.Spot;
import obt.tag.Tag;
import obt.tag.Tracking;
import obt.tag.TrackingAction;
import obt.tag.estimation.DefaultPositionEstimator;
import obt.tag.estimation.PositionEstimator;
import odp.service.listener.Listener;
import odp.service.listener.ProximitySighting;
import odp.service.listener.TagSighting;

/**
 * @author bbehrens
 *
 */
public class ServiceListener implements Listener {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private DataIndex dataIndex;
	private long runId;
	private PositionEstimator estimator;
	
	/**
	 * @param dataIndex
	 * @param estimator
	 */
	public ServiceListener(DataIndex dataIndex, long runId, PositionEstimator estimator) {
		setDataIndex(dataIndex);
		setRunId(runId);
		setPositionEstimator(estimator);
	} // Constructor
	
	/**
	 * @param dataIndex
	 */
	public ServiceListener(DataIndex dataIndex, long runId) {
		this(dataIndex, runId, DefaultPositionEstimator.getInstance());
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
	 * @return the run id
	 */
	private long getRunId() {
		return runId;
	}
	
	/**
	 * @param rundId the id of the current run
	 */
	private void setRunId(long runId) {
		this.runId = runId;
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
	
	/**
	 * @param tag
	 */
	private void saveTrack(Tag tag, TrackingAction action) {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		session.save(new Tracking(getRunId(), tag.getKey(), action, tag.getX(), tag.getY(), tag.isButtonPressed()));
		session.getTransaction().commit();
	} // saveTrack
	
	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	@Override
	public void messageReceived(TagSighting tagSighting) {
		if (getDataIndex() != null) {
			DateTime now = DateTime.now();
			String readerKey = "R" + tagSighting.getReaderId();
			Tag tag;
			
			if (configuration.isValidReader(readerKey)) {
				// Reader is known, update data
				configuration.getReader(readerKey).setLastSeen(now);
				
				String tagKey = "T" + tagSighting.getTagId();
				
				if (configuration.isSpotTag(tagKey)) {
					// A spot tag can be a spot tag, a register tag or an unregister tag
					// Get proximity sightings and if the current tag is a:
					// Spot Tag: update position of moving tag
					// Register Tag: register moving tag and update position
					// Unregister Tag: unregister moving tag and update position
					Spot spotTag = configuration.getSpot(tagKey);
					spotTag.setLastSeen(DateTime.now());
					
					ArrayList<ProximitySighting> rawSightings = tagSighting.getProximitySightings();
					
					if (rawSightings != null && rawSightings.size() > 0) {
						String otherTagKey;
						
						for (ProximitySighting sighting : rawSightings) {
							otherTagKey = "T" + sighting.getTagId();
							
							if (!configuration.isSpotTag(otherTagKey)) {
								tag = getDataIndex().getTagByKey(otherTagKey);
								
								if (tag == null) {
									// new tag
									tag = new Tag(tagSighting.getTagId(), getPositionEstimator());
									getDataIndex().addTag(tag);
								}
								
								if (!tag.isRegistered() && configuration.isRegisterTag(tagKey)) {
									tag.setRegistered(true);
									saveTrack(tag, TrackingAction.Register);
								} else if (tag.isRegistered() && configuration.isUnRegisterTag(tagKey)) {
									tag.setRegistered(false);
									saveTrack(tag, TrackingAction.UnRegister);
								}
								
								tag.addSpotTagSighting(spotTag, sighting.getStrength());
								
								if (tag.needsEstimation()) {
									tag.updatePositionEstimation();
									
									if (tag.isRegistered()) {
										saveTrack(tag, TrackingAction.Spot);
									}
								}
							}
						}
					}
				} else {
					tag = getDataIndex().getTagByKey(tagKey);
					
					if (tag == null) {
						// new tag
						tag = new Tag(tagSighting.getTagId(), getPositionEstimator());
						getDataIndex().addTag(tag);
					}
					
					tag.setLastReaderKey("R" + tagSighting.getReaderId());
					tag.setLastSeen(now);
					
					if (tagSighting.getProximitySightings() != null && 
						tagSighting.getProximitySightings().size() > 0) {
						
						String otherTagKey;
						for (ProximitySighting sighting : tagSighting.getProximitySightings()) {
							otherTagKey = "T" + sighting.getTagId();
							
							if (configuration.isSpotTag(otherTagKey)) {
								if (!tag.isRegistered() && configuration.isRegisterTag(otherTagKey)) {
									tag.setRegistered(true);
									saveTrack(tag, TrackingAction.Register);
								} else if (tag.isRegistered() && configuration.isUnRegisterTag(otherTagKey)) {
									tag.setRegistered(false);
									saveTrack(tag, TrackingAction.UnRegister);
								}
								
								tag.addSpotTagSighting(configuration.getSpot(otherTagKey), sighting.getStrength());
							} else {
								tag.addProximitySighting(sighting);
							}
						}
					}
					
					tag.setButtonPressed(tagSighting.isTagButtonPressed());
					
					if (tag.needsEstimation()) {
						tag.updatePositionEstimation();
						
						if (tag.isRegistered()) {
							saveTrack(tag, TrackingAction.Spot);
						}
					}
				}
			} else {
				System.out.println("Unknown reader: " + tagSighting.getReaderId());
			}
		}
	} // messageReceived
}