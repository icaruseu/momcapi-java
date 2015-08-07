package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 22.07.2015.
 * =======
 * import org.testng.annotations.BeforeClass;
 * <p>
 * import static org.testng.Assert.assertNotNull;
 * <p>
 * /**
 * Created by djell on 07/08/2015.
 * >>>>>>> MAPI-7
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
    public void testGetArchive() throws Exception {

        IdArchive existingArchiveIdentifier = new IdArchive("CH-KAE");
        IdArchive nonExistingArchiveIdentifier = new IdArchive("CH-ABC");

        assertTrue(hierarchyManager.getArchive(existingArchiveIdentifier).isPresent());
        assertFalse(hierarchyManager.getArchive(nonExistingArchiveIdentifier).isPresent());

    }

}