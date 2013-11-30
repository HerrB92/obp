package obt.configuration;

import static org.junit.Assert.*;
import obt.configuration.ServiceConfiguration;
import obt.spots.Reader;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Björn Behrens
 */
public class TestServiceConfiguration {
	private ServiceConfiguration configuration;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		configuration = ServiceConfiguration.getInstance();
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getInstance()}.
	 */
	@Test
	public final void testGetInstance() {
		assertNotNull(ServiceConfiguration.getInstance());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getTagButtonActiveSeconds()}.
	 */
	@Test
	public final void testGetTagButtonActiveSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getTagButtonActiveSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getTagReaderSightingActiveSeconds()}.
	 */
	@Test
	public final void testGetTagReaderSightingActiveSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getTagReaderSightingActiveSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getTagSpotTagSightingActiveSeconds()}.
	 */
	@Test
	public final void testGetTagSpotTagSightingActiveSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getTagSpotTagSightingActiveSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getTagProximitySightingActiveSeconds()}.
	 */
	@Test
	public final void testGetTagProximitySightingActiveSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getTagProximitySightingActiveSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getStrengthAggregationWindowSeconds()}.
	 */
	@Test
	public final void testGetStrengthAggregationWindowSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getStrengthAggregationWindowSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getStrengthAggregationAgedSeconds()}.
	 */
	@Test
	public final void testGetStrengthAggregationAgedSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(5, configuration.getStrengthAggregationAgedSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getTagKeyList()}.
	 */
	@Test
	public final void testGetTagKeyList() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getTagKeyList());
		assertTrue(configuration.getTagKeyList().size() > 0);
		assertEquals(4, configuration.getTagKeyList().get(0).length);
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getConfigurationLoaded()}.
	 */
	@Test
	public final void testGetConfigurationLoaded() {
		assertTrue(configuration.getConfigurationLoaded());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#addReader(obt.spots.Reader)}.
	 */
	@Test
	public final void testAddReader() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(4, configuration.getReaders().size());
		assertNull(configuration.getReader("R-1"));
		
		Reader reader = new Reader(-1);
		configuration.addReader(reader);
		
		assertEquals(5, configuration.getReaders().size());
		assertNotNull(configuration.getReader("R-1"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReaderMap()}.
	 */
	@Test
	public final void testGetReaderMap() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(4, configuration.getReaderMap().size());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReaders()}.
	 */
	@Test
	public final void testGetReaders() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(4, configuration.getReaders().size());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReader(java.lang.String)}.
	 */
	@Test
	public final void testGetReader() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getReader("R1276"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReaderDistance(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetReaderDistance() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isValidReader(java.lang.String)}.
	 */
	@Test
	public final void testIsValidReader() {
		assertTrue(configuration.getConfigurationLoaded());
		assertTrue(configuration.isValidReader("R1276"));
		assertFalse(configuration.isValidReader("R1280"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#addSpot(obt.spots.Spot)}.
	 */
	@Test
	public final void testAddSpot() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotTagMap()}.
	 */
	@Test
	public final void testGetSpotTagMap() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotTags()}.
	 */
	@Test
	public final void testGetSpotTags() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpot(java.lang.String)}.
	 */
	@Test
	public final void testGetSpot() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isSpotTag(java.lang.String)}.
	 */
	@Test
	public final void testIsSpotTag() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isRegisterTag(java.lang.String)}.
	 */
	@Test
	public final void testIsRegisterTag() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isUnRegisterTag(java.lang.String)}.
	 */
	@Test
	public final void testIsUnRegisterTag() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotDistance(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetSpotDistance() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getMaxX()}.
	 */
	@Test
	public final void testGetMaxX() {
		assertTrue(configuration.getConfigurationLoaded());
		assertTrue(configuration.getMaxX() > 0);
		assertTrue(configuration.getMaxX() < 10000);
		
		Reader reader = new Reader(-1, "Test", true, 1, 1, 1, 10000, 10000);
		configuration.addReader(reader);
		
		assertEquals(10000, configuration.getMaxX());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getMaxY()}.
	 */
	@Test
	public final void testGetMaxY() {
		assertTrue(configuration.getConfigurationLoaded());
		assertTrue(configuration.getMaxY() > 0);
		assertTrue(configuration.getMaxY() < 10000);
		
		Reader reader = new Reader(-2, "Test2", true, 1, 1, 1, 10000, 10000);
		configuration.addReader(reader);
		
		assertEquals(10000, configuration.getMaxY());
	}

}
