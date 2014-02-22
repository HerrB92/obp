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
 * Test class for the service class itself.
 * 
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class TestService implements Listener {
	
	public static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	
	public TestService() throws Exception {
		
		String host = "10.254.0.1";	// Default OpenBeacon service IP
		int port = 2342; 			// Default OpenBeacon port
		int timeout = 10;			// Timeout in seconds

		ListenerService service = new ListenerService(host, port, timeout, key);
		service.setMessageListener(this);
		service.start();
		System.out.println("Message Listener Started");

		while (true) {
			Thread.sleep(50);
		}
	}
	
	@Override
	public void messageReceived(TagSighting tag) {
		// System.out.println("Message Received, TagID:" + tag.getId()+ " | Strength: "+tag.getStrength());
	}

	public static final void main(String[] args) {
		try {
			new TestService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
