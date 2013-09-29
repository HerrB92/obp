//  2007 Alessandro Marianantoni <alex@alexrieti.com>
 
// RFIDeasyreader_test.java
// Example that prints UDP packets from the reader
 

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


package test;

import driver.easyreader.*;

public class RFIDeasyreader_test implements MessageListener {

	public static final int [] tea_key ={0xe107341e,0xab99c57e,0x48e17803,0x52fb4d16};
	
	public RFIDeasyreader_test() throws Exception {
		
		String host = "10.254.0.1";
		int port = 2342; 		// default port openbeacon
		int timeout = 10;		// in sec.

		MessageListenerService service = new MessageListenerService(host,port,timeout, tea_key);
		service.setMessageListener(this);
		service.setDebug(false);
		service.Start();
		System.out.println("Message Listener Started");

		while (true) {
			Thread.sleep(50);
		}
	}

	public void messageReceived(Sputnik SputnikData) {
		System.out.println("Message Received, TagID:" + SputnikData.getID()+ " | Strength: "+SputnikData.getStrength());
	}

	public static final void main(String[] args) {
		try {
			new RFIDeasyreader_test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
