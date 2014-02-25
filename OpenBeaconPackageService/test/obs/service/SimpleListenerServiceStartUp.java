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
package obs.service;

import obs.service.listener.Listener;
import obs.service.listener.ListenerService;
import obs.service.sighting.TagSighting;

/**
 * Simple, executable (includes main method) manual listener service 
 * start class, which outputs any tagSighting received on port 2342.
 * 
 * Note, that the host IP may have to be adjusted (for example, if
 * several network interfaces with different IPs are used).
 * 
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class SimpleListenerServiceStartUp implements Listener {
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	
	private SimpleListenerServiceStartUp() throws Exception {
		String host = "127.0.0.1";	// Default OpenBeacon service IP: 10.254.0.2
		int port = 2342; 			// Default OpenBeacon port
		int timeout = 10;			// Timeout in seconds

		ListenerService service = new ListenerService(host, port, timeout, key);
		service.setMessageListener(this);
		service.start();
		System.out.println("ListenerService started.");

		while (true) {
			Thread.sleep(50);
		}
	} // TestService
	
	@Override
	public void messageReceived(TagSighting tagSighting) {
		System.out.println(tagSighting.toString());
	} // messageReceived

	public static final void main(String[] args) {
		try {
			new SimpleListenerServiceStartUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // main
}