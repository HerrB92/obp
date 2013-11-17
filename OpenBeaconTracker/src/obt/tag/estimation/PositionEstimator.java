/**
 * 
 */
package obt.tag.estimation;

import obt.spots.SpotType;
import obt.tag.Tag;

/**
 * @author bbehrens
 *
 */
public interface PositionEstimator {
	public void estimate(Tag tag);
	public int getX();
	public int getY();
	public EstimationMethod getMethod();
	public SpotType getLastEstimationSpotType();
}
