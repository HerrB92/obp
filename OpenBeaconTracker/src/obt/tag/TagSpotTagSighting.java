/**
 * 
 */
package obt.tag;

import obt.configuration.ServiceConfiguration;
import obt.spots.Spot;

/**
 * @author bbehrens
 *
 */
public class TagSpotTagSighting extends SpotSighting<Spot> {		
	public TagSpotTagSighting(Spot spotTag, int strength) {
		// FIXME: Check, if it make sense that aging and active time are equal
		super(spotTag, strength, 
			  ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds(),
		      ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds());
	} // Constructor
}
