/**
 * 
 */
package obp;

import obp.listener.ServiceListener;
import odp.service.listener.ListenerService;

/**
 * @author bbehrens
 *
 */
public class OpenBeaconTestLab {
	//private static DataIndex index = new DataIndex();

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
		service.setMessageListener(new ServiceListener());
		service.start();
		
//		outputJSON = new JSONOutputService("/var/www/html/json/obp.json");
//		
//		timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				try {
//					outputJSON.update(index);
//				} catch (JsonGenerationException e) {
//					e.printStackTrace();
//					timer.cancel();
//				}
//			}
//		}, 1000, 1000);
	}
}
