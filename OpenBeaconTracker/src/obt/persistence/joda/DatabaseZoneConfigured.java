package obt.persistence.joda;

/**
 * @author Chris
 * @param <T> Type representing the database zone
 */
public interface DatabaseZoneConfigured<T> {

	void setDatabaseZone(T databaseZone);

	T parseZone(String zoneString);
}