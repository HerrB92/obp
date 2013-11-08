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
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		
		ListenerService service = new ListenerService(
				"10.254.0.2",
				2342,
				1,
				configuration.getTagDataKey(), false);
		service.setMessageListener(new ServiceListener());
		service.start();		
	} // main
}
