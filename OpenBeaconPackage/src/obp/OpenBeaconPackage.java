/**
 * 
 */
package obp;

import java.util.Timer;
import java.util.TimerTask;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fasterxml.jackson.core.JsonGenerationException;

import obp.index.DataIndex;
import obp.index.ObpRun;
import obp.index.output.JSONOutputService;
import obp.listener.ServiceListener;
import obp.persistence.DatabaseSessionFactory;
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
		ServiceConfiguration configuration = ServiceConfiguration.getInstance();
		// System.out.println(configuration.getConfigurationLoaded());
		// configuration.saveConfiguration();

		ObpRun run = new ObpRun();

		Session session = DatabaseSessionFactory.getInstance().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		session.save(run);
		transaction.commit();
		
		System.out.println(run.getRunId());

		ListenerService service = new ListenerService("10.254.0.2", 2342, 1,
				configuration.getTagDataKey(), false);
		service.setMessageListener(new ServiceListener(index));
		service.start();

		outputJSON = new JSONOutputService("/var/www/html/json/obp.json");

		// Timer(true) -> Make the timer a daemon thread. If the only
		// remaining threads are daemon threads, the application exits.
		// See
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
	}
}
