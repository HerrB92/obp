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
package obs.service.tools;

import javax.sound.sampled.*;

/**
 * Class used to play short sound signals (beeps) at a certain frequency,
 * length and volume.
 * 
 * To avoid that calling code is stopped while the sound is played, use
 * the threaded way of calling:
 * SoundUtils.setHz(1000);
 * new SoundUtils().start();
 * 
 * Note, that subsequent calls are ignored, while a sound is currently
 * played.
 * 
 * Partly based on code provided by
 * Gagnon, R. as RealHowTo (2011) 'Answer to: How to play a sound (alert) in a java application?'
 * stackoverflow.com, 14.07.2011, 21:17 [Online]
 * Available at: http://stackoverflow.com/a/6700039 (Accessed: 12.10.2013)
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class SoundUtils extends Thread {
	private static final float SAMPLE_RATE = 8000f;
	
	// Sound frequency
	private static int hz = 500;
	
	// Beep duration in milliseconds. On the system used for development,
	// 450 was the lowest working value
	private static int milliseconds = 500; 
	
	// Prevent trying to play a sound
	// while a sound is already played
	private static boolean playing = false;

	/**
	 * Method called for a new thread of SoundUtils
	 * 
	 * Tagged as synchronized: Avoid being called by different SoundUtils
	 * threads with an in-between state of the "playing" variable.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public synchronized void run() {
		if (!playing) {
			SoundUtils.beep(getHz(), getDurationMilliseconds());
		}
	} // run
	
	/**
	 * @return the hz
	 */
	private static int getHz() {
		return hz;
	} // getHz

	/**
	 * @param hz
	 *            the hz to set
	 */
	public static void setHz(int hz) {
		SoundUtils.hz = hz;
	} // setHZ

	/**
	 * @return the duration in milliseconds
	 */
	private static int getDurationMilliseconds() {
		return milliseconds;
	} // getDurationMilliseconds

	/**
	 * @param milliseconds
	 *            the duration milliseconds to set
	 */
	public static void setDurationMilliseconds(int milliseconds) {
		SoundUtils.milliseconds = milliseconds;
	} // setDurationMilliseconds

	/**
	 * @param hz
	 * @param msecs
	 */
	public static void beep(int hz, int msecs) {
		try {
			tone(hz, msecs, 1.0);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	} // beep

	/**
	 * @param hz
	 * @param msecs
	 * @throws LineUnavailableException
	 */
	public static void tone(int hz, int msecs)
		throws LineUnavailableException {
		tone(hz, msecs, 1.0);
	} // tone

	/**
	 * @param hz
	 * @param msecs
	 * @param volume
	 * @throws LineUnavailableException
	 */
	public static void tone(int hz, int msecs, double volume)
		throws LineUnavailableException {
		
		playing = true;
		
		byte[] buffer = new byte[1];
		AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, // sampleRate
				8, // sampleSizeInBits
				1, // channels
				true, // signed
				false); // bigEndian
		
		SourceDataLine dataLine = AudioSystem.getSourceDataLine(audioFormat);
		dataLine.open(audioFormat);
		dataLine.start();
		
		for (int i = 0; i < msecs * 8; i++) {
			double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
			buffer[0] = (byte) (Math.sin(angle) * 127.0 * volume);
			dataLine.write(buffer, 0, 1);
		}
		
		dataLine.drain();
		dataLine.stop();
		dataLine.close();
		
		playing = false;
	} // tone (full)
}
