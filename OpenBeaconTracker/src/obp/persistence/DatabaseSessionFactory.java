/**
 * 
 */
package obp.persistence;

import obp.index.ObpRun;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * @author bbehrens
 * 
 */
public class DatabaseSessionFactory {
	private static org.hibernate.SessionFactory sessionFactory = null;

	private DatabaseSessionFactory() {} // Constructor

//	static {
//		final Configuration cfg = new Configuration();
//		cfg.configure("/hibernate.cfg.xml");
//		
//		sessionFactory = cfg.buildSessionFactory(new ServiceRegistryBuilder().buildServiceRegistry());
//	}

	public static SessionFactory getInstance() {
		if (sessionFactory == null) {
			Configuration cfg = new Configuration();
			cfg.configure("/hibernate.cfg.xml"); // /hibernate.cfg.xml
			cfg.addAnnotatedClass(ObpRun.class);
			
			ServiceRegistry serviceRegistry = 
					new ServiceRegistryBuilder().applySettings(
							cfg.getProperties()).buildServiceRegistry();
			sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}
		
		return sessionFactory;
	} // getInstance
}