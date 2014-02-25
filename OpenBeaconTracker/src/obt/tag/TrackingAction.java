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

/**
 * Enumerator values used to describe the location tracking events 
 * in the database.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public enum TrackingAction {
	Unknown,			// Default
	Register,			// Registration
	Spot,				// Proximity to spot tag(s) detected
	UnRegister,			// De-registration
	UnRegisterForced	// Forced de-registration after inactivity
}