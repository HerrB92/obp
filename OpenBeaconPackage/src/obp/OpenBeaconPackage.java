/**
 * 
 */
package obp;

import obp.listener.ListenerService;

/**
 * @author bbehrens
 *
 */
public class OpenBeaconPackage {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		//int[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		System.out.println(configuration.getConfigurationLoaded());
		configuration.saveConfiguration();
		ListenerService service = new ListenerService(
				"10.254.0.2",
				2342,
				1,
				configuration.getTagDataKey(), true);
		service.start();
	}
}
