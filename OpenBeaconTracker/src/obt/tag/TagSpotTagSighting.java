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
package obt.tag;

import obt.configuration.ServiceConfiguration;
import obt.spots.Spot;

/**
 * Spot tag sighting class.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class TagSpotTagSighting extends SpotSighting<Spot> {		
	public TagSpotTagSighting(Spot spotTag, int strength) {
		// FIXME: Check, if it make sense that aging and active time are equal
		super(spotTag, strength, 
			  ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds(),
		      ServiceConfiguration.getInstance().getTagSpotTagSightingActiveSeconds());
	} // Constructor
}
