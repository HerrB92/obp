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
package obp.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import obp.Constants;
import obp.tag.TagSighting;
import obp.tag.Tag;

/**
 * @author bbehrens
 *
 */
public class ListenerService implements Runnable {
	private InetAddress server;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private int c = 0;
	private Thread thread;
	private Listener listener;
	private boolean debug = false;
	private boolean running = false;
	private int[] encryptionKey;
	private TagSighting envelope;
	private Tag tag;
	
//	public ListenerService(String host, int port, int timeout, int[] key, boolean debug) throws InterruptedException {
//		new ListenerService(host, new byte[]{}, port, timeout, key, debug);
//	}
//	
//	public ListenerService(byte[] hostIP, int port, int timeout, int[] key, boolean debug) throws InterruptedException {
//		new ListenerService(null, hostIP, port, timeout, key, debug);
//	}
	
	public ListenerService(String host, int port, int timeout, int[] key, boolean debug)
			throws InterruptedException {

		setDebug(debug);
		
		boolean bound = false;
		int attempts = 0;
		encryptionKey = key;

		while (!(bound || (attempts >= 3))) {
			try {
//				if (host == null) {
//					server = InetAddress.getByName(hostIP);
//				} else {
					server = InetAddress.getByName(host);
//				}
				
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
				
				if (packet.getLength() == TagSighting.ENVELOPE_SIZE_BYTE) {
					envelope = new TagSighting(packet, encryptionKey);
					
//					TBeaconNetworkHdr hdr;
//					u_int32_t sequence;
//					u_int32_t timestamp;
//					TBeaconEnvelope log
					
//					tag = new Tag(packet, encryptionKey);
//					
//					if (debug) {
//						System.out.print(c + " | length:" + tag.getSize()
//								+ " | sent by: " + tag.getBeaconAddress()
//								+ " | Tag Id: " + tag.getID());
//					}
//					
//					if (tag.checkCRC()) {
//						listener.messageReceived(tag);
//						c++;
//					} else {
//						System.out.println("Packet lost CRC:" + tag.getCRC());
//					}
				}
			} catch (IOException ioException) {
				// there is probably a better way..
				if (ioException.toString().equalsIgnoreCase(
						"java.net.SocketTimeoutException: Receive timed out")) {
					System.out.println("No Tags");
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
