/**
 * 
 */
package obt.persistence;

import obt.configuration.Setting;
import obt.index.ObtRun;
import obt.spots.Reader;
import obt.spots.RegisterTag;
import obt.spots.SpotTag;
import obt.spots.UnRegisterTag;
import obt.tag.TagKey;
import obt.tag.Tracking;

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
			cfg.addAnnotatedClass(ObtRun.class);
			cfg.addAnnotatedClass(Setting.class);
			cfg.addAnnotatedClass(Reader.class);
			cfg.addAnnotatedClass(SpotTag.class);
			cfg.addAnnotatedClass(RegisterTag.class);
			cfg.addAnnotatedClass(UnRegisterTag.class);
			cfg.addAnnotatedClass(TagKey.class);
			cfg.addAnnotatedClass(Tracking.class);
			
			ServiceRegistry serviceRegistry = 
					new ServiceRegistryBuilder().applySettings(
							cfg.getProperties()).buildServiceRegistry();
			sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}
		
		return sessionFactory;
	} // getInstance
}