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
package obt.tag.estimation;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import obs.service.Constants;
import obt.configuration.ServiceConfiguration;
import obt.spots.Spot;
import obt.spots.SpotType;
import obt.tag.Tag;
import obt.tag.TagSpotTagSighting;

/**
 * Default estimator class used for OpenBeaconTracker (singleton). It uses only 
 * spot tag sightings to estimate the spot position.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class DefaultPositionEstimator implements PositionEstimator {
	static final Logger logger = LogManager.getLogger(DefaultPositionEstimator.class.getName());
	
	// Configuration data, includes spot tag data
	private final ServiceConfiguration configuration = 
			ServiceConfiguration.getInstance();
	
	// Singleton class, store own instance
	private static DefaultPositionEstimator estimator = null;
	
	// Determined x-, y-positions and used method
	private int x = 0;
	private int y = 0;
	private EstimationMethod method = EstimationMethod.None;
	
	// Private constructor (singleton)
	private DefaultPositionEstimator() {}
	
	/**
	 * @return Instance
	 */
	public static PositionEstimator getInstance() {
		if (estimator == null) {
			estimator = new DefaultPositionEstimator();
		}
		
		return estimator;
	} // getInstance
	
	/**
	 * If there is only one sighting (here: to a spot tag), than we can 
	 * only predict that we are near the position of the spot tag.
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
	 * If there are two sightings (here: to two spot tags), then we are 
	 * likely somewhere between them.
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
		// one position to get to the estimated tag position.
		
		setX((int)Math.round(x1 + ((x2 - x1) * factor)));
		setY((int)Math.round(y1 + ((y2 - y1) * factor)));
		setMethod(method);
	} // estimateByTwoRecords
	
	/**
	 * Use trilateration to estimate a position of three proximity sightings.
     * 
     * Uses information from http://en.wikipedia.org/wiki/Trilateration, referencing
     * Manolakis, D.E (2011) 'Efficient Solution and Performance Analysis of 
     * 3-D Position Estimation by Trilateration' IEEE Transactions on
     * Aerospace And Electronic Systems, vol. 32, no. 4, pp. 1239-1248
     * Available at: http://www.general-files.org/download/gs5bac8d73h32i0/
     * IEEE-AES-96-Efficient%20Solution-and-Performance-Analysis-of-3-
     * D%20Position-Estimation-by-Trilateration.pdf.html#
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

		// Get the greatest distance between the identified spot tags
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

		logger.trace(String.format("Distance: %d, Strength1: %d, Radius1: %d, X1: %d, Y1: %d", distance, strength1, radius1, x1, y1));
		logger.trace(String.format("Distance: %d, Strength2: %d, Radius2: %d, X2: %d, Y2: %d", distance, strength2, radius2, x2, y2));
		logger.trace(String.format("Distance: %d, Strength3: %d, Radius3: %d, X3: %d, Y3: %d", distance, strength3, radius3, x3, y3));
				
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
	
	/**
	 * Estimate the position of the tag using the current proximity
	 * sightings of the tag. If no estimation is possible (e.g. no
	 * tag sightings), x and y are set to Constants.NOT_DEFINED.
	 * 
	 * @see obt.tag.estimation.PositionEstimator#estimate(obt.tag.Tag)
	 */
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
			
			logger.trace(String.format("OneSpotTag: X: %d, Y: %d", getX(), getY()));
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
			
			logger.trace(String.format("TwoSpotTags: X: %d, Y:%d (X1: %d, Y1: %d, Strength1: %d)", getX(), getY(), x1, y1, strength1));
			logger.trace(String.format("TwoSpotTags: X: %d, Y:%d (X2: %d, Y2: %d, Strength2: %d)", getX(), getY(), x2, y2, strength2));
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
			
			logger.trace(String.format("TriSpotTags: X: %d, Y:%d (X1: %d, Y1: %d, Strength1: %d)", getX(), getY(), x1, y1, strength1));
			logger.trace(String.format("TriSpotTags: X: %d, Y:%d (X2: %d, Y2: %d, Strength2: %d)", getX(), getY(), x2, y2, strength2));
			logger.trace(String.format("TriSpotTags: X: %d, Y:%d (X3: %d, Y3: %d, Strength3: %d)", getX(), getY(), x3, y3, strength3));
		}
	} // estimate
	
	/**
	 * @param x
	 */
	private void setX(int x) {
		this.x = x;
	} // setX
		
	/**
	 * @see obt.tag.estimation.PositionEstimator#getX()
	 */
	public int getX() {
		return x;
	} // getX
	
	/**
	 * @param y
	 */
	private void setY(int y) {
		this.y = y;
	} // setY
	
	/**
	 * @see obt.tag.estimation.PositionEstimator#getY()
	 */
	public int getY() {
		return y;
	} // getY
	
	/**
	 * Update estimation method used.
	 * 
	 * @param method
	 */
	private void setMethod(EstimationMethod method) {
		this.method = method;
	} // setMethod
	
	/**
	 * @see obt.tag.estimation.PositionEstimator#getMethod()
	 */
	@Override
	public EstimationMethod getMethod() {
		return method;
	} // getMethod

	/**
	 * @see obt.tag.estimation.PositionEstimator#getLastEstimationSpotType()
	 */
	@Override
	public SpotType getLastEstimationSpotType() {
		return null;
	} // getLastEstimationSpotType
}