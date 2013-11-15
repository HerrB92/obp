package obp;

import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.JsonGenerationException;

import obp.index.DataIndex;
import obp.index.output.JSONOutputService;
import obp.listener.ServiceListener;
import odp.service.listener.ListenerService;

/**
 * The OpenBeaconTracker class provides the main method for the 
 * "tracking" service. The OpenBeaconTracker uses the proximity
 * sightings to fixed proximity tags ("SpotTag") only, to determine
 * the position of a tag.
 * 
 * Additionally, the moving tags have to be registered first by
 * passing one or more registration tags and will be unregistered
 * also, by passing one or more unregistration tags.
 * 
 * The main method loads the configuration, initializes the 
 * listener service, the JSON output service (to provide the 
 * data as a JSON structure which may be consumed by web clients) 
 * and the timer object which refreshes the JSON file once
 * a second.
 * 
 * @author Björn Behrens
 */
public class OpenBeaconTracker {
	// Provide a globally available in-memory data store
	private static DataIndex index = new DataIndex();
	
	// JSON output writer service
	private static JSONOutputService outputJSON;
	
	// Time object to frequently write the current in-memory
	// data in a certain format as JSON into a file, accessible
	// by the web client.
	private static Timer timer;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// Load configuration
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		// System.out.println(configuration.getConfigurationLoaded());
		// configuration.saveConfiguration();

//		ObpRun run = new ObpRun();

//		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
//		Transaction transaction = session.beginTransaction();
//		session.save(run);
//		transaction.commit();
//		
//		System.out.println(run.getRunId());

		// Create listener service and register service
		// listener which processes the tag sighting data.
		ListenerService service = new ListenerService("10.254.0.2", 2342, 1,
				configuration.getTagDataKey(), false);
		service.setMessageListener(new ServiceListener(index));
		service.start();

		// Create JSON output service
		outputJSON = new JSONOutputService("/var/www/html/json/obtracker.json");

		// Create the Timer object to frequently update the
		// JSON data.
		// 
		// The object is initialized as Timer(true) to make the timer a 
		// daemon thread. If the only remaining application threads are daemon 
		// threads, the application exits automatically. See 
		// http://www.math.uni-hamburg.de/doc/java/tutorial/essential/threads/timer.html
		timer = new Timer(true);
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
	} // main
}
