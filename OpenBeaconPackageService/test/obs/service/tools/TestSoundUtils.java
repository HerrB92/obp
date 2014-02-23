/**
 * 
 */
package obs.service.tools;

import javax.sound.sampled.LineUnavailableException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the SoundUtils class. Use your ears for assertion... ;-)
 * 
 * @author Björn Behrens <uol@btech.de>
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
		sound.run();
	} // testSoundUtils

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#beep(int, int)}.
	 */
	@Test
	public void testBeep() {
		SoundUtils.beep(500, 750);
	} // testBeep

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#tone(int, int)}.
	 * @throws LineUnavailableException 
	 */
	@Test
	public void testTone() throws LineUnavailableException {
		SoundUtils.tone(600, 750);
	} // testTone

	/**
	 * Test method for {@link obs.service.tools.SoundUtils#tone(int, int, double)}.
	 * @throws LineUnavailableException 
	 */
	@Test
	public void testToneWithVolumne() throws LineUnavailableException {
		SoundUtils.tone(700, 750, 2.0);
	} // testToneWithVolumne
}