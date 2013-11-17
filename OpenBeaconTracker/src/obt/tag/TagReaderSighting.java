/**
 * 
 */
package obt.tag;

import obt.configuration.ServiceConfiguration;
import obt.spots.Reader;

/**
 * @author bbehrens
 *
 */
public class TagReaderSighting extends SpotSighting<Reader> {
	public TagReaderSighting(Reader reader, int strength) {
		super(reader, strength,
			  ServiceConfiguration.getInstance().getStrengthAggregationAgedSeconds(),
			  ServiceConfiguration.getInstance().getTagReaderSightingActiveSeconds());
	}
}
