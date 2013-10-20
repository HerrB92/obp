/**
 * Code based on the work of 
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 */
/*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */
package odp.service.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import obp.service.Constants;

/**
 * @author bbehrens
 *
 */
public class ListenerService implements Runnable {
	private InetAddress server;
	private DatagramSocket socket;
	private DatagramPacket packet;
	//private int c = 0;
	private Thread thread;
	private Listener listener;
	private boolean debug = false;
	private boolean running = false;
	private long[] encryptionKey;
	private TagSighting tagSighting;
	//private Tag tag;
	
	public ListenerService(String host, int port, int timeout, long[] key, boolean debug)
		throws InterruptedException {
		
		setDebug(debug);
		this.encryptionKey = key;
		
		int attempts = 0;
		boolean bound = false;
		while (!(bound || (attempts >= 3))) {
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
				System.err
						.println("Error printing info ListenerService: "
								+ ioException.toString());
			}
		}

		packet = new DatagramPacket(new byte[256], 256);
	} // Constructor
	
	@Override
	public void run() {
		while (isRunning()) {
			try {
				socket.receive(packet);
				
				if (packet.getLength() == Constants.ENVELOPE_SIZE_BYTE) {
					tagSighting = new TagSighting(packet, encryptionKey, debug);
										
					if (tagSighting.isValid() && listener != null) {
						listener.messageReceived(tagSighting);
					} else {
						System.out.println("Packet lost CRC:" + tagSighting.getTagCRC() + " or no listener");
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

	public void start() {
		running = true;
		thread.start();
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isRunning() {
		return running;
	}

	public void setMessageListener(Listener listener) {
		this.listener = listener;
	}
}
