/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package obt.listener;

import java.util.ArrayList;

import org.hibernate.Session;
import org.joda.time.DateTime;

import obs.service.listener.Listener;
import obs.service.sighting.ProximitySighting;
import obs.service.sighting.TagSighting;
import obt.configuration.ServiceConfiguration;
import obt.index.DataIndex;
import obt.persistence.DatabaseSessionFactory;
import obt.spots.Spot;
import obt.tag.RawData;
import obt.tag.Tag;
import obt.tag.Tracking;
import obt.tag.TrackingAction;
import obt.tag.estimation.DefaultPositionEstimator;
import obt.tag.estimation.PositionEstimator;

/**
 * 
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class ServiceListener implements Listener {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	
	private DataIndex dataIndex = DataIndex.getInstance();
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
	 * @param rawData
	 */
	private void saveRawData(byte[] rawData) {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		session.save(new RawData(getRunId(), rawData));
		session.getTransaction().commit();
	} // saveRawData
	
	/**
	 * @param tag
	 */
	private void saveTrack(Tag tag, TrackingAction action) {
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		session.save(new Tracking(
							getRunId(),
							tag.getKey(),
							tag.getTrackId(),
							action,
							tag.getX(),
							tag.getY(),
							tag.isButtonPressed(),
							tag.getMethod(),
							tag.getLastSeen()));
		session.getTransaction().commit();
		
		tag.resetMainDataChanged();
	} // saveTrack
	
	/* (non-Javadoc)
	 * @see obp.listener.Listener#messageReceived(obp.tag.TagSighting)
	 */
	@Override
	public void messageReceived(TagSighting tagSighting) {
		saveRawData(tagSighting.getRawData());
		
		DateTime now = DateTime.now();
		DateTime agedNow = now.minusSeconds(configuration.getTagAgedSeconds());
		String readerKey = tagSighting.getReaderKey();
		Tag tag;
		
		if (configuration.isValidReader(readerKey)) {
			// Reader is known, update data
			configuration.getReader(readerKey).setLastSeen(now);
			
			String tagKey = tagSighting.getTagKey();
			
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
						otherTagKey = sighting.getTagKey();
						
						if (!configuration.isSpotTag(otherTagKey)) {
							// A spot tag has recognized a moving tag
							
							tag = getDataIndex().getTagByKey(otherTagKey);
							
							if (tag == null) {
								// new tag
								tag = new Tag(tagSighting.getTagId(), getPositionEstimator());
								getDataIndex().addTag(tag);
							}
														
							if (tag.isRegistered() && tag.getLastSeen().isBefore(agedNow)) {
								// Tag was not active for some time; it is likely,
								// that the unregistration did not happen.
								// Unregister the tag correctly.
								
								tag.unregister(configuration.getForcedUnRegisterX(), configuration.getForcedUnRegisterY());
								saveTrack(tag, TrackingAction.UnRegisterForced);
							}
							
							tag.setLastSeen(now);
							
							if (tag.isRegistered()) {
								if (configuration.isUnRegisterTag(tagKey)) {
									tag.unregister(spotTag.getX(), spotTag.getY());
									saveTrack(tag, TrackingAction.UnRegister);
								}
							} else if (configuration.isRegisterTag(tagKey)) {
								tag.register(spotTag.getX(), spotTag.getY());
								saveTrack(tag, TrackingAction.Register);
							}
							
							if (tag.isRegistered()) {
								tag.addSpotTagSighting(spotTag, sighting.getStrength());
								
								if (tag.needsEstimation()) {
									tag.updatePositionEstimation();
								
									if (tag.isMainDataChanged()) {
										saveTrack(tag, TrackingAction.Spot);
									}
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
				
				tag.setLastReaderKey(readerKey);
				
				if (tag.isRegistered() && tag.getLastSeen().isBefore(agedNow)) {
					// Tag was not active for some time; it is likely,
					// that the unregistration did not happen.
					
					// Unregister the tag correctly.
					tag.unregister(configuration.getForcedUnRegisterX(), configuration.getForcedUnRegisterY());
					saveTrack(tag, TrackingAction.UnRegisterForced);
				}
				
				tag.setLastSeen(now);
				
				if (tagSighting.getProximitySightings() != null && 
					tagSighting.getProximitySightings().size() > 0) {
					
					String otherTagKey;
					Spot spotTag;
					for (ProximitySighting sighting : tagSighting.getProximitySightings()) {
						otherTagKey = sighting.getTagKey();
						
						if (configuration.isSpotTag(otherTagKey)) {
							spotTag = configuration.getSpot(otherTagKey);
							
							if (tag.isRegistered()) {
								if (configuration.isUnRegisterTag(otherTagKey)) {
									tag.unregister(spotTag.getX(), spotTag.getY());
									saveTrack(tag, TrackingAction.UnRegister);
								}
							} else if (configuration.isRegisterTag(otherTagKey)) {
								tag.register(spotTag.getX(), spotTag.getY());
								saveTrack(tag, TrackingAction.Register);
							}
							
							if (tag.isRegistered()) {
								tag.addSpotTagSighting(spotTag, sighting.getStrength());
							}
						} else if (tag.isRegistered()) {
							tag.addProximitySighting(sighting);
						}
					}
				}
				
				if (tag.isRegistered()) {
					tag.setButtonPressed(tagSighting.isTagButtonPressed());
					
					if (tag.needsEstimation()) {
						tag.updatePositionEstimation();
						
						if (tag.isMainDataChanged()) {
							saveTrack(tag, TrackingAction.Spot);
						}
					}
				}
			}
		} else {
			System.out.println("Unknown reader: " + tagSighting.getReaderId());
		}
	} // messageReceived
}