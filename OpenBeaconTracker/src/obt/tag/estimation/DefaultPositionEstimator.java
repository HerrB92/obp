/**
 * 
 */
package obt.tag.estimation;

import java.util.ArrayList;

import obs.service.Constants;
import obt.configuration.ServiceConfiguration;
import obt.spots.Spot;
import obt.spots.SpotType;
import obt.tag.Tag;
import obt.tag.TagSpotTagSighting;

/**
 * @author bbehrens
 *
 */
public class DefaultPositionEstimator implements PositionEstimator {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	private static DefaultPositionEstimator estimator = null;
	
	private int x = 0;
	private int y = 0;
	private EstimationMethod method = EstimationMethod.None;
	
	private DefaultPositionEstimator() {}
	
	public static PositionEstimator getInstance() {
		if (estimator == null) {
			estimator = new DefaultPositionEstimator();
		}
		
		return estimator;
	} // getInstance
	
	/**
	 * If there is only one active reader seeing the tag, than
	 * we can only predict that we are at the position of the 
	 * reader (with the inaccuracy of the highest possible power 
	 * level minus the provided minimal power level at the 
	 * last update).
	 * 
	 * @param method
	 * @param x
	 * @param y
	 */
	private void estimateBySingleRecord(EstimationMethod method, int x, int y) {
		setX(x);
		setY(y);
		setMethod(method);
	} // estimateBySingleRecord
	
	/**
	 * If there are two readers, then we are likely somewhere between them
	 * (at least, if the reader ranges would not overlap - which they might 
	 * often do. But if we assume that all readers are located on rather "outer 
	 * edges" the assumption is better than the alternative).
	 * last update).
	 * 
	 * @param method
	 * @param x1
	 * @param y1
	 * @param strength1
	 * @param x2
	 * @param y2
	 * @param strength2
	 */
	private void estimateByTwoRecords(EstimationMethod method, int x1, int y1, int strength1, int x2, int y2, int strength2) {
		strength1 += 1; // +1: Avoid 0
		strength2 += 1; // +1: Avoid 0
		
		double factor = strength1 / (double)(strength1 + strength2);
		
		// Simple formula: The position is calculated using the delta
		// between the positions (here for x) and the factor
		// "part of strength1 of the sum of strength 1 and 2"
		// to calculate how much of the delta has to be added to
		// one reader position to get to the estimated tag position.
		// 
		// It is trivial to show, that a tag can be really 
		// far, far away from this position and still show the
		// same strength and reader detection data.
		setX((int)Math.round(x1 + ((x2 - x1) * factor)));
		setY((int)Math.round(y1 + ((y2 - y1) * factor)));
		setMethod(method);
	} // estimateByTwoRecords
	
	/**
	 * Use Trilateration to estimate a position of the first three
     * reader sightings.
     * 	
     * Uses information from http://en.wikipedia.org/wiki/Trilateration, referencing
     * Manolakis, D.E (2011) 'Efficient Solution and Performance Analysis of 
     * 3-D Position Estimation by Trilateration' IEEE Transactions on
     * Aerospace And Electronic Systems, vol. 32, no. 4, pp. 1239-1248
     * Available at: http://www.general-files.org/download/gs5bac8d73h32i0/IEEE-AES-96-Efficient%20Solution-and-Performance-Analysis-of-3-D%20Position-Estimation-by-Trilateration.pdf.html#
     * Accessed: 18.10.2013
     *  
     * Reba, D. (2012) Answer to '2d trilateration' stackoverflow.com [Online]
     * Available at: http://stackoverflow.com/a/9754358 (Accessed: 18.10.2013)
	 * 
	 * @param method
	 * @param x1
	 * @param y1
	 * @param strength1
	 * @param x2
	 * @param y2
	 * @param strength2
	 * @param x3
	 * @param y3
	 * @param strength3
	 */
	private void estimateByThreeRecords(EstimationMethod method, 
										int x1, int y1, int strength1,
										int x2, int y2, int strength2,
										int x3, int y3, int strength3,
										long distance12,
										long distance23,
										long distance13) {				

		// Get the greatest distance between the identified readers
		// and use this distance to estimate a radius based on the
		// signal strength
		Long distance = distance12;
		
		if (distance23 > distance) {
			distance = distance23;
		}
		
		if (distance13 > distance) {
			distance = distance13;
		}
		
		strength1 += 1; // +1: Avoid 0
		strength2 += 1; // +1: Avoid 0
		strength3 += 1; // +1: Avoid 0
														
		double radius1 = distance * strength1 / (double)(Constants.STRENGTH_MAX_LEVEL + 1);
		double radius2 = distance * strength2 / (double)(Constants.STRENGTH_MAX_LEVEL + 1);
		double radius3 = distance * strength3 / (double)(Constants.STRENGTH_MAX_LEVEL + 1);

//				System.out.println("Distance: " + distance + 
//					" R1: " + reader1.getId() + " (" + strength1 + "/" + radius1 + "/" + x1 + "/" + y1 + ")" +
//					" R2: " + reader2.getId() + " (" + strength2 + "/" + radius2 + "/" + x2 + "/" + y2 + ")" + 
//					" R3: " + reader3.getId() + " (" + strength3 + "/" + radius3 + "/" + x3 + "/" + y3 + ")");
				
		// ex = (P2 - P1) / ‖P2 - P1‖, result: vector
		double exx = (x2 - x1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		double exy = (y2 - y1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
					
		// i = ex · (P3 - P1), result: scalar
		double i = exx * (x3 - x1) + exy * (y3 - y1);
		
		// ey = (P3 - P1 - i · ex) / ‖P3 - P1 - i · ex‖, result: vector
		double eyx = (x3 - x1 - (i * exx))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
		double eyy = (y3 - y1 - (i * exy))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
		
		// d = ‖P2 - P1‖, result: scalar
		double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		
		// j = ey · (P3 - P1), result: scalar
		double j = eyx * (x3 - x1) + eyy * (y3 - y1);
		
		// x = (r1^2 - r2^2 + d2) / 2d, result: scalar
		double x = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(d, 2)) / (2 * d);
		
		// y = (r1^2 - r3^2 + i2 + j2) / 2j - ix / j, result: scalar
		double y = (Math.pow(radius1, 2) - Math.pow(radius3, 2) + Math.pow(i, 2) + Math.pow(j, 2)) / (2 * j) - (i/j) * x;
		
		// p1,2 = P1 + x*ex + y*ey
		double xPos = x1 + x*exx + y*eyx;
		double yPos = y1 + x*exy + y*eyy;
		
		setX(((int)Math.round(xPos)));
		setY(((int)Math.round(yPos)));
		setMethod(method);
	} // estimateByThreeRecords
	
	public void estimate(Tag tag) {
		setX(Constants.NOT_DEFINED);
		setY(Constants.NOT_DEFINED);
		
		ArrayList<TagSpotTagSighting> tagSightings = tag.getActiveSpotTagSightings();
		
		if (!tag.isRegistered() || tagSightings.size() == 0) {
			// If the tag is not registered or if there is no spot tag sighting 
			// seeing this tag, use the last spot available
			if (!"".equals(tag.getLastSpotTagKey())) {
				Spot spotTag = configuration.getSpot(tag.getLastSpotTagKey());
				estimateBySingleRecord(EstimationMethod.OneSpotTag, spotTag.getX(), spotTag.getY());
			}
			
			return;
		}
		
		TagSpotTagSighting tagSighting;
		Spot tag1, tag2, tag3;
		int x1, y1, x2, y2, x3, y3;
		int strength1, strength2, strength3;
		
		switch (tagSightings.size()) {
		case 1:
			tag1 = tagSightings.get(0).getSpot();
			estimateBySingleRecord(EstimationMethod.OneSpotTag, tag1.getX(), tag1.getY());
			
			//System.out.println("1: R: " + reader.getId() + " X: " + getX() + " Y: " + getY() + " S: " + sighting.getMinStrength());
			break;
		case 2:
			tagSighting = tagSightings.get(0);
			tag1 = tagSighting.getSpot();
			x1 = tag1.getX();
			y1 = tag1.getY();
			strength1 = tagSighting.getMinStrength();
			
			tagSighting = tagSightings.get(1);
			tag2 = tagSighting.getSpot();
			x2 = tag2.getX();
			y2 = tag2.getY();
			strength2 = tagSighting.getMinStrength();
			
			estimateByTwoRecords(EstimationMethod.TwoSpotTags, x1, y1, strength1, x2, y2, strength2);
			
			//System.out.println("2: X: " + getX() + " Y: " + getY() + " S1: " + strength1 + " S2: " + strength2);
			break;
		default:
			tagSighting = tagSightings.get(0);
			tag1 = tagSighting.getSpot();
			x1 = tag1.getX();
			y1 = tag1.getY();
			strength1 = tagSighting.getMinStrength();
			
			tagSighting = tagSightings.get(1);
			tag2 = tagSighting.getSpot();
			x2 = tag2.getX();
			y2 = tag2.getY();
			strength2 = tagSighting.getMinStrength();
			
			tagSighting = tagSightings.get(2);
			tag3 = tagSighting.getSpot();
			x3 = tag3.getX();
			y3 = tag3.getY();
			strength3 = tagSighting.getMinStrength();
			
			estimateByThreeRecords(
					EstimationMethod.SpotTagTrilateration,
					x1, y1, strength1,
					x2, y2, strength2,
					x3, y3, strength3,
					configuration.getSpotDistance(tag1.getKey(), tag2.getKey()),
					configuration.getSpotDistance(tag2.getKey(), tag3.getKey()),
					configuration.getSpotDistance(tag1.getKey(), tag3.getKey()));
			
			// System.out.println("3: X: " + getX() + ", Y: " + getY());
		}
	} // estimate
	
	private void setX(int x) {
		this.x = x;
	} // setX
		
	public int getX() {
		return x;
	} // getX
	
	private void setY(int y) {
		this.y = y;
	} // setY
	
	public int getY() {
		return y;
	} // getY
	
	private void setMethod(EstimationMethod method) {
		this.method = method;
	} // setMethod
	
	@Override
	public EstimationMethod getMethod() {
		return method;
	} // getMethod

	@Override
	public SpotType getLastEstimationSpotType() {
		return null;
	} // getLastEstimationSpotType
}