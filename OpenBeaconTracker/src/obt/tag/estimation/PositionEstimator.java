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

import obt.spots.SpotType;
import obt.tag.Tag;

/**
 * Interface to provide an abstract PositionEstimator definition
 * which can be used to integrate alternative PositionEstimators.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public interface PositionEstimator {
	public void estimate(Tag tag);
	public int getX();
	public int getY();
	public EstimationMethod getMethod();
	public SpotType getLastEstimationSpotType();
}