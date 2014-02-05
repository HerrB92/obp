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
package obt;

import java.util.Timer;
import java.util.TimerTask;

import org.hibernate.Session;

import com.fasterxml.jackson.core.JsonGenerationException;

import obs.service.listener.ListenerService;
import obt.configuration.ServiceConfiguration;
import obt.index.DataIndex;
import obt.index.ObtRun;
import obt.index.output.OutputJSONRegisteredTagKeys;
import obt.index.output.OutputJSONTagData;
import obt.index.output.OutputJSONUnRegisteredTagKeys;
import obt.listener.ServiceListener;
import obt.persistence.DatabaseSessionFactory;

/**
 * The OpenBeaconTracker class provides the main method for the 
 * "tracking" service. The OpenBeaconTracker uses the proximity
 * sightings of other tags at fixed locations ("SpotTag") to 
 * determine the position of a tag, only. The default localization
 * method using received and - especially - lost packages is
 * not used.
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
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
public class OpenBeaconTracker {	
	// JSON output writer classes
	private static OutputJSONTagData outputJSONTagData;
	private static OutputJSONRegisteredTagKeys outputJSONRegisteredTagKeys;
	private static OutputJSONUnRegisteredTagKeys outputJSONUnRegisteredTagKeys;
	
	// Time object to frequently write the current in-memory
	// data in a certain format as JSON into files, accessible
	// by the web client.
	private static Timer timer;
	
	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ObtRun run = new ObtRun();
		
		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		session.save(run);
		session.getTransaction().commit();
		
		// Load configuration
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		
		// Preserve current settings for a later replay
		configuration.storeReplayInformation(run.getRunId());
		
		// Prepare global data index object
		DataIndex index = DataIndex.getInstance();
		
		// Create listener service and register service
		// listener which processes the tag sighting data.
		ListenerService service = new ListenerService("10.254.0.2", 2342, 1,
				configuration.getTagKeyList().get(0), false);
		service.setMessageListener(new ServiceListener(index, run.getRunId()));
		service.start();
		
		// Create JSON output service
		// Data
		outputJSONTagData = 
				new OutputJSONTagData("/var/www/html/json/obtracker.json", run.getRunId());
		// List of recently registered tags
		outputJSONRegisteredTagKeys = 
				new OutputJSONRegisteredTagKeys("/var/www/html/json/obtregistered.json");
		// List of recently unregistered tags
		outputJSONUnRegisteredTagKeys = 
				new OutputJSONUnRegisteredTagKeys("/var/www/html/json/obtunregistered.json");

		// Create the Timer object to frequently update the
		// JSON data.
		
		// The object is initialized as Timer(true) to make the timer a 
		// daemon thread. If the only remaining application threads are daemon 
		// threads, the application exits automatically. See 
		// http://www.math.uni-hamburg.de/doc/java/tutorial/essential/threads/timer.html
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					outputJSONRegisteredTagKeys.update();
					outputJSONUnRegisteredTagKeys.update();
					outputJSONTagData.update();
				} catch (JsonGenerationException e) {
					e.printStackTrace();
					timer.cancel();
				}
			}
		}, 1000, 1000);
	} // main
}
