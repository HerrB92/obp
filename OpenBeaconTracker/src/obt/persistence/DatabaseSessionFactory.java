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

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Database session factory for Hibernate. This class also defines, which 
 * classes are relevant for Hibernate.
 * 
 * The parameters to connect to the database are specified in the 
 * hibernate.cfg.xml file.
 * 
 * @author Bjoern Behrens <uol@btech.de>
 * @version 1.0
 */
public class DatabaseSessionFactory {
	private static org.hibernate.SessionFactory sessionFactory = null;
	
	private static final String ENV_DB_URL = "OBP_DB_URL";
	private static final String ENV_DB_USERNAME = "OBP_DB_USERNAME";
	private static final String ENV_DB_PASSWORD = "OBP_DB_PASSWORD";
	private static final String DOTENV_FILE = ".env";

	private DatabaseSessionFactory() {} // Constructor
	
	private static String getConfigValue(Dotenv dotenv, String key, String fallback) {
		String value = System.getenv(key);
		if (value == null || value.isBlank()) {
			value = dotenv != null ? dotenv.get(key) : null;
		}
		if (value == null || value.isBlank()) {
			value = readDotenvValue(key);
		}
		if (value == null || value.isBlank()) {
			return fallback;
		}
		return value;
	}
	
	private static Dotenv loadDotenv() {
		String[] directories = {".", "..", "../.."};
		for (String directory : directories) {
			try {
				return Dotenv.configure()
						.directory(directory)
						.ignoreIfMalformed()
						.ignoreIfMissing()
						.load();
			} catch (DotenvException ignored) {
				// Try next location.
			}
		}
		return null;
	}
	
	private static String readDotenvValue(String key) {
		Path[] dotenvLocations = {
				Path.of(DOTENV_FILE),
				Path.of("..", DOTENV_FILE),
				Path.of("../..", DOTENV_FILE)
		};
		
		for (Path path : dotenvLocations) {
			if (!Files.exists(path)) {
				continue;
			}
			try {
				List<String> lines = Files.readAllLines(path);
				for (String line : lines) {
					String trimmed = line.trim();
					if (trimmed.isEmpty() || trimmed.startsWith("#")) {
						continue;
					}
					int separator = trimmed.indexOf('=');
					if (separator <= 0) {
						continue;
					}
					String candidateKey = trimmed.substring(0, separator).trim();
					if (!candidateKey.equals(key)) {
						continue;
					}
					String value = trimmed.substring(separator + 1).trim();
					if ((value.startsWith("\"") && value.endsWith("\"")) ||
						(value.startsWith("'") && value.endsWith("'"))) {
						value = value.substring(1, value.length() - 1);
					}
					return value;
				}
			} catch (IOException ignored) {
				// Try next location.
			}
		}
		return null;
	}

	public static SessionFactory getInstance() {
		if (sessionFactory == null) {
			Dotenv dotenv = loadDotenv();
			
			Configuration cfg = new Configuration();
			cfg.configure("/hibernate.cfg.xml");
			cfg.setProperty(
					"hibernate.connection.url",
					getConfigValue(dotenv, ENV_DB_URL, cfg.getProperty("hibernate.connection.url")));
			cfg.setProperty(
					"hibernate.connection.username",
					getConfigValue(dotenv, ENV_DB_USERNAME, cfg.getProperty("hibernate.connection.username")));
			cfg.setProperty(
					"hibernate.connection.password",
					getConfigValue(dotenv, ENV_DB_PASSWORD, cfg.getProperty("hibernate.connection.password")));
			
			cfg.addAnnotatedClass(ObtRun.class);
			cfg.addAnnotatedClass(Setting.class);
			cfg.addAnnotatedClass(Reader.class);
			cfg.addAnnotatedClass(SpotTag.class);
			cfg.addAnnotatedClass(RegisterTag.class);
			cfg.addAnnotatedClass(UnRegisterTag.class);
			cfg.addAnnotatedClass(TagKey.class);
			cfg.addAnnotatedClass(Tracking.class);
			cfg.addAnnotatedClass(RawData.class);
			
			sessionFactory = cfg.buildSessionFactory(
					new StandardServiceRegistryBuilder()
							.applySettings(cfg.getProperties())
							.build());
		}
		
		return sessionFactory;
	} // getInstance
}