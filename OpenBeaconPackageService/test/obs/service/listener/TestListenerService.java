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
package obs.service.listener;

import static org.junit.Assert.*;
import obs.service.sighting.TagSighting;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for the test of the service class reading previously
 * recorded tag data.
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class TestListenerService {
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	//private ArrayList<byte[]> data = new ArrayList<byte[]>();
	
	private static ListenerService service;
	
	/**
	 * Local MessageListener class which implements the
	 * Listener interface (to be used as message listener
	 * during the tests)
	 */
	private static class MessageListener implements Listener {
		@Override
		public void messageReceived(TagSighting tagSighting) {
			// Do nothing
		} // messageReceived
	} // Class MessageListener

	/**
	 * Prepare listener service only once, before running the test
	 * cases.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create service using the local host IP and the default
		// OpenBeacon port (2342), timeout 1 second and the encryption key.
		
		// Information: Default OpenBeacon server IP: 10.254.0.2
		service = new ListenerService("127.0.0.1", 2342, 1, key);
		service.setMessageListener(new MessageListener());
	} // setUp

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link obs.service.listener.ListenerService#ListenerService(java.lang.String, int, int, long[])}.
	 */
	@Test
	public void testListenerService() {
		// see setUp
	} // testListenerService

	/**
	 * Test method for {@link obs.service.listener.ListenerService#getEncryptionKey()}.
	 */
	@Test
	public void testGetEncryptionKey() {
		assertEquals(key, service.getEncryptionKey());
	} // testGetEncryptionKey

	/**
	 * Test method for {@link obs.service.listener.ListenerService#setMessageListener(obs.service.listener.Listener)}.
	 */
	@Test
	public void testSetMessageListener() {
		// See setUp
	} // testSetMessageListener

	/**
	 * Test method for {@link obs.service.listener.ListenerService#getLostCRC()}.
	 */
	@Test
	public void testGetLostCRC() {
		assertEquals(0, service.getLostCRC());
	} // testGetLostCRC

	/**
	 * Test method for {@link obs.service.listener.ListenerService#isRunning()}.
	 */
	@Test
	public void testIsRunning() {
		assertEquals(false, service.isRunning());
	} // testIsRunning

	/**
	 * Test method for {@link obs.service.listener.ListenerService#start()}.
	 * Test method for {@link obs.service.listener.ListenerService#stop()}.
	 */
	@Test
	public void testStartStop() {
		service.start();
		assertEquals(true, service.isRunning());
		
		service.stop();
		assertEquals(false, service.isRunning());
	} // testStartStop

	/**
	 * Test method for {@link obs.service.listener.ListenerService#run()}.
	 */
	@Test
	public void testRun() {
//		service.start();
//		assertEquals(true, service.isRunning());
	}
}
