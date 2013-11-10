/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package obp.persistence.joda;

import java.sql.Timestamp;
import java.util.TimeZone;

import obp.persistence.joda.ZoneHelper;
import obp.persistence.joda.AbstractTimestampColumnMapper;
import obp.persistence.joda.ColumnMapper;
import obp.persistence.joda.DatabaseZoneConfigured;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class TimestampColumnLocalDateTimeMapper extends AbstractTimestampColumnMapper<LocalDateTime> implements ColumnMapper<LocalDateTime, Timestamp>, DatabaseZoneConfigured<DateTimeZone> {

    private static final long serialVersionUID = -7670411089210984705L;

    private DateTimeZone databaseZone = DateTimeZone.UTC;

    public TimestampColumnLocalDateTimeMapper() {
    }
    
    public TimestampColumnLocalDateTimeMapper(DateTimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public LocalDateTime fromNonNullString(String s) {
       return new LocalDateTime(s);
    }

    @Override
    public LocalDateTime fromNonNullValue(Timestamp value) {

        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;
        
        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - currentDatabaseZone.getOffset(null);
        
        DateTime dateTime = new DateTime(value.getTime() + adjustment, currentDatabaseZone);
        LocalDateTime localDateTime = dateTime.toLocalDateTime();
        
        return localDateTime;
    }
    
    @Override
    public String toNonNullString(LocalDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalDateTime value) {
        
        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;
    	DateTime zonedValue = value.toDateTime(value.toDateTime(currentDatabaseZone));
        
        int adjustment = TimeZone.getDefault().getOffset(zonedValue.getMillis()) - currentDatabaseZone.getOffset(null);
    	
        final Timestamp timestamp = new Timestamp(zonedValue.getMillis() - adjustment);
        return timestamp;
    }
    

    @Override
	public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }

	@Override
	public DateTimeZone parseZone(String zoneString) {
		return DateTimeZone.forID(zoneString);
	}
}
