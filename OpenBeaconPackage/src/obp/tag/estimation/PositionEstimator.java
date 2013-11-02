/**
 * 
 */
package obp.tag.estimation;

import obp.tag.Tag;

/**
 * @author bbehrens
 *
 */
public interface PositionEstimator {
	public void estimate(Tag tag);
	public int getX();
	public int getY();
	public EstimationMethod getMethod();
}
