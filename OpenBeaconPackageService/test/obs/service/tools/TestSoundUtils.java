/**
 * 
 */
package obs.service.tools;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the SoundUtils class. Use your ears for assertion... ;-)
 * 
 * @author Bjoern Behrens <uol@btech.de>
 * @version 1.0
 */
public class TestSoundUtils {
	
	private SoundUtils sound;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SoundUtils.setHz(1000);
		SoundUtils.setDurationMilliseconds(1000);
		
		sound = new SoundUtils();
	} // setUp

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
 
	/**
	 * Test method for {@link obs.service.tools.SoundUtils#run()}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testRun() throws InterruptedException {		
		Assume.assumeTrue("Audio output line not available in this environment", isAudioOutputAvailable());
		sound.run();
	} // testSoundUtils

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#beep(int, int)}.
	 */
	@Test
	public void testBeep() {
		Assume.assumeTrue("Audio output line not available in this environment", isAudioOutputAvailable());
		SoundUtils.beep(500, 750);
	} // testBeep

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#tone(int, int)}.
	 * @throws LineUnavailableException 
	 */
	@Test
	public void testTone() throws LineUnavailableException {
		Assume.assumeTrue("Audio output line not available in this environment", isAudioOutputAvailable());
		SoundUtils.tone(600, 750);
	} // testTone

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#tone(int, int, double)}.
	 * @throws LineUnavailableException 
	 */
	@Test
	public void testToneWithVolumne() throws LineUnavailableException {
		Assume.assumeTrue("Audio output line not available in this environment", isAudioOutputAvailable());
		SoundUtils.tone(700, 750, 2.0);
	} // testToneWithVolumne

	private boolean isAudioOutputAvailable() {
		AudioFormat audioFormat = new AudioFormat(8000f, 8, 1, true, false);
		DataLine.Info lineInfo = new DataLine.Info(javax.sound.sampled.SourceDataLine.class, audioFormat);
		return AudioSystem.isLineSupported(lineInfo);
	}
}