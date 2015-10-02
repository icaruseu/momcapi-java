package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.resource.ExistResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomcaConnectionTest {

    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        assertNotNull(momcaConnection, "MomcaConnection connection not initialized.");
    }

    @AfterClass
    public void tearDown() throws Exception {
        momcaConnection.closeConnection();
    }

    @Test
    public void testAddCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        assertTrue(momcaConnection.writeCollection(name, path));
        assertTrue(momcaConnection.readCollection(uri).isPresent());

    }

    @Test
    public void testCreateCollectionPath() throws Exception {

        String path = "/db/path/to/collection";
        assertTrue(momcaConnection.createCollectionPath(path));
        momcaConnection.deleteCollection("/db/path");

        assertFalse(momcaConnection.createCollectionPath("not/absolute/path"));

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        momcaConnection.writeCollection(name, path);
        assertTrue(momcaConnection.deleteCollection(uri));
        assertFalse(momcaConnection.readCollection(uri).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("deleteTest.xml", "/db", "<empty/>");

        momcaConnection.writeExistResource(res);
        assertTrue(momcaConnection.deleteExistResource(res));
        assertFalse(momcaConnection.readExistResource(res.getResourceName(), res.getParentUri()).isPresent());

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db", "<empty/>");
        assertTrue(momcaConnection.writeExistResource(res));
        assertTrue(momcaConnection.readExistResource(res.getResourceName(), res.getParentUri()).isPresent());
        momcaConnection.deleteExistResource(res);
    }


}