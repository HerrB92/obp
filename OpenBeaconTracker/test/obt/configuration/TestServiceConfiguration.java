package obt.configuration;

import static org.junit.Assert.*;
import obt.configuration.ServiceConfiguration;
import obt.spots.Reader;
import obt.spots.RegisterTag;
import obt.spots.SpotTag;
import obt.spots.UnRegisterTag;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for service configuration
 * 
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
		assertEquals(2, configuration.getTagSpotTagSightingActiveSeconds());
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
		assertEquals(2, configuration.getStrengthAggregationWindowSeconds());
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getStrengthAggregationAgedSeconds()}.
	 */
	@Test
	public final void testGetStrengthAggregationAgedSeconds() {
		assertTrue(configuration.getConfigurationLoaded());
		assertEquals(4, configuration.getStrengthAggregationAgedSeconds());
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
		
		int count = configuration.getReaders().size();
		
		assertNull(configuration.getReader("R-1000"));
		
		Reader reader = new Reader(-1000);
		configuration.addReader(reader);
		
		assertEquals(count + 1, configuration.getReaders().size());
		assertNotNull(configuration.getReader("R-1000"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReaderMap()}.
	 */
	@Test
	public final void testGetReaderMap() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getReaderMap());
		assertTrue(configuration.getReaderMap().size() > 0);
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReaders()}.
	 */
	@Test
	public final void testGetReaders() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getReaders());
		assertTrue(configuration.getReaders().size() > 0);
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getReader(java.lang.String)}.
	 */
	@Test
	public final void testGetReader() {
		assertTrue(configuration.getConfigurationLoaded());
		
		Reader reader = new Reader(-1010);
		configuration.addReader(reader);
		
		assertNotNull(configuration.getReader("R-1010"));
	}

	/**
	 * Test method for 
	 * {@link obt.configuration.ServiceConfiguration#getReaderDistance(java.lang.String, java.lang.String)}.
	 * 
	 * Also tests, if distance calculation works.
	 */
	@Test
	public final void testGetReaderDistance() {
		Reader reader;
		reader = new Reader(-1020, "DistanceTest1", true, 1, 1, 1, 1000, 1000);
		configuration.addReader(reader);
		
		reader = new Reader(-1030, "DistanceTest2", true, 1, 1, 1, 2000, 2000);
		configuration.addReader(reader);
		
		assertEquals(1414, configuration.getReaderDistance("R-1020","R-1030"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isValidReader(java.lang.String)}.
	 */
	@Test
	public final void testIsValidReader() {
		assertTrue(configuration.getConfigurationLoaded());
		
		Reader reader;
		reader = new Reader(-1040, "ValidTest", true, 1, 1, 1, 1000, 1000);
		configuration.addReader(reader);
		
		assertTrue(configuration.isValidReader("R-1040"));
		assertFalse(configuration.isValidReader("R-9999"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#addSpot(obt.spots.Spot)}.
	 */
	@Test
	public final void testAddSpot() {
		assertTrue(configuration.getConfigurationLoaded());
		
		int spotCount = configuration.getSpotTags().size();
		
		SpotTag spotTag = new SpotTag(-3000);
		configuration.addSpot(spotTag);
		
		RegisterTag registerTag = new RegisterTag(-3001);
		configuration.addSpot(registerTag);
		
		UnRegisterTag unRegisterTag = new UnRegisterTag(-3002);
		configuration.addSpot(unRegisterTag);
		
		assertEquals(spotCount + 3, configuration.getSpotTags().size());
		assertTrue(configuration.isSpotTag("T-3000"));
		assertTrue(configuration.isSpotTag("T-3001"));
		assertTrue(configuration.isSpotTag("T-3002"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotTagMap()}.
	 */
	@Test
	public final void testGetSpotTagMap() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getSpotTagMap());
		assertTrue(configuration.getSpotTagMap().size() > 0);
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotTags()}.
	 */
	@Test
	public final void testGetSpotTags() {
		assertTrue(configuration.getConfigurationLoaded());
		assertNotNull(configuration.getSpotTags());
		assertTrue(configuration.getSpotTags().size() > 0);
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpot(java.lang.String)}.
	 */
	@Test
	public final void testGetSpot() {
		assertTrue(configuration.getConfigurationLoaded());
		
		SpotTag spotTag = new SpotTag(-3005);
		configuration.addSpot(spotTag);
		
		assertEquals(spotTag, configuration.getSpot("T-3005"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isSpotTag(java.lang.String)}.
	 */
	@Test
	public final void testIsSpotTag() {
		SpotTag spotTag = new SpotTag(-3010);
		configuration.addSpot(spotTag);
		
		assertTrue(configuration.isSpotTag("T-3010"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isRegisterTag(java.lang.String)}.
	 */
	@Test
	public final void testIsRegisterTag() {
		RegisterTag registerTag = new RegisterTag(-3015);
		configuration.addSpot(registerTag);
		
		assertTrue(configuration.isRegisterTag("T-3015"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#isUnRegisterTag(java.lang.String)}.
	 */
	@Test
	public final void testIsUnRegisterTag() {
		UnRegisterTag unRegisterTag = new UnRegisterTag(-3020);
		configuration.addSpot(unRegisterTag);
		
		assertTrue(configuration.isSpotTag("T-3020"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getSpotDistance(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetSpotDistance() {
		SpotTag spotTag;
		spotTag = new SpotTag(-3050, "DistanceTest1", true, 1, 1, 1, 1000, 1000);
		configuration.addSpot(spotTag);
		
		spotTag = new SpotTag(-3051, "DistanceTest2", true, 1, 1, 1, 2000, 2000);
		configuration.addSpot(spotTag);
		
		assertEquals(1414, configuration.getSpotDistance("T-3050","T-3051"));
	}

	/**
	 * Test method for {@link obt.configuration.ServiceConfiguration#getMaxX()}.
	 */
	@Test
	public final void testGetMaxX() {
		assertTrue(configuration.getConfigurationLoaded());
		assertTrue(configuration.getMaxX() > 0);
		assertTrue(configuration.getMaxX() < 10000);
		
		Reader reader = new Reader(-4000, "Test", true, 1, 1, 1, 10000, 10000);
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
		assertTrue(configuration.getMaxY() < 10005);
		
		Reader reader = new Reader(-4010, "Test2", true, 1, 1, 1, 10005, 10005);
		configuration.addReader(reader);
		
		assertEquals(10005, configuration.getMaxY());
	}
}
