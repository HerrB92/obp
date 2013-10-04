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

import obp.listener.Listener;
import obp.listener.ListenerService;
import obp.tag.Tag;
import obp.tag.TagSighting;

public class TestService implements Listener {
	
	public static final int[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};

	//public static final int [] tea_key ={0xe107341e,0xab99c57e,0x48e17803,0x52fb4d16};
	
	public TestService() throws Exception {
		
		String host = "10.254.0.1";
		int port = 2342; 		// default port openbeacon
		int timeout = 10;		// in sec.

		ListenerService service = new ListenerService(host, port, timeout, key, false);
		service.setMessageListener(this);
		service.setDebug(false);
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
