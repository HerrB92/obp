/**
 * 
 */
package obp;

import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.JsonGenerationException;

import obp.index.DataIndex;
import obp.index.output.JSONOutputService;
import obp.listener.ServiceListener;
import odp.service.listener.ListenerService;

/**
 * @author bbehrens
 *
 */
public class OpenBeaconPackage {
	private static DataIndex index = new DataIndex();
	private static JSONOutputService outputJSON;
	private static Timer timer;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		//int[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		//System.out.println(configuration.getConfigurationLoaded());
		//configuration.saveConfiguration();
		
		ListenerService service = new ListenerService(
				"10.254.0.2",
				2342,
				1,
				configuration.getTagDataKey(), false);
		service.setMessageListener(new ServiceListener(index));
		service.start();
		
		outputJSON = new JSONOutputService("/var/www/html/json/obp.json");
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					outputJSON.update(index);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
					timer.cancel();
				}
			}
		}, 1000, 1000);
	}
}
