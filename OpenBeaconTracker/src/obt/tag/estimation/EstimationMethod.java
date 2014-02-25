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

/**
 * Enumerator values used to describe the determination method used 
 * to estimate the position for the location tracking event stored
 * in the database.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public enum EstimationMethod {
	None,					// Default
	OneReader,				// Cell of Origin (guessed as near the reader)
	TwoReaders,				// Between two readers (weighted by signal strength)
	ReaderTrilateration,	// Between three readers (weighted by signal strength)
	OneSpotTag,				// Cell of Origin (guessed as near the spot tag)
	TwoSpotTags,			// Between two tags
	SpotTagTrilateration	// Between three tags
}