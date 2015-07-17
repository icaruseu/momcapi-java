package eu.icarus.momca.momcapi;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class HierarchyManagerTest {

    private HierarchyManager hierarchyManager;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        hierarchyManager = momcaConnection.getHierarchyManager();
        assertNotNull(hierarchyManager, "MOM-CA connection not initialized.");
    }

    @Test
    public void testGetCountry() throws Exception {
        assertTrue(hierarchyManager.getCountry("DE").isPresent());
    }

    @Test
    public void testListCountries() throws Exception {
        assertEquals(hierarchyManager.listCountries().toString(), "[CH, DE, RS]");
    }


}