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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import obs.service.Constants;
import obs.service.sighting.TagSighting;

/**
 * Main threaded listener service class used to receive the data
 * packages sent by the reader(s) and received by listening on
 * the specified host/IP and port.
 * 
 * If a valid data package is received the messageReceived method of
 * the specified messageListener object is called, providing a 
 * tagSighting object with all available data.
 * 
 * Note, that for each property of the tagSighting object it has to be
 * checked, if a value is provided, before it is processed (as the 
 * data packages provide different sets of information; depending on
 * the used protocol type).
 * 
 * Note, that this version can handle only one encryption key.
 * 
 * Code based on the work of
 * 2007 Alessandro Marianantoni <alex@alexrieti.com>
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class ListenerService implements Runnable {
	static final Logger logger = LogManager.getLogger(ListenerService.class.getName());
	
	// Thread variable
	private Thread thread;
	
	// Server object
	private InetAddress server;
	
	// Socket the listener is bound to
	private DatagramSocket socket;
	
	// Data container for received data packages
	private DatagramPacket packet = new DatagramPacket(new byte[256], 256);
	
	// Listener service
	private Listener listener;
	
	// Variable signaling, if the service is running
	private boolean running = false;
	
	// Key used to decrypt the data within the data package
	private long[] encryptionKey;
	
	// Prepare a tagSighting variable, filled for each
	// received data package
	private TagSighting tagSighting;
	
	// Number of received data packages with invalid
	// CRC values (invalid data packages)
	private long lostCRC = 0;
	
	/**
	 * Constructor
	 * 
	 * Note, that this version can handle only one encryption key.
	 * 
	 * @param host		Host name or - recommended - the host IP address (default: 10.254.0.1)
	 * @param port		Port (default: 2342)
	 * @param timeout	Timeout in seconds the listener waits for a data package
	 * @param key		Encryption key of the tag data
	 * 
	 * @throws InterruptedException
	 */
	public ListenerService(String host, int port, int timeout, long[] key)
		throws InterruptedException {
		
		setEncryptionKey(key);
		
		int attempts = 0;
		boolean bound = false;
		while (!(bound || attempts >= 3)) {
			try {
				server = InetAddress.getByName(host);
				
				socket = new DatagramSocket(port, server);
				bound = socket.isBound();
			} catch (IOException ioException) {
				logger.error(
						String.format("%d attempt to connect to %s:",
									  attempts + 1, host),
						ioException);
				attempts++;
				Thread.sleep(5000);
			}
		}
		
		if (!bound) {
			logger.error("Unable to connect, exit.");
			Runtime.getRuntime().exit(1);
		} else {
			logger.debug("Socket bound");
		}

		try {
			socket.setSoTimeout(timeout * 1000);
			socket.setReceiveBufferSize(1);
		} catch (IOException ioException) {
			logger.error("Error socket setup", ioException);
		}
		
		thread = new Thread(this);

		try {
			logger.debug(String.format("Local IP: %s:%d, buffer size: %d, timeout: %d",
					socket.getLocalAddress().getHostAddress().toString(),
					socket.getLocalPort(),
					socket.getReceiveBufferSize(),
					socket.getSoTimeout()));
		} catch (IOException ioException) {
			logger.error("Error printing service information", ioException);
		}
	} // Constructor
	
	/**
	 * Sets the encryption key.
	 * 
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
	
	/**
	 * Sets the message listener object, called for each valid data package.
	 * 
	 * @param listener
	 */
	public void setMessageListener(Listener listener) {
		this.listener = listener;
	} // setMessageListener
	
	/**
	 * @return The specified message listener object
	 */
	private Listener getMessageListener() {
		return listener;
	} // getMessageListener

	/**
	 * @return The number of packages with invalid CRC value (invalid packages)
	 */
	public long getLostCRC() {
		return lostCRC;
	} // getLostCRC
	
	/**
	 * Update running property.
	 * 
	 * @param running
	 */
	private void setRunning(boolean running) {
		this.running = running;
	} // setRunning

	/**
	 * @return True, if the service is running, false otherwise.
	 */
	public boolean isRunning() {
		return running;
	} // isRunning

	/**
	 * Start service.
	 */
	public void start() {
		if (getMessageListener() == null) {
			logger.throwing(new IllegalStateException("No message listener specified"));
		}
		
		setRunning(true);
		thread.start();
	} // start
	
	/**
	 * Stop service.
	 */
	public void stop() {
		if (isRunning()) {
			setRunning(false);
		}
	} // stop
		
	/**
	 * Thread run method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (isRunning()) {
			try {
				socket.receive(packet);
				
				if (packet.getLength() == Constants.ENVELOPE_SIZE_BYTE) {
					tagSighting = new TagSighting(packet, encryptionKey);
										
					if (tagSighting.isValid()) {
						getMessageListener().messageReceived(tagSighting);
					} else {
						lostCRC++;
						logger.debug(String.format("Invalid CRC packages: %d", tagSighting.getTagCRC()));
					}
				}
			} catch (IOException ioException) {
				// there is probably a better way..
				if (ioException.toString().equalsIgnoreCase(
						"java.net.SocketTimeoutException: Receive timed out")) {
					logger.info("No tag sightings");
				}
			}
		}
	} // run
}
