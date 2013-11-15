/**
 * 
 */
package obp.tag;

import obp.ServiceConfiguration;
import obp.spots.Reader;

/**
 * @author bbehrens
 *
 */
public class TagReaderSighting extends SpotSighting<Reader> {
	public TagReaderSighting(Reader spot, int strength) {
		super(spot, strength, 
			  ServiceConfiguration.getInstance().getStrengthAggregationAgedSeconds(),
			  ServiceConfiguration.getInstance().getTagReaderSightingActiveSeconds());
	}
}
