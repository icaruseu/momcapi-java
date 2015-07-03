package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ExistResource;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.XmldbURI;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xmldb.api.base.Collection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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

        Class<?> cl = momcaConnection.getClass();

        Method addCollection = cl.getDeclaredMethod("addCollection", String.class, String.class);
        addCollection.setAccessible(true);
        addCollection.invoke(momcaConnection, name, path);

        Method removeCollection = cl.getDeclaredMethod("deleteCollection", String.class);
        removeCollection.setAccessible(true);
        removeCollection.invoke(momcaConnection, path + "/" + name);

        assertFalse(momcaConnection.getCollection(path + "/" + name).isPresent());

    }

    @Test
    public void testDeleteCollection() throws Exception {

        XmldbURI uri = XmldbURI.create("/db/t|est");

        Field f = momcaConnection.getClass().getDeclaredField("rootCollection");
        f.setAccessible(true);
        Collection rootCollection = (Collection) f.get(momcaConnection);

        RemoteCollectionManagementService service = (RemoteCollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
        service.createCollection(uri);

        Class<?> cl = momcaConnection.getClass();
        Method deleteCollectionMethod = cl.getDeclaredMethod("deleteCollection", String.class);
        deleteCollectionMethod.setAccessible(true);
        deleteCollectionMethod.invoke(momcaConnection, uri.toASCIIString());

        Method getCollectionMethod = cl.getDeclaredMethod("getCollection", String.class);
        getCollectionMethod.setAccessible(true);

        //noinspection unchecked
        assertFalse(momcaConnection.getCollection(uri.toASCIIString()).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("deleteTest.xml", "/db", "<empty/>");
        momcaConnection.storeExistResource(res);
        momcaConnection.deleteExistResource(res);
        assertFalse(momcaConnection.getExistResource(res.getResourceName(), res.getParentUri()).isPresent());

    }

    @Test
    public void testQueryDatabase() throws Exception {

        Class<?> cl = momcaConnection.getClass();
        Method method = cl.getDeclaredMethod("queryDatabase", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> queryResults = (List<String>) method.invoke(momcaConnection, QUERY_FACTORY.queryUserModerator("user1.testuser@dev.monasterium.net"));
        assertEquals(queryResults.get(0), "admin");

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db", "<empty/>");
        momcaConnection.storeExistResource(res);
        assertTrue(momcaConnection.getExistResource(res.getResourceName(), res.getParentUri()).isPresent());
        momcaConnection.deleteExistResource(res);
    }


}