/**
 * 
 */
package obt.configuration;

import static org.junit.Assert.*;
import obt.configuration.Setting.SettingType;
import obt.configuration.Setting.SettingValueType;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Setting class
 * 
 * @author Björn Behrens
 */
public class TestSetting {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link obt.configuration.Setting#Setting()}.
	 */
	@Test
	public final void testSetting() {}

	/**
	 * Test method for {@link obt.configuration.Setting#Setting(obt.configuration.Setting.SettingType, java.lang.String)}.
	 */
	@Test
	public final void testSettingFullConstructor() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "10");
		assertEquals("10", setting.getValue());
		assertEquals(10, setting.getIntValue());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getSettingType()}.
	 */
	@Test
	public final void testGetSettingType() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "10");
		assertEquals(SettingType.TagButtonActiveSeconds, setting.getSettingType());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getValueType()}.
	 */
	@Test
	public final void testGetValueType() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "10");
		assertEquals(SettingValueType.IntValue, setting.getValueType());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getValue()}.
	 */
	@Test
	public final void testGetValue() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "100");
		assertEquals("100", setting.getValue());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#setValue(java.lang.String)}.
	 */
	@Test
	public final void testSetValue() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "200");
		assertEquals("200", setting.getValue());
		assertEquals(200, setting.getIntValue());
		setting.setValue("300");
		assertEquals("300", setting.getValue());
		assertEquals(300, setting.getIntValue());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getIntValue()}.
	 */
	@Test
	public final void testGetIntValue() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "200");
		assertEquals("200", setting.getValue());
		assertEquals(200, setting.getIntValue());
		setting.setValue("300");
		assertEquals("300", setting.getValue());
		assertEquals(300, setting.getIntValue());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getDefaultValue()}.
	 */
	@Test
	public final void testGetDefaultValue() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "200");
		assertEquals("5", setting.getDefaultValue());
		assertEquals(5, setting.getDefaultIntValue());
	}

	/**
	 * Test method for {@link obt.configuration.Setting#getDefaultIntValue()}.
	 */
	@Test
	public final void testGetDefaultIntValue() {
		Setting setting = new Setting(SettingType.TagButtonActiveSeconds, "200");
		assertEquals("5", setting.getDefaultValue());
		assertEquals(5, setting.getDefaultIntValue());
	}

}
