package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ExistResource;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomcaConnectionTest {

    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
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

        momcaConnection.addCollection(name, path);
        assertTrue(momcaConnection.getCollection(uri).isPresent());

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        momcaConnection.addCollection(name, path);
        momcaConnection.deleteCollection(uri);
        assertFalse(momcaConnection.getCollection(uri).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("deleteTest.xml", "/db", "<empty/>");
        momcaConnection.storeExistResource(res);
        momcaConnection.deleteExistResource(res);
        assertFalse(momcaConnection.getExistResource(res.getResourceName(), res.getParentUri()).isPresent());

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db", "<empty/>");
        momcaConnection.storeExistResource(res);
        assertTrue(momcaConnection.getExistResource(res.getResourceName(), res.getParentUri()).isPresent());
        momcaConnection.deleteExistResource(res);
    }


}