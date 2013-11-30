//  2007 Alessandro Marianantoni <alex@alexrieti.com>
 
// SputnikEmulator.java
// sends UDP packets emulating the OpenBeacon system (EasyReader+Sputnik)
// If you don't have the hardware: you can launch SputnikEmulator.java before RFIDeasyreader_test.java
 

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

package obt;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SputnikEmulator {
	/*
	 * 
	 * Size_1B Proto_1B Flags_1B Strength_1B Seq_4B tagID_4B reserved_2B crc_2B
	 * 
	 */
	public static long computeCRC(byte[] buf) {
		int size = 14;
		long crc = 0xFFFF;
		int p = 0;
		while (size-- > 0) {
			crc = 0xFFFF & ((crc >> 8) | (crc << 8));
			crc ^= 0xFF & buf[p++];
			crc ^= ((0xff & crc) >> 4) & 0xFFFF;
			crc ^= (crc << 12) & 0xFFFF;
			crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
		}
		crc = (0xFFFF & crc);
		return crc;
	}

	public static void main(String[] args) {

		byte[] buf = { 0x10, 0, 0,  0, 0, 0, 0, 1, 0, 0, 0, 0x05, 0, 0, 0, 0 };
		InetAddress dest;

		long crc = computeCRC(buf);
		buf[15] = (byte) (0xFF & crc);
		buf[14] = (byte) (0xFF & (crc >> 8) ) ;
		
		try {
			dest = InetAddress.getByName((args.length > 1) ? args[1] : "10.254.0.1");

			DatagramSocket sock = new DatagramSocket();
			DatagramPacket pack = new DatagramPacket(buf, buf.length, dest,	2342);

			while (true) {
				Thread.sleep(2000);
				sock.send(pack);
			//	System.out.println("Send: " + buf.toString());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
