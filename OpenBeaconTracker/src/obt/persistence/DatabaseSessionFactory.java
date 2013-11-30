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
package obt.persistence;

import obt.configuration.Setting;
import obt.index.ObtRun;
import obt.spots.Reader;
import obt.spots.RegisterTag;
import obt.spots.SpotTag;
import obt.spots.UnRegisterTag;
import obt.tag.RawData;
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
			cfg.addAnnotatedClass(RawData.class);
			
			ServiceRegistry serviceRegistry = 
					new ServiceRegistryBuilder().applySettings(
							cfg.getProperties()).buildServiceRegistry();
			sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}
		
		return sessionFactory;
	} // getInstance
}