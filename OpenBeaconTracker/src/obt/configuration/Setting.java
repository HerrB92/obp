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
package obt.configuration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 */
@Entity
@Table(name = "Settings")
public class Setting implements Serializable {
	private static final long serialVersionUID = 8368719576310574295L;
	
	/**
	 * Type of value enumeration class.
	 * 
	 * @author Björn Behrens <uol@btech.de>
	 * @version 1.0
	 */
	public enum SettingValueType {
		IntValue,
		StringValue
	} // SettingValueType
	
	/**
	 * Type of setting enumeration class.
	 * 
	 * @author Björn Behrens <uol@btech.de>
	 * @version 1.0
	 */
	public enum SettingType {
		TagAgedSeconds (SettingValueType.IntValue, "5", 5),
		TagButtonActiveSeconds (SettingValueType.IntValue, "5", 5),
		TagReaderSightingActiveSeconds (SettingValueType.IntValue, "5", 5),
		TagSpotTagSightingActiveSeconds (SettingValueType.IntValue, "2", 2),
		TagProximitySightingActiveSeconds (SettingValueType.IntValue, "5", 5),
		StrengthAggregationWindowSeconds (SettingValueType.IntValue, "2", 2),
		StrengthAggregationAgedSeconds  (SettingValueType.IntValue, "4", 4);
		
		// Data type of the setting (integer or string)
		private final SettingValueType type;
		
		// Default string value
		private final String defaultValue;
		
		// Default integer value
		private final int defaultIntValue;
		
		/**
		 * @param type
		 * @param defaultValue
		 * @param defaultIntValue
		 */
		private SettingType(SettingValueType type, String defaultValue, int defaultIntValue) {
			this.type = type;
			this.defaultValue = defaultValue;
			this.defaultIntValue = defaultIntValue;
		} // Constructor
				
		/**
		 * @return
		 */
		private SettingValueType getType() {
			return type;
		}
		
		/**
		 * @return
		 */
		private String getDefaultValue() {
			return defaultValue;
		}
		
		/**
		 * @return
		 */
		public int getDefaultIntValue() {
			return defaultIntValue;
		}
	} // SettingType
		
	@Id
	@Enumerated(EnumType.STRING)
	// Type of setting
	private SettingType settingType;
	
	@Id
	@Column(nullable = false)
	// Run id: 0 for current configuration, otherwise used for replay
	private Long runId;
	
	@Column
	// String value
	private String value;
	
	@Transient
	// Integer value, not stored in database (as converted 
	// from string value)
	private int intValue;
	
	/**
	 * Simple constructor (required by Hibernate)
	 */
	public Setting() {}
	
	/**
	 * @param settingType
	 * @param value
	 */
	public Setting(SettingType settingType, String value) {
		setSettingType(settingType);
		setValue(value);
	} // Constructor (full)
		
	/**
	 * @return the setting type
	 */
	public SettingType getSettingType() {
		return settingType;
	}

	/**
	 * @param type the setting type to set
	 */
	private void setSettingType(SettingType type) {
		this.settingType = type;
	}
	
	/**
	 * @return Run id, 0 for current run
	 */
	@SuppressWarnings("unused")
	private Long getRunId() {
		return runId;
	} // getRunId

	/**
	 * @param runId
	 */
	@SuppressWarnings("unused")
	private void setRunId(Long runId) {
		this.runId = runId;
	} // setRunId
	
	/**
	 * @return the value type
	 */
	public SettingValueType getValueType() {
		return settingType.getType();
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		if (getValueType().equals(SettingValueType.IntValue)) {
			intValue = Integer.parseInt(value);
		} else {
			intValue = -1;
		}
		
		this.value = value;
	}
	
	/**
	 * @return the value as int value
	 */
	public int getIntValue() {
		return intValue;
	}
	
	/**
	 * @return
	 */
	public String getDefaultValue() {
		return getSettingType().getDefaultValue();
	}
	
	/**
	 * @return
	 */
	public int getDefaultIntValue() {
		return getSettingType().getDefaultIntValue();
	}
}