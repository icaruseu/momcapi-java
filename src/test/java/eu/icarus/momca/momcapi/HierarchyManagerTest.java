package eu.icarus.momca.momcapi;

import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertNotNull;

/**
 * Created by djell on 07/08/2015.
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

}