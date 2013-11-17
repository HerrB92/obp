/**
 * 
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
 * @author bbehrens
 *
 */
@Entity
@Table(name = "Settings")
public class Setting implements Serializable {
	private static final long serialVersionUID = 8368719576310574295L;
	
	public enum SettingType {
		TagButtonActiveSeconds (SettingValueType.IntValue, "5", 5),
		TagReaderSightingActiveSeconds (SettingValueType.IntValue, "5", 5),
		TagSpotTagSightingActiveSeconds (SettingValueType.IntValue, "2", 2),
		TagProximitySightingActiveSeconds (SettingValueType.IntValue, "5", 5),
		StrengthAggregationWindowSeconds (SettingValueType.IntValue, "2", 2),
		StrengthAggregationAgedSeconds  (SettingValueType.IntValue, "4", 4);
		
		private final SettingValueType type;
		private final String defaultValue;
		private final int defaultIntValue;
		
		private SettingType(SettingValueType type, String defaultValue, int defaultIntValue) {
			this.type = type;
			this.defaultValue = defaultValue;
			this.defaultIntValue = defaultIntValue;
		} // Constructor
				
		private SettingValueType getType() {
			return type;
		}
		
		private String getDefaultValue() {
			return defaultValue;
		}
		
		public int getDefaultIntValue() {
			return defaultIntValue;
		}
	}
		
	@Id
	@Enumerated(EnumType.STRING)
	private SettingType settingType;
	
	@Column
	private String value;
	
	@Transient
	private int intValue;
	
	public Setting() {}
	
	public Setting(SettingType settingType, String value) {
		setSettingType(settingType);
		setValue(value);
	}
		
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
	 * @return the valie type
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
	
	public String getDefaultValue() {
		return getSettingType().getDefaultValue();
	}
	
	public int getDefaultIntValue() {
		return getSettingType().getDefaultIntValue();
	}
}