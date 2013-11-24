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
package odp.service.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import obp.service.Constants;
import odp.service.sighting.TagSighting;

/**
 * 
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class ListenerService implements Runnable {
	private InetAddress server;
	private DatagramSocket socket;
	private DatagramPacket packet = new DatagramPacket(new byte[256], 256);
	
	private Thread thread;
	private Listener listener;
	
	private boolean debug = false;
	private boolean running = false;
	private long[] encryptionKey;
	private TagSighting tagSighting;
	
	private long lostCRC = 0;
	
	public ListenerService(String host, int port, int timeout, long[] key, boolean debug)
		throws InterruptedException {
		
		setDebug(debug);
		setEncryptionKey(key);
		
		int attempts = 0;
		boolean bound = false;
		while (!(bound || attempts >= 3)) {
			try {
				server = InetAddress.getByName(host);
				
				socket = new DatagramSocket(port, server);
				bound = socket.isBound();
			} catch (IOException ioException) {
				System.err.println("ListenerService: " + (attempts + 1)
						+ " attempt to connect to: " + host + " IOexc:"
						+ ioException.toString());
				attempts++;
				Thread.sleep(5000);
			}
		}
		
		if (!bound) {
			System.out.println("ListenerService: Unable to connect, exit.");
			Runtime.getRuntime().exit(1);
		} else {
			System.out.println("Socket bound");
		}

		try {
			socket.setSoTimeout(timeout * 1000);
			socket.setReceiveBufferSize(1);
		} catch (IOException ioException) {
			System.err.println("Error Setup Socket ListenerService: "
					+ ioException.toString());
		}
		
		thread = new Thread(this);

		if (debug) {
			try {
				System.out.println("Local IP: "
						+ socket.getLocalAddress().getHostAddress() + ":"
						+ socket.getLocalPort() + "  receive buffer size: "
						+ socket.getReceiveBufferSize() + "  timeout: "
						+ socket.getSoTimeout());
			} catch (IOException ioException) {
				System.err.println("Error printing info ListenerService: "
								+ ioException.toString());
			}
		}
//
//		packet = new DatagramPacket(new byte[256], 256);
	} // Constructor
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	} // setDebug
	
	public boolean isDebug() {
		return debug;
	} // isDebug
	
	/**
	 * @param encryptionKey the encryptionKey to set
	 */
	private void setEncryptionKey(long[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
	/**
	 * @return the encryptionKey
	 */
	public long[] getEncryptionKey() {
		return encryptionKey;
	}
	
	public void setMessageListener(Listener listener) {
		this.listener = listener;
	} // setMessageListener
	
	private Listener getMessageListener() {
		return listener;
	} // getMessageListener

	/**
	 * @return the lostCRC
	 */
	public long getLostCRC() {
		return lostCRC;
	} // getLostCRC
	
	private void setRunning(boolean running) {
		this.running = running;
	} // setRunning

	public boolean isRunning() {
		return running;
	} // isRunning

	public void start() {
		if (getMessageListener() == null) {
			throw new IllegalStateException("ListenerService: No message listener specified");
		}
		
		setRunning(true);
		thread.start();
	} // start
	
	@Override
	public void run() {
		while (isRunning()) {
			try {
				socket.receive(packet);
				
				if (packet.getLength() == Constants.ENVELOPE_SIZE_BYTE) {
					tagSighting = new TagSighting(packet, encryptionKey, debug);
										
					if (tagSighting.isValid()) {
						getMessageListener().messageReceived(tagSighting);
					} else {
						lostCRC++;
						System.out.println("Packet lost CRC:" + tagSighting.getTagCRC());
					}
				}
			} catch (IOException ioException) {
				// there is probably a better way..
				if (ioException.toString().equalsIgnoreCase(
						"java.net.SocketTimeoutException: Receive timed out")) {
					System.out.println("No Tag Sightings");
				}
			}
		}
	} // run
}
