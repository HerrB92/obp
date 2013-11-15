/**
 * 
 */
package obp.tag;

import obp.ServiceConfiguration;
import obp.spots.SpotTag;

/**
 * @author bbehrens
 *
 */
public class TagSpotTagSighting extends SpotSighting<SpotTag> {		
	public TagSpotTagSighting(SpotTag spot, int strength) {
		// FIXME: Check, if it make sense that aging and active time is equal
		super(spot, strength, 
			  ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds(),
		      ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds());
	} // Constructor
}
