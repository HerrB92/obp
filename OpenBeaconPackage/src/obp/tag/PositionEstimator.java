/**
 * 
 */
package obp.tag;

import java.util.ArrayList;

import obp.ServiceConfiguration;
import obp.reader.Reader;
import obp.service.Constants;

/**
 * @author bbehrens
 *
 */
public class PositionEstimator {
	private final ServiceConfiguration configuration = ServiceConfiguration.getInstance();
	private static PositionEstimator estimator = null;
	
	private int x = 0;
	private int y = 0;
	
	private PositionEstimator() {}
	
	public static PositionEstimator getInstance() {
		if (estimator == null) {
			estimator = new PositionEstimator();
		}
		
		return estimator;
	} // getInstance
	
	public void estimate(Tag tag) {
		setX(Constants.NOT_DEFINED);
		setY(Constants.NOT_DEFINED);
		
		ArrayList<TagReaderSighting> sightings = tag.getActiveTagReaderSightings();
		
		if (sightings.size() == 0) {
			// If there is no reader seeing this tag, we have
			// left the building...
			return;
		}
		
		TagReaderSighting sighting;
		if (sightings.size() == 1) {
			// If there is only one active reader sighting, than
			// we can only predict that we are at the position of the 
			// reader with the inaccuracy of the highest possible power 
			// level minus the provided minimal power level at the 
			// last update.
			sighting = sightings.get(0);
			Reader reader = sighting.getReader();
			setX(reader.getX());
			setY(reader.getY());
			
			System.out.println("1: R: " + reader.getId() + " X: " + getX() + " Y: " + getY() + " S: " + sighting.getMinStrength());
			return;
		}
		
		if (sightings.size() == 2) {
			sighting = sightings.get(0);
			Reader reader1 = sighting.getReader();
			int x1 = reader1.getX();
			int y1 = reader1.getY();
			int strength1 = sighting.getMinStrength() + 1; // +1: Avoid 0
			
			sighting = sightings.get(1);
			Reader reader2 = sighting.getReader();
			int x2 = reader2.getX();
			int y2 = reader2.getY();
			int strength2 = sighting.getMinStrength() + 1; // +1: Avoid 0
			
			Long distance = configuration.getDistance(reader1.getId(), reader2.getId());
			
			Long radius1 = distance * strength1/(strength1 + strength2);
			Long radius2 = distance - radius1;
			
			// Pythagoras:
			// radius1^2 = (X - R1x)^2 + (Y - R1y)^2
			// radius2^2 = (X - R2x)^2 + (Y - R2y)^2
			// -> 
			
			// FIXME: Mostly this will result in something funny...
			double factor = radius1/radius2;
			setX(((int)Math.round(x1 + (factor * Math.abs(x2 - x1)))));
			setY(((int)Math.round(y1 + (factor * Math.abs(y2 - y1)))));
			
			System.out.println("2: X: " + getX() + " Y: " + getY() + " S1: " + strength1 + " S2: " + strength2);
			return;
		}
		
		if (sightings.size() > 2) {
			// Use Trilateration to estimate a position of the first three
			// reader sightings.
			
			// Uses information from http://en.wikipedia.org/wiki/Trilateration, referencing
			// Manolakis, D.E (2011) 'Efficient Solution and Performance Analysis of 
			// 3-D Position Estimation by Trilateration' IEEE Transactions on
			// Aerospace And Electronic Systems, vol. 32, no. 4, pp. 1239-1248
			// Available at: http://www.general-files.org/download/gs5bac8d73h32i0/IEEE-AES-96-Efficient%20Solution-and-Performance-Analysis-of-3-D%20Position-Estimation-by-Trilateration.pdf.html#
			// Accessed: 18.10.2013
			// and Reba, D. (2012) Answer to '2d trilateration' stackoverflow.com [Online]
			// Available at: http://stackoverflow.com/a/9754358 (Accessed: 18.10.2013)
			
			sighting = sightings.get(0);
			Reader reader1 = sighting.getReader();
			int x1 = reader1.getX();
			int y1 = reader1.getY();
			int strength1 = sighting.getMinStrength() + 1;
			
			sighting = sightings.get(1);
			Reader reader2 = sighting.getReader();
			int x2 = reader2.getX();
			int y2 = reader2.getY();
			int strength2 = sighting.getMinStrength() + 1;
			//int radius2 = (4 - sighting.getMinStrength()) * 100;
			//int reader2 = sighting.getReaderId();
			
			sighting = sightings.get(2);
			Reader reader3 = sighting.getReader();
			int x3 = reader3.getX();
			int y3 = reader3.getY();
			int strength3 = sighting.getMinStrength() + 1;
			//int radius3 = sighting.getReaderId();
			
			// Get the greatest distance between the identified readers
			// and use this distance to estimate a radius based on the
			// signal strength
			
			Long distance;
			distance = configuration.getDistance(reader1.getId(), reader2.getId());
			
			if (configuration.getDistance(reader2.getId(), reader3.getId()) > distance) {
				distance = configuration.getDistance(reader2.getId(), reader3.getId());
			}
			
			if (configuration.getDistance(reader3.getId(), reader1.getId()) > distance) {
				distance = configuration.getDistance(reader3.getId(), reader1.getId());
			}
									
			long radius1 = distance * strength1 / (Constants.STRENGTH_MAX_LEVEL + 1);
			long radius2 = distance * strength2 / (Constants.STRENGTH_MAX_LEVEL + 1);
			long radius3 = distance * strength3 / (Constants.STRENGTH_MAX_LEVEL + 1);

			System.out.println("Distance: " + distance + 
					" R1: " + reader1.getId() + " (" + strength1 + "/" + radius1 + "/" + x1 + "/" + y1 + ")" +
					" R2: " + reader2.getId() + " (" + strength2 + "/" + radius2 + "/" + x2 + "/" + y2 + ")" + 
					" R3: " + reader3.getId() + " (" + strength3 + "/" + radius3 + "/" + x3 + "/" + y3 + ")");
			
			// ex = (P2 - P1) / ‖P2 - P1‖, result: vector
			double exx = (x2 - x1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
			double exy = (y2 - y1)/Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
			
//			System.out.println("Exx: " + exx);
//			System.out.println("Exy: " + exy);
			
			// i = ex · (P3 - P1), result: scalar
			double i = exx * (x3 - x1) + exy * (y3 - y1);
//			System.out.println("i: " + i);
			
			// ey = (P3 - P1 - i · ex) / ‖P3 - P1 - i · ex‖, result: vector
			double eyx = (x3 - x1 - (i * exx))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
			double eyy = (y3 - y1 - (i * exy))/Math.sqrt(Math.pow(x3 - x1 - (i * exx), 2) + Math.pow(y3 - y1 - (i * exy), 2));
			
			// d = ‖P2 - P1‖, result: scalar
			double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
//			System.out.println("d: " + d);
			
			// j = ey · (P3 - P1), result: scalar
			double j = eyx * (x3 - x1) + eyy * (y3 - y1);
//			System.out.println("j: " + j);
			
			// x = (r1^2 - r2^2 + d2) / 2d, result: scalar
			double x = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(d, 2)) / (2 * d);
//			System.out.println("x: " + x);
			
			// y = (r1^2 - r3^2 + i2 + j2) / 2j - ix / j, result: scalar
			double y = (Math.pow(radius1, 2) - Math.pow(radius3, 2) + Math.pow(i, 2) + Math.pow(j, 2)) / (2 * j) - (i/j) * x;
//			System.out.println("y: " + y);
			
			// p1,2 = P1 + x*ex + y*ey
			double xPos = x1 + x*exx + y*eyx;
			double yPos = y1 + x*exy + y*eyy;
			
			setX(((int)Math.round(xPos)));
			setY(((int)Math.round(yPos)));
			
			System.out.println("3: X: " + getX() + ", Y: " + getY());
		}
	}
	
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
}